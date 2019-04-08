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
package org.eclipse.vtp.framework.interactions.core.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.vtp.framework.util.XMLWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Base class for component-style document constructs.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public abstract class Widget
{
	/** An empty string. */
	private static final String EMPTY = new String();

	/**
	 * Creates a new <code>Widget</code>.
	 */
	protected Widget()
	{
	}

	/**
	 * Writes the content of this widget to an XML content handler.
	 * 
	 * @param outputHandler The handler to write this widget to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of this widget fails.
	 */
	public abstract void writeWidget(ContentHandler outputHandler)
			throws NullPointerException, SAXException;

	/**
	 * Writes the content of this widget to a stream.
	 * 
	 * @param outputStream The stream to write this widget to.
	 * @throws IOException If the writing of this widget fails.
	 * @throws NullPointerException If the supplied stream is <code>null</code>.
	 */
	public void writeWidget(OutputStream outputStream) throws IOException,
			NullPointerException
	{
		if (outputStream == null)
			throw new NullPointerException("writer"); //$NON-NLS-1$
		try
		{
			writeWidget(new XMLWriter(outputStream));
		}
		catch (SAXException e)
		{
			IOException ex = new IOException(e.getMessage());
			ex.initCause(e);
			throw ex;
		}
	}

	/**
	 * Writes an attribute member of this widget to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @param uri The URI of the attribute.
	 * @param localName The local name of the attribute.
	 * @param name The qualified name of the attribute.
	 * @param type The type of the attribute.
	 * @param value The value of the attribute.
	 * 
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttribute(AttributesImpl attributes, String uri,
			String localName, String name, String type, String value)
			throws NullPointerException
	{
		if (attributes == null)
			throw new NullPointerException("attributes"); //$NON-NLS-1$
		uri = uri == null ? EMPTY : uri;
		localName = localName == null ? EMPTY : localName;
		name = name == null ? EMPTY : name;
		type = type == null ? EMPTY : type;
		value = value == null ? EMPTY : value;
		int index = -1;
		if (uri.length() == 0)
		{
			if (name.length() > 0)
				index = attributes.getIndex(name);
			if (index < 0 && localName.length() > 0)
				index = attributes.getIndex(localName);
		}
		else if (localName.length() == 0)
			index = attributes.getIndex(name);
		else
		{
			index = attributes.getIndex(uri, localName);
			if (index < 0 && name.length() > 0)
				index = attributes.getIndex(name);
		}
		if (index >= 0)
			attributes.setAttribute(index, uri, localName, name, type, value);
		else
			attributes.addAttribute(uri, localName, name, type, value);
	}

	/**
	 * Writes the specified array of Widgets to the supplied output handler.
	 * 
	 * @param outputHandler The handler to write the widgets to.
	 * @param children The array of widgets to write.
	 * @throws SAXException If the writing of one of the specified widgets fails.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 */
	protected void writeChildren(ContentHandler outputHandler, Widget[] children)
			throws NullPointerException, SAXException
	{
		if (outputHandler == null)
			throw new NullPointerException("outputHandler"); //$NON-NLS-1$
		if (children != null)
			for (int i = 0; i < children.length; ++i)
				children[i].writeWidget(outputHandler);
	}

	/**
	 * Writes the specified collection of Widgets to the supplied output handler.
	 * 
	 * @param outputHandler The handler to write the widgets to.
	 * @param children The collection of widgets to write.
	 * @throws SAXException If the writing of one of the specified widgets fails.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 */
	protected void writeChildren(ContentHandler outputHandler, Collection children)
			throws NullPointerException, SAXException
	{
		if (outputHandler == null)
			throw new NullPointerException("outputHandler"); //$NON-NLS-1$
		if (children != null)
			for (Iterator i = children.iterator(); i.hasNext();)
				((Widget)i.next()).writeWidget(outputHandler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringWriter writer = new StringWriter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			writeWidget(baos);
		}
		catch (IOException e)
		{
			try {
				baos.write(e.getMessage().getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return baos.toString();
	}
}
