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
 * An interface to report generic events to.
 * 
 * @author Lonnie Pryor
 */
public interface IReporter
{
	/** The error severity constant. */
	int SEVERITY_ERROR = 1;
	/** The warning severity constant. */
	int SEVERITY_WARN = 2;
	/** The informational severity constant. */
	int SEVERITY_INFO = 3;
	/** The debug severity constant. */
	int SEVERITY_DEBUG = 4;
	
	/**
	 * Returns true if the specified severity level is enabled.
	 *
	 * @param severity The severity to check.
	 * @return True if the specified severity level is enabled.
	 */
	boolean isSeverityEnabled(int severity);
	
	/**
	 * @return true if reporting is enabled from this reporter, false otherwise
	 */
	boolean isReportingEnabled();

	/**
	 * Creates and publishes a reporting entry with the specified attributes.
	 * 
	 * @param severity The severity of the report.
	 * @param message The message associated with the report or <code>null</code>
	 *          to not include a message.
	 */
	void report(int severity, String message);

	/**
	 * Creates and publishes a reporting entry with the specified attributes.
	 * 
	 * @param severity The severity of the report.
	 * @param message The message associated with the report or <code>null</code>
	 *          to not include a message.
	 * @param properties The properties of the report or <code>null</code> if no
	 *          properties are specified.
	 */
	void report(int severity, String message, @SuppressWarnings("rawtypes") Dictionary properties);

	/**
	 * Creates and publishes a reporting entry with the specified attributes.
	 * 
	 * @param severity The severity of the report.
	 * @param categories The categories the report pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the report or <code>null</code>
	 *          to not include a message.
	 */
	void report(int severity, String[] categories, String message);

	/**
	 * Creates and publishes a reporting entry with the specified attributes.
	 * 
	 * @param severity The severity of the report.
	 * @param categories The categories the report pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the report or <code>null</code>
	 *          to not include a message.
	 * @param properties The properties of the report or <code>null</code> if no
	 *          properties are specified.
	 */
	void report(int severity, String[] categories, String message,
			@SuppressWarnings("rawtypes") Dictionary properties);
}
