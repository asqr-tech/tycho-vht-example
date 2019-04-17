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
package org.eclipse.vtp.desktop.projects.interactive.core.view;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.ICommonMenuConstants;

/**
 * @since 3.2
 * 
 */
public class ReplicationActionGroup extends ActionGroup
{
	private Shell shell;
	private ReplicateLanguageAction replicateAction;

	/**
	 * 
	 * @param aShell
	 */
	public ReplicationActionGroup(Shell aShell)
	{
		shell = aShell;
		makeActions();
	}

	public void fillContextMenu(IMenuManager menu)
	{
		replicateAction.selectionChanged((IStructuredSelection)getContext().getSelection());
		menu.appendToGroup(ICommonMenuConstants.GROUP_SOURCE, replicateAction);
	}

	protected void makeActions() {
		replicateAction = new ReplicateLanguageAction(shell);
		replicateAction.setActionDefinitionId(ReplicateLanguageAction.ID);

	}
}
