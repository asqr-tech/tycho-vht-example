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
 * A configuration for a variable assignment.
 * 
 * @author Lonnie Pryor
 */
public class AssignmentConfiguration implements IConfiguration, CommonConstants
{
	/** The name of the variable to assign to. */
	private String name = ""; //$NON-NLS-1$
	/** The type of the variable to assign to. */
	private String type = ""; //$NON-NLS-1$
	/** The value to assign to the variable. */
	private String value = null;
	private boolean secured = false;

	/**
	 * Creates a new AssignmentConfiguration.
	 */
	public AssignmentConfiguration()
	{
	}

	/**
	 * Returns the name of the variable to assign to.
	 * 
	 * @return The name of the variable to assign to.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the variable to assign to.
	 * 
	 * @param name The name of the variable to assign to.
	 */
	public void setName(String name)
	{
		this.name = name == null ? "" : name; //$NON-NLS-1$;
	}

	/**
	 * Returns the type of the variable to assign to.
	 * 
	 * @return The type of the variable to assign to.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Sets the type of the variable to assign to.
	 * 
	 * @param type The type of the variable to assign to.
	 */
	public void setType(String type)
	{
		this.type = type == null ? "" : type; //$NON-NLS-1$
	}

	/**
	 * Returns the value to assign to the variable.
	 * 
	 * @return The value to assign to the variable.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value to assign to the variable.
	 * 
	 * @param value The value to assign to the variable.
	 */
	public void setValue(String value)
	{
		this.value = value;
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
		if (configurationElement.hasAttribute(NAME_VALUE))
			value = configurationElement.getAttribute(NAME_VALUE);
		else
			value = null;
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
		if (value != null)
			configurationElement.setAttribute(NAME_VALUE, value);
		configurationElement.setAttribute(NAME_SECURED, Boolean.toString(secured));
	}
}
