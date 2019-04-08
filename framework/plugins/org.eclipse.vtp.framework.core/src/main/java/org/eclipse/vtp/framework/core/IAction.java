/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods), 
 *    Randy Childers (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.core;

/**
 * An action performed by the process engine.
 *
 * @author Lonnie Pryor
 */
public interface IAction
{
	String ACTION_PREFIX = "Action_";
	
	/**
	 * Executes this action and returns its outcome.
	 *
	 * @return An object representing the outcome of this action.
	 */
	IActionResult execute();
}
