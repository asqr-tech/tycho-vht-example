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
import java.util.Hashtable;

import org.eclipse.vtp.framework.core.ILogger;

/**
 * A support implementation of the {@link ILogger} interface.
 * 
 * @author Lonnie Pryor
 */
public abstract class AbstractLogger extends AbstractReporter implements
		ILogger
{
	/**
	 * Creates a new AbstractLogger.
	 */
	protected AbstractLogger()
	{
	}

	/**
	 * Implementation of log entry creation and publication.
	 * 
	 * @param severity The severity of the log.
	 * @param categories The categories the log entry pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 * @param properties The properties of the log entry or <code>null</code> if
	 *          no properties are specified.
	 */
	protected void doLog(int severity, String[] categories, String message,
			Dictionary properties)
	{
		if (properties == null)
			properties = new Hashtable();
		properties.put("type", "log"); //$NON-NLS-1$ //$NON-NLS-2$
		doReport(severity, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String)
	 */
	public final void log(int severity, String message)
	{
		if (isSeverityEnabled(severity))
			doLog(severity, null, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String,
	 *      java.util.Dictionary)
	 */
	public final void log(int severity, String message, Dictionary properties)
	{
		if (isSeverityEnabled(severity))
			doLog(severity, null, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String[],
	 *      java.lang.String)
	 */
	public final void log(int severity, String[] categories, String message)
	{
		if (isSeverityEnabled(severity))
			doLog(severity, categories, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public final void log(int severity, String[] categories, String message,
			Dictionary properties)
	{
		if (isSeverityEnabled(severity))
			doLog(severity, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#isErrorEnabled()
	 */
	public final boolean isErrorEnabled()
	{
		return isSeverityEnabled(SEVERITY_ERROR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#error(java.lang.String)
	 */
	public final void error(String message)
	{
		if (isSeverityEnabled(SEVERITY_ERROR))
			doLog(SEVERITY_ERROR, null, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#error(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public final void error(String message, Dictionary properties)
	{
		if (isSeverityEnabled(SEVERITY_ERROR))
			doLog(SEVERITY_ERROR, null, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#error(java.lang.String[],
	 *      java.lang.String)
	 */
	public final void error(String[] categories, String message)
	{
		if (isSeverityEnabled(SEVERITY_ERROR))
			doLog(SEVERITY_ERROR, categories, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#error(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public final void error(String[] categories, String message,
			Dictionary properties)
	{
		if (isSeverityEnabled(SEVERITY_ERROR))
			doLog(SEVERITY_ERROR, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#isWarnEnabled()
	 */
	public final boolean isWarnEnabled()
	{
		return isSeverityEnabled(SEVERITY_WARN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#warn(java.lang.String)
	 */
	public final void warn(String message)
	{
		if (isSeverityEnabled(SEVERITY_WARN))
			doLog(SEVERITY_WARN, null, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#warn(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public final void warn(String message, Dictionary properties)
	{
		if (isSeverityEnabled(SEVERITY_WARN))
			doLog(SEVERITY_WARN, null, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#warn(java.lang.String[],
	 *      java.lang.String)
	 */
	public final void warn(String[] categories, String message)
	{
		if (isSeverityEnabled(SEVERITY_WARN))
			doLog(SEVERITY_WARN, categories, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#warn(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public final void warn(String[] categories, String message,
			Dictionary properties)
	{
		if (isSeverityEnabled(SEVERITY_WARN))
			doLog(SEVERITY_WARN, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#isInfoEnabled()
	 */
	public final boolean isInfoEnabled()
	{
		return isSeverityEnabled(SEVERITY_INFO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#info(java.lang.String)
	 */
	public final void info(String message)
	{
		if (isSeverityEnabled(SEVERITY_INFO))
			doLog(SEVERITY_INFO, null, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#info(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public final void info(String message, Dictionary properties)
	{
		if (isSeverityEnabled(SEVERITY_INFO))
			doLog(SEVERITY_INFO, null, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#info(java.lang.String[],
	 *      java.lang.String)
	 */
	public final void info(String[] categories, String message)
	{
		if (isSeverityEnabled(SEVERITY_INFO))
			doLog(SEVERITY_INFO, categories, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#info(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public final void info(String[] categories, String message,
			Dictionary properties)
	{
		if (isSeverityEnabled(SEVERITY_INFO))
			doLog(SEVERITY_INFO, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#isDebugEnabled()
	 */
	public final boolean isDebugEnabled()
	{
		return isSeverityEnabled(SEVERITY_DEBUG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#debug(java.lang.String)
	 */
	public final void debug(String message)
	{
		if (isSeverityEnabled(SEVERITY_DEBUG))
			doLog(SEVERITY_DEBUG, null, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#debug(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public final void debug(String message, Dictionary properties)
	{
		if (isSeverityEnabled(SEVERITY_DEBUG))
			doLog(SEVERITY_DEBUG, null, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#debug(java.lang.String[],
	 *      java.lang.String)
	 */
	public final void debug(String[] categories, String message)
	{
		if (isSeverityEnabled(SEVERITY_DEBUG))
			doLog(SEVERITY_DEBUG, categories, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ILogger#debug(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public final void debug(String[] categories, String message,
			Dictionary properties)
	{
		if (isSeverityEnabled(SEVERITY_DEBUG))
			doLog(SEVERITY_DEBUG, categories, message, properties);
	}
}
