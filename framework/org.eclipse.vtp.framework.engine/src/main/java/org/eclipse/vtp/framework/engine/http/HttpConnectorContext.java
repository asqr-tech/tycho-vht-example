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
package org.eclipse.vtp.framework.engine.http;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

/**
 * An {@link HttpContext} implementation that forwards invocations to an
 * {@link HttpConnector}.
 * 
 * @author Lonnie Pryor
 */
public class HttpConnectorContext implements HttpContext
{
	/** The default HTTP context. */
	private final HttpContext defaultContext;
	/** The {@link HttpConnector} instance to use. */
	private final HttpConnector connector;

	/**
	 * Creates a new HttpConnectorContext.
	 * 
	 * @param defaultContext The default HTTP context.
	 * @param connector The {@link HttpConnector} instance to use.
	 */
	public HttpConnectorContext(HttpContext defaultContext,
			HttpConnector connector)
	{

		this.defaultContext = defaultContext;
		this.connector = connector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.http.HttpContext#getMimeType(java.lang.String)
	 */
	public String getMimeType(String path)
	{
		String mimeType = connector.getMimeType(path);
		if (mimeType == null)
			mimeType = defaultContext.getMimeType(path);
		return mimeType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.http.HttpContext#getResource(java.lang.String)
	 */
	public URL getResource(String path)
	{
		URL resource = connector.getResource(path);
		if (resource == null)
			resource = defaultContext.getResource(path);
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.http.HttpContext#handleSecurity(
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public boolean handleSecurity(HttpServletRequest req, HttpServletResponse res)
			throws IOException
	{
		return defaultContext.handleSecurity(req, res);
	}
}
