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

import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>ExternalGrammar</code> class represents the &lt;grammar&gt; VXML
 * element with a reference to an external grammar file.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class ExternalGrammar extends Grammar
{
	/** The URI of the external grammar file. */
	private String grammarURI;
	/** The MIME type of the external grammar file. */
	private String grammarType = null;

	/**
	 * Creates a new ExternalGrammar.
	 * 
	 * @param mode The mode of the grammar (dtmf or voice).
	 * @param grammarURI The URI of the external grammar file.
	 * @throws IllegalArgumentException If the specified mode is not one of "dtmf"
	 *           or "voice".
	 * @throws IllegalArgumentException If the specified grammar URI is empty.
	 * @throws NullPointerException If the specified mode is <code>null</code>.
	 * @throws NullPointerException If the specified grammar URI is
	 *           <code>null</code>.
	 */
	public ExternalGrammar(String mode, String grammarURI)
			throws IllegalArgumentException, NullPointerException
	{
		super(mode);
		setGrammarURI(grammarURI);
	}

	/**
	 * Creates a new ExternalGrammar.
	 * 
	 * @param mode The mode of the grammar (dtmf or voice).
	 * @param grammarURI The URI of the external grammar file.
	 * @param grammarType The MIME type of the external grammar file.
	 * @throws IllegalArgumentException If the specified mode is not one of "dtmf"
	 *           or "voice".
	 * @throws IllegalArgumentException If the specified grammar URI is empty.
	 * @throws IllegalArgumentException If the specified grammar type is empty.
	 * @throws NullPointerException If the specified mode is <code>null</code>.
	 * @throws NullPointerException If the specified grammar URI is
	 *           <code>null</code>.
	 */
	public ExternalGrammar(String mode, String grammarURI, String grammarType)
			throws IllegalArgumentException, NullPointerException
	{
		super(mode);
		setGrammarURI(grammarURI);
		setGrammarType(grammarType);
	}

	/**
	 * Returns the URI of the external grammar file.
	 * 
	 * @return The URI of the external grammar file.
	 */
	public String getGrammarURI()
	{
		return grammarURI;
	}

	/**
	 * Returns the MIME type of the external grammar file.
	 * 
	 * @return The MIME type of the external grammar file.
	 */
	public String getGrammarType()
	{
		return grammarURI;
	}

	/**
	 * Sets the URI of the external grammar file.
	 * 
	 * @param grammarURI The URI of the external grammar file.
	 * @throws IllegalArgumentException If the specified grammar URI is empty.
	 * @throws NullPointerException If the specified grammar URI is
	 *           <code>null</code>.
	 */
	public void setGrammarURI(String grammarURI) throws IllegalArgumentException,
			NullPointerException
	{
		if (grammarURI == null)
			throw new NullPointerException("grammarURI"); //$NON-NLS-1$
		if (grammarURI.length() == 0)
			throw new IllegalArgumentException("grammarURI"); //$NON-NLS-1$
		this.grammarURI = grammarURI;
	}

	/**
	 * Sets the MIME type of the external grammar file.
	 * 
	 * @param grammarType The MIME type of the external grammar file.
	 * @throws IllegalArgumentException If the specified grammar type is empty.
	 */
	public void setGrammarType(String grammarType)
			throws IllegalArgumentException
	{
		if (grammarType != null && grammarType.length() == 0)
			throw new IllegalArgumentException("grammarType"); //$NON-NLS-1$
		this.grammarType = grammarType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.voice.output.Grammar#writeAttributes(
	 *      org.xml.sax.helpers.AttributesImpl)
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		super.writeAttributes(attributes);
		if (grammarType != null)
			writeAttribute(attributes, null, null, NAME_TYPE, TYPE_CDATA, grammarType);
		else
			writeAttribute(attributes, null, null, NAME_TYPE, TYPE_CDATA,
					detectGrammarType());
		writeAttribute(attributes, null, null, NAME_SRC, TYPE_CDATA, grammarURI);
	}

	/**
	 * Attempts to detect the MIME type for this grammar.
	 * 
	 * @return The detected MIME type for this grammar.
	 */
	protected String detectGrammarType()
	{
		if (grammarURI.endsWith(FILE_EXT_GSL))
			return MIME_TYPE_GSL;
		else
			return MIME_TYPE_SRGS;
	}
}
