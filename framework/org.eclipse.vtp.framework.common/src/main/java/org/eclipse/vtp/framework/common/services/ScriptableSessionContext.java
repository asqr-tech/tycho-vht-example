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
import org.eclipse.vtp.framework.core.ISessionContext;

/**
 * An {@link IScriptable} implementation that makes the {@link ISessionContext}
 * instance available as a scripting object.
 * 
 * <p>
 * This service will make available a "Session" object to all scripts in its
 * scope. The variable supports the following properties:
 * <ul>
 * <li><code>id</code>: a string containing the session ID</li>
 * <li><code>attributes</code>: an object containing the session attributes</li>
 * </ul>
 * The attributes object listed above will have a property for each session
 * attribute defined in the session context. New attributes may be added by
 * assigning to non-existent properties of the object. The attributes object
 * will not have an implicit value.
 * </p>
 * 
 * <p>
 * The "Session" scripting object uses the session ID as the implicit value,
 * thus it can be compared to other string objects.
 * </p>
 * 
 * @author Lonnie Pryor
 * @see ISessionContext
 */
public class ScriptableSessionContext implements IScriptable
{
	/** The context to provide scripting services for. */
	private final ISessionContext context;
	/** The scripted view of the session attributes. */
	private final ScriptableAttributes attributes = new ScriptableAttributes();

	/**
	 * Creates a new ScriptableSessionContext.
	 * 
	 * @param context The context to provide scripting services for.
	 */
	public ScriptableSessionContext(ISessionContext context)
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
		return "Session"; //$NON-NLS-1$
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
		propNames.add("startTime");
		for(String prop : attributes.getPropertyNames())
		{
			propNames.add(prop);
		}
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
		return "id".equals(name) || attributes.getName().equals(name) || "startTime".equals(name); //$NON-NLS-1$
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
			return context.getSessionID();
		if (attributes.getName().equals(name))
			return attributes;
		if ("startTime".equals(name))
			return context.getSessionStartTime();
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
	 * Scripted access to the session attributes.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ScriptableAttributes implements IScriptable
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
		 */
		public String getName()
		{
			return "attributes"; //$NON-NLS-1$
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
			return context.getAttributeNames();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasEntry(
		 *      java.lang.String)
		 */
		public boolean hasEntry(String name)
		{
			return context.getAttribute(name) != null;
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
			return context.getAttribute(name);
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
			context.setAttribute(name, value);
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
			context.clearAttribute(name);
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
