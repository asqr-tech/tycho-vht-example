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

import org.eclipse.core.resources.IFolder;

/**
 * This interface represents the folder resource of an application project
 * that contains the custom dependencies of a project.
 *
 * @author Lonnie Pryor
 * @version 1.0
 * @since 2.1
 */
public interface IDependencySet extends IWorkflowResourceContainer
{
	/**
	 * Returns the dependencies in this set.
	 *
	 * @return The dependencies in this set.
	 */
	List<IDependency> getDependencies();
	
	/**
	 * Creates a new dependency in the application.
	 *
	 * @param name The name to give the dependency resource.
	 * @param content The content of the dependency resource.
	 * @return The new dependency instance.
	 */
	IDependency createDependency(String name, InputStream content);
	
	/**
	 * Removes a dependency from the application.
	 *
	 * @param dependency The dependency to remove.
	 */
	void removeDependency(IDependency dependency);
	
	public IFolder getUnderlyingFolder();
}
