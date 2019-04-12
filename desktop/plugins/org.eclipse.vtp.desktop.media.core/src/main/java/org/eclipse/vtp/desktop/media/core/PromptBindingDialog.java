/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.media.core;

import java.util.List;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PromptBindingItem;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PromptBindingNode;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;

public class PromptBindingDialog extends ContentTreeDialog {
	PromptBindingItem promptBinding;
	List<Variable> variables;

	/**
	 * @param parentShell
	 */
	public PromptBindingDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * @param parentShell
	 */
	public PromptBindingDialog(IShellProvider parentShell) {
		super(parentShell);
	}

	/**
	 * @param promptBinding
	 * @param mediaProvider
	 */
	public void setPromptBinding(PromptBindingItem promptBinding,
			IMediaProvider mediaProvider) {
		this.promptBinding = promptBinding;
		setTreeContent((PromptBindingItem) promptBinding.clone());
		setMediaProvider(mediaProvider);
	}

	/**
	 * @param variables
	 */
	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.media.core.ContentDialog#getVariables()
	 */
	protected List<Variable> getVariables() {
		return variables;
	}

	/**
	 * @return
	 */
	public PromptBindingItem getPromptBindingItem() {
		return promptBinding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		promptBinding.clearEntries();
		for (PromptBindingNode node : getTreeContent().getEntries())
			promptBinding.addEntry((PromptBindingNode) node.clone());
		super.okPressed();
	}
}
