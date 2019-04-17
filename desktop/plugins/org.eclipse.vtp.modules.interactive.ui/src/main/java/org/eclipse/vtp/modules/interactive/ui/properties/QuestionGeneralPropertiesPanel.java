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
package org.eclipse.vtp.modules.interactive.ui.properties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.interactive.ui.QuestionInformationProvider;

public class QuestionGeneralPropertiesPanel extends DesignElementPropertiesPanel
{
	Label nameLabel;
	Text nameField;
	Text variableField;
	private QuestionInformationProvider info = null;

	public QuestionGeneralPropertiesPanel(String name, IDesignElement ppe)
	{
		super(name, ppe);
		info = (QuestionInformationProvider)((PrimitiveElement)getElement()).getInformationProvider();
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
		nameField.setText(getElement().getName());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));
		Label exitTypeLabel = new Label(comp, SWT.NONE);
		exitTypeLabel.setBackground(comp.getBackground());
		exitTypeLabel.setText("Variable Name:");
		exitTypeLabel.setLayoutData(new GridData());
		variableField = new Text(comp, SWT.SINGLE | SWT.BORDER);
		variableField.setText(info.getVariableName());
		variableField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		setControl(comp);
	}

	public void save()
	{
		try
		{
			getElement().setName(nameField.getText());
			info.setVariableName(variableField.getText());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void cancel()
	{
		
	}

	public void setConfigurationContext(Map<String, Object> values)
	{
	}
	
	public List<String> getApplicableContexts()
	{
		return Collections.emptyList();
	}
}
