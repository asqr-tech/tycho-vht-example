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
 * The <code>Variable</code> class represents the &lt;var&gt; VXML element.
 * Variables hold temporary values during the processing of a VXML document.
 * When declared, a variable can optionally be set to an initial value. Some
 * platforms offer predefined variables that do not need declared with this
 * element.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Variable extends Widget implements VXMLConstants
{
	/** The name of this variable. */
	private String name;
	/** The initial value of this variable */
	protected String initialValue = null;

	/**
	 * Creates a new instance of <code>Variable</code> with the specified name
	 * and no initial value. Throws an IllegalArgumentException if the name
	 * argument is <code>null</code> or is an empty string.
	 * 
	 * @param name The name of this variable.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 */
	public Variable(String name) throws IllegalArgumentException,
			NullPointerException
	{
		setName(name);
	}

	/**
	 * Creates a new instance of <code>Variable</code> with the specified name
	 * and initial value. Throws an IllegalArgumentException if the name argument
	 * is <code>null</code> or is an empty string.
	 * 
	 * @param name The name of this variable.
	 * @param initialValue The initial value of this variable.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 */
	public Variable(String name, String initialValue)
			throws IllegalArgumentException, NullPointerException
	{
		setName(name);
		setInitialValue(initialValue);
	}

	/**
	 * Returns the name of this variable.
	 * 
	 * @return The name of this variable.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the initial value of this variable.
	 * 
	 * @return The initial value of this variable.
	 */
	public String getInitialValue()
	{
		return initialValue;
	}

	/**
	 * Sets the name of this variable.
	 * 
	 * @param name The name of this variable.
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
	 * Sets the initial value of the variable.
	 * 
	 * @param initialValue The new initial value of the variable
	 */
	public void setInitialValue(String initialValue)
	{
		this.initialValue = initialValue;
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_VAR, NAME_VAR, attributes);
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_VAR, NAME_VAR);
	}

	/**
	 * Write the attribute members of this variable to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		writeAttribute(attributes, null, null, NAME_NAME, TYPE_CDATA, name);
		if (initialValue != null)
			writeAttribute(attributes, null, null, NAME_EXPR, TYPE_CDATA,
					initialValue);
	}
}
