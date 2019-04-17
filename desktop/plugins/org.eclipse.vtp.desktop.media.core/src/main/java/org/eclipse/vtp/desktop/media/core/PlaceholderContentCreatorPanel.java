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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.PlaceholderContent;

/**
 * DynamicContentCreatorPanel.
 * 
 * @author Lonnie Pryor
 */
public class PlaceholderContentCreatorPanel extends ContentCreatorPanel
{
	/** The list of variables that are available. */
	private List<ContentPlaceholder> placeholders = null;
	Combo placeholderCombo = null;
	
	/**
	 * Creates a new DynamicContentCreatorPanel.
	 */
	protected PlaceholderContentCreatorPanel()
	{
	}

	/**
	 * Sets the list of variables that are available.
	 * 
	 * @param variables The list of variables that are available.
	 */
	public void setPlaceholders(List<ContentPlaceholder> placeholders)
	{
		this.placeholders = new ArrayList<ContentPlaceholder>(placeholders);
		Collections.sort(this.placeholders, new Comparator<ContentPlaceholder>()
		{
			public int compare(ContentPlaceholder o1, ContentPlaceholder o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
	}

	public String getFormat()
	{
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
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		Label formatLabel = new Label(composite, SWT.NONE);
		formatLabel.setText("Please select a placeholder:");
		formatLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		placeholderCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
		for(ContentPlaceholder cp : placeholders)
        {
			placeholderCombo.add(cp.getName());
        }
		placeholderCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		placeholderCombo.select(0);
		final Text descText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.widthHint = 200;
		descText.setLayoutData(layoutData);
		descText.setText(placeholders.get(0).getDescription());
		descText.setBackground(composite.getBackground());
		placeholderCombo.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(placeholderCombo.getSelectionIndex() > -1)
				{
					descText.setText(placeholders.get(placeholderCombo.getSelectionIndex()).getDescription());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
		setControl(composite);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.ContentCreatorPanel#setInitialContent(org.eclipse.vtp.framework.interactions.core.media.Content)
	 */
	public void setInitialContent(Content content)
	{
		if(content instanceof PlaceholderContent)
		{
			for(int i = 0; i < placeholders.size(); i++)
			{
				if(placeholders.get(i).getName().equals(((PlaceholderContent)content).getPlaceholder()))
				{
					placeholderCombo.select(i);
					break;
				}
			}
		}
	}

	@Override
	public Content createContent()
	{
		PlaceholderContent content = new PlaceholderContent();
		content.setPlaceholder(placeholderCombo.getItem(placeholderCombo.getSelectionIndex() == -1 ? 0 : placeholderCombo.getSelectionIndex()));
		return content;
	}

}
