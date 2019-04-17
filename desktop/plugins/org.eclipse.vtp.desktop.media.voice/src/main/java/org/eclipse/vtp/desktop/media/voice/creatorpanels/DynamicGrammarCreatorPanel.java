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
package org.eclipse.vtp.desktop.media.voice.creatorpanels;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.media.core.InputGrammarCreatorPanel;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;
import org.eclipse.vtp.framework.interactions.voice.media.DynamicInputGrammar;

public class DynamicGrammarCreatorPanel extends InputGrammarCreatorPanel
{
	private DynamicInputGrammar grammar = new DynamicInputGrammar();
	private Text scriptText = null;

	public DynamicGrammarCreatorPanel()
	{
		super();
	}

	@Override
	public void setInitialInput(InputGrammar content)
	{
		if(content instanceof DynamicInputGrammar)
			grammar = (DynamicInputGrammar)content;
		if(scriptText != null)
			scriptText.setText(grammar.getScript());
	}

	@Override
	public void createControls(Composite parent)
	{
	    Composite comp = new Composite(parent, SWT.NONE);
	    comp.setBackground(parent.getBackground());
	    comp.setLayout(new GridLayout(1, false));
	    
	    Label label = new Label(comp, SWT.NONE);
	    label.setText("Javascript to create Grammar contents");
	    label.setLayoutData(new GridData());
	    
		scriptText = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 400;
		gridData.widthHint = 300;
		scriptText.setLayoutData(gridData);
		scriptText.setText(grammar.getScript());
		
		this.setControl(comp);
	}

	@Override
	public InputGrammar createGrammar()
	{
		grammar.setScript(scriptText.getText());
		return grammar;
	}

}
