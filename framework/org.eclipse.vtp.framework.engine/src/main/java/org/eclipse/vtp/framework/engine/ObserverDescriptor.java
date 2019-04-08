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
 * An object that describes an observer that can be bound to actions.
 * 
 * @author Lonnie Pryor
 */
public final class ObserverDescriptor
{
	/** The ID of this service. */
	private final String id;
	/** The name of this service. */
	private final String name;
	/** The type of this service. */
	private final Class type;
	/** True if this observer is blocking. */
	private final boolean blocking;

	/**
	 * Creates a new ObserverDescriptor.
	 * 
	 * @param id The ID of this observer.
	 * @param name The name of this observer.
	 * @param type The type of this observer.
	 * @param blocking True if this observer is blocking.
	 * @throws IllegalArgumentException If the supplied ID is empty.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws IllegalArgumentException If the supplied event is not one of before
	 *           or after.
	 * @throws IllegalArgumentException If the supplied type is not a public,
	 *           concrete class with at least one public constructor or is not
	 *           assignable to {@link Runnable}.
	 * @throws NullPointerException If the supplied ID is <code>null</code>.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 * @throws NullPointerException If the supplied event is <code>null</code>.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 */
	public ObserverDescriptor(String id, String name, Class type, boolean blocking)
			throws IllegalArgumentException, NullPointerException
	{
		if (id == null)
			throw new NullPointerException("id"); //$NON-NLS-1$
		if (id.length() == 0)
			throw new IllegalArgumentException("id"); //$NON-NLS-1$
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		if (type == null)
			throw new NullPointerException("type"); //$NON-NLS-1$
		if (!DescriptorUtils.isValidImplementation(type, Runnable.class))
			throw new IllegalArgumentException("type"); //$NON-NLS-1$
		this.id = id;
		this.name = name;
		this.type = type;
		this.blocking = blocking;
	}

	/**
	 * Returns the ID of this service.
	 * 
	 * @return The ID of this service.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Returns the name of this service.
	 * 
	 * @return The name of this service.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the type of this service.
	 * 
	 * @return The type of this service.
	 */
	public Class getType()
	{
		return type;
	}

	/**
	 * Returns true if this observer is blocking.
	 * 
	 * @return True if this observer is blocking.
	 */
	public boolean isBlocking()
	{
		return blocking;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return new StringBuffer(getClass().getName().substring(
				getClass().getName().lastIndexOf('.') + 1)).append('[').append(id)
				.append(';').append(name).append(';').append(type).append(']')
				.toString();
	}
}
