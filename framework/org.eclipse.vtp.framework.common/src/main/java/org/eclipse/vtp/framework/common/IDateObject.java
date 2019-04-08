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
package org.eclipse.vtp.framework.common;

import java.util.Calendar;

/**
 * Represents a dynamic date and/or time value.
 * 
 * @author Lonnie Pryor
 */
public interface IDateObject extends IValueObject
{
	/** The name of the date type. */
	String TYPE_NAME = "Date"; //$NON-NLS-1$

	/**
	 * Returns the current value of this data object.
	 * 
	 * @return The current value of this data object.
	 */
	Calendar getValue();

}
