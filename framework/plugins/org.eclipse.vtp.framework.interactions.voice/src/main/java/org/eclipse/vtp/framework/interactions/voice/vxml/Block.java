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

import org.eclipse.vtp.framework.interactions.core.support.Widget;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Block</code> element is used to encapsulate an arbitrary set of
 * actions to be executed during the processing of a VXML <code>Form</code>.
 * The actions are executed in the order in which they are added to the
 * <code>Block</code> instance.
 * 
 * <code>Variable</code>s can also be declared in a block element and
 * referenced later during the processing of the form.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Block extends FormElement
{
	/** The variables set when this block executes. */
	private final LinkedList<Variable> variables = new LinkedList<Variable>();
	/** The actions and conditionals that this block executes. */
	private final LinkedList<Widget> actionsAndIfClauses = new LinkedList<Widget>();

	/**
	 * Creates a new instance of a <code>Block</code> element.
	 * 
	 * @param name The name of the block element.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Block(String name) throws IllegalArgumentException,
			NullPointerException
	{
		super(name);
	}

	/**
	 * Creates a new instance of a <code>Block</code> element.
	 * 
	 * @param name The name of the block element.
	 * @param expression An expression that will set the initial value of the
	 *          block element.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Block(String name, String expression) throws IllegalArgumentException,
			NullPointerException
	{
		super(name, expression);
	}

	/**
	 * Creates a new instance of a <code>Block</code> element.
	 * 
	 * @param name The name of the block element.
	 * @param expression An expression that will set the initial value of the
	 *          block element.
	 * @param condition An expression that equates to a boolean value which
	 *          determines if the block will be executed.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws IllegalArgumentException If the specified condition is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Block(String name, String expression, String condition)
			throws IllegalArgumentException, NullPointerException
	{
		super(name, expression, condition);
	}

	/**
	 * Returns the variables set when this block executes.
	 * 
	 * @return The variables set when this block executes.
	 */
	public Variable[] getVariables()
	{
		return variables.toArray(new Variable[variables.size()]);
	}

	/**
	 * Returns the actions and conditionals that this block executes.
	 * 
	 * @return The actions and conditionals that this block executes.
	 */
	public Widget[] getActionsAndIfClauses()
	{
		return actionsAndIfClauses.toArray(new Widget[actionsAndIfClauses
				.size()]);
	}

	/**
	 * Adds the specified variable element to this block.
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
	 * Removes the specified variable element from this block.
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
	 * Adds the action to the list of actions to perform if this block element is
	 * executed.
	 * 
	 * @param action The action to add.
	 * @throws NullPointerException If the specified action is <code>null</code>.
	 */
	public void addAction(Action action) throws NullPointerException
	{
		if (action == null)
			throw new NullPointerException("action"); //$NON-NLS-1$
		actionsAndIfClauses.add(action);
	}

	/**
	 * Removes the action from the list of actions to perform if this block
	 * element is executed.
	 * 
	 * @param action The action to remove.
	 * @throws NullPointerException If the specified action is <code>null</code>.
	 */
	public void removeAction(Action action) throws NullPointerException
	{
		if (action == null)
			throw new NullPointerException("action"); //$NON-NLS-1$
		actionsAndIfClauses.remove(action);
	}

	/**
	 * Adds a set of conditional actions into the list of actions to perform if
	 * this block element is executed.
	 * 
	 * @param ifClause The If clause to add.
	 * @throws NullPointerException If the specified if clause is
	 *           <code>null</code>.
	 */
	public void addIfClause(If ifClause) throws NullPointerException
	{
		if (ifClause == null)
			throw new NullPointerException("ifClause"); //$NON-NLS-1$
		actionsAndIfClauses.add(ifClause);
	}

	/**
	 * Removes a set of conditional actions from the list of actions to perform if
	 * this block element is executed.
	 * 
	 * @param ifClause The If clause to remove.
	 * @throws NullPointerException If the specified if clause is
	 *           <code>null</code>.
	 */
	public void removeIfClause(If ifClause) throws NullPointerException
	{
		if (ifClause == null)
			throw new NullPointerException("ifClause"); //$NON-NLS-1$
		actionsAndIfClauses.remove(ifClause);
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_BLOCK, NAME_BLOCK,
				attributes);
		// Write the children.
		writeVariables(outputHandler);
		writeActionsAndIfClauses(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_BLOCK, NAME_BLOCK);
	}

	/**
	 * Write the variables in this block to the specified content handler.
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
	 * Write the actions and if clauses of this block to the specified content
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
