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

import java.util.LinkedList;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * The <code>OutputSet</code> class allows the combination of
 * <code>Output</code>s into a single output for the IVR to render to the
 * caller. The outputs will be rendered in the order they are added.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class BasicOutputSet extends BasicOutput
{
	/** The list of outputs in this set. */
	private final LinkedList<BasicOutput> outputs = new LinkedList<BasicOutput>();

	/**
	 * Constructs a new instance of <code>OutputSet</code> whose list of outputs
	 * is initially empty.
	 */
	public BasicOutputSet()
	{
	}

	/**
	 * Returns the list of outputs in this set.
	 * 
	 * @return The list of outputs in this set.
	 */
	public BasicOutput[] getOutputs()
	{
		return outputs.toArray(new BasicOutput[outputs.size()]);
	}

	/**
	 * Appends the <code>BasicOutput</code> to the list of outputs to render to
	 * the caller.
	 * 
	 * @param output The <code>BasicOutput</code> instance to append to the
	 *          list.
	 * @throws NullPointerException If the supplied action is <code>null</code>.
	 */
	public void addOutput(BasicOutput output) throws NullPointerException
	{
		if (output == null)
			throw new NullPointerException("output"); //$NON-NLS-1$
		outputs.add(output);
	}

	/**
	 * Removes the <code>BasicOutput</code> from the list of outputs to render
	 * to the caller.
	 * 
	 * @param output The <code>BasicOutput</code> instance to remove from the
	 *          list.
	 * @throws NullPointerException If the supplied action is <code>null</code>.
	 */
	public void removeOutput(BasicOutput output) throws NullPointerException
	{
		if (output == null)
			throw new NullPointerException("output"); //$NON-NLS-1$
		outputs.remove(output);
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
		writeOutputs(outputHandler);
	}

	/**
	 * Write the outputs in this set to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the actions fails.
	 */
	protected void writeOutputs(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, outputs);
	}
}
