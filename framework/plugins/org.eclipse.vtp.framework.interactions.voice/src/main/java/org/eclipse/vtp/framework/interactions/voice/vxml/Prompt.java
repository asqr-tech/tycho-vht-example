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

import java.util.Locale;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Prompt</code> class represents the &lt;prompt&gt; VXML element.
 * The prompt element is used to designate some output to be rendered to the
 * caller. The output of a prompt can be audio, TTS, or some combination of
 * both.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Prompt extends Action
{
	/** The output to render. */
	private Output output;
	/** True if the caller can barge in. */
	private boolean bargeInEnabled = true;
	/** The language the prompt is in. */
	private Locale language = Locale.getDefault();
	/** The prompt timeout, used for record elements only. */
	private String timeout = null;

	/**
	 * Creates a new instance of Prompt with the given output. By default, this
	 * prompt can be barged-in on.
	 * 
	 * @param output The output to render.
	 * @throws NullPointerException If the supplied output is <code>null</code>.
	 */
	public Prompt(Output output) throws NullPointerException
	{
		setOutput(output);
	}

	/**
	 * Creates a new instance of Prompt with the given output. The bargeIn
	 * argument governs whether or not the prompt can be cut short by caller
	 * input.
	 * 
	 * @param output The output to render.
	 * @param bargeIn True if the caller can barge in.
	 * @throws NullPointerException If the supplied output is <code>null</code>.
	 */
	public Prompt(Output output, boolean bargeIn) throws NullPointerException
	{
		setOutput(output);
		setBargeInEnabled(bargeIn);
	}

	/**
	 * Returns the output to render.
	 * 
	 * @return The output to render.
	 */
	public Output getOutput()
	{
		return output;
	}

	/**
	 * Returns true if the caller can barge in.
	 * 
	 * @return True if the caller can barge in.
	 */
	public boolean isBargeInEnabled()
	{
		return bargeInEnabled;
	}

	/**
	 * Returns the language the prompt is in.
	 * 
	 * @return The language the prompt is in.
	 */
	public Locale getLanguage()
	{
		return language;
	}

	/**
	 * Returns the prompt timeout, used for record elements only.
	 * 
	 * @return The prompt timeout, used for record elements only.
	 */
	public String getTimeout()
	{
		return timeout;
	}

	/**
	 * Sets the output to render.
	 * 
	 * @param output The output to render.
	 * @throws NullPointerException If the supplied output is <code>null</code>.
	 */
	public void setOutput(Output output) throws NullPointerException
	{
		if (output == null)
			throw new NullPointerException("output"); //$NON-NLS-1$
		this.output = output;
	}

	/**
	 * Sets the barge in flag.
	 * 
	 * @param bargeInEnabled True if the caller can barge in.
	 */
	public void setBargeInEnabled(boolean bargeInEnabled)
	{
		this.bargeInEnabled = bargeInEnabled;
	}

	/**
	 * Sets the language the prompt is in.
	 * 
	 * @param language The language the prompt is in.
	 * @throws NullPointerException If the supplied language is <code>null</code>.
	 */
	public void setLanguage(Locale language) throws NullPointerException
	{
		if (language == null)
			throw new NullPointerException("language"); //$NON-NLS-1$
		this.language = language;
	}

	/**
	 * Sets the prompt timeout, used for record elements only.
	 * 
	 * @param output The prompt timeout, used for record elements only.
	 * @throws IllegalArgumentException If the specified timeout is empty.
	 */
	public void setTimeout(String timeout) throws IllegalArgumentException
	{
		if (timeout != null && timeout.length() == 0)
			throw new IllegalArgumentException("timeout"); //$NON-NLS-1$
		this.timeout = timeout;
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_PROMPT, NAME_PROMPT,
				attributes);
		// Write the children.
		writeOutput(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_PROMPT, NAME_PROMPT);
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
		writeAttribute(attributes, null, null, NAME_BARGEIN, TYPE_CDATA, String
				.valueOf(bargeInEnabled));
		StringBuffer xmlLang = new StringBuffer(language.getLanguage());
		if (language.getCountry() != null && language.getCountry().length() > 0)
			xmlLang.append('-').append(language.getCountry());
		writeAttribute(attributes, null, null, QNAME_XML_LANG, TYPE_CDATA, xmlLang
				.toString());
		if (timeout != null)
			writeAttribute(attributes, null, null, NAME_TIMEOUT, TYPE_CDATA, timeout);
	}

	/**
	 * Write the output in this prompt to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing the output fails.
	 */
	protected void writeOutput(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		output.writeWidget(outputHandler);
	}
}
