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

import org.eclipse.vtp.framework.core.IAction;

/**
 * An object that describes an action.
 * 
 * @author Lonnie Pryor
 */
public final class ActionDescriptor
{
	/** The ID of this action. */
	private final String id;
	/** The name of this action. */
	private final String name;
	/** The type of this action. */
	private final Class type;
	/** True if this action is blocking. */
	private final boolean blocking;

	/**
	 * Creates a new ActionDescriptor.
	 * 
	 * @param id The ID of this action.
	 * @param name The name of this action.
	 * @param type The type of this action.
	 * @param blocking True if this action is blocking.
	 * @throws IllegalArgumentException If the supplied ID is empty.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws IllegalArgumentException If the supplied type is not a public,
	 *           concrete class with at least one public constructor or is not
	 *           assignable to {@link IAction}.
	 * @throws NullPointerException If the supplied ID is <code>null</code>.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 */
	public ActionDescriptor(String id, String name, Class type, boolean blocking)
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
		if (!DescriptorUtils.isValidImplementation(type, IAction.class))
			throw new IllegalArgumentException("type="+type.getName()); //$NON-NLS-1$
		this.id = id;
		this.name = name;
		this.type = type;
		this.blocking = blocking;
	}

	/**
	 * Returns the ID of this action.
	 * 
	 * @return The ID of this action.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Returns the name of this action.
	 * 
	 * @return The name of this action.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the type of this action.
	 * 
	 * @return The type of this action.
	 */
	public Class getType()
	{
		return type;
	}

	/**
	 * Returns true if this action is blocking.
	 * 
	 * @return True if this action is blocking.
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
				.append(';').append(name).append(';').append(type.getName())
				.append(']').toString();
	}
}
