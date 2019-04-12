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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignFolder;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IDesignRootFolder;
import org.eclipse.vtp.desktop.model.core.IWorkflowExit;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.standard.ui.ReturnInformationProvider;

public class ReturnGeneralPropertiesPanel extends DesignElementPropertiesPanel
{
	Label errorField;
	Label nameLabel;
	Text nameField;
	Combo exitTypeCombo;
	/** An ArrayList containing the Portal Exits to which this Portal Entry may link */
	List<IWorkflowExit> returnElements = new ArrayList<IWorkflowExit>();
	private ReturnInformationProvider info = null;

	public ReturnGeneralPropertiesPanel(String name, IDesignElement ppe)
	{
		super(name, ppe);
		info = (ReturnInformationProvider)((PrimitiveElement)ppe).getInformationProvider();
		returnElements.addAll(ppe.getDesign().getDocument().getWorkflowExits());
		IDesignItemContainer container = ppe.getDesign().getDocument().getParentDesignContainer();
		while(!(container instanceof IDesignRootFolder))
			container = (IDesignItemContainer)container.getParent();
		traverseDesigns(container);
	}
	
	private void traverseDesigns(IDesignItemContainer container)
	{
		List<IDesignDocument> documents = container.getDesignDocuments();
		for(IDesignDocument document : documents)
		{
			if(!document.equals(getElement().getDesign().getDocument()))
			{
				returnElements.addAll(document.getWorkflowExits());
			}
		}
		List<IDesignFolder> folders = container.getDesignFolders();
		for(IDesignFolder folder : folders)
		{
			traverseDesigns(folder);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.ComponentPropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBackground(parent.getBackground());
		comp.setLayout(new GridLayout(2, false));
		nameLabel = new Label(comp, SWT.NONE);
		nameLabel.setText("Name: ");
		nameLabel.setBackground(comp.getBackground());
		nameLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		nameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));
		errorField = new Label(comp, SWT.SINGLE);
		errorField.setBackground(comp.getBackground());
		errorField.setForeground(comp.getDisplay().getSystemColor(SWT.COLOR_RED));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER);
		gd.horizontalSpan = 2;
		errorField.setLayoutData(gd);
		nameField.addModifyListener(new ModifyListener()
		{
			
			public void modifyText(ModifyEvent e)
			{
				for(IWorkflowExit exit : returnElements)
				{
					if(!exit.getId().equals(getElement().getId()))
					{
						if(exit.getName().equals(nameField.getText()))
						{
							errorField.setText("There is already a Return with that name.");
							ReturnGeneralPropertiesPanel.this.getContainer().setCanFinish(false);
							return;
						}
					}
				}
				errorField.setText("");
				ReturnGeneralPropertiesPanel.this.getContainer().setCanFinish(true);
			}
			
		});
		nameField.setText(getElement().getName());
		errorField.setText("");
		getContainer().setCanFinish(true);
		for(IWorkflowExit exit : returnElements)
		{
			if(!exit.getId().equals(getElement().getId()))
			{
				if(exit.getName().equals(nameField.getText()))
				{
					errorField.setText("There is already a Return with that name.");
					getContainer().setCanFinish(false);
					break;
				}
			}
		}
		Label exitTypeLabel = new Label(comp, SWT.NONE);
		exitTypeLabel.setBackground(comp.getBackground());
		exitTypeLabel.setText("Exit Type:");
		exitTypeLabel.setLayoutData(new GridData());
		exitTypeCombo = new Combo(comp, SWT.SINGLE | SWT.READ_ONLY | SWT.DROP_DOWN);
		exitTypeCombo.add("Normal");
		exitTypeCombo.add("Error");
		if(info.getExitType() != null && info.getExitType().equals("Error"))
			exitTypeCombo.select(1);
		else
			exitTypeCombo.select(0);

		setControl(comp);
	}

	public void save()
	{
		try
		{
			getElement().setName(nameField.getText());
			info.setExitType(exitTypeCombo.getItem(exitTypeCombo.getSelectionIndex()));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void cancel()
	{
		
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
