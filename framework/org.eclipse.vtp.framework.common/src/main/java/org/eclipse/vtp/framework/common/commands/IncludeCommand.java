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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A command that tells the process engine to suspend the current process and
 * pass control to another process, resuming the current process when the target
 * process completes.
 * 
 * @author Lonnie Pryor
 */
public final class IncludeCommand extends ControllerCommand
{
	/** The URI identifying the process to transfer control to. */
	private String targetProcessURI = null;
	/** The variables that will be passed to the target process. */
	private final Map<String, String> variableMappings = new HashMap<String, String>();
	/** The variables that will be passed back from the target process. */
	private final Map<String, Map<String, String>> outgoingData = new HashMap<String, Map<String, String>>();
	/** The parameters to set when the current process resumes. */
	private final Map<String, List<String>> parameters = new HashMap<String, List<String>>();

	/**
	 * Creates a new IncludeCommand.
	 */
	public IncludeCommand()
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

	public String[] getOutgoingPaths()
	{
		return outgoingData.keySet().toArray(
				new String[outgoingData.size()]);
	}

	public String[] getOutgoingDataNames(String path)
	{
		Map<String, String> map = outgoingData.get(path);
		if (map == null)
			return new String[0];
		return map.keySet().toArray(new String[map.size()]);
	}

	public String getOutgoingDataValue(String path, String name)
	{
		Map<String, String> map = outgoingData.get(path);
		if (map == null)
			return null;
		return map.get(name);
	}

	public void setOutgoingDataValue(String path, String name, String value)
	{
		if (path == null || name == null)
			return;
		Map<String, String> map = outgoingData.get(path);
		if (map == null)
		{
			if (value == null)
				return;
			outgoingData.put(path, map = new HashMap<String, String>());
		}
		if (value == null)
		{
			map.remove(name);
			if (map.isEmpty())
				outgoingData.remove(path);
		}
		else
			map.put(name, value);
	}

	/**
	 * Returns the names of the parameters that will be returned from the target
	 * process.
	 * 
	 * @return The names of the parameters that will be returned from the target
	 *         process.
	 */
	public String[] getParameterNames()
	{
		return parameters.keySet().toArray(new String[parameters.size()]);
	}

	/**
	 * Returns the values of a parameter to be set when the current process
	 * resumes.
	 * 
	 * @param name The name of the parameter to be set.
	 * @return The values that specified parameter will be set to.
	 */
	public String[] getParameterValues(String name)
	{
		if (name == null)
			return null;
		List<String> list = parameters.get(name);
		if (list == null)
			return null;
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Configures a parameter set when the current process resumes.
	 * 
	 * @param name The name of the parameter to set.
	 * @param values The values to set the parameter to.
	 */
	public void setParameterValues(String name, String[] values)
	{
		if (name == null)
			return;
		if (values == null)
			parameters.remove(name);
		else
		{
			List<String> list = parameters.get(name);
			if (list == null)
				parameters.put(name, list = new LinkedList<String>());
			else
				list.clear();
			for (int i = 0; i < values.length; ++i)
				if (values[i] != null)
					list.add(values[i]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.commands.ControllerCommand#accept(
	 *      org.eclipse.vtp.framework.spi.commands.IControllerCommandVisitor)
	 */
	Object accept(IControllerCommandVisitor visitor)
	{
		return visitor.visitInclude(this);
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
		List<Object> outgoingData = new ArrayList<Object>(this.outgoingData.size() * 2);
		for (Map.Entry<String, Map<String, String>> entry : this.outgoingData.entrySet())
		{
			outgoingData.add(entry.getKey());
			Map<String, String> map = entry.getValue();
			List<String> entryData = new ArrayList<String>(map.size() * 2);
			for (Map.Entry<String, String> e : map.entrySet())
			{
				entryData.add(e.getKey());
				entryData.add(e.getValue());
			}
			outgoingData.add(entryData.toArray(new String[entryData.size()]));
		}
		List<Object> parameters = new ArrayList<Object>(this.parameters.size() * 2);
		for (Map.Entry<String, List<String>> entry : this.parameters.entrySet())
		{
			parameters.add(entry.getKey());
			parameters.add(entry.getValue().toArray(new String[entry.getValue().size()]));
		}
		return new Object[] { this.targetProcessURI,
				variableMappings.toArray(new String[variableMappings.size()]),
				outgoingData.toArray(),
				parameters.toArray() };
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
		this.outgoingData.clear();
		Object[] outgoingData = (Object[])array[2];
		for (int i = 0; i < outgoingData.length; i += 2)
		{
			String path = (String)outgoingData[i];
			String[] entries = (String[])outgoingData[i + 1];
			for (int j = 0; j < entries.length; j += 2)
				setOutgoingDataValue(path, entries[i], entries[i + 1]);
		}
		this.parameters.clear();
		Object[] parameters = (Object[])array[3];
		for (int i = 0; i < parameters.length; i += 2)
			this.parameters.put((String)parameters[i],
					new LinkedList<String>(Arrays.asList((String[])parameters[i + 1])));
	}
}
