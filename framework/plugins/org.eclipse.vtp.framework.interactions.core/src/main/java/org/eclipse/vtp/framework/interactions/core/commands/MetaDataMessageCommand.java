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
package org.eclipse.vtp.framework.interactions.core.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A command that sends meta-data to the user.
 * 
 * @author Lonnie Pryor
 */
public final class MetaDataMessageCommand extends ConversationCommand
{
	/** The name of the parameter to pass the result of the request as. */
	private String resultName = null;
	/** The value of the result parameter to pass if the input is valid. */
	private String filledResultValue = null;
	/** The value of the result parameter to pass if the caller hungup. */
	private String hangupResultValue = null;
	/** The meta-data in the interaction. */
	private final Map metaData = new HashMap();
	/** The parameters to set when the process resumes. */
	private final Map parameters = new HashMap();
	private boolean ignoreErrors = false;

	/**
	 * Creates a new MetaDataMessageCommand.
	 */
	public MetaDataMessageCommand()
	{
	}

	/**
	 * Returns the name of the parameter to pass the result of the request as.
	 * 
	 * @return The name of the parameter to pass the result of the request as.
	 */
	public String getResultName()
	{
		return resultName;
	}

	/**
	 * Sets the name of the parameter to pass the result of the request as.
	 * 
	 * @param resultName The name of the parameter to pass the result of the
	 *          request as.
	 */
	public void setResultName(String resultName)
	{
		this.resultName = resultName;
	}

	/**
	 * Returns the value of the result parameter to pass if the input is valid.
	 * 
	 * @return The value of the result parameter to pass if the input is valid.
	 */
	public String getFilledResultValue()
	{
		return filledResultValue;
	}

	/**
	 * Sets the value of the result parameter to pass if the input is valid.
	 * 
	 * @param filledResultValue The value of the result parameter to pass if the
	 *          input is valid.
	 */
	public void setFilledResultValue(String filledResultValue)
	{
		this.filledResultValue = filledResultValue;
	}

	/**
	 * Returns the value of the result parameter to pass if the caller hungup.
	 * 
	 * @return The value of the result parameter to pass if the caller hungup.
	 */
	public String getHangupResultValue()
	{
		return hangupResultValue;
	}

	/**
	 * Sets the value of the result parameter to pass if the caller hungup.
	 * 
	 * @param noInputResultValue The value of the result parameter to pass if the
	 *          caller hungup.
	 */
	public void setHangupResultValue(String hangupResultValue)
	{
		this.hangupResultValue = hangupResultValue;
	}

	/**
	 * Returns the names of the meta-data in the interaction.
	 * 
	 * @return The names of the meta-data in the interaction.
	 */
	public String[] getMetaDataNames()
	{
		return (String[])metaData.keySet().toArray(new String[metaData.size()]);
	}

	/**
	 * Returns the value of a meta-data item in the interaction.
	 * 
	 * @param name The name of the meta-data item to be set.
	 * @return The value that the specified meta-data item will be set to.
	 */
	public String getMetaDataValue(String name)
	{
		if (name == null)
			return null;
		return (String)metaData.get(name);
	}

	/**
	 * Configures a meta-data item in the interaction.
	 * 
	 * @param name The name of the meta-data item to set.
	 * @param value The value to set the meta-data item to.
	 */
	public void setMetaDataValue(String name, String value)
	{
		if (name == null)
			return;
		if (value == null)
			metaData.remove(name);
		else
			metaData.put(name, value);
	}

	/**
	 * Returns the names of the parameters that will be returned from the
	 * interaction.
	 * 
	 * @return The names of the parameters that will be returned from the
	 *         interaction.
	 */
	public String[] getParameterNames()
	{
		return (String[])parameters.keySet().toArray(new String[parameters.size()]);
	}

	/**
	 * Returns the values of a parameter to be set when the process resumes.
	 * 
	 * @param name The name of the parameter to be set.
	 * @return The values that specified parameter will be set to.
	 */
	public String[] getParameterValues(String name)
	{
		if (name == null)
			return null;
		List list = (List)parameters.get(name);
		if (list == null)
			return null;
		return (String[])list.toArray(new String[list.size()]);
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
			List list = (List)parameters.get(name);
			if (list == null)
				parameters.put(name, list = new LinkedList());
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
	 * @see org.eclipse.vtp.framework.interactions.core.commands.
	 *      ConversationCommand#accept(
	 *      org.eclipse.vtp.framework.interactions.core.commands.
	 *      IConversationCommandVisitor)
	 */
	Object accept(IConversationCommandVisitor visitor)
	{
		return visitor.visitMetaDataMessage(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#exportContents()
	 */
	public Object exportContents()
	{
		List metaData = new ArrayList(this.metaData.size() * 2);
		for (Iterator i = this.metaData.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			metaData.add((String)entry.getKey());
			metaData.add((String)entry.getValue());
		}
		List parameters = new ArrayList(this.parameters.size() * 2);
		for (Iterator i = this.parameters.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			parameters.add((String)entry.getKey());
			parameters.add((String)entry.getValue());
		}
		return new Object[] { resultName, filledResultValue, Boolean.toString(ignoreErrors),
				metaData.toArray(new String[metaData.size()]),
				parameters.toArray(new String[parameters.size()]) };
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
		this.resultName = (String)array[0];
		this.filledResultValue = (String)array[1];
		this.ignoreErrors = Boolean.parseBoolean((String)array[2]);
		this.metaData.clear();
		String[] metaData = (String[])array[3];
		for (int i = 0; i < metaData.length; i += 2)
			this.metaData.put(metaData[i], metaData[i + 1]);
		this.parameters.clear();
		String[] parameters = (String[])array[4];
		for (int i = 0; i < parameters.length; i += 2)
			this.parameters.put(parameters[i], parameters[i + 1]);
	}

	public boolean isIgnoreErrors()
    {
    	return ignoreErrors;
    }

	public void setIgnoreErrors(boolean ignoreErrors)
    {
    	this.ignoreErrors = ignoreErrors;
    }
}
