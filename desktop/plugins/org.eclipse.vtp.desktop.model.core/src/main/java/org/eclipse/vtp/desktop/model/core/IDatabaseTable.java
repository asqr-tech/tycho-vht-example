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
package org.eclipse.vtp.desktop.model.core;

import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * This interface represents a database table definition file resource.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public interface IDatabaseTable extends IWorkflowResource
{
	/**
	 * @return The name of this database table
	 */
	public String getName();

	/**
	 * @return The list of <code>IDatabaseTableColumn</code>s defined for
	 * this database table
	 */
	public List<IDatabaseTableColumn> getColumns();
	
	/**
	 * @return
	 */
	public IFile getUnderlyingFile();

	public void write(InputStream source) throws CoreException;

	/**
	 * @throws CoreException
	 */
	public void delete() throws CoreException;
}
