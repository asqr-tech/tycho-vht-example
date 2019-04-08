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
package org.eclipse.vtp.framework.interactions.voice.vxml;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.vtp.framework.interactions.core.support.Widget;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A support object for widgets that contain properties.
 * 
 * @author Lonnie Pryor
 * @version 1.0
 */
public class PropertiesSupport extends Widget implements VXMLConstants
{
	/** The mapping of property names and values. */
	private final Map<String, String> properties = new LinkedHashMap<String, String>();

	/**
	 * Creates a new PropertiesSupport.
	 */
	public PropertiesSupport()
	{
	}

	/**
	 * Returns the names of the properties.
	 * 
	 * @return The names of the properties.
	 */
	public String[] getPropertyNames()
	{
		return properties.keySet().toArray(new String[properties.size()]);
	}

	/**
	 * Returns the value of the specified property or <code>null</code> if no
	 * such property exists.
	 * 
	 * @param propertyName The name of the property to find the value of.
	 * @return The value of the specified property or <code>null</code> if no
	 *         such property exists.
	 * @throws NullPointerException If the supplied property name is
	 *           <code>null</code>.
	 */
	public String getPropertyValue(String propertyName)
			throws NullPointerException
	{
		if (propertyName == null)
			throw new NullPointerException("propertyName"); //$NON-NLS-1$
		return properties.get(propertyName);
	}

	/**
	 * Sets the value of a property.
	 * 
	 * @param propertyName The name of the property to set.
	 * @param propertyValue The value to set the property to.
	 * @throws NullPointerException If the supplied property name or value is
	 *           <code>null</code>.
	 */
	public void setProperty(String propertyName, String propertyValue)
			throws NullPointerException
	{
		if (propertyName == null)
			throw new NullPointerException("propertyName"); //$NON-NLS-1$
		if (propertyValue == null)
			throw new NullPointerException("propertyValue"); //$NON-NLS-1$
		properties.put(propertyName, propertyValue);
	}

	/**
	 * Clears the value of a property.
	 * 
	 * @param propertyName The name of the property to clear.
	 * @throws NullPointerException If the supplied property name is
	 *           <code>null</code>.
	 */
	public void clearProperty(String propertyName) throws NullPointerException
	{
		if (propertyName == null)
			throw new NullPointerException("propertyName"); //$NON-NLS-1$
		properties.remove(propertyName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.core.output.Widget#writeWidget(
	 *      org.xml.sax.ContentHandler)
	 */
	public void writeWidget(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeProperties(outputHandler);
	}

	/**
	 * Writes the properties to an XML document fragment.
	 * 
	 * @param outputHandler The handler to write the properties to.
	 * @throws SAXException If the writing of the properties fails.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 */
	protected void writeProperties(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (outputHandler == null)
			throw new NullPointerException("outputHandler"); //$NON-NLS-1$
		AttributesImpl attributes = new AttributesImpl();
		writeAttribute(attributes, null, null, NAME_NAME, TYPE_CDATA, null);
		writeAttribute(attributes, null, null, NAME_VALUE, TYPE_CDATA, null);
		for (Map.Entry<String, String> entry : properties.entrySet())
		{
			attributes.setValue(0, entry.getKey());
			attributes.setValue(1, entry.getValue());
			outputHandler.startElement(NAMESPACE_URI_VXML, NAME_PROPERTY, NAME_PROPERTY,
					attributes);
			outputHandler.endElement(NAMESPACE_URI_VXML, NAME_PROPERTY, NAME_PROPERTY);
		}
	}
}
