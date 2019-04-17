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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.framework.interactions.core.media.FileInputGrammar;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;

public abstract class GrammarFileCreatorPanel extends InputGrammarCreatorPanel
		implements SelectionListener {
	Text text = null;

	public GrammarFileCreatorPanel() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.InputGrammarCreatorPanel#createGrammar()
	 */
	public InputGrammar createGrammar()
	{
		if(text.getText().equals(""))
			return null;
		FileInputGrammar content = createNewGrammar();
		content.setStaticPath(text.getText());
		return content;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.InputGrammarCreatorPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		text = new Text(comp, SWT.BORDER | SWT.FLAT | SWT.SINGLE);
		text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
				false));
//		text.setEditable(false);
		Button browseButton = new Button(comp, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				false, false));
		browseButton.addSelectionListener(this);
		setControl(comp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.InputGrammarCreatorPanel#setInitialInput(org.eclipse.vtp.framework.interactions.core.media.InputGrammar)
	 */
	public void setInitialInput(InputGrammar content) {
		if (content instanceof FileInputGrammar) {
			text.setText(((FileInputGrammar) content).getPath());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		ResourceChooserDialog dialog = new ResourceChooserDialog(text
				.getShell(), getMediaProvider().getResourceManager(), text
				.getText());
		if (dialog.open() != ResourceChooserDialog.OK)
			return;
		text.setText(dialog.getValue());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	/**
	 * @return
	 */
	protected abstract FileInputGrammar createNewGrammar();

}
