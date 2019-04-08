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

import org.eclipse.vtp.framework.interactions.core.support.Widget;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Grammar</code> class represents the &lt;grammar&gt; VXML element.
 * The grammar element is used to designate the format of some input to be
 * collected from the caller.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Grammar extends Widget implements VXMLConstants
{
	/** The mode of the grammar (dtmf or voice). */
	private String mode;

	/**
	 * Creates a new Grammar.
	 * 
	 * @param mode The mode of the grammar (dtmf or voice).
	 * @throws IllegalArgumentException If the specified mode is not one of "dtmf"
	 *           or "voice".
	 * @throws NullPointerException If the specified mode is <code>null</code>.
	 */
	public Grammar(String mode) throws IllegalArgumentException,
			NullPointerException
	{
		setMode(mode);
	}

	/**
	 * Returns the mode of the grammar (dtmf or voice).
	 * 
	 * @return The mode of the grammar (dtmf or voice).
	 */
	public String getMode()
	{
		return mode;
	}

	/**
	 * Sets the mode of the grammar (dtmf or voice).
	 * 
	 * @param mode The mode of the grammar (dtmf or voice).
	 * @throws IllegalArgumentException If the specified mode is not one of "dtmf"
	 *           or "voice".
	 * @throws NullPointerException If the specified mode is <code>null</code>.
	 */
	public void setMode(String mode) throws IllegalArgumentException,
			NullPointerException
	{
		if (mode == null)
			throw new NullPointerException("mode"); //$NON-NLS-1$
		else if (mode.equalsIgnoreCase(GRAMMAR_MODE_DTMF))
			this.mode = GRAMMAR_MODE_DTMF;
		else if (mode.equalsIgnoreCase(GRAMMAR_MODE_VOICE))
			this.mode = GRAMMAR_MODE_VOICE;
		else
			throw new IllegalArgumentException("mode==\"" //$NON-NLS-1$
					+ mode + "\""); //$NON-NLS-1$
		this.mode = mode;
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_GRAMMAR, NAME_GRAMMAR,
				attributes);
		// Write any children.
		writeChildren(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_GRAMMAR, NAME_GRAMMAR);
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
		writeAttribute(attributes, null, null, NAME_MODE, TYPE_CDATA, mode);
	}

	/**
	 * Write the children(if any) of this grammar to the specified content
	 * handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the children fails.
	 */
	protected void writeChildren(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
	}
}
