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
package org.eclipse.vtp.modules.interactive.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.model.interactive.core.internal.MenuChoice;

public class MenuOptionScriptDialog extends Dialog
{
	/** The MenuChoice to which this script will apply */
	MenuChoice menuChoice = null;
	/**	The text field that contains the script and serves as the editor */
	Text scriptText = null;

	/**
	 * Creates a new dialog on which to display the guard condition script
	 * @param parentShell
	 */
	public MenuOptionScriptDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/**
	 * Creates a new dialog on which to display the guard condition script
	 * @param parentShell
	 */
	public MenuOptionScriptDialog(IShellProvider parentShell)
	{
		super(parentShell);
	}

	/**
	 * Sets the MenuChoice to which this script will apply
	 * @param menuChoice - the MenuChoice to which this script will apply
	 */
	public void setMenuChoice(MenuChoice menuChoice)
	{
		this.menuChoice = menuChoice;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
    {
		this.getShell().setText("Guard Condition (" + menuChoice.getOptionName() + ")");
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		Label descLabel = new Label(comp, SWT.WRAP);
		descLabel.setText("The Guard condition entered below must evaluate to TRUE for this option to be available in the menu when presented to the user.");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.widthHint = 300;
		descLabel.setLayoutData(gd);
		Label scriptLabel = new Label(comp, SWT.NONE);
		scriptLabel.setText("Expression");
		scriptLabel.setLayoutData(new GridData());
		scriptText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		scriptText.setText(menuChoice.getScriptText() == null ? "" : menuChoice.getScriptText());
		scriptText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return comp;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
    {
		menuChoice.setScriptText(scriptText.getText());
	    super.okPressed();
    }
	
	
}
