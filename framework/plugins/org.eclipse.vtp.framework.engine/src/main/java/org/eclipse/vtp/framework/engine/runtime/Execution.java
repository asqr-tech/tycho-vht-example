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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.vtp.framework.engine.support.AbstractExecutionContext;
import org.eclipse.vtp.framework.spi.ICommand;
import org.eclipse.vtp.framework.spi.ICommandProcessor;
import org.eclipse.vtp.framework.spi.ICommandVisitor;
import org.eclipse.vtp.framework.spi.IExecution;
import org.eclipse.vtp.framework.spi.IExecutionDescriptor;
import org.eclipse.vtp.framework.spi.IRunnableCommand;
import org.eclipse.vtp.framework.spi.IRunnableCommandVisitor;
import org.eclipse.vtp.framework.spi.ISession;

/**
 * Implementation of the execution scope and context.
 * 
 * @author Lonnie Pryor
 */
public class Execution extends Scope implements IExecution
{
	/** The exported command queue. */
	private static final String COMMAND_QUEUE = "engine.command.queue"; //$NON-NLS-1$

	/** The owner of this execution. */
	protected final Session session;
	/** The generic context implementation. */
	protected final Context context;
	/** The command queue. */
	private LinkedList commandQueue = new LinkedList();
	/** The current sequence. */
	private Sequence current = null;

	/**
	 * Creates a new Execution.
	 * 
	 * @param session The owner of this execution.
	 * @param descriptor The execution descriptor.
	 * @throws NullPointerException If the supplied session is <code>null</code>.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public Execution(Session session, IExecutionDescriptor descriptor)
	{
		if (session == null)
			throw new NullPointerException("session"); //$NON-NLS-1$
		this.session = session;
		this.context = new Context(descriptor);
		Object exportedQueue = context.getAttribute(COMMAND_QUEUE);
		if (exportedQueue instanceof Object[])
		{
			Object[] array = (Object[])exportedQueue;
			for (int i = 0; i < array.length; i += 2)
			{
				try
				{
					ICommand command = (ICommand)context.loadClass((String)array[i])
							.newInstance();
					command.importContents(array[i + 1]);
					commandQueue.addLast(command);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		Sequence current = new Sequence(this);
		if (current.hasNext())
			this.current = current;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Scope#getServices(
	 *      java.lang.String)
	 */
	protected Collection getServices(String identifier)
	{
		return session.process.blueprint.getExecutionServices(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecution#getExecutionID()
	 */
	public String getExecutionID()
	{
		return context.getExecutionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecution#lookupService(
	 *      java.lang.String)
	 */
	public Object lookupService(String identifier) throws NullPointerException
	{
		return context.lookup(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecution#lookupAllServices(
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
	 * @see org.eclipse.vtp.framework.spi.IExecution#hasNextStep()
	 */
	public boolean hasNextStep()
	{
		return commandQueue != null && !commandQueue.isEmpty() || current != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecution#isNextStepBlocking()
	 */
	public boolean isNextStepBlocking()
	{
		if (commandQueue != null && !commandQueue.isEmpty())
			return false;
		return current != null && current.isNextBlocking();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecution#nextStep()
	 */
	public ICommand nextStep() throws IllegalStateException
	{
		if (commandQueue != null && !commandQueue.isEmpty())
			return (ICommand)commandQueue.removeFirst();
		if (current == null)
			throw new IllegalStateException();
		return new IRunnableCommand()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run()
			{
				current = current.next();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.ICommand#accept(
			 *      org.eclipse.vtp.framework.spi.ICommandVisitor)
			 */
			public Object accept(ICommandVisitor visitor) throws NullPointerException
			{
				if (visitor == null)
					throw new NullPointerException("visitor"); //$NON-NLS-1$
				if (visitor instanceof IRunnableCommandVisitor)
					return ((IRunnableCommandVisitor)visitor).visitRunnable(this);
				return visitor.visitUnknown(this);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.ICommand#importContents(
			 *      java.lang.Object)
			 */
			public void importContents(Object contents)
			{
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.ICommand#exportContents()
			 */
			public Object exportContents()
			{
				return new Object[0];
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecution#takeNextStep()
	 */
	public void takeNextStep() throws IllegalStateException, RuntimeException
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecution#getSession()
	 */
	public ISession getSession()
	{
		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecution#dispose()
	 */
	public void dispose()
	{
		if (commandQueue == null)
			return;
		LinkedList commandQueue = this.commandQueue;
		this.commandQueue = null;
		Object[] array = new Object[commandQueue.size() * 2];
		int i = 0;
		for (Iterator j = commandQueue.iterator(); j.hasNext(); i += 2)
		{
			ICommand command = (ICommand)j.next();
			array[i] = command.getClass().getName();
			array[i + 1] = command.exportContents();
		}
	}

	/**
	 * Implementation of the generic context.
	 * 
	 * @author Lonnie Pryor
	 */
	protected final class Context extends AbstractExecutionContext implements
			ICommandProcessor
	{
		/**
		 * Creates a new Context.
		 * 
		 * @param descriptor The execution descriptor.
		 * @throws NullPointerException If the supplied descriptor is
		 *           <code>null</code>.
		 */
		Context(IExecutionDescriptor descriptor) throws NullPointerException
		{
			super(session.context, descriptor);
		}

		/**
		 * Returns the execution descriptor.
		 * 
		 * @return The execution descriptor.
		 */
		IExecutionDescriptor getDescriptor()
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
		 * @see org.eclipse.vtp.framework.engine.support.AbstractExecutionContext#
		 *      lookupReservedService(java.lang.String)
		 */
		protected Object lookupReservedService(String identifier)
		{
			Object service = super.lookupReservedService(identifier);
			if (service == null
					&& ICommandProcessor.class.getName().equals(identifier))
				service = this;
			return service;
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.ICommandProcessor#enqueue(
		 *      org.eclipse.vtp.framework.spi.ICommand)
		 */
		public boolean enqueue(ICommand command)
		{
			commandQueue.addLast(command);
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.ICommandProcessor#process(
		 *      org.eclipse.vtp.framework.spi.ICommand)
		 */
		public boolean process(ICommand command) throws IllegalStateException
		{
			// commandQueue.addLast(command);
			throw new IllegalStateException(); // FIXME
		}
	}
}
