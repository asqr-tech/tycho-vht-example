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
package org.eclipse.vtp.desktop.projects.interactive.core.view;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
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
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFile;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObjectContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;
import org.eclipse.vtp.desktop.model.interactive.core.IPromptSet;
import org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowCore;

/**
 * @author trip
 *
 */
public class MediaProjectContentProvider implements IPipelinedTreeContentProvider, IDoubleClickListener
{
	private Viewer viewer = null;

	public MediaProjectContentProvider()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		if(parentElement instanceof IProject)
		{
			IMediaProject workflowProject = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().convertToMediaProject((IProject)parentElement);
			return workflowProject.getChildren().toArray();
		}
		else if(parentElement instanceof IMediaObjectContainer)
			return ((IMediaObjectContainer)parentElement).getChildren().toArray();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		if(element instanceof IProject)
			return ((IProject)element).getWorkspace().getRoot();
		else if(element instanceof IMediaObject)
		{
			IMediaObject con = ((IMediaObject)element).getParent();
			if(con instanceof IMediaProject)
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
		if(element instanceof IMediaObjectContainer)
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getPipelinedChildren(Object aParent, Set theCurrentChildren)
	{
		List newChildren = new LinkedList();
		if(aParent instanceof IProject)
		{
			IProject project = (IProject)aParent;
			try
			{
				if(InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().isMediaProject(project))
				{
					IMediaProject workflowProject = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().convertToMediaProject(project);
					List<IMediaObject> workflowResources = workflowProject.getChildren();
					Iterator iterator = theCurrentChildren.iterator();
					while(iterator.hasNext())
					{
						Object child = iterator.next();
						if(child instanceof IResource)
						{
							IResource resource = (IResource)child;
							for(IMediaObject workflowResource : workflowResources)
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

	@SuppressWarnings({ "rawtypes" })
	public void getPipelinedElements(Object anInput, Set theCurrentElements)
	{
		getPipelinedChildren(anInput, theCurrentElements);
	}

	public Object getPipelinedParent(Object anObject, Object aSuggestedParent)
	{
		if(aSuggestedParent instanceof IResource)
		{
			IResource resource = (IResource)aSuggestedParent;
			IMediaObject workflowResource = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().convertToMediaObject(resource);
			if(workflowResource != null)
			{
				if(!(workflowResource instanceof IMediaProject))
				{
					return workflowResource;
				}
			}
		}
		return aSuggestedParent;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PipelinedShapeModification interceptAdd(
		final PipelinedShapeModification anAddModification)
	{
		Object parentObj = anAddModification.getParent();
		if(parentObj instanceof IResource)
		{
			IResource parent = (IResource)parentObj;
			IMediaObject workflowParent = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().convertToMediaObject(parent);
			if(workflowParent != null && !(workflowParent instanceof IMediaProject))
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
					IMediaObject workflowChild = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().convertToMediaObject(childResource);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
				IMediaObject workflowChild = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().convertToMediaObject(childResource);
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
		final List<IMediaObject> parents = new LinkedList<IMediaObject>();
		Iterator iterator = children.iterator();
		while(iterator.hasNext())
		{
			Object childObj = iterator.next();
			if(childObj instanceof IResource)
			{
				IResource childResource = (IResource)childObj;
				IMediaObject workflowParent = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().convertToMediaObject(childResource.getParent());
				if(workflowParent != null)
				{
					parents.add(workflowParent);
				}
			}
		}
		viewer.getControl().getDisplay().asyncExec(new Runnable(){
			public void run()
			{
				for(IMediaObject parent : parents)
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
		if(sel instanceof IMediaFile)
		{
			try
			{
				IDE.openEditor(PlatformUI.getWorkbench()
					  .getActiveWorkbenchWindow()
					  .getActivePage(), ((IMediaFile)sel).getUnderlyingFile());
			}
			catch (PartInitException e)
			{
				e.printStackTrace();
			}
		}
		else if(sel instanceof IPromptSet)
		{
			try
			{
				IDE.openEditor(PlatformUI.getWorkbench()
					  .getActiveWorkbenchWindow()
					  .getActivePage(), ((IPromptSet)sel).getUnderlyingFile());
			}
			catch (PartInitException e)
			{
				e.printStackTrace();
			}
		}
	}

}
