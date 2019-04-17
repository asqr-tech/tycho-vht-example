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

import org.eclipse.vtp.desktop.model.interactive.core.content.ContentLoadingManager;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.w3c.dom.Element;

/**
 * This class represents a single line item within a prompt binding. Each entry
 * can be one of several different types of content from an audio file to a
 * piece of dynamic data to be read from a variable.
 * 
 * @author trip
 */
public class PromptBindingEntry extends PromptBindingNode {
	/** The content of this entry */
	private Content content = null;

	public PromptBindingEntry() {}

	public PromptBindingEntry(Content content) {
		this.content = content;
	}

	/**
	 * @return The content object of this entry
	 */
	public Content getContent() {
		return content;
	}

	/**
	 * Sets the content of this entry to the given object. Any previous content
	 * is forgotten.
	 * 
	 * @param content
	 *            The new content of this entry
	 */
	public void setContent(Content content) {
		this.content = content;
	}

	/* Read the configuration for this node. */
	@Override
	void readConfiguration(Element configuration) {
		content = ContentLoadingManager.getInstance().loadContent(configuration);
	}

	/* Write the configuration for this node. */
	@Override
	void writeConfiguration(Element configuration) {
		content.store(configuration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		PromptBindingEntry copy = new PromptBindingEntry();
		if (content != null)
			copy.content = content.createCopy();
		return copy;
	}
}
