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
package org.eclipse.vtp.framework.engine.support;

import java.util.Date;
import java.util.Dictionary;

import org.eclipse.vtp.framework.core.ISessionContext;

/**
 * A wrapper for the {@link ISessionContext} interface.
 * 
 * @author Lonnie Pryor
 */
public abstract class SessionContextWrapper implements ISessionContext
{
	/**
	 * Creates a new SessionContextWrapper.
	 */
	protected SessionContextWrapper()
	{
	}

	/**
	 * Returns true if the specified identifier is implemented by the context.
	 * 
	 * @param identifier The identifier to check.
	 * @return True if the specified identifier is implemented by the context.
	 */
	protected boolean isReservedIdentifier(String identifier)
	{
		return AbstractSessionContext.RESERVED_SERVICE_IDENTIFIERS
				.contains(identifier);
	}

	/**
	 * Returns the wrapped session context.
	 * 
	 * @return The wrapped session context.
	 */
	protected abstract ISessionContext getSessionContext();

	// IReporter Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IReporter#isSeverityEnabled(int)
	 */
	public boolean isSeverityEnabled(int severity)
	{
		return getSessionContext().isSeverityEnabled(severity);
	}
	
	public boolean isReportingEnabled()
	{
		return getSessionContext().isReportingEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int, java.lang.String)
	 */
	public void report(int severity, String message)
	{
		getSessionContext().report(severity, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int, java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void report(int severity, String message, Dictionary properties)
	{
		getSessionContext().report(severity, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int,
	 *      java.lang.String[], java.lang.String)
	 */
	public void report(int severity, String[] categories, String message)
	{
		getSessionContext().report(severity, categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int,
	 *      java.lang.String[], java.lang.String, java.util.Dictionary)
	 */
	public void report(int severity, String[] categories, String message,
			Dictionary properties)
	{
		getSessionContext().report(severity, categories, message, properties);
	}

	// ILogger Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String)
	 */
	public void log(int severity, String message)
	{
		getSessionContext().log(severity, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void log(int severity, String message, Dictionary properties)
	{
		getSessionContext().log(severity, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String[],
	 *      java.lang.String)
	 */
	public void log(int severity, String[] categories, String message)
	{
		getSessionContext().log(severity, categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void log(int severity, String[] categories, String message,
			Dictionary properties)
	{
		getSessionContext().log(severity, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isErrorEnabled()
	 */
	public boolean isErrorEnabled()
	{
		return getSessionContext().isErrorEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String)
	 */
	public void error(String message)
	{
		getSessionContext().error(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void error(String message, Dictionary properties)
	{
		getSessionContext().error(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String[],
	 *      java.lang.String)
	 */
	public void error(String[] categories, String message)
	{
		getSessionContext().error(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void error(String[] categories, String message, Dictionary properties)
	{
		getSessionContext().error(categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isWarnEnabled()
	 */
	public boolean isWarnEnabled()
	{
		return getSessionContext().isWarnEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String)
	 */
	public void warn(String message)
	{
		getSessionContext().warn(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void warn(String message, Dictionary properties)
	{
		getSessionContext().warn(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String[],
	 *      java.lang.String)
	 */
	public void warn(String[] categories, String message)
	{
		getSessionContext().warn(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void warn(String[] categories, String message, Dictionary properties)
	{
		getSessionContext().warn(categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isInfoEnabled()
	 */
	public boolean isInfoEnabled()
	{
		return getSessionContext().isInfoEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String)
	 */
	public void info(String message)
	{
		getSessionContext().info(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void info(String message, Dictionary properties)
	{
		getSessionContext().info(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String[],
	 *      java.lang.String)
	 */
	public void info(String[] categories, String message)
	{
		getSessionContext().info(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void info(String[] categories, String message, Dictionary properties)
	{
		getSessionContext().info(categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isDebugEnabled()
	 */
	public boolean isDebugEnabled()
	{
		return getSessionContext().isDebugEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String)
	 */
	public void debug(String message)
	{
		getSessionContext().debug(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void debug(String message, Dictionary properties)
	{
		getSessionContext().debug(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String[],
	 *      java.lang.String)
	 */
	public void debug(String[] categories, String message)
	{
		getSessionContext().debug(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void debug(String[] categories, String message, Dictionary properties)
	{
		getSessionContext().debug(categories, message, properties);
	}

	// IServiceRegistry Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IServiceRegistry#lookupService(
	 *      java.lang.String)
	 */
	public Object lookup(String identifier) throws NullPointerException
	{
		if (isReservedIdentifier(identifier))
			return this;
		return getSessionContext().lookup(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IServiceRegistry#lookupAllServices(
	 *      java.lang.String)
	 */
	public Object[] lookupAll(String identifier)
			throws NullPointerException
	{
		if (isReservedIdentifier(identifier))
			return new Object[] { this };
		return getSessionContext().lookupAll(identifier);
	}

	// IProcessContext Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProcessID()
	 */
	public String getProcessID()
	{
		return getSessionContext().getProcessID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProperty(
	 *      java.lang.String)
	 */
	public Object getProperty(String propertyName) throws NullPointerException
	{
		return getSessionContext().getProperty(propertyName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#loadClass(
	 *      java.lang.String)
	 */
	public Class loadClass(String className) throws ClassNotFoundException,
			NullPointerException
	{
		return getSessionContext().loadClass(className);
	}

	// ISessionContext Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getSessionID()
	 */
	public String getSessionID()
	{
		return getSessionContext().getSessionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getSessionStartTime()
	 */
	public Date getSessionStartTime()
	{
		return getSessionContext().getSessionStartTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttributeNames()
	 */
	public String[] getAttributeNames()
	{
		return getSessionContext().getAttributeNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttribute(
	 *      java.lang.String)
	 */
	public Object getAttribute(String attributeName) throws NullPointerException
	{
		return getSessionContext().getAttribute(attributeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#setAttribute(
	 *      java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String attributeName, Object attributeValue)
			throws NullPointerException
	{
		getSessionContext().setAttribute(attributeName, attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#clearAttribute(
	 *      java.lang.String)
	 */
	public void clearAttribute(String attributeName) throws NullPointerException
	{
		getSessionContext().clearAttribute(attributeName);
	}

	public Object getInheritedAttribute(String attributeName) throws NullPointerException
	{
		return getSessionContext().getInheritedAttribute(attributeName);
	}

	public String[] getRootAttributeNames()
	{
		return getSessionContext().getRootAttributeNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttribute(
	 *      java.lang.String)
	 */
	public Object getRootAttribute(String attributeName) throws NullPointerException
	{
		return getSessionContext().getRootAttribute(attributeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#setAttribute(
	 *      java.lang.String, java.lang.Object)
	 */
	public void setRootAttribute(String attributeName, Object attributeValue)
			throws NullPointerException
	{
		getSessionContext().setRootAttribute(attributeName, attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#clearAttribute(
	 *      java.lang.String)
	 */
	public void clearRootAttribute(String attributeName) throws NullPointerException
	{
		getSessionContext().clearRootAttribute(attributeName);
	}
}
