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
 * Describes a type of dynamic data object.
 * 
 * @author Lonnie Pryor
 */
public interface IDataType
{
	/**
	 * Returns the unique name of this type.
	 * 
	 * @return The unique name of this type.
	 */
	String getName();

	/**
	 * Returns the names of the fields that objects of this type support.
	 * 
	 * @return The names of the fields that objects of this type support.
	 */
	String[] getFieldNames();

	/**
	 * Returns the name of the field that objects of this type treat as their
	 * primary value or <code>null</code> if no such field is defined.
	 * 
	 * @return The name of the field that objects of this type treat as their
	 *         primary value if no such field is defined.
	 */
	String getPrimaryFieldName();

	/**
	 * Returns the type of the specified field or <code>null</code> if no such
	 * field exists.
	 * 
	 * @param fieldName The name of the field to determine the type of.
	 * @return The type of the specified field or <code>null</code> if no such
	 *         field exists.
	 */
	IDataType getFieldType(String fieldName);

	/**
	 * Returns the initial value for new instances of the specified field.
	 * 
	 * @param fieldName The name of the field to determine the initial value of.
	 * @return The initial value for new instances of the specified field.
	 */
	String getFieldInitialValue(String fieldName);
	
	boolean isFieldSecured(String fieldName);

	/**
	 * Returns true if this type represents an array type.
	 * 
	 * @return True if this type represents an array type.
	 */
	boolean isArrayType();

	/**
	 * Returns true if this type represents a type with named fields.
	 * 
	 * @return True if this type represents a type with named fields.
	 */
	boolean isComplexType();

	/**
	 * Returns true if this type represents a boolean, date, decimal, number, or
	 * string type.
	 * 
	 * @return True if this type represents a boolean, date, decimal, number, or
	 *         string type.
	 */
	boolean isSimpleType();
	
	boolean isExternalType();
	
	IExternalDataType getExternalFactory();
}
