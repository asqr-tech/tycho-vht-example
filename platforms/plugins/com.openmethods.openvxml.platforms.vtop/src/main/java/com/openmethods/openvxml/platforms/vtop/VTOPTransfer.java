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

package com.openmethods.openvxml.platforms.vtop;

import java.util.LinkedList;

import org.eclipse.vtp.framework.interactions.voice.vxml.EventHandler;
import org.eclipse.vtp.framework.interactions.voice.vxml.Filled;
import org.eclipse.vtp.framework.interactions.voice.vxml.FormElement;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Transfer</code> class represents the &lt;transfer&gt; VXML
 * element.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class VTOPTransfer extends FormElement
{
	/** The destination of the transfer. */
	private String destination;
	/** The maximum duration of the transfer. */
	private String maxTime = null;
	private String aai = null;
	private String aaiExpression = null;
	private String signalVars = null;
	/** True if the transfer will be bridged. */
	private String transferType = "blind";
	/** The filled handlers registered with this recording. */
	private final LinkedList<Filled> filledHandlers = new LinkedList<Filled>();
	/** The event handlers registered with this recording. */
	private final LinkedList<EventHandler> eventHandlers = new LinkedList<EventHandler>();

	/**
	 * Creates a new instance of <code>Transfer</code>.
	 * 
	 * @param name The name this element will be referred to by.
	 * @param destination The destination of the transfer.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified destination is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 * @throws NullPointerException If the specified destination is
	 *           <code>null</code>.
	 */
	public VTOPTransfer(String name, String destination)
			throws IllegalArgumentException, NullPointerException
	{
		super(name);
		setDestination(destination);
	}

	/**
	 * Creates a new instance of <code>Transfer</code>.
	 * 
	 * @param name The name this element will be referred to by.
	 * @param expression See the documentation for <code>FormElement</code>.
	 * @param destination The destination of the transfer.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws IllegalArgumentException If the specified destination is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 * @throws NullPointerException If the specified destination is
	 *           <code>null</code>.
	 */
	public VTOPTransfer(String name, String expression, String destination)
			throws IllegalArgumentException, NullPointerException
	{
		super(name, expression);
		setDestination(destination);
	}

	/**
	 * Creates a new instance of <code>Transfer</code>.
	 * 
	 * @param name The name this element will be referred to by.
	 * @param expression See the documentation for <code>FormElement</code>.
	 * @param condition See the documentation for <code>FormElement</code>.
	 * @param destination The destination of the transfer.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws IllegalArgumentException If the specified condition is empty.
	 * @throws IllegalArgumentException If the specified destination is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 * @throws NullPointerException If the specified destination is
	 *           <code>null</code>.
	 */
	public VTOPTransfer(String name, String expression, String condition,
			String destination) throws IllegalArgumentException, NullPointerException
	{
		super(name, expression, condition);
	}

	/**
	 * Returns the destination of the transfer.
	 * 
	 * @return The destination of the transfer.
	 */
	public String getDestination()
	{
		return destination;
	}

	/**
	 * Returns the maxTime.
	 *
	 * @return The maxTime.
	 */
	public String getMaxTime()
	{
		return maxTime;
	}

	/**
	 * Returns the bridge setting.
	 * 
	 * @return True if the transfer will be bridged.
	 */
	public String getTransferType()
	{
		return transferType;
	}

	public String getSignalVars()
	{
		return signalVars;
	}

	public void setSignalVars(String signalVars)
	{
		this.signalVars = signalVars;
	}

	/**
	 * Returns the list of filled handlers for this transfer.
	 * 
	 * @return The list of filled handlers for this transfer.
	 */
	Filled[] getFilledHandlers()
	{
		return filledHandlers.toArray(new Filled[filledHandlers.size()]);
	}

	/**
	 * Returns the list of event handlers for this recording.
	 * 
	 * @return The list of event handlers for this recording.
	 */
	EventHandler[] getEventHandlers()
	{
		return eventHandlers.toArray(new EventHandler[eventHandlers
				.size()]);
	}

	/**
	 * Sets the destination of the transfer.
	 * 
	 * @param destination The destination of the transfer.
	 * @throws IllegalArgumentException If the specified destination is empty.
	 * @throws NullPointerException If the specified destination is
	 *           <code>null</code>.
	 */
	public void setDestination(String destination)
			throws IllegalArgumentException, NullPointerException
	{
		if (destination == null)
			throw new NullPointerException("destination"); //$NON-NLS-1$
		if (destination.length() == 0)
			throw new IllegalArgumentException("destination"); //$NON-NLS-1$
		this.destination = destination;
	}

	/**
	 * Sets the maxTime.
	 * 
	 * @param maxTime The maxTime to set.
	 */
	public void setMaxTime(String maxTime)
	{
		this.maxTime = maxTime;
	}
	
	public void setAAI(String aai)
	{
		this.aai = aai;
	}
	
	public void setAAIExpression(String expression)
	{
		this.aaiExpression = expression;
	}

	public void setTransferType(String transferType)
	{
		this.transferType = transferType;
	}

	/**
	 * Adds the specified filled handler to this transfer. The filled handlers
	 * will be executed in the order they were added.
	 * 
	 * @param filled The filled handler to be added.
	 * @throws NullPointerException If the supplied filled handler is
	 *           <code>null</code>.
	 */
	public void addFilledHandler(Filled filled) throws NullPointerException
	{
		if (filled == null)
			throw new NullPointerException("filled"); //$NON-NLS-1$
		filledHandlers.add(filled);
	}

	/**
	 * Removes the specified filled handler from this transfer.
	 * 
	 * @param filled The filled handler to be removed.
	 * @throws NullPointerException If the supplied filled handler is
	 *           <code>null</code>.
	 */
	public void removeFilledHandler(Filled filled) throws NullPointerException
	{
		if (filled == null)
			throw new NullPointerException("filled"); //$NON-NLS-1$
		filledHandlers.remove(filled);
	}

	/**
	 * Adds the specified event handler to this recording. The event handlers are
	 * evaluated in the order they were added.
	 * 
	 * @param eventHandler The event handler to add.
	 * @throws NullPointerException If the supplied event handler is
	 *           <code>null</code>.
	 */
	public void addEventHandler(EventHandler eventHandler)
			throws NullPointerException
	{
		if (eventHandler == null)
			throw new NullPointerException("eventHandler"); //$NON-NLS-1$
		eventHandlers.add(eventHandler);
	}

	/**
	 * Removes the specified event handler from this recording.
	 * 
	 * @param eventHandler The event handler to remove.
	 * @throws NullPointerException If the supplied event handler is
	 *           <code>null</code>.
	 */
	public void removeEventHandler(EventHandler eventHandler)
			throws NullPointerException
	{
		if (eventHandler == null)
			throw new NullPointerException("eventHandler"); //$NON-NLS-1$
		eventHandlers.remove(eventHandler);
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_TRANSFER, NAME_TRANSFER,
				attributes);
		writeFilledHandlers(outputHandler);
		writeEventHandlers(outputHandler);
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_TRANSFER, NAME_TRANSFER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.voice.output.FormElement#
	 *      writeAttributes(org.xml.sax.helpers.AttributesImpl)
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		super.writeAttributes(attributes);
		writeAttribute(attributes, null, null, NAME_DEST, TYPE_CDATA, destination);
		writeAttribute(attributes, null, null, NAME_TYPE, TYPE_CDATA, transferType);
		if (maxTime != null)
			writeAttribute(attributes, null, null, NAME_MAXTIME, TYPE_CDATA, maxTime);
		if(aaiExpression != null)
			writeAttribute(attributes, null, null, "aaiexpr", TYPE_CDATA, aaiExpression);
		else if(aai != null)
			writeAttribute(attributes, null, null, "aai", TYPE_CDATA, aai);
		if(signalVars != null)
			writeAttribute(attributes, null, null, "signalvar", TYPE_CDATA, signalVars);
	}

	/**
	 * Write the filled handlers of this field to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the filled handlers fails.
	 */
	protected void writeFilledHandlers(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, filledHandlers);
	}

	/**
	 * Write the event handlers of this field to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the event handlers fails.
	 */
	protected void writeEventHandlers(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, eventHandlers);
	}
}
