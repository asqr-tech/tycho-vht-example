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

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Assignment</code> class represents the &lt;assign&gt; VXML
 * element. It places the value of <code>value</code> into the variable named
 * by <code>name</code>.<br>
 * <br>
 * The <code>name</code> and <code>value</code> fields are required and
 * cannot be be <code>null</code> or empty strings.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Assignment extends Action
{
	/** The name of the variable to be assigned a new value. */
	private String name;
	/** The value to assign the named variable. */
	private String value;

	/**
	 * Creates a new instance of <code>Assignment</code>. The
	 * <code>value</code> parameter may contain either a string literal or an
	 * expression that will evaluate to a string literal.
	 * 
	 * @param name The name of the variable to be assigned a new value.
	 * @param value The value to assign the named variable.
	 */
	public Assignment(String name, String value)
	{
		setName(name);
		setValue(value);
	}

	/**
	 * Returns the name of the variable to be assigned the new value.
	 * 
	 * @return The name of the variable being assigned the value.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the value to be assigned to new named variable.
	 * 
	 * @return The value being assigned the variable.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the name of the variable to be assigned the new value.
	 * 
	 * @param name The name of the variable to be assigned the value.
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
	 * Sets the value to be assigned to the named variable.
	 * 
	 * @param value The value to be assigned to the variable.
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
	 * @see org.eclipse.vtp.framework.spi.core.output.Widget#writeWidget(
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_ASSIGN, NAME_ASSIGN,
				attributes);
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_ASSIGN, NAME_ASSIGN);
	}

	/**
	 * Write the attribute members of this assignment to the supplied set.
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
