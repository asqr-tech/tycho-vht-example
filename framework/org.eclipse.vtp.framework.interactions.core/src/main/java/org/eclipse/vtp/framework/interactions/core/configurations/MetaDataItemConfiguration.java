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
package org.eclipse.vtp.framework.interactions.core.configurations;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;

/**
 * A single meta-data item configuration.
 * 
 * @author Lonnie Pryor
 */
public class MetaDataItemConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** A static variable value. */
	public static final int TYPE_STATIC = 0;
	/** A variable value derived from an expression. */
	public static final int TYPE_EXPRESSION = 1;
	/** A variable value derived from another variable. */
	public static final int TYPE_VARIABLE = 2;
	public static final int TYPE_MAP = 4;

	/** The name of the target variable. */
	private String name = ""; //$NON-NLS-1$
	/** The type of the variable value. */
	private int type = TYPE_STATIC;
	/** The variable value. */
	private String value = ""; //$NON-NLS-1$
	/** The scripting language the expression is in. */
	private String scriptingLanguage = null;

	/**
	 * Creates a new MetaDataItemConfiguration.
	 */
	public MetaDataItemConfiguration()
	{
	}

	/**
	 * Returns the name of the target variable.
	 * 
	 * @return The name of the target variable.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the target variable.
	 * 
	 * @param name The name of the target variable.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the type of the variable value.
	 * 
	 * @return The type of the variable value.
	 */
	public int getValueType()
	{
		return type;
	}

	/**
	 * Returns the variable value.
	 * 
	 * @return The variable value.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Returns the scripting language the expression is in.
	 * 
	 * @return The scripting language the expression is in.
	 */
	public String getScriptingLanguage()
	{
		return scriptingLanguage;
	}

	/**
	 * Sets the value of the target variable as a static value.
	 * 
	 * @param value The value of the target variable.
	 */
	public void setStaticValue(String value)
	{
		this.type = TYPE_STATIC;
		this.value = value == null ? "" : value; //$NON-NLS-1$
		this.scriptingLanguage = null;
	}

	/**
	 * Sets the value of the target variable as the result of an expression.
	 * 
	 * @param expression The expression to evaluate.
	 * @param scriptingLanguage The scripting language the expression is in.
	 */
	public void setExpressionValue(String expression, String scriptingLanguage)
	{
		this.type = TYPE_EXPRESSION;
		this.value = expression == null ? "" : expression; //$NON-NLS-1$
		this.scriptingLanguage = scriptingLanguage;
	}

	/**
	 * Sets the value of the target variable as an existing variable.
	 * 
	 * @param variableName The name of the variable to set as the target variable.
	 */
	public void setVariableValue(String variableName)
	{
		this.type = TYPE_VARIABLE;
		this.value = variableName == null ? "" : variableName; //$NON-NLS-1$
		this.scriptingLanguage = null;
	}

	public void setMapValue(String variableName)
	{
		this.type = TYPE_MAP;
		this.value = variableName == null ? "" : variableName; //$NON-NLS-1$
		this.scriptingLanguage = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(
	 *      org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		name = configurationElement.getAttribute(NAME_NAME);
		String typeString = configurationElement.getAttribute(NAME_TYPE);
		if (typeString.equals("")) //$NON-NLS-1$
			type = TYPE_STATIC;
		else if ("static".equalsIgnoreCase(typeString)) //$NON-NLS-1$
			type = TYPE_STATIC;
		else if ("expression".equalsIgnoreCase(typeString)) //$NON-NLS-1$
			type = TYPE_EXPRESSION;
		else if ("variable".equalsIgnoreCase(typeString)) //$NON-NLS-1$
			type = TYPE_VARIABLE;
		else if ("map".equalsIgnoreCase(typeString))
			type = TYPE_MAP;
		else
			type = Integer.parseInt(typeString);
		value = configurationElement.getAttribute(NAME_VALUE);
		if (configurationElement.hasAttribute(NAME_SCRIPTING_LANGUGAGE))
			scriptingLanguage = configurationElement
					.getAttribute(NAME_SCRIPTING_LANGUGAGE);
		else
			scriptingLanguage = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(
	 *      org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute(NAME_NAME, name);
		String typeString = "static"; //$NON-NLS-1$
		switch (type)
		{
		case TYPE_EXPRESSION:
			typeString = "expression"; //$NON-NLS-1$
			break;
		case TYPE_VARIABLE:
			typeString = "variable"; //$NON-NLS-1$
			break;
		case TYPE_MAP:
			typeString = "map";
			break;
		}
		configurationElement.setAttribute(NAME_TYPE, typeString);
		configurationElement.setAttribute(NAME_VALUE, value);
		if (scriptingLanguage != null)
			configurationElement.setAttribute(NAME_SCRIPTING_LANGUGAGE,
					scriptingLanguage);
	}
}
