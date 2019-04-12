/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.vtp.desktop.model.legacy.v3_xTo4_0.view;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

/**
 * @since 3.2
 *
 */
public class ConvertActionProvider extends CommonActionProvider
{
	 
	private ConvertActionGroup audioGroup;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator.ICommonActionExtensionSite)
	 */
	public void init(ICommonActionExtensionSite anActionSite) {
		audioGroup = new ConvertActionGroup();
 
	}

	public void dispose() { 
		audioGroup.dispose();
	}

	public void fillActionBars(IActionBars actionBars) { 
		audioGroup.fillActionBars(actionBars);
	}

	public void fillContextMenu(IMenuManager menu) { 
		audioGroup.fillContextMenu(menu);
	}

	public void setContext(ActionContext context) { 
		audioGroup.setContext(context);
	}

	public void updateActionBars() { 
		audioGroup.updateActionBars();
	}
}
