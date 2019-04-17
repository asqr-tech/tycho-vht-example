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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog for editing a prompt script.
 * 
 * @author Lonnie Pryor
 */
public final class ScriptDialog extends Dialog {
	/** The script data. */
	private String script = "";
	/** The area the script is displayed in. */
	private Text scriptText = null;

	/**
	 * Creates a new <code>ContentDialog</code>.
	 * 
	 * @param parentShell
	 *            The shell to create the dialog from.
	 */
	public ScriptDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Creates a new <code>ContentDialog</code>.
	 * 
	 * @param parentShellProvider
	 *            The shell provider to create the dialog from.
	 */
	public ScriptDialog(IShellProvider parentShellProvider) {
		super(parentShellProvider);
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script == null ? "" : script;
		contentChanged();
	}

	/**
	 * Called when the content of the dialog has changed.
	 */
	protected void contentChanged() {
		if (scriptText != null)
			script = scriptText.getText();
		Button ok = getButton(OK);
		if (ok != null)
			ok.setEnabled(isContentValid());
	}

	/**
	 * Returns true if the content of this dialog is valid and the OK button
	 * should be enabled.
	 * 
	 * @return True if the content of this dialog is valid and the OK button
	 *         should be enabled.
	 */
	protected boolean isContentValid() {
		return true;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets
	 * .Composite)
	 */
	protected Control createContents(Composite parent) {
		Control createdContents = super.createContents(parent);
		contentChanged();
		return createdContents;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(300, 200);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(
	 * org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText("Script");
		scriptText = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		scriptText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (script != null)
			scriptText.setText(script);
		scriptText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				contentChanged();
			}
		});
		return scriptText;
	}

}