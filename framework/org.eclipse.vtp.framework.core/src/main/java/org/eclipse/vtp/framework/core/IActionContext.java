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
 * A service available to services at the action scope.
 * 
 * @author Lonnie Pryor
 */
public interface IActionContext extends IExecutionContext
{
	/** Before the action has been run. */
	int STATE_BEFORE = 1;
	/** While the action is running. */
	int STATE_DURING = 2;
	/** After the action has been run. */
	int STATE_AFTER = 3;
	
	/**
	 * Returns the ID of the current action.
	 * 
	 * @return The ID of the current action.
	 */
	String getActionID();
	
	/**
	 * Returns the name of the current action.
	 * 
	 * @return The name of the current action.
	 */
	String getActionName();
	
	/**
	 * Returns the state of the current action.
	 *
	 * @return The state of the current action.
	 */
	int getActionState();

	/**
	 * Creates an action result with the specified name.
	 * 
	 * @param resultName The name of the result to create or <code>null</code>
	 *          to create an undefined result.
	 * @return A new action result with the specified name.
	 */
	IActionResult createResult(String resultName);

	/**
	 * Creates an action result with the specified name.
	 * 
	 * @param resultName The name of the result to create or <code>null</code>
	 *          to create an undefined result.
	 * @param failureCause The cause of the failure the action result represents
	 *          or <code>null</code> to not specify a failure cause.
	 * @return A new action result with the specified name and failure cause.
	 */
	IActionResult createResult(String resultName, Throwable failureCause);
}
