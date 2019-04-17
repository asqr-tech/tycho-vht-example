/**
 * 
 */
package org.eclipse.vtp.desktop.model.core.builders;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IWebserviceDescriptor;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowReference;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.internal.WorkflowIndex;
import org.eclipse.vtp.desktop.model.core.internal.WorkflowIndexService;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.desktop.model.core.markers.WorkflowMarkerConstants;
import org.eclipse.vtp.desktop.model.core.schema.SchemaProblem;
import org.eclipse.vtp.desktop.model.core.wsdl.WSDL;
import org.eclipse.vtp.desktop.model.core.wsdl.WSDLProblem;

/**
 * @author trip
 *
 */
public class WorkflowProjectBuilder extends IncrementalProjectBuilder
{
	/**
	 * Constant string containing the builder id
	 */
	public static final String BUILDER_ID =
		"org.eclipse.vtp.desktop.model.core.WorkflowProjectBuilder";
	
	private WorkflowIndex index = null;
	private List<IDesignDocument> modifiedByBuilder = new LinkedList<IDesignDocument>();

	/**
	 * 
	 */
	public WorkflowProjectBuilder()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
	throws CoreException
	{
		modifiedByBuilder.clear();
		System.err.println("building: " + getProject().getName() + " this: " + this);
		//index|build of project
		boolean isFirstTime = !WorkflowIndexService.getInstance().isIndexed(getProject());
		if(!getProject().isSynchronized(IResource.DEPTH_INFINITE))
		{
			System.err.println("Resource out of synch with filesystem, refreshing");
			if(!isFirstTime)
			{
				index = WorkflowIndexService.getInstance().getIndex(getProject());
				index.cleanProject();
			}
			getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			this.needRebuild();
			return getProject().getReferencedProjects();
		}
		System.err.println("Unindexed: " + isFirstTime);
		index = WorkflowIndexService.getInstance().getIndex(getProject());
		index.getWriteLock().lock();
		try
		{
			if(!isFirstTime)
			{
				IResourceDelta delta = getDelta(getProject());
				if(delta == null && kind != CLEAN_BUILD)
				{
					fullBuild(monitor);
				}
				else
				{
					ChangeCounter counter = new ChangeCounter();
					delta.accept(counter);
					System.err.println("delta: " + delta + " changes: " + counter.affected);
					if(counter.affected > 0)
						incrementalBuild(delta, monitor);
					index.indexExportedData();
				}
				if(modifiedByBuilder.size() > 0)
				{
					this.needRebuild();
					return getProject().getReferencedProjects();
				}
			}
		}
		finally
		{
			index.getWriteLock().unlock();
		}
		//update to project references
		IProjectDescription desc = getProject().getDescription();
		IProject[] oldReferences = desc.getDynamicReferences();
		IProject[] allProjects = getProject().getWorkspace().getRoot().getProjects();
		List<IProject> refProjects = new LinkedList<IProject>();
		List<IWorkflowReference> newRefs = index.getWorkflowReferences();
		for(IWorkflowReference ref : newRefs)
		{
			for(IProject p : allProjects)
			{
				IWorkflowProject wp = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowProject(p);
				if(wp != null && wp.getId().equals(ref.getTargetId()))
				{
					if(!refProjects.contains(p))
						refProjects.add(p);
					break;
				}
			}
		}
		boolean mismatch = false;
		for(IProject ref : refProjects)
		{
			boolean found = false;
			for(IProject old : oldReferences)
			{
				if(ref.equals(old))
				{
					found = true;
					break;
				}
			}
			if(!found)
			{
				mismatch = true;
				break;
			}
		}
		if(!mismatch)
			for(IProject old : oldReferences)
			{
				if(!refProjects.contains(old))
				{
					mismatch = true;
					break;
				}
			}
		if(mismatch)
		{
			desc.setDynamicReferences(refProjects.toArray(new IProject[refProjects.size()]));
			getProject().setDescription(desc, monitor);
		}
		if(!index.isValidated())
		{
			validate(monitor);
		}
		return getProject().getReferencedProjects();
	}
	
	private void validate(final IProgressMonitor monitor)
	{
		try
		{
			getProject().deleteMarkers(WorkflowMarkerConstants.WORKFLOW_MARKER, true, IResource.DEPTH_INFINITE);
			index.setValidated(true);
		}
		catch(CoreException e)
		{
		}
	}
	
	/**
	 * Performs all tasks required by a full build of the application project.
	 *
	 * @param monitor The progress monitor used to provide user feedback
	 * @throws CoreException If the build encounters an error during execution
	 */
	protected void fullBuild(final IProgressMonitor monitor)
		throws CoreException
	{
		System.err.println("##########doing full build****************************");
		index.cleanProject();
		index.fullIndex();
	}
	
	/**
	 * Performs any build tasks required by the resource delta of the application project.
	 *
	 * @param delta The changes to the application project
	 * @param monitor The progress monitor used to provide user feedback
	 * @throws CoreException If the build encounters an error during execution
	 */
	protected void incrementalBuild(IResourceDelta delta,
		IProgressMonitor monitor) throws CoreException
	{
		RemovalDeltaVisitor removalVisitor = new RemovalDeltaVisitor();
		delta.accept(removalVisitor);
		AddedDeltaVisitor addedVisitor = new AddedDeltaVisitor();
		delta.accept(addedVisitor);
		ChangedDeltaVisitor changedVisitor = new ChangedDeltaVisitor();
		delta.accept(changedVisitor);
		if(modifiedByBuilder.size() > 0)
		{
			Display.getDefault().asyncExec(new Runnable(){
				public void run()
				{
					for(IDesignDocument document : modifiedByBuilder)
					{
						try
						{
							document.commitWorkingCopy();
						}
						catch(Exception ex)
						{
							IStatus status = new Status(IStatus.ERROR,
								WorkflowCore.PLUGIN_ID, 0,
							      "Error during build: updating a copied or moved document. " + document.getUnderlyingFile().getFullPath(), ex);
							WorkflowCore.getDefault().getLog().log(status);
						}
					}
				}
			});
		}
	}
	
	/**
	 * This delta visitor is currently a NOOP.  Any resource delta analysis
	 * needed by future incarnations of this builder will be performed here.
	 */
	private class RemovalDeltaVisitor implements IResourceDeltaVisitor
	{
		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			if(resource instanceof IFile && delta.getKind() == IResourceDelta.REMOVED)
			{
				System.out.println("Removal Vistor: " + resource);
				System.out.println(delta);
				index.remove(resource.getProjectRelativePath().toString());
//				resource.deleteMarkers(WorkflowMarkerConstants.WORKFLOW_MARKER, true, IResource.DEPTH_INFINITE);
				return false;
			}
			return true;
		}
	}
	
	/**
	 * This delta visitor is currently a NOOP.  Any resource delta analysis
	 * needed by future incarnations of this builder will be performed here.
	 */
	private class ChangedDeltaVisitor implements IResourceDeltaVisitor
	{
		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			if(resource instanceof IFile && delta.getKind() == IResourceDelta.CHANGED)
			{
				IWorkflowResource workflowResource = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(resource);
				if(workflowResource instanceof IDesignDocument)
				{
					System.out.println("Changed Vistor: " + resource);
					System.out.println(delta);
					IDesignDocument designDocument = (IDesignDocument)workflowResource;
					index.clean(designDocument);
					index.indexStructure(designDocument);
				}
				else if(workflowResource instanceof IWebserviceDescriptor)
				{
					IWebserviceDescriptor descriptor = (IWebserviceDescriptor)workflowResource;
					resource.deleteMarkers(WorkflowMarkerConstants.WORKFLOW_MARKER, true, IResource.DEPTH_INFINITE);
					try
					{
						WSDL wsdl = descriptor.getWSDL();
						List<SchemaProblem> schemaProblems = wsdl.getSchemaProblems();
						List<WSDLProblem> wsdlProblems = wsdl.getWSDLProblems();
						for(SchemaProblem problem : schemaProblems)
						{
							IMarker marker = resource.createMarker(WorkflowMarkerConstants.WORKFLOW_MARKER);
							marker.setAttributes(new String[] {IMarker.MESSAGE, IMarker.LINE_NUMBER, IMarker.LOCATION, IMarker.SEVERITY}, new Object[] {problem.getMessage(), problem.getLineNumber(), "Line " + problem.getLineNumber(), IMarker.SEVERITY_ERROR});
						}
						for(WSDLProblem problem : wsdlProblems)
						{
							IMarker marker = resource.createMarker(WorkflowMarkerConstants.WORKFLOW_MARKER);
							marker.setAttributes(new String[] {IMarker.MESSAGE, IMarker.LINE_NUMBER, IMarker.LOCATION, IMarker.SEVERITY}, new Object[] {problem.getMessage(), problem.getLineNumber(), "Line " + problem.getLineNumber(), IMarker.SEVERITY_ERROR});
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			return true;
		}
	}
	
	/**
	 * This delta visitor is currently a NOOP.  Any resource delta analysis
	 * needed by future incarnations of this builder will be performed here.
	 */
	private class AddedDeltaVisitor implements IResourceDeltaVisitor
	{
		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			if(delta.getKind() == IResourceDelta.ADDED)
			{
				System.out.println("Added Vistor: " + resource);
				System.out.println(delta);
				IWorkflowResource workflowResource = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(resource);
				if(workflowResource instanceof IDesignDocument)
				{
					final IDesignDocument designDocument = (IDesignDocument)workflowResource;
					if(delta.getFlags() != IResourceDelta.MOVED_FROM)
					{
						System.out.println("not a move operation");
						designDocument.becomeWorkingCopy();
						List<IDesignElement> mainElements = designDocument.getMainDesign().getDesignElements();
						for(IDesignElement element : mainElements)
						{
							if(index.elementExists(designDocument, element.getId()))
							{
								System.out.println("duplicate id detected");
								//there is an id collision that must be resolved
								((Design)designDocument.getMainDesign()).forceNewIds();
								modifiedByBuilder.add(designDocument);
							}
						}
						designDocument.discardWorkingCopy();
					}
					index.clean(designDocument);
					index.indexStructure(designDocument);
				}
				else if(workflowResource instanceof IWebserviceDescriptor)
				{
					IWebserviceDescriptor descriptor = (IWebserviceDescriptor)workflowResource;
					resource.deleteMarkers(WorkflowMarkerConstants.WORKFLOW_MARKER, true, IResource.DEPTH_INFINITE);
					try
					{
						WSDL wsdl = descriptor.getWSDL();
						List<SchemaProblem> schemaProblems = wsdl.getSchemaProblems();
						List<WSDLProblem> wsdlProblems = wsdl.getWSDLProblems();
						for(SchemaProblem problem : schemaProblems)
						{
							IMarker marker = resource.createMarker(WorkflowMarkerConstants.WORKFLOW_MARKER);
							marker.setAttributes(new String[] {IMarker.MESSAGE, IMarker.LINE_NUMBER, IMarker.LOCATION, IMarker.SEVERITY}, new Object[] {problem.getMessage(), problem.getLineNumber(), "Line " + problem.getLineNumber(), IMarker.SEVERITY_ERROR});
						}
						for(WSDLProblem problem : wsdlProblems)
						{
							IMarker marker = resource.createMarker(WorkflowMarkerConstants.WORKFLOW_MARKER);
							marker.setAttributes(new String[] {IMarker.MESSAGE, IMarker.LINE_NUMBER, IMarker.LOCATION, IMarker.SEVERITY}, new Object[] {problem.getMessage(), problem.getLineNumber(), "Line " + problem.getLineNumber(), IMarker.SEVERITY_ERROR});
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			return true;
		}
	}
	
	private class ChangeCounter implements IResourceDeltaVisitor
	{
		int affected = 0;
		
		public boolean visit(IResourceDelta delta)
		{
			IResource resource = delta.getResource();
			if(resource instanceof IProject)
				return true;
			IWorkflowResource workflowResource = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(resource);
			if(workflowResource instanceof IDesignItemContainer)
				return true;
			if(workflowResource instanceof IDesignDocument)
			{
				++affected;
			}
			return false;
		}
	}
	
}
