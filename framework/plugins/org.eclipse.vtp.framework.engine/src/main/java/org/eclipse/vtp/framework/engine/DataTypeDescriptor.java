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
package org.eclipse.vtp.framework.engine;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An object that describes a dynamic data type.
 * 
 * @author Lonnie Pryor
 */
public final class DataTypeDescriptor
{
	/** The name of this type. */
	private final String name;
	/** The fields declared in this type. */
	private final FieldDescriptor[] fields;

	/**
	 * Creates a new DataTypeDescriptor.
	 * 
	 * @param name The name of this type.
	 * @param fields The fields declared in this type.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws IllegalArgumentException If the same field name is listed more than
	 *           once in the supplied field array.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 * @throws NullPointerException If the supplied field array or any of its
	 *           elements are <code>null</code>.
	 */
	public DataTypeDescriptor(String name, FieldDescriptor[] fields)
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		if (fields == null)
			throw new NullPointerException("fields"); //$NON-NLS-1$
		this.name = name;
		Map map = new LinkedHashMap();
		for (int i = 0; i < fields.length; ++i)
		{
			if (fields[i] == null)
				throw new NullPointerException("fields[" //$NON-NLS-1$
						+ i + "]"); //$NON-NLS-1$
			if (map.containsKey(fields[i].getName()))
				throw new IllegalArgumentException("fields[" //$NON-NLS-1$
						+ i + "]"); //$NON-NLS-1$
			map.put(fields[i].getName(), fields[i]);
		}
		this.fields = (FieldDescriptor[])map.values().toArray(
				new FieldDescriptor[map.size()]);
	}

	/**
	 * Returns the name of this type.
	 * 
	 * @return The name of this type.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the number of fields declared on this type.
	 * 
	 * @return The number of fields declared on this type.
	 */
	public int getFieldCount()
	{
		return fields.length;
	}

	/**
	 * Returns the field at the specified index.
	 * 
	 * @return The field at the specified index.
	 * @throws IndexOutOfBoundsException If the supplied index is less than zero
	 *           or greater than or equal to this type's field count.
	 */
	public FieldDescriptor getField(int index) throws IndexOutOfBoundsException
	{
		if (index < 0 || index >= fields.length)
			throw new IndexOutOfBoundsException(String.valueOf(index));
		return fields[index];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer(getClass().getName().substring(
				getClass().getName().lastIndexOf('.') + 1)).append('[').append(name)
				.append(';');
		for (int i = 0; i < fields.length; ++i)
			buffer.append(fields[i].getName()).append(',');
		buffer.setLength(buffer.length() - 1);
		return buffer.append(']').toString();
	}
}
