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

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Return</code> class represents the &lt;return&gt; VXML element.
 * The return element ends execution of a subdialog and returns control and data
 * to a calling dialog. The data can either be in the form of an event that is
 * thrown upon returning or a list of variables in the scope of the subdialog to
 * add to the scope of the calling dialog.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Return extends Action
{
	/** The name of the event to throw. */
	private String eventName = null;
	/** The list of variables to return. */
	private final LinkedList<String> names = new LinkedList<String>();

	/**
	 * Creates a new Return object that throws the specified event upon returning
	 * to the caller dialog.
	 * 
	 * @param eventName Name of the event to throw.
	 * @throws IllegalArgumentException If the supplied event name is empty.
	 */
	public Return(String eventName)
	{
		setEventName(eventName);
	}

	/**
	 * Creates a new Return object that copies the named variables from the
	 * subdialog's scope into the scope of the calling dialog.
	 * 
	 * @param names List of variables to copy.
	 * @throws IllegalArgumentException If any of the supplied names are empty.
	 * @throws NullPointerException If any of the supplied names are
	 *           <code>null</code>.
	 */
	public Return(String[] names) throws IllegalArgumentException,
			NullPointerException
	{
		if (names != null)
			for (int i = 0; i < names.length; ++i)
				addName(names[i]);
	}

	/**
	 * Returns the name of the event this return object will throw.
	 * 
	 * @return the name of the event.
	 */
	public String getEventName()
	{
		return eventName;
	}

	/**
	 * Returns the list of variable names that will be copied into the calling
	 * dialog's scope.
	 * 
	 * @return the list of variable names.
	 */
	public String[] getNames()
	{
		return names.toArray(new String[names.size()]);
	}

	/**
	 * Sets the name of the event this return object will throw.
	 * 
	 * @param eventName The name of the event this return object will throw.
	 * @throws IllegalArgumentException If the supplied event name is empty.
	 */
	public void setEventName(String eventName) throws IllegalArgumentException
	{
		if (eventName != null && eventName.length() == 0)
			throw new IllegalArgumentException("eventName"); //$NON-NLS-1$
		this.eventName = eventName;
	}

	/**
	 * Adds the variable name to the list of variable names that will be copied
	 * into the calling dialog's scope.
	 * 
	 * @param name The variable name to add.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
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
	 * Removes the variable name from the list of variable names that will be
	 * copied into the calling dialog's scope.
	 * 
	 * @param name The variable name to remove.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 */
	public void removeName(String name) throws IllegalArgumentException,
			NullPointerException
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		names.remove(name);
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
		// Start and end the element.
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_RETURN, NAME_RETURN,
				attributes);
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_RETURN, NAME_RETURN);
	}

	/**
	 * Write the attribute members of this action to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		if (names.size() > 0)
		{
			StringBuffer buffer = new StringBuffer();
			for (Iterator<String> i = names.iterator(); i.hasNext();)
			{
				buffer.append(i.next());
				if (i.hasNext())
					buffer.append(' ');
			}
			writeAttribute(attributes, null, null, NAME_NAMELIST, TYPE_CDATA, buffer
					.toString());
		}
		else if (eventName != null)
			writeAttribute(attributes, null, null, NAME_EVENTEXPR, TYPE_CDATA,
					eventName);
	}
}
