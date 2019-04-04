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
 * Represents the result of an action.
 * 
 * @author Lonnie Pryor
 */
public interface IActionResult
{
	/** The name of the global result that represents an internal error. */
	String RESULT_NAME_ERROR = "error"; //$NON-NLS-1$
	/** The name of the global result that will abort the process. */
	String RESULT_NAME_ABORT = "abort"; //$NON-NLS-1$
	/** The name of the global result that will take the default path. */
	String RESULT_NAME_DEFAULT = "default"; //$NON-NLS-1$
	/** The name of the global result that will execute the same action again. */
	String RESULT_NAME_REPEAT = "repeat"; //$NON-NLS-1$

	/**
	 * Returns the name of this action result.
	 * 
	 * @return The name of this action result.
	 */
	String getName();

	/**
	 * Returns the cause of the failure if this result path represents a failure,
	 * otherwise returns <code>null</code>.
	 * 
	 * @return The cause of the failure if this result path represents a failure.
	 */
	Throwable getFailureCause();
}
