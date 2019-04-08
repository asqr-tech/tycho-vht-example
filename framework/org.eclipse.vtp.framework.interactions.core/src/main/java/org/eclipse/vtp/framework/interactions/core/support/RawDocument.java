/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods),
 *    T.D. Barnes (OpenMethods) - initial API and implementation
 -------------------------------------------------------------------------*/
/*
 * RawWidgetDocument.java Created on Aug 25, 2004
 */
package org.eclipse.vtp.framework.interactions.core.support;

import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;

/**
 * An output document backed by an in-memory XML stream.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 3.0
 */
public class RawDocument implements IDocument
{
	/** The the type of XML document this instance contains. */
	private final String documentType;
	/** The the type of XML document this instance contains. */
	private final String contentType;
	/** The textual content of this document. */
	private final StringBuffer text = new StringBuffer();
	private boolean secured = false;
	/** Determines if the receiving browser should cache this document */
	private boolean cachable = true;

	/**
	 * Creates a new <code>RawOutputDocument</code>.
	 * 
	 * @param documentType The the type of XML document this instance contains.
	 */
	public RawDocument(String documentType)
	{
		this(documentType, null);
	}

	/**
	 * Creates a new <code>RawOutputDocument</code>.
	 * 
	 * @param documentType The the type of XML document this instance contains.
	 */
	public RawDocument(String documentType, String contentType)
	{
		this.documentType = documentType;
		this.contentType = contentType == null ? DEFAULT_CONTENT_TYPE : contentType;
	}

	/**
	 * Adds the given text to the end of the current document text.
	 * 
	 * @param text The text to add.
	 */
	public void appendText(String text)
	{
		this.text.append(text);
	}

	/**
	 * Clears all the text contained in this document.
	 */
	public void clearText()
	{
		text.setLength(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.core.output.IOutputDocument#
	 *      getDocumentType()
	 */
	public String getDocumentType()
	{
		return documentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.core.output.IDocument#getContentType()
	 */
	public String getContentType()
	{
		return contentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.core.output.OutputDocument#
	 *      toXMLSource()
	 */
	public Source toXMLSource() throws IllegalStateException
	{
		return new StreamSource(new StringReader(text.toString()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return text.toString();
	}

	public boolean isSecured()
    {
	    return secured;
    }
	
	public void setSecured(boolean secured)
	{
		this.secured = secured;
	}

	public boolean isCachable()
	{
		return cachable;
	}
	
	public void setCachable(boolean bool)
	{
		this.cachable = bool;
	}

}
