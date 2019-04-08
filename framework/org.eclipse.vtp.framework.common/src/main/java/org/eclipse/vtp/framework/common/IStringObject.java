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

/**
 * Represents a dynamic sequence of characters.
 * 
 * @author Lonnie Pryor
 */
public interface IStringObject extends IValueObject
{
	/** The name of the string type. */
	String TYPE_NAME = "String"; //$NON-NLS-1$
	/** The name of the length field. */
	String FIELD_NAME_LENGTH = "length"; //$NON-NLS-1$
	
	/**
	 * Returns the length field of this string object.
	 *
	 * @return The length field of this string object.
	 */
	INumberObject getLength();
	
	/**
	 * Returns the current value of this data object.
	 * 
	 * @return The current value of this data object.
	 */
	String getValue();

}
