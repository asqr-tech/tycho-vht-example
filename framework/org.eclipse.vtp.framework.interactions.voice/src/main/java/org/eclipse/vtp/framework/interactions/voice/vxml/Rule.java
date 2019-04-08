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
 * Rule.java Created on Jan 7, 2004
 */
package org.eclipse.vtp.framework.interactions.voice.vxml;

import java.util.LinkedList;

import org.eclipse.vtp.framework.interactions.core.support.Widget;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A rule declaration for inline grammars.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Rule extends Widget implements VXMLConstants
{
	/** The ID of this rule. */
	private String id;
	/** The list of items in this rule. */
	private final LinkedList<Iota> iotas = new LinkedList<Iota>();

	/**
	 * Creates a new Rule.
	 * 
	 * @param id The ID of this rule.
	 * @throws IllegalArgumentException If the specified ID is empty.
	 * @throws NullPointerException If the specified ID is <code>null</code>.
	 */
	public Rule(String id) throws IllegalArgumentException, NullPointerException
	{
		setId(id);
	}

	/**
	 * Returns the ID of this rule.
	 * 
	 * @return id The ID of this rule.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Returns the list of items in this rule.
	 * 
	 * @return The list of items in this rule.
	 */
	public Iota[] getItems()
	{
		return iotas.toArray(new Iota[iotas.size()]);
	}

	/**
	 * Sets the ID of this rule.
	 * 
	 * @param id The ID of this rule.
	 * @throws IllegalArgumentException If the specified ID is empty.
	 * @throws NullPointerException If the specified ID is <code>null</code>.
	 */
	public void setId(String id) throws IllegalArgumentException,
			NullPointerException
	{
		if (id == null)
			throw new NullPointerException("id"); //$NON-NLS-1$
		if (id.length() == 0)
			throw new IllegalArgumentException("id"); //$NON-NLS-1$
		this.id = id;
	}

	/**
	 * Adds an item to this rule's list.
	 * 
	 * @param item The item to add.
	 * @throws NullPointerException If the specified item is <code>null</code>.
	 */
	public void addItem(Iota item) throws NullPointerException
	{
		if (item == null)
			throw new NullPointerException("item"); //$NON-NLS-1$
		iotas.add(item);
	}

	/**
	 * Removes an item from this rule's list.
	 * 
	 * @param item The item to remove.
	 * @throws NullPointerException If the specified item is <code>null</code>.
	 */
	public void removeItem(Iota item) throws NullPointerException
	{
		if (item == null)
			throw new NullPointerException("item"); //$NON-NLS-1$
		iotas.remove(item);
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
		outputHandler
				.startElement(NAMESPACE_URI_VXML, NAME_RULE, NAME_RULE, attributes);
		// Write any children.
		writeItems(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_RULE, NAME_RULE);
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
		writeAttribute(attributes, null, null, NAME_ID, TYPE_CDATA, id);
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
		writeChildren(outputHandler, iotas);
	}
}
