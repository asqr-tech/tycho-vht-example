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
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This interface represents the file resource that contains the prompt
 * configuration of a persona project or an application brand.
 *
 * @author Trip
 * @version 2.0
 */
public interface IPromptSet extends IMediaObject
{
	/**
	 * Opens the prompt definition resource and returns an
	 * <code>InputStream</code> that will produce the byte contents
	 * of the resource.
	 *
	 * @return An input stream to the contents of the resource
	 * @throws CoreException If an error occurs while opening the resource
	 */
	public InputStream read() throws CoreException;

	/**
	 * Requests the contents of the prompt definition resource be
	 * replaced with the contents of the given input stream.
	 *
	 * @param source An input stream to the new contents of the prompt definition
	 * resource
	 * @param monitor The progress monitor to be used to provide user feedback
	 * during the storage operation
	 * @throws CoreException If and error occurs while storing the new
	 * prompt definition contents
	 */
	public void write(InputStream source, IProgressMonitor monitor)
		throws CoreException;
	
	/**
	 * @return
	 */
	public IFile getUnderlyingFile();
}
