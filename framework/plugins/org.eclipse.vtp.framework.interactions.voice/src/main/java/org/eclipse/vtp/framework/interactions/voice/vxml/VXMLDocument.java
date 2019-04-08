/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods),
 *    Vincent Pruitt (OpenMethods)
 *    
 *    T.D. Barnes (OpenMethods) - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.voice.vxml;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.vtp.framework.interactions.core.support.WidgetDocument;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Document type for VXML widgets.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class VXMLDocument extends WidgetDocument implements VXMLConstants
{
	/** The version of VXML this document conforms to. */
	private String version = VERSION_2_1;
	/** The application URI of this document. */
	private String applicationURI = null;
	/** The properties of this document. */
	private final PropertiesSupport properties = new PropertiesSupport();
	/** The list of variables in this document's scope. */
	private final List<Variable> variables = new LinkedList<Variable>();
	/** The list of script blocks in this document. */
	private final List<Script> scripts = new LinkedList<Script>();
	/** The list of dialogs in this document. */
	private final List<Dialog> dialogs = new LinkedList<Dialog>();
	/** The list of event handlers for this document. */
	private final List<EventHandler> eventHandlers = new LinkedList<EventHandler>();

	/**
	 * Creates a new VXMLDocument object.
	 */
	public VXMLDocument()
	{
	}

	/**
	 * Creates a new VXMLDocument object.
	 * 
	 * @param version The version of VXML this document conforms to.
	 * @throws NullPointerException If the version string is <code>null</code>.
	 */
	public VXMLDocument(String version) throws NullPointerException
	{
		setVersion(version);
	}
	
	/**
	 * Returns the version of VXML this document conforms to.
	 * 
	 * @return The version of VXML this document conforms to.
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Returns the application URI of this document or <code>null</code> if no
	 * URI is specified.
	 * 
	 * @return The application URI of this document or <code>null</code> if no
	 *         URI is specified.
	 */
	public String getApplicationURI()
	{
		return applicationURI;
	}

	/**
	 * Returns the names of the properties of this document.
	 * 
	 * @return The names of the properties of this document.
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
	 * Returns the list of variables in this document's scope.
	 * 
	 * @return The list of variables in this document's scope.
	 */
	public Variable[] getVariables()
	{
		return variables.toArray(new Variable[variables.size()]);
	}

	/**
	 * Returns the list of script blocks in this document.
	 * 
	 * @return The list of script blocks in this document.
	 */
	public Script[] getScripts()
	{
		return scripts.toArray(new Script[scripts.size()]);
	}

	/**
	 * Returns the list of dialogs in this document.
	 * 
	 * @return The list of dialogs in this document.
	 */
	public Dialog[] getDialogs()
	{
		return dialogs.toArray(new Dialog[dialogs.size()]);
	}

	/**
	 * Returns the list of event handlers for this document.
	 * 
	 * @return The list of event handlers for this document.
	 */
	public EventHandler[] getEventHandlers()
	{
		return eventHandlers.toArray(new EventHandler[eventHandlers.size()]);
	}

	/**
	 * Sets the version of VXML this document conforms to.
	 * 
	 * @param version The version of VXML this document conforms to.
	 * @throws NullPointerException If the version string is <code>null</code>.
	 */
	public void setVersion(String version)
	{
		if (version == null)
			throw new NullPointerException("version"); //$NON-NLS-1$
		this.version = version;
	}

	/**
	 * Sets the application URI of this document.
	 * 
	 * @param applicationURI The application URI of this document.
	 */
	public void setApplicationURI(String applicationURI)
	{
		this.applicationURI = applicationURI;
	}

	/**
	 * Sets the value of a property in this document.
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
	 * Clears the value of a property in this document.
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
	 * Adds a variable to this document.
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
	 * Removes a variable from this document.
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
	 * Adds a script block to this document.
	 * 
	 * @param script The script block to add.
	 * @throws NullPointerException If the supplied script block is
	 *           <code>null</code>.
	 */
	public void addScript(Script script) throws NullPointerException
	{
		if (script == null)
			throw new NullPointerException("script"); //$NON-NLS-1$
		scripts.add(script);
	}

	/**
	 * Removes a script block from this document.
	 * 
	 * @param script The script block to remove.
	 * @throws NullPointerException If the supplied script block is
	 *           <code>null</code>.
	 */
	public void removeScript(Script script) throws NullPointerException
	{
		if (script == null)
			throw new NullPointerException("script"); //$NON-NLS-1$
		scripts.remove(script);
	}

	/**
	 * Adds a dialog to this document.
	 * 
	 * @param dialog The dialog to add.
	 * @throws NullPointerException If the supplied dialog is <code>null</code>.
	 */
	public void addDialog(Dialog dialog) throws NullPointerException
	{
		if (dialog == null)
			throw new NullPointerException("dialog"); //$NON-NLS-1$
		dialogs.add(dialog);
	}

	/**
	 * Removes a dialog from this document.
	 * 
	 * @param dialog The dialog to remove.
	 * @throws NullPointerException If the supplied dialog is <code>null</code>.
	 */
	public void removeDialog(Dialog dialog) throws NullPointerException
	{
		if (dialog == null)
			throw new NullPointerException("dialog"); //$NON-NLS-1$
		dialogs.remove(dialog);
	}

	/**
	 * Adds an event handler to this document.
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
	 * Removes an event handler from this document.
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
	 * @see org.eclipse.vtp.framework.spi.core.output.IOutputDocument#
	 *      getDocumentType()
	 */
	public String getDocumentType()
	{
		return NAMESPACE_URI_VXML;
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
		// Start the document.
		outputHandler.startDocument();
		// Start the VXML name space mapping.
		outputHandler.startPrefixMapping(EMPTY, NAMESPACE_URI_VXML);
		// Start the root element.
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		outputHandler
				.startElement(NAMESPACE_URI_VXML, NAME_VXML, NAME_VXML, attributes);
		// Write the properties.
		writeProperties(outputHandler);
		// Write the child elements.
		writeVariables(outputHandler);
		writeScripts(outputHandler);
		writeDialogs(outputHandler);
		writeEventHandlers(outputHandler);
		// End the root element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_VXML, NAME_VXML);
		// End the VXML name space mapping.
		outputHandler.endPrefixMapping(EMPTY);
		// End the document.
		outputHandler.endDocument();
	}

	/**
	 * Write the attribute members of this variable to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		writeAttribute(attributes, null, null, NAME_VERSION, TYPE_CDATA, version);
		if (applicationURI != null)
			writeAttribute(attributes, null, null, NAME_APPLICATION, TYPE_CDATA,
					applicationURI);
	}

	/**
	 * Writes the properties of this document to the specified content handler.
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
	 * Write the variables of this document to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the event handlers fails.
	 */
	protected void writeVariables(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, variables);
	}

	/**
	 * Write the scripts of this document to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the event handlers fails.
	 */
	protected void writeScripts(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, scripts);
	}

	/**
	 * Write the dialogs of this document to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the event handlers fails.
	 */
	protected void writeDialogs(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, dialogs);
	}

	/**
	 * Write the event handlers of this document to the specified content handler.
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
