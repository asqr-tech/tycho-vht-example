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
import org.eclipse.vtp.framework.interactions.voice.media.BuiltInDigitsInputGrammar;

public class BuiltInDigitsGrammarCreatorPanel extends InputGrammarCreatorPanel
{
	BuiltInDigitsInputGrammar grammar = new BuiltInDigitsInputGrammar();
	private Text minDigitsText = null;
	private Text maxDigitsText = null;

	public BuiltInDigitsGrammarCreatorPanel()
	{
		super();
		}

	public InputGrammar createGrammar()
	{
		grammar.setMinDigits(minDigitsText.getText());
		grammar.setMaxDigits(maxDigitsText.getText());
		return grammar;
	}

	public void createControls(Composite parent)
    {
	    Composite comp = new Composite(parent, SWT.NONE);
	    comp.setBackground(parent.getBackground());
	    comp.setLayout(new GridLayout(2, false));
	    
	    Label yesKeyLabel = new Label(comp, SWT.NONE);
	    yesKeyLabel.setBackground(comp.getBackground());
	    yesKeyLabel.setText("Minimum Digits Allowed");
	    yesKeyLabel.setLayoutData(new GridData());
	    minDigitsText = new Text(comp, SWT.BORDER | SWT.SINGLE);
	    minDigitsText.setText(grammar.getMinDigits());
	    minDigitsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    Label noKeyLabel = new Label(comp, SWT.NONE);
	    noKeyLabel.setBackground(comp.getBackground());
	    noKeyLabel.setText("Maximum Digits Allowed");
	    noKeyLabel.setLayoutData(new GridData());
	    maxDigitsText = new Text(comp, SWT.BORDER | SWT.SINGLE);
	    maxDigitsText.setText(grammar.getMaxDigits());
	    maxDigitsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    this.setControl(comp);
    }

	public void setInitialInput(InputGrammar content)
    {
		if(content instanceof BuiltInDigitsInputGrammar)
			grammar = (BuiltInDigitsInputGrammar)content;
		if(minDigitsText != null)
		{
		    minDigitsText.setText(grammar.getMinDigits());
		    maxDigitsText.setText(grammar.getMaxDigits());
		}
    }

}
