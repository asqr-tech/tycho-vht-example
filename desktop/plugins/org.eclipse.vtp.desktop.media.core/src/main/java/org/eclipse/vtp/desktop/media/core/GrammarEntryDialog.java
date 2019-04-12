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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.vtp.desktop.model.interactive.core.input.InputType;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;

/**
 * A dialog for editing an individual content object.
 * 
 * @author Lonnie Pryor
 */
public class GrammarEntryDialog extends Dialog {
	/** The media provider the list is bound to. */
	private IMediaProvider mediaProvider = null;
	InputGrammar content;
	Combo typeCombo = null;
	InputGrammarCreatorPanel ecp = null;
	StackLayout stackLayout = new StackLayout();
	List<InputGrammarCreatorPanelManager.ContentCreatorRecord> typeRecords
		= new ArrayList<InputGrammarCreatorPanelManager.ContentCreatorRecord>();
	List<InputGrammarCreatorPanel> panels
		= new ArrayList<InputGrammarCreatorPanel>();

	/**
	 * Creates a new <code>ContentEntryDialog</code>.
	 * 
	 * @param parentShell
	 *            The shell to create the dialog from.
	 */
	public GrammarEntryDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Creates a new <code>ContentEntryDialog</code>.
	 * 
	 * @param parentShellProvider
	 *            The shell provider to create the dialog from.
	 */
	public GrammarEntryDialog(IShellProvider parentShellProvider) {
		super(parentShellProvider);
	}

	/**
	 * Sets the media provider the item is bound to.
	 * 
	 * @param mediaProvider
	 *            The media provider the item is bound to.
	 */
	public void setMediaProvider(IMediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(
	 *      org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		this.getShell().setText("Grammar Entry");
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		typeCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeCombo.add("Not Configured");
		final Composite stackComp = new Composite(comp, SWT.NONE);
		stackComp.setBackground(comp.getBackground());
		stackComp.setLayout(stackLayout);
		stackComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ecp = new EmptyInputGrammarCreatorPanel();
			ecp.createControls(stackComp);
			ecp.setInitialInput(content);
			ecp.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		List<InputGrammarCreatorPanelManager.ContentCreatorRecord> types
			= InputGrammarCreatorPanelManager.getInstance().getInputTypes();
		for(InputGrammarCreatorPanelManager.ContentCreatorRecord typeRecord : types)
		{
			typeRecords.add(typeRecord);
			typeCombo.add(typeRecord.contentName);
			InputGrammarCreatorPanel ccp = InputGrammarCreatorPanelManager.getInstance().getCreatorPanel(new InputType(typeRecord.contentType, ""));
			ccp.setMediaProvider(mediaProvider);
			ccp.createControls(stackComp);
			ccp.setInitialInput(content);
			ccp.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
			panels.add(ccp);
		}

		typeCombo.addSelectionListener(new SelectionListener()
		{
			
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
			
			public void widgetSelected(SelectionEvent e)
			{
				if(typeCombo.getSelectionIndex() == 0)
				{
					stackLayout.topControl = ecp.getControl();
				}
				else
				{
	            	stackLayout.topControl = panels.get(typeCombo.getSelectionIndex() - 1).getControl();
				}
				stackComp.layout();
			}
			
		});
		
		if(content == null)
		{
			typeCombo.select(0);
			stackLayout.topControl = ecp.getControl();
		}
		else
		{
			for(int i = 0; i < typeRecords.size(); i++)
            {
	            InputGrammarCreatorPanelManager.ContentCreatorRecord record = typeRecords.get(i);
	            if(content.getInputGrammarType().equals(record.contentType))
	            {
	            	typeCombo.select(i + 1);
	            	stackLayout.topControl = panels.get(i).getControl();
	            }
            }
		}
		stackComp.layout();
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		if(typeCombo.getSelectionIndex() == 0)
		{
			content = ecp.createGrammar();
		}
		else
		{
           	content = panels.get(typeCombo.getSelectionIndex() - 1).createGrammar();
		}
		super.okPressed();
	}

	/**
	 * @return
	 */
	public InputGrammar getContent() {
		return content;
	}

	/**
	 * @param content
	 */
	public void setContent(InputGrammar content) {
		this.content = content;
	}
}
