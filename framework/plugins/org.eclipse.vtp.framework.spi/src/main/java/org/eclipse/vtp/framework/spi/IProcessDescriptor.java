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
package org.eclipse.vtp.framework.spi;

import java.util.Dictionary;

/**
 * A descriptor for process instances.
 * 
 * @author Lonnie Pryor
 */
public interface IProcessDescriptor
{
	/**
	 * Returns the ID of the process being described.
	 * 
	 * @return The ID of the process being described.
	 */
	String getProcessID();

	/**
	 * Returns the identifiers of all the externally-provided services.
	 * 
	 * @return The identifiers of all the externally-provided services.
	 */
	String[] getServiceIdentifiers();

	/**
	 * Returns the service selected for the specified identifier or
	 * <code>null</code> if no such service exists.
	 * 
	 * @param identifier The identifier of the service to return.
	 * @return The service selected for the specified identifier or
	 *         <code>null</code> if no such service exists.
	 * @throws NullPointerException If the supplied identifier is
	 *           <code>null</code>.
	 */
	Object getService(String identifier) throws NullPointerException;

	/**
	 * Returns the value of the property with the specified name or
	 * <code>null</code> if no such property exists.
	 * 
	 * @param propertyName The name of the property to return.
	 * @return The value of the property with the specified name or
	 *         <code>null</code> if no such property exists.
	 * @throws NullPointerException If the specified property name is
	 *           <code>null</code>.
	 */
	Object getProperty(String propertyName) throws NullPointerException;

	/**
	 * Loads a class visible to the process.
	 * 
	 * @param className The name of the class to load.
	 * @return The requested class instance.
	 * @throws ClassNotFoundException If a class with the specified name cannot be
	 *           found.
	 * @throws NullPointerException If the supplied class name is
	 *           <code>null</code>.
	 */
	@SuppressWarnings("rawtypes")
	Class loadClass(String className) throws ClassNotFoundException,
			NullPointerException;
	
	/**
	 * Returns true if the specified report severity level is enabled.
	 *
	 * @param severity The severity to check.
	 * @return True if the specified report severity level is enabled.
	 */
	boolean isSeverityEnabled(int severity);
	
	/**
	 * @return true if reporting is enabled for this process descriptor, false
	 * otherwise
	 */
	boolean isReportingEnabled();

	/**
	 * Notifies this descriptor of a report made during the execution of the
	 * process.
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
