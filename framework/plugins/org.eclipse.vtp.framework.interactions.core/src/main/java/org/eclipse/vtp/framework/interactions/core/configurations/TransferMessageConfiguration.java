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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Configuration for a transfer message interaction.
 * 
 * @author Lonnie Pryor
 */
public class TransferMessageConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** The destination type. */
	private PropertyConfiguration type = null;
	/** The destination configuration. */
	private PropertyConfiguration destination = null;

	/**
	 * Creates a new TransferMessageConfiguration.
	 */
	public TransferMessageConfiguration()
	{
	}

	/**
	 * Returns the destination type for this message or <code>null</code> if no
	 * such configuration is registered.
	 * 
	 * @return The destination type for this message or <code>null</code> if no
	 *         such type is registered.
	 */
	public PropertyConfiguration getType()
	{
		return type;
	}

	/**
	 * Sets the destination type for this message.
	 * 
	 * @param type The type for this message or <code>null</code> to remove the
	 *          type.
	 */
	public void setType(PropertyConfiguration type)
	{
		this.type = type;
	}

	/**
	 * Returns the destination configuration for this message or <code>null</code>
	 * if no such configuration is registered.
	 * 
	 * @return The destination configuration for this message or <code>null</code>
	 *         if no such configuration is registered.
	 */
	public PropertyConfiguration getDestination()
	{
		return destination;
	}

	/**
	 * Sets the destination configuration for this message.
	 * 
	 * @param destination The configuration for this message or <code>null</code>
	 *          to remove the configuration.
	 */
	public void setDestination(PropertyConfiguration destination)
	{
		this.destination = destination;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		destination = null;
		NodeList elements = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, NAME_PROPERTY);
		for (int i = 0; i < elements.getLength(); ++i)
		{
			Element e = (Element)elements.item(i);
			if ("type".equalsIgnoreCase(e.getAttribute(NAME_NAME)))
				(type = new PropertyConfiguration()).load(e);
			else if ("destination".equalsIgnoreCase(e.getAttribute(NAME_NAME)))
				(destination = new PropertyConfiguration()).load(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		String propertyName = NAME_PROPERTY;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
			propertyName = prefix + ":" + propertyName; //$NON-NLS-1$
		if (type != null)
		{
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, propertyName);
			type.save(element);
			element.setAttribute(NAME_NAME, "type"); //$NON-NLS-1$
			configurationElement.appendChild(element);
		}
		if (destination != null)
		{
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, propertyName);
			destination.save(element);
			element.setAttribute(NAME_NAME, "destination"); //$NON-NLS-1$
			configurationElement.appendChild(element);
		}
	}
}
