package org.eclipse.vtp.desktop.model.interactive.core;

import org.eclipse.core.runtime.IAdaptable;

public interface IMediaObject extends IAdaptable
{
	/**
	 * @return The name of the resource
	 */
	public String getName();

	/**
	 * Returns the parent of this resource.  If this is a project
	 * resource this function returns <code>this</code>.
	 *
	 * @return The parent of this resource resource
	 */
	public IMediaObjectContainer getParent();

	/**
	 * Returns the top level project object that contains this resource.
	 *
	 * @return The containing project
	 */
	public IMediaProject getProject();

}
