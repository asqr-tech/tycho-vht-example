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
package com.openmethods.openvxml.idriver.ui.properties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;

import com.openmethods.openvxml.idriver.ui.IDriverInitInformationProvider;

/**
 * The graphical user interface used to configure a script module
 */
public class IDriverInitPropertiesPanel extends DesignElementPropertiesPanel
{
	Text callIdVariableText = null;
	Text connIdVariableText = null;
	Text portVariableText = null;
	FormToolkit toolkit = null;
	/** The text field used to set name of this particular module */
	Text nameField = null;
	/** A checkbox used to denote whether this script may contain sensitive data */
	Button secureElementButton = null;
	private IDriverInitInformationProvider info = null;

	/**
	 * @param name
	 * @param element
	 */
	public IDriverInitPropertiesPanel(String name, IDesignElement element)
	{
		super(name, element);
		info = (IDriverInitInformationProvider)((PrimitiveElement)element).getInformationProvider();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		parent.setLayout(new GridLayout(1, false));
		
		toolkit = new FormToolkit(parent.getDisplay());
		final Section generalSection =
			toolkit.createSection(parent, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		generalSection.setLayoutData(gridData);
		generalSection.setText("General");

		Composite generalComp = new Composite(generalSection, SWT.NONE);
		generalComp.setBackground(parent.getBackground());
		generalComp.setLayout(new GridLayout(2, false));
		generalSection.setClient(generalComp);
		
		Label nameLabel = new Label(generalComp, SWT.NONE);
		nameLabel.setText("Name: ");
		nameLabel.setBackground(generalComp.getBackground());
		nameLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		nameField = new Text(generalComp, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getElement().getName());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));

		secureElementButton = new Button(generalComp, SWT.CHECK);
		secureElementButton.setText("This script may contain sensitive data and should be secured");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		secureElementButton.setLayoutData(gridData);
		secureElementButton.setSelection(info.isSecured());

		final Section contentSection =
			toolkit.createSection(parent, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		contentSection.setLayoutData(gridData);
		contentSection.setText("Variables");
		
		Composite variablesComp = new Composite(contentSection, SWT.NONE);
		variablesComp.setBackground(parent.getBackground());
		variablesComp.setLayout(new GridLayout(2, false));
		contentSection.setClient(variablesComp);
		
		Label callIdLabel = new Label(variablesComp, SWT.NONE);
		callIdLabel.setText("Call Id Variable: ");
		callIdLabel.setBackground(variablesComp.getBackground());
		callIdLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));

		callIdVariableText = new Text(variablesComp, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		callIdVariableText.setLayoutData(gridData);
		callIdVariableText.setText(info.getCallIdVariable());

		Label portLabel = new Label(variablesComp, SWT.NONE);
		portLabel.setText("Port Variable: ");
		portLabel.setBackground(variablesComp.getBackground());
		portLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));

		portVariableText = new Text(variablesComp, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		portVariableText.setLayoutData(gridData);
		portVariableText.setText(info.getPortVariable());

		Label connIdLabel = new Label(variablesComp, SWT.NONE);
		connIdLabel.setText("Conn Id Variable: ");
		connIdLabel.setBackground(variablesComp.getBackground());
		connIdLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));

		connIdVariableText = new Text(variablesComp, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		connIdVariableText.setLayoutData(gridData);
		connIdVariableText.setText(info.getConnIdVariable());

	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#save()
	 */
	public void save()
	{
		getElement().setName(nameField.getText());
		info.setCallIdVariable(callIdVariableText.getText());
		info.setPortVariable(portVariableText.getText());
		info.setConnIdVariable(connIdVariableText.getText());
		info.setSecured(secureElementButton.getSelection());
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
