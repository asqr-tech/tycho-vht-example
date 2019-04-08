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
 * Represents a dynamic piece of data at runtime.
 * 
 * @author Lonnie Pryor
 */
public interface IDataObject extends IScriptable
{
	String getId();
	
	/**
	 * Returns the type of this data object.
	 * 
	 * @return The type of this data object.
	 */
	IDataType getType();

	/**
	 * Returns true if this data object is immutable.
	 * 
	 * @return True if this data object is immutable.
	 */
	boolean isReadOnly();

	/**
	 * Returns the value of the specified field.
	 * 
	 * @param fieldName The name of the field to return the value of.
	 * @return The value of the specified field or <code>null</code> if this
	 *         object does not have a field with the specified name.
	 */
	IDataObject getField(String fieldName);

	/**
	 * Sets the value of the specified field.
	 * 
	 * @param fieldName The name of the field to set the value of.
	 * @param variable The value to set the field to.
	 * @return True if the specified field exists and its value was changed.
	 */
	boolean setField(String fieldName, IDataObject variable);

	/**
	 * Returns true if the specified value is equal to this object's value.
	 * 
	 * <p>
	 * This method will attempt to coerce the supplied value into a type
	 * compatible with the type of this data object.
	 * </p>
	 * 
	 * @param object The value to test against.
	 * @return True if the specified value is equal to this object's value.
	 */
	boolean isEqualTo(Object object);

	/**
	 * Returns true if the specified value is less than this object's value.
	 * 
	 * <p>
	 * This method will attempt to coerce the supplied value into a type
	 * compatible with the type of this data object.
	 * </p>
	 * 
	 * @param object The value to test against.
	 * @return True if the specified value is less than this object's value.
	 */
	boolean isLessThan(Object object);

	/**
	 * Returns true if the specified value is less than or equal to this object's
	 * value.
	 * 
	 * <p>
	 * This method will attempt to coerce the supplied value into a type
	 * compatible with the type of this data object.
	 * </p>
	 * 
	 * @param object The value to test against.
	 * @return True if the specified value is less than or equal to this object's
	 *         value.
	 */
	boolean isLessThanOrEqualTo(Object object);

	/**
	 * Returns true if the specified value is greater than this object's value.
	 * 
	 * <p>
	 * This method will attempt to coerce the supplied value into a type
	 * compatible with the type of this data object.
	 * </p>
	 * 
	 * @param object The value to test against.
	 * @return True if the specified value is greater than this object's value.
	 */
	boolean isGreaterThan(Object object);

	/**
	 * Returns true if the specified value is greater than or equal to this
	 * object's value.
	 * 
	 * <p>
	 * This method will attempt to coerce the supplied value into a type
	 * compatible with the type of this data object.
	 * </p>
	 * 
	 * @param object The value to test against.
	 * @return True if the specified value is greater than or equal to this
	 *         object's value.
	 */
	boolean isGreaterThanOrEqualTo(Object object);
	
	boolean isSecured();
	
	void setSecured(boolean secure);
	
	/**
	 * Returns a string representation of this data object.
	 * 
	 * @return A string representation of this data object.
	 */
	String toString();
}
