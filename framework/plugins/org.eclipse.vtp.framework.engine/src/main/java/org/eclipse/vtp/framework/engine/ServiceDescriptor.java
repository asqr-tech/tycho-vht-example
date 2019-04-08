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
 * An object that describes a service made available to actions.
 * 
 * @author Lonnie Pryor
 */
public final class ServiceDescriptor
{
	/** The constant representing the process-wide scope. */
	public static final String SCOPE_PROCESS = "process"; //$NON-NLS-1$
	/** The constant representing the session-specific scope. */
	public static final String SCOPE_SESSION = "session"; //$NON-NLS-1$
	/** The constant representing the execution-specific scope. */
	public static final String SCOPE_EXECUTION = "execution"; //$NON-NLS-1$
	/** The constant representing the action-specific scope. */
	public static final String SCOPE_ACTION = "action"; //$NON-NLS-1$

	/** The ID of this service. */
	private final String id;
	/** The name of this service. */
	private final String name;
	/** The scope this service is registered at. */
	private final String scope;
	/** The type of this service. */
	private final Class type;
	/** The identifier of this service. */
	private final IdentifierDescriptor[] identifiers;

	/**
	 * Creates a new ServiceDescriptor.
	 * 
	 * @param id The ID of this service.
	 * @param name The name of this service.
	 * @param scope The scope this service is registered at.
	 * @param type The type of this service.
	 * @param identifiers The identifiers to register the service under.
	 * @throws IllegalArgumentException If the supplied ID is empty.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws IllegalArgumentException If the supplied scope is not one of
	 *           application, session, execution, or action.
	 * @throws IllegalArgumentException If the supplied type is not a public,
	 *           concrete class with at least one public constructor.
	 * @throws IllegalArgumentException If the same identifier name is listed more
	 *           than once in the supplied identifier array.
	 * @throws NullPointerException If the supplied ID is <code>null</code>.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 * @throws NullPointerException If the supplied scope is <code>null</code>.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 * @throws NullPointerException If the supplied identifier array or any of its
	 *           elements are <code>null</code>.
	 */
	public ServiceDescriptor(String id, String name, String scope, Class type,
			IdentifierDescriptor[] identifiers) throws IllegalArgumentException,
			NullPointerException
	{
		if (id == null)
			throw new NullPointerException("id"); //$NON-NLS-1$
		if (id.length() == 0)
			throw new IllegalArgumentException("id"); //$NON-NLS-1$
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		if (scope == null)
			throw new NullPointerException("scope"); //$NON-NLS-1$
		if (type == null)
			throw new NullPointerException("type"); //$NON-NLS-1$
		if (!DescriptorUtils.isValidImplementation(type))
			throw new IllegalArgumentException("type"); //$NON-NLS-1$
		if (identifiers == null)
			throw new NullPointerException("identifiers"); //$NON-NLS-1$
		this.id = id;
		this.name = name;
		if (SCOPE_PROCESS.equalsIgnoreCase(scope))
			this.scope = SCOPE_PROCESS;
		else if (SCOPE_SESSION.equalsIgnoreCase(scope))
			this.scope = SCOPE_SESSION;
		else if (SCOPE_EXECUTION.equalsIgnoreCase(scope))
			this.scope = SCOPE_EXECUTION;
		else if (SCOPE_ACTION.equalsIgnoreCase(scope))
			this.scope = SCOPE_ACTION;
		else
			throw new IllegalArgumentException("scope"); //$NON-NLS-1$
		this.type = type;
		Map map = new LinkedHashMap();
		for (int i = 0; i < identifiers.length; ++i)
		{
			if (identifiers[i] == null)
				throw new NullPointerException("identifiers[" //$NON-NLS-1$
						+ i + "]"); //$NON-NLS-1$
			if (map.containsKey(identifiers[i].getName()))
				throw new IllegalArgumentException("identifiers[" //$NON-NLS-1$
						+ i + "]"); //$NON-NLS-1$
			map.put(identifiers[i].getName(), identifiers[i]);
		}
		this.identifiers = (IdentifierDescriptor[])map.values().toArray(
				new IdentifierDescriptor[map.size()]);
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
	 * Returns the scope this service is registered at.
	 * 
	 * @return The scope this service is registered at.
	 */
	public String getScope()
	{
		return scope;
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
	 * Returns the number of identifiers declared on this service.
	 * 
	 * @return The number of identifiers declared on this service.
	 */
	public int getIdentifierCount()
	{
		return identifiers.length;
	}

	/**
	 * Returns the identifiers at the specified index.
	 * 
	 * @return The qualifier at the specified index.
	 * @throws IndexOutOfBoundsException If the supplied index is less than zero
	 *           or greater than or equal to this service's identifiers count.
	 */
	public IdentifierDescriptor getIdentifier(int index)
			throws IndexOutOfBoundsException
	{
		if (index < 0 || index >= identifiers.length)
			throw new IndexOutOfBoundsException(String.valueOf(index));
		return identifiers[index];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer(getClass().getName().substring(
				getClass().getName().lastIndexOf('.') + 1)).append('[').append(id)
				.append(';').append(name).append(';').append(scope).append(';').append(
						type).append(';');
		for (int i = 0; i < identifiers.length; ++i)
			buffer.append(identifiers[i].getName()).append(',');
		buffer.setLength(buffer.length() - 1);
		return buffer.append(']').toString();
	}
}
