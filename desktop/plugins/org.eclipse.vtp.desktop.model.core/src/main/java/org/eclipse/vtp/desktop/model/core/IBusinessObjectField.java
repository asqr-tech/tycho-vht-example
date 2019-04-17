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


/**
 * This interface represents a definition of a business object field.
 * This includes the field name, data type definition, and initial
 * value if any.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public interface IBusinessObjectField
{
	/**
	 * @return The name of this business object field
	 */
	public String getName();

	/**
	 * @return The data type definition of this business object field
	 */
	public FieldType getDataType();

	/**
	 * @return The initial value this field will have when a new instance
	 * of it's containing business object is created
	 */
	public String getInitialValue();
	
	/**
	 * @return
	 */
	public boolean isSecured();
}
