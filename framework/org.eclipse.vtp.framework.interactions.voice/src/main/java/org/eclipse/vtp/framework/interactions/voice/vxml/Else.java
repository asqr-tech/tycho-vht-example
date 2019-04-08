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
 * Else.java Created on Jan 4, 2004
 */
package org.eclipse.vtp.framework.interactions.voice.vxml;

import java.util.LinkedList;

import org.eclipse.vtp.framework.interactions.core.support.Widget;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Else</code> class represents the &lt;else&gt; VXML element. It
 * contains a list of actions to perform if its parent's and siblings'
 * conditions are not satisfied. The actions will be written to the VXML
 * document in the order they are added.<br>
 * <br>
 * <code>
 * &nbsp;&nbsp;&nbsp;. . .<br>
 * <br>
 * If ifClause = new If("variable == '45');<br>
 * Else elseClause = new Else();<br>
 * ifClause.setElseClause(elseClause);<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;. . .
 * </code>
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Else extends Widget implements VXMLConstants
{
	/** The actions to perform. */
	private final LinkedList<Widget> actions = new LinkedList<Widget>();

	/**
	 * Creates a new instance of <code>Else</code> with an empty list of
	 * actions.
	 */
	public Else()
	{
	}

	/**
	 * Returns the list of actions in this else.
	 * 
	 * @return The list of actions in this else.
	 */
	public Widget[] getActions()
	{
		return actions.toArray(new Widget[actions.size()]);
	}

	/**
	 * Adds the action to this else. The list of actions are written to the VXML
	 * document in the order they were added.
	 * 
	 * @param action The action to add.
	 * @throws NullPointerException If the supplied action is <code>null</code>.
	 */
	public void addAction(Action action) throws NullPointerException
	{
		if (action == null)
			throw new NullPointerException("action"); //$NON-NLS-1$
		actions.add(action);
	}

	/**
	 * Removes the action from this else.
	 * 
	 * @param action The action to remove.
	 * @throws NullPointerException If the supplied action is <code>null</code>.
	 */
	public void removeAction(Action action) throws NullPointerException
	{
		if (action == null)
			throw new NullPointerException("action"); //$NON-NLS-1$
		actions.remove(action);
	}

	/**
	 * Adds the action to the list of actions to perform if this filled element is
	 * executed.
	 * 
	 * @param script The action to add.
	 * @throws NullPointerException If the specified action is <code>null</code>.
	 */
	public void addScript(Script script) throws NullPointerException
	{
		if (script == null)
			throw new NullPointerException("script"); //$NON-NLS-1$
		actions.add(script);
	}

	/**
	 * Removes the action from the list of actions to perform if this filled
	 * element is executed.
	 * 
	 * @param script The action to remove.
	 * @throws NullPointerException If the specified action is <code>null</code>.
	 */
	public void removeScript(Script script) throws NullPointerException
	{
		if (script == null)
			throw new NullPointerException("script"); //$NON-NLS-1$
		actions.remove(script);
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
		outputHandler
				.startElement(NAMESPACE_URI_VXML, NAME_ELSE, NAME_ELSE, attributes);
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_ELSE, NAME_ELSE);
		// Write the children.
		writeActions(outputHandler);
		// End the element.
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
	}

	/**
	 * Write the actions in this condition to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the actions fails.
	 */
	protected void writeActions(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, actions);
	}
}
