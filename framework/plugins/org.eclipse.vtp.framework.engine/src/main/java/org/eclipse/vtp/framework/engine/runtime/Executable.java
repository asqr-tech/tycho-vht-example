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

import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IContext;
import org.eclipse.vtp.framework.engine.support.ActionContextWrapper;
import org.w3c.dom.Element;

/**
 * Executable.
 * 
 * @author Lonnie Pryor
 */
public abstract class Executable extends Configurable
{
	/** The ID of this instance of the executable. */
	private final String instanceID;

	/**
	 * Creates a new Executable.
	 * 
	 * @param blueprint The blueprint of the process.
	 * @param type The type of the executable component.
	 * @param elements The configuration data or <code>null</code> for no
	 *          configuration data.
	 * @param instanceID The ID of this instance of the executable.
	 * @throws NullPointerException If the supplied blueprint is <code>null</code>.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 * @throws NullPointerException If the supplied instance ID is
	 *           <code>null</code>.
	 */
	public Executable(Blueprint blueprint, Class type, Element[] elements,
			String instanceID) throws NullPointerException
	{
		super(blueprint, type, elements);
		if (instanceID == null)
			throw new NullPointerException("instanceID"); //$NON-NLS-1$
		this.instanceID = instanceID;
	}

	/**
	 * Returns the ID of this instance of the executable.
	 * 
	 * @return The ID of this instance of the executable.
	 */
	public String getInstanceID()
	{
		return instanceID;
	}

	/**
	 * Returns the ID of the action instance that owns this executable.
	 * 
	 * @return The ID of the action instance that owns this executable.
	 */
	public abstract Action getActionInstance();

	/**
	 * Returns the action state this executable runs in.
	 * 
	 * @return The action state this executable runs in.
	 */
	public abstract int getActionState();

	/**
	 * Returns true if this execution is blocking.
	 * 
	 * @return True if this execution is blocking.
	 */
	public abstract boolean isBlocking();

	/**
	 * Creates and executes an instance of this executable.
	 * 
	 * @param sequence The sequence to create under.
	 * @return The next executable in the process or <code>null</code> if the
	 *         process is over.
	 */
	public abstract Executable execute(Sequence sequence);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Configurable#
	 *      createServiceRegistry(org.eclipse.vtp.framework.engine.runtime.Scope)
	 */
	protected IContext createServiceRegistry(Scope scope)
	{
		return new ActionContext((Sequence)scope);
	}

	/**
	 * Creates a new instance of the executable component.
	 * 
	 * @param sequence The sequence that owns the component.
	 * @return A new instance of the executable component.
	 * @throws NullPointerException If the supplied sequence is <code>null</code>.
	 */
	protected Object createInstance(Sequence sequence)
			throws NullPointerException
	{
		if (sequence == null)
			throw new NullPointerException("sequence"); //$NON-NLS-1$
		Builder builder = createBuilder(sequence);
		Object instance = builder.create();
		if (instance != null)
			builder.configure();
		return instance;
	}

	/**
	 * Observer-specific action context implementation.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ActionContext extends ActionContextWrapper
	{
		/** The sequence this context represents. */
		private final Sequence sequence;

		/**
		 * Creates a new ActionContext.
		 * 
		 * @param sequence The sequence this context represents.
		 * @throws NullPointerException If the supplied sequence is
		 *           <code>null</code>.
		 */
		ActionContext(Sequence sequence) throws NullPointerException
		{
			if (sequence == null)
				throw new NullPointerException("sequence"); //$NON-NLS-1$
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
				return sequence.context.lookup(identifier);
			return config;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.ActionContextWrapper#
		 *      lookupAllServices(java.lang.String)
		 */
		public Object[] lookupAll(String identifier)
				throws NullPointerException
		{
			if (identifier == null)
				throw new NullPointerException("identifier"); //$NON-NLS-1$
			if (isReservedIdentifier(identifier))
				return new Object[] { this };
			Object[] configs = lookupAllConfigurations(identifier, this);
			if (configs == null || configs.length == 0)
				return sequence.context.lookupAll(identifier);
			Object[] services = sequence.context.lookupAll(identifier);
			if (services == null || services.length == 0)
				return configs;
			Object[] results = new Object[configs.length + services.length];
			System.arraycopy(configs, 0, results, 0, configs.length);
			System.arraycopy(services, 0, results, configs.length, services.length);
			return results;
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
