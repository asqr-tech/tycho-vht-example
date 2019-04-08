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
package org.eclipse.vtp.framework.common.configurations;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;

/**
 * A configuration for a field of a data type.
 * 
 * @author Lonnie Pryor
 */
public class FieldConfiguration implements IConfiguration,
		CommonConstants
{
	/** The name of this field. */
	private String name = ""; //$NON-NLS-1$
	/** The type of this field. */
	private String type = ""; //$NON-NLS-1$
	/** The initial value of this field when an object is created. */
	private String initialValue = null;
	private boolean secured = false;

	/**
	 * Creates a new DataTypeConfiguration.
	 */
	public FieldConfiguration()
	{
	}

	/**
	 * Returns the name of this field.
	 * 
	 * @return The name of this field.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this field.
	 * 
	 * @param name The name of this field.
	 */
	public void setName(String name)
	{
		this.name = name == null ? "" : name; //$NON-NLS-1$
	}

	/**
	 * Returns the type of this field.
	 * 
	 * @return The type of this field.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Sets the type of this field.
	 * 
	 * @param type The type of this field.
	 */
	public void setType(String type)
	{
		this.type = type == null ? "" : type; //$NON-NLS-1$
	}

	/**
	 * Returns the initial value of this field when an object is created.
	 * 
	 * @return The initial value of this field when an object is created.
	 */
	public String getInitialValue()
	{
		return initialValue;
	}

	/**
	 * Sets the initial value of this field when an object is created.
	 * 
	 * @param initialValue The initial value of this field when an object is
	 *          created.
	 */
	public void setInitialValue(String initialValue)
	{
		this.initialValue = initialValue;
	}
	
	public boolean isSecured()
	{
		return secured;
	}
	
	public void setSecured(boolean secured)
	{
		this.secured = secured;
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
		type = configurationElement.getAttribute(NAME_TYPE);
		if (configurationElement.hasAttribute(NAME_INITIAL_VALUE))
			initialValue = configurationElement.getAttribute(NAME_INITIAL_VALUE);
		else
			initialValue = null;
		secured = Boolean.parseBoolean(configurationElement.getAttribute(NAME_SECURED));
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
		configurationElement.setAttribute(NAME_TYPE, type);
		if (initialValue != null)
			configurationElement.setAttribute(NAME_INITIAL_VALUE, initialValue);
		configurationElement.setAttribute(NAME_SECURED, Boolean.toString(secured));
	}
}
