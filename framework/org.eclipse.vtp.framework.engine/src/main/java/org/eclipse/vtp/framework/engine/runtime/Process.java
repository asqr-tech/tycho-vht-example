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
package org.eclipse.vtp.framework.engine.runtime;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.vtp.framework.engine.support.AbstractProcessContext;
import org.eclipse.vtp.framework.spi.IProcess;
import org.eclipse.vtp.framework.spi.IProcessDescriptor;
import org.eclipse.vtp.framework.spi.ISession;
import org.eclipse.vtp.framework.spi.ISessionDescriptor;

/**
 * Implementation of the process scope and context.
 * 
 * @author Lonnie Pryor
 */
public class Process extends Scope implements IProcess
{
	/** The process blueprint. */
	public final Blueprint blueprint;
	/** The generic context implementation. */
	protected final Context context;

	/**
	 * Creates a new Process.
	 * 
	 * @param blueprint The process blueprint.
	 * @param descriptor The process descriptor.
	 * @throws NullPointerException If the supplied blueprint is <code>null</code>.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public Process(Blueprint blueprint, IProcessDescriptor descriptor)
			throws NullPointerException
	{
		if (blueprint == null)
			throw new NullPointerException("blueprint"); //$NON-NLS-1$
		this.blueprint = blueprint;
		this.context = new Context(descriptor);
		blueprint.solidifyConfigurations(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Scope#lookupInScope(
	 *      java.lang.String)
	 */
	protected Object lookupInScope(String identifier)
	{
		if (IExtensionRegistry.class.getName().equals(identifier))
			return blueprint.getRegistry();
		return super.lookupInScope(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Scope#lookupAllInScope(
	 *      java.lang.String)
	 */
	protected Object[] lookupAllInScope(String identifier)
	{
		Object[] results = super.lookupAllInScope(identifier);
		if (IExtensionRegistry.class.getName().equals(identifier))
		{
			Object[] allResults = new Object[results == null ? 1 : results.length + 1];
			allResults[0] = blueprint.getRegistry();
			if (results != null)
				System.arraycopy(results, 0, allResults, 1, results.length);
			return allResults;
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Scope#getServices(
	 *      java.lang.String)
	 */
	protected Collection getServices(String identifier)
	{
		return blueprint.getProcessServices(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcess#getProcessID()
	 */
	public String getProcessID()
	{
		return context.getProcessID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcess#lookupService(java.lang.String)
	 */
	public Object lookupService(String identifier) throws NullPointerException
	{
		return context.lookup(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcess#lookupAllServices(
	 *      java.lang.String)
	 */
	public Object[] lookupAllServices(String identifier)
			throws NullPointerException
	{
		return context.lookupAll(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcess#createSession(
	 *      org.eclipse.vtp.framework.spi.ISessionDescriptor)
	 */
	public ISession createSession(ISessionDescriptor descriptor)
			throws NullPointerException
	{
		return new Session(this, descriptor);
	}

	/**
	 * Implementation of the generic context.
	 * 
	 * @author Lonnie Pryor
	 */
	protected final class Context extends AbstractProcessContext
	{
		/**
		 * Creates a new Context.
		 * 
		 * @param descriptor The process descriptor.
		 * @throws NullPointerException If the supplied descriptor is
		 *           <code>null</code>.
		 */
		Context(IProcessDescriptor descriptor) throws NullPointerException
		{
			super(descriptor);
		}

		/**
		 * Returns the process descriptor.
		 * 
		 * @return The process descriptor.
		 */
		IProcessDescriptor getDescriptor()
		{
			return descriptor;
		}

		/**
		 * Returns the provided service identifiers.
		 *
		 * @return The provided service identifiers.
		 */
		Set getProvidedServiceIdentifiers()
		{
			return providedServiceIdentifiers;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractProcessContext#
		 *      lookupLocalService(java.lang.String)
		 */
		protected Object lookupLocalService(String identifier)
		{
			Object provided = super.lookupLocalService(identifier);
			if (provided == null)
				return lookupInScope(identifier);
			return provided;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractProcessContext#
		 *      lookupAllLocalServices(java.lang.String)
		 */
		protected Object[] lookupAllLocalServices(String identifier)
		{
			Object provided = super.lookupLocalService(identifier);
			Object[] all = lookupAllInScope(identifier);
			if (provided == null)
				return all;
			Object[] result = new Object[all.length];
			result[0] = provided;
			System.arraycopy(all, 0, result, 1, all.length);
			return result;
		}
	}
}
