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
 * The <code>Choice</code> class represents an option within a
 * <code>Menu</code> element. The caller is presented the output and can
 * select this option by entering the proper DTMF sequence or verbally matching
 * its grammar. <br>
 * <br>
 * If the CED (caller entered digits) matches the grammar contained in the
 * <code>dtmf</code> member, call processing is continued by the document
 * located at the targetURI. <br>
 * <br>
 * The <code>targetURI</code> can be a string literal or an expression that
 * equates to the URI of the next VXML document to be processed.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Choice extends Widget implements VXMLConstants
{
	/** The URI of the next VXML document to process the call. */
	private String targetURI;
	/** The DTMF grammar used to match against the caller's CED. */
	private String dtmf = null;
	/** The TTS and audio output to play to the caller. */
	private Output output = null;
	/** The grammar that describes what input selects this choice. */
	private Grammar grammar = null;

	/**
	 * Creates a new instance of the <code>Choice</code> element. The targetURI
	 * is defined, but the grammars and output are left initially
	 * <code>null</code>.
	 * 
	 * @param targetURI The URI of the next VXML document to process the call.
	 * @throws IllegalArgumentException If supplied target URI is empty.
	 * @throws NullPointerException If supplied target URI is <code>null</code>.
	 */
	public Choice(String targetURI) throws IllegalArgumentException,
			NullPointerException
	{
		setTargetURI(targetURI);
	}

	/**
	 * Creates a new instance of the <code>Choice</code> element. The targetURI
	 * and DTMF grammar are set, but the other grammar and output are left
	 * initialized to <code>null</code>.
	 * 
	 * @param targetURI The URI of the next VXML document to process the call.
	 * @param dtmf The DTMF grammar used to match against the caller's CED.
	 * @throws IllegalArgumentException If supplied target URI is empty.
	 * @throws IllegalArgumentException If supplied DTMF value is empty.
	 * @throws NullPointerException If supplied target URI is <code>null</code>.
	 */
	public Choice(String targetURI, String dtmf) throws IllegalArgumentException,
			NullPointerException
	{
		setTargetURI(targetURI);
		setDTMF(dtmf);
	}

	/**
	 * Creates a new instance of the <code>Choice</code> element. The targetURI
	 * and output are set, but the grammars are left initialized to
	 * <code>null</code>.
	 * 
	 * @param targetURI The URI of the next VXML document to process the call.
	 * @param output The TTS and audio output to play to the caller.
	 * @throws IllegalArgumentException If supplied target URI is empty.
	 * @throws NullPointerException If supplied target URI is <code>null</code>.
	 */
	public Choice(String targetURI, Output output)
			throws IllegalArgumentException, NullPointerException
	{
		setTargetURI(targetURI);
		setOutput(output);
	}

	/**
	 * Creates a new instance of the <code>Choice</code> element. The targetURI
	 * and grammar are set, but the DTMF grammar and output are left initialized
	 * to <code>null</code>.
	 * 
	 * @param targetURI The URI of the next VXML document to process the call.
	 * @param grammar The grammar that describes what input selects this choice.
	 * @throws IllegalArgumentException If supplied target URI is empty.
	 * @throws NullPointerException If supplied target URI is <code>null</code>.
	 */
	public Choice(String targetURI, Grammar grammar)
			throws IllegalArgumentException, NullPointerException
	{
		setTargetURI(targetURI);
		setGrammar(grammar);
	}

	/**
	 * Creates a new instance of <code>Choice</code> element with the specified
	 * targetURI, DTMF grammar, and output, but the other grammar is left
	 * initialized to <code>null</code>.
	 * 
	 * @param targetURI The URI of the next VXML document to process the call.
	 * @param dtmf The DTMF grammar used to match against the caller's CED.
	 * @param output The TTS and audio output to play to the caller.
	 * @throws IllegalArgumentException If supplied target URI is empty.
	 * @throws IllegalArgumentException If supplied DTMF value is empty.
	 * @throws NullPointerException If supplied target URI is <code>null</code>.
	 */
	public Choice(String targetURI, String dtmf, Output output)
			throws IllegalArgumentException, NullPointerException
	{
		setTargetURI(targetURI);
		setDTMF(dtmf);
		setOutput(output);
	}

	/**
	 * Creates a new instance of <code>Choice</code> element with the specified
	 * targetURI and grammars, but the output is left initialized to
	 * <code>null</code>.
	 * 
	 * @param targetURI The URI of the next VXML document to process the call.
	 * @param dtmf The DTMF grammar used to match against the caller's CED.
	 * @param grammar The grammar that describes what input selects this choice.
	 * @throws IllegalArgumentException If supplied target URI is empty.
	 * @throws IllegalArgumentException If supplied DTMF value is empty.
	 * @throws NullPointerException If supplied target URI is <code>null</code>.
	 */
	public Choice(String targetURI, String dtmf, Grammar grammar)
			throws IllegalArgumentException, NullPointerException
	{
		setTargetURI(targetURI);
		setDTMF(dtmf);
		setGrammar(grammar);
	}

	/**
	 * Creates a new instance of <code>Choice</code> element with the specified
	 * targetURI, grammars, and output.
	 * 
	 * @param targetURI The URI of the next VXML document to process the call.
	 * @param dtmf The DTMF grammar used to match against the caller's CED.
	 * @param output The TTS and audio output to play to the caller.
	 * @param grammar The grammar that describes what input selects this choice.
	 * @throws IllegalArgumentException If supplied target URI is empty.
	 * @throws IllegalArgumentException If supplied DTMF value is empty.
	 * @throws NullPointerException If supplied target URI is <code>null</code>.
	 */
	public Choice(String targetURI, String dtmf, Output output, Grammar grammar)
			throws IllegalArgumentException, NullPointerException
	{
		setTargetURI(targetURI);
		setDTMF(dtmf);
		setOutput(output);
		setGrammar(grammar);
	}

	/**
	 * Returns the URI of the VXML document the call should be processed by if
	 * this <code>Choice</code> is selected.
	 * 
	 * @return URI of the target VMXL document.
	 */
	public String getTargetURI()
	{
		return targetURI;
	}

	/**
	 * Returns the DTMF grammar associated with this <code>Choice</code>.
	 * 
	 * @return The DTMF grammar.
	 */
	public String getDTMF()
	{
		return dtmf;
	}

	/**
	 * Returns the <code>Output</code> object to play to the caller to describe
	 * this choice
	 * 
	 * @return The output to play to the caller.
	 */
	public Output getOutput()
	{
		return output;
	}

	/**
	 * Returns the grammar that describes what input selects this choice.
	 * 
	 * @return The grammar that describes what input selects this choice
	 */
	public Grammar getGrammar()
	{
		return grammar;
	}

	/**
	 * Sets the URI of the next vxml page to process if this menu choice is
	 * selected by the caller. Throws an IllegalArgumentException if the
	 * <code>targetURI</code> argument is <code>null</code> or an empty
	 * string.
	 * 
	 * @param targetURI The URI of the next page to process
	 * @throws IllegalArgumentException If supplied target URI is empty.
	 * @throws NullPointerException If supplied target URI is <code>null</code>.
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

	/**
	 * Set the simple DTMF grammar that governs the CED the caller uses to select
	 * this menu choice.
	 * 
	 * @param dtmf The simple DTMF grammar.
	 * @throws IllegalArgumentException If supplied DTMF value is empty.
	 */
	public void setDTMF(String dtmf) throws IllegalArgumentException
	{
		if (dtmf.length() == 0)
			throw new IllegalArgumentException("dtmf"); //$NON-NLS-1$
		this.dtmf = dtmf;
	}

	/**
	 * Sets the output to be rendered to the caller when this <code>Choice</code>
	 * is enumerated.
	 * 
	 * @param output The output to play to the caller.
	 */
	public void setOutput(Output output)
	{
		this.output = output;
	}

	/**
	 * Sets the grammar that describes what input selects this choice.
	 * 
	 * @param grammar The grammar that describes what input selects this choice.
	 */
	public void setGrammar(Grammar grammar)
	{
		this.grammar = grammar;
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_CHOICE, NAME_CHOICE,
				attributes);
		// Write the children.
		writeGrammar(outputHandler);
		writeOutput(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_CHOICE, NAME_CHOICE);
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
		if (dtmf != null)
			writeAttribute(attributes, null, null, NAME_DTMF, TYPE_CDATA, dtmf);
		writeAttribute(attributes, null, null, NAME_NEXT, TYPE_CDATA, targetURI);
	}

	/**
	 * Write the grammar in this choice to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the actions fails.
	 */
	protected void writeGrammar(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (grammar != null)
			grammar.writeWidget(outputHandler);
	}

	/**
	 * Write the output in this choice to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the actions fails.
	 */
	protected void writeOutput(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (output != null)
			output.writeWidget(outputHandler);
	}
}
