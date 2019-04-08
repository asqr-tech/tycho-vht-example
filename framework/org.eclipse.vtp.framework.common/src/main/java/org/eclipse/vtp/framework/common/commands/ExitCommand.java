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
package org.eclipse.vtp.framework.common.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A command that terminates an included process and returns control to the
 * originating process.
 * 
 * @author Lonnie Pryor
 */
public final class ExitCommand extends ControllerCommand
{
	/** The value of this exit command. */
	private String exitValue = null;
	/** The variables that will be passed to the target process. */
	private final Map<String, Object> variables = new HashMap<String, Object>();

	/**
	 * Creates a new ExitCommand.
	 */
	public ExitCommand()
	{
	}

	/**
	 * Returns the value of this exit command.
	 * 
	 * @return The value of this exit command.
	 */
	public String getExitValue()
	{
		return exitValue;
	}

	/**
	 * Sets the value of this exit command.
	 * 
	 * @param exitValue The value of this exit command.
	 */
	public void setExitValue(String exitValue)
	{
		this.exitValue = exitValue;
	}

	public String[] getVariableNames()
	{
		return variables.keySet().toArray(new String[variables.size()]);
	}

	public Object getVariableValue(String name)
	{
		if (name == null)
			return null;
		return variables.get(name);
	}

	public void setVariableValues(String name, Object value)
	{
		if (name == null)
			return;
		if (value == null)
			variables.remove(name);
		else
			variables.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.commands.ControllerCommand#accept(
	 *      org.eclipse.vtp.framework.spi.commands.IControllerCommandVisitor)
	 */
	Object accept(IControllerCommandVisitor visitor)
	{
		return visitor.visitExit(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#exportContents()
	 */
	public Object exportContents()
	{
		List<Object> variables = new ArrayList<Object>(this.variables.size() * 2);
		for (Map.Entry<String, Object> entry : this.variables.entrySet())
		{
			variables.add(entry.getKey());
			variables.add(entry.getValue());
		}
		return new Object[] { exitValue, variables.toArray() };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#importContents(
	 *      java.lang.Object)
	 */
	public void importContents(Object contents)
	{
		Object[] array = (Object[])contents;
		exitValue = (String)array[0];
		this.variables.clear();
		Object[] variables = (Object[])array[1];
		for (int i = 0; i < variables.length; i += 2)
			this.variables.put((String)variables[i], variables[i + 1]);
	}
}
