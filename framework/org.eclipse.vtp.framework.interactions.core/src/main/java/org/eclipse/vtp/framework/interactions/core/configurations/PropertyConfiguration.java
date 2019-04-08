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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * PropertyConfiguration.
 * 
 * @author Lonnie Pryor
 */
public class PropertyConfiguration implements IConfiguration,
		InteractionsConstants
{
	/**	Constant indicating the value is static */
	public static final String STATIC = "static";
	/**	Constant indicating the value is an expression */
	public static final String EXPRESSION = "expression";
	/**	Constant indicating the value is a variable name */
	public static final String VARIABLE = "variable";
	/** Index of values by brand, interaction type, and language. */
	private final Map<String, Value> items = new HashMap<String, Value>();

	/**
	 * Creates a new PropertyConfiguration.
	 */
	public PropertyConfiguration()
	{
	}

	/**
	 * Returns the item configured for the specified brand, interaction type, and
	 * language or <code>null</code> if no such item is configured.
	 * 
	 * @param brandName The path of the brand to find the item for.
	 * @param interactionTypeID The ID of the interaction type to find the item
	 *          for.
	 * @return The item configured for the specified brand, interaction type, and
	 *         language or <code>null</code> if no such item is configured.
	 */
	public Value getItem(String brandName, String interactionTypeID)
	{
		return getItem(brandName, interactionTypeID, "");
	}
	
	public Value getItem(String brandName, String interactionTypeID, String language)
	{
		String key = brandName + interactionTypeID + language;
		return items.get(key);
	}

	/**
	 * Sets the item configured for the specified brand, interaction type, and
	 * language.
	 * 
	 * @param brandName The path of the brand to set the item for.
	 * @param interactionTypeID The ID of the interaction type to set the item
	 *          for.
	 * @param item The item to set as the configuration or <code>null</code> to
	 *          remove the specified configuration.
	 */
	public void setItem(String brandName, String interactionTypeID, String type, String value)
	{
		setItem(brandName + interactionTypeID, new Value(type, value));
	}

	/**
	 * Sets the item configured for the specified key.
	 * 
	 * @param key The key to set the item for.
	 * @param item The item to set as the configuration or <code>null</code> to
	 *          remove the specified configuration.
	 */
	public void setItem(String key, String type, String value)
	{
		setItem(key, new Value(type, value));
	}
	
	public void setItem(String key, Value value)
	{
		if (value == null)
			items.remove(key);
		else
			items.put(key, value);
	}

	public void setItem(String brandName, String interactionTypeID, String language, String type, String value)
	{
		setItem(brandName + interactionTypeID + language, new Value(type, value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		items.clear();
		NodeList propertyItemElements = configurationElement
				.getElementsByTagNameNS(NAMESPACE_URI, NAME_PROPERTY_ITEM);
		for (int i = 0; i < propertyItemElements.getLength(); ++i)
		{
			Element propertyItemElement = (Element)propertyItemElements.item(i);
			String key = propertyItemElement.getAttribute(NAME_KEY);
			String type = propertyItemElement.getAttribute("type");
			if(type == null || "".equals(type))
				type = STATIC;
			try
			{
				items.put(key, new Value(type, XMLUtilities.getElementTextData(propertyItemElement,
						true)));
			}
			catch (Exception e)
			{
				throw new IllegalArgumentException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		String propertyItemName = NAME_PROPERTY_ITEM;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
			propertyItemName = prefix + ":" + propertyItemName; //$NON-NLS-1$
		for (Iterator<Map.Entry<String, Value>> i = items.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry<String, Value> entry = i.next();
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, propertyItemName);
			element.setAttribute(NAME_KEY, entry.getKey());
			element.setAttribute("type", entry.getValue().getType());
			element.appendChild(configurationElement.getOwnerDocument()
					.createTextNode(entry.getValue().getValue()));
			configurationElement.appendChild(element);
		}
	}
	
	public class Value
	{
		String type = null;
		String value = null;
		
		public Value(String type, String value)
		{
			super();
			this.type = type;
			this.value = value;
		}
		
		public String getType()
		{
			return type;
		}
		
		public String getValue()
		{
			return value;
		}
	}
}
