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
 * A single of branded configuration for executing a script block.
 * 
 * @author Lonnie Pryor
 */
public class ExitConfiguration implements IConfiguration, CommonConstants
{
	/** The value the configured exit represents. */
	private String value = ""; //$NON-NLS-1$

	/**
	 * Creates a new ScriptItemConfiguration.
	 */
	public ExitConfiguration()
	{
	}

	/**
	 * Returns the value the configured exit represents.
	 * 
	 * @return The value the configured exit represents.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value the configured exit represents.
	 * 
	 * @param value The value the configured exit represents.
	 */
	public void setValue(String value)
	{
		this.value = value == null ? "" //$NON-NLS-1$
				: value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		value = configurationElement.getAttribute(NAME_VALUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute(NAME_VALUE, value);
	}
}
