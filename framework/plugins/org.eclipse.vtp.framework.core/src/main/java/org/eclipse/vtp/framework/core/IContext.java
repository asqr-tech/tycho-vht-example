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
 * A registry of service objects indexed by identifier.
 * 
 * @author Lonnie Pryor
 */
public interface IContext extends ILogger
{
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
	Object lookup(String identifier) throws NullPointerException;

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
	Object[] lookupAll(String identifier) throws NullPointerException;
}
