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

/**
 * A service available to all services at the execution or action scope.
 * 
 * @author Lonnie Pryor
 */
public interface IExecutionContext extends ISessionContext
{
	/**
	 * Returns the ID of this execution.
	 * 
	 * @return The ID of this execution.
	 */
	String getExecutionID();

	/**
	 * Returns the names of the parameters available to the execution sequence.
	 * 
	 * @return The names of the parameters available to the execution sequence.
	 */
	String[] getParameterNames();

	/**
	 * Returns the value of the execution-level parameter with the specified name
	 * or <code>null</code> if no such parameter exists.
	 * 
	 * @param parameterName The name of the parameter to return.
	 * @return The value of the execution-level parameter with the specified name
	 *         or <code>null</code> if no such parameter exists.
	 * @throws NullPointerException If the supplied parameter name is
	 *           <code>null</code>.
	 */
	String getParameter(String parameterName) throws NullPointerException;

	/**
	 * Returns the values of the execution-level parameter with the specified name
	 * or <code>null</code> if no such parameter exists.
	 * 
	 * @param parameterName The name of the parameter to return.
	 * @return The values of the execution-level parameter with the specified name
	 *         or <code>null</code> if no such parameter exists.
	 * @throws NullPointerException If the supplied parameter name is
	 *           <code>null</code>.
	 */
	String[] getParameters(String parameterName) throws NullPointerException;

	/**
	 * Sets the value of the execution-level parameter with the specified name or
	 * clears it if the value is <code>null</code>.
	 * 
	 * @param parameterName The name of the parameter to set.
	 * @param value The value of the execution-level parameter with the specified
	 *          name or <code>null</code> to clear the value.
	 * @throws NullPointerException If the supplied parameter name is
	 *           <code>null</code>.
	 */
	void setParameter(String parameterName, String value)
			throws NullPointerException;

	/**
	 * Sets the values of the execution-level parameter with the specified name or
	 * clears them if the value is <code>null</code> or empty.
	 * 
	 * @param parameterName The name of the parameters to set.
	 * @param values The values of the execution-level parameter with the
	 *          specified name or <code>null</code> or to clear the values.
	 * @throws NullPointerException If the supplied parameter name is
	 *           <code>null</code>.
	 */
	void setParameters(String parameterName, String[] values)
			throws NullPointerException;

	/**
	 * Clears the value of the execution-level parameter with the specified name.
	 * 
	 * @param parameterName The name of the parameter to clear.
	 * @throws NullPointerException If the supplied parameter name is
	 *           <code>null</code>.
	 */
	void clearParameter(String parameterName) throws NullPointerException;
}
