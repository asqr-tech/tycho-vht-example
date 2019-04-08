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
package org.eclipse.vtp.framework.common.controller;

/**
 * A dispatcher that suspends the current process, forwards control to another
 * process, and resumes the current process when the target process completes.
 * 
 * @author Lonnie Pryor
 */
public interface IIncludeDispatcher extends IDispatcher
{
	/**
	 * Configures a parameter set when the current process resumes.
	 * 
	 * @param name The name of the parameter to set.
	 * @param value The value to set the parameter to.
	 */
	void setParameterValue(String name, String value);

	/**
	 * Configures a parameter set when the current process resumes.
	 * 
	 * @param name The name of the parameter to set.
	 * @param values The values to set the parameter to.
	 */
	void setParameterValues(String name, String[] values);
}
