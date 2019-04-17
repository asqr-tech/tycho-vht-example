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

import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This interface represents a folder resource that contains media
 * resources such as audio and grammar file resources as well as
 * other folder resources that can contain similar items.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public interface IMediaContainer extends IMediaObjectContainer
{
	/**
	 * @return The list of <code>IMediaResource</code>s contained by this
	 * media folder resource
	 * @throws CoreException If an error occured while enumerating the media
	 * resources
	 */
	public List<IMediaResource> listMediaResources() throws CoreException;

	/**
	 * Locates the media folder with the given name.
	 *
	 * @param name The name of the desired media folder
	 * @return The desired media folder or <code>null</code> if not found
	 */
	public IMediaFolder getMediaFolder(String name);

	/**
	 * Locates the media file with the given name.
	 *
	 * @param name The name of the desired media file
	 * @return The desired media file or <code>null</code> if not found
	 */
	public IMediaFile getMediaFile(String name);

	/**
	 * Creates a new media file with the given name.  The created file will
	 * be initially empty (0 length).
	 *
	 * @param name The name of the new media file
	 * @return The newly created media file
	 * @throws CoreException If an error occured while creating the new media
	 * file
	 */
	public IMediaFile create(String name) throws CoreException;

	/**
	 * Creates a new media file with the given name.  It's initial contents
	 * will be retrieved from the provided input stream.  The progress
	 * monitor will be used to provide user feedback during the operation.
	 *
	 * @param name The name of the new media file
	 * @param source The initial contents of the file
	 * @param monitor A progress monitor for user feedback
	 * @return The newly created media file
	 * @throws CoreException If an error occured while creating the new
	 * media file
	 */
	public IMediaFile create(String name, InputStream source,
		IProgressMonitor monitor) throws CoreException;

	/**
	 * Creates a new media folder with the given name.
	 *
	 * @param name The name of the new media folder
	 * @return The newly created media folder
	 * @throws CoreException If an error occured while creating the media
	 * folder
	 */
	public IMediaFolder makeDirectory(String name) throws CoreException;
	
	public IFolder getUnderlyingFolder();
}
