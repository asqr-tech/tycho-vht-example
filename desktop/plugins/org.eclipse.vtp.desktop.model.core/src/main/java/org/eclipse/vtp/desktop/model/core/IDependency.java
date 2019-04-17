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

import org.eclipse.core.runtime.CoreException;

/**
 * This interface represents the file resource of an application project
 * that is a custom dependency of a project.
 *
 * @author Trip Gilman
 * @version 1.0
 * @since 2.1
 */
public interface IDependency extends IWorkflowResource
{
	/**
	 * @return The parent dependency set of this dependency
	 */
	public IDependencySet getParent();

	/**
	 * Determines if this dependency exists.
	 *
	 * @return <code>true</code> if the dependency exists, <code>false</code>
	 * otherwise
	 */
	public boolean exists();

	/**
	 * Removes this dependency from the project structure.
	 *
	 * @throws CoreException If an error occured during the deletion
	 */
	public void delete() throws CoreException;

}
