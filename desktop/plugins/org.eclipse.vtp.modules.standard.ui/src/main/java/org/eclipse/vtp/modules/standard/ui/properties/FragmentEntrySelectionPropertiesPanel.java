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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IWorkflowEntry;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.elements.core.configuration.FragmentConfigurationManager;
import org.eclipse.vtp.desktop.model.elements.core.internal.ApplicationFragmentElement;

public class FragmentEntrySelectionPropertiesPanel extends DesignElementPropertiesPanel
{
	/** A combo box used to select by name the Portal Exit module to which this Portal Entry module will link */
	Combo exitCombo = null;
	/** An ArrayList containing the Portal Exits to which this Portal Entry may link */
	List<IWorkflowEntry> entryPoints = new ArrayList<IWorkflowEntry>();
	/** The InformationProvider for this particular Portal Entry Module */
	private FragmentConfigurationManager manager = null;
	IWorkflowEntry currentEntry = null;

	/**
	 * @param name
	 * @param element
	 */
	public FragmentEntrySelectionPropertiesPanel(String name, IDesignElement element)
	{
		super(name, element);
		manager = (FragmentConfigurationManager)element.getConfigurationManager(FragmentConfigurationManager.TYPE_ID);
	}
	
	public void resolve()
	{
		ApplicationFragmentElement applicationFragmentElement = (ApplicationFragmentElement)getElement();
		if(applicationFragmentElement.isModelPresent())
		{
			IWorkflowProject referencedModel = applicationFragmentElement.getReferencedModel();
			entryPoints = referencedModel.getWorkflowEntries();
			IWorkflowEntry defaultEntry = null;
			for(IWorkflowEntry entry : entryPoints)
			{
				if(entry.getName().equals(""))
				{
					defaultEntry = entry;
				}
				if(defaultEntry == null)
					defaultEntry = entry;
			}
			if(manager.getEntryId() != null)
			{
				for(IWorkflowEntry entry : entryPoints)
				{
					if(entry.getId().equals(manager.getEntryId()))
					{
						currentEntry = entry;
						break;
					}
				}
			}
			if(currentEntry == null)
			{
				currentEntry = defaultEntry;
				if(defaultEntry != null)
					manager.setEntryId(defaultEntry.getId());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBackground(parent.getBackground());
		GridLayout gridLayout = new GridLayout();
		comp.setLayout(gridLayout);
		Label exitLabel = new Label(comp, SWT.NONE);
		exitLabel.setText("Please select a Workflow Entry.");
		exitLabel.setBackground(comp.getBackground());
		exitLabel.setLayoutData(new GridData());
		exitCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SINGLE);
		for(IWorkflowEntry entryPoint : entryPoints)
        {
       		exitCombo.add(entryPoint.getName());
       		if(entryPoint.equals(currentEntry))
       			exitCombo.select(exitCombo.getItemCount() - 1);
        }
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 300;
		exitCombo.setLayoutData(gridData);
		exitCombo.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				IWorkflowEntry entryPoint = entryPoints.get(exitCombo.getSelectionIndex());
				manager.setEntryId(entryPoint.getId());
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#save()
	 */
	public void save()
	{
		IWorkflowEntry entryPoint = entryPoints.get(exitCombo.getSelectionIndex());
		getElement().setName(((ApplicationFragmentElement)getElement()).getReferencedModel().getProject().getName() + " (" + entryPoint.getName() + ")");
		getElement().commitConfigurationChanges(manager);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ComponentPropertiesPanel#cancel()
	 */
	public void cancel()
	{
		getElement().rollbackConfigurationChanges(manager);
	}

	@Override
	public void setConfigurationContext(Map<String, Object> values)
	{
	}

	@Override
	public List<String> getApplicableContexts()
	{
		return Collections.emptyList();
	}

}
