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
 * Represents a dynamic sequence of other data objects.
 * 
 * @author Lonnie Pryor
 */
public interface IArrayObject extends IDataObject
{
	/** The name of the array type. */
	String TYPE_NAME = "Array"; //$NON-NLS-1$
	/** The name of the length field. */
	String FIELD_NAME_LENGTH = "length"; //$NON-NLS-1$

	/**
	 * Returns the length field of this array object.
	 * 
	 * @return The length field of this array object.
	 */
	INumberObject getLength();

	/**
	 * Returns the item at the specified index in this array.
	 * 
	 * @param index The index of the item to return.
	 * @return The item at the specified index in this array.
	 * @throws IndexOutOfBoundsException If the specified index is less than zero
	 *           or greater than or equal to the length of this array.
	 */
	IDataObject getElement(int index) throws IndexOutOfBoundsException;

	/**
	 * Adds an item to the end of this array.
	 * 
	 * @param item The name item to add to this array.
	 * @throws IllegalArgumentException If the supplied item was not created by
	 *           the registry that created this array.
	 * @throws IllegalStateException If this object is read-only.
	 */
	void addElement(IDataObject item) throws IllegalArgumentException,
			IllegalStateException;

	/**
	 * Inserts an item at the specified index in this array.
	 * 
	 * @param index The index to insert the item at.
	 * @param item The item to insert into this array.
	 * @throws IllegalArgumentException If the supplied item was not created by
	 *           the registry that created this array.
	 * @throws IllegalStateException If this object is read-only.
	 * @throws IndexOutOfBoundsException If the specified index is less than zero
	 *           or greater than the length of this array.
	 */
	void insertElement(int index, IDataObject item) throws IllegalArgumentException,
			IllegalStateException, IndexOutOfBoundsException;

	/**
	 * Sets the item at the specified index in this array.
	 * 
	 * @param index The index to set the item at.
	 * @param item The item to set at the specified index in this array.
	 * @throws IllegalArgumentException If the supplied item was not created by
	 *           the registry that created this array.
	 * @throws IllegalStateException If this object is read-only.
	 * @throws IndexOutOfBoundsException If the specified index is less than zero
	 *           or greater than the length of this array.
	 */
	void setElement(int index, IDataObject item) throws IllegalArgumentException,
			IllegalStateException, IndexOutOfBoundsException;

	/**
	 * Removes the item at the specified index in this array.
	 * 
	 * @param item The item to remove from this array.
	 * @throws IllegalStateException If this object is read-only.
	 * @throws IndexOutOfBoundsException If the specified index is less than zero
	 *           or greater than or equal to the length of this array.
	 */
	void removeElement(int index) throws IllegalStateException,
			IndexOutOfBoundsException;
}
