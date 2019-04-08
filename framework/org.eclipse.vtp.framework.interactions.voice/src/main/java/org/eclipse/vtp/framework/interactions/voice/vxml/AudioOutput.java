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
 * The <code>AudioOutput</code> class represents the &lt;audio&gt; VXML
 * element and specifies the location of an audio file to be played to the
 * caller.
 * 
 * An <code>AudioOutput</code> can only be contained by the Prompt and Choice
 * elements. All other output must be some combination of BasicOutput or its
 * subclasses.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class AudioOutput extends OutputSet
{
	/** The URI of the audio file to play to the caller. */
	protected String audioFileURI;

	/**
	 * Creates a new instance of <code>AudioOutput</code> with the specified
	 * audio file URI. The URI passed in may be a string literal or an expression
	 * that will evaluate to the desired string literal.
	 * 
	 * @param audioFileURI The URI of the audio file to play to the caller.
	 * @throws IllegalArgumentException If the supplied URI is empty.
	 * @throws NullPointerException If the supplied URI is <code>null</code>.
	 */
	public AudioOutput(String audioFileURI) throws IllegalArgumentException,
			NullPointerException
	{
		setAudioFileURI(audioFileURI);
	}

	/**
	 * Returns the URI of the audio file to be played to the caller.
	 * 
	 * @return The URI of the audio file.
	 */
	public String getAudioFileURI()
	{
		return audioFileURI;
	}

	/**
	 * Sets the URI of the audio file to be played to the caller.
	 * 
	 * @param audioFileURI The new URI of the audio file.
	 * @throws IllegalArgumentException If the supplied URI is empty.
	 * @throws NullPointerException If the supplied URI is <code>null</code>.
	 */
	public void setAudioFileURI(String audioFileURI)
			throws IllegalArgumentException, NullPointerException
	{
		if (audioFileURI == null)
			throw new NullPointerException("audioFileURI"); //$NON-NLS-1$
		if (audioFileURI.length() == 0)
			throw new IllegalArgumentException("audioFileURI"); //$NON-NLS-1$
		this.audioFileURI = audioFileURI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.voice.output.OutputSet#writeWidget(
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_AUDIO, NAME_AUDIO,
				attributes);
		// Write the children.
		writeOutputs(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_AUDIO, NAME_AUDIO);
	}

	/**
	 * Write the attribute members of this condition to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		writeAttribute(attributes, null, null, NAME_SRC, TYPE_CDATA, audioFileURI);
	}
}
