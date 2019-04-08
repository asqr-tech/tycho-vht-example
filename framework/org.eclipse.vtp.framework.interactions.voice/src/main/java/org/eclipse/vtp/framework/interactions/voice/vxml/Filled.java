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

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.vtp.framework.interactions.core.support.Widget;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Filled</code> class represents the &lt;filled&gt; VXML element.
 * It defines a list of actions to be performed by the VXML interpreter when
 * data has been collected from the caller. Both <code>Field</code> and
 * <code>Form</code> instances can contain <code>Filled</code> elements.
 * 
 * An optional list of names can be defined to specify which elements this
 * filled element is interested in. The mode determines which elements of the
 * name list must be filled before the actions are performed. Setting the mode
 * to Filled.MODE_ANY allows the execution of the filled element when data is
 * collected for any of the named elements, where as, Filled.MODE_ALL only
 * allows execution of the filled element when data has been collected for all
 * of the named elements. The default mode is Filled.MODE_ANY. If no list of
 * named elements is provided, the list defaults to all elements within the
 * current scope.
 * 
 * NOTE: If a filled element is added to a field whose name is not contained
 * within the name list (only if a name list is explicitly defined), the filled
 * element will never execute.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Filled extends Widget implements VXMLConstants
{
	/** The setting that determines which elements need to be filled. */
	private String mode = null;
	/** The element names this filled instance requires. */
	private final LinkedList<String> names = new LinkedList<String>();
	/** The variables set when this filled executes. */
	private final LinkedList<Variable> variables = new LinkedList<Variable>();
	/** The actions and conditionals that this filled executes. */
	private final LinkedList<Widget> actionsAndIfClauses = new LinkedList<Widget>();

	/**
	 * Creates a new instance of <code>Filled</code> that will execute if any of
	 * the elements in the current scope are filled.
	 */
	public Filled()
	{
	}

	/**
	 * Creates a new instance of <code>Filled</code> that will execute based on
	 * the specified <code>mode</code> when the elements in the current scope
	 * are filled.<br>
	 * <ul>
	 * <li>Filled.FILLED_MODE_ANY - When any elements are filled</li>
	 * <li>Filled.FILLED_MODE_ALL - When all elements are filled</li>
	 * </ul>
	 * 
	 * @param mode The setting that determines which elements need to be filled.
	 * @throws IllegalArgumentException If the specified mode is not one of "any"
	 *           or "all".
	 */
	public Filled(String mode) throws IllegalArgumentException,
			NullPointerException
	{
		setMode(mode);
	}

	/**
	 * Creates a new instance of <code>Filled</code> that will execute if any of
	 * the elements named in the array are filled.
	 * 
	 * @param names An array of element names.
	 */
	public Filled(String[] names) throws IllegalArgumentException,
			NullPointerException
	{
		if (names != null)
			for (int i = 0; i < names.length; ++i)
				addName(names[i]);
	}

	/**
	 * Creates a new instance of <code>Filled</code> that will execute based on
	 * the specified <code>mode</code> when the elements named in the array are
	 * filled.<br>
	 * <ul>
	 * <li>Filled.FILLED_MODE_ANY - When any elements are filled</li>
	 * <li>Filled.FILLED_MODE_ALL - When all elements are filled</li>
	 * </ul>
	 * 
	 * @param mode The setting that determines which elements need to be filled.
	 * @param names An array of element names.
	 * @throws IllegalArgumentException If the specified mode is not one of "any"
	 *           or "all".
	 */
	public Filled(String mode, String[] names) throws IllegalArgumentException,
			NullPointerException
	{
		setMode(mode);
		if (names != null)
			for (int i = 0; i < names.length; ++i)
				addName(names[i]);
	}

	/**
	 * Returns the setting that determines which elements need to be filled.
	 * 
	 * @return The setting that determines which elements need to be filled.
	 */
	public String getMode()
	{
		return mode;
	}

	/**
	 * Returns the element names this filled instance requires.
	 * 
	 * @return The element names this filled instance requires.
	 */
	public String[] getNames()
	{
		return names.toArray(new String[names.size()]);
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
	 * Returns the actions and conditionals that this filled executes.
	 * 
	 * @return The actions and conditionals that this filled executes.
	 */
	public Widget[] getActionsAndIfClauses()
	{
		return actionsAndIfClauses.toArray(new Widget[actionsAndIfClauses
				.size()]);
	}

	/**
	 * Sets the setting that determines which elements need to be filled.<br>
	 * <ul>
	 * <li>Filled.FILLED_MODE_ANY - When any elements are filled</li>
	 * <li>Filled.FILLED_MODE_ALL - When all elements are filled</li>
	 * </ul>
	 * 
	 * @param mode The setting that determines which elements need to be filled.
	 * @throws IllegalArgumentException If the specified mode is not one of "any"
	 *           or "all".
	 */
	public void setMode(String mode) throws IllegalArgumentException,
			NullPointerException
	{
		if (mode == null)
			this.mode = null;
		else if (mode.equalsIgnoreCase(FILLED_MODE_ALL))
			this.mode = FILLED_MODE_ALL;
		else if (mode.equalsIgnoreCase(FILLED_MODE_ANY))
			this.mode = FILLED_MODE_ANY;
		else
			throw new IllegalArgumentException("mode==\"" //$NON-NLS-1$
					+ mode + "\""); //$NON-NLS-1$
	}

	/**
	 * Adds the name to the list of element names.
	 * 
	 * @param name The element name to add.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public void addName(String name) throws IllegalArgumentException,
			NullPointerException
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		names.add(name);
	}

	/**
	 * Removes the name from the list of element names.
	 * 
	 * @param name The element name to remove.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public void removeName(String name) throws IllegalArgumentException,
			NullPointerException
	{
		if (name == null)
			throw new NullPointerException("mode"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("mode"); //$NON-NLS-1$
		names.remove(name);
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
	 * Adds the action to the list of actions to perform if this filled element is
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
	 * Removes the action from the list of actions to perform if this filled
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
	 * Adds a set of conditional actions into the list of actions to perform if
	 * this filled element is executed.
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
	 * this filled element is executed.
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_FILLED, NAME_FILLED,
				attributes);
		// Write the children.
		writeVariables(outputHandler);
		writeActionsAndIfClauses(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_FILLED, NAME_FILLED);
	}

	/**
	 * Write the attribute members of this filled to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		if (mode != null)
			writeAttribute(attributes, null, null, NAME_MODE, TYPE_CDATA, mode);
		if (!names.isEmpty())
		{
			StringBuffer nameList = new StringBuffer();
			for (Iterator<String> i = names.iterator(); i.hasNext();)
			{
				nameList.append(i.next());
				if (i.hasNext())
					nameList.append(' ');
			}
			writeAttribute(attributes, null, null, NAME_NAMELIST, TYPE_CDATA,
					nameList.toString());
		}
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
	 * Write the actions and if clauses of this filled to the specified content
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
