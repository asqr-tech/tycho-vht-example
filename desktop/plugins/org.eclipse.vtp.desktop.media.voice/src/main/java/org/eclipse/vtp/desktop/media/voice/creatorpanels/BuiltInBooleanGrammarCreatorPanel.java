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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.vtp.desktop.media.core.InputGrammarCreatorPanel;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;
import org.eclipse.vtp.framework.interactions.voice.media.BuiltInBooleanInputGrammar;

public class BuiltInBooleanGrammarCreatorPanel extends InputGrammarCreatorPanel
{
	BuiltInBooleanInputGrammar grammar = new BuiltInBooleanInputGrammar();
	private Combo yesKeyCombo = null;
	private Combo noKeyCombo = null;

	public BuiltInBooleanGrammarCreatorPanel()
	{
		super();
	}

	public InputGrammar createGrammar()
	{
		grammar.setYesDigit(yesKeyCombo.getItem(yesKeyCombo.getSelectionIndex()));
		grammar.setNoDigit(noKeyCombo.getItem(noKeyCombo.getSelectionIndex()));
		return grammar;
	}

	public void createControls(Composite parent)
    {
	    Composite comp = new Composite(parent, SWT.NONE);
	    comp.setBackground(parent.getBackground());
	    comp.setLayout(new GridLayout(2, false));
	    
	    Label yesKeyLabel = new Label(comp, SWT.NONE);
	    yesKeyLabel.setBackground(comp.getBackground());
	    yesKeyLabel.setText("Key to signify YES");
	    yesKeyLabel.setLayoutData(new GridData());
	    yesKeyCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SINGLE);
	    yesKeyCombo.add("1");
	    yesKeyCombo.add("2");
	    yesKeyCombo.add("3");
	    yesKeyCombo.add("4");
	    yesKeyCombo.add("5");
	    yesKeyCombo.add("6");
	    yesKeyCombo.add("7");
	    yesKeyCombo.add("8");
	    yesKeyCombo.add("9");
	    yesKeyCombo.add("0");
	    yesKeyCombo.add("*");
	    yesKeyCombo.add("#");
	    if(grammar.getYesDigit().equals("*"))
	    	yesKeyCombo.select(10);
	    else if(grammar.getYesDigit().equals("#"))
	    	yesKeyCombo.select(11);
	    else if(grammar.getYesDigit().equals("0"))
	    	yesKeyCombo.select(9);
	    else
	    	yesKeyCombo.select(Integer.parseInt(grammar.getYesDigit()) - 1);
	    yesKeyCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    Label noKeyLabel = new Label(comp, SWT.NONE);
	    noKeyLabel.setBackground(comp.getBackground());
	    noKeyLabel.setText("Key to signify NO");
	    noKeyLabel.setLayoutData(new GridData());
	    noKeyCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SINGLE);
	    noKeyCombo.add("1");
	    noKeyCombo.add("2");
	    noKeyCombo.add("3");
	    noKeyCombo.add("4");
	    noKeyCombo.add("5");
	    noKeyCombo.add("6");
	    noKeyCombo.add("7");
	    noKeyCombo.add("8");
	    noKeyCombo.add("9");
	    noKeyCombo.add("0");
	    noKeyCombo.add("*");
	    noKeyCombo.add("#");
	    if(grammar.getNoDigit().equals("*"))
	    	noKeyCombo.select(10);
	    else if(grammar.getNoDigit().equals("#"))
	    	noKeyCombo.select(11);
	    else if(grammar.getNoDigit().equals("0"))
	    	noKeyCombo.select(9);
	    else
	    	noKeyCombo.select(Integer.parseInt(grammar.getNoDigit()) - 1);
	    noKeyCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    this.setControl(comp);
    }

	public void setInitialInput(InputGrammar content)
    {
		if(content instanceof BuiltInBooleanInputGrammar)
			grammar = (BuiltInBooleanInputGrammar)content;
		if(yesKeyCombo != null)
		{
		    if(grammar.getYesDigit().equals("*"))
		    	yesKeyCombo.select(10);
		    else if(grammar.getYesDigit().equals("#"))
		    	yesKeyCombo.select(11);
		    else if(grammar.getYesDigit().equals("0"))
		    	yesKeyCombo.select(9);
		    else
		    	yesKeyCombo.select(Integer.parseInt(grammar.getYesDigit()) - 1);

		    if(grammar.getNoDigit().equals("*"))
		    	noKeyCombo.select(10);
		    else if(grammar.getNoDigit().equals("#"))
		    	noKeyCombo.select(11);
		    else if(grammar.getNoDigit().equals("0"))
		    	noKeyCombo.select(9);
		    else
		    	noKeyCombo.select(Integer.parseInt(grammar.getNoDigit()) - 1);
		}
   }

}
