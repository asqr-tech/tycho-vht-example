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
import org.eclipse.vtp.framework.interactions.core.media.IInputGrammarFactory;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * InputConfiguration.
 * 
 * @author Lonnie Pryor
 */
public class InputConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** The input factory to use. */
	private final IInputGrammarFactory inputFactory;
	/** Index of input grammars by brand, interaction type, and language. */
	private final Map items = new HashMap();

	/**
	 * Creates a new InputConfiguration.
	 * 
	 * @param inputFactory The input factory to use.
	 */
	public InputConfiguration(IInputGrammarFactory inputFactory)
	{
		this.inputFactory = inputFactory;
	}

	/**
	 * Returns the item configured for the specified brand, interaction type, and
	 * language or <code>null</code> if no such item is configured.
	 * 
	 * @param brandName The path of the brand to find the item for.
	 * @param interactionTypeID The ID of the interaction type to find the item
	 *          for.
	 * @param languageID The ID of the language to find the item for.
	 * @return The item configured for the specified brand, interaction type, and
	 *         language or <code>null</code> if no such item is configured.
	 */
	public InputGrammar getItem(String brandName, String interactionTypeID,
			String languageID)
	{
		return (InputGrammar)items.get(brandName + interactionTypeID + languageID);
	}

	/**
	 * Sets the item configured for the specified brand, interaction type, and
	 * language.
	 * 
	 * @param brandName The path of the brand to set the item for.
	 * @param interactionTypeID The ID of the interaction type to set the item
	 *          for.
	 * @param languageID The ID of the language to set the item for.
	 * @param item The item to set as the configuration or <code>null</code> to
	 *          remove the specified configuration.
	 */
	public void setItem(String brandName, String interactionTypeID,
			String languageID, InputGrammar item)
	{
		setItem(brandName + interactionTypeID + languageID, item);
	}

	/**
	 * Sets the item configured for the specified brand, interaction type, and
	 * language.
	 * 
	 * @param key The key to set the item for.
	 * @param item The item to set as the configuration or <code>null</code> to
	 *          remove the specified configuration.
	 */
	public void setItem(String key, InputGrammar item)
	{
		if (item == null)
			items.remove(key);
		else
			items.put(key, item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		items.clear();
		NodeList itemElements = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, NAME_INPUT_ITEM);
		for (int i = 0; i < itemElements.getLength(); ++i)
		{
			Element itemElement = (Element)itemElements.item(i);
			String key = itemElement.getAttribute(NAME_KEY);
			NodeList inputElements = itemElement.getChildNodes();
			InputGrammar input = null;
			for (int j = 0; input == null && j < inputElements.getLength(); j++)
				if (inputElements.item(j) instanceof Element)
					input = inputFactory.loadInput((Element)inputElements.item(j));
			if (input != null)
				items.put(key, input);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		String inputItemName = NAME_INPUT_ITEM;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
			inputItemName = prefix + ":" + inputItemName; //$NON-NLS-1$
		for (Iterator i = items.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, inputItemName);
			((InputGrammar)entry.getValue()).store(element);
			element.setAttribute(NAME_KEY, (String)entry.getKey());
			configurationElement.appendChild(element);
		}
	}
}
