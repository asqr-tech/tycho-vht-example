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
package org.eclipse.vtp.framework.common.configurations;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;

/**
 * A configuration describing how to map a variable into a target process.
 * 
 * @author Lonnie Pryor
 */
public class VariableMappingConfiguration implements IConfiguration,
		CommonConstants
{
	/** Do not initialize the variable. */
	public static final int TYPE_NONE = 0;
	/** Initialize variable to a static value. */
	public static final int TYPE_STATIC = 1;
	/** Initialize variable with an expression. */
	public static final int TYPE_EXPRESSION = 2;
	/** Initialize variable with an expression. */
	public static final int TYPE_VARIABLE = 3;

	/** The type of mapping to perform. */
	private int type = TYPE_NONE;
	/** The value to use for the mapping. */
	private String value = null;
	/** The scripting language to use if the value is an expression. */
	private String scriptingLangugage = null;

	/**
	 * Creates a new VariableMapping.
	 * 
	 */
	public VariableMappingConfiguration()
	{
	}

	/**
	 * Returns the type of mapping to perform.
	 * 
	 * @return The type of mapping to perform.
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Returns the value to use for the mapping.
	 * 
	 * @return The value to use for the mapping.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Returns the scripting language to use if the value is an expression.
	 * 
	 * @return The scripting language to use if the value is an expression.
	 */
	public String getScriptingLangugage()
	{
		return scriptingLangugage;
	}

	/**
	 * Configures this mapping to have no value set.
	 */
	public void setNoValue()
	{
		this.type = TYPE_NONE;
		this.scriptingLangugage = null;
		this.value = null;
	}

	/**
	 * Configures this mapping to have a static value set.
	 * 
	 * @param value The value to set the variable to.
	 */
	public void setStaticValue(String value)
	{
		this.type = TYPE_STATIC;
		this.value = value == null ? "" : //$NON-NLS-1$
				value;
		this.scriptingLangugage = null;
	}

	/**
	 * Configures this mapping to have the result of an expression set as the
	 * value.
	 * 
	 * @param expression THe expression to evaluate.
	 * @param scriptingLangugage The language the expression is defined in.
	 */
	public void setExpressionValue(String expression, String scriptingLangugage)
	{
		this.type = TYPE_EXPRESSION;
		this.value = expression == null ? "" : //$NON-NLS-1$
				expression;
		this.scriptingLangugage = scriptingLangugage == null ? "" : //$NON-NLS-1$
				scriptingLangugage;
	}

	/**
	 * Configures this mapping to have a variable set as the value.
	 * 
	 * @param variableName The name of the variable to use as the value.
	 */
	public void setVariableValue(String variableName)
	{
		this.type = TYPE_VARIABLE;
		this.value = variableName == null ? "" : //$NON-NLS-1$
				variableName;
		this.scriptingLangugage = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		String typeName = configurationElement.getAttribute(NAME_TYPE);
		if (typeName.length() == 0)
			type = TYPE_NONE;
		else if (MAPPING_TYPE_NONE.equalsIgnoreCase(typeName))
			type = TYPE_NONE;
		else if (MAPPING_TYPE_STATIC.equalsIgnoreCase(typeName))
			type = TYPE_STATIC;
		else if (MAPPING_TYPE_EXPRESSION.equalsIgnoreCase(typeName))
			type = TYPE_EXPRESSION;
		else if (MAPPING_TYPE_VARIABLE.equalsIgnoreCase(typeName))
			type = TYPE_VARIABLE;
		else
			this.type = Integer.parseInt(typeName);
		if (type == TYPE_NONE)
			value = null;
		else
			value = configurationElement.getAttribute(NAME_VALUE);
		if (type == TYPE_EXPRESSION)
			scriptingLangugage = configurationElement
					.getAttribute(NAME_SCRIPTING_LANGUGAGE);
		else
			scriptingLangugage = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		switch (type)
		{
		case TYPE_STATIC:
			configurationElement.setAttribute(NAME_TYPE, MAPPING_TYPE_STATIC);
			configurationElement.setAttribute(NAME_VALUE, value);
			break;
		case TYPE_EXPRESSION:
			configurationElement.setAttribute(NAME_TYPE, MAPPING_TYPE_EXPRESSION);
			configurationElement.setAttribute(NAME_VALUE, value);
			configurationElement.setAttribute(NAME_SCRIPTING_LANGUGAGE,
					scriptingLangugage);
			break;
		case TYPE_VARIABLE:
			configurationElement.setAttribute(NAME_TYPE, MAPPING_TYPE_VARIABLE);
			configurationElement.setAttribute(NAME_VALUE, value);
			break;
		default:
			configurationElement.setAttribute(NAME_TYPE, MAPPING_TYPE_NONE);
		}
	}
}
