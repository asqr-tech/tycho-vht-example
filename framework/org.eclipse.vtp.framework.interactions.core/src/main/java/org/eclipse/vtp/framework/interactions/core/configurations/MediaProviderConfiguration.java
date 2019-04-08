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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.eclipse.vtp.framework.interactions.core.media.IContentFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A configuration for a media provider.
 * 
 * @author Lonnie Pryor
 */
public class MediaProviderConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** The content factory to use. */
	private final IContentFactory contentFactory;
	/** The ID of this media provider. */
	private String id = ""; //$NON-NLS-1$
	/** The formatter ID for this media provider. */
	private String formatterID = ""; //$NON-NLS-1$
	/** The ID of the resource manager for this media provider. */
	private String resourceManagerID = ""; //$NON-NLS-1$
	/** The shared content in this media provider. */
	private final Set sharedContent = new HashSet();

	/**
	 * Creates a new MediaProviderConfiguration.
	 * 
	 * @param contentFactory The content factory to use.
	 */
	public MediaProviderConfiguration(IContentFactory contentFactory)
	{
		this.contentFactory = contentFactory;
	}

	/**
	 * Returns the ID of this media provider.
	 * 
	 * @return The ID of this media provider.
	 */
	public String getID()
	{
		return id;
	}

	/**
	 * Sets the ID of this media provider.
	 * 
	 * @param id The ID of this media provider.
	 */
	public void setID(String id)
	{
		this.id = id == null ? "" : id; //$NON-NLS-1$
	}

	/**
	 * Returns the formatter ID name for this media provider.
	 * 
	 * @return The formatter ID name for this media provider.
	 */
	public String getFormatterID()
	{
		return formatterID;
	}

	/**
	 * Sets the formatter ID name for this media provider.
	 * 
	 * @param formatterType The formatter ID name for this media provider.
	 */
	public void setFormatterID(String formatterID)
	{
		this.formatterID = formatterID == null ? "" : formatterID; //$NON-NLS-1$
	}

	/**
	 * Returns the ID of the resource manager for this media provider.
	 * 
	 * @return The ID of the resource manager for this media provider.
	 */
	public String getResourceManagerID()
	{
		return resourceManagerID;
	}

	/**
	 * Sets the ID of the resource manager for this media provider.
	 * 
	 * @param id The ID of the resource manager for this media provider.
	 */
	public void setResourceManagerID(String resourceManagerID)
	{
		this.resourceManagerID = resourceManagerID == null ? "" : resourceManagerID; //$NON-NLS-1$
	}

	/**
	 * Returns the shared content in this media provider.
	 * 
	 * @return The shared content in this media provider.
	 */
	public SharedContentConfiguration[] getSharedContent()
	{
		return (SharedContentConfiguration[])sharedContent
				.toArray(new SharedContentConfiguration[sharedContent.size()]);
	}

	/**
	 * Adds a shared content item to this media provider.
	 * 
	 * @param sharedContent The item to add.
	 */
	public void addSharedContent(SharedContentConfiguration sharedContent)
	{
		if (sharedContent == null)
			return;
		this.sharedContent.add(sharedContent);
	}

	/**
	 * Removes a shared content item from this media provider.
	 * 
	 * @param sharedContent The item to remove.
	 */
	public void removeSharedContent(SharedContentConfiguration sharedContent)
	{
		this.sharedContent.remove(sharedContent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(
	 *      org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		id = configurationElement.getAttribute(NAME_ID);
		formatterID = configurationElement.getAttribute(NAME_FORMATTER);
		resourceManagerID = configurationElement
				.getAttribute(NAME_RESOURCE_MANAGER);
		sharedContent.clear();
		NodeList list = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_SHARED_CONTENT);
		for (int i = 0; i < list.getLength(); ++i)
		{
			SharedContentConfiguration item = new SharedContentConfiguration(
					contentFactory);
			item.load((Element)list.item(i));
			sharedContent.add(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(
	 *      org.w3c.dom.Element)NAME_SHARED_CONTENT
	 */
	public void save(Element configurationElement)
	{
		String prefix = configurationElement.getPrefix();
		configurationElement.setAttribute(NAME_ID, id);
		configurationElement.setAttribute(NAME_FORMATTER, formatterID);
		configurationElement.setAttribute(NAME_RESOURCE_MANAGER, resourceManagerID);
		if (!sharedContent.isEmpty())
		{
			String sharedContentName = NAME_SHARED_CONTENT;
			if (prefix != null && prefix.length() > 0)
				sharedContentName = prefix + ":" + sharedContentName; //$NON-NLS-1$
			for (Iterator i = sharedContent.iterator(); i.hasNext();)
			{
				Element sharedContentElement = configurationElement.getOwnerDocument()
						.createElementNS(NAMESPACE_URI, sharedContentName);
				((SharedContentConfiguration)i.next()).save(sharedContentElement);
				configurationElement.appendChild(sharedContentElement);
			}
		}
	}
}
