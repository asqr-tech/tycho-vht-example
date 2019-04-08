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
 * A configuration for a database query result mapping.
 * 
 * @author Lonnie Pryor
 */
public class DatabaseMappingConfiguration implements
		IConfiguration, DatabaseConstants
{
	/** No action is taken for this mapping. */
	public static final int TYPE_NONE = 0;
	/** Use the value of a column in the result set. */
	public static final int TYPE_COLUMN = 1;
	/** Use a static value. */
	public static final int TYPE_STATIC = 2;

	/** The name of the field to map to. */
	private String name = ""; //$NON-NLS-1$
	/** The type of mapping to perform. */
	private int type = -1;
	/** The value to use for the mapping. */
	private String value = null;

	/**
	 * Creates a new DatabaseMappingConfiguration.
	 */
	public DatabaseMappingConfiguration()
	{
	}

	/**
	 * Returns the name of the field to map to.
	 *
	 * @return The name of the field to map to.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the field to map to.
	 * 
	 * @param name The name of the field to map to.
	 */
	public void setName(String name)
	{
		this.name = name == null ? "" : name; //$NON-NLS-1$
	}

	/**
	 * Returns the  type of mapping to perform.
	 *
	 * @return The  type of mapping to perform.
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Sets the  type of mapping to perform.
	 * 
	 * @param type The  type of mapping to perform.
	 */
	public void setType(int type)
	{
		this.type = type;
	}

	/**
	 * Returns the value to use for the mapping.
	 *
	 * @return The value to use for the mapping.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value to use for the mapping.
	 * 
	 * @param value The value to use for the mapping.
	 */
	public void setValue(String value)
	{
		this.value = value;
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
		if ("column".equalsIgnoreCase(typeStr)) //$NON-NLS-1$
			type = TYPE_COLUMN;
		else if ("static".equalsIgnoreCase(typeStr)) //$NON-NLS-1$
			type = TYPE_STATIC;
		else
			type = TYPE_NONE;
		if (configurationElement.hasAttribute(NAME_VALUE))
			value = configurationElement.getAttribute(NAME_VALUE);
		else
			value = null;
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
		case TYPE_COLUMN:
			configurationElement.setAttribute(NAME_TYPE, "column"); //$NON-NLS-1$
			break;
		case TYPE_STATIC:
			configurationElement.setAttribute(NAME_TYPE, "static"); //$NON-NLS-1$
			break;
		default:
			configurationElement.setAttribute(NAME_TYPE, "none"); //$NON-NLS-1$
		}
		if (value != null)
			configurationElement.setAttribute(NAME_VALUE, value);
	}
}
