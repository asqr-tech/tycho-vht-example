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

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen;
import org.eclipse.vtp.desktop.media.core.MediaConfigurationScreenContainer;
import org.eclipse.vtp.desktop.media.core.MediaConfigurationScreenManager;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.internal.branding.BrandContext;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.internal.context.InteractionTypeContext;
import org.eclipse.vtp.desktop.model.interactive.core.internal.context.LanguageContext;

@SuppressWarnings("restriction")
public class PrimitiveMediaPropertiesPanel extends DesignElementPropertiesPanel implements MediaConfigurationScreenContainer
{
	StackLayout stackLayout = null;
	Map<String, MediaConfigurationScreen> screensByType;
	Composite comp = null;
	
	public PrimitiveMediaPropertiesPanel(String name, IDesignElement element)
	{
		super(name, element);
		PrimitiveElement pe = (PrimitiveElement)element;
		screensByType = MediaConfigurationScreenManager.getInstance().getIndexedMediaConfigurationScreens(pe.getSubTypeId(), this);
	}

	public void createControls(Composite parent)
	{
		comp = new Composite(parent, SWT.NONE);
		comp.setBackground(parent.getBackground());
		stackLayout = new StackLayout();
		comp.setLayout(stackLayout);
		for(Map.Entry<String, MediaConfigurationScreen> entry : screensByType.entrySet())
		{
			MediaConfigurationScreen mcs = entry.getValue();
			mcs.createControls(comp);
			stackLayout.topControl = mcs.getControl();
		}
		setControl(comp);
	}

	public void save()
	{
		for(Map.Entry<String, MediaConfigurationScreen> entry : screensByType.entrySet())
		{
			MediaConfigurationScreen mcs = entry.getValue();
			mcs.save();
		}
	}

	public void cancel()
	{
		for(Map.Entry<String, MediaConfigurationScreen> entry : screensByType.entrySet())
		{
			MediaConfigurationScreen mcs = entry.getValue();
			mcs.cancel();
		}
	}

	public void setConfigurationContext(Map<String, Object> values)
	{
		IBrand brand = (IBrand)values.get(BrandContext.CONTEXT_ID);
		String language = (String)values.get(LanguageContext.CONTEXT_ID);
		Object object = values.get(InteractionTypeContext.CONTEXT_ID);
		if(brand == null || language == null || object == null)
		{
			final IWorkflowProject project = getElement().getDesign().getDocument().getProject();
			System.out.println("project: " + project);
			final IProject uproject = project.getUnderlyingProject();
			final Shell shell = this.getContainer().getParentShell();
			Display.getCurrent().asyncExec(new Runnable(){
				public void run()
				{
					MessageBox mb = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_ERROR);
					mb.setText("Configuration Problems");
					mb.setMessage("The interaction and language configuration for this project is incomplete.  You will not be able edit the applications effectively until this is resolved.  Would you like to configure this now?");
					if(mb.open() == SWT.OK)
					{
						Display.getCurrent().asyncExec(new Runnable(){
							public void run()
							{
								PropertyDialog pd = PropertyDialog
								.createDialogOn(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "org.eclipse.vtp.desktop.projects.core.appproperties", uproject);
								pd.open();
							}
						});
					}
					getContainer().cancelDialog();
				}
			});
			return;
		}
		String interactionType = ((InteractionType)object).getId();
		MediaConfigurationScreen mcs = screensByType.get(interactionType);
		if(mcs != null)
		{
			mcs.setBrand(brand);
			mcs.setLanguage(language);
			stackLayout.topControl = mcs.getControl();
			comp.layout(true, true);
		}
	}

	public void cancelMediaConfiguration()
	{
		final IWorkflowProject project = getElement().getDesign().getDocument().getProject();
		final IProject uproject = project.getUnderlyingProject();
		final Shell shell = this.getContainer().getParentShell();
		Display.getCurrent().asyncExec(new Runnable(){
			public void run()
			{
				MessageBox mb = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_ERROR);
				mb.setText("Configuration Problems");
				mb.setMessage("The selected language does not have an associated Voice project.  You will not be able to configure interactive modules until this is resolved.  Would you like to configure this now?");
				if(mb.open() == SWT.OK)
				{
					Display.getCurrent().asyncExec(new Runnable(){
						public void run()
						{
							PropertyDialog pd = PropertyDialog
							.createDialogOn(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "org.eclipse.vtp.desktop.projects.core.appproperties", uproject);
							pd.open();
						}
					});
					getContainer().cancelDialog();
				}
			}
		});
	}

	public IDesignElement getDesignElement()
	{
		return getElement();
	}

}
