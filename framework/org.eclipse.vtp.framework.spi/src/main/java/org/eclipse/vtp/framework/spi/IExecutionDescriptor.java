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

/**
 * A descriptor for process instances.
 * 
 * @author Lonnie Pryor
 */
public interface IExecutionDescriptor
{
	/**
	 * Returns the ID of the execution sequence being described.
	 * 
	 * @return The ID of the execution sequence being described.
	 */
	String getExecutionID();

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
	 * Returns the names of the parameters available to the execution sequence.
	 * 
	 * @return The names of the parameters available to the execution sequence.
	 */
	String[] getParameterNames();

	/**
	 * Returns the value of the parameter with the specified name or
	 * <code>null</code> if no such parameter exists.
	 * 
	 * @param parameterName The name of the parameter to return.
	 * @return The values of the parameter with the specified name or
	 *         <code>null</code> if no such parameter exists.
	 * @throws NullPointerException If the specified parameter name is
	 *           <code>null</code>.
	 */
	String getParameter(String parameterName) throws NullPointerException;

	/**
	 * Returns the values of the parameter with the specified name or
	 * <code>null</code> if no such parameter exists.
	 * 
	 * @param parameterName The name of the parameter to return.
	 * @return The values of the parameter with the specified name or
	 *         <code>null</code> if no such parameter exists.
	 * @throws NullPointerException If the specified parameter name is
	 *           <code>null</code>.
	 */
	String[] getParameters(String parameterName) throws NullPointerException;

	/**
	 * Sets the value of the parameter with the specified name or clears it if the
	 * value is <code>null</code>.
	 * 
	 * @param parameterName The name of the parameter to set.
	 * @param value The value of the parameter with the specified name or
	 *          <code>null</code> to clear the value.
	 * @throws NullPointerException If the supplied parameter name is
	 *           <code>null</code>.
	 */
	void setParameter(String parameterName, String value)
			throws NullPointerException;

	/**
	 * Sets the values of the parameter with the specified name or clears them if
	 * the value is <code>null</code> or empty.
	 * 
	 * @param parameterName The name of the parameters to set.
	 * @param values The values of the parameter with the specified name or
	 *          <code>null</code> or to clear the values.
	 * @throws NullPointerException If the supplied parameter name is
	 *           <code>null</code>.
	 */
	void setParameters(String parameterName, String[] values)
			throws NullPointerException;

	/**
	 * Clears the value of the parameter with the specified name.
	 * 
	 * @param parameterName The name of the parameter to clear.
	 * @throws NullPointerException If the supplied parameter name is
	 *           <code>null</code>.
	 */
	void clearParameter(String parameterName) throws NullPointerException;
}
