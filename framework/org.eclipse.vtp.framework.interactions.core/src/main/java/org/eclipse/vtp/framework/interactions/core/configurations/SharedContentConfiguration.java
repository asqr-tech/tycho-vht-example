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
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.IContentFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A configuration for a shared content item.
 * 
 * @author Lonnie Pryor
 */
public class SharedContentConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** The content factory to use. */
	private final IContentFactory contentFactory;
	/** The name of this item. */
	private String name = ""; //$NON-NLS-1$
	/** The content in this item. */
	private Content content = null;

	/**
	 * Creates a new SharedContentConfiguration.
	 * 
	 * @param contentFactory The content factory to use.
	 */
	public SharedContentConfiguration(IContentFactory contentFactory)
	{
		this.contentFactory = contentFactory;
	}

	/**
	 * Returns the name of this item.
	 * 
	 * @return The name of this item.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this item.
	 * 
	 * @param name The name of this item.
	 */
	public void setName(String name)
	{
		this.name = name == null ? "" : name; //$NON-NLS-1$
	}

	/**
	 * Returns the content in this item.
	 * 
	 * @return The content in this item.
	 */
	public Content getContent()
	{
		return content;
	}

	/**
	 * Sets the content in this item.
	 * 
	 * @param contentType The content in this item.
	 */
	public void setContent(Content content)
	{
		this.content = content;
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
		content = null;
		NodeList list = configurationElement.getChildNodes();
		for (int i = 0; content == null && i < list.getLength(); ++i)
			if (list.item(i) instanceof Element)
				content = contentFactory.loadContent((Element)list.item(i));
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
		if (content != null)
			content.store(configurationElement);
	}
}
