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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.ObjectField;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.FormattableContent;

/**
 * DynamicContentCreatorPanel.
 * 
 * @author Lonnie Pryor
 */
public abstract class DynamicContentCreatorPanel extends ContentCreatorPanel
{
	/** The list of variables that are available. */
	private List<Variable> variables = null;
	/** True if the dynamic value is currently selected. */
	private boolean dynamicSelected = false;
	private Button staticButton = null;
	private Button dynamicButton = null;
	private Combo options = null;
	Combo formatCombo = null;
	List<String> formatNames = null;
	
	/**
	 * Creates a new DynamicContentCreatorPanel.
	 */
	protected DynamicContentCreatorPanel()
	{
		
	}

	/**
	 * Returns true if this panel displays variable selection.
	 * 
	 * @return True if this panel displays variable selection.
	 */
	public boolean isDynamic()
	{
		return variables != null;
	}

	/**
	 * Returns true if the dynamic value is currently selected.
	 * 
	 * @return True if the dynamic value is currently selected.
	 */
	public boolean isDynamicSelected()
	{
		return dynamicSelected;
	}

	/**
	 * Returns the current dynamic selection.
	 * 
	 * @return The current dynamic selection.
	 */
	public String getDynamicSelection()
	{
		if (options == null || !dynamicSelected)
			return null;
		return options.getItem(options.getSelectionIndex());
	}

	/**
	 * Sets the list of variables that are available.
	 * 
	 * @param variables The list of variables that are available.
	 */
	public void setVariables(List<Variable> variables)
	{
		if (variables == null)
		{
			setDynamicSelected(false);
			this.variables = null;
		}
		else
		{
			this.variables = new ArrayList<Variable>(variables);
			Collections.sort(this.variables, new Comparator<Variable>()
			{
				public int compare(Variable o1, Variable o2)
				{
					return o1.getName().compareTo(o2.getName());
				}
			});
		}
	}

	/**
	 * Sets the selected value to dynamic or static.
	 * 
	 * @param dynamicSelected True if the dynamic value should be selected.
	 */
	public void setDynamicSelected(boolean dynamicSelected)
	{
		if (!isDynamic() && dynamicSelected)
			return;
		this.dynamicSelected = dynamicSelected;
		if (staticButton != null)
			staticButton.setSelection(!dynamicSelected);
		if (dynamicButton != null)
			dynamicButton.setSelection(dynamicSelected);
	}

	/**
	 * Sets the current dynamic selection.
	 * 
	 * @param dynamicSelection The dynamic selection to choose.
	 */
	public void setDynamicSelection(String dynamicSelection)
	{
		if (options == null || !dynamicSelected || dynamicSelection == null)
			return;
		for (int i = 0; i < options.getItemCount(); ++i)
		{
			if (dynamicSelection.equals(options.getItem(i)))
			{
				options.select(i);
				break;
			}
		}
	}
	
	/**
	 * @return
	 */
	public String getFormat()
	{
		if(formatCombo != null && formatCombo.getSelectionIndex() != -1)
		{
			return formatCombo.getItem(formatCombo.getSelectionIndex());
		}
		return "Default";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.media.core.ContentCreatorPanel#createControls(
	 *      org.eclipse.swt.widgets.Composite)
	 */
	public final void createControls(Composite parent)
	{
		if (!isDynamic())
		{
			setControl(createStaticControls(parent));
			return;
		}
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		staticButton = new Button(composite, SWT.RADIO);
		staticButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
				false));
		staticButton.setSelection(!dynamicSelected);
		staticButton.setText("Use a static value:");
		Control staticControl = createStaticControls(composite);
		staticControl.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, true));
		dynamicButton = new Button(composite, SWT.RADIO);
		dynamicButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dynamicButton.setSelection(dynamicSelected);
		dynamicButton.setText("Use a value from a variable:");
		Control dynamicControl = createDynamicControls(composite);
		dynamicControl.setLayoutData(new GridData(GridData.FILL_BOTH));
		SelectionListener selectionListener = new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (staticButton.equals(e.getSource()))
				{
					dynamicSelected = false;
				}
				else if (dynamicButton.equals(e.getSource()))
				{
					dynamicSelected = true;
				}
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		};
		staticButton.addSelectionListener(selectionListener);
		dynamicButton.addSelectionListener(selectionListener);
		
		if(createContent() instanceof FormattableContent)
		{
			createFormatterControls(composite);
		}
		setControl(composite);
	}
	
	protected Control createFormatterControls(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label formatLabel = new Label(composite, SWT.NONE);
		formatLabel.setText("Please select a format:");
		formatLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		formatCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
		formatNames = getMediaProvider().getFormatManager().getFormats((FormattableContent)createContent());
		for(String formatName : formatNames)
        {
	        formatCombo.add(formatName);
        }
		formatCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		formatCombo.select(0);
		return composite;
	}

	/**
	 * @param parent
	 * @return
	 */
	protected Control createDynamicControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData());
		comp.setLayout(new GridLayout(2, false));
		options = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		options.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for (Variable v : variables)
		{
			processVariable(v);
		}
		options.add("LastResult.markname");
		options.add("LastResult.marktime");
		options.add("LastResult.confidence");
		options.add("LastResult.utterance");
		options.add("LastResult.inputmode");
		options.add("LastResult.interpretation");
		if (options.getItemCount() > 0)
			options.select(0);
		return comp;
	}
	
	/**
	 * @param def
	 */
	private void processVariable(ObjectDefinition def) {
		options.add(def.getPath());
		for (ObjectField field : def.getFields())
			processVariable(field);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.ContentCreatorPanel#setInitialContent(org.eclipse.vtp.framework.interactions.core.media.Content)
	 */
	public void setInitialContent(Content content)
	{
		if (content instanceof FormattableContent)
		{
			FormattableContent dateContent = (FormattableContent)content;
			for(int i = 0; i < formatNames.size(); i++)
	        {
		        String formatName = formatNames.get(i);
		        if(formatName.equals(dateContent.getFormatName()))
		        	formatCombo.select(i);
	        }
		}
	}

	/**
	 * @param parent
	 * @return
	 */
	protected abstract Control createStaticControls(Composite parent);
	
}
