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
package org.eclipse.vtp.desktop.model.interactive.core.configuration.generic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This implementation of the binding item interface represents a prompt.
 * Prompts are the output of an application. A prompt can contain any number of
 * content entries.
 * 
 * @author trip
 */
public class PromptBindingItem implements BindingItem {
	/** The unique identifier for this binding item type */
	public static final String TYPE_ID = "org.eclipse.vtp.configuration.generic.items.prompt";

	/** The list of this binding item's entries */
	private List<PromptBindingNode> entries = new ArrayList<PromptBindingNode>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.core.configuration.generic.BindingItem#getType()
	 */
	public String getType() {
		return TYPE_ID;
	}

	/**
	 * Removes all the entries currently contained by this binding item.
	 */
	public void clearEntries() {
		entries.clear();
	}

	/**
	 * @return A list of the entries contained by this binding item.
	 */
	public List<PromptBindingNode> getEntries() {
		return entries;
	}

	/**
	 * Adds the given entry to this binding item. The entry is added at the end
	 * of the current list of entries.
	 * 
	 * @param entry
	 *            The entry to add
	 */
	public void addEntry(PromptBindingNode entry) {
		entries.add(entry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.configuration.generic.BindingItem#
	 * readConfiguration(org.w3c.dom.Element)
	 */
	public void readConfiguration(Element configuration) {
		NodeList contentList = configuration.getChildNodes();
		for (int i = 0; i < contentList.getLength(); i++) {
			if (!(contentList.item(i) instanceof Element))
				continue;
			Element element = (Element) contentList.item(i);
			PromptBindingNode child = null;
			if ("binding-branch".equals(element.getTagName()))
				child = new PromptBindingSwitch();
			else
				child = new PromptBindingEntry();
			child.readConfiguration(element);
			entries.add(child);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.configuration.generic.BindingItem#
	 * writeConfiguration(org.w3c.dom.Element)
	 */
	public void writeConfiguration(Element configuration) {
		for (PromptBindingNode entry : entries) {
			entry.writeConfiguration(configuration);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		PromptBindingItem copy = new PromptBindingItem();
		for (PromptBindingNode entry : entries) {
			copy.addEntry((PromptBindingNode) entry.clone());
		}
		return copy;
	}
}
