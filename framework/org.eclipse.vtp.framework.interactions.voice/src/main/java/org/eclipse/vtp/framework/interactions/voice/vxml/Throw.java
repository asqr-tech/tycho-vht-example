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
 * The <code>Throw</code> class represents the &lt;throw&gt; VXML element.
 * When processed, this action causes the named event to be raised.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Throw extends Action
{
	/** The name of the event to throw. */
	private String eventName;

	/**
	 * Creates a new instance of <code>Throw</code> with the specified event
	 * name.
	 * 
	 * @param eventName The name of the event to raise.
	 * @throws IllegalArgumentException If the supplied event name is empty.
	 * @throws NullPointerException If the supplied event name is
	 *           <code>null</code>.
	 */
	public Throw(String eventName)
	{
		setEventName(eventName);
	}

	/**
	 * @return the name of the event this throw raises.
	 */
	public String getEventName()
	{
		return eventName;
	}

	/**
	 * Sets the name of the event this throw raises.
	 * 
	 * @param eventName The name of the event this throw raises.
	 * @throws IllegalArgumentException If the supplied event name is empty.
	 * @throws NullPointerException If the supplied event name is
	 *           <code>null</code>.
	 */
	public void setEventName(String eventName) throws IllegalArgumentException,
			NullPointerException
	{
		if (eventName == null)
			throw new NullPointerException("eventName"); //$NON-NLS-1$
		if (eventName.length() == 0)
			throw new IllegalArgumentException("eventName"); //$NON-NLS-1$
		this.eventName = eventName;
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_THROW, NAME_THROW,
				attributes);
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_THROW, NAME_THROW);
	}

	/**
	 * Write the attribute members of this action to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		writeAttribute(attributes, null, null, NAME_EVENTEXPR, TYPE_CDATA,
				eventName);
	}
}
