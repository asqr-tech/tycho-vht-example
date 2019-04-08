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

/**
 * The <code>ActionSet</code> class bundles multiple <code>Action</code>
 * instances so they may be added to a container en mass instead of
 * individually.
 * 
 * The <code>Action</code> instances contained by an instance of this class
 * are output in the order in which they were added.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class ActionSet extends Action
{
	/** The list of event handlers for this dialog. */
	private final LinkedList<Action> actions = new LinkedList<Action>();

	/**
	 * Constructs a new <code>ActionSet</code> instance that is initially empty.
	 */
	public ActionSet()
	{
	}

	/**
	 * Returns the list of actions in this set.
	 * 
	 * @return The list of actions in this set.
	 */
	public Action[] getActions()
	{
		return actions.toArray(new Action[actions.size()]);
	}

	/**
	 * Adds the action to this set. The list of actions are written to the VXML
	 * document in the order they were added.
	 * 
	 * @param action The action to add.
	 * @throws NullPointerException If the supplied action is <code>null</code>.
	 */
	public void addAction(Action action) throws NullPointerException
	{
		if (action == null)
			throw new NullPointerException("action"); //$NON-NLS-1$
		actions.add(action);
	}

	/**
	 * Removes the action from this set.
	 * 
	 * @param action The action to remove.
	 * @throws NullPointerException If the supplied action is <code>null</code>.
	 */
	public void removeAction(Action action) throws NullPointerException
	{
		if (action == null)
			throw new NullPointerException("action"); //$NON-NLS-1$
		actions.remove(action);
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
		writeActions(outputHandler);
	}

	/**
	 * Write the actions in this set to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the actions fails.
	 */
	protected void writeActions(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, actions);
	}
}
