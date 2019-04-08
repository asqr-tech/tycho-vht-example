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

import java.util.Map;

/**
 * Represents a dynamic sequence of other data objects.
 * 
 * @author Lonnie Pryor
 */
public interface IMapObject extends IDataObject
{
	/** The name of the array type. */
	String TYPE_NAME = "Map"; //$NON-NLS-1$
	/** The name of the length field. */
	String FIELD_NAME_LENGTH = "size"; //$NON-NLS-1$

	/**
	 * Returns the length field of this array object.
	 * 
	 * @return The length field of this array object.
	 */
	INumberObject getSize();

	/**
	 * Removes the item mapped to the given key and also removes the key from
	 * the map as well.
	 * 
	 * @param item The key to remove from this map.
	 * @throws IllegalStateException If this object is read-only.
	 * @throws IndexOutOfBoundsException If the specified index is less than zero
	 *           or greater than or equal to the length of this array.
	 */
	void removeElement(String key) throws IllegalStateException,
			IndexOutOfBoundsException;
	
	Map<String, IDataObject> getValues();
}
