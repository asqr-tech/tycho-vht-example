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

/**
 * An object that describes a field of a dynamic data type.
 * 
 * @author Lonnie Pryor
 */
public final class FieldDescriptor
{
	/** The name of this field. */
	private final String name;
	/** The type of this field. */
	private final String type;
	/** The initial value of this field when an object is created. */
	private final String initialValue;

	/**
	 * Creates a new FieldDescriptor.
	 * 
	 * @param name The name of this field.
	 * @param type The type of this field.
	 * @param initialValue The initial value of this field when an object is
	 *          created.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws IllegalArgumentException If the supplied type is empty.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 */
	public FieldDescriptor(String name, String type, String initialValue)
			throws IllegalArgumentException, NullPointerException
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		if (type == null)
			throw new NullPointerException("type"); //$NON-NLS-1$
		if (type.length() == 0)
			throw new IllegalArgumentException("type"); //$NON-NLS-1$
		this.name = name;
		this.type = type;
		this.initialValue = initialValue;
	}

	/**
	 * Returns the name of this field.
	 * 
	 * @return The name of this field.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the type of this field.
	 * 
	 * @return The type of this field.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Returns the initial value of this field when an object is created.
	 * 
	 * @return The initial value of this field when an object is created.
	 */
	public String getInitialValue()
	{
		return initialValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return new StringBuffer(getClass().getName().substring(
				getClass().getName().lastIndexOf('.') + 1)).append('[').append(name)
				.append(';').append(type).append(';').append(initialValue).append(']')
				.toString();
	}
}
