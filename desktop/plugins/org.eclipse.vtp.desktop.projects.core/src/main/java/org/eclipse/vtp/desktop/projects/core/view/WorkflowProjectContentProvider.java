/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.projects.core.view;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IPipelinedTreeContentProvider;
import org.eclipse.ui.navigator.PipelinedShapeModification;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.IWorkflowResourceContainer;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;

/**
 * @author trip
 *
 */
public class WorkflowProjectContentProvider implements IPipelinedTreeContentProvider, IDoubleClickListener
{
	private Viewer viewer = null;

	public WorkflowProjectContentProvider()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		if(parentElement instanceof IProject)
		{
			IWorkflowProject workflowProject = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowProject((IProject)parentElement);
			return workflowProject.getChildren().toArray();
		}
		else if(parentElement instanceof IWorkflowResourceContainer)
			return ((IWorkflowResourceContainer)parentElement).getChildren().toArray();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		if(element instanceof IProject)
			return ((IProject)element).getWorkspace().getRoot();
		else if(element instanceof IWorkflowResource)
		{
			IWorkflowResource con = ((IWorkflowResource)element).getParent();
			if(con instanceof IWorkflowProject)
				return con.getAdapter(IResource.class);
			return con;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		if(element instanceof IProject)
			return true;
		if(element instanceof IDesignDocument)
			return false;
		if(element instanceof IWorkflowResourceContainer)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		if(inputElement instanceof IWorkspaceRoot)
			return ((IWorkspaceRoot)inputElement).getProjects();
		return getChildren(inputElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if(this.viewer != null)
			((CommonViewer)this.viewer).removeDoubleClickListener(this);
		this.viewer = viewer;
		if(viewer != null)
			((CommonViewer)this.viewer).addDoubleClickListener(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getPipelinedChildren(Object aParent, Set theCurrentChildren)
	{
		List<Object> newChildren = new LinkedList<Object>();
		if(aParent instanceof IProject)
		{
			IProject project = (IProject)aParent;
			try
			{
				if(WorkflowCore.getDefault().getWorkflowModel().isWorkflowProject(project))
				{
					IWorkflowProject workflowProject = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowProject(project);
					List<IWorkflowResource> workflowResources = workflowProject.getChildren();
					Iterator<?> iterator = theCurrentChildren.iterator();
					while(iterator.hasNext())
					{
						Object child = iterator.next();
						if(child instanceof IResource)
						{
							IResource resource = (IResource)child;
							for(IWorkflowResource workflowResource : workflowResources)
							{
								if(workflowResource.getName().equals(resource.getName()))
								{
									iterator.remove();
									newChildren.add(workflowResource);
									break;
								}
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		theCurrentChildren.addAll(newChildren);
	}

	@SuppressWarnings("rawtypes")
	public void getPipelinedElements(Object anInput, Set theCurrentElements)
	{
		getPipelinedChildren(anInput, theCurrentElements);
	}

	public Object getPipelinedParent(Object anObject, Object aSuggestedParent)
	{
		if(aSuggestedParent instanceof IResource)
		{
			IResource resource = (IResource)aSuggestedParent;
			IWorkflowResource workflowResource = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(resource);
			if(workflowResource != null)
			{
				if(!(workflowResource instanceof IWorkflowProject))
				{
					return workflowResource;
				}
			}
		}
		return aSuggestedParent;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PipelinedShapeModification interceptAdd(
		final PipelinedShapeModification anAddModification)
	{
		Object parentObj = anAddModification.getParent();
		if(parentObj instanceof IResource)
		{
			IResource parent = (IResource)parentObj;
			IWorkflowResource workflowParent = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(parent);
			if(workflowParent != null && !(workflowParent instanceof IWorkflowProject))
			{
				anAddModification.setParent(workflowParent);
			}
			Set children = anAddModification.getChildren();
			List newChildren = new LinkedList();
			Iterator iterator = children.iterator();
			while(iterator.hasNext())
			{
				Object childObj = iterator.next();
				if(childObj instanceof IResource)
				{
					IResource childResource = (IResource)childObj;
					IWorkflowResource workflowChild = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(childResource);
					if(workflowChild != null)
					{
						iterator.remove();
						newChildren.add(workflowChild);
					}
				}
			}
			children.addAll(newChildren);
		}
		return anAddModification;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean interceptRefresh(
		PipelinedViewerUpdate aRefreshSynchronization)
	{
		boolean changed = false;
		Set children = aRefreshSynchronization.getRefreshTargets();
		List newTargets = new LinkedList();
		Iterator iterator = children.iterator();
		while(iterator.hasNext())
		{
			Object childObj = iterator.next();
			if(childObj instanceof IResource)
			{
				IResource childResource = (IResource)childObj;
				IWorkflowResource workflowChild = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(childResource);
				if(workflowChild != null)
				{
					iterator.remove();
					newTargets.add(workflowChild);
					changed = true;
				}
			}
		}
		children.addAll(newTargets);
		return changed;
	}

	@SuppressWarnings({ "rawtypes" })
	public PipelinedShapeModification interceptRemove(
		PipelinedShapeModification aRemoveModification)
	{
		Set children = aRemoveModification.getChildren();
		final List<IWorkflowResource> parents = new LinkedList<IWorkflowResource>();
		Iterator iterator = children.iterator();
		while(iterator.hasNext())
		{
			Object childObj = iterator.next();
			if(childObj instanceof IResource)
			{
				IResource childResource = (IResource)childObj;
				IWorkflowResource workflowParent = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(childResource.getParent());
				if(workflowParent != null)
				{
					parents.add(workflowParent);
				}
			}
		}
		viewer.getControl().getDisplay().asyncExec(new Runnable(){
			public void run()
			{
				for(IWorkflowResource parent : parents)
				{
					((TreeViewer)viewer).refresh(parent);
				}
			}
		});
		return aRemoveModification;
	}

	public boolean interceptUpdate(PipelinedViewerUpdate anUpdateSynchronization)
	{
		return false;
	}

	public void init(ICommonContentExtensionSite aConfig)
	{
	}

	public void restoreState(IMemento aMemento)
	{
	}

	public void saveState(IMemento aMemento)
	{
	}

	public void doubleClick(DoubleClickEvent event)
	{
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		Object sel = selection.getFirstElement();
		if(sel instanceof IAdaptable)
		{
			IResource resource = (IResource)((IAdaptable)sel).getAdapter(IResource.class);
			if(resource instanceof IFile)
			{
				IFile underlyingFile = (IFile)resource;
				if(underlyingFile != null)
				{
					try
					{
						IDE.openEditor(PlatformUI.getWorkbench()
							  .getActiveWorkbenchWindow()
							  .getActivePage(), underlyingFile);
					}
					catch (PartInitException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

}
