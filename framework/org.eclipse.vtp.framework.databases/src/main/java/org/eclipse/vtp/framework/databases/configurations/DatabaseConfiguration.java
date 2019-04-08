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
package org.eclipse.vtp.framework.databases.configurations;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A configuration for a database connection factory.
 * 
 * @author Lonnie Pryor
 */
public abstract class DatabaseConfiguration implements IConfiguration,
		DatabaseConstants
{
	/** The name of this configuration. */
	private String name = ""; //$NON-NLS-1$
	/** The user name to connect with. */
	private String username = null;
	/** The password to connect with. */
	private String password = null;
	/** The tables defined in the database. */
	private Set<DatabaseTableConfiguration> tables = new LinkedHashSet<DatabaseTableConfiguration>();

	/**
	 * Creates a new DatabaseConfiguration.
	 */
	public DatabaseConfiguration()
	{
	}

	/**
	 * Returns the name of this configuration.
	 * 
	 * @return The name of this configuration.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this configuration.
	 * 
	 * @param name The name of this configuration.
	 */
	public void setName(String name)
	{
		this.name = name == null ? "" : name; //$NON-NLS-1$;
	}

	/**
	 * Returns the user name to connect with.
	 * 
	 * @return The user name to connect with.
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Sets the user name to connect with.
	 * 
	 * @param username The user name to connect with.
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * Returns the password to connect with.
	 * 
	 * @return The password to connect with.
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password to connect with.
	 * 
	 * @param password The password to connect with.
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Returns the tables defined in the database.
	 * 
	 * @return The tables defined in the database.
	 */
	public DatabaseTableConfiguration[] getTables()
	{
		return tables.toArray(new DatabaseTableConfiguration[tables.size()]);
	}

	/**
	 * Adds a table to the end of this database.
	 * 
	 * @param table The table to add.
	 */
	public void addTable(DatabaseTableConfiguration table)
	{
		if (table != null)
			tables.add(table);
	}

	/**
	 * Removes a table from this database.
	 * 
	 * @param table The table to remove.
	 */
	public void removeTable(DatabaseTableConfiguration table)
	{
		if (table != null)
			tables.remove(table);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(
	 *      org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		name = configurationElement.getAttribute(NAME_NAME);
		if (configurationElement.hasAttribute(NAME_USERNAME))
			username = configurationElement.getAttribute(NAME_USERNAME);
		else
			username = null;
		if (configurationElement.hasAttribute(NAME_PASSWORD))
			password = configurationElement.getAttribute(NAME_PASSWORD);
		else
			password = null;
		tables.clear();
		NodeList list = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_TABLE);
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element)list.item(i);
			DatabaseTableConfiguration item = new DatabaseTableConfiguration();
			item.load(element);
			tables.add(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(
	 *      org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute(NAME_NAME, name);
		if (username != null)
			configurationElement.setAttribute(NAME_USERNAME, username);
		if (password != null)
			configurationElement.setAttribute(NAME_PASSWORD, password);
		String tableName = NAME_TABLE;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
			tableName = prefix + ":" + tableName; //$NON-NLS-1$
		for (DatabaseTableConfiguration item : tables)
		{
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, tableName);
			item.save(element);
			configurationElement.appendChild(element);
		}
	}
}
