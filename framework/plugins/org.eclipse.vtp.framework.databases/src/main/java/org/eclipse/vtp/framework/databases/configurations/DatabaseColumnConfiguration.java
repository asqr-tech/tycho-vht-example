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

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;

/**
 * A configuration for a column in a database table.
 * 
 * @author Lonnie Pryor
 */
public class DatabaseColumnConfiguration implements IConfiguration,
		DatabaseConstants
{
	/** The database long decimal type. */
	public static final int TYPE_BIG_DECIMAL = 1;
	/** The database long number type. */
	public static final int TYPE_BIG_NUMBER = 2;
	/** The database true/false type. */
	public static final int TYPE_BOOLEAN = 3;
	/** The database date and time type. */
	public static final int TYPE_DATETIME = 4;
	/** The database short decimal type. */
	public static final int TYPE_DECIMAL = 5;
	/** The database short number type. */
	public static final int TYPE_NUMBER = 6;
	/** The database long string type. */
	public static final int TYPE_TEXT = 7;
	/** The database short string type. */
	public static final int TYPE_VARCHAR = 8;

	/** The name of the column. */
	private String name = ""; //$NON-NLS-1$
	/** The type of the column. */
	private int type = -1;

	/**
	 * Creates a new DatabaseColumnConfiguration.
	 */
	public DatabaseColumnConfiguration()
	{
	}

	/**
	 * Returns the name of the column.
	 * 
	 * @return The name of the column.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the column.
	 * 
	 * @param name The name of the column.
	 */
	public void setName(String name)
	{
		this.name = name == null ? "" : name; //$NON-NLS-1$
	}

	/**
	 * Returns the type of the column.
	 * 
	 * @return The type of the column.
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Sets the type of the column.
	 * 
	 * @param type The type of the column.
	 */
	public void setType(int type)
	{
		this.type = type;
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
		String typeStr = configurationElement.getAttribute(NAME_TYPE);
		if ("big-decimal".equalsIgnoreCase(typeStr)) //$NON-NLS-1$
			type = TYPE_BIG_DECIMAL;
		else if ("big-number".equalsIgnoreCase(typeStr)) //$NON-NLS-1$
			type = TYPE_BIG_NUMBER;
		else if ("boolean".equalsIgnoreCase(typeStr)) //$NON-NLS-1$
			type = TYPE_BOOLEAN;
		else if ("datetime".equalsIgnoreCase(typeStr)) //$NON-NLS-1$
			type = TYPE_DATETIME;
		else if ("decimal".equalsIgnoreCase(typeStr)) //$NON-NLS-1$
			type = TYPE_DECIMAL;
		else if ("number".equalsIgnoreCase(typeStr)) //$NON-NLS-1$
			type = TYPE_NUMBER;
		else if ("text".equalsIgnoreCase(typeStr)) //$NON-NLS-1$
			type = TYPE_TEXT;
		else if ("varchar".equalsIgnoreCase(typeStr)) //$NON-NLS-1$
			type = TYPE_VARCHAR;
		else
			type = 0;
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
		switch (type)
		{
		case TYPE_BIG_DECIMAL:
			configurationElement.setAttribute(NAME_TYPE, "big-decimal"); //$NON-NLS-1$
			break;
		case TYPE_BIG_NUMBER:
			configurationElement.setAttribute(NAME_TYPE, "big-number"); //$NON-NLS-1$
			break;
		case TYPE_BOOLEAN:
			configurationElement.setAttribute(NAME_TYPE, "boolean"); //$NON-NLS-1$
			break;
		case TYPE_DATETIME:
			configurationElement.setAttribute(NAME_TYPE, "datetime"); //$NON-NLS-1$
			break;
		case TYPE_DECIMAL:
			configurationElement.setAttribute(NAME_TYPE, "decimal"); //$NON-NLS-1$
			break;
		case TYPE_NUMBER:
			configurationElement.setAttribute(NAME_TYPE, "number"); //$NON-NLS-1$
			break;
		case TYPE_TEXT:
			configurationElement.setAttribute(NAME_TYPE, "text"); //$NON-NLS-1$
			break;
		case TYPE_VARCHAR:
			configurationElement.setAttribute(NAME_TYPE, "varchar"); //$NON-NLS-1$
			break;
		}
	}
}
