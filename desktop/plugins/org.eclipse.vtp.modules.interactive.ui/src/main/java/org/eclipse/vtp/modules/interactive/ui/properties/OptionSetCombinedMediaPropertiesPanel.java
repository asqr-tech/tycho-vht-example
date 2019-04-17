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

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen;
import org.eclipse.vtp.desktop.media.core.MediaConfigurationScreenContainer;
import org.eclipse.vtp.desktop.media.core.MediaConfigurationScreenManager;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.ISecurableElement;
import org.eclipse.vtp.desktop.model.core.internal.branding.BrandContext;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.internal.context.InteractionTypeContext;
import org.eclipse.vtp.desktop.model.interactive.core.internal.context.LanguageContext;
import org.eclipse.vtp.framework.util.VariableNameValidator;
import org.eclipse.vtp.modules.interactive.ui.OptionSetInformationProvider;

@SuppressWarnings("restriction")
public class OptionSetCombinedMediaPropertiesPanel extends DesignElementPropertiesPanel implements MediaConfigurationScreenContainer
{
	StackLayout stackLayout = null;
	Map<String, MediaConfigurationScreen> screensByType;
	Composite comp = null;
	FormToolkit toolkit = null;
	/** The text field used to set name of this particular Question module */
	Text nameField = null;
	Text variableField = null;
	/** A checkbox used to denote whether this element may contain sensitive data */
	Button secureElementButton = null;
	Label exitTypeLabel = null;
	private OptionSetInformationProvider info = null;
	
	/**
	 * @param element
	 */
	/**
	 * @param name
	 * @param element
	 */
	public OptionSetCombinedMediaPropertiesPanel(String name, IDesignElement element)
	{
		super(name, element);
		PrimitiveElement primitiveElement = (PrimitiveElement)element;
		info = (OptionSetInformationProvider)primitiveElement.getInformationProvider();
		screensByType = MediaConfigurationScreenManager.getInstance().getIndexedMediaConfigurationScreens(primitiveElement.getSubTypeId(), this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setBackground(parent.getBackground());
		mainComp.setLayout(new GridLayout(2, false));
		
		toolkit = new FormToolkit(parent.getDisplay());
		final Section contentSection =
			toolkit.createSection(mainComp, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		contentSection.setLayoutData(gridData);
		contentSection.setText("General");
		
		Label nameLabel = new Label(mainComp, SWT.NONE);
		nameLabel.setBackground(mainComp.getBackground());
		nameLabel.setText("Name");
		nameLabel.setLayoutData(new GridData());
		nameField = new Text(mainComp, SWT.BORDER | SWT.SINGLE);
		nameField.setText(getElement().getName());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		exitTypeLabel = new Label(mainComp, SWT.NONE);
		exitTypeLabel.setBackground(mainComp.getBackground());
		exitTypeLabel.setText("Variable Name: (Optional)");
		exitTypeLabel.setLayoutData(new GridData());
		
		variableField = new Text(mainComp, SWT.SINGLE | SWT.BORDER);
		variableField.setText(info.getVariableName());
		variableField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		variableField.addVerifyListener(new VerifyListener()
		{
			public void verifyText(VerifyEvent e)
			{
				String currentName = variableField.getText().substring(0, e.start) + e.text + variableField.getText(e.end, (variableField.getText().length() - 1));
				if("".equals(currentName) || VariableNameValidator.followsVtpNamingRules(currentName))
				{
					exitTypeLabel.setForeground(exitTypeLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
					variableField.setForeground(variableField.getDisplay().getSystemColor(SWT.COLOR_BLACK));
					getContainer().setCanFinish(true);
				}
				else
				{
					exitTypeLabel.setForeground(exitTypeLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
					variableField.setForeground(variableField.getDisplay().getSystemColor(SWT.COLOR_RED));
					getContainer().setCanFinish(false);
				}
            }
		});
		if("".equals(variableField.getText()) || VariableNameValidator.followsVtpNamingRules(variableField.getText()))
		{
			exitTypeLabel.setForeground(exitTypeLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			variableField.setForeground(variableField.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			getContainer().setCanFinish(true);
		}
		else
		{
			exitTypeLabel.setForeground(exitTypeLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
			variableField.setForeground(variableField.getDisplay().getSystemColor(SWT.COLOR_RED));
			getContainer().setCanFinish(false);
		}

		if(ISecurableElement.class.isAssignableFrom(info.getClass()))
		{
			secureElementButton = new Button(mainComp, SWT.CHECK);
			secureElementButton.setText("This element may contain sensitive data and should be secured");
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;
			secureElementButton.setLayoutData(gridData);
			secureElementButton.setSelection(((ISecurableElement)info).isSecured());
		}
		
		comp = new Composite(mainComp, SWT.NONE);
		comp.setBackground(parent.getBackground());
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalIndent = 20;
		gridData.horizontalSpan = 2;
		comp.setLayoutData(gridData);
		stackLayout = new StackLayout();
		comp.setLayout(stackLayout);
		for(Map.Entry<String, MediaConfigurationScreen> entry : screensByType.entrySet())
		{
			MediaConfigurationScreen mcs = entry.getValue();
			mcs.createControls(comp);
			stackLayout.topControl = mcs.getControl();
		}
		setControl(mainComp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#save()
	 */
	public void save()
	{
		try
		{
			getElement().setName(nameField.getText());
			if(ISecurableElement.class.isAssignableFrom(info.getClass()))
			{
				((ISecurableElement)info).setSecured(secureElementButton.getSelection());
			}
			info.setVariableName(variableField.getText());
			for(Map.Entry<String, MediaConfigurationScreen> entry : screensByType.entrySet())
			{
				MediaConfigurationScreen mcs = entry.getValue();
				mcs.save();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ComponentPropertiesPanel#cancel()
	 */
	public void cancel()
	{
		for(Map.Entry<String, MediaConfigurationScreen> entry : screensByType.entrySet())
		{
			MediaConfigurationScreen mcs = entry.getValue();
			mcs.cancel();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#setConfigurationContext(org.eclipse.vtp.desktop.core.configuration.Brand, java.lang.String, java.lang.String)
	 */
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

	public List<String> getApplicableContexts()
	{
		List<String> ret = super.getApplicableContexts();
		ret.add(LanguageContext.CONTEXT_ID);
		ret.add(InteractionTypeContext.CONTEXT_ID);
		return ret;
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
