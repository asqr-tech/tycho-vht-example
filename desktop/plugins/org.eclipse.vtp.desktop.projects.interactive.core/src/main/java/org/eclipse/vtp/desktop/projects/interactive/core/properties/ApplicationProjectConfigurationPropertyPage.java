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
package org.eclipse.vtp.desktop.projects.interactive.core.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.core.internal.InteractiveWorkflowProject;
import org.eclipse.vtp.desktop.projects.core.util.BrandConfigurationScreen;
import org.eclipse.vtp.desktop.projects.core.util.ConfigurationBrandManager;
import org.eclipse.vtp.desktop.projects.interactive.core.util.InteractionSupportManager;
import org.eclipse.vtp.desktop.projects.interactive.core.util.InteractionTypeConfigurationScreen;
import org.eclipse.vtp.desktop.projects.interactive.core.util.LanguageConfigurationScreen;

public class ApplicationProjectConfigurationPropertyPage extends PropertyPage
{
	private BrandConfigurationScreen brandScreen = new BrandConfigurationScreen();
	private InteractionTypeConfigurationScreen interactionScreen = new InteractionTypeConfigurationScreen();
	private LanguageConfigurationScreen languageScreen = new LanguageConfigurationScreen();
	private InteractiveWorkflowProject applicationProject = null;
	private ConfigurationBrandManager brandManager = null;
	private InteractionSupportManager supportManager = null;

	public ApplicationProjectConfigurationPropertyPage()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.PropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
	 */
	public void setElement(IAdaptable element)
	{
		super.setElement(element);
		try
        {
	        if(element instanceof InteractiveWorkflowProject)
	        	applicationProject = (InteractiveWorkflowProject)element;
	        else if(element instanceof IProject)
	        {
	        	IProject project = (IProject)element;
	        	if(WorkflowCore.getDefault().getWorkflowModel().isWorkflowProject(project))
	        		applicationProject = (InteractiveWorkflowProject)WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowProject(project);
	        	else
	        		throw new RuntimeException("Unsupported element type");
	        }
	        else
        		throw new RuntimeException("Unsupported element type");
			brandManager = new ConfigurationBrandManager(applicationProject.getBrandManager());
			supportManager = new InteractionSupportManager();
			supportManager.init(applicationProject.getInteractionTypeSupport());
			brandScreen.init(brandManager);
			interactionScreen.setSupport(supportManager);
			languageScreen.init(brandManager, supportManager);
        }
        catch(Exception e)
        {
	        e.printStackTrace();
        }
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		TabFolder tabFolder = new TabFolder(comp, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		TabItem brandItem = new TabItem(tabFolder, SWT.NONE);
		brandItem.setText("Brands");
		Composite brandComp = new Composite(tabFolder, SWT.NONE);
		brandComp.setLayout(new FillLayout());
		brandScreen.createContents(brandComp);
		brandItem.setControl(brandComp);
		TabItem interactionItem = new TabItem(tabFolder, SWT.NONE);
		interactionItem.setText("Interaction Types");
		Composite interactionComp = new Composite(tabFolder, SWT.NONE);
		interactionComp.setLayout(new FillLayout());
		interactionScreen.createContents(interactionComp);
		interactionItem.setControl(interactionComp);
		TabItem languageItem = new TabItem(tabFolder, SWT.NONE);
		languageItem.setText("Languages");
		Composite languageComp = new Composite(tabFolder, SWT.NONE);
		languageComp.setLayout(new FillLayout());
		languageScreen.createContents(languageComp);
		languageItem.setControl(languageComp);
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		brandManager = new ConfigurationBrandManager(applicationProject.getBrandManager());
		supportManager = new InteractionSupportManager();
		supportManager.init(applicationProject.getInteractionTypeSupport());
		brandScreen.init(brandManager);
		interactionScreen.setSupport(supportManager);
		languageScreen.init(brandManager, supportManager);
		super.performDefaults();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk()
	{
		brandManager.saveTo(applicationProject.getBrandManager());
		applicationProject.setInteractionTypeSupport(supportManager.getSupport());
		applicationProject.storeBuildPath();
		return true;
	}
}
