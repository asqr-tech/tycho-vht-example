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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignFolder;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IDesignRootFolder;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignEntryPoint;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.standard.ui.PortalEntryInformationProvider;

public class PortalEntryPropertiesPanel extends DesignElementPropertiesPanel
{
	/** A combo box used to select by name the Portal Exit module to which this Portal Entry module will link */
	Combo exitCombo = null;
	/** An ArrayList containing the Portal Exits to which this Portal Entry may link */
	List<IDesignEntryPoint> entryPoints = new ArrayList<IDesignEntryPoint>();
	/** The InformationProvider for this particular Portal Entry Module */
	PortalEntryInformationProvider info;

	/**
	 * @param name
	 * @param element
	 */
	public PortalEntryPropertiesPanel(String name, IDesignElement element)
	{
		super(name, element);
		info = (PortalEntryInformationProvider)((PrimitiveElement)element).getInformationProvider();
		entryPoints.addAll(element.getDesign().getDocument().getDesignEntryPoints());
		IDesignItemContainer container = element.getDesign().getDocument().getParentDesignContainer();
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
		exitLabel.setText("Please select the Portal exit for this entry element.");
		exitLabel.setBackground(comp.getBackground());
		exitLabel.setLayoutData(new GridData());
		exitCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SINGLE);
		for(IDesignEntryPoint entryPoint : entryPoints)
        {
       		exitCombo.add(entryPoint.getName());
        }
		if(info.getExitId() != null)
		{
			for(int i = 0; i < entryPoints.size(); i++)
	        {
				IDesignEntryPoint entryPoint = entryPoints.get(i);
		        if(entryPoint.getId().equals(info.getExitId()))
		        {
		        	exitCombo.select(i);
		        	break;
		        }
	        }
		}
		else
			exitCombo.select(0);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = 300;
		exitCombo.setLayoutData(gridData);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#save()
	 */
	public void save()
	{
		IDesignEntryPoint entryPoint = entryPoints.get(exitCombo.getSelectionIndex());
		info.setExitId(entryPoint.getId());
		getElement().setName(entryPoint.getName());
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
