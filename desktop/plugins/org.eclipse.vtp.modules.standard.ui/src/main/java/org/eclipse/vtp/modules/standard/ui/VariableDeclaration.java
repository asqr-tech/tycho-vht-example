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
/**
 *
 */
package org.eclipse.vtp.modules.standard.ui;

import org.eclipse.vtp.desktop.model.core.FieldType;


/**
 * @author Trip
 *
 */
public class VariableDeclaration implements Cloneable
{
	String name = null;
	FieldType type = null;
	int valueType = 0;
	String value = null;
	boolean secure = false;

	/**
	 *
	 */
	public VariableDeclaration(String name, FieldType type,
		int valueType, String value)
	{
		super();
		this.name = name;
		this.type = type;
		this.valueType = valueType;
		this.value = value;
	}

	public VariableDeclaration(String name, FieldType type,
			int valueType, String value, boolean secure)
	{
		super();
		this.name = name;
		this.type = type;
		this.valueType = valueType;
		this.value = value;
		this.secure = secure;
	}

	public String getName()
    {
    	return name;
    }

	public void setName(String name)
    {
    	this.name = name;
    }

	public FieldType getType()
    {
    	return type;
    }

	public void setType(FieldType type)
    {
    	this.type = type;
    }

	public int getValueType()
    {
    	return valueType;
    }

	public void setValueType(int valueType)
    {
    	this.valueType = valueType;
    }

	public String getValue()
    {
    	return value;
    }

	public void setValue(String value)
    {
    	this.value = value;
    }

	public boolean isSecure()
	{
		return secure;
	}
	
	public void setSecure(boolean secure)
	{
		this.secure = secure;
	}

	public Object clone()
	{
		return new VariableDeclaration(name, type, valueType,
			value);
	}
}
