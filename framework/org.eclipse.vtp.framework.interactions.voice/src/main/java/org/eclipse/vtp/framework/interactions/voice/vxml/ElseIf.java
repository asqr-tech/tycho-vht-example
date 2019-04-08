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
 * ElseIf.java Created on Jan 4, 2004
 */
package org.eclipse.vtp.framework.interactions.voice.vxml;

import java.util.LinkedList;

import org.eclipse.vtp.framework.interactions.core.support.Widget;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>ElseIf</code> class represents the &lt;elseif&gt; VXML element.
 * It contains a list of actions for the VXML interpreter to execute if its
 * conditional expression evaluates to <code>true</code>. The list of actions
 * will be written to the VXML document in the order they were added.<br>
 * <br>
 * For more information on the ElseIf class and its usage, see the JavaDoc of
 * the If class.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class ElseIf extends Widget implements VXMLConstants
{
	/** The condition that is required. */
	private String condition;
	/** The actions to perform. */
	private final LinkedList<Widget> actions = new LinkedList<Widget>();

	/**
	 * Creates a new instance of <code>ElseIf</code> with the specified
	 * conditional expression and an empty list of actions.
	 * 
	 * @param condition An expression that evaluates to a Boolean.
	 * @throws IllegalArgumentException If the supplied condition is empty.
	 * @throws NullPointerException If the supplied condition is <code>null</code>.
	 */
	public ElseIf(String condition)
	{
		setCondition(condition);
	}

	/**
	 * Returns the conditional expression that must evaluate to true.
	 * 
	 * @return The conditional expression.
	 */
	public String getCondition()
	{
		return condition;
	}

	/**
	 * Returns the list of actions in this else-if.
	 * 
	 * @return The list of actions in this else-if.
	 */
	public Widget[] getActions()
	{
		return actions.toArray(new Widget[actions.size()]);
	}

	/**
	 * Sets the conditional expression that must evaluate to true.
	 * 
	 * @param condition The conditional expression.
	 * @throws IllegalArgumentException If the supplied condition is empty.
	 * @throws NullPointerException If the supplied condition is <code>null</code>.
	 */
	public void setCondition(String condition) throws IllegalArgumentException,
			NullPointerException
	{
		if (condition == null)
			throw new NullPointerException("condition"); //$NON-NLS-1$
		if (condition.length() == 0)
			throw new IllegalArgumentException("condition"); //$NON-NLS-1$
		this.condition = condition;
	}

	/**
	 * Adds the action to this else-if. The list of actions are written to the
	 * VXML document in the order they were added.
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
	 * Removes the action from this else-if.
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_ELSEIF, NAME_ELSEIF,
				attributes);
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_ELSEIF, NAME_ELSEIF);
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
		writeAttribute(attributes, null, null, NAME_COND, TYPE_CDATA, condition);
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
