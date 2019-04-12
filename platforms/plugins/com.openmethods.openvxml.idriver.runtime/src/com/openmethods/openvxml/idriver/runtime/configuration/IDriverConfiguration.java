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
package com.openmethods.openvxml.idriver.runtime.configuration;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;

/**
 * Configuration for a set of meta-data.
 * 
 * @author Lonnie Pryor
 */
public class IDriverConfiguration implements IConfiguration
{
	public static final String NAMESPACE_URI = "http://openvxml.org/xml/platforms/configurations";
	public static final String NAME_IDRIVER = "idriver-config";
	private static final String NAME_CALLID = "call-id";
	private static final String NAME_CONNID = "con-id";
	private static final String NAME_PORT = "port";

	String callIdVar;
	String connIdVar;
	String portVar;

	/**
	 * Creates a new MetaDataMessageConfiguration.
	 * 
	 */
	public IDriverConfiguration()
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		callIdVar = configurationElement.getAttribute(NAME_CALLID);
		connIdVar = configurationElement.getAttribute(NAME_CONNID);
		portVar = configurationElement.getAttribute(NAME_PORT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute(NAME_CALLID, callIdVar);
		configurationElement.setAttribute(NAME_CONNID, connIdVar);
		configurationElement.setAttribute(NAME_PORT, portVar);
	}

	public String getCallIdVariable()
	{
		return callIdVar;
	}

	public void setCallIdVariable(String callIdVar)
	{
		this.callIdVar = callIdVar;
	}

	public String getConnIdVariable()
	{
		return connIdVar;
	}

	public void setConnIdVariable(String connIdVar)
	{
		this.connIdVar = connIdVar;
	}

	public String getPortVariable()
	{
		return portVar;
	}

	public void setPortVariable(String portVar)
	{
		this.portVar = portVar;
	}
}
