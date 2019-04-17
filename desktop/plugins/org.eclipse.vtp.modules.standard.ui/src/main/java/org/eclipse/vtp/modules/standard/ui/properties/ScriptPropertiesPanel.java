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
import org.eclipse.vtp.modules.standard.ui.ScriptInformationProvider;

/**
 * The graphical user interface used to configure a script module
 */
public class ScriptPropertiesPanel extends DesignElementPropertiesPanel
{
	/**	The text field that contains the script and serves as the editor */
	Text scriptText = null;
	FormToolkit toolkit = null;
	/** The text field used to set name of this particular Script module */
	Text nameField = null;
	/** A checkbox used to denote whether this script may contain sensitive data */
	Button secureElementButton = null;
	private ScriptInformationProvider info = null;

	/**
	 * @param name
	 * @param element
	 */
	public ScriptPropertiesPanel(String name, IDesignElement element)
	{
		super(name, element);
		info = (ScriptInformationProvider)((PrimitiveElement)element).getInformationProvider();
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
		contentSection.setText("Script");
		
		scriptText = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 400;
		gridData.widthHint = 300;
		scriptText.setLayoutData(gridData);
		scriptText.setText(info.getScriptText());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#save()
	 */
	public void save()
	{
		getElement().setName(nameField.getText());
		info.setScriptText(scriptText.getText());
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
