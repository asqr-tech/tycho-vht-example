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
 * The <code>NoMatch</code> class represents the &lt;nomatch&gt; VXML element.
 * It is a subclass of <code>EventHandler</code> that catches errors generated
 * when the caller entered data fails to satisfy the grammar rules.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class NoMatch extends EventHandler
{
	/**
	 * Creates a new instance of <code>NoMatch</code> which does not employ a
	 * conditional expression or require a specific number of occurrences to
	 * receive an event.
	 */
	public NoMatch()
	{
	}

	/**
	 * Creates a new instance of <code>NoMatch</code> that requires a specific
	 * number of occurrences of an event to receive it. It does not employ a
	 * conditional expression that must be evaluated before receiving an event.
	 * 
	 * @param count The number of occurrences of an event for this event handler
	 *          to receive it.
	 */
	public NoMatch(int count)
	{
		super(count);
	}

	/**
	 * Creates a new instance of <code>NoMatch</code> with a conditional
	 * expression that must evaluate to true for the event handler to receive an
	 * event. It does not require a specific number of occurrences to receive an
	 * event.
	 * 
	 * @param condition Expression that must evaluate to true for the event
	 *          handler to receive the event
	 * @throws IllegalArgumentException If the supplied condition is empty.
	 */
	public NoMatch(String condition) throws IllegalArgumentException
	{
		super(condition);
	}

	/**
	 * Creates a new instance of <code>NoMatch</code> with a conditional
	 * expression that must evaluate to true for the event handler to receive an
	 * event. It also requires a specific number of occurrences of the event to
	 * receive it.
	 * 
	 * @param count The number of occurrences of an event for this event handler
	 *          to receive it.
	 * @param condition Expression that must evaluate to true for the event
	 *          handler to receive the event.
	 * @throws IllegalArgumentException If the supplied condition is empty.
	 */
	public NoMatch(int count, String condition) throws IllegalArgumentException
	{
		super(count, condition);
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_NOMATCH, NAME_NOMATCH,
				attributes);
		// Write the children.
		writeVariables(outputHandler);
		writeActionsAndIfClauses(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_NOMATCH, NAME_NOMATCH);
	}
}
