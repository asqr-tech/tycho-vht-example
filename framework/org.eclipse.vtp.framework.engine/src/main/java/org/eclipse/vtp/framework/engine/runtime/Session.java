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
import java.util.Date;
import java.util.Set;

import org.eclipse.vtp.framework.engine.support.AbstractSessionContext;
import org.eclipse.vtp.framework.spi.IExecution;
import org.eclipse.vtp.framework.spi.IExecutionDescriptor;
import org.eclipse.vtp.framework.spi.IProcess;
import org.eclipse.vtp.framework.spi.ISession;
import org.eclipse.vtp.framework.spi.ISessionDescriptor;

/**
 * Implementation of the session scope and context.
 * 
 * @author Lonnie Pryor
 */
public class Session extends Scope implements ISession
{
	/** The owner of this session. */
	public final Process process;
	/** The generic context implementation. */
	protected final Context context;

	/**
	 * Creates a new Session.
	 * 
	 * @param process The owner of this session.
	 * @param descriptor The session descriptor.
	 * @throws NullPointerException If the supplied process is <code>null</code>.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public Session(Process process, ISessionDescriptor descriptor)
			throws NullPointerException
	{
		if (process == null)
			throw new NullPointerException("process"); //$NON-NLS-1$
		this.process = process;
		this.context = new Context(descriptor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Scope#getServices(
	 *      java.lang.String)
	 */
	protected Collection getServices(String identifier)
	{
		return process.blueprint.getSessionServices(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ISession#getSessionID()
	 */
	public String getSessionID()
	{
		return context.getSessionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ISession#getSessionStartTime()
	 */
	public Date getSessionStartTime()
	{
		return context.getSessionStartTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ISession#lookupService(java.lang.String)
	 */
	public Object lookupService(String identifier) throws NullPointerException
	{
		return context.lookup(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ISession#lookupAllServices(
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
	 * @see org.eclipse.vtp.framework.spi.ISession#createExecution(
	 *      org.eclipse.vtp.framework.spi.IExecutionDescriptor)
	 */
	public IExecution createExecution(IExecutionDescriptor descriptor)
			throws NullPointerException
	{
		return new Execution(this, descriptor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ISession#getProcess()
	 */
	public IProcess getProcess()
	{
		return process;
	}

	/**
	 * Implementation of the generic context.
	 * 
	 * @author Lonnie Pryor
	 */
	protected final class Context extends AbstractSessionContext
	{
		/**
		 * Creates a new Context.
		 * 
		 * @param descriptor The session descriptor.
		 * @throws NullPointerException If the supplied descriptor is
		 *           <code>null</code>.
		 */
		Context(ISessionDescriptor descriptor) throws NullPointerException
		{
			super(process.context, descriptor);
		}

		/**
		 * Returns the session descriptor.
		 * 
		 * @return The session descriptor.
		 */
		ISessionDescriptor getDescriptor()
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
