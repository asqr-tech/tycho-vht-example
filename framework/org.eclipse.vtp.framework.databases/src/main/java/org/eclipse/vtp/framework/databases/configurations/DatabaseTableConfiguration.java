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
 * A configuration for a database table.
 * 
 * @author Lonnie Pryor
 */
public class DatabaseTableConfiguration implements IConfiguration,
		DatabaseConstants
{
	/** The name of the table. */
	private String name = ""; //$NON-NLS-1$
	/** The columns defined in the table. */
	private Set<DatabaseColumnConfiguration> columns = new LinkedHashSet<DatabaseColumnConfiguration>();

	/**
	 * Creates a new DatabaseTableConfiguration.
	 */
	public DatabaseTableConfiguration()
	{
	}

	/**
	 * Returns the name of the table.
	 * 
	 * @return The name of the table.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the table.
	 * 
	 * @param name The name of the table.
	 */
	public void setName(String name)
	{
		this.name = name == null ? "" : name; //$NON-NLS-1$
	}

	/**
	 * Returns the columns defined in the table.
	 * 
	 * @return The columns defined in the table.
	 */
	public DatabaseColumnConfiguration[] getColumns()
	{
		return columns.toArray(new DatabaseColumnConfiguration[columns.size()]);
	}

	/**
	 * Adds a column to the end of this table.
	 * 
	 * @param column The column to add.
	 */
	public void addColumn(DatabaseColumnConfiguration column)
	{
		if (column != null)
			columns.add(column);
	}

	/**
	 * Removes a column from this table.
	 * 
	 * @param column The column to remove.
	 */
	public void removeColumn(DatabaseColumnConfiguration column)
	{
		if (column != null)
			columns.remove(column);
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
		columns.clear();
		NodeList list = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_COLUMN);
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element)list.item(i);
			DatabaseColumnConfiguration item = new DatabaseColumnConfiguration();
			item.load(element);
			columns.add(item);
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
		String columnName = NAME_COLUMN;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
			columnName = prefix + ":" + columnName; //$NON-NLS-1$
		for (DatabaseColumnConfiguration item : columns)
		{
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, columnName);
			item.save(element);
			configurationElement.appendChild(element);
		}
	}
}
