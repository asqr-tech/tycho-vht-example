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

import org.w3c.dom.Element;

/**
 * A configuration for a database connection factory that uses the JDBC driver
 * manager.
 * 
 * @author Lonnie Pryor
 */
public class JdbcDatabaseConfiguration extends DatabaseConfiguration
{
	/** The name of the driver class. */
	private String driver = ""; //$NON-NLS-1$
	/** The URL to connect with. */
	private String url = ""; //$NON-NLS-1$

	/**
	 * Creates a new JdbcDatabaseConfiguration.
	 */
	public JdbcDatabaseConfiguration()
	{
	}

	/**
	 * Returns the name of the driver class.
	 * 
	 * @return The name of the driver class.
	 */
	public String getDriver()
	{
		return driver;
	}

	/**
	 * Sets the name of the driver class.
	 * 
	 * @param driver The name of the driver class.
	 */
	public void setDriver(String driver)
	{
		this.driver = driver == null ? "" : driver; //$NON-NLS-1$
	}

	/**
	 * Returns the URL to connect with.
	 * 
	 * @return The URL to connect with.
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * Sets the URL to connect with.
	 * 
	 * @param url The URL to connect with.
	 */
	public void setUrl(String url)
	{
		this.url = url == null ? "" : url; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.databases.configurations.
	 *      DatabaseConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		driver = configurationElement.getAttribute(NAME_DRIVER);
		url = configurationElement.getAttribute(NAME_URL);
		super.load(configurationElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.databases.configurations.
	 *      DatabaseConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute(NAME_DRIVER, driver);
		configurationElement.setAttribute(NAME_URL, url);
		super.save(configurationElement);
	}

}
