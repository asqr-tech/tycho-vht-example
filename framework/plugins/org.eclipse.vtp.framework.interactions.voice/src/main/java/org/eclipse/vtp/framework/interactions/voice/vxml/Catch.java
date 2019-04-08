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
 * Catch.java Created on Jan 3, 2004
 */
package org.eclipse.vtp.framework.interactions.voice.vxml;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Catch</code> class can be used to handle any error or event type.
 * It is typically employed to implement user defined events and errors.
 * 
 * Though it can be used to intercept the "nomatch" and "noinput" events, these
 * should be handled by the dedicated element types <code>NoMatch</code> and
 * <code>NoInput</code> respectively.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Catch extends EventHandler
{
	/** The name of the event or error to catch. */
	protected String eventName;

	/**
	 * Creates a new instance of <code>Catch</code> that will be executed when
	 * the event or error named by the <code>eventName</code> argument is
	 * generated during call processing.
	 * 
	 * @param eventName The name of the event or error to catch.
	 * @throws NullPointerException If the supplied event name is
	 *           <code>null</code>.
	 */
	public Catch(String eventName) throws IllegalArgumentException,
			NullPointerException
	{
		setEventName(eventName);
	}

	/**
	 * Creates a new instance of <code>Catch</code> that will be executed when
	 * the <code>count</code><sup>th</sup> occurrence of the event or error
	 * named by the <code>eventName</code> argument is generated during call
	 * processing.
	 * 
	 * @param eventName The name of the event or error to catch.
	 * @param count Specifies which occurrence of the event to catch.
	 * @throws NullPointerException If the supplied event name is
	 *           <code>null</code>.
	 */
	public Catch(String eventName, int count) throws IllegalArgumentException,
			NullPointerException
	{
		super(count);
		setEventName(eventName);
	}

	/**
	 * Creates a new instance of <code>Catch</code> that will be executed when
	 * the event or error named by the <code>eventName</code> argument is
	 * generated during call processing and the expression in the
	 * <code>condition</code> evaluates to <code>true</code>.
	 * 
	 * @param eventName The name of the event or error to catch.
	 * @param condition An expression that evaluates to a boolean that determines
	 *          whether or not this catch element will execute.
	 * @throws IllegalArgumentException If the supplied condition is empty.
	 * @throws NullPointerException If the supplied event name is
	 *           <code>null</code>.
	 */
	public Catch(String eventName, String condition)
			throws IllegalArgumentException, NullPointerException
	{
		super(condition);
		setEventName(eventName);
	}

	/**
	 * Creates a new instance of <code>Catch</code> that will be executed when
	 * the <code>count</code><sup>th</sup> occurrence of the event or error
	 * named by the <code>eventName</code> argument is generated during call
	 * processing and the expression in the <code>condition</code> evaluates to
	 * <code>true</code>.
	 * 
	 * @param eventName The name of the event or error to catch.
	 * @param condition An expression that evaluates to a boolean that determines
	 *          whether or not this catch element will execute.
	 * @param count Specifies which occurrence of the event to catch.
	 * @throws IllegalArgumentException If the supplied condition is empty.
	 * @throws NullPointerException If the supplied event name is
	 *           <code>null</code>.
	 */
	public Catch(String eventName, String condition, int count)
			throws IllegalArgumentException, NullPointerException
	{
		super(count, condition);
		setEventName(eventName);
	}

	/**
	 * Returns the name of the event or error to catch.
	 * 
	 * @return The name of the event or error to catch.
	 */
	public String getEventName()
	{
		return eventName;
	}

	/**
	 * Sets the name of the event or error to catch.
	 * 
	 * @param eventName The name of the event or error to catch.
	 * @throws NullPointerException If the supplied event name is
	 *           <code>null</code>.
	 */
	public void setEventName(String eventName) throws IllegalArgumentException,
			NullPointerException
	{
		if (eventName == null)
			throw new NullPointerException("eventName"); //$NON-NLS-1$
//		if (eventName.length() == 0)
//			throw new IllegalArgumentException("eventName"); //$NON-NLS-1$
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
		// Start the element.
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_CATCH, NAME_CATCH,
				attributes);
		// Write the children.
		writeVariables(outputHandler);
		writeActionsAndIfClauses(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_CATCH, NAME_CATCH);
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
		if(!eventName.equals(""))
			writeAttribute(attributes, null, null, NAME_EVENT, TYPE_CDATA, eventName);
	}
}
