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

import java.util.LinkedList;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A set of possible items for a {@link Rule}.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class OneOf extends Iota
{
	/** The list of items in this set. */
	private final LinkedList<Item> items = new LinkedList<Item>();

	/**
	 * Creates a new OneOf.
	 */
	public OneOf()
	{
	}

	/**
	 * Returns the list of items in this set.
	 * 
	 * @return The list of items in this set.
	 */
	public Item[] getItems()
	{
		return items.toArray(new Item[items.size()]);
	}

	/**
	 * Adds an option to this set.
	 * 
	 * @param item The item to add.
	 * @throws NullPointerException If the specified item is <code>null</code>.
	 */
	public void addItem(Item item) throws NullPointerException
	{
		if (item == null)
			throw new NullPointerException("item"); //$NON-NLS-1$
		items.add(item);
	}

	/**
	 * Remove an option from this set.
	 * 
	 * @param item The item to remove.
	 * @throws NullPointerException If the specified item is <code>null</code>.
	 */
	public void removeItem(Item item) throws NullPointerException
	{
		if (item == null)
			throw new NullPointerException("item"); //$NON-NLS-1$
		items.remove(item);
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
		// Start the element.
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_ONE_OF, NAME_ONE_OF,
				attributes);
		// Write any children.
		writeItems(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_ONE_OF, NAME_ONE_OF);
	}

	/**
	 * Write the attribute members of this set to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
	}

	/**
	 * Write the content of this set to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of any of the items fails.
	 */
	protected void writeItems(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, items);
	}
}
