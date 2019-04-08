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
 * The <code>Script</code> class represents the &lt;script&gt; VXML element. A
 * script block contains ECMAScript that is executed when the block is
 * encountered by the VXML interpreter.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Script extends Action implements VXMLConstants
{
	/** The script text of this script block */
	private final StringBuffer text = new StringBuffer();

	/**
	 * Creates a new Script object.
	 */
	public Script()
	{
	}

	/**
	 * Returns the current ECMAScript text of this script object.
	 * 
	 * @return current script text.
	 */
	public String getText()
	{
		return text.toString();
	}

	/**
	 * Replaces the current script text with that given.
	 * 
	 * @param text The new script text.
	 * @throws NullPointerException If the supplied text is <code>null</code>.
	 */
	public void setText(String text) throws NullPointerException
	{
		if (text == null)
			throw new NullPointerException("text"); //$NON-NLS-1$
		this.text.setLength(0);
		this.text.append(text);
	}

	/**
	 * Appends the given script text to the current script text of this script
	 * object.
	 * 
	 * @param text Script text to append.
	 * @throws NullPointerException If the supplied text is <code>null</code>.
	 */
	public void appendText(String text) throws NullPointerException
	{
		if (text == null)
			throw new NullPointerException("text"); //$NON-NLS-1$
		this.text.append(text);
	}

	/**
	 * Clears the text of the current script.
	 */
	public void clearText()
	{
		this.text.setLength(0);
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_SCRIPT, NAME_SCRIPT,
				attributes);
		// Write the script.
		writeText(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_SCRIPT, NAME_SCRIPT);
	}

	/**
	 * Write the attribute members of this script to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
	}

	/**
	 * Write the text of this script to the supplied content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of the script text fails.
	 */
	protected void writeText(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (outputHandler == null)
			throw new NullPointerException("outputHandler"); //$NON-NLS-1$
		outputHandler.characters(text.toString().toCharArray(), 0, text.length());
	}
}
