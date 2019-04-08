/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.webservices.configurations.document;

import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This binding item implementation represents a property value.  Many elements
 * used in applications require different property settings.
 * 
 * @author trip
 */
public class BindingValue
{
	/**	Constant indicating the value is static */
	public static final String STATIC = "static";
	/**	Constant indicating the value is an expression */
	public static final String EXPRESSION = "expression";
	/**	Constant indicating the value is a variable name */
	public static final String VARIABLE = "variable";
	
	/**	The type of value contained by this property */
	private String valueType = STATIC;
	/** The current value of this property */
	private String value = null;

	/**
	 * @return The type of value contained by this property
	 */
	public String getValueType()
	{
		return valueType;
	}
	
	/**
	 * @return The current value of this property
	 */
	public String getValue()
	{
		return value;
	}
	
	/**
	 * Sets the type of this property's value with the given type identifier.
	 * 
	 * @param valueType The type of this property's value
	 */
	public void setValueType(String valueType)
	{
		this.valueType = valueType;
	}
	
	/**
	 * Sets the value of this property to the given value.
	 * 
	 * @param value The value of this property
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Convenience method to simultaneously set the value and value type of this
	 * property.
	 * 
	 * @param value The static value of this property
	 */
	public void setStaticValue(String value)
	{
		valueType = STATIC;
		this.value = value;
	}
	
	/**
	 * Convenience method to simultaneously set the value and value type of this
	 * property.
	 * 
	 * @param expression The expression to use for this property
	 */
	public void setExpression(String expression)
	{
		valueType = EXPRESSION;
		this.value = expression;
	}
	
	/**
	 * Convenience method to simultaneously set the value and value type of this
	 * property.
	 * 
	 * @param variable The variable name to use for this property
	 */
	public void setVariable(String variable)
	{
		valueType = VARIABLE;
		this.value = variable;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.generic.BindingItem#readConfiguration(org.w3c.dom.Element)
	 */
	public void readConfiguration(Element configuration)
	{
		NodeList propertyValueElementList = configuration.getElementsByTagName("property-value");
		if(propertyValueElementList.getLength() > 0)
		{
			Element propertyValueElement = (Element)propertyValueElementList.item(0);
			String valueTypeAtt = propertyValueElement.getAttribute("value-type");
			if(VARIABLE.equalsIgnoreCase(valueTypeAtt))
				valueType = VARIABLE;
			else if(EXPRESSION.equalsIgnoreCase(valueTypeAtt))
				valueType = EXPRESSION;
	        value = XMLUtilities.getElementTextDataNoEx(propertyValueElement, true);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.generic.BindingItem#writeConfiguration(org.w3c.dom.Element)
	 */
	public void writeConfiguration(Element configuration)
	{
		Element propertyValueElement = configuration.getOwnerDocument().createElement("property-value");
		configuration.appendChild(propertyValueElement);
		propertyValueElement.setAttribute("value-type", valueType);
		propertyValueElement.setTextContent(value);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		BindingValue copy = new BindingValue();
		copy.valueType = valueType;
		copy.value = value;
		return copy;
	}
}
