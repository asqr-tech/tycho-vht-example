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
 * If.java Created on Jan 4, 2004
 */
package org.eclipse.vtp.framework.interactions.voice.vxml;

import java.util.LinkedList;

import org.eclipse.vtp.framework.interactions.core.support.Widget;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>If</code> class represents the &lt;if&gt; VXML element. An if
 * element specifies a conditional expression that determines whether or not the
 * actions it contains should be executed. It is also the container of the
 * secondary conditional elements: <code>ElseIf</code> and <code>Else</code>.
 * This construct follows the same rules as similar facilities of programming
 * languages such as Java&tm;.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class If extends Widget implements VXMLConstants
{
	/** The condition that is required. */
	private String condition;
	/** The actions to perform. */
	private final LinkedList<Widget> actions = new LinkedList<Widget>();
	/** The other conditional branches. */
	private final LinkedList<ElseIf> elseIfs = new LinkedList<ElseIf>();
	/** The final conditional branch. */
	private Else elseClause = null;

	/**
	 * Creates a new instance of <code>If</code> with the specified conditional
	 * expression.
	 * 
	 * @param condition A boolean expression.
	 * @throws IllegalArgumentException If the supplied condition is empty.
	 * @throws NullPointerException If the supplied condition is <code>null</code>.
	 */
	public If(String condition) throws IllegalArgumentException,
			NullPointerException
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
	 * Returns the list of actions in this if.
	 * 
	 * @return The list of actions in this if.
	 */
	public Widget[] getActions()
	{
		return actions.toArray(new Widget[actions.size()]);
	}

	/**
	 * Returns the list of extra conditionals in this if.
	 * 
	 * @return The list of extra conditionals in this if.
	 */
	public ElseIf[] getElseIfs()
	{
		return elseIfs.toArray(new ElseIf[elseIfs.size()]);
	}

	/**
	 * Returns the final conditional in this if.
	 * 
	 * @return The final conditional in this if.
	 */
	public Else getElse()
	{
		return elseClause;
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
	 * Adds the action to this if. The list of actions are written to the VXML
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
	 * Removes the action from this if.
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

	/**
	 * Adds the else-if element to the set of secondary conditionals to evaluate
	 * if this element's condition evaluates to false.
	 * 
	 * @param elseIf The secondary condition to add.
	 * @throws NullPointerException If the supplied conditional is
	 *           <code>null</code>.
	 */
	public void addElseIf(ElseIf elseIf)
	{
		if (elseIf == null)
			throw new NullPointerException("elseIf"); //$NON-NLS-1$
		elseIfs.add(elseIf);
	}

	/**
	 * Removes the else-if element from the set of secondary conditionals to
	 * evaluate if this element's condition evaluates to false.
	 * 
	 * @param elseIf The secondary condition to remove.
	 * @throws NullPointerException If the supplied conditional is
	 *           <code>null</code>.
	 */
	public void removeElseIf(ElseIf elseIf)
	{
		if (elseIf == null)
			throw new NullPointerException("elseIf"); //$NON-NLS-1$
		elseIfs.remove(elseIf);
	}

	/**
	 * Sets the else element to process if this element's condition and all
	 * secondary conditions evaluate to false.
	 * 
	 * @param elseClause The element to process if all other conditions are not
	 *          satisfied.
	 */
	public void setElse(Else elseClause)
	{
		this.elseClause = elseClause;
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_IF, NAME_IF, attributes);
		// Write the children.
		writeActions(outputHandler);
		writeElseIfs(outputHandler);
		writeElse(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_IF, NAME_IF);
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

	/**
	 * Write the else-if conditions in this condition to the specified content
	 * handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the actions fails.
	 */
	protected void writeElseIfs(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, elseIfs);
	}

	/**
	 * Write the else condition in this condition to the specified content
	 * handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the actions fails.
	 */
	protected void writeElse(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (elseClause != null)
			elseClause.writeWidget(outputHandler);
	}
}
