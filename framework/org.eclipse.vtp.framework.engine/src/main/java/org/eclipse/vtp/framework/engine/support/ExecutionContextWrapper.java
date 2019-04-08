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

import org.eclipse.vtp.framework.core.IExecutionContext;

/**
 * A wrapper for the {@link IExecutionContext} interface.
 * 
 * @author Lonnie Pryor
 */
public abstract class ExecutionContextWrapper implements IExecutionContext
{
	/**
	 * Creates a new ExecutionContextWrapper.
	 */
	protected ExecutionContextWrapper()
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
		return AbstractExecutionContext.RESERVED_SERVICE_IDENTIFIERS
				.contains(identifier);
	}

	/**
	 * Returns the wrapped execution context.
	 * 
	 * @return The wrapped execution context.
	 */
	protected abstract IExecutionContext getExecutionContext();

	// IReporter Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IReporter#isSeverityEnabled(int)
	 */
	public boolean isSeverityEnabled(int severity)
	{
		return getExecutionContext().isSeverityEnabled(severity);
	}
	
	public boolean isReportingEnabled()
	{
		return getExecutionContext().isReportingEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int, java.lang.String)
	 */
	public void report(int severity, String message)
	{
		getExecutionContext().report(severity, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int, java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void report(int severity, String message, Dictionary properties)
	{
		getExecutionContext().report(severity, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int,
	 *      java.lang.String[], java.lang.String)
	 */
	public void report(int severity, String[] categories, String message)
	{
		getExecutionContext().report(severity, categories, message);
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
		getExecutionContext().report(severity, categories, message, properties);
	}

	// ILogger Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String)
	 */
	public void log(int severity, String message)
	{
		getExecutionContext().log(severity, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void log(int severity, String message, Dictionary properties)
	{
		getExecutionContext().log(severity, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String[],
	 *      java.lang.String)
	 */
	public void log(int severity, String[] categories, String message)
	{
		getExecutionContext().log(severity, categories, message);
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
		getExecutionContext().log(severity, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isErrorEnabled()
	 */
	public boolean isErrorEnabled()
	{
		return getExecutionContext().isErrorEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String)
	 */
	public void error(String message)
	{
		getExecutionContext().error(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void error(String message, Dictionary properties)
	{
		getExecutionContext().error(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String[],
	 *      java.lang.String)
	 */
	public void error(String[] categories, String message)
	{
		getExecutionContext().error(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void error(String[] categories, String message, Dictionary properties)
	{
		getExecutionContext().error(categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isWarnEnabled()
	 */
	public boolean isWarnEnabled()
	{
		return getExecutionContext().isWarnEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String)
	 */
	public void warn(String message)
	{
		getExecutionContext().warn(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void warn(String message, Dictionary properties)
	{
		getExecutionContext().warn(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String[],
	 *      java.lang.String)
	 */
	public void warn(String[] categories, String message)
	{
		getExecutionContext().warn(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void warn(String[] categories, String message, Dictionary properties)
	{
		getExecutionContext().warn(categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isInfoEnabled()
	 */
	public boolean isInfoEnabled()
	{
		return getExecutionContext().isInfoEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String)
	 */
	public void info(String message)
	{
		getExecutionContext().info(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void info(String message, Dictionary properties)
	{
		getExecutionContext().info(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String[],
	 *      java.lang.String)
	 */
	public void info(String[] categories, String message)
	{
		getExecutionContext().info(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void info(String[] categories, String message, Dictionary properties)
	{
		getExecutionContext().info(categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isDebugEnabled()
	 */
	public boolean isDebugEnabled()
	{
		return getExecutionContext().isDebugEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String)
	 */
	public void debug(String message)
	{
		getExecutionContext().debug(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String,
	 *      java.util.Dictionary)
	 */
	public void debug(String message, Dictionary properties)
	{
		getExecutionContext().debug(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String[],
	 *      java.lang.String)
	 */
	public void debug(String[] categories, String message)
	{
		getExecutionContext().debug(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String[],
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void debug(String[] categories, String message, Dictionary properties)
	{
		getExecutionContext().debug(categories, message, properties);
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
		return getExecutionContext().lookup(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IServiceRegistry#lookupAllServices(
	 *      java.lang.String)
	 */
	public Object[] lookupAll(String identifier) throws NullPointerException
	{
		if (isReservedIdentifier(identifier))
			return new Object[] { this };
		return getExecutionContext().lookupAll(identifier);
	}

	// IProcessContext Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProcessID()
	 */
	public String getProcessID()
	{
		return getExecutionContext().getProcessID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProperty(
	 *      java.lang.String)
	 */
	public Object getProperty(String propertyName) throws NullPointerException
	{
		return getExecutionContext().getProperty(propertyName);
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
		return getExecutionContext().loadClass(className);
	}

	// ISessionContext Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getSessionID()
	 */
	public String getSessionID()
	{
		return getExecutionContext().getSessionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getSessionStartTime()
	 */
	public Date getSessionStartTime()
	{
		return getExecutionContext().getSessionStartTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttributeNames()
	 */
	public String[] getAttributeNames()
	{
		return getExecutionContext().getAttributeNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttribute(
	 *      java.lang.String)
	 */
	public Object getAttribute(String attributeName) throws NullPointerException
	{
		return getExecutionContext().getAttribute(attributeName);
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
		getExecutionContext().setAttribute(attributeName, attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#clearAttribute(
	 *      java.lang.String)
	 */
	public void clearAttribute(String attributeName) throws NullPointerException
	{
		getExecutionContext().clearAttribute(attributeName);
	}

	public Object getInheritedAttribute(String attributeName) throws NullPointerException
	{
		return getExecutionContext().getInheritedAttribute(attributeName);
	}

	// IExecutionContext Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getExecutionID()
	 */
	public String getExecutionID()
	{
		return getExecutionContext().getExecutionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getParameterNames()
	 */
	public String[] getParameterNames()
	{
		return getExecutionContext().getParameterNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getParameter(
	 *      java.lang.String)
	 */
	public String getParameter(String parameterName) throws NullPointerException
	{
		return getExecutionContext().getParameter(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getParameters(
	 *      java.lang.String)
	 */
	public String[] getParameters(String parameterName)
			throws NullPointerException
	{
		return getExecutionContext().getParameters(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#setParameter(
	 *      java.lang.String, java.lang.String)
	 */
	public void setParameter(String parameterName, String value)
			throws NullPointerException
	{
		getExecutionContext().setParameter(parameterName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#setParameters(
	 *      java.lang.String, java.lang.String[])
	 */
	public void setParameters(String parameterName, String[] values)
			throws NullPointerException
	{
		getExecutionContext().setParameters(parameterName, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#clearParameter(
	 *      java.lang.String)
	 */
	public void clearParameter(String parameterName) throws NullPointerException
	{
		getExecutionContext().clearParameter(parameterName);
	}

	public String[] getRootAttributeNames()
	{
		return getExecutionContext().getRootAttributeNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttribute(
	 *      java.lang.String)
	 */
	public Object getRootAttribute(String attributeName) throws NullPointerException
	{
		return getExecutionContext().getRootAttribute(attributeName);
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
		getExecutionContext().setRootAttribute(attributeName, attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#clearAttribute(
	 *      java.lang.String)
	 */
	public void clearRootAttribute(String attributeName) throws NullPointerException
	{
		getExecutionContext().clearRootAttribute(attributeName);
	}
}
