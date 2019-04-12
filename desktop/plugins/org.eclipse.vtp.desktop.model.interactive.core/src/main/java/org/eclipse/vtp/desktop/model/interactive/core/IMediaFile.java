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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * This abstract interface represents a generic media file resource.
 * The creation of new media files is performed through the intended
 * parent media container. Currently there are two specializations
 * of this interface: <code>IAudioFile</code> and <code>IGrammarFile</code>.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public interface IMediaFile extends IMediaResource
{
	/**
	 * Opens this media file resource and returns an <code>InputStream</code>
	 * to it's contents.
	 *
	 * @return An input stream to the resource contents
	 * @throws CoreException If an error occured while opening the resource
	 */
	public InputStream open() throws CoreException;

	/**
	 * Returns the length of this media resource in number of bytes.
	 *
	 * @return The number of bytes contained in this resource
	 * @throws CoreException If an error occured while calculating the file
	 * length
	 */
	public long length() throws CoreException;
	
	public IFile getUnderlyingFile();
}
