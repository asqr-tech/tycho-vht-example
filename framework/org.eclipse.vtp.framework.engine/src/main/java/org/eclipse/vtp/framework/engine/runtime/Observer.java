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

import java.util.Hashtable;

import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.engine.ObserverDescriptor;
import org.w3c.dom.Element;

/**
 * Represents an observer that can be run by the engine before or after an
 * action.
 * 
 * @author Lonnie Pryor
 */
public class Observer extends Executable
{
	/** The descriptor of the observer. */
	private final ObserverDescriptor descriptor;
	/** The action this observer is bound to. */
	private final Action observed;
	/** The next executable in the process or <code>null</code>. */
	private Executable next = null;

	/**
	 * Creates a new Observer.
	 * 
	 * @param blueprint The blueprint of the process.
	 * @param elements The configuration data or <code>null</code> for no
	 *          configuration data.
	 * @param instanceID The ID of this instance of the observer.
	 * @param descriptor The descriptor of the observer.
	 * @param observed The action this observer is bound to.
	 * @throws NullPointerException If the supplied blueprint is <code>null</code>.
	 * @throws NullPointerException If the supplied instance ID is
	 *           <code>null</code>.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 * @throws NullPointerException If the supplied action is <code>null</code>.
	 */
	public Observer(Blueprint blueprint, Element[] elements, String instanceID,
			ObserverDescriptor descriptor, Action observed)
			throws NullPointerException
	{
		super(blueprint, descriptor.getType(), elements, instanceID);
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		if (observed == null)
			throw new NullPointerException("observed"); //$NON-NLS-1$
		this.descriptor = descriptor;
		this.observed = observed;
	}

	/**
	 * Configures this observer's placement in the process flow.
	 * 
	 * @param next The next executable in the process or <code>null</code>.
	 */
	protected void configure(Executable next)
	{
		this.next = next;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Executable#
	 *      getActionInstance()
	 */
	public Action getActionInstance()
	{
		return observed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Executable#getActionState()
	 */
	public int getActionState()
	{
		if (next == null)
			return IActionContext.STATE_AFTER;
		else if (next instanceof Observer)
			return next.getActionState();
		else if (observed.equals(next))
			return IActionContext.STATE_BEFORE;
		else
			return IActionContext.STATE_AFTER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Executable#isBlocking()
	 */
	public boolean isBlocking()
	{
		return descriptor.isBlocking();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Executable#execute(
	 *      org.eclipse.vtp.framework.engine.runtime.Sequence)
	 */
	public Executable execute(Sequence sequence)
	{
		try
		{
			((Runnable)createInstance(sequence)).run();
		}
		catch (RuntimeException cause)
		{
			if (sequence.context.isErrorEnabled())
			{
				Hashtable properties = new Hashtable();
				properties.put("cause", cause); //$NON-NLS-1$
				sequence.context.error(cause.getMessage(), properties);
			}
		}
		return next;
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
}
