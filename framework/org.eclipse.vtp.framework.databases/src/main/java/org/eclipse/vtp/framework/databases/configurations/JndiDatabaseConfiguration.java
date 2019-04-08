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
 * A configuration for a database connection factory that uses the JNDI lookup.
 * 
 * @author Lonnie Pryor
 */
public class JndiDatabaseConfiguration extends DatabaseConfiguration
{
	/** The URI to connect with. */
	private String uri = ""; //$NON-NLS-1$

	/**
	 * Creates a new JdbcDatabaseConfiguration.
	 */
	public JndiDatabaseConfiguration()
	{
	}

	/**
	 * Returns the URI to connect with.
	 * 
	 * @return The URI to connect with.
	 */
	public String getUri()
	{
		return uri;
	}

	/**
	 * Sets the URI to connect with.
	 * 
	 * @param uri The URI to connect with.
	 */
	public void setUri(String uri)
	{
		this.uri = uri == null ? "" : uri; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.databases.configurations.
	 *      DatabaseConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		uri = configurationElement.getAttribute(NAME_URI);
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
		configurationElement.setAttribute(NAME_URI, uri);
		super.save(configurationElement);
	}

}
