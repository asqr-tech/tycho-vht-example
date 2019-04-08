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
 * The <code>Dialog</code> class is the base class for the family of VXML
 * elements that collect data from the caller, allow the caller to select from a
 * set of menu options, or manipulate the flow of the call during processing.
 * <br>
 * <br>
 * Certain VXML compliant platforms allow the use of proprietary properties to
 * be applied to the dialogs of a VXML document. These properties are written to
 * the VXML document in the order they are added to the <code>Dialog</code>
 * instance.<br>
 * <br>
 * During processing of a dialog's constituent elements, events and error can be
 * generated. <code>EventHandler</code>s can be added to the dialog instance
 * that will handle events and errors not dealt with by the child's event
 * handlers. The handlers will be written to the VXML document in the order they
 * were added.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public abstract class Dialog extends Widget implements VXMLConstants
{
	/** The identifier of this dialog instance. */
	private String id;
	/** The scope of any grammars defined within the dialog. */
	private String scope = SCOPE_DOCUMENT;
	/** The properties of this dialog. */
	private final PropertiesSupport properties = new PropertiesSupport();
	/** The list of event handlers for this dialog. */
	private final LinkedList<EventHandler> eventHandlers = new LinkedList<EventHandler>();

	/**
	 * Creates a new instance of <code>Dialog</code> with the specified id and a
	 * grammar scope of Dialog.SCOPE_DIALOG. Throws an IllegalArgumentException if
	 * id argument is <code>null</code> or is an empty string.
	 * 
	 * @param id The identifier of this dialog instance.
	 * @throws IllegalArgumentException If the supplied ID is empty.
	 * @throws NullPointerException If the supplied ID is <code>null</code>.
	 */
	protected Dialog(String id) throws IllegalArgumentException,
			NullPointerException
	{
		setID(id);
	}

	/**
	 * Creates a new instance of <code>Dialog</code> with the specified id and
	 * the grammar scope of <code>scope</code>. Throws an
	 * IllegalArgumentException if id argument is <code>null</code> or is an
	 * empty string. Throws IllegalArgumentException if scope argument is not
	 * Dialog.SCOPE_DIALOG or Dialog.SCOPE_DOCUMENT.
	 * 
	 * @param id The identifier of this dialog instance
	 * @param scope The scope of any grammars defined within the dialog.
	 * @throws IllegalArgumentException If the supplied ID is empty.
	 * @throws IllegalArgumentException If the supplied scope is invalid.
	 * @throws NullPointerException If the supplied ID is <code>null</code>.
	 * @throws NullPointerException If the supplied scope is <code>null</code>.
	 */
	protected Dialog(String id, String scope) throws IllegalArgumentException,
			NullPointerException
	{
		setID(id);
		setScope(scope);
	}

	/**
	 * Returns the identifier for this dialog instance.
	 * 
	 * @return The identifier for this dialog instance.
	 */
	public String getID()
	{
		return id;
	}

	/**
	 * Returns the grammar scope of this dialog's grammars.
	 * 
	 * @return The dialog's grammar scope.
	 */
	public String getScope()
	{
		return scope;
	}

	/**
	 * Returns the names of the properties of this dialog.
	 * 
	 * @return The names of the properties of this dialog.
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
	 * Returns the list of event handlers for this dialog.
	 * 
	 * @return The list of event handlers for this dialog.
	 */
	public EventHandler[] getEventHandlers()
	{
		return eventHandlers.toArray(new EventHandler[eventHandlers
				.size()]);
	}

	/**
	 * Sets the identifier of this dialog instance.
	 * 
	 * @param id The new identifier of this dialog instance
	 * @throws IllegalArgumentException If the supplied ID is empty.
	 * @throws NullPointerException If the supplied ID is <code>null</code>.
	 */
	public void setID(String id) throws IllegalArgumentException,
			NullPointerException
	{
		if (id == null)
			throw new NullPointerException("id"); //$NON-NLS-1$
		if (id.length() == 0)
			throw new IllegalArgumentException("id"); //$NON-NLS-1$
		this.id = id;
	}

	/**
	 * Sets the grammar scope of this dialog instance. Throws
	 * IllegalArgumentException if scope is not Dialog.SCOPE_DIALOG or
	 * Dialog.SCOPE_DIALOG.
	 * 
	 * @param scope The new grammar scope for this dialog instance.
	 * @throws IllegalArgumentException If the supplied scope is invalid.
	 * @throws NullPointerException If the supplied scope is <code>null</code>.
	 */
	public void setScope(String scope) throws IllegalArgumentException,
			NullPointerException
	{
		if (scope == null)
			throw new NullPointerException("scope"); //$NON-NLS-1$
		else if (scope.equalsIgnoreCase(SCOPE_DOCUMENT))
			this.scope = SCOPE_DOCUMENT;
		else if (scope.equalsIgnoreCase(SCOPE_DIALOG))
			this.scope = SCOPE_DIALOG;
		else
			throw new IllegalArgumentException("scope==\"" //$NON-NLS-1$
					+ scope + "\""); //$NON-NLS-1$
	}

	/**
	 * Sets the value of a property in this dialog.
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
	 * Clears the value of a property in this dialog.
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
	 * Adds an EventHandler to this dialog instance. The event handlers will be
	 * written to the VXML document in the order they are added.
	 * 
	 * @param eventHandler The event handler to add to this dialog.
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
	 * Removes an event handler from this dialog instance.
	 * 
	 * @param eventHandler The event handler to remove from this dialog.
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

	/**
	 * Write the attribute members of this dialog to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		writeAttribute(attributes, null, null, NAME_ID, TYPE_CDATA, id);
		writeAttribute(attributes, null, null, NAME_SCOPE, NAME_SCOPE, scope);
	}

	/**
	 * Writes the properties of this dialog to the specified content handler.
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
	 * Write the event handlers of this dialog to the specified content handler.
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
