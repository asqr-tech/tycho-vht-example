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
package org.eclipse.vtp.desktop.projects.core.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.vtp.desktop.model.core.IBusinessObject;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectSet;

/**
 * This wizard walks the user through the steps required to create a
 * new business object for an application.  The user is prompted to
 * enter a name for the new business object.  This name must be unique
 * among the current business objects in the application.  The business
 * object is automatically created by this wizard and so requires no
 * actions from the caller of the wizard.
 *
 * @author Trip
 */
public class CreateBusinessObjectWizard extends Wizard implements INewWizard,
IExecutableExtension
{
	/**
	 * The business object set that will contain the new business object.
	 */
	private IBusinessObjectSet objectSet = null;

	/**
	 * The wizard page that collects the name of the new business object.
	 */
	private BusinessObjectWizardPage bwp = null;

	IConfigurationElement configElement = null;
	
	/**
	 * Creates a new <code>CreateBusinessObjectWizard</code> instance for
	 * the given business object set.
	 *
	 * @param objectSet The business object set that will contain the new
	 * business object.
	 */
	public CreateBusinessObjectWizard()
	{
		super();
		bwp = new BusinessObjectWizardPage();
		addPage(bwp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement cfig,
		String propertyName, Object data)
	{
		configElement = cfig;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		Object obj = selection.getFirstElement();
		if(obj == null)
		{
			obj = ((IStructuredSelection)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection()).getFirstElement();
		}
		if(obj instanceof IBusinessObjectSet)
			this.objectSet = (IBusinessObjectSet)obj;
		else if(obj instanceof IBusinessObject)
			this.objectSet = ((IBusinessObject)obj).getBusinessObjectSet();
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		try
		{
			IBusinessObject bo = objectSet.createBusinessObject(bwp.brandNameField.getText());
			objectSet.refresh();
			IDE.openEditor(PlatformUI.getWorkbench()
					  .getActiveWorkbenchWindow()
					  .getActivePage(), bo.getUnderlyingFile());

			return true;
		}
		catch(CoreException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	private class BusinessObjectWizardPage extends WizardPage
	{
		Text brandNameField = null;

		public BusinessObjectWizardPage()
		{
			super("BusinessObjectPage",
				"Enter a name for the new business object", null);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			setPageComplete(false);

			Composite comp = new Composite(parent, SWT.NONE);
			Label brandNameLabel = new Label(comp, SWT.NONE);
			brandNameLabel.setText("Business Object Name:");
			brandNameLabel.setSize(brandNameLabel.computeSize(SWT.DEFAULT,
					SWT.DEFAULT));
			brandNameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
			brandNameField.addVerifyListener(new VerifyListener()
			{

				public void verifyText(VerifyEvent e)
                {
	                String text = e.text;
	                char[] chars = text.toCharArray();
					String currentName = brandNameField.getText().substring(0, e.start) + e.text + brandNameField.getText(e.end, (brandNameField.getText().length() - 1));
	                if(currentName.length() > 255)
	                {
	                	e.doit = false;
	                	return;
	                }
	                for(int i = 0; i < chars.length; i++)
	                {
	                	if(Character.isLetterOrDigit(chars[i]))
	                		continue;
	                	if(chars[i] == '$')
	                		continue;
	                	if(chars[i] == '_')
	                		continue;
	                	if(chars[i] == '.')
	                		continue;
	                	e.doit = false;
	                	return;
	                }
	                
                }
				
			});
			brandNameField.addModifyListener(new ModifyListener()
				{
					public void modifyText(ModifyEvent e)
					{
						String n = brandNameField.getText();
						for(IBusinessObject bo : objectSet.getBusinessObjects())
						{
							if(bo.getName().equals(n))
							{
								setErrorMessage(
									"A Business Object already exists with that name.");
								setPageComplete(false);

								return;
							}
						}

						setErrorMessage(null);
						setPageComplete(true);
					}
				});
			comp.setLayout(new FormLayout());

			FormData brandNameLabelData = new FormData();
			brandNameLabelData.left = new FormAttachment(0, 10);
			brandNameLabelData.top = new FormAttachment(0, 30);
			brandNameLabelData.right = new FormAttachment(0,
					10 + brandNameLabel.getSize().x);
			brandNameLabelData.bottom = new FormAttachment(0,
					30 + brandNameLabel.getSize().y);
			brandNameLabel.setLayoutData(brandNameLabelData);

			FormData brandNameFieldData = new FormData();
			brandNameFieldData.left = new FormAttachment(brandNameLabel, 10);
			brandNameFieldData.top = new FormAttachment(0, 29);
			brandNameFieldData.right = new FormAttachment(100, -10);
			brandNameField.setLayoutData(brandNameFieldData);
			setControl(comp);
		}
	}
}
