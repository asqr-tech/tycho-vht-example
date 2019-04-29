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

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * This interface represents a folder resource that contains database
 * table definitions.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public interface IDatabase extends IWorkflowResourceContainer
{
	/**
	 * @return The list of <code>IDatabaseTable</code>s that are defined for this
	 * database
	 */
	public List<IDatabaseTable> getTables();

	/**
	 * Creates a new database table definition with the given name.  The table
	 * will initially have no columns but all file and folder resources related
	 * to this new table will be created as part of this action.
	 *
	 * @param name The name of the new table definition
	 * @return The new database table
	 * @throws CoreException If an error occured while creating the required
	 * file or folder resources of this database table definition
	 */
	public IDatabaseTable createTable(String name) throws CoreException;
	
	/**
	 * @throws CoreException
	 */
	public void delete() throws CoreException;
}
