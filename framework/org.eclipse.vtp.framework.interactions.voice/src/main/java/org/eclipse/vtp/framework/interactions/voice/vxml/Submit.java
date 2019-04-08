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

import java.util.Iterator;
import java.util.LinkedList;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Submit</code> class represents the &lt;submit&gt; VXML element.
 * This element acts exactly like the goto element, except that data can be
 * passed to the next document as well.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Submit extends Goto
{
	/** The HTTP method to send data with. */
	private String method = METHOD_GET;
	/** The encoding type for the parameters. */
	private String encodingType = null;
	/** List of variable names to pass to the next document */
	private final LinkedList<String> names = new LinkedList<String>();

	/**
	 * Creates a new Submit object that will transfer execution to the given URI.
	 * 
	 * @param targetURI URI of the next document to process.
	 * @throws IllegalArgumentException If the supplied URI is empty.
	 * @throws NullPointerException If the supplied URI is <code>null</code>.
	 */
	public Submit(String targetURI)
	{
		super(targetURI);
	}

	/**
	 * Creates a new Submit object that will transfer execution and pass the
	 * specified variable values to the given URI.
	 * 
	 * @param targetURI URI of the next document to process.
	 * @param names List of variables to pass to the next document.
	 * @throws IllegalArgumentException If the supplied URI is empty.
	 * @throws IllegalArgumentException If any of the supplied names are empty.
	 * @throws NullPointerException If the supplied URI is <code>null</code>.
	 * @throws NullPointerException If any of the supplied names are
	 *           <code>null</code>.
	 */
	public Submit(String targetURI, String[] names)
	{
		super(targetURI);
		if (names != null)
			for (int i = 0; i < names.length; ++i)
				addName(names[i]);
	}

	/**
	 * Returns the HTTP method to send data with.
	 * 
	 * @return The HTTP method to send data with.
	 */
	public String getMethod()
	{
		return method;
	}

	/**
	 * Returns the encoding type for the parameters.
	 * 
	 * @return The encoding type for the parameters.
	 */
	public String getEncodingType()
	{
		return encodingType;
	}

	/**
	 * Returns the list of variable names to pass to the next document.
	 * 
	 * @return the list of variable names to pass to the next document.
	 */
	public String[] getNames()
	{
		return names.toArray(new String[names.size()]);
	}

	/**
	 * Sets the HTTP method to send data with.
	 * 
	 * @param method The HTTP method to send data with.
	 * @throws IllegalArgumentException If the supplied method is empty.
	 * @throws NullPointerException If the supplied method is <code>null</code>.
	 */
	public void setMethod(String method) throws IllegalArgumentException,
			NullPointerException
	{
		if (method == null)
			throw new NullPointerException("method"); //$NON-NLS-1$
		if (method.length() == 0)
			throw new IllegalArgumentException("method"); //$NON-NLS-1$
		this.method = method;
	}

	/**
	 * Sets the encoding type for the parameters.
	 * 
	 * @param encodingType The encoding type for the parameters
	 * @throws IllegalArgumentException If the supplied encoding type is empty.
	 */
	public void setEncodingType(String encodingType)
			throws IllegalArgumentException
	{
		if (encodingType != null && encodingType.length() == 0)
			throw new IllegalArgumentException("encodingType"); //$NON-NLS-1$
		this.encodingType = encodingType;
	}

	/**
	 * Adds the variable name to the list of variables to pass to the next
	 * document.
	 * 
	 * @param name The variable name to add.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 */
	public void addName(String name)
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		names.add(name);
	}

	/**
	 * Removes the variable name from the list of variables to pass to the next
	 * document.
	 * 
	 * @param name The variable name to remove.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 */
	public void removeName(String name)
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		names.remove(name);
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_SUBMIT, NAME_SUBMIT,
				attributes);
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_SUBMIT, NAME_SUBMIT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.voice.output.EventHandler#
	 *      writeAttributes(org.xml.sax.helpers.AttributesImpl)
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		super.writeAttributes(attributes);
		writeAttribute(attributes, null, null, NAME_METHOD, TYPE_CDATA, method);
		if (encodingType != null)
			writeAttribute(attributes, null, null, NAME_ENCTYPE, TYPE_CDATA,
					encodingType);
		if (names.size() > 0)
		{
			StringBuffer buffer = new StringBuffer();
			for (Iterator<String> i = names.iterator(); i.hasNext();)
			{
				buffer.append(i.next());
				if (i.hasNext())
					buffer.append(' ');
			}
			writeAttribute(attributes, null, null, NAME_NAMELIST, TYPE_CDATA, buffer
					.toString());
		}
	}
}
