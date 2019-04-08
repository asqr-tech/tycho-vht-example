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
package org.eclipse.vtp.framework.common.support;

import org.eclipse.vtp.framework.common.IScriptable;

/**
 * An implementation of a read-only scriptable array.
 * 
 * @author Lonnie Pryor
 */
public class ScriptableArray implements IScriptable
{
	/** The name of this array. */
	private final String name;
	/** The items in this array. */
	private final Object[] items;

	/**
	 * Creates a new ScriptableArray.
	 * 
	 * @param name The name of this array.
	 * @param items The items in this array.
	 */
	public ScriptableArray(String name, Object[] items)
	{
		this.name = name;
		this.items = items == null ? new IScriptable[0] : items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
	 */
	public String getName()
	{
		return name;
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
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getFunctionNames()
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
		return index >= 0 && index < items.length;
	}

	public String[] getPropertyNames()
	{
		return new String[] {"length"};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasEntry(
	 * java.lang.String)
	 */
	public boolean hasEntry(String name)
	{
		return "length".equals(name); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
	 */
	public Object getItem(int index)
	{
		return hasItem(index) ? items[index] : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getEntry(
	 * java.lang.String)
	 */
	public Object getEntry(String name)
	{
		return "length".equals(name) ? new Integer(items.length) : null; //$NON-NLS-1$
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
		return false;
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
		return false;
	}

	@Override
	public boolean isMutable()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
