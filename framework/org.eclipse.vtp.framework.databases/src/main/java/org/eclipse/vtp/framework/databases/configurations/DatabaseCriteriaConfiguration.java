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
 * A configuration for a database query criteria.
 * 
 * @author Lonnie Pryor
 */
public class DatabaseCriteriaConfiguration implements IConfiguration,
		DatabaseConstants
{
	/** Compare for equality. */
	public static final int COMPARISON_EQUAL = 0;
	/** Compare for lack of equality. */
	public static final int COMPARISON_NOT_EQUAL = 1;
	/** Compare for less than. */
	public static final int COMPARISON_LESS_THAN = 2;
	/** Compare for less than or equality. */
	public static final int COMPARISON_LESS_THAN_OR_EQUAL = 3;
	/** Compare for greater than. */
	public static final int COMPARISON_GREATER_THAN = 4;
	/** Compare for greater than or equality. */
	public static final int COMPARISON_GREATER_THAN_OR_EQUAL = 5;
	/** No requirement exists for this criteria. */
	public static final int TYPE_NONE = 0;
	/** Use the value of a variable to match against. */
	public static final int TYPE_VARIABLE = 1;
	/** Use a static value to match against. */
	public static final int TYPE_STATIC = 2;

	/** The name of the column to constrain. */
	private String name = ""; //$NON-NLS-1$
	/** The type of comparison to perform. */
	private int comparison = -1;
	/** The type of constraint to apply. */
	private int type = -1;
	/** The value to match against. */
	private String value = null;

	/**
	 * Creates a new DatabaseCriteriaConfiguration.
	 */
	public DatabaseCriteriaConfiguration()
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
	 * Returns the type of comparison to perform.
	 * 
	 * @return The type of comparison to perform.
	 */
	public int getComparison()
	{
		return comparison;
	}

	/**
	 * Sets the type of comparison to perform.
	 * 
	 * @param comparison The type of comparison to perform.
	 */
	public void setComparison(int comparison)
	{
		this.comparison = comparison;
	}

	/**
	 * Returns the type of mapping to perform.
	 * 
	 * @return The type of mapping to perform.
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Sets the type of mapping to perform.
	 * 
	 * @param type The type of mapping to perform.
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
		String comparisonStr = configurationElement.getAttribute(NAME_COMPARISON);
		if ("not-equal".equalsIgnoreCase(comparisonStr)) //$NON-NLS-1$
			comparison = COMPARISON_NOT_EQUAL;
		else if ("less-than".equalsIgnoreCase(comparisonStr)) //$NON-NLS-1$
			comparison = COMPARISON_LESS_THAN;
		else if ("less-than-or-equal".equalsIgnoreCase(comparisonStr)) //$NON-NLS-1$
			comparison = COMPARISON_LESS_THAN_OR_EQUAL;
		else if ("greater-than".equalsIgnoreCase(comparisonStr)) //$NON-NLS-1$
			comparison = COMPARISON_GREATER_THAN;
		else if ("greater-than-or-equal".equalsIgnoreCase(comparisonStr)) //$NON-NLS-1$
			comparison = COMPARISON_GREATER_THAN_OR_EQUAL;
		else
			comparison = COMPARISON_EQUAL;
		String typeStr = configurationElement.getAttribute(NAME_TYPE);
		if ("variable".equalsIgnoreCase(typeStr)) //$NON-NLS-1$
			type = TYPE_VARIABLE;
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
		switch (comparison)
		{
		case COMPARISON_NOT_EQUAL:
			configurationElement.setAttribute(NAME_COMPARISON, "not-equal"); //$NON-NLS-1$
			break;
		case COMPARISON_LESS_THAN:
			configurationElement.setAttribute(NAME_COMPARISON, "less-than"); //$NON-NLS-1$
			break;
		case COMPARISON_LESS_THAN_OR_EQUAL:
			configurationElement.setAttribute(NAME_COMPARISON, "less-than-or-equal"); //$NON-NLS-1$
			break;
		case COMPARISON_GREATER_THAN:
			configurationElement.setAttribute(NAME_COMPARISON, "greater-than"); //$NON-NLS-1$
			break;
		case COMPARISON_GREATER_THAN_OR_EQUAL:
			configurationElement.setAttribute(NAME_COMPARISON,
					"greater-than-or-equal"); //$NON-NLS-1$
			break;
		default:
			configurationElement.setAttribute(NAME_COMPARISON, "equal"); //$NON-NLS-1$
		}
		switch (type)
		{
		case TYPE_VARIABLE:
			configurationElement.setAttribute(NAME_TYPE, "variable"); //$NON-NLS-1$
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
