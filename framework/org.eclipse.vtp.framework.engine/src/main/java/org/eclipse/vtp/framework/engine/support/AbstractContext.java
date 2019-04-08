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
package org.eclipse.vtp.framework.engine.support;

import java.util.LinkedList;

import org.eclipse.vtp.framework.core.IContext;

/**
 * A support implementation of the {@link IContext} interface.
 * 
 * @author Lonnie Pryor
 */
public abstract class AbstractContext extends AbstractLogger implements
		IContext
{
	/**
	 * Creates a new AbstractContext.
	 */
	protected AbstractContext()
	{
	}

	/**
	 * Returns the reserved service if the specified identifier is one of the
	 * reserved identifiers or <code>null</code> if these conditions are not
	 * met.
	 * 
	 * @param identifier The identifier of the service to return, will never be
	 *          <code>null</code>.
	 * @return The reserved service if the specified identifier is one of the
	 *         reserved identifiers or <code>null</code> if these conditions are
	 *         not met.
	 */
	protected Object lookupReservedService(String identifier)
	{
		if (IContext.class.getName().equals(identifier))
			return this;
		return null;
	}

	/**
	 * Returns a service registered under the specified identifier in this
	 * registry or <code>null</code> if no such service exists.
	 * 
	 * @param identifier The identifier of the service to return, will never be
	 *          <code>null</code>.
	 * @return A service registered under the specified identifier in this
	 *         registry or <code>null</code> if no such service exists.
	 */
	protected Object lookupLocalService(String identifier)
	{
		return null;
	}

	/**
	 * Returns all the services registered under the specified identifier in this
	 * registry or <code>null</code> if no such services can be found.
	 * 
	 * @param identifier The identifier of the services to return, will never be
	 *          <code>null</code>.
	 * @return All the services registered under the specified identifier in this
	 *         registry or <code>null</code> if no such services can be found.
	 */
	protected Object[] lookupAllLocalServices(String identifier)
	{
		return null;
	}

	/**
	 * Returns a service registered under the specified identifier in any ancestor
	 * registry or <code>null</code> if no such service exists.
	 * 
	 * @param identifier The identifier of the service to return, will never be
	 *          <code>null</code>.
	 * @return A service registered under the specified identifier in any ancestor
	 *         registry or <code>null</code> if no such service exists.
	 */
	protected Object lookupInheritedService(String identifier)
	{
		return null;
	}

	/**
	 * Returns all the services registered under the specified identifier in any
	 * ancestor registry or <code>null</code> if no such services can be found.
	 * 
	 * @param identifier The identifier of the services to return, will never be
	 *          <code>null</code>.
	 * @return All the services registered under the specified identifier in any
	 *         ancestor registry or <code>null</code> if no such services can be
	 *         found.
	 */
	protected Object[] lookupAllInheritedServices(String identifier)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IServiceRegistry#lookupService(
	 *      java.lang.String)
	 */
	public final Object lookup(String identifier) throws NullPointerException
	{
		if (identifier == null)
			throw new NullPointerException("identifier"); //$NON-NLS-1$
		Object service = lookupReservedService(identifier);
		if (service != null)
			return service;
		service = lookupLocalService(identifier);
		if (service != null)
			return service;
		return lookupInheritedService(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IServiceRegistry#lookupAllServices(
	 *      java.lang.String)
	 */
	public final Object[] lookupAll(String identifier)
			throws NullPointerException
	{
		if (identifier == null)
			throw new NullPointerException("identifier"); //$NON-NLS-1$
		Object service = lookupReservedService(identifier);
		if (service != null)
			return new Object[] { service };
		LinkedList results = new LinkedList();
		Object[] services = lookupAllLocalServices(identifier);
		if (services != null)
			for (int i = 0; i < services.length; ++i)
				if (services[i] != null)
					results.addLast(services[i]);
		services = lookupAllInheritedServices(identifier);
		if (services != null)
			for (int i = 0; i < services.length; ++i)
				if (services[i] != null)
					results.addLast(services[i]);
		return results.toArray();
	}
}
