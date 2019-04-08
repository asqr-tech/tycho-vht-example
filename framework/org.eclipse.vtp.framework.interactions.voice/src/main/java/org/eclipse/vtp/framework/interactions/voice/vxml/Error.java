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
/*
 * Error.java Created on Jan 4, 2004
 */
package org.eclipse.vtp.framework.interactions.voice.vxml;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Error</code> class represents the &lt;error&gt; VXML element.
 * This type of event handler only receives errors. Error event names start with
 * "error." and can be well known or platform specific.<br>
 * <br>
 * An <code>Error</code> instance catches only errors with names that match
 * the specified name. The specified error name matches if it is equal to or is
 * a prefix of the name of the generated error.<br>
 * <br>
 * As with other event handlers execution of the error handler is dependent on
 * the evaluation of the conditional expression and the numbers of times it has
 * occurred during the processing of the current VXML document.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Error extends Catch
{
	/**
	 * Creates a new instance of the <code>Error</code> class that will catch
	 * errors whose names match the <code>errorName</code> argument. Throws an
	 * IllegalArgumentException if the error name is <code>null</code>.
	 * 
	 * @param errorName The name or prefix of the errors to catch.
	 * @throws IllegalArgumentException If the supplied error name is empty.
	 * @throws NullPointerException If the supplied error name is
	 *           <code>null</code>.
	 */
	public Error(String errorName)
	{
		super(errorName);
	}

	/**
	 * Creates a new instance of the <code>Error</code> class that will catch
	 * errors whose names match the <code>errorName</code> argument and who have
	 * occurred <code>count</code> number of times. Throws an
	 * IllegalArgumentException if the error name is <code>null</code>.
	 * 
	 * @param errorName The name or prefix of the errors to catch.
	 * @param count The occurrence of the named error to catch.
	 * @throws IllegalArgumentException If the supplied error name is empty.
	 * @throws NullPointerException If the supplied error name is
	 *           <code>null</code>.
	 */
	public Error(String errorName, int count)
	{
		super(errorName, count);
	}

	/**
	 * Creates a new instance of the <code>Error</code> class that will catch
	 * errors whose names match the <code>errorName</code> argument, but only
	 * when the conditional expression evaluates to <code>true</code>. Throws
	 * an IllegalArgumentException if the error name is <code>null</code>.
	 * 
	 * @param errorName The name or prefix of the errors to catch.
	 * @param condition An expression that must evaluate to true for this instance
	 *          to catch an error.
	 * @throws IllegalArgumentException If the supplied error name is empty.
	 * @throws IllegalArgumentException If the supplied condition is empty.
	 * @throws NullPointerException If the supplied error name is
	 *           <code>null</code>.
	 */
	public Error(String errorName, String condition)
	{
		super(errorName, condition);
	}

	/**
	 * Creates a new instance of the <code>Error</code> class that will catch
	 * errors whose names match the <code>errorName</code> argument and who have
	 * occurred <code>count</code> number of times, but only when the
	 * conditional expression evaluates to <code>true</code>. Throws an
	 * IllegalArgumentException if the error name is <code>null</code>.
	 * 
	 * @param errorName The name or prefix of the errors to catch.
	 * @param condition An expression that must evaluate to true for this instance
	 *          to catch an error.
	 * @param count The occurrence of the named error to catch.
	 * @throws IllegalArgumentException If the supplied error name is empty.
	 * @throws IllegalArgumentException If the supplied condition is empty.
	 * @throws NullPointerException If the supplied error name is
	 *           <code>null</code>.
	 */
	public Error(String errorName, String condition, int count)
	{
		super(errorName, condition, count);
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_ERROR, NAME_ERROR,
				attributes);
		// Write the children.
		writeActionsAndIfClauses(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_ERROR, NAME_ERROR);
	}
}
