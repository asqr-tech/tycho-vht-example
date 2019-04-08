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
 * The <code>Form</code> class represents the &lt;form&gt; VXML element. A
 * form can contain any number of <code>Block</code> and <code>Field</code>
 * elements. The child elements are presented to the caller in the order they
 * were added to the form.<br>
 * <br>
 * Instead of handling filled events within each field of the form, filled
 * handlers can be added to the form. This provides flexibility in the way a
 * form is completed or progresses during processing.<br>
 * <br>
 * Variables can be added at the form level. These variables are then accessible
 * to all child elements and are a convenient way to pass data from one child to
 * another.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Form extends Dialog
{
	/** The variables in this form. */
	private final LinkedList<Variable> variables = new LinkedList<Variable>();
	/** The elements in this form. */
	private final LinkedList<FormElement> formElements = new LinkedList<FormElement>();
	/** The filled handlers in this form. */
	private final LinkedList<Filled> filledHandlers = new LinkedList<Filled>();

	/**
	 * Creates a new instance of <code>Form</code> with the specified
	 * identifier. The sets of form elements, variables, and filled handlers are
	 * initially empty.
	 * 
	 * @param id See documentation of the Dialog element.
	 * @throws IllegalArgumentException If the supplied ID is empty.
	 * @throws NullPointerException If the supplied ID is <code>null</code>.
	 */
	public Form(String id) throws IllegalArgumentException, NullPointerException
	{
		super(id);
	}

	/**
	 * Creates a new instance of <code>Form</code> with the specified identifier
	 * and scope. The sets of form elements, variables, and filled handlers are
	 * initially empty.
	 * 
	 * @param id See documentation of the Dialog element.
	 * @param scope See documentation of the Dialog element.
	 * @throws IllegalArgumentException If the supplied ID is empty.
	 * @throws IllegalArgumentException If the supplied scope is invalid.
	 * @throws NullPointerException If the supplied ID is <code>null</code>.
	 * @throws NullPointerException If the supplied scope is <code>null</code>.
	 */
	public Form(String id, String scope) throws IllegalArgumentException,
			NullPointerException
	{
		super(id, scope);
	}

	/**
	 * Returns the list of variables for this form.
	 * 
	 * @return The list of variables for this form.
	 */
	public Variable[] getVariables()
	{
		return variables.toArray(new Variable[variables.size()]);
	}

	/**
	 * Returns the list of elements for this form.
	 * 
	 * @return The list of elements for this form.
	 */
	public FormElement[] getFormElements()
	{
		return formElements.toArray(new FormElement[formElements
				.size()]);
	}

	/**
	 * Returns the list of filled handlers for this form.
	 * 
	 * @return The list of filled handlers for this form.
	 */
	public Filled[] getFilledHandlers()
	{
		return filledHandlers.toArray(new Filled[filledHandlers.size()]);
	}

	/**
	 * Adds the specified variable element to this form. Variables declared at the
	 * form level are accessible to all child elements.
	 * 
	 * @param variable The variable to add.
	 * @throws NullPointerException If the supplied variable is <code>null</code>.
	 */
	public void addVariable(Variable variable) throws NullPointerException
	{
		if (variable == null)
			throw new NullPointerException("variable"); //$NON-NLS-1$
		variables.add(variable);
	}

	/**
	 * Removes the specified variable element from this form.
	 * 
	 * @param variable The variable to remove.
	 * @throws NullPointerException If the supplied variable is <code>null</code>.
	 */
	public void removeVariable(Variable variable) throws NullPointerException
	{
		if (variable == null)
			throw new NullPointerException("variable"); //$NON-NLS-1$
		variables.remove(variable);
	}

	/**
	 * Adds the specified form element to this form. Form elements will be
	 * processed in the order they were added.
	 * 
	 * @param formElement The form element to add.
	 * @throws NullPointerException If the supplied element is <code>null</code>.
	 */
	public void addFormElement(FormElement formElement)
			throws NullPointerException
	{
		if (formElement == null)
			throw new NullPointerException("formElement"); //$NON-NLS-1$
		formElements.add(formElement);
	}

	/**
	 * Removes the specified form element from this form.
	 * 
	 * @param formElement The form element to remove.
	 * @throws NullPointerException If the supplied element is <code>null</code>.
	 */
	public void removeFormElement(FormElement formElement)
			throws NullPointerException
	{
		if (formElement == null)
			throw new NullPointerException("formElement"); //$NON-NLS-1$
		formElements.remove(formElement);
	}

	/**
	 * Adds the specified filled handler to this form. Filled handlers at the form
	 * level are processed when some or all of the form's child elements are set,
	 * depending on the mode of the handler. Filled handlers are executed in the
	 * order they were added.
	 * 
	 * @param filled The filled handler to add.
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
	 * Removes the specified filled handler from this form.
	 * 
	 * @param filled The filled handler to remove.
	 * @throws NullPointerException If the supplied filled handler is
	 *           <code>null</code>.
	 */
	public void removeFilledHandler(Filled filled) throws NullPointerException
	{
		if (filled == null)
			throw new NullPointerException("filled"); //$NON-NLS-1$
		filledHandlers.remove(filled);
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
				.startElement(NAMESPACE_URI_VXML, NAME_FORM, NAME_FORM, attributes);
		// Write the children.
		writeProperties(outputHandler);
		writeVariables(outputHandler);
		writeFormElements(outputHandler);
		writeFilledHandlers(outputHandler);
		writeEventHandlers(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_FORM, NAME_FORM);
	}

	/**
	 * Write the variables in this form to the specified content handler.
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
	 * Write the elements in this form to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the elements fails.
	 */
	protected void writeFormElements(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, formElements);
	}

	/**
	 * Write the filled handlers in this form to the specified content handler.
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
}
