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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Configuration for a set of meta-data.
 * 
 * @author Lonnie Pryor
 */
public class MetaDataConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** Index of meta data items by brand, interaction type, and language. */
	private final Map items = new HashMap();
	private boolean ignoreErrors = false;

	/**
	 * Creates a new MetaDataMessageConfiguration.
	 * 
	 */
	public MetaDataConfiguration()
	{
	}
	
	public void setIgnoreErrors(boolean ignore)
	{
		this.ignoreErrors = ignore;
	}

	/**
	 * Returns the items configured for the specified brand, interaction type, and
	 * language or <code>null</code> if no such item is configured.
	 * 
	 * @param key The key the items are to be found under.
	 * @return The items configured for the specified brand, interaction type, and
	 *         language or <code>null</code> if no such item is configured.
	 */
	public MetaDataItemConfiguration[] getItem(String key)
	{
		return (MetaDataItemConfiguration[])items.get(key);
	}
	
	public Map getItemMap()
	{
		return Collections.unmodifiableMap(items);
	}

	/**
	 * Sets the items configured for the specified brand, interaction type, and
	 * language.
	 * 
	 * @param key The key the items are to be stored under.
	 * @param items The items to set as the configuration or <code>null</code>
	 *          to remove the specified configuration.
	 */
	public void setItem(String key, MetaDataItemConfiguration[] items)
	{
		if (items == null)
			this.items.remove(key);
		else
			this.items.put(key, items);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		items.clear();
		NodeList setElements = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, NAME_META_DATA_SET);
		for (int i = 0; i < setElements.getLength(); ++i)
		{
			Element setElement = (Element)setElements.item(i);
			String key = setElement.getAttribute(NAME_KEY);
			NodeList itemElements = setElement.getElementsByTagNameNS(NAMESPACE_URI,
					NAME_META_DATA_ITEM);
			MetaDataItemConfiguration[] metaData = new MetaDataItemConfiguration[itemElements
					.getLength()];
			for (int j = 0; j < itemElements.getLength(); j++)
			{
				MetaDataItemConfiguration item = new MetaDataItemConfiguration();
				item.load((Element)itemElements.item(j));
				metaData[j] = item;
			}
			items.put(key, metaData);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		String metaDataSetName = NAME_META_DATA_SET;
		String metaDataItemName = NAME_META_DATA_ITEM;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
		{
			metaDataSetName = prefix + ":" + metaDataSetName; //$NON-NLS-1$
			metaDataItemName = prefix + ":" + metaDataItemName; //$NON-NLS-1$
		}
		for (Iterator i = items.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			Element setElement = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, metaDataSetName);
			MetaDataItemConfiguration[] metaData = (MetaDataItemConfiguration[])entry
					.getValue();
			for (int j = 0; j < metaData.length; ++j)
			{
				Element itemElement = configurationElement.getOwnerDocument()
						.createElementNS(NAMESPACE_URI, metaDataItemName);
				metaData[j].save(itemElement);
				setElement.appendChild(itemElement);
			}
			setElement.setAttribute(NAME_KEY, (String)entry.getKey());
			configurationElement.appendChild(setElement);
		}
	}
}
