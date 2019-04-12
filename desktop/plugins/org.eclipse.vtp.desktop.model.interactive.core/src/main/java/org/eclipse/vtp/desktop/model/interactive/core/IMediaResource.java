/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.interactive.core;

import org.eclipse.core.runtime.CoreException;


/**
 * This abstract interface represents a generic file or folder
 * media resource.  It provides functions that allow upwards
 * traversal of the resource tree and simple informational
 * retrieval.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public interface IMediaResource extends IMediaObject
{
	/**
	 * @return The parent media container of this media resource
	 */
	public IMediaContainer getParentMediaContainer();

	/**
	 * Determines if this media resource exists.
	 *
	 * @return <code>true</code> if the resource exists, <code>false</code>
	 * otherwise
	 */
	public boolean exists();

	/**
	 * Removes this resource from the project structure.  If this resource
	 * is a folder, all items under the resource will also be removed as
	 * part of this action.
	 *
	 * @throws CoreException If an error occured during the deletion
	 */
	public void delete() throws CoreException;

	/**
	 * @return A string representation of this media resource relative to
	 * the top level media files folder of this brand or persona project
	 */
	public String getMediaPath();
}
