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
 * IScriptable.
 * 
 * @author Lonnie Pryor
 */
public interface IScriptable
{
	String getName();
	
	boolean hasValue();
	
	Object toValue();
	
	String[] getFunctionNames();
	
	String[] getPropertyNames();

	Object invokeFunction(String name, Object[] arguments) throws Exception;

	boolean hasItem(int index);

	Object getItem(int index);

	boolean setItem(int index, Object value);

	boolean clearItem(int index);

	boolean hasEntry(String name);

	Object getEntry(String name);

	boolean setEntry(String name, Object value);

	boolean clearEntry(String name);
	
	boolean isMutable();
}
