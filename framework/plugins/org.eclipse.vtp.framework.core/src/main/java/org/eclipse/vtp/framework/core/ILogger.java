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
package org.eclipse.vtp.framework.core;

import java.util.Dictionary;

/**
 * An interface to use for common logging purposes.
 * 
 * @author Lonnie Pryor
 */
public interface ILogger extends IReporter
{

	/**
	 * Creates and publishes a log entry with the specified attributes.
	 * 
	 * @param severity The severity of the log entry.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 */
	void log(int severity, String message);

	/**
	 * Creates and publishes a log entry with the specified attributes.
	 * 
	 * @param severity The severity of the log entry.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 * @param properties The properties of the log entry or <code>null</code> if
	 *          no properties are specified.
	 */
	void log(int severity, String message, @SuppressWarnings("rawtypes") Dictionary properties);

	/**
	 * Creates and publishes a log entry with the specified attributes.
	 * 
	 * @param severity The severity of the log entry.
	 * @param categories The categories the log entry pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 */
	void log(int severity, String[] categories, String message);

	/**
	 * Creates and publishes a log entry with the specified attributes.
	 * 
	 * @param severity The severity of the log entry.
	 * @param categories The categories the log entry pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 * @param properties The properties of the log entry or <code>null</code> if
	 *          no properties are specified.
	 */
	void log(int severity, String[] categories, String message,
			@SuppressWarnings("rawtypes") Dictionary properties);

	/**
	 * Returns true if the error severity level is enabled.
	 * 
	 * @return True if the error severity level is enabled.
	 */
	boolean isErrorEnabled();

	/**
	 * Creates and publishes an error log entry with the specified attributes.
	 * 
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 */
	void error(String message);

	/**
	 * Creates and publishes an error log entry with the specified attributes.
	 * 
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 * @param properties The properties of the log entry or <code>null</code> if
	 *          no properties are specified.
	 */
	void error(String message, @SuppressWarnings("rawtypes") Dictionary properties);

	/**
	 * Creates and publishes an error log entry with the specified attributes.
	 * 
	 * @param categories The categories the log entry pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 */
	void error(String[] categories, String message);

	/**
	 * Creates and publishes an error log entry with the specified attributes.
	 * 
	 * @param categories The categories the log entry pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 * @param properties The properties of the log entry or <code>null</code> if
	 *          no properties are specified.
	 */
	void error(String[] categories, String message, @SuppressWarnings("rawtypes") Dictionary properties);

	/**
	 * Returns true if the warning severity level is enabled.
	 * 
	 * @return True if the warning severity level is enabled.
	 */
	boolean isWarnEnabled();

	/**
	 * Creates and publishes a warning log entry with the specified attributes.
	 * 
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 */
	void warn(String message);

	/**
	 * Creates and publishes a warning log entry with the specified attributes.
	 * 
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 * @param properties The properties of the log entry or <code>null</code> if
	 *          no properties are specified.
	 */
	void warn(String message, @SuppressWarnings("rawtypes") Dictionary properties);

	/**
	 * Creates and publishes a warning log entry with the specified attributes.
	 * 
	 * @param categories The categories the log entry pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 */
	void warn(String[] categories, String message);

	/**
	 * Creates and publishes a warning log entry with the specified attributes.
	 * 
	 * @param categories The categories the log entry pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 * @param properties The properties of the log entry or <code>null</code> if
	 *          no properties are specified.
	 */
	void warn(String[] categories, String message, @SuppressWarnings("rawtypes") Dictionary properties);

	/**
	 * Returns true if the informational severity level is enabled.
	 * 
	 * @return True if the informational severity level is enabled.
	 */
	boolean isInfoEnabled();

	/**
	 * Creates and publishes an informational log entry with the specified
	 * attributes.
	 * 
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 */
	void info(String message);

	/**
	 * Creates and publishes an informational log entry with the specified
	 * attributes.
	 * 
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 * @param properties The properties of the log entry or <code>null</code> if
	 *          no properties are specified.
	 */
	void info(String message, @SuppressWarnings("rawtypes") Dictionary properties);

	/**
	 * Creates and publishes an informational log entry with the specified
	 * attributes.
	 * 
	 * @param categories The categories the log entry pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 */
	void info(String[] categories, String message);

	/**
	 * Creates and publishes an informational log entry with the specified
	 * attributes.
	 * 
	 * @param categories The categories the log entry pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 * @param properties The properties of the log entry or <code>null</code> if
	 *          no properties are specified.
	 */
	void info(String[] categories, String message, @SuppressWarnings("rawtypes") Dictionary properties);

	/**
	 * Returns true if the debug severity level is enabled.
	 * 
	 * @return True if the debug severity level is enabled.
	 */
	boolean isDebugEnabled();

	/**
	 * Creates and publishes a debug log entry with the specified attributes.
	 * 
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 */
	void debug(String message);

	/**
	 * Creates and publishes a debug log entry with the specified attributes.
	 * 
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 * @param properties The properties of the log entry or <code>null</code> if
	 *          no properties are specified.
	 */
	void debug(String message, @SuppressWarnings("rawtypes") Dictionary properties);

	/**
	 * Creates and publishes a debug log entry with the specified attributes.
	 * 
	 * @param categories The categories the log entry pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 */
	void debug(String[] categories, String message);

	/**
	 * Creates and publishes a debug log entry with the specified attributes.
	 * 
	 * @param categories The categories the log entry pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the log entry or
	 *          <code>null</code> to not include a message.
	 * @param properties The properties of the log entry or <code>null</code> if
	 *          no properties are specified.
	 */
	void debug(String[] categories, String message, @SuppressWarnings("rawtypes") Dictionary properties);
}
