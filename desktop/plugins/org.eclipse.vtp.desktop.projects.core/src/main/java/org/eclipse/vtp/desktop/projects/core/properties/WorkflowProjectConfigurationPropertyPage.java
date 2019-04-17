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
package org.eclipse.vtp.desktop.projects.core.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.internal.WorkflowProject;
import org.eclipse.vtp.desktop.model.core.internal.branding.Brand;
import org.eclipse.vtp.desktop.model.core.internal.branding.DefaultBrandManager;
import org.eclipse.vtp.desktop.projects.core.util.BrandConfigurationScreen;
import org.eclipse.vtp.desktop.projects.core.util.ConfigurationBrandManager;
import org.eclipse.vtp.framework.util.Guid;

public class WorkflowProjectConfigurationPropertyPage extends PropertyPage
{
	private BrandConfigurationScreen screen = new BrandConfigurationScreen();
	private WorkflowProject applicationProject = null;
	private ConfigurationBrandManager brandManager = null;

	public WorkflowProjectConfigurationPropertyPage()
	{
		DefaultBrandManager defaultManager = new DefaultBrandManager();
		defaultManager.setDefaultBrand(new Brand(Guid.createGUID(), "Default"));
		brandManager = new ConfigurationBrandManager(defaultManager);
		screen.init(brandManager);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.PropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
	 */
	public void setElement(IAdaptable element)
	{
		super.setElement(element);
		try
        {
	        if(element instanceof WorkflowProject)
	        	applicationProject = (WorkflowProject)element;
	        else if(element instanceof IProject)
	        {
	        	IProject project = (IProject)element;
	        	if(WorkflowCore.getDefault().getWorkflowModel().isWorkflowProject(project))
	        		applicationProject = (WorkflowProject)WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowProject(project);
	        	else
	        		throw new RuntimeException("Unsupported element type");
	        }
	        else
        		throw new RuntimeException("Unsupported element type");
	        brandManager = new ConfigurationBrandManager(applicationProject.getBrandManager());
	        screen.init(brandManager);
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
		comp.setLayout(new FillLayout());
		screen.createContents(comp);
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		if(applicationProject != null)
		{
			brandManager = new ConfigurationBrandManager(applicationProject.getBrandManager());
		}
		else
		{
			DefaultBrandManager defaultManager = new DefaultBrandManager();
			defaultManager.setDefaultBrand(new Brand(Guid.createGUID(), "Default"));
			brandManager = new ConfigurationBrandManager(defaultManager);
		}
		screen.init(brandManager);
		super.performDefaults();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk()
	{
		brandManager.saveTo(applicationProject.getBrandManager(), false);
		applicationProject.storeBuildPath();
		return true;
	}
}
