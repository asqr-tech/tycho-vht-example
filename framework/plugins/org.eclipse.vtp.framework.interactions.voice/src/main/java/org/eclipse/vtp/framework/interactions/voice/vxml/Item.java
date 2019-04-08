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
/*
 * Item.java Created on Jan 7, 2004
 */
package org.eclipse.vtp.framework.interactions.voice.vxml;

import org.eclipse.vtp.framework.util.XMLWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A single item contained in a {@link Rule}.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Item extends Iota
{
	/** The acceptable value(s) for this item. */
	private Object content;
	/** The minimum number of times this item must be repeated. */
	private int minimumRepeat = 0;
	/** The maximum number of times this item may be repeated. */
	private int maximumRepeat = 1;

	/**
	 * Creates a new Item.
	 * 
	 * @param content The single value that is acceptable for this item.
	 * @throws NullPointerException If the specified value is <code>null</code>.
	 */
	public Item(String content) throws NullPointerException
	{
		setContent(content);
	}

	/**
	 * Creates a new Item.
	 * 
	 * @param content The set of values that are acceptable for this item.
	 * @throws NullPointerException If the specified set of values is
	 *           <code>null</code>.
	 */
	public Item(OneOf content) throws NullPointerException
	{
		setContent(content);
	}

	/**
	 * Returns the acceptable value(s) for this item.
	 * 
	 * @return The acceptable value(s) for this item.
	 */
	public Object getContent()
	{
		return content;
	}

	/**
	 * Returns the minimum number of times this item must be repeated.
	 * 
	 * @return The minimum number of times this item must be repeated.
	 */
	public int getMinimumRepeat()
	{
		return minimumRepeat;
	}

	/**
	 * Returns the maximum number of times this item may be repeated.
	 * 
	 * @return The maximum number of times this item may be repeated.
	 */
	public int getMaximumRepeat()
	{
		return maximumRepeat;
	}

	/**
	 * Sets the single value that is acceptable for this item.
	 * 
	 * @param content The single value that is acceptable for this item.
	 * @throws NullPointerException If the specified value is <code>null</code>.
	 */
	public void setContent(String content) throws NullPointerException
	{
		if (content == null)
			throw new IllegalArgumentException("content"); //$NON-NLS-1$
		this.content = content;
	}

	/**
	 * Sets the set of values that are acceptable for this item.
	 * 
	 * @param content The set of values that are acceptable for this item.
	 * @throws NullPointerException If the specified set of values is
	 *           <code>null</code>.
	 */
	public void setContent(OneOf content) throws NullPointerException
	{
		if (content == null)
			throw new IllegalArgumentException("content"); //$NON-NLS-1$
		this.content = content;
	}

	/**
	 * Sets the minimum number of times this item must be repeated.
	 * 
	 * @param minimumRepeat The minimum number of times this item must be
	 *          repeated.
	 * @throws IllegalArgumentException If the specified value is less than zero.
	 */
	public void setMinimumRepeat(int minimumRepeat)
			throws IllegalArgumentException
	{
		if (minimumRepeat < 0)
			throw new IllegalArgumentException("minimumRepeat"); //$NON-NLS-1$
		this.minimumRepeat = minimumRepeat;
	}

	/**
	 * Sets the maximum number of times this item may be repeated.
	 * 
	 * @param maximumRepeat The maximum number of times this item may be repeated.
	 * @throws IllegalArgumentException If the specified value is less than zero.
	 */
	public void setMaximumRepeat(int maximumRepeat)
			throws IllegalArgumentException
	{
		if (maximumRepeat < 0)
			throw new IllegalArgumentException("maximumRepeat"); //$NON-NLS-1$
		this.maximumRepeat = maximumRepeat;
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
		if(outputHandler instanceof XMLWriter)
		{
//			System.out.println("XMLWRITER");
			((XMLWriter)outputHandler).setCompactElements(true);
		}
		// Start the element.
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		outputHandler
				.startElement(NAMESPACE_URI_VXML, NAME_ITEM, NAME_ITEM, attributes);
		// Write any children.
		writeContent(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_ITEM, NAME_ITEM);
		if(outputHandler instanceof XMLWriter)
		{
//			System.out.println("XMLWRITER");
			((XMLWriter)outputHandler).setCompactElements(false);
		}
	}

	/**
	 * Write the attribute members of this grammar to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		writeAttribute(attributes, null, null, NAME_REPEAT, TYPE_CDATA,
				minimumRepeat + "-" + maximumRepeat); //$NON-NLS-1$
	}

	/**
	 * Write the content of this item to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of the content fails.
	 */
	protected void writeContent(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (content instanceof OneOf)
			((OneOf)content).writeWidget(outputHandler);
		else
		{
			String text = (String)content;
			outputHandler.characters(text.toCharArray(), 0, text.length());
		}
	}
}
