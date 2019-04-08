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
package org.eclipse.vtp.framework.common.services;

import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.core.ILogger;

/**
 * An {@link IScriptable} implementation that makes the scoped {@link ILogger}
 * instance available as a scripting object.
 * 
 * <p>
 * This service will make available a "Log" object to all scripts in its scope.
 * The variable supports the following methods:
 * <ul>
 * <li><code>isEnabled(level)</code></li>
 * <li><code>report(level, categories*, message)</code></li>
 * <li><code>log(level, categories*, message)</code></li>
 * <li><code>error(categories*, message)</code></li>
 * <li><code>warn(categories*, message)</code></li>
 * <li><code>info(categories*, message)</code></li>
 * <li><code>debug(categories*, message)</code></li>
 * </ul>
 * In all the above methods, the <code>level</code> argument must be one of
 * "error", "warn", "info", "debug", or an integer. The <code>categories*</code>
 * argument(s) must be zero or more category names the report or log entry
 * applies to. The <code>message</code> argument must be the message to report
 * or log.
 * </p>
 * 
 * <p>
 * The "Log" scripting object has no implicit value and cannot be compared to
 * other objects.
 * </p>
 * 
 * @author Lonnie Pryor
 * @see ILogger
 */
public class ScriptableLogger implements IScriptable
{
	/** The logger to provide scripting services for. */
	private final ILogger logger;

	/**
	 * Creates a new ScriptableLogger.
	 * 
	 * @param logger The logger to provide scripting services for.
	 */
	public ScriptableLogger(ILogger logger)
	{
		this.logger = logger;
	}

	/**
	 * Converts an object into a severity level.
	 * 
	 * @param value The value to convert.
	 * @return The converted severity level.
	 */
	private int toSeverityLevel(Object value)
	{
		String string = String.valueOf(value);
		if ("error".equalsIgnoreCase(string)) //$NON-NLS-1$ 
			return ILogger.SEVERITY_ERROR;
		else if ("warn".equalsIgnoreCase(string)) //$NON-NLS-1$ 
			return ILogger.SEVERITY_WARN;
		else if ("info".equalsIgnoreCase(string)) //$NON-NLS-1$ 
			return ILogger.SEVERITY_INFO;
		else if ("debug".equalsIgnoreCase(string)) //$NON-NLS-1$ 
			return ILogger.SEVERITY_DEBUG;
		try
		{
			return Integer.parseInt(string);
		}
		catch (NumberFormatException e)
		{
			return ILogger.SEVERITY_DEBUG;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
	 */
	public final String getName()
	{
		return "Log"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
	 */
	public boolean hasValue()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
	 */
	public Object toValue()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getFunctionNames()
	 */
	public final String[] getFunctionNames()
	{
		return new String[] { "isEnabled", //$NON-NLS-1$
				"report", //$NON-NLS-1$
				"log", //$NON-NLS-1$
				"error", //$NON-NLS-1$
				"warn", //$NON-NLS-1$
				"info", //$NON-NLS-1$
				"debug" //$NON-NLS-1$
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
	 *      java.lang.String, java.lang.Object[])
	 */
	public final Object invokeFunction(String name, Object[] arguments)
	{
		if ("isEnabled".equals(name)) //$NON-NLS-1$
		{
			if (arguments.length == 0)
				return null;
			return logger.isSeverityEnabled(toSeverityLevel(arguments[0])) ? Boolean.TRUE
					: Boolean.FALSE;
		}
		else if ("report".equals(name)) //$NON-NLS-1$
		{
			if (arguments.length < 2)
				return null;
			int severity = toSeverityLevel(arguments[0]);
			if (!logger.isSeverityEnabled(severity))
				return null;
			String[] categories = null;
			if (arguments.length > 2)
			{
				categories = new String[arguments.length - 2];
				for (int i = 0; i < categories.length; ++i)
					categories[i] = String.valueOf(arguments[i + 1]);
			}
			logger.report(severity, categories, String
					.valueOf(arguments[arguments.length - 1]));
		}
		else
		{
			int severity = ILogger.SEVERITY_DEBUG;
			String[] categories = null;
			if ("log".equals(name)) //$NON-NLS-1$
			{
				if (arguments.length < 2)
					return null;
				severity = toSeverityLevel(arguments[0]);
				if (!logger.isSeverityEnabled(severity))
					return null;
				if (arguments.length > 2)
				{
					categories = new String[arguments.length - 2];
					for (int i = 0; i < categories.length; ++i)
						categories[i] = String.valueOf(arguments[i + 1]);
				}
			}
			else
			{
				if (arguments.length < 1)
					return null;
				severity = toSeverityLevel(name);
				if (!logger.isSeverityEnabled(severity))
					return null;
				if (arguments.length > 1)
				{
					categories = new String[arguments.length - 1];
					for (int i = 0; i < categories.length; ++i)
						categories[i] = String.valueOf(arguments[i]);
				}
			}
			logger.log(severity, categories, String
					.valueOf(arguments[arguments.length - 1]));
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
	 */
	public final boolean hasItem(int index)
	{
		return false;
	}

	public String[] getPropertyNames()
	{
		return new String[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasProperty(
	 *      java.lang.String)
	 */
	public final boolean hasEntry(String name)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
	 */
	public final Object getItem(int index)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getProperty(
	 *      java.lang.String)
	 */
	public final Object getEntry(String name)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
	 *      java.lang.Object)
	 */
	public final boolean setItem(int index, Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setProperty(
	 *      java.lang.String, java.lang.Object)
	 */
	public final boolean setEntry(String name, Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
	 */
	public final boolean clearItem(int index)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearProperty(
	 *      java.lang.String)
	 */
	public final boolean clearEntry(String name)
	{
		return false;
	}

	@Override
	public boolean isMutable()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
