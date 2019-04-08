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
 * EventHandler.java Created on Jan 3, 2004
 */
package org.eclipse.vtp.framework.interactions.voice.vxml;

import java.util.LinkedList;

import org.eclipse.vtp.framework.interactions.core.support.Widget;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>EventHandler</code> class is the base class for the family of
 * VXML elements that process events and errors that are generated during
 * execution of a VXML document. Each instance contains a list of actions for
 * the interpreter to perform if the event handler receives an event. These
 * actions are written to the VXML document in the order they were added. There
 * are two optional methods to govern whether the event is received by the event
 * handler instance. A conditional expression can be specified that must
 * evaluate to true at the time the event is generated for the event handler to
 * be notified of the event. Also, an event handler can declare that it only
 * needs to be notified if it is the n<sup>th</sup> occurrence of the event by
 * setting the count field. Each of these options can be employed separately or
 * together.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public abstract class EventHandler extends Widget implements VXMLConstants
{
	/** Number of occurences required. */
	private int count = -1;
	/** The condition that is required. */
	private String condition = null;
	/** The actions and conditionals to perform. */
	private final LinkedList<Widget> actionsAndIfClauses = new LinkedList<Widget>();
	/** The variables set when this filled executes. */
	private final LinkedList<Variable> variables = new LinkedList<Variable>();

	/**
	 * Creates a new instance of <code>EventHandler</code> which does not employ
	 * a conditional expression or require a specific number of occurrences to
	 * receive an event.
	 */
	protected EventHandler()
	{
	}

	/**
	 * Creates a new instance of <code>EventHandler</code> that requires a
	 * specific number of occurrences of an event to receive it. It does not
	 * employ a conditional expression that must be evaluated before receiving an
	 * event.
	 * 
	 * @param count The number of occurrences of an event for this event handler
	 *          to receive it.
	 */
	protected EventHandler(int count)
	{
		setCount(count);
	}

	/**
	 * Creates a new instance of <code>EventHandler</code> with a conditional
	 * expression that must evaluate to true for the event handler to receive an
	 * event. It does not require a specific number of occurrences to receive an
	 * event.
	 * 
	 * @param condition Expression that must evaluate to true for the event
	 *          handler to receive the event
	 * @throws IllegalArgumentException If the supplied condition is empty.
	 */
	protected EventHandler(String condition) throws IllegalArgumentException
	{
		setCondition(condition);
	}

	/**
	 * Creates a new instance of <code>EventHandler</code> with a conditional
	 * expression that must evaluate to true for the event handler to receive an
	 * event. It also requires a specific number of occurrences of the event to
	 * receive it.
	 * 
	 * @param count The number of occurrences of an event for this event handler
	 *          to receive it.
	 * @param condition Expression that must evaluate to true for the event
	 *          handler to receive the event.
	 * @throws IllegalArgumentException If the supplied condition is empty.
	 */
	protected EventHandler(int count, String condition)
			throws IllegalArgumentException
	{
		setCount(count);
		setCondition(condition);
	}

	/**
	 * Returns the number of occurrences of an event that will trigger this event
	 * handler.
	 * 
	 * @return The number of occurrences.
	 */
	public int getCount()
	{
		return count;
	}

	/**
	 * Returns the conditional expression that must evaluate to true before this
	 * event handler can receive an event.
	 * 
	 * @return The conditional expression.
	 */
	public String getCondition()
	{
		return condition;
	}

	/**
	 * Returns the list of actions and conditionals in this event handler.
	 * 
	 * @return The list of actions and conditionals in this event handler.
	 */
	public Widget[] getActionsAndIfClauses()
	{
		return actionsAndIfClauses.toArray(new Widget[actionsAndIfClauses
				.size()]);
	}

	/**
	 * Returns the variables set when this filled executes.
	 * 
	 * @return The variables set when this filled executes.
	 */
	public Variable[] getVariables()
	{
		return variables.toArray(new Variable[variables.size()]);
	}

	/**
	 * Sets the number of occurrences of the event that this event handler will
	 * accept.
	 * 
	 * @param count The new number of occurrences.
	 */
	public void setCount(int count)
	{
		this.count = count;
	}

	/**
	 * Sets the conditional expression that must evaluate to true for the event
	 * handler to accept an event.
	 * 
	 * @param condition The conditional expression.
	 * @throws IllegalArgumentException If the supplied condition is empty.
	 */
	public void setCondition(String condition) throws IllegalArgumentException
	{
		if (condition != null && condition.length() == 0)
			throw new IllegalArgumentException("condition"); //$NON-NLS-1$
		this.condition = condition;
	}

	/**
	 * Adds the specified variable element to this filled handler. Variables
	 * declared at the filled level are accessible to only its child elements.
	 * 
	 * @param variable The variable to add.
	 * @throws NullPointerException If the specified variable is <code>null</code>.
	 */
	public void addVariable(Variable variable) throws NullPointerException
	{
		if (variable == null)
			throw new NullPointerException("variable"); //$NON-NLS-1$
		variables.add(variable);
	}

	/**
	 * Removes the specified variable element from this filled handler. Variables
	 * declared at the filled level are accessible to only its child elements.
	 * 
	 * @param variable The variable to remove.
	 * @throws NullPointerException If the specified variable is <code>null</code>.
	 */
	public void removeVariable(Variable variable) throws NullPointerException
	{
		if (variable == null)
			throw new NullPointerException("variable"); //$NON-NLS-1$
		variables.remove(variable);
	}

	/**
	 * Adds the action to the list of actions to be performed by the interpreter
	 * if this event handler receives an event. The list of actions are written to
	 * the VXML document in the order they were added.
	 * 
	 * @param action The action to add.
	 * @throws NullPointerException If the supplied action is <code>null</code>.
	 */
	public void addAction(Action action) throws NullPointerException
	{
		if (action == null)
			throw new NullPointerException("action"); //$NON-NLS-1$
		actionsAndIfClauses.add(action);
	}

	/**
	 * Removes the action from the list of actions to be performed by the
	 * interpreter if this event handler receives an event.
	 * 
	 * @param action The action to remove.
	 * @throws NullPointerException If the supplied action is <code>null</code>.
	 */
	public void removeAction(Action action) throws NullPointerException
	{
		if (action == null)
			throw new NullPointerException("action"); //$NON-NLS-1$
		actionsAndIfClauses.remove(action);
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
		actionsAndIfClauses.add(script);
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
		actionsAndIfClauses.remove(script);
	}

	/**
	 * Inserts a conditional set of actions into the list of actions to be
	 * performed by the interpreter if this event handler receives an event.
	 * 
	 * @param ifClause The conditional set of actions to add.
	 * @throws NullPointerException If the supplied conditional is
	 *           <code>null</code>.
	 */
	public void addIfClause(If ifClause) throws NullPointerException
	{
		if (ifClause == null)
			throw new NullPointerException("ifClause"); //$NON-NLS-1$
		actionsAndIfClauses.add(ifClause);
	}

	/**
	 * Removes the conditional set of actions from the list of actions to be
	 * performed by the interpreter if this event handler receives an event.
	 * 
	 * @param ifClause The conditional set of actions to remove.
	 * @throws NullPointerException If the supplied conditional is
	 *           <code>null</code>.
	 */
	public void removeIfClause(If ifClause) throws NullPointerException
	{
		if (ifClause == null)
			throw new NullPointerException("ifClause"); //$NON-NLS-1$
		actionsAndIfClauses.remove(ifClause);
	}

	/**
	 * Write the attribute members of this handler to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		if (condition != null)
			writeAttribute(attributes, null, null, NAME_COND, TYPE_CDATA, condition);
		if (count >= 0)
			writeAttribute(attributes, null, null, NAME_COUNT, TYPE_CDATA, String
					.valueOf(count));
	}

	/**
	 * Write the variables in this filled to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the variables fails.
	 */
	protected void writeVariables(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, variables);
	}

	/**
	 * Write the actions and if clauses of this handler to the specified content
	 * handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the actions or if clauses
	 *           fails.
	 */
	protected void writeActionsAndIfClauses(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (outputHandler == null)
			throw new NullPointerException("outputHandler"); //$NON-NLS-1$
		writeChildren(outputHandler, actionsAndIfClauses);
	}
}
