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
 * A service that manages a collection of named data variables.
 * 
 * @author Lonnie Pryor
 */
public interface IVariableRegistry
{
	/**
	 * Creates a new variable of the specified type.
	 * 
	 * @param typeName The name of the type of variable to create.
	 * @return A new variable of the specified type.
	 * @throws IllegalArgumentException If no type with the specified name exists.
	 * @throws NullPointerException If the supplied type name is <code>null</code>.
	 */
	IDataObject createVariable(String typeName) throws IllegalArgumentException,
			NullPointerException;

	IDataObject createVariable(String typeName, boolean secured) throws IllegalArgumentException,
	NullPointerException;
	
	IDataObject createVariable(String typeName, String id) throws IllegalArgumentException,
	NullPointerException;

	/**
	 * Creates a new variable of the specified type.
	 * 
	 * @param type The type of variable to create.
	 * @return A new variable of the specified type.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 */
	IDataObject createVariable(IDataType type) throws IllegalArgumentException,
			NullPointerException;

	IDataObject createVariable(IDataType type, boolean secured) throws IllegalArgumentException,
	NullPointerException;

	IDataObject createVariable(IDataType type, String id) throws IllegalArgumentException,
	NullPointerException;

	/**
	 * Returns the names of all the variables currently registered.
	 * 
	 * @return The names of all the variables currently registered.
	 */
	String[] getVariableNames();

	/**
	 * Returns the variable registered under the specified name or
	 * <code>null</code> if no such variable is registered.
	 * 
	 * @param name The name of the variable to return.
	 * @return The variable registered under the specified name or
	 *         <code>null</code> if no such variable is registered.
	 * @throws NullPointerException If the supplied variable name is
	 *           <code>null</code>.
	 */
	IDataObject getVariable(String name) throws NullPointerException;

	/**
	 * Registers a variable under the specified name, removing any variable
	 * previously registered under that name.
	 * 
	 * @param name The name to register the variable under.
	 * @throws IllegalArgumentException If the supplied variable was not created
	 *           by this registry.
	 * @throws NullPointerException If the supplied variable name is
	 *           <code>null</code>.
	 * @throws NullPointerException If the supplied variable is <code>null</code>.
	 */
	void setVariable(String name, IDataObject variable)
			throws IllegalArgumentException, NullPointerException;

	/**
	 * Removes the registration of the variable under the specified name
	 * 
	 * @param name The name of the variable to clear.
	 * @throws NullPointerException If the supplied variable name is
	 *           <code>null</code>.
	 */
	void clearVariable(String name) throws NullPointerException;
}
