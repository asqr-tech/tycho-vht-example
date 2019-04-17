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
package org.eclipse.vtp.modules.standard.ui.properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ReceiverNameDialog extends Dialog
{
	String name = null;
	Text nameText = null;
	Button okButton = null;

	/**
	 * @param parentShell
	 */
	public ReceiverNameDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/**
	 * @param parentShell
	 */
	public ReceiverNameDialog(IShellProvider parentShell)
	{
		super(parentShell);
	}

	protected Control createContents(Composite parent) {
		// create the top level composite for the dialog
		Composite composite = new Composite(parent, 0);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		// initialize the dialog units
		initializeDialogUnits(composite);
		
		// create the dialog area and button bar
		buttonBar = createButtonBar(composite);
		dialogArea = createDialogArea(composite);
		
		buttonBar.moveBelow(null);
		return composite;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		Label nameLabel = new Label(parent, SWT.NONE);
		nameLabel.setText("Name");
		nameLabel.setLayoutData(new GridData());
		nameText = new Text(parent, SWT.BORDER | SWT.FLAT | SWT.SINGLE);
		if(name != null)
			nameText.setText(name);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		nameText.setLayoutData(gd);
		nameText.addVerifyListener(new VerifyListener()
		{
			public void verifyText(VerifyEvent e)
			{
				String currentName = nameText.getText().substring(0, e.start) + e.text + nameText.getText(e.end, (nameText.getText().length() - 1));
				okButton.setEnabled(!currentName.equals(""));
			}
		});

		okButton = getButton(IDialogConstants.OK_ID);
		okButton.setEnabled(!nameText.getText().equals(""));
		return parent;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
	{
		name = nameText.getText();
		super.okPressed();
	}

	/**
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
		if(nameText != null)
			nameText.setText(name);
	}
}
