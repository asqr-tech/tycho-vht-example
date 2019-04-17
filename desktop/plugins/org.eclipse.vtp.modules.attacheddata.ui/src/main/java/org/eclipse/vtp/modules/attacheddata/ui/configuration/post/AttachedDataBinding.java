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
package org.eclipse.vtp.modules.attacheddata.ui.configuration.post;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class represents a named set of configuration bindings for attached
 * data submissions.  Items and their entries are stored as children to the
 * binding based on the brand, interaction type, and language.
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
	 * @see org.eclipse.vtp.desktop.core.configuration.attacheddata.GenericBinding#readBindingItem(org.w3c.dom.Element)
	 */
	public GenericBindingItem readBindingItem(Element itemConfiguration) {
		AttachedDataBindingItem item = new AttachedDataBindingItem();
		NodeList contentList = itemConfiguration.getChildNodes();
		for (int i = 0; i < contentList.getLength(); i++) {
			if (!(contentList.item(i) instanceof Element))
				continue;
			AttachedDataItemEntry entry = new AttachedDataItemEntry();
			Element entryElement = (Element)contentList.item(i);
			entry.setName(entryElement.getAttribute("name"));
			String dataTypeString = entryElement.getAttribute("type"); //$NON-NLS-1$
			int dataType = AttachedDataItemEntry.TYPE_STATIC;
			if ("".equalsIgnoreCase(dataTypeString)) //$NON-NLS-1$
				dataType = AttachedDataItemEntry.TYPE_STATIC;
			else if ("static".equalsIgnoreCase(dataTypeString)) //$NON-NLS-1$
				dataType = AttachedDataItemEntry.TYPE_STATIC;
			else if ("expression".equalsIgnoreCase(dataTypeString)) //$NON-NLS-1$
				dataType = AttachedDataItemEntry.TYPE_EXPRESSION;
			else if ("variable".equalsIgnoreCase(dataTypeString)) //$NON-NLS-1$
				dataType = AttachedDataItemEntry.TYPE_VARIABLE;
			else if ("map".equalsIgnoreCase(dataTypeString)) //$NON-NLS-1$
				dataType = AttachedDataItemEntry.TYPE_MAP;
			else
				dataType = Integer.parseInt(dataTypeString);
			entry.setDataType(dataType);
			entry.setValue(entryElement.getAttribute("value"));
			item.addEntry(entry);
		}
		return item;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.attacheddata.GenericBinding#writeBindingItem(org.eclipse.vtp.desktop.core.configuration.attacheddata.GenericBindingItem, org.w3c.dom.Element)
	 */
	public void writeBindingItem(GenericBindingItem item, Element itemConfiguration) {
		for (Iterator<AttachedDataItemEntry> i = ((AttachedDataBindingItem) item).getEntries().iterator(); i
				.hasNext();) {
			AttachedDataItemEntry entry = i.next();
			if (entry.getValue() == null)
				continue;
			Element entryElement = itemConfiguration.getOwnerDocument().createElement("entry");
			itemConfiguration.appendChild(entryElement);
			entryElement.setAttribute("name", entry.getName());
			String dataTypeString = "static"; //$NON-NLS-1$
			switch(entry.getDataType())
			{
			case AttachedDataItemEntry.TYPE_EXPRESSION:
				dataTypeString = "expression"; //$NON-NLS-1$
				break;
			case AttachedDataItemEntry.TYPE_VARIABLE:
				dataTypeString = "variable"; //$NON-NLS-1$
				break;
			case AttachedDataItemEntry.TYPE_MAP:
				dataTypeString = "map";
				break;
			}
			entryElement.setAttribute("type", dataTypeString);
			entryElement.setAttribute("value", entry.getValue());
		}
	}
}
