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

import java.util.Date;

/**
 * A service available to all services at any scope except the process scope.
 * 
 * @author Lonnie Pryor
 */
public interface ISessionContext extends IProcessContext
{
	/**
	 * Returns the ID of the current session.
	 * 
	 * @return The ID of the current session.
	 */
	String getSessionID();

	/**
	 * Returns the start time of the current session.
	 * 
	 * @return The start time of the current session.
	 */
	Date getSessionStartTime();

	/**
	 * Returns the names of all the session-level attributes currently registered.
	 * 
	 * @return The names of all the session-level attributes currently registered.
	 */
	String[] getAttributeNames();

	/**
	 * Returns the value of a session-level attribute with the specified name or
	 * <code>null</code> if no such attribute exists.
	 * 
	 * @param attributeName The name of the session attribute to return.
	 * @return The value of a session-level attribute with the specified name or
	 *         <code>null</code> if no such attribute exists.
	 * @throws NullPointerException If the supplied attribute name is
	 *           <code>null</code>.
	 */
	Object getAttribute(String attributeName) throws NullPointerException;

	/**
	 * Sets the value of a session-level attribute or clears it if the supplied
	 * value is <code>null</code>.
	 * 
	 * @param attributeName The name of the session attribute to set.
	 * @param attributeValue The value to set the attribute to or
	 *          <code>null</code> to clear the attribute.
	 * @throws NullPointerException If the supplied attribute name is
	 *           <code>null</code>.
	 */
	void setAttribute(String attributeName, Object attributeValue)
			throws NullPointerException;

	/**
	 * Clears the value of a session-level attribute.
	 * 
	 * @param attributeName The name of the session attribute to clear.
	 * @throws NullPointerException If the supplied attribute name is
	 *           <code>null</code>.
	 */
	void clearAttribute(String attributeName) throws NullPointerException;
	
	Object getInheritedAttribute(String attributeName) throws NullPointerException;

	/**
	 * Returns the names of all the session-level attributes currently registered.
	 * 
	 * @return The names of all the session-level attributes currently registered.
	 */
	String[] getRootAttributeNames();

	/**
	 * Returns the value of a session-level attribute with the specified name or
	 * <code>null</code> if no such attribute exists.
	 * 
	 * @param attributeName The name of the session attribute to return.
	 * @return The value of a session-level attribute with the specified name or
	 *         <code>null</code> if no such attribute exists.
	 * @throws NullPointerException If the supplied attribute name is
	 *           <code>null</code>.
	 */
	Object getRootAttribute(String attributeName) throws NullPointerException;

	/**
	 * Sets the value of a session-level attribute or clears it if the supplied
	 * value is <code>null</code>.
	 * 
	 * @param attributeName The name of the session attribute to set.
	 * @param attributeValue The value to set the attribute to or
	 *          <code>null</code> to clear the attribute.
	 * @throws NullPointerException If the supplied attribute name is
	 *           <code>null</code>.
	 */
	void setRootAttribute(String attributeName, Object attributeValue)
			throws NullPointerException;

	/**
	 * Clears the value of a session-level attribute.
	 * 
	 * @param attributeName The name of the session attribute to clear.
	 * @throws NullPointerException If the supplied attribute name is
	 *           <code>null</code>.
	 */
	void clearRootAttribute(String attributeName) throws NullPointerException;
}
