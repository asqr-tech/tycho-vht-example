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
package org.eclipse.vtp.framework.engine;

import java.lang.reflect.Modifier;

/**
 * Utilities used by the various descriptors.
 * 
 * @author Lonnie Pryor
 */
public final class DescriptorUtils
{
	/**
	 * Returns true if the specified type is a public, concrete class with at
	 * least one public constructor.
	 * 
	 * @param type The type to check.
	 * @return True if the specified type is a public, concrete class with at
	 *         least one public constructor.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 */
	public static boolean isValidImplementation(Class type)
			throws NullPointerException
	{
		return isValidImplementation(type, Object.class);
	}

	/**
	 * Returns true if the specified type is a public, concrete class with at
	 * least one public constructor and is assignable to the specified type.
	 * 
	 * @param type The type to check.
	 * @param assignableToType The type the first type must be assignable to.
	 * @return True if the specified type is a public, concrete class with at
	 *         least one public constructor and is assignable to the specified
	 *         type.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 * @throws NullPointerException If the supplied assignable to type is
	 *           <code>null</code>.
	 */
	public static boolean isValidImplementation(Class type, Class assignableToType)
			throws NullPointerException
	{
		if (type == null)
			throw new NullPointerException("type"); //$NON-NLS-1$
		if (assignableToType == null)
			throw new NullPointerException("assignableToType"); //$NON-NLS-1$
		if (!assignableToType.isAssignableFrom(type))
			return false;
		if (type.isPrimitive())
			return false;
		if (type.isArray())
			return false;
		if (type.isInterface())
			return false;
		int modifiers = type.getModifiers();
		if (Modifier.isAbstract(modifiers))
			return false;
		if (!Modifier.isPublic(modifiers))
			return false;
		return type.getConstructors().length > 0;
	}
}
