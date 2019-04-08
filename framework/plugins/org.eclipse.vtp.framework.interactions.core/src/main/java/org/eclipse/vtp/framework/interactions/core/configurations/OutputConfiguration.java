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
import org.eclipse.vtp.framework.interactions.core.media.IContentFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * OutputConfiguration.
 * 
 * @author Lonnie Pryor
 */
public class OutputConfiguration implements IConfiguration,
		InteractionsConstants {
	/** The content factory to use. */
	private final IContentFactory contentFactory;
	/** Index of content arrays by brand, interaction type, and language. */
	private final Map<String, OutputNode[]> items = new HashMap<String, OutputNode[]>();

	/**
	 * Creates a new OutputConfiguration.
	 * 
	 * @param contentFactory
	 *            The content factory to use.
	 */
	public OutputConfiguration(IContentFactory contentFactory) {
		this.contentFactory = contentFactory;
	}

	/**
	 * Returns the item configured for the specified brand, interaction type,
	 * and language or <code>null</code> if no such item is configured.
	 * 
	 * @param brandName
	 *            The path of the brand to find the item for.
	 * @param interactionTypeID
	 *            The ID of the interaction type to find the item for.
	 * @param languageID
	 *            The ID of the language to find the item for.
	 * @return The item configured for the specified brand, interaction type,
	 *         and language or <code>null</code> if no such item is configured.
	 */
	public OutputNode[] getItem(String brandName, String interactionTypeID,
			String languageID) {
		return items.get(brandName + interactionTypeID + languageID);
	}

	/**
	 * Sets the item configured for the specified brand, interaction type, and
	 * language.
	 * 
	 * @param brandName
	 *            The path of the brand to set the item for.
	 * @param interactionTypeID
	 *            The ID of the interaction type to set the item for.
	 * @param languageID
	 *            The ID of the language to set the item for.
	 * @param item
	 *            The item to set as the configuration or <code>null</code> to
	 *            remove the specified configuration.
	 */
	public void setItem(String brandName, String interactionTypeID,
			String languageID, OutputNode[] item) {
		setItem(brandName + interactionTypeID + languageID, item);
	}

	/**
	 * Sets the item configured for the specified brand, interaction type, and
	 * language.
	 * 
	 * @param key
	 *            The key to set the item for.
	 * @param item
	 *            The item to set as the configuration or <code>null</code> to
	 *            remove the specified configuration.
	 */
	public void setItem(String key, OutputNode[] item) {
		if (item == null)
			items.remove(key);
		else
			items.put(key, item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement) {
		items.clear();
		NodeList outputItemElements = configurationElement
				.getElementsByTagNameNS(NAMESPACE_URI, NAME_OUTPUT_ITEM);
		for (int i = 0; i < outputItemElements.getLength(); ++i) {
			Element outputItemElement = (Element) outputItemElements.item(i);
			String key = outputItemElement.getAttribute(NAME_KEY);
			items.put(key,
					OutputNode.loadAll(outputItemElement, contentFactory));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement) {
		String outputItemName = NAME_OUTPUT_ITEM;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
			outputItemName = prefix + ":" + outputItemName; //$NON-NLS-1$
		for (Iterator<Map.Entry<String, OutputNode[]>> i = items.entrySet()
				.iterator(); i.hasNext();) {
			Map.Entry<String, OutputNode[]> entry = i.next();
			Element outputItemElement = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, outputItemName);
			for (OutputNode node : entry.getValue())
				node.save(outputItemElement);
			outputItemElement.setAttribute(NAME_KEY, entry.getKey());
			configurationElement.appendChild(outputItemElement);
		}
	}

}
