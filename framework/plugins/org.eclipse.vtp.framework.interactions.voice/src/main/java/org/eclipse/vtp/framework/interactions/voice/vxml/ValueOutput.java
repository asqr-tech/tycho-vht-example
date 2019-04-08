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
 * The <code>ValueOutput</code> represents the &lt;value&gt; VXML element.
 * This element causes the result of the expression to be rendered to the caller
 * with TTS.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class ValueOutput extends BasicOutput
{
	/** The expression to evaluate and render with TTS. */
	protected String expression;

	/**
	 * Creates a new instance of <code>ValueOutput</code> with the specified
	 * expression.
	 * 
	 * @param expression The expression to evaluate and render with TTS.
	 * @throws IllegalArgumentException If the supplied expression is empty.
	 * @throws NullPointerException If the supplied expression is
	 *           <code>null</code>.
	 */
	public ValueOutput(String expression) throws IllegalArgumentException,
			NullPointerException
	{
		setExpression(expression);
	}

	/**
	 * Returns the expression to evaluate and render with TTS.
	 * 
	 * @return The expression to evaluate and render with TTS.
	 */
	public String getExpression()
	{
		return expression;
	}

	/**
	 * Sets the expression to evaluate and render with TTS.
	 * 
	 * @return The new expression to evaluate and render with TTS.
	 * @throws IllegalArgumentException If the supplied expression is empty.
	 * @throws NullPointerException If the supplied expression is
	 *           <code>null</code>.
	 */
	public void setExpression(String expression) throws IllegalArgumentException,
			NullPointerException
	{
		if (expression == null)
			throw new NullPointerException("expression"); //$NON-NLS-1$
		if (expression.length() == 0)
			throw new IllegalArgumentException("expression"); //$NON-NLS-1$
		this.expression = expression;
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
		// Start and end the element.
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_VALUE, NAME_VALUE,
				attributes);
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_VALUE, NAME_VALUE);
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
		writeAttribute(attributes, null, null, NAME_EVENTEXPR, TYPE_CDATA,
				expression);
	}
}
