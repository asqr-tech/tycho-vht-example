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
 * The <code>Goto</code> class represents the &lt;goto&gt; VXML element. This
 * element causes the VXML interpreter to stop execution of the current VXML
 * document and begin executing the document located at the target URI.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Goto extends Action
{
	/** The URI of the next VXML document to process. */
	private String targetURI;

	/**
	 * Creates a new instance of <code>Goto</code> with the specified target
	 * URI.
	 * 
	 * @param targetURI The URI of the next VXML document to process.
	 * @throws IllegalArgumentException If the supplied URI is empty.
	 * @throws NullPointerException If the supplied URI is <code>null</code>.
	 */
	public Goto(String targetURI) throws IllegalArgumentException,
			NullPointerException
	{
		setTargetURI(targetURI);
	}

	/**
	 * Returns the URI of the next VXML document to process when this element is
	 * encountered by the VXML interpreter.
	 * 
	 * @return The URI of the next VXML document to process.
	 */
	public String getTargetURI()
	{
		return targetURI;
	}

	/**
	 * Sets the URI of the next VXML document to process when this element is
	 * encountered by the VXML interpreter.
	 * 
	 * @param targetURI The new URI of the next VXML document to process.
	 * @throws IllegalArgumentException If the supplied URI is empty.
	 * @throws NullPointerException If the supplied URI is <code>null</code>.
	 */
	public void setTargetURI(String targetURI) throws IllegalArgumentException,
			NullPointerException
	{
		if (targetURI == null)
			throw new NullPointerException("targetURI"); //$NON-NLS-1$
		if (targetURI.length() == 0)
			throw new IllegalArgumentException("targetURI"); //$NON-NLS-1$
		this.targetURI = targetURI;
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
		outputHandler
				.startElement(NAMESPACE_URI_VXML, NAME_GOTO, NAME_GOTO, attributes);
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_GOTO, NAME_GOTO);
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
		writeAttribute(attributes, null, null, NAME_NEXT, TYPE_CDATA, targetURI);
	}
}
