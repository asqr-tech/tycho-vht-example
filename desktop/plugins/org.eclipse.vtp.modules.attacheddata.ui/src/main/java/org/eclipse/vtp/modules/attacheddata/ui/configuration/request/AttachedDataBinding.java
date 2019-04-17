/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.modules.attacheddata.ui.configuration.request;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class represents a named set of configuration bindings for attached
 * data requests.  Items and their entries are stored as children to the binding
 * based on the brand, interaction type, and language.
 * 
 * @author trip
 */
public class AttachedDataBinding extends GenericBinding
{

	/**
	 * Creates a new <code>AttachedDataBinding</code> instance with the given
	 * name and managed by the <code>AttachedDataManager</code> instance
	 * provided.
	 * 
	 * @param manager The binding container to manage this binding
	 * @param name The name of this binding
	 * @see GenericBinding#GenericBinding(AttachedDataManager, String)
	 */
	public AttachedDataBinding(AttachedDataManager manager, String name)
	{
		super(manager, name);
	}

	/**
	 * Locates the <code>AttachedDataBindingItem</code> mapped to the given
	 * brand, interactionType, and language.  If there is not an item already
	 * mapped to the provided information a new binding item is created and
	 * returned.
	 * 
	 * @param brand The brand the binding item pertains to
	 * @param interactionType The interaction type the binding item is valid for
	 * @param language The language the binding item is associated with
	 * @return The binding item registered under the given brand, interaction
	 * type, and language, or a new binding item instance if none was registered
	 * previously.
	 */
	public AttachedDataBindingItem getAttachedDataItem(String brand,
													   String interactionType,
													   String language)
	{
		AttachedDataBindingItem attachedDataBinding =
			(AttachedDataBindingItem)super.getItem(brand, interactionType, language);
		if (attachedDataBinding == null) // no current items
		{
			attachedDataBinding = new AttachedDataBindingItem();
		}
		return attachedDataBinding;
	}

	/**
	 * Registers the binding item instance under the given brand, interaction
	 * type, and language.  Any previously mapped binding item will be replaced
	 * and disposed of.
	 * 
	 * @param brand The brand to map the binding item to
	 * @param interactionType The interaction type to map the binding item to
	 * @param language The language associated with the binding item
	 * @param item The binding item to map to the given information.
	 */
	public void putAttachedDataItem(String brand,
									String interactionType,
									String language,
									AttachedDataBindingItem item)
	{
		super.putItem(brand, interactionType, language, item);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.adrequest.GenericBinding#readBindingItem(org.w3c.dom.Element)
	 */
	protected GenericBindingItem readBindingItem(Element itemConfiguration)
	{
		AttachedDataBindingItem item = new AttachedDataBindingItem();
		NodeList contentList = itemConfiguration.getChildNodes();
		for (int i = 0; i < contentList.getLength(); i++)
		{
			if (!(contentList.item(i) instanceof Element))
				continue;
			AttachedDataItemEntry entry = new AttachedDataItemEntry();
			Element entryElement = (Element)contentList.item(i);
			entry.setName(entryElement.getAttribute("name")); //$NON-NLS-1$
			entry.setValue(entryElement.getAttribute("value")); //$NON-NLS-1$
			item.addEntry(entry);
		}
		return item;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.adrequest.GenericBinding#writeBindingItem(org.eclipse.vtp.desktop.core.configuration.adrequest.GenericBindingItem, org.w3c.dom.Element)
	 */
	protected void writeBindingItem(GenericBindingItem item, Element itemConfiguration)
	{
		for (AttachedDataItemEntry entry : ((AttachedDataBindingItem)item).getEntries())
		{
			if (entry.getValue() == null)
				continue;
			Element entryElement = itemConfiguration.getOwnerDocument().createElement("entry");
			itemConfiguration.appendChild(entryElement);
			entryElement.setAttribute("name", entry.getName()); //$NON-NLS-1$
			entryElement.setAttribute("value", entry.getValue()); //$NON-NLS-1$
		}
	}
}
