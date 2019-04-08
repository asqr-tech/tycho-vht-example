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
package org.eclipse.vtp.framework.core;

/**
 * A service available to all services at any scope.
 * 
 * @author Lonnie Pryor
 */
public interface IProcessContext extends IContext
{
	/**
	 * Returns the ID of this process.
	 * 
	 * @return The ID of this process.
	 */
	String getProcessID();

	/**
	 * Returns the value of the process-level configuration property with the
	 * specified name or <code>null</code> if no such property exists.
	 * 
	 * @param propertyName The name of the configuration property to return.
	 * @return The value of the process-level configuration property with the
	 *         specified name or <code>null</code> if no such property exists.
	 * @throws NullPointerException If the supplied property name is
	 *           <code>null</code>.
	 */
	Object getProperty(String propertyName) throws NullPointerException;

	/**
	 * Loads a class visible to the process.
	 * 
	 * @param className The name of the class to load.
	 * @return The requested class instance.
	 * @throws ClassNotFoundException If a class with the specified name cannot be
	 *           found.
	 * @throws NullPointerException If the supplied class name is
	 *           <code>null</code>.
	 */
	@SuppressWarnings("rawtypes")
	Class loadClass(String className) throws ClassNotFoundException,
			NullPointerException;
}
