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
package org.eclipse.vtp.framework.common.configurations;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A configuration for an entry in the brand registry.
 * 
 * @author Lonnie Pryor
 */
public class BrandConfiguration implements IConfiguration,
		CommonConstants
{
	private String id = ""; //$NON-NLS-1$
	/** The name of this brand. */
	private String name = ""; //$NON-NLS-1$
	/** The children of this brand. */
	private final Set<BrandConfiguration> children = new HashSet<BrandConfiguration>();

	/**
	 * Creates a new BrandRegistryConfiguration.
	 */
	public BrandConfiguration()
	{
	}
	
	public String getId()
	{
		return id;
	}

	/**
	 * Returns the name of this brand.
	 * 
	 * @return The name of this brand.
	 */
	public String getName()
	{
		return name;
	}
	
	public void setId(String id)
	{
		if(id == null || id.equals(""))
			throw new IllegalArgumentException("Brand Id cannont be null or the empty string.");
		this.id = id;
	}

	/**
	 * Sets the name of this brand.
	 * 
	 * @param name The name to name of this brand.
	 */
	public void setName(String name)
	{
		this.name = name == null ? "" : name; //$NON-NLS-1$;
	}

	/**
	 * Returns the children of this brand.
	 * 
	 * @return The children of this brand.
	 */
	public BrandConfiguration[] getChildren()
	{
		return children.toArray(new BrandConfiguration[children.size()]);
	}

	/**
	 * Adds a child to this brand.
	 * 
	 * @param child The child to add.
	 */
	public void addChild(BrandConfiguration child)
	{
		if (child == null)
			return;
		children.add(child);
	}

	/**
	 * Removes a child from this brand.
	 * 
	 * @param child The child to remove.
	 */
	public void removeChild(BrandConfiguration child)
	{
		children.remove(child);
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
		name = configurationElement.getAttribute(NAME_NAME);
		children.clear();
		NodeList list = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_BRAND);
		for (int i = 0; i < list.getLength(); ++i)
		{
			BrandConfiguration child = new BrandConfiguration();
			child.load((Element)list.item(i));
			children.add(child);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(
	 *      org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute(NAME_ID, id);
		configurationElement.setAttribute(NAME_NAME, name);
		if (!children.isEmpty())
		{
			String brandName = NAME_BRAND;
			String prefix = configurationElement.getPrefix();
			if (prefix != null && prefix.length() > 0)
				brandName = prefix + ":" + brandName; //$NON-NLS-1$
			for (BrandConfiguration child : children)
			{
				Element childElement = configurationElement.getOwnerDocument()
						.createElementNS(NAMESPACE_URI, brandName);
				child.save(childElement);
				configurationElement.appendChild(childElement);
			}
		}
	}
}
