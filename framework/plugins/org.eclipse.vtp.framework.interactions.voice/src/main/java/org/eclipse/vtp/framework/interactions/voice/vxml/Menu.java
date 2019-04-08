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
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Menu</code> class represents the &lt;menu&gt; VXML element. A
 * menu is the counterpart to the form element. Instead of the free-form nature
 * of the form's field element, a menu provides a set of choices that are
 * typically selected with a single digit entry.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Menu extends Dialog
{
	/** The opening prompt. */
	private Prompt prompt;
	/** The choices in this menu. */
	private final LinkedList<Choice> choices = new LinkedList<Choice>();

	/**
	 * Creates a new instance of <code>Menu</code> with the specified identifier
	 * and opening prompt. The set of properties and choices are initially empty.
	 * Throws an IllegalArgumentException if the prompt argument is
	 * <code>null</code>.
	 * 
	 * @param id See documentation of the Dialog element.
	 * @param prompt The opening prompt.
	 * @throws IllegalArgumentException If the supplied ID is empty.
	 * @throws NullPointerException If the supplied ID is <code>null</code>.
	 * @throws NullPointerException If the supplied prompt is <code>null</code>.
	 */
	public Menu(String id, Prompt prompt) throws IllegalArgumentException,
			NullPointerException
	{
		super(id);
		setPrompt(prompt);
	}

	/**
	 * Creates a new instance of <code>Menu</code> with the specified
	 * identifier, scope, and opening prompt. The set of properties and choices
	 * are initially empty. Throws an IllegalArgumentException if the prompt
	 * argument is <code>null</code>.
	 * 
	 * @param id See documentation of the Dialog element
	 * @param scope See documentation of the Dialog element
	 * @param prompt The opening prompt.
	 * @throws IllegalArgumentException If the supplied ID is empty.
	 * @throws IllegalArgumentException If the supplied scope is invalid.
	 * @throws NullPointerException If the supplied ID is <code>null</code>.
	 * @throws NullPointerException If the supplied scope is <code>null</code>.
	 * @throws NullPointerException If the supplied prompt is <code>null</code>.
	 */
	public Menu(String id, String scope, Prompt prompt)
			throws IllegalArgumentException, NullPointerException
	{
		super(id, scope);
		setPrompt(prompt);
	}

	/**
	 * Returns the opening prompt played to the caller.
	 * 
	 * @return The opening prompt played to the caller.
	 */
	public Prompt getPrompt()
	{
		return prompt;
	}

	/**
	 * Returns the list of choices for this menu.
	 * 
	 * @return The list of choices for this menu.
	 */
	public Choice[] getChoices()
	{
		return choices.toArray(new Choice[choices.size()]);
	}

	/**
	 * Sets the opening prompt to play to the caller.
	 * 
	 * @param prompt The opening prompt.
	 * @throws NullPointerException If the supplied prompt is <code>null</code>.
	 */
	public void setPrompt(Prompt prompt) throws NullPointerException
	{
		if (prompt == null)
			throw new NullPointerException("prompt"); //$NON-NLS-1$
		this.prompt = prompt;
	}

	/**
	 * Adds the specified choice to this menu. The choices will be presented to
	 * the caller in the order they were added.
	 * 
	 * @param choice The choice to add.
	 * @throws NullPointerException If the supplied choice is <code>null</code>.
	 */
	public void addChoice(Choice choice) throws NullPointerException
	{
		if (choice == null)
			throw new NullPointerException("choice"); //$NON-NLS-1$
		choices.add(choice);
	}

	/**
	 * Removes the specified choice from this menu.
	 * 
	 * @param choice The choice to remove.
	 * @throws NullPointerException If the supplied choice is <code>null</code>.
	 */
	public void removeChoice(Choice choice) throws NullPointerException
	{
		if (choice == null)
			throw new NullPointerException("choice"); //$NON-NLS-1$
		choices.remove(choice);
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
				.startElement(NAMESPACE_URI_VXML, NAME_MENU, NAME_MENU, attributes);
		// Write the children.
		writeProperties(outputHandler);
		writePrompt(outputHandler);
		writeChoices(outputHandler);
		writeEventHandlers(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_MENU, NAME_MENU);
	}

	/**
	 * Writes this menu's prompt to the specified content handler.
	 * 
	 * @param outputHandler The handler to write the prompt to.
	 * @throws SAXException If the writing of the prompt fails.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 */
	protected void writePrompt(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (prompt != null)
			prompt.writeWidget(outputHandler);
	}

	/**
	 * Write the choices in this menu to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the choices fails.
	 */
	protected void writeChoices(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, choices);
	}
}
