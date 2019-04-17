/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 * (Templated from
 *  org.eclipse.ui.internal.navigator.resources.actions.EditActionGroup)
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.legacy.v3_xTo4_0.view;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionGroup;

/**
 * @since 3.2
 * 
 */
public class ConvertActionGroup extends ActionGroup
{
	private ConvertWorkflowProjectAction convertAction;

	/**
	 * 
	 * @param aShell
	 */
	public ConvertActionGroup()
	{
		makeActions();
	}

	public void fillContextMenu(IMenuManager menu)
	{
		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		convertAction.selectionChanged(selection);
		menu.add(convertAction);
//		menu.appendToGroup("audio.group", playFileAction);
	}

	public void fillActionBars(IActionBars actionBars) {
	}

	/**
	 * Handles a key pressed event by invoking the appropriate action.
	 * 
	 * @param event
	 *            The Key Event
	 */
	public void handleKeyPressed(KeyEvent event) {
	}

	protected void makeActions()
	{
		convertAction = new ConvertWorkflowProjectAction();
	}

	public void updateActionBars() {
	}
}
