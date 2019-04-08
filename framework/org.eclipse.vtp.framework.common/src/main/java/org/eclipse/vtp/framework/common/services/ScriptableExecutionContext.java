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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.core.IExecutionContext;

/**
 * An {@link IScriptable} implementation that makes the
 * {@link IExecutionContext} instance available as a scripting object.
 * 
 * <p>
 * This service will make available a "Execution" object to all scripts in its
 * scope. The variable supports the following properties:
 * <ul>
 * <li><code>id</code>: a string containing the execution ID</li>
 * <li><code>parameters</code>: an object containing the execution
 * parameters</li>
 * </ul>
 * The parameters object listed above will have a property for each execution
 * parameter defined in the execution context. New parameters may be added by
 * assigning to non-existent properties of the object. The parameters object
 * will not have an implicit value.
 * </p>
 * 
 * <p>
 * The "Execution" scripting object uses the execution ID as the implicit value,
 * thus it can be compared to other string objects.
 * </p>
 * 
 * @author Lonnie Pryor
 * @see IExecutionContext
 */
public class ScriptableExecutionContext implements IScriptable
{
	/** The context to provide scripting services for. */
	private final IExecutionContext context;
	/** The scripted view of the execution parameters. */
	private final ScriptableParameters parameters = new ScriptableParameters();

	/**
	 * Creates a new ScriptableExecutionContext.
	 * 
	 * @param context The context to provide scripting services for.
	 */
	public ScriptableExecutionContext(IExecutionContext context)
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
		return "Execution"; //$NON-NLS-1$
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
		List<String> propNames = new ArrayList<String>();
		propNames.add("id");
		for(String prop : parameters.getPropertyNames())
			propNames.add(prop);
		return propNames.toArray(new String[propNames.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasEntry(
	 *      java.lang.String)
	 */
	public final boolean hasEntry(String name)
	{
		return "id".equals(name) || parameters.getName().equals(name); //$NON-NLS-1$
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
			return context.getExecutionID();
		if (parameters.getName().equals(name))
			return parameters;
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

	/**
	 * Scripted access to the execution parameters.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ScriptableParameters implements IScriptable
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
		 */
		public String getName()
		{
			return "parameters"; //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
		 */
		public boolean hasValue()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
		 */
		public Object toValue()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#
		 *      getFunctionNames()
		 */
		public String[] getFunctionNames()
		{
			return new String[] {};
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
		 *      java.lang.String, java.lang.Object[])
		 */
		public Object invokeFunction(String name, Object[] arguments)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
		 */
		public boolean hasItem(int index)
		{
			return false;
		}

		public String[] getPropertyNames()
		{
			return context.getParameterNames();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasEntry(
		 *      java.lang.String)
		 */
		public boolean hasEntry(String name)
		{
			return context.getParameters(name) != null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
		 */
		public Object getItem(int index)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getEntry(
		 *      java.lang.String)
		 */
		public Object getEntry(String name)
		{
			return context.getParameters(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
		 *      java.lang.Object)
		 */
		public boolean setItem(int index, Object value)
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setEntry(
		 *      java.lang.String, java.lang.Object)
		 */
		public boolean setEntry(String name, Object value)
		{
			if (value instanceof Object[])
			{
				Object[] values = (Object[])value;
				String[] strings = new String[values.length];
				for (int i = 0; i < values.length; ++i)
					strings[i] = values[i] == null ? null : values[i].toString();
				context.setParameters(name, strings);
			}
			else
				context.setParameter(name, value == null ? null : value.toString());
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
		 */
		public boolean clearItem(int index)
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearEntry(
		 *      java.lang.String)
		 */
		public boolean clearEntry(String name)
		{
			context.clearParameter(name);
			return true;
		}

		@Override
		public boolean isMutable()
		{
			// TODO Auto-generated method stub
			return false;
		}
	}

	@Override
	public boolean isMutable()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
