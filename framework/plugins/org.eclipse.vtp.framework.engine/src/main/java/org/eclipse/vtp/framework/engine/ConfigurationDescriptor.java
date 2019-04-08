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
package org.eclipse.vtp.framework.engine;

import org.eclipse.vtp.framework.core.IConfiguration;

/**
 * An object that describes an action configuration format.
 * 
 * @author Lonnie Pryor
 */
public final class ConfigurationDescriptor
{
	/** The ID of this configuration format. */
	private final String id;
	/** The name of this configuration format. */
	private final String name;
	/** The XML name space URI of the configuration format. */
	private final String xmlNamespace;
	/** The XML tag name of the configuration format. */
	private final String xmlTag;
	/** The type of this configuration format. */
	private final Class type;

	/**
	 * Creates a new ConfigurationDescriptor.
	 * 
	 * @param id The ID of this configuration format.
	 * @param name The name of this configuration format.
	 * @param xmlNamespace The XML name space URI of the configuration format.
	 * @param xmlTag The XML tag name of the configuration format.
	 * @param type The type of this configuration format.
	 * @throws IllegalArgumentException If the supplied ID is empty.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws IllegalArgumentException If the supplied XML name space URI is
	 *           empty.
	 * @throws IllegalArgumentException If the supplied XML tag name is empty.
	 * @throws IllegalArgumentException If the supplied type is not a public,
	 *           concrete class with a public, no-argument constructor or is not
	 *           assignable to {@link IConfiguration}.
	 * @throws NullPointerException If the supplied ID is <code>null</code>.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 * @throws NullPointerException If the supplied XML name space URI is
	 *           <code>null</code>.
	 * @throws NullPointerException If the supplied XML tag name is
	 *           <code>null</code>.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 */
	public ConfigurationDescriptor(String id, String name, String xmlNamespace,
			String xmlTag, Class type) throws IllegalArgumentException,
			NullPointerException
	{
		if (id == null)
			throw new NullPointerException("id"); //$NON-NLS-1$
		if (id.length() == 0)
			throw new IllegalArgumentException("id"); //$NON-NLS-1$
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		if (xmlNamespace == null)
			throw new NullPointerException("xmlNamespace"); //$NON-NLS-1$
		if (xmlNamespace.length() == 0)
			throw new IllegalArgumentException("xmlNamespace"); //$NON-NLS-1$
		if (xmlTag == null)
			throw new NullPointerException("xmlTag"); //$NON-NLS-1$
		if (xmlTag.length() == 0)
			throw new IllegalArgumentException("xmlTag"); //$NON-NLS-1$
		if (type == null)
			throw new NullPointerException("type"); //$NON-NLS-1$
		if (!DescriptorUtils.isValidImplementation(type, IConfiguration.class))
			throw new IllegalArgumentException("type: " + type); //$NON-NLS-1$
		this.id = id;
		this.name = name;
		this.xmlNamespace = xmlNamespace;
		this.xmlTag = xmlTag;
		this.type = type;
	}

	/**
	 * Returns the ID of this configuration format.
	 * 
	 * @return The ID of this configuration format.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Returns the name of this configuration format.
	 * 
	 * @return The name of this configuration format.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the XML name space URI of the configuration format.
	 * 
	 * @return The XML name space URI of the configuration format.
	 */
	public String getXmlNamespace()
	{
		return xmlNamespace;
	}

	/**
	 * Returns the XML tag name of the configuration format.
	 * 
	 * @return The XML tag name of the configuration format.
	 */
	public String getXmlTag()
	{
		return xmlTag;
	}

	/**
	 * Returns the type of this configuration format.
	 * 
	 * @return The type of this configuration format.
	 */
	public Class getType()
	{
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return new StringBuffer(getClass().getName().substring(
				getClass().getName().lastIndexOf('.') + 1)).append('[').append(id)
				.append(';').append(name).append(';').append(xmlNamespace).append(';')
				.append(xmlTag).append(';').append(type).append(']').toString();
	}
}
