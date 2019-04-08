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
 * The <code>Option</code> class represents the &lt;option&gt; VXML element.
 * Options are used to enumerate valid inputs within field form elements. The
 * DTMF of each option can be provided, or one will be assigned by the field
 * element during processing. A value can be defined that will be assigned to
 * the field element if the option is selected. A TTS output can associated with
 * an option that will be played to the caller. Unlike the Choice element, only
 * TTS is supported.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Option extends Widget implements VXMLConstants
{
	/** The TTS output to play to the caller. */
	private BasicOutput output = null;
	/** The DTMF grammar that selects this option. */
	private String dtmf = null;
	/** The value to assign to the field element if this option is selected. */
	private String value = null;

	/**
	 * Creates a new instance of <code>Option</code> with no DTMF grammar,
	 * value, or TTS output.
	 */
	public Option()
	{
	}

	/**
	 * Creates a new instance of <code>Option</code> with the specified TTS
	 * output. The DTMF grammar and value fields are initialized to
	 * <code>null</code>.
	 * 
	 * @param output The TTS output to play to the caller.
	 */
	public Option(BasicOutput output)
	{
		setOutput(output);
	}

	/**
	 * Creates a new instance of <code>Option</code> with the specified TTS
	 * output and DTMF grammar. The value field is initialized to
	 * <code>null</code>. Throws an IllegalArgumentException if the dtmf
	 * argument is an empty string.
	 * 
	 * @param output The TTS output to play to the caller.
	 * @param dtmf The DTMF grammar that selects this option.
	 * @throws IllegalArgumentException If the supplied DTMF value is empty.
	 */
	public Option(BasicOutput output, String dtmf)
			throws IllegalArgumentException
	{
		setOutput(output);
		setDTMF(dtmf);
	}

	/**
	 * Creates a new instance of <code>Option</code> with the specified TTS
	 * output, DTMF grammar, and value. Throws an IllegalArgumentException if the
	 * dtmf or value arguments are empty strings.
	 * 
	 * @param output The TTS output to play to the caller.
	 * @param dtmf The DTMF grammar that selects this option.
	 * @param value The value to assign to the field element if this option is
	 *          selected.
	 * @throws IllegalArgumentException If the supplied DTMF value is empty.
	 * @throws IllegalArgumentException If the supplied option value is empty.
	 */
	public Option(BasicOutput output, String dtmf, String value)
			throws IllegalArgumentException
	{
		setOutput(output);
		setDTMF(dtmf);
		setValue(value);
	}

	/**
	 * Returns the TTS output that is played to the caller when this option is
	 * enumerated to the caller.
	 * 
	 * @return the TTS output that is played to the caller when this option is
	 *         enumerated to the caller.
	 */
	public BasicOutput getOutput()
	{
		return output;
	}

	/**
	 * Returns the DTMF grammar that selects this option.
	 * 
	 * @return the DTMF grammar that selects this option.
	 */
	public String getDTMF()
	{
		return dtmf;
	}

	/**
	 * Returns the value that will be assigned to the field element if this option
	 * is selected.
	 * 
	 * @return the value that will be assigned to the field element if this option
	 *         is selected.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the TTS output that is played to the caller when this option is
	 * enumerated to the caller.
	 * 
	 * @param output The TTS output to play to the caller
	 */
	public void setOutput(BasicOutput output)
	{
		this.output = output;
	}

	/**
	 * Sets the DTMF grammar that selects this option. Throws an
	 * IllegalArgumentException if the DTMF argument is an empty string.
	 * 
	 * @param dtmf The DTMF grammar
	 * @throws IllegalArgumentException If the supplied DTMF value is empty.
	 */
	public void setDTMF(String dtmf) throws IllegalArgumentException
	{
		if (dtmf != null && dtmf.length() == 0)
			throw new IllegalArgumentException("dtmf"); //$NON-NLS-1$
		this.dtmf = dtmf;
	}

	/**
	 * Sets the value that will be assigned to the field element if this option is
	 * selected. Throws an IllegalArgumentException if the value argument is an
	 * empty string.
	 * 
	 * @param value The value to assign to the field
	 * @throws IllegalArgumentException If the supplied option value is empty.
	 */
	public void setValue(String value)
	{
		if (value != null && value.length() == 0)
			throw new IllegalArgumentException("value"); //$NON-NLS-1$
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.voice.output.VXMLWidget#writeWidget(
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_OPTION, NAME_OPTION,
				attributes);
		// Write the children.
		writeOutput(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_OPTION, NAME_OPTION);
	}

	/**
	 * Write the attribute members of this option to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		if (dtmf != null)
			writeAttribute(attributes, null, null, NAME_DTMF, TYPE_CDATA, dtmf);
		if (value != null)
			writeAttribute(attributes, null, null, NAME_VALUE, TYPE_CDATA, value);
	}

	/**
	 * Write the output in this option to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing the output fails.
	 */
	protected void writeOutput(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (output != null)
			output.writeWidget(outputHandler);
	}
}
