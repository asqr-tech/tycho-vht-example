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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.core.IProcessContext;
import org.eclipse.vtp.framework.core.IContext;
import org.eclipse.vtp.framework.core.ISessionContext;
import org.eclipse.vtp.framework.engine.IdentifierDescriptor;
import org.eclipse.vtp.framework.engine.ServiceDescriptor;
import org.eclipse.vtp.framework.engine.support.ActionContextWrapper;
import org.eclipse.vtp.framework.engine.support.ExecutionContextWrapper;
import org.eclipse.vtp.framework.engine.support.ProcessContextWrapper;
import org.eclipse.vtp.framework.engine.support.SessionContextWrapper;
import org.eclipse.vtp.framework.spi.ICommandProcessor;
import org.w3c.dom.Element;

/**
 * Represents a service available to actions and observers.
 * 
 * @author Lonnie Pryor
 */
public class Service extends Configurable
{
	/** The descriptor this service is based on. */
	private final ServiceDescriptor descriptor;
	/** The identifiers of this service. */
	private final Set identifiers;

	/**
	 * Creates a new Service.
	 * 
	 * @param blueprint The blueprint of the process.
	 * @param elements The configuration data or <code>null</code> for no
	 *          configuration data.
	 * @param descriptor
	 * @throws NullPointerException If the supplied blueprint is <code>null</code>.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public Service(Blueprint blueprint, Element[] configurations,
			ServiceDescriptor descriptor) throws NullPointerException
	{
		super(blueprint, descriptor.getType(), configurations);
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		this.descriptor = descriptor;
		Set identifiers = new HashSet();
		identifiers.add(descriptor.getId());
		Set typeHierarchy = new HashSet();
		RuntimeUtils.listTypeHierarchy(descriptor.getType(), typeHierarchy);
		typeHierarchy.remove(Object.class);
		for (Iterator i = typeHierarchy.iterator(); i.hasNext();)
			identifiers.add(((Class)i.next()).getName());
		int identifierCount = descriptor.getIdentifierCount();
		for (int i = 0; i < identifierCount; ++i)
		{
			IdentifierDescriptor identifier = descriptor.getIdentifier(i);
			identifiers.add(identifier.getName());
			int qualifierCount = identifier.getQualifierCount();
			for (int j = 0; j < qualifierCount; ++j)
				identifiers.add(RuntimeUtils.getQualifiedIdentifier(identifier
						.getName(), identifier.getQualifier(j)));
		}
		this.identifiers = Collections.unmodifiableSet(new HashSet(identifiers));
	}

	/**
	 * Returns the ID of the descriptor this service is based on.
	 * 
	 * @return The ID of the descriptor this service is based on.
	 */
	public String getDescriptorID()
	{
		return descriptor.getId();
	}

	/**
	 * Returns the identifiers of this service.
	 * 
	 * @return The identifiers of this service.
	 */
	public Set getIdentifiers()
	{
		return identifiers;
	}

	/**
	 * Looks up a service with the specified identifier in the supplied scope or
	 * <code>null</code> if said identifier is also present on this service or
	 * no such service exists.
	 * 
	 * @param identifier The identifier of the service to look up.
	 * @param scope The scope to search for a sibling service in.
	 * @return A service with the specified identifier in the supplied scope or
	 *         <code>null</code> if said identifier is also present on this
	 *         service or no such service exists.
	 */
	protected Object lookupSiblingService(String identifier, Scope scope)
	{
		if (scope instanceof Process)
		{
			Process process = (Process)scope;
			if (process.context.getProvidedServiceIdentifiers().contains(identifier))
				return process.context.getDescriptor().getService(identifier);
		}
		else if (scope instanceof Session)
		{
			Session session = (Session)scope;
			if (session.context.getProvidedServiceIdentifiers().contains(identifier))
				return session.context.getDescriptor().getService(identifier);
		}
		else if (scope instanceof Execution)
		{
			Execution execution = (Execution)scope;
			if (execution.context.getProvidedServiceIdentifiers()
					.contains(identifier))
				return execution.context.getDescriptor().getService(identifier);
		}
		if (identifiers.contains(identifier))
			return null;
		return scope.lookupInScope(identifier);
	}

	/**
	 * Looks up all services with the specified identifier in the supplied scope
	 * or <code>null</code> if said identifier is also present on this service
	 * or no such service exists.
	 * 
	 * @param identifier The identifier of the services to look up.
	 * @param scope The scope to search for sibling services in.
	 * @return All services with the specified identifier in the supplied scope or
	 *         <code>null</code> if said identifier is also present on this
	 *         service or no such service exists.
	 */
	protected Object[] lookupAllSiblingServices(String identifier, Scope scope)
	{
		List results = new LinkedList();
		if (scope instanceof Process)
		{
			Process process = (Process)scope;
			if (process.context.getProvidedServiceIdentifiers().contains(identifier))
				results.add(process.context.getDescriptor().getService(identifier));
		}
		else if (scope instanceof Session)
		{
			Session session = (Session)scope;
			if (session.context.getProvidedServiceIdentifiers().contains(identifier))
				results.add(session.context.getDescriptor().getService(identifier));
		}
		else if (scope instanceof Execution)
		{
			Execution execution = (Execution)scope;
			if (execution.context.getProvidedServiceIdentifiers()
					.contains(identifier))
				results.add(execution.context.getDescriptor().getService(identifier));
		}
		if (!identifiers.contains(identifier))
		{
			Object[] services = scope.lookupAllInScope(identifier);
			if (services != null)
				for (int i = 0; i < services.length; ++i)
					results.add(services[i]);
		}
		return results.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Configurable#
	 *      getComponentType()
	 */
	protected Class getComponentType()
	{
		return descriptor.getType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Configurable#
	 *      createServiceRegistry(org.eclipse.vtp.framework.engine.runtime.Scope)
	 */
	protected IContext createServiceRegistry(Scope scope)
	{
		if (scope instanceof Process)
			return new ProcessContext((Process)scope);
		if (scope instanceof Session)
			return new SessionContext((Session)scope);
		if (scope instanceof Execution)
			return new ExecutionContext((Execution)scope);
		if (scope instanceof Sequence)
			return new ActionContext((Sequence)scope);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Configurable#getInstance(
	 *      org.eclipse.vtp.framework.engine.runtime.Scope)
	 */
	public Object getInstance(Scope scope) throws IllegalStateException,
			NullPointerException
	{
		return scope.getServiceInstance(this);
	}

	/**
	 * Service-specific process context implementation.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ProcessContext extends ProcessContextWrapper
	{
		/** The process this context represents. */
		final Process process;

		/**
		 * Creates a new ProcessContext.
		 * 
		 * @param process The process this context represents.
		 * @throws NullPointerException If the supplied process is <code>null</code>.
		 */
		ProcessContext(Process process) throws NullPointerException
		{
			this.process = process;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.ActionContextWrapper#
		 *      lookupService(java.lang.String)
		 */
		public Object lookup(String identifier) throws NullPointerException
		{
			if (identifier == null)
				throw new NullPointerException("identifier"); //$NON-NLS-1$
			if (isReservedIdentifier(identifier))
				return this;
			Object config = lookupConfiguration(identifier, this);
			if (config == null)
				return lookupSiblingService(identifier, process);
			return config;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.ActionContextWrapper#
		 *      lookupAllServices(java.lang.String)
		 */
		public Object[] lookupAll(String identifier) throws NullPointerException
		{
			if (identifier == null)
				throw new NullPointerException("identifier"); //$NON-NLS-1$
			if (isReservedIdentifier(identifier))
				return new Object[] { this };
			List results = new LinkedList();
			Object[] configs = lookupAllConfigurations(identifier, this);
			if (configs != null)
				for (int i = 0; i < configs.length; ++i)
					results.add(configs[i]);
			Object[] services = lookupAllSiblingServices(identifier, process);
			if (services != null)
				for (int i = 0; i < services.length; ++i)
					results.add(services[i]);
			return results.toArray();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.ProcessContextWrapper#
		 *      getProcessContext()
		 */
		protected IProcessContext getProcessContext()
		{
			return process.context;
		}
	}

	/**
	 * Service-specific session context implementation.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class SessionContext extends SessionContextWrapper
	{
		/** The session this context represents. */
		final Session session;

		/**
		 * Creates a new SessionContext.
		 * 
		 * @param session The session this context represents.
		 * @throws NullPointerException If the supplied session is <code>null</code>.
		 */
		SessionContext(Session session) throws NullPointerException
		{
			this.session = session;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.ActionContextWrapper#
		 *      lookupService(java.lang.String)
		 */
		public Object lookup(String identifier) throws NullPointerException
		{
			if (identifier == null)
				throw new NullPointerException("identifier"); //$NON-NLS-1$
			if (isReservedIdentifier(identifier))
				return this;
			Object config = lookupConfiguration(identifier, this);
			if (config == null)
			{
				Object service = lookupSiblingService(identifier, session);
				if (service == null)
					return session.process.context.lookup(identifier);
				return service;
			}
			return config;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.ActionContextWrapper#
		 *      lookupAllServices(java.lang.String)
		 */
		public Object[] lookupAll(String identifier) throws NullPointerException
		{
			if (identifier == null)
				throw new NullPointerException("identifier"); //$NON-NLS-1$
			if (isReservedIdentifier(identifier))
				return new Object[] { this };
			List results = new LinkedList();
			Object[] configs = lookupAllConfigurations(identifier, this);
			if (configs != null)
				for (int i = 0; i < configs.length; ++i)
					results.add(configs[i]);
			Object[] services = lookupAllSiblingServices(identifier, session);
			if (services != null)
				for (int i = 0; i < services.length; ++i)
					results.add(services[i]);
			services = session.process.context.lookupAll(identifier);
			if (services != null)
				for (int i = 0; i < services.length; ++i)
					results.add(services[i]);
			return results.toArray();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.SessionContextWrapper#
		 *      getSessionContext()
		 */
		protected ISessionContext getSessionContext()
		{
			return session.context;
		}
	}

	/**
	 * Service-specific execution context implementation.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ExecutionContext extends ExecutionContextWrapper
	{
		/** The execution this context represents. */
		final Execution execution;

		/**
		 * Creates a new ExecutionContext.
		 * 
		 * @param execution The execution this context represents.
		 * @throws NullPointerException If the supplied execution is
		 *           <code>null</code>.
		 */
		ExecutionContext(Execution execution) throws NullPointerException
		{
			this.execution = execution;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.ActionContextWrapper#
		 *      lookupService(java.lang.String)
		 */
		public Object lookup(String identifier) throws NullPointerException
		{
			if (identifier == null)
				throw new NullPointerException("identifier"); //$NON-NLS-1$
			if (isReservedIdentifier(identifier))
				return this;
			if (ICommandProcessor.class.getName().equals(identifier))
				return execution.context;
			Object config = lookupConfiguration(identifier, this);
			if (config == null)
			{
				Object service = lookupSiblingService(identifier, execution);
				if (service == null)
					return execution.session.context.lookup(identifier);
				return service;
			}
			return config;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.ActionContextWrapper#
		 *      lookupAllServices(java.lang.String)
		 */
		public Object[] lookupAll(String identifier) throws NullPointerException
		{
			if (identifier == null)
				throw new NullPointerException("identifier"); //$NON-NLS-1$
			if (isReservedIdentifier(identifier))
				return new Object[] { this };
			List results = new LinkedList();
			Object[] configs = lookupAllConfigurations(identifier, this);
			if (configs != null)
				for (int i = 0; i < configs.length; ++i)
					results.add(configs[i]);
			Object[] services = lookupAllSiblingServices(identifier, execution);
			if (services != null)
				for (int i = 0; i < services.length; ++i)
					results.add(services[i]);
			services = execution.session.context.lookupAll(identifier);
			if (services != null)
				for (int i = 0; i < services.length; ++i)
					results.add(services[i]);
			return results.toArray();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.ExecutionContextWrapper#
		 *      getExecutionContext()
		 */
		protected IExecutionContext getExecutionContext()
		{
			return execution.context;
		}
	}

	/**
	 * Service-specific action context implementation.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ActionContext extends ActionContextWrapper
	{
		/** The sequence this context represents. */
		final Sequence sequence;

		/**
		 * Creates a new ActionContext.
		 * 
		 * @param sequence The sequence this context represents.
		 * @throws NullPointerException If the supplied sequence is
		 *           <code>null</code>.
		 */
		ActionContext(Sequence sequence) throws NullPointerException
		{
			this.sequence = sequence;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.ActionContextWrapper#
		 *      lookupService(java.lang.String)
		 */
		public Object lookup(String identifier) throws NullPointerException
		{
			if (identifier == null)
				throw new NullPointerException("identifier"); //$NON-NLS-1$
			if (isReservedIdentifier(identifier))
				return this;
			Object config = lookupConfiguration(identifier, this);
			if (config == null)
			{
				Object service = lookupSiblingService(identifier, sequence);
				if (service == null)
					return sequence.execution.context.lookup(identifier);
				return service;
			}
			return config;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.ActionContextWrapper#
		 *      lookupAllServices(java.lang.String)
		 */
		public Object[] lookupAll(String identifier) throws NullPointerException
		{
			if (identifier == null)
				throw new NullPointerException("identifier"); //$NON-NLS-1$
			if (isReservedIdentifier(identifier))
				return new Object[] { this };
			List results = new LinkedList();
			Object[] configs = lookupAllConfigurations(identifier, this);
			if (configs != null)
				for (int i = 0; i < configs.length; ++i)
					results.add(configs[i]);
			Object[] services = lookupAllSiblingServices(identifier, sequence);
			if (services != null)
				for (int i = 0; i < services.length; ++i)
					results.add(services[i]);
			services = sequence.execution.context.lookupAll(identifier);
			if (services != null)
				for (int i = 0; i < services.length; ++i)
					results.add(services[i]);
			return results.toArray();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.ActionContextWrapper#
		 *      getActionContext()
		 */
		protected IActionContext getActionContext()
		{
			return sequence.context;
		}
	}
}
