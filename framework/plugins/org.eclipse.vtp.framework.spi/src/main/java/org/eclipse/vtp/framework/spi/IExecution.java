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
 * A collection of steps in a process.
 * 
 * @author Lonnie Pryor
 */
public interface IExecution
{
	/**
	 * Returns the ID of this execution sequence.
	 * 
	 * @return The ID of this execution sequence.
	 */
	String getExecutionID();

	/**
	 * Looks up the service selected for the specified identifier or
	 * <code>null</code> if no such service exists.
	 * 
	 * @param identifier The identifier of the service to look up.
	 * @return The service selected for the specified identifier or
	 *         <code>null</code> if no such service exists.
	 * @throws NullPointerException If the supplied identifier is
	 *           <code>null</code>.
	 */
	Object lookupService(String identifier) throws NullPointerException;

	/**
	 * Returns an array containing all the services registered under the specified
	 * identifier.
	 * 
	 * @param identifier The identifier of the services to look up.
	 * @return An array containing all the services registered under the specified
	 *         identifier.
	 * @throws NullPointerException If the supplied identifier is
	 *           <code>null</code>.
	 */
	Object[] lookupAllServices(String identifier) throws NullPointerException;

	/**
	 * Returns true if there are more steps to perform in the process.
	 * 
	 * @return True if there are more steps to perform in the process.
	 */
	boolean hasNextStep();

	/**
	 * Returns true if there is at least one step to perform and the next step may
	 * block across executions.
	 * 
	 * @return True if there is at least one step to perform and the next step may
	 *         block across executions
	 */
	boolean isNextStepBlocking();

	/**
	 * Returns the next step in the process.
	 * 
	 * @return The next step in the process.
	 * @throws IllegalStateException If there are no more steps in this process.
	 */
	ICommand nextStep() throws IllegalStateException;

	/**
	 * Returns the session that created this execution.
	 * 
	 * @return The session that created this execution.
	 */
	ISession getSession();
	
	/**
	 * Disposes this execution.
	 */
	void dispose();
}
