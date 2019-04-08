/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods),
 *    T.D. Barnes (OpenMethods) - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.voice.vxml;

import org.eclipse.vtp.framework.interactions.core.support.Widget;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Parameter</code> class represents the &lt;param&gt; VXML element.
 * Certain VXML elements require data to be passed in Parameter objects. The
 * <code>Subdialog</code> element requires any variables in the current dialog
 * you want to perform operations on must be passed as Parameter elements.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Parameter extends Widget implements VXMLConstants
{
	/** The name of this parameter. */
	protected String name;
	/** The value of this parameter. */
	protected String value;

	/**
	 * Creates a new Parameter.
	 * 
	 * @param name The name of this parameter.
	 * @param value The value of this parameter.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws IllegalArgumentException If the supplied value is empty.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 * @throws NullPointerException If the supplied value is <code>null</code>.
	 */
	public Parameter(String name, String value) throws IllegalArgumentException,
			NullPointerException
	{
		setName(name);
		setValue(value);
	}

	/**
	 * Returns the name of this parameter.
	 * 
	 * @return The name of this parameter.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the value of this parameter.
	 * 
	 * @return The value of this parameter.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the name of this parameter.
	 * 
	 * @param name The name of this parameter.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 */
	public void setName(String name) throws IllegalArgumentException,
			NullPointerException
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		this.name = name;
	}

	/**
	 * Sets the value of this parameter.
	 * 
	 * @param value The value of this parameter.
	 * @throws IllegalArgumentException If the supplied value is empty.
	 * @throws NullPointerException If the supplied value is <code>null</code>.
	 */
	public void setValue(String value) throws IllegalArgumentException,
			NullPointerException
	{
		if (value == null)
			throw new NullPointerException("value"); //$NON-NLS-1$
		if (value.length() == 0)
			throw new IllegalArgumentException("value"); //$NON-NLS-1$
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.voice.output.VXMLWidget#writeWidget(
	 *      org.xml.sax.ContentHandler)
	 */
	public void writeWidget(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (outputHandler == null)
			throw new NullPointerException("outputHandler"); //$NON-NLS-1$
		// Start and end the element.
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_PARAM, NAME_PARAM,
				attributes);
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_PARAM, NAME_PARAM);
	}

	/**
	 * Write the attribute members of this parameter to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		writeAttribute(attributes, null, null, NAME_NAME, TYPE_CDATA, name);
		writeAttribute(attributes, null, null, NAME_EXPR, TYPE_CDATA, value);
	}
}
