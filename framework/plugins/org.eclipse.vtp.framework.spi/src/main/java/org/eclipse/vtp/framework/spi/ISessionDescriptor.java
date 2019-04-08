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

import java.util.Date;

/**
 * The descriptor for a session in the process engine.
 * 
 * @author Lonnie Pryor
 */
public interface ISessionDescriptor
{
	/**
	 * Returns the ID of the session being described.
	 * 
	 * @return The ID of the session being described.
	 */
	String getSessionID();

	/**
	 * Returns the start time of the session being described.
	 * 
	 * @return The start time of the session being described.
	 */
	Date getSessionStartTime();

	/**
	 * Returns the identifiers of all the externally-provided services.
	 * 
	 * @return The identifiers of all the externally-provided services.
	 */
	String[] getServiceIdentifiers();
	
	/**
	 * Returns the service selected for the specified identifier or
	 * <code>null</code> if no such service exists.
	 * 
	 * @param identifier The identifier of the service to return.
	 * @return The service selected for the specified identifier or
	 *         <code>null</code> if no such service exists.
	 * @throws NullPointerException If the supplied identifier is
	 *           <code>null</code>.
	 */
	Object getService(String identifier) throws NullPointerException;

	/**
	 * Returns the names of the attributes set in the session.
	 * 
	 * @return The names of the attributes set in the session.
	 */
	String[] getAttributeNames();

	/**
	 * Returns the value of the session attribute with the specified name or
	 * <code>null</code> if no such attribute exists.
	 * 
	 * @param attributeName The name of the session attribute to return.
	 * @return The value of the session attribute with the specified name or
	 *         <code>null</code> if no such attribute exists.
	 * @throws NullPointerException If the specified attribute name is
	 *           <code>null</code>.
	 */
	Object getAttribute(String attributeName) throws NullPointerException;

	/**
	 * Sets the value of the specified session attribute or clears it if the
	 * supplied value is <code>null</code>.
	 * 
	 * @param attributeName The name of the session attribute to set.
	 * @param attributeValue The value to set the attribute to or
	 *          <code>null</code> to clear the attribute.
	 * @throws NullPointerException If the specified attribute name is
	 *           <code>null</code>.
	 */
	void setAttribute(String attributeName, Object attributeValue)
			throws NullPointerException;

	/**
	 * Clears the value of the specified session attribute.
	 * 
	 * @param attributeName The name of the session attribute to clear.
	 * @throws NullPointerException If the specified attribute name is
	 *           <code>null</code>.
	 */
	void clearAttribute(String attributeName) throws NullPointerException;
	
	Object getInheritedAttribute(String attributeName) throws NullPointerException;

	/**
	 * Returns the names of the attributes set in the session.
	 * 
	 * @return The names of the attributes set in the session.
	 */
	String[] getRootAttributeNames();

	/**
	 * Returns the value of the session attribute with the specified name or
	 * <code>null</code> if no such attribute exists.
	 * 
	 * @param attributeName The name of the session attribute to return.
	 * @return The value of the session attribute with the specified name or
	 *         <code>null</code> if no such attribute exists.
	 * @throws NullPointerException If the specified attribute name is
	 *           <code>null</code>.
	 */
	Object getRootAttribute(String attributeName) throws NullPointerException;

	/**
	 * Sets the value of the specified session attribute or clears it if the
	 * supplied value is <code>null</code>.
	 * 
	 * @param attributeName The name of the session attribute to set.
	 * @param attributeValue The value to set the attribute to or
	 *          <code>null</code> to clear the attribute.
	 * @throws NullPointerException If the specified attribute name is
	 *           <code>null</code>.
	 */
	void setRootAttribute(String attributeName, Object attributeValue)
			throws NullPointerException;

	/**
	 * Clears the value of the specified session attribute.
	 * 
	 * @param attributeName The name of the session attribute to clear.
	 * @throws NullPointerException If the specified attribute name is
	 *           <code>null</code>.
	 */
	void clearRootAttribute(String attributeName) throws NullPointerException;
}
