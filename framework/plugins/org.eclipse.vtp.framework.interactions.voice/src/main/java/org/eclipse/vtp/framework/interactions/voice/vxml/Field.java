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
import java.util.List;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Field</code> class represents the &lt;field&gt; VXML element.
 * Each field within a <code>Form</code> element can have an initial prompt
 * that is played to the caller when the field is first entered during
 * processing. A field can be allow free-form entry of digits or a DTMF grammar
 * can provided to validate the entry. A field can also have a set of options
 * that the caller input is matched against. Be aware that field options do NOT
 * support the use of the <code>AudioOutput</code> element.<br>
 * <br>
 * As a named element of a form, its value can be used in
 * <code>ValueOutput</code> and <code>Script</code> elements or returned to
 * the IVR system with the <code>Submit</code> action.
 * 
 * @author Trip Gilman
 * @version 1.0
 */
public class Field extends FormElement
{
	/** The type of the field. */
	private String type;
	/** The opening prompt played to the caller. */
	private Prompt prompt = null;
	/** The grammar used to collect the field input. */
	private List<Grammar> grammars = new LinkedList<Grammar>();
	/** The properties of this field. */
	private final PropertiesSupport properties = new PropertiesSupport();
	/** The options that are valid for this field. */
	private final LinkedList<Option> options = new LinkedList<Option>();
	/** The handlers for when this field is filled. */
	private final LinkedList<Filled> filledHandlers = new LinkedList<Filled>();
	/** The event handlers registered with this field. */
	private final LinkedList<EventHandler> eventHandlers = new LinkedList<EventHandler>();

	/**
	 * Creates a new instance of <code>Field</code> with the specified name. The
	 * sets of options, event handlers, filled handlers, and properties are
	 * initially empty. No opening prompt is provided, and no DTMF grammar is
	 * applied.
	 * 
	 * @param name The name this field will be referred to by.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Field(String name) throws IllegalArgumentException,
			NullPointerException
	{
		super(name);
	}

	/**
	 * Creates a new instance of <code>Field</code> with the specified name and
	 * opening prompt. The sets of options, event handlers, filled handlers, and
	 * properties are initially empty. No DTMF grammar is applied.
	 * 
	 * @param name The name this field will be referred to by.
	 * @param prompt The opening prompt played to the caller.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Field(String name, Prompt prompt) throws IllegalArgumentException,
			NullPointerException
	{
		super(name);
		setPrompt(prompt);
	}

	/**
	 * Creates a new instance of <code>Field</code> with the specified name. The
	 * sets of options, event handlers, filled handlers, and properties are
	 * initially empty. No opening prompt is provided, and no DTMF grammar is
	 * applied.
	 * 
	 * @param name The name this field will be referred to by.
	 * @param expression See the documentation for <code>FormElement</code>.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Field(String name, String expression) throws IllegalArgumentException,
			NullPointerException
	{
		super(name, expression);
	}

	/**
	 * Creates a new instance of <code>Field</code> with the specified name and
	 * opening prompt. The sets of options, event handlers, filled handlers, and
	 * properties are initially empty. No DTMF grammar is applied.
	 * 
	 * @param name The name this field will be referred to by.
	 * @param expression See the documentation for <code>FormElement</code>.
	 * @param prompt The opening prompt played to the caller.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Field(String name, String expression, Prompt prompt)
			throws IllegalArgumentException, NullPointerException
	{
		super(name, expression);
		setPrompt(prompt);
	}

	/**
	 * Creates a new instance of <code>Field</code> with the specified name. The
	 * sets of options, event handlers, filled handlers, and properties are
	 * initially empty. No opening prompt is provided, and no DTMF grammar is
	 * applied.
	 * 
	 * @param name The name this field will be referred to by.
	 * @param expression See the documentation for <code>FormElement</code>.
	 * @param condition See the documentation for <code>FormElement</code>.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws IllegalArgumentException If the specified condition is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Field(String name, String expression, String condition)
			throws IllegalArgumentException, NullPointerException
	{
		super(name, expression, condition);
	}

	/**
	 * Creates a new instance of <code>Field</code> with the specified name and
	 * opening prompt. The sets of options, event handlers, filled handlers, and
	 * properties are initially empty. No DTMF grammar is applied.
	 * 
	 * @param name The name this field will be referred to by.
	 * @param expression See the documentation for <code>FormElement</code>.
	 * @param condition See the documentation for <code>FormElement</code>.
	 * @param prompt The opening prompt played to the caller.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws IllegalArgumentException If the specified condition is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Field(String name, String expression, String condition, Prompt prompt)
			throws IllegalArgumentException, NullPointerException
	{
		super(name, expression, condition);
		setPrompt(prompt);
	}

	/**
	 * Returns the type of the field.
	 * 
	 * @return The type of the field.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Returns the opening prompt to play to the caller.
	 * 
	 * @return the opening prompt to play to the caller.
	 */
	public Prompt getPrompt()
	{
		return prompt;
	}

	/**
	 * Returns the DTMF grammar to apply to the caller's input.
	 * 
	 * @return the DTMF grammar to apply to the caller's input.
	 */
	public Grammar[] getGrammar()
	{
		return grammars.toArray(new Grammar[grammars.size()]);
	}

	/**
	 * Returns the names of the properties of this field.
	 * 
	 * @return The names of the properties of this field.
	 */
	public String[] getPropertyNames()
	{
		return properties.getPropertyNames();
	}

	/**
	 * Returns the value of the specified property or <code>null</code> if no
	 * such property exists.
	 * 
	 * @param propertyName The name of the property to find the value of.
	 * @return The value of the specified property or <code>null</code> if no
	 *         such property exists.
	 * @throws NullPointerException If the supplied property name is
	 *           <code>null</code>.
	 */
	public String getPropertyValue(String propertyName)
			throws NullPointerException
	{
		return properties.getPropertyValue(propertyName);
	}

	/**
	 * Returns the list of options for this field.
	 * 
	 * @return The list of options for this field.
	 */
	public Option[] getOptions()
	{
		return options.toArray(new Option[options.size()]);
	}

	/**
	 * Returns the list of filled handlers for this field.
	 * 
	 * @return The list of filled handlers for this field.
	 */
	public Filled[] getFilledHandlers()
	{
		return filledHandlers.toArray(new Filled[filledHandlers.size()]);
	}

	/**
	 * Returns the list of event handlers for this field.
	 * 
	 * @return The list of event handlers for this field.
	 */
	public EventHandler[] getEventHandlers()
	{
		return eventHandlers.toArray(new EventHandler[eventHandlers
				.size()]);
	}

	/**
	 * Sets the type of the field.
	 * 
	 * @param type The type of the field.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Sets the opening prompt to the specified <code>Prompt</code>.
	 * 
	 * @param prompt The new opening prompt to be played to the caller.
	 */
	public void setPrompt(Prompt prompt)
	{
		this.prompt = prompt;
	}

	/**
	 * Specifies the DTMF grammar to match the caller's entered digits against. If
	 * the input does not conform to the rules of the grammar, a "nomatch" event
	 * is generated.
	 * 
	 * @param grammar The DTMF grammar to apply to the caller's input.
	 */
	public void addGrammar(Grammar grammar)
	{
		this.grammars.add(grammar);
	}

	/**
	 * Sets the value of a property in this field.
	 * 
	 * @param propertyName The name of the property to set.
	 * @param propertyValue The value to set the property to.
	 * @throws NullPointerException If the supplied property name or value is
	 *           <code>null</code>.
	 */
	public void setProperty(String propertyName, String propertyValue)
			throws NullPointerException
	{
		properties.setProperty(propertyName, propertyValue);
	}

	/**
	 * Clears the value of a property in this field.
	 * 
	 * @param propertyName The name of the property to clear.
	 * @throws NullPointerException If the supplied property name is
	 *           <code>null</code>.
	 */
	public void clearProperty(String propertyName) throws NullPointerException
	{
		properties.clearProperty(propertyName);
	}

	/**
	 * Adds the specified option to the set of available options for this field.
	 * The options will be enumerated to the caller and matched against the input
	 * in the order they were added.
	 * 
	 * @param option The option to add.
	 * @throws NullPointerException If the supplied option is <code>null</code>.
	 */
	public void addOption(Option option) throws NullPointerException
	{
		if (option == null)
			throw new NullPointerException("option"); //$NON-NLS-1$
		options.add(option);
	}

	/**
	 * Removes the specified option from the set of available options for this
	 * field. The options will be enumerated to the caller and matched against the
	 * input in the order they were added.
	 * 
	 * @param option The option to remove.
	 * @throws NullPointerException If the supplied option is <code>null</code>.
	 */
	public void removeOption(Option option) throws NullPointerException
	{
		if (option == null)
			throw new NullPointerException("option"); //$NON-NLS-1$
		options.remove(option);
	}

	/**
	 * Adds the specified filled handler to this field. The filled handlers will
	 * be executed in the order they were added.<br>
	 * <br>
	 * NOTE: A filled handler element that is added to a field must NOT specify a
	 * mode. Although this caveat is not mentioned in the w3c spec, many
	 * implementations do not allow mode to be declared in this situation.
	 * 
	 * @param filled The filled handler to be added.
	 * @throws NullPointerException If the supplied filled handler is
	 *           <code>null</code>.
	 */
	public void addFilledHandler(Filled filled) throws NullPointerException
	{
		if (filled == null)
			throw new NullPointerException("filled"); //$NON-NLS-1$
		filledHandlers.add(filled);
	}

	/**
	 * Removes the specified filled handler from this field.<br>
	 * <br>
	 * NOTE: A filled handler element that is added to a field must NOT specify a
	 * mode. Although this caveat is not mentioned in the w3c spec, many
	 * implementations do not allow mode to be declared in this situation.
	 * 
	 * @param filled The filled handler to be removed.
	 * @throws NullPointerException If the supplied filled handler is
	 *           <code>null</code>.
	 */
	public void removeFilledHandler(Filled filled) throws NullPointerException
	{
		if (filled == null)
			throw new NullPointerException("filled"); //$NON-NLS-1$
		filledHandlers.remove(filled);
	}

	/**
	 * Adds the specified event handler to this field. The event handlers are
	 * evaluated in the order they were added.
	 * 
	 * @param eventHandler The event handler to add.
	 * @throws NullPointerException If the supplied event handler is
	 *           <code>null</code>.
	 */
	public void addEventHandler(EventHandler eventHandler)
			throws NullPointerException
	{
		if (eventHandler == null)
			throw new NullPointerException("eventHandler"); //$NON-NLS-1$
		eventHandlers.add(eventHandler);
	}

	/**
	 * Removes the specified event handler from this field.
	 * 
	 * @param eventHandler The event handler to remove.
	 * @throws NullPointerException If the supplied event handler is
	 *           <code>null</code>.
	 */
	public void removeEventHandler(EventHandler eventHandler)
			throws NullPointerException
	{
		if (eventHandler == null)
			throw new NullPointerException("eventHandler"); //$NON-NLS-1$
		eventHandlers.remove(eventHandler);
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_FIELD, NAME_FIELD,
				attributes);
		// Write the children.
		writeProperties(outputHandler);
		writePrompt(outputHandler);
		writeGrammar(outputHandler);
		writeOptions(outputHandler);
		writeFilledHandlers(outputHandler);
		writeEventHandlers(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_FIELD, NAME_FIELD);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.voice.vxml.FormElement#
	 *      writeAttributes(org.xml.sax.helpers.AttributesImpl)
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		super.writeAttributes(attributes);
		if (type != null && type.length() > 0)
			writeAttribute(attributes, null, null, NAME_TYPE, TYPE_CDATA, type);
	}

	/**
	 * Writes this field's prompt to the specified content handler.
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
	 * Writes this field's grammar to the specified content handler.
	 * 
	 * @param outputHandler The handler to write the properties to.
	 * @throws SAXException If the writing of the grammar fails.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 */
	protected void writeGrammar(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		for (Grammar g : grammars)
		{
			if (g != null)
				g.writeWidget(outputHandler);
		}
	}

	/**
	 * Writes the properties of this field to the specified content handler.
	 * 
	 * @param outputHandler The handler to write the properties to.
	 * @throws SAXException If the writing of the properties fails.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 */
	protected void writeProperties(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		properties.writeWidget(outputHandler);
	}

	/**
	 * Write the options in this field to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the options fails.
	 */
	protected void writeOptions(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (options.isEmpty())
			return;
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_ENUMERATE,
				NAME_ENUMERATE, new AttributesImpl());
		outputHandler
				.endElement(NAMESPACE_URI_VXML, NAME_ENUMERATE, NAME_ENUMERATE);
		writeChildren(outputHandler, options);
	}

	/**
	 * Write the filled handlers of this field to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the filled handlers fails.
	 */
	protected void writeFilledHandlers(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, filledHandlers);
	}

	/**
	 * Write the event handlers of this field to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the event handlers fails.
	 */
	protected void writeEventHandlers(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, eventHandlers);
	}
}
