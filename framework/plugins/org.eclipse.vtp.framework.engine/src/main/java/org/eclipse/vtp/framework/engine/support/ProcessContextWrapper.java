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

import java.util.Dictionary;

import org.eclipse.vtp.framework.core.IProcessContext;

/**
 * A wrapper for the {@link IProcessContext} interface.
 * 
 * @author Lonnie Pryor
 */
public abstract class ProcessContextWrapper implements IProcessContext
{
	/**
	 * Creates a new ProcessContextWrapper.
	 */
	protected ProcessContextWrapper()
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
		return AbstractProcessContext.RESERVED_SERVICE_IDENTIFIERS
				.contains(identifier);
	}

	/**
	 * Returns the wrapped process context.
	 * 
	 * @return The wrapped process context.
	 */
	protected abstract IProcessContext getProcessContext();

	// IReporter Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IReporter#isSeverityEnabled(int)
	 */
	public boolean isSeverityEnabled(int severity)
	{
		return getProcessContext().isSeverityEnabled(severity);
	}
	
	public boolean isReportingEnabled()
	{
		return getProcessContext().isReportingEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int, java.lang.String)
	 */
	public void report(int severity, String message)
	{
		getProcessContext().report(severity, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int, java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void report(int severity, String message, Dictionary properties)
	{
		getProcessContext().report(severity, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int,
	 *      java.lang.String[], java.lang.String)
	 */
	public void report(int severity, String[] categories, String message)
	{
		getProcessContext().report(severity, categories, message);
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
		getProcessContext().report(severity, categories, message, properties);
	}

	// ILogger Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String)
	 */
	public void log(int severity, String message)
	{
		getProcessContext().log(severity, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void log(int severity, String message, Dictionary properties)
	{
		getProcessContext().log(severity, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String[],
	 *      java.lang.String)
	 */
	public void log(int severity, String[] categories, String message)
	{
		getProcessContext().log(severity, categories, message);
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
		getProcessContext().log(severity, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isErrorEnabled()
	 */
	public boolean isErrorEnabled()
	{
		return getProcessContext().isErrorEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String)
	 */
	public void error(String message)
	{
		getProcessContext().error(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void error(String message, Dictionary properties)
	{
		getProcessContext().error(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String[],
	 *      java.lang.String)
	 */
	public void error(String[] categories, String message)
	{
		getProcessContext().error(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void error(String[] categories, String message, Dictionary properties)
	{
		getProcessContext().error(categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isWarnEnabled()
	 */
	public boolean isWarnEnabled()
	{
		return getProcessContext().isWarnEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String)
	 */
	public void warn(String message)
	{
		getProcessContext().warn(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void warn(String message, Dictionary properties)
	{
		getProcessContext().warn(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String[],
	 *      java.lang.String)
	 */
	public void warn(String[] categories, String message)
	{
		getProcessContext().warn(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void warn(String[] categories, String message, Dictionary properties)
	{
		getProcessContext().warn(categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isInfoEnabled()
	 */
	public boolean isInfoEnabled()
	{
		return getProcessContext().isInfoEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String)
	 */
	public void info(String message)
	{
		getProcessContext().info(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void info(String message, Dictionary properties)
	{
		getProcessContext().info(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String[],
	 *      java.lang.String)
	 */
	public void info(String[] categories, String message)
	{
		getProcessContext().info(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void info(String[] categories, String message, Dictionary properties)
	{
		getProcessContext().info(categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isDebugEnabled()
	 */
	public boolean isDebugEnabled()
	{
		return getProcessContext().isDebugEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String)
	 */
	public void debug(String message)
	{
		getProcessContext().debug(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void debug(String message, Dictionary properties)
	{
		getProcessContext().debug(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String[],
	 *      java.lang.String)
	 */
	public void debug(String[] categories, String message)
	{
		getProcessContext().debug(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void debug(String[] categories, String message, Dictionary properties)
	{
		getProcessContext().debug(categories, message, properties);
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
		return getProcessContext().lookup(identifier);
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
		return getProcessContext().lookupAll(identifier);
	}

	// IProcessContext Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProcessID()
	 */
	public String getProcessID()
	{
		return getProcessContext().getProcessID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProperty(
	 *      java.lang.String)
	 */
	public Object getProperty(String propertyName) throws NullPointerException
	{
		return getProcessContext().getProperty(propertyName);
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
		return getProcessContext().loadClass(className);
	}
}
