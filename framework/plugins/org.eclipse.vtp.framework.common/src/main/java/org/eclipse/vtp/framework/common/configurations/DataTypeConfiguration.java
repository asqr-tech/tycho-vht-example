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
 * A configuration for an entry in the data type registry.
 * 
 * @author Lonnie Pryor
 */
public class DataTypeConfiguration implements IConfiguration,
		CommonConstants
{
	/** The name of this type. */
	private String name = ""; //$NON-NLS-1$
	/** The name of the primary field of this type. */
	private String primaryField = null;
	/** The fields declared in this type. */
	private final Set<FieldConfiguration> fields = new HashSet<FieldConfiguration>();

	/**
	 * Creates a new DataTypeConfiguration.
	 */
	public DataTypeConfiguration()
	{
	}

	/**
	 * Returns the name of this type.
	 * 
	 * @return The name of this type.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this type.
	 * 
	 * @param name The name of this type.
	 */
	public void setName(String name)
	{
		this.name = name == null ? "" : name; //$NON-NLS-1$
	}

	/**
	 * Returns the name of the primary field of this type.
	 * 
	 * @return The name of the primary field of this type.
	 */
	public String getPrimaryField()
	{
		return primaryField;
	}

	/**
	 * Sets the name of the primary field of this type.
	 * 
	 * @param primaryField The name of the primary field of this type.
	 */
	public void setPrimaryField(String primaryField)
	{
		this.primaryField = primaryField;
	}

	/**
	 * Returns the fields declared in this type.
	 * 
	 * @return The fields declared in this type.
	 */
	public FieldConfiguration[] getFields()
	{
		return fields.toArray(new FieldConfiguration[fields.size()]);
	}

	/**
	 * Adds a field to this type.
	 * 
	 * @param field The field to add.
	 */
	public void addField(FieldConfiguration field)
	{
		if (field == null)
			return;
		fields.add(field);
	}

	/**
	 * Removes a field from this type.
	 * 
	 * @param field The field to remove.
	 */
	public void removeField(FieldConfiguration field)
	{
		fields.remove(field);
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
		if (configurationElement.hasAttribute(NAME_PRIMARY_FIELD))
			primaryField = configurationElement.getAttribute(NAME_PRIMARY_FIELD);
		else
			primaryField = null;
		fields.clear();
		NodeList list = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_FIELD);
		for (int i = 0; i < list.getLength(); ++i)
		{
			FieldConfiguration field = new FieldConfiguration();
			field.load((Element)list.item(i));
			fields.add(field);
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
		configurationElement.setAttribute(NAME_NAME, name);
		if (primaryField != null)
			configurationElement.setAttribute(NAME_PRIMARY_FIELD, primaryField);
		if (!fields.isEmpty())
		{
			String fieldName = NAME_FIELD;
			String prefix = configurationElement.getPrefix();
			if (prefix != null && prefix.length() > 0)
				fieldName = prefix + ":" + fieldName; //$NON-NLS-1$
			for (FieldConfiguration field : fields)
			{
				Element fieldElement = configurationElement.getOwnerDocument()
						.createElementNS(NAMESPACE_URI, fieldName);
				field.save(fieldElement);
				configurationElement.appendChild(fieldElement);
			}
		}
	}
}
