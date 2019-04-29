package org.eclipse.vtp.desktop.projects.interactive.core.view;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObjectContainer;

/**
 * @author trip
 *
 */
public class MediaProjectLabelProvider implements ILabelProvider
{
	ILabelProvider parentlabelProvider = null;
	/**
	 * 
	 */
	public MediaProjectLabelProvider()
	{
		super();
		parentlabelProvider = WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		if(element instanceof IMediaObjectContainer)
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		return getInheritedImage(element);
	}
	
	private Image getInheritedImage(Object element)
	{
		Image ret = parentlabelProvider.getImage(element);
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
		else if(element instanceof IMediaObject)
			return ((IMediaObject)element).getName();
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
