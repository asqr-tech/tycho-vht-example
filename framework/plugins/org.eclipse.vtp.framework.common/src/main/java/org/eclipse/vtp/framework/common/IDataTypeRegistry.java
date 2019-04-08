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
package org.eclipse.vtp.framework.common;

/**
 * A service that manages dynamic data types available to the system.
 *
 * @author Lonnie Pryor
 */
public interface IDataTypeRegistry
{
	/**
	 * Returns a type definition for the type with the specified name or
	 * <code>null</code> if no type with that name exists.
	 * 
	 * @param typeName The name of the type definition to return.
	 * @return A type definition for the type with the specified name or
	 *         <code>null</code> if no type with that name exists.
	 * @throws NullPointerException If the supplied type name is <code>null</code>.
	 */
	IDataType getDataType(String typeName) throws NullPointerException;
}
