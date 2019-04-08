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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An {@link HttpServlet} implementation that forwards invocations to an
 * {@link HttpConnector}.
 * 
 * @author Lonnie Pryor
 * @version 1.0
 * @since 1.0
 */
public class HttpConnectorServlet extends HttpServlet
{
	/** The resource buffer size. */
	private static final int BUFFER_SIZE = 8192;
	/** The serialization version. */
	private static final long serialVersionUID = 1L;

	/** The {@link HttpConnector} instance to use. */
	private final HttpConnector connector;

	/**
	 * Creates a new HttpConnector.
	 * 
	 * @param engine The {@link HttpConnector} instance to use.
	 */
	public HttpConnectorServlet(HttpConnector connector)
	{
		this.connector = connector;
	}

	/**
	 * Process an HTTP GET or POST request.
	 * 
	 * @param req The HTTP request object.
	 * @param res The HTTP response object.
	 * @throws ServletException If the method fails to complete.
	 * @throws IOException If the network connection fails.
	 */
	protected void process(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		String pathInfo = HttpUtils.normalizePath(req.getPathInfo());
		if (pathInfo.startsWith(HttpConnector.RESOURCES_PATH))
		{
			String resourcePath = pathInfo.substring(HttpConnector.RESOURCES_PATH
					.length());
			URL resource = connector.getResource(resourcePath);
			if (resource == null)
				resource = getServletContext().getResource(resourcePath);
			if (resource == null)
			{
				res.sendError(HttpServletResponse.SC_NOT_FOUND, resourcePath);
				return;
			}
			res.setStatus(HttpServletResponse.SC_OK);
			String mimeType = connector.getMimeType(resourcePath);
			if (mimeType == null)
				mimeType = getServletContext().getMimeType(resourcePath);
			if (mimeType != null)
				res.setContentType(mimeType);
			OutputStream output = null;
			try
			{
				output = res.getOutputStream();
				InputStream input = null;
				try
				{
					input = resource.openStream();
					byte[] b = new byte[BUFFER_SIZE];
					for (int i = input.read(b); i >= 0; i = input.read(b))
						output.write(b, 0, i);
				}
				finally
				{
					try
					{
						if (input != null)
							input.close();
					}
					catch (IOException e)
					{
					}
				}
				output.flush();
				res.flushBuffer();
			}
			finally
			{
				try
				{
					if (output != null)
						output.close();
				}
				catch (IOException e)
				{
				}
			}
			return;
		}
		else if (pathInfo.startsWith(HttpConnector.EXAMINE_PATH)) {
			connector.examine(req, res);
			return;
		}
		connector.process(req, res);
	}
	
	

    protected void doHead(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException
    {
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected final void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		try
		{
			process(req, res);
		}
		catch (Exception e)
		{
			log(e.getMessage(), e);
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
					.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected final void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		try
		{
			process(req, res);
		}
		catch (Exception e)
		{
			log(e.getMessage(), e);
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
					.getMessage());
		}
	}
}
