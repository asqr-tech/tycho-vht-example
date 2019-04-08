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
package org.eclipse.vtp.framework.common.services;

import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.core.IActionContext;

/**
 * An {@link IScriptable} implementation that makes the {@link IActionContext}
 * instance available as a scripting object.
 * 
 * <p>
 * This service will make available a "Action" object to all scripts in its
 * scope. The variable supports the following properties:
 * <ul>
 * <li><code>id</code>: a string containing the action ID</li>
 * <li><code>state</code>: current state of the action</li>
 * </ul>
 * The state will always be one of "before", "during", or "after".
 * </p>
 * 
 * <p>
 * The "Action" scripting object uses the action ID as the implicit value, thus
 * it can be compared to other string objects.
 * </p>
 * 
 * @author Lonnie Pryor
 * @see IActionContext
 */
public class ScriptableActionContext implements IScriptable
{
	/** The context to provide scripting services for. */
	private final IActionContext context;

	/**
	 * Creates a new ScriptableActionContext.
	 * 
	 * @param context The context to provide scripting services for.
	 */
	public ScriptableActionContext(IActionContext context)
	{
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
	 */
	public final String getName()
	{
		return "Action"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
	 */
	public boolean hasValue()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
	 */
	public Object toValue()
	{
		return getEntry("id"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getFunctionNames()
	 */
	public final String[] getFunctionNames()
	{
		return new String[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
	 *      java.lang.String, java.lang.Object[])
	 */
	public final Object invokeFunction(String name, Object[] arguments)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
	 */
	public final boolean hasItem(int index)
	{
		return false;
	}

	public String[] getPropertyNames()
	{
		return new String[] {"id", "state"};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasEntry(
	 *      java.lang.String)
	 */
	public final boolean hasEntry(String name)
	{
		return "id".equals(name) //$NON-NLS-1$
				|| "state".equals(name); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
	 */
	public final Object getItem(int index)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getEntry(
	 *      java.lang.String)
	 */
	public final Object getEntry(String name)
	{
		if ("id".equals(name)) //$NON-NLS-1$
			return context.getActionID();
		if ("state".equals(name)) //$NON-NLS-1$
		{
			switch (context.getActionState())
			{
			case IActionContext.STATE_BEFORE:
				return "before";//$NON-NLS-1$
			case IActionContext.STATE_DURING:
				return "during";//$NON-NLS-1$
			case IActionContext.STATE_AFTER:
				return "after";//$NON-NLS-1$
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
	 *      java.lang.Object)
	 */
	public final boolean setItem(int index, Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setEntry(
	 *      java.lang.String, java.lang.Object)
	 */
	public final boolean setEntry(String name, Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
	 */
	public final boolean clearItem(int index)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearEntry(
	 *      java.lang.String)
	 */
	public final boolean clearEntry(String name)
	{
		return false;
	}

	@Override
	public boolean isMutable()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
