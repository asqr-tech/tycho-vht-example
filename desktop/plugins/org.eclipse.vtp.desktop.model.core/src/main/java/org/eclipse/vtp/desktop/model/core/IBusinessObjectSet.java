/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007, 2009 OpenMethods, LLC
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
 * This interface represents the folder resource that contains the business
 * objects defined in an application project.
 *
 * @author Trip Gilman
 * @version 2.0
 * @since 3.0
 */
public interface IBusinessObjectSet extends IWorkflowResourceContainer
{
	public IBusinessObject getBusinessObject(String typeName);
	
	/**
	 * @return The list of <code>IBusinessObject</code>s used by the application
	 * project
	 */
	public List<IBusinessObject> getBusinessObjects();

	/**
	 * Creates a new business object with the given name.  The business object
	 * will initially have no fields defined but all file and folder resources
	 * will be created as part of this action.
	 *
	 * @param name The name of the new business object
	 * @return The created business object
	 * @throws CoreException If an error occured during the creation of the
	 * required file and folder resources for the new business object
	 */
	public IBusinessObject createBusinessObject(String name)
		throws CoreException;

	/**
	 * Removes the given business object from the application project.  All
	 * business object related file and folder resources will automatically
	 * be removed as part of this action.
	 *
	 * @param businessObject The business object to remove
	 */
	public void deleteBusinessObject(IBusinessObject businessObject) throws CoreException;
	
}
