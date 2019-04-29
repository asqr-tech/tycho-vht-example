package org.eclipse.vtp.desktop.model.core.internal;

import org.eclipse.core.resources.IFolder;
import org.eclipse.vtp.desktop.model.core.IDesignRootFolder;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;

/**
 * @author trip
 */
public class DesignRootFolder extends DesignItemContainer implements IDesignRootFolder
{
	private WorkflowProject project = null;

	/**
	 * 
	 */
	public DesignRootFolder(WorkflowProject project, IFolder folder)
	{
		super(folder);
		this.project = project;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowResource#getParent()
	 */
	public IWorkflowResource getParent()
	{
		return project;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass)
	{
		if(IDesignRootFolder.class.isAssignableFrom(adapterClass))
			return this;
		return super.getAdapter(adapterClass);
	}
}
