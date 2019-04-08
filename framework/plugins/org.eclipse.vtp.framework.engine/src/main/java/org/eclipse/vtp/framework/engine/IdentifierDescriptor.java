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

import java.util.HashSet;
import java.util.Set;

/**
 * An object that describes an identifier of a service.
 * 
 * @author Lonnie Pryor
 */
public final class IdentifierDescriptor
{
	/** The name of this identifier. */
	private final String name;
	/** The qualifiers of this identifier. */
	private final String[] qualifiers;

	/**
	 * Creates a new FieldDescriptor.
	 * 
	 * @param name The name of this identifier.
	 * @param qualifiers The type of this identifier.
	 * @param initialValue The initial value of this field when an object is
	 *          created.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws IllegalArgumentException If the same qualifier is listed more than
	 *           once in the supplied qualifier array.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 * @throws NullPointerException If the supplied qualifier array or any of its
	 *           elements are <code>null</code>.
	 */
	public IdentifierDescriptor(String name, String[] qualifiers)
			throws IllegalArgumentException, NullPointerException
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		if (qualifiers == null)
			throw new NullPointerException("qualifiers"); //$NON-NLS-1$
		this.name = name;
		Set set = new HashSet();
		for (int i = 0; i < qualifiers.length; ++i)
		{
			if (qualifiers[i] == null)
				throw new NullPointerException("qualifiers[" //$NON-NLS-1$
						+ i + "]"); //$NON-NLS-1$
			if (!set.add(qualifiers[i]))
				throw new IllegalArgumentException("qualifiers[" //$NON-NLS-1$
						+ i + "]"); //$NON-NLS-1$
		}
		this.qualifiers = (String[])set.toArray(new String[set.size()]);
	}

	/**
	 * Returns the name of this identifier.
	 * 
	 * @return The name of this identifier.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the number of qualifiers declared on this identifier.
	 * 
	 * @return The number of qualifiers declared on this identifier.
	 */
	public int getQualifierCount()
	{
		return qualifiers.length;
	}

	/**
	 * Returns the qualifier at the specified index.
	 * 
	 * @return The qualifier at the specified index.
	 * @throws IndexOutOfBoundsException If the supplied index is less than zero
	 *           or greater than or equal to this identifier's qualifier count.
	 */
	public String getQualifier(int index) throws IndexOutOfBoundsException
	{
		if (index < 0 || index >= qualifiers.length)
			throw new IndexOutOfBoundsException(String.valueOf(index));
		return qualifiers[index];
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
		for (int i = 0; i < qualifiers.length; ++i)
			buffer.append(qualifiers[i]).append(',');
		buffer.setLength(buffer.length() - 1);
		return buffer.append(']').toString();
	}
}
