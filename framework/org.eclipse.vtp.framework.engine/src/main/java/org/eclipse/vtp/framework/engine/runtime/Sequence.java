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

import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.engine.support.AbstractActionContext;

/**
 * Implementation of the action scope and context.
 * 
 * @author Lonnie Pryor
 */
public class Sequence extends Scope
{
	/** The attribute name for the current position in the process. */
	private static final String SEQUENCE_POSITION = "engine.sequence.position"; //$NON-NLS-1$
	private static final String SEQUENCE_ENTRY = "engine.sequence.entry";

	/** The owner of this sequence. */
	protected final Execution execution;
	/** The generic context implementation. */
	protected final Context context;
	/** The next step in this sequence. */
	private Executable next = null;

	/**
	 * Creates a new Sequence.
	 * 
	 * @param execution The owner of this sequence.
	 * @throws NullPointerException If the supplied execution is <code>null</code>.
	 */
	public Sequence(Execution execution) throws NullPointerException
	{
		if (execution == null)
			throw new NullPointerException("execution"); //$NON-NLS-1$
		this.execution = execution;
		this.context = new Context();
		Object position = context.getAttribute(SEQUENCE_POSITION);
		if (position == null)
		{
			String entry = (String)context.getAttribute(SEQUENCE_ENTRY);
			if(entry == null || entry.equals(""))
				entry = "Default";
			this.next = execution.session.process.blueprint.getEntryPoint(entry);
			context.info("Sequence position is null, starting at entry point: " + entry);
		}
		else if (position instanceof String)
			this.next = execution.session.process.blueprint
					.getExecutable((String)position);
	}

	/**
	 * Creates a new Sequence.
	 * 
	 * @param execution The owner of this sequence.
	 * @param next The next executable this sequence runs.
	 */
	private Sequence(Execution execution, Executable next)
	{
		this.execution = execution;
		this.context = new Context();
		this.next = next;
	}

	/**
	 * Returns true if there is another step in this sequence.
	 * 
	 * @return True if there is another step in this sequence.
	 */
	public boolean hasNext()
	{
		return next != null;
	}

	/**
	 * Returns true if the next step is blocking.
	 * 
	 * @return True if the next step is blocking
	 */
	public boolean isNextBlocking()
	{
		return next.isBlocking();
	}

	/**
	 * Executes the next executable.
	 * 
	 * @return The next sequence to use or <code>null</code> if there are no
	 *         more executable objects to run.
	 */
	public Sequence next()
	{
		Executable previous = next;
		String instanceID = next.getActionInstance().getInstanceID();
		int colon = instanceID.indexOf(':');
		if (colon < 0)
			context.setAttribute("span.primitive", instanceID);
		else {
			context.setAttribute("span.dialog", instanceID.substring(0, colon));
			context.setAttribute("span.primitive", instanceID.substring(colon + 1));
		}
		context.setAttribute("span.primitive.name", next.getActionInstance()
				.getName());
		next = next.execute(this);
		if (next == null)
		{
			context.setAttribute(SEQUENCE_POSITION, ""); //$NON-NLS-1$
			return null;
		}
		context.setAttribute(SEQUENCE_POSITION, next.getInstanceID());
		if (!previous.getActionInstance().equals(next.getActionInstance()))
		{
			Executable next = this.next;
			this.next = null;
			return new Sequence(execution, next);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Scope#getServices(
	 *      java.lang.String)
	 */
	protected Collection getServices(String identifier)
	{
		return execution.session.process.blueprint.getActionServices(identifier);
	}

	/**
	 * Implementation of the generic context.
	 * 
	 * @author Lonnie Pryor
	 */
	protected final class Context extends AbstractActionContext
	{
		/**
		 * Creates a new Context.
		 */
		Context()
		{
			super(execution.context);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractProcessContext#
		 *      lookupLocalService(java.lang.String)
		 */
		protected Object lookupLocalService(String identifier)
		{
			return lookupInScope(identifier);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractProcessContext#
		 *      lookupAllLocalServices(java.lang.String)
		 */
		protected Object[] lookupAllLocalServices(String identifier)
		{
			return lookupAllInScope(identifier);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IActionContext#getActionID()
		 */
		public String getActionID()
		{
			return next.getActionInstance().getInstanceID();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IActionContext#getActionName()
		 */
		public String getActionName()
		{
			return next.getActionInstance().getName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IActionContext#getActionState()
		 */
		public int getActionState()
		{
			return next.getActionState();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IActionContext#createResult(
		 *      java.lang.String, java.lang.Throwable)
		 */
		public IActionResult createResult(final String resultName,
				final Throwable failureCause)
		{
			return new IActionResult()
			{
				public String getName()
				{
					return resultName == null || resultName.length() == 0 ? RESULT_NAME_ERROR
							: resultName;
				}

				public Throwable getFailureCause()
				{
					return failureCause;
				}
			};
		}
	}
}
