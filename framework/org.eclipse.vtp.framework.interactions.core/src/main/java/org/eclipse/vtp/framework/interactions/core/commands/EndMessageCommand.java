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
import java.util.List;
import java.util.Map;


/**
 * A command that ends the conversation with the user.
 * 
 * @author Lonnie Pryor
 */
public final class EndMessageCommand extends ConversationCommand
{
	/** The parameters to set when the process resumes. */
	private final Map<String, String> variables = new HashMap<String, String>();

	/**
	 * Creates a new EndMessageCommand.
	 */
	public EndMessageCommand()
	{
	}

	/**
	 * getVariables.
	 * 
	 * @return
	 */
	public String[] getVariableNames()
	{
		return variables.keySet().toArray(new String[variables.size()]);
	}

	/**
	 * getVariable.
	 * 
	 * @param name
	 * @return
	 */
	public String getVariable(String name)
	{
		return variables.get(name);
	}

	/**
	 * addVariable.
	 * 
	 * @param name
	 * @return
	 */
	public boolean addVariable(String name, String value)
	{
		variables.put(name, value);
		return true;
	}

	/**
	 * removeVariable.
	 * 
	 * @param name
	 * @return
	 */
	public boolean removeVariable(String name)
	{
		return variables.remove(name) != null;
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
		return visitor.visitEndMessage(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#exportContents()
	 */
	public Object exportContents()
	{
		List<String> list = new ArrayList<String>(this.variables.size() * 2);
		for (Map.Entry<String, String> entry : this.variables.entrySet())
		{
			list.add(entry.getKey());
			list.add(entry.getValue());
		}
		return list.toArray(new String[list.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#importContents(
	 *      java.lang.Object)
	 */
	public void importContents(Object contents)
	{
		variables.clear();
		String[] array = (String[])contents;
		for (int i = 0; i < array.length; i += 2)
			this.variables.put(array[i], array[i + 1]);
	}
}
