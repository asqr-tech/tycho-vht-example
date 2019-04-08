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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A command that requests meta-data from the user.
 * 
 * @author Lonnie Pryor
 */
public final class MetaDataRequestCommand extends ConversationCommand
{
	/** The name of the parameter to pass the provided data as. */
	private String dataName = null;
	/** The name of the parameter to pass the result of the request as. */
	private String resultName = null;
	/** The value of the result parameter to pass if the input is valid. */
	private String filledResultValue = null;
	/** The value of the result parameter to pass if the caller hungup. */
	private String hangupResultValue = null;
	/** The meta-data in the interaction. */
	private final Set metaData = new HashSet();
	/** The parameters to set when the process resumes. */
	private final Map parameters = new HashMap();

	/**
	 * Creates a new MetaDataMessageCommand.
	 */
	public MetaDataRequestCommand()
	{
	}

	/**
	 * Returns the name of the parameter to pass the provided data as.
	 * 
	 * @return The name of the parameter to pass the provided data as.
	 */
	public String getDataName()
	{
		return dataName;
	}

	/**
	 * Sets the name of the parameter to pass the provided data as.
	 * 
	 * @param inputName The name of the parameter to pass the provided data as.
	 */
	public void setDataName(String inputName)
	{
		this.dataName = inputName;
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
		return (String[])metaData.toArray(new String[metaData.size()]);
	}

	/**
	 * Adds a meta-data item to the interaction.
	 * 
	 * @param name The name of the meta-data item to add.
	 */
	public boolean addMetaDataName(String name)
	{
		if (name == null)
			return false;
		return metaData.add(name);
	}

	/**
	 * Removes a meta-data item from the interaction.
	 * 
	 * @param name The name of the meta-data item to remove.
	 */
	public boolean removeMetaDataName(String name)
	{
		if (name == null)
			return false;
		return metaData.remove(name);
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
		return visitor.visitMetaDataRequest(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#exportContents()
	 */
	public Object exportContents()
	{
		List parameters = new ArrayList(this.parameters.size() * 2);
		for (Iterator i = this.parameters.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			parameters.add((String)entry.getKey());
			parameters.add((String)entry.getValue());
		}
		return new Object[] { dataName, resultName, filledResultValue,
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
		this.dataName = (String)array[0];
		this.resultName = (String)array[1];
		this.filledResultValue = (String)array[2];
		this.metaData.clear();
		this.metaData.addAll(Arrays.asList( (String[])array[3]));
		this.parameters.clear();
		String[] parameters = (String[])array[4];
		for (int i = 0; i < parameters.length; i += 2)
			this.parameters.put(parameters[i], parameters[i + 1]);
	}
}
