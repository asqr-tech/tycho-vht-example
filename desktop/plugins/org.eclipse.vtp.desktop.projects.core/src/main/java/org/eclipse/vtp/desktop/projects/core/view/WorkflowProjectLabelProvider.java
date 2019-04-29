/**
 * 
 */
package org.eclipse.vtp.desktop.projects.core.view;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.DecorationContext;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.vtp.desktop.model.core.IDependencySet;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.IWorkflowResourceContainer;

/**
 * @author trip
 *
 */
public class WorkflowProjectLabelProvider implements ILabelProvider
{
	ILabelProvider parentlabelProvider = null;
	/**
	 * 
	 */
	public WorkflowProjectLabelProvider()
	{
		super();
		parentlabelProvider = WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		Image ret = null;
		if(element instanceof IDependencySet)
			ret = org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry().get("ICON_LIBRARY");
		if(element instanceof IDesignDocument)
			ret = getInheritedImage(element);
		if(element instanceof IWorkflowResourceContainer)
			ret = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		if(ret != null)
		{
			ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager()
            .getLabelDecorator();
	        if (decorator != null)
	        {
	        	Object obj = element;
				IResource resource = (IResource)((IAdaptable)element).getAdapter(IResource.class);
				if(resource != null)
					obj = resource;
	        	if (decorator instanceof LabelDecorator) {
					LabelDecorator ld2 = (LabelDecorator) decorator;
		            Image decorated = ld2.decorateImage(ret, obj, DecorationContext.DEFAULT_CONTEXT);
		            if (decorated != null) {
		                return decorated;
		            }
				} else {
		            Image decorated = decorator.decorateImage(ret, obj);
		            if (decorated != null) {
		                return decorated;
		            }
				}
	        }
	        return ret;
		}
		return getInheritedImage(element);
	}
	
	private Image getInheritedImage(Object element)
	{
		Image ret = null;//parentlabelProvider.getImage(element);
		if(ret == null)
		{
			if(element instanceof IAdaptable)
			{
				IResource resource = (IResource)((IAdaptable)element).getAdapter(IResource.class);
				if(resource != null)
					ret = parentlabelProvider.getImage(resource);
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		if(element instanceof IProject)
			return ((IProject)element).getName();
		else if(element instanceof IWorkflowResource)
			return ((IWorkflowResource)element).getName();
		return parentlabelProvider.getText(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

}
