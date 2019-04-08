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

/**
 * The <code>TextOutput</code> class wraps raw text that is meant to be
 * rendered to the caller with TTS. A text output can be used anywhere output to
 * the caller can be specified.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class TextOutput extends BasicOutput
{
	/** A single space placed after a text item. */
	private static final char[] SINGLE_SPACE = { ' ' };

	/** The text to render with TTS. */
	private String text = EMPTY;

	/**
	 * Creates a new instance of <code>TextOutput</code> with no text to render.
	 */
	public TextOutput()
	{
	}

	/**
	 * Creates a new instance of <code>TextOutput</code> with the specified text
	 * to render with TTS.
	 * 
	 * @param text The text to render with TTS.
	 * @throws NullPointerException If the supplied text is <code>null</code>.
	 */
	public TextOutput(String text) throws NullPointerException
	{
		setText(text);
	}

	/**
	 * Returns the current text to render with TTS.
	 * 
	 * @return the current text to render with TTS.
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Sets the text of this TextOutput to render with TTS.
	 * 
	 * @param text The new text to render with TTS.
	 * @throws NullPointerException If the supplied text is <code>null</code>.
	 */
	public void setText(String text) throws NullPointerException
	{
		if (text == null)
			throw new NullPointerException("text"); //$NON-NLS-1$
		this.text = text;
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
		writeText(outputHandler);
	}

	/**
	 * Write the text of this output to the supplied content handler.
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
		outputHandler.characters(SINGLE_SPACE, 0, SINGLE_SPACE.length);
	}
}
