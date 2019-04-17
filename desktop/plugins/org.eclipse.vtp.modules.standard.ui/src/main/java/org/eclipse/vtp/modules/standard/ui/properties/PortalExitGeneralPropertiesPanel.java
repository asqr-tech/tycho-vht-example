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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignFolder;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IDesignRootFolder;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignEntryPoint;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.standard.ui.PortalExitInformationProvider;

/**
 * LanguageSelectionGeneralPropertiesPanel.
 *
 * @author Lonnie Pryor
 */
public class PortalExitGeneralPropertiesPanel extends DesignElementPropertiesPanel
{
	Label nameLabel;
	/** The text field used to set name of this particular Portal Exit module */
	Text nameField;
	Label errorField;
	Label referencesLabel;
	/** The InformationProvider for this particular Portal Exit module */
	PortalExitInformationProvider info;
	/** An ArrayList containing the Portal Exits to which this Portal Entry may link */
	List<IDesignEntryPoint> entryPoints = new ArrayList<IDesignEntryPoint>();

	/**
	 * @param name
	 * @param ppe
	 */
	public PortalExitGeneralPropertiesPanel(String name, IDesignElement ppe)
	{
		super(name, ppe);
		info = (PortalExitInformationProvider)((PrimitiveElement)ppe).getInformationProvider();
		entryPoints.addAll(ppe.getDesign().getDocument().getDesignEntryPoints());
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
				entryPoints.addAll(document.getDesignEntryPoints());
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
		referencesLabel = new Label(comp, SWT.NONE);
		referencesLabel.setText("Entries associated with this Exit: " + info.getConnectingPoints().size());
		referencesLabel.setBackground(comp.getBackground());
		referencesLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER);
		gd.horizontalSpan = 2;
		errorField.setLayoutData(gd);
		nameField.addModifyListener(new ModifyListener()
		{
			
			public void modifyText(ModifyEvent e)
			{
				for(IDesignEntryPoint entryPoint : entryPoints)
				{
					if(!entryPoint.getId().equals(getElement().getId()) && entryPoint.getName().equals(nameField.getText()))
					{
						errorField.setText("There is already a Portal with that name.");
						PortalExitGeneralPropertiesPanel.this.getContainer().setCanFinish(false);
						return;
					}
				}
				errorField.setText("");
				PortalExitGeneralPropertiesPanel.this.getContainer().setCanFinish(true);
			}
			
		});
		nameField.setText(getElement().getName());
		for(IDesignEntryPoint entryPoint : entryPoints)
		{
			if(!entryPoint.getId().equals(getElement().getId()) && entryPoint.getName().equals(nameField.getText()))
			{
				errorField.setText("There is already a Portal with that name.");
				getContainer().setCanFinish(false);
				return;
			}
		}
		errorField.setText("");
		getContainer().setCanFinish(true);
		setControl(comp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#save()
	 */
	public void save()
	{
		getElement().setName(nameField.getText());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ComponentPropertiesPanel#cancel()
	 */
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
