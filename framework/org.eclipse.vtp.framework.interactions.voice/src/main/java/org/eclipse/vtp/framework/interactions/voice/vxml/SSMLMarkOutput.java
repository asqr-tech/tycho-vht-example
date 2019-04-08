/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods) - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.voice.vxml;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>SSMLMarkOutput</code> class represents the SSML mark tag.  A name
 * for the tag is supplied and used to identify the last mark encountered by the
 * voice browser prior to barge in.
 * 
 * @author Trip Gilman
 * @version 1.0
 */
public class SSMLMarkOutput extends BasicOutput
{
	/** The name of this mark tag. */
	private String name = EMPTY;

	/**
	 * Creates a new instance of <code>SSMLMarkOutput</code> with the specified
	 * name.
	 * 
	 * @param name The name of this mark tag
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 */
	public SSMLMarkOutput(String name) throws NullPointerException
	{
		setName(name);
	}

	/**
	 * @return the current name of this mark tag
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this mark tag
	 * 
	 * @param name The new name of this mark tag
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 */
	public void setName(String name) throws NullPointerException
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		this.name = name;
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
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_MARK, NAME_MARK,
				attributes);
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_MARK, NAME_MARK);
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
		writeAttribute(attributes, null, null, NAME_NAME, TYPE_CDATA, name);
	}
}
