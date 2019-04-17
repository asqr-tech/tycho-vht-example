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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * This interface represents a file rosource that contains the definition
 * of a business object used in the application.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public interface IBusinessObject extends IWorkflowResource
{
	/**
	 * @return The unique identifier for this business object definition
	 */
	public String getId();

	/**
	 * @return A list of <code>IBusinessObjectField</code>s that define the
	 * names and data types of the business object's fields
	 */
	public List<IBusinessObjectField> getFields();

	/**
	 * @return The parent folder resource of this business object definition
	 */
	public IBusinessObjectSet getBusinessObjectSet();
	
	/**
	 * @return
	 */
	public IFile getUnderlyingFile();

	
	/**
	 * @throws CoreException
	 */
	public void delete() throws CoreException;
}
