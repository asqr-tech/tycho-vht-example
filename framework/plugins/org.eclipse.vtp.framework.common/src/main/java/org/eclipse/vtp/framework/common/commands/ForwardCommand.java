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
 * A command that tells the process engine to terminate the current process and
 * pass control to another process.
 * 
 * @author Lonnie Pryor
 */
public final class ForwardCommand extends ControllerCommand
{
	/** The URI identifying the process to transfer control to. */
	private String targetProcessURI = null;
	/** The variables that will be passed to the target process. */
	private final Map<String, String> variableMappings = new HashMap<String, String>();

	/**
	 * Creates a new ForwardCommand.
	 */
	public ForwardCommand()
	{
	}

	/**
	 * Returns the URI identifying the process to transfer control to.
	 * 
	 * @return The URI identifying the process to transfer control to.
	 */
	public String getTargetProcessURI()
	{
		return targetProcessURI;
	}

	/**
	 * Sets the URI identifying the process to transfer control to.
	 * 
	 * @param targetProcessURI The URI identifying the process to transfer control
	 *          to.
	 */
	public void setTargetProcessURI(String targetProcessURI)
	{
		this.targetProcessURI = targetProcessURI;
	}

	/**
	 * Returns the names of the variables that will be passed to the target
	 * process.
	 * 
	 * @return The names of the variables that will be passed to the target
	 *         process.
	 */
	public String[] getVariableNames()
	{
		return variableMappings.keySet().toArray(
				new String[variableMappings.size()]);
	}

	/**
	 * Returns the name of the variable in the current process that will be set as
	 * the specified variable in the target process.
	 * 
	 * @param targetVariableName The name of the variable in the target process.
	 * @return The name of the variable in the current process that will be set as
	 *         the specified variable in the target process.
	 */
	public String getVariableValue(String targetVariableName)
	{
		if (targetVariableName == null)
			return null;
		return variableMappings.get(targetVariableName);
	}

	/**
	 * Sets the name of the variable in the current process that will be set as
	 * the specified variable in the target process.
	 * 
	 * @param targetVariableName The name of the variable in the target process.
	 * @param localVariableName The name of the variable in the current process to
	 *          pass to the target process.
	 */
	public void setVariableValue(String targetVariableName,
			String localVariableName)
	{
		if (targetVariableName == null)
			return;
		if (localVariableName == null)
			variableMappings.remove(targetVariableName);
		else
			variableMappings.put(targetVariableName, localVariableName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.commands.ControllerCommand#accept(
	 *      org.eclipse.vtp.framework.spi.commands.IControllerCommandVisitor)
	 */
	Object accept(IControllerCommandVisitor visitor)
	{
		return visitor.visitForward(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#exportContents()
	 */
	public Object exportContents()
	{
		List<String> variableMappings = new ArrayList<String>(this.variableMappings.size() * 2);
		for (Map.Entry<String, String> entry : this.variableMappings.entrySet())
		{
			variableMappings.add(entry.getKey());
			variableMappings.add(entry.getValue());
		}
		return new Object[] { this.targetProcessURI,
				variableMappings.toArray(new String[variableMappings.size()]) };
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
		this.targetProcessURI = (String)array[0];
		this.variableMappings.clear();
		String[] variableMappings = (String[])array[1];
		for (int i = 0; i < variableMappings.length; i += 2)
			this.variableMappings.put(variableMappings[i], variableMappings[i + 1]);
	}
}
