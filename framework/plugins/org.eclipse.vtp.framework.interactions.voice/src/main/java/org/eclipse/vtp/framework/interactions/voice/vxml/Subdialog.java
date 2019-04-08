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
 * The <code>Subdialog</code> class represents the &lt;subdialog&gt; VXML
 * element. From VXML 2.0 Proposed Recommendation 3:
 * 
 * <pre>
 * The &lt;subdialog&gt; element invokes a 'called' dialog (known as the subdialog)
 * identified by its src or srcexpr attribute in the 'calling' dialog. The
 * subdialog executes in a new execution context that includes all the
 * declarations and state information for the subdialog, the subdialog�s
 * document, and the subdialog�s application root (if present), with counters
 * reset, and variables initialized. The subdialog proceeds until the execution
 * of a &lt;return&gt; or &lt;exit&gt; element, or until no form items remain eligible for
 * the FIA to select (equivalent to an &lt;exit&gt;). A &lt;return&gt; element causes
 * control and data to be returned to the calling dialog (Section 5.3.10). When
 * the subdialog returns, its execution context is deleted, and execution
 * resumes in the calling dialog with any appropriate &lt;filled&gt; elements.
 * </pre>
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Subdialog extends FormElement
{
	/** The URI of the document that contains the subdialog. */
	private String sourceURI = null;
	/** The parameters to pass to the subdialog. */
	private final LinkedList<Parameter> parameters = new LinkedList<Parameter>();
	/** List of filled handlers for the subdialog. */
	private final LinkedList<Filled> filledHandlers = new LinkedList<Filled>();
	/** List of event handlers for the subdialog. */
	private final LinkedList<EventHandler> eventHandlers = new LinkedList<EventHandler>();
	
	private String nameList = null;

	/**
	 * Creates a new instance of Subdialog with the specified name.
	 * 
	 * @param name The name of the subdialog.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Subdialog(String name) throws IllegalArgumentException,
			NullPointerException
	{
		super(name);
	}

	/**
	 * Creates a new instance of Subdialog with the specified name and an
	 * expression that evaluates to the subdialog's initial value.
	 * 
	 * @param name The name of the subdialog.
	 * @param expression Evaluates to the subdialog's initial value.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Subdialog(String name, String expression)
			throws IllegalArgumentException, NullPointerException
	{
		super(name, expression);
	}

	/**
	 * Creates a new instance of Subdialog with the specified name, initial value
	 * derived from evaluating the expression, and condition that must be true for
	 * the subdialog to be visited.
	 * 
	 * @param name The name of the subdialog.
	 * @param expression Evaluates to the subdialog's initial value.
	 * @param condition Determines if this subdialog will be visited.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws IllegalArgumentException If the specified condition is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Subdialog(String name, String expression, String condition)
			throws IllegalArgumentException, NullPointerException
	{
		super(name, expression, condition);
	}

	/**
	 * Creates a new instance of Subdialog with the specified name, initial value
	 * derived from evaluating the expression, condition that must be true for the
	 * subdialog to be visited, and source URI.
	 * 
	 * @param name The name of the subdialog.
	 * @param expression Evaluates to the subdialog's initial value.
	 * @param condition Determines if this subdialog will be visited.
	 * @param sourceURI URI of the document that contains the target dialog.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws IllegalArgumentException If the specified condition is empty.
	 * @throws IllegalArgumentException If the specified URI is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Subdialog(String name, String expression, String condition,
			String sourceURI) throws IllegalArgumentException, NullPointerException
	{
		super(name, expression, condition);
		setSourceURI(sourceURI);
	}

	/**
	 * Returns the URI of the document that contains the target dialog.
	 * 
	 * @return URI of the target document.
	 */
	public String getSourceURI()
	{
		return sourceURI;
	}
	
	public String getNameList()
	{
		return nameList;
	}

	/**
	 * Returns the list of parameters for this subdialog.
	 * 
	 * @return The list of parameters for this subdialog.
	 */
	public Parameter[] getParameters()
	{
		return parameters.toArray(new Parameter[parameters.size()]);
	}

	/**
	 * Returns the list of filled handlers for this subdialog.
	 * 
	 * @return The list of filled handlers for this subdialog.
	 */
	public Filled[] getFilledHandlers()
	{
		return filledHandlers.toArray(new Filled[filledHandlers.size()]);
	}

	/**
	 * Returns the list of event handlers for this subdialog.
	 * 
	 * @return The list of event handlers for this subdialog.
	 */
	public EventHandler[] getEventHandlers()
	{
		return eventHandlers.toArray(new EventHandler[eventHandlers
				.size()]);
	}

	/**
	 * Sets the URI of the document that contains the target dialog.
	 * 
	 * @param sourceURI URI of the new target document.
	 * @throws IllegalArgumentException If the specified URI is empty.
	 */
	public void setSourceURI(String sourceURI) throws IllegalArgumentException
	{
		if (sourceURI != null && sourceURI.length() == 0)
			throw new IllegalArgumentException("sourceURI"); //$NON-NLS-1$
		this.sourceURI = sourceURI;
	}
	
	public void setNameList(String nameList)
	{
		this.nameList = nameList;
		if (nameList != null && nameList.length() == 0)
			this.nameList = null;
	}

	/**
	 * Adds the specified parameter to the set of parameters for this subdialog.
	 * 
	 * @param parameter The parameter to add.
	 * @throws NullPointerException If the supplied parameter is <code>null</code>.
	 */
	public void addParameter(Parameter parameter) throws NullPointerException
	{
		if (parameter == null)
			throw new NullPointerException("parameter"); //$NON-NLS-1$
		parameters.add(parameter);
	}

	/**
	 * Removes the specified parameter from the set of parameters for this
	 * subdialog.
	 * 
	 * @param parameter The parameter to remove.
	 * @throws NullPointerException If the supplied parameter is <code>null</code>.
	 */
	public void removeParameter(Parameter parameter) throws NullPointerException
	{
		if (parameter == null)
			throw new NullPointerException("parameter"); //$NON-NLS-1$
		parameters.remove(parameter);
	}

	/**
	 * Adds the specified filled handler to this subdialog. The filled handlers
	 * will be executed in the order they were added.
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
	 * Removes the specified filled handler from this subdialog.
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
	 * Adds the specified event handler to this subdialog. The event handlers are
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
	 * Removes the specified event handler from this subdialog.
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
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_SUBDIALOG, NAME_SUBDIALOG,
				attributes);
		// Write the children.
		writeParameters(outputHandler);
		writeFilledHandlers(outputHandler);
		writeEventHandlers(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_SUBDIALOG, NAME_SUBDIALOG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.voice.output.FormElement#
	 *      writeAttributes(org.xml.sax.helpers.AttributesImpl)
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		super.writeAttributes(attributes);
		if (sourceURI != null)
			writeAttribute(attributes, null, null, NAME_SRC, TYPE_CDATA, sourceURI);
		if(nameList != null)
			writeAttribute(attributes, null, null, NAME_NAMELIST, TYPE_CDATA, nameList);
	}

	/**
	 * Write the parameters in this subdialog to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the parameters fails.
	 */
	protected void writeParameters(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, parameters);
	}

	/**
	 * Write the filled handlers of this subdialog to the specified content
	 * handler.
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
	 * Write the event handlers of this subdialog to the specified content
	 * handler.
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
