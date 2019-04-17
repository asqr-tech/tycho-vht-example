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
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;

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
public class CreateWorkflowDesignDocumentWizard extends Wizard implements INewWizard,
IExecutableExtension
{
	/**
	 * The business object set that will contain the new business object.
	 */
	private IDesignItemContainer designItemContainer = null;

	/**
	 * The wizard page that collects the name of the new business object.
	 */
	private DesignDocumentWizardPage bwp = null;

	IConfigurationElement configElement = null;
	
	/**
	 * Creates a new <code>CreateBusinessObjectWizard</code> instance for
	 * the given business object set.
	 *
	 * @param objectSet The business object set that will contain the new
	 * business object.
	 */
	public CreateWorkflowDesignDocumentWizard()
	{
		super();
		bwp = new DesignDocumentWizardPage();
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
		if(obj instanceof IDesignItemContainer)
			this.designItemContainer = (IDesignItemContainer)obj;
		else if(obj instanceof IDesignDocument)
			this.designItemContainer = ((IDesignDocument)obj).getParentDesignContainer();
		else if(obj instanceof IWorkflowProject)
			this.designItemContainer = ((IWorkflowProject)obj).getDesignRootFolder();
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		IDesignDocument bo = designItemContainer.createDesignDocument(bwp.documentNameField.getText());
		if(bo != null)
		{
			designItemContainer.refresh();
			return true;
		}
		return false;
	}

	private class DesignDocumentWizardPage extends WizardPage
	{
		Text documentNameField = null;

		public DesignDocumentWizardPage()
		{
			super("DesignDocumentPage",
				"Enter a name for the new design document.  The \".canvas\" file extension will automatically be added to the name you provide.", null);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			setPageComplete(false);

			Composite comp = new Composite(parent, SWT.NONE);
			Label folderNameLabel = new Label(comp, SWT.NONE);
			folderNameLabel.setText("Name:");
			folderNameLabel.setSize(folderNameLabel.computeSize(SWT.DEFAULT,
					SWT.DEFAULT));
			documentNameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
			documentNameField.addVerifyListener(new VerifyListener()
			{

				public void verifyText(VerifyEvent e)
                {
	                String text = e.text;
	                char[] chars = text.toCharArray();
					String currentName = documentNameField.getText().substring(0, e.start) + e.text + documentNameField.getText(e.end, (documentNameField.getText().length() - 1));
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
			documentNameField.addModifyListener(new ModifyListener()
				{
					public void modifyText(ModifyEvent e)
					{
						String n = documentNameField.getText();
						for(IWorkflowResource wr : designItemContainer.getChildren())
						{
							if(wr.getName().equals(n))
							{
								setErrorMessage(
									"A design document already exists with that name.");
								setPageComplete(false);

								return;
							}
						}

						setErrorMessage(null);
						setPageComplete(true);
					}
				});
			comp.setLayout(new FormLayout());

			FormData folderNameLabelData = new FormData();
			folderNameLabelData.left = new FormAttachment(0, 10);
			folderNameLabelData.top = new FormAttachment(0, 30);
			folderNameLabelData.right = new FormAttachment(0,
					10 + folderNameLabel.getSize().x);
			folderNameLabelData.bottom = new FormAttachment(0,
					30 + folderNameLabel.getSize().y);
			folderNameLabel.setLayoutData(folderNameLabelData);

			FormData brandNameFieldData = new FormData();
			brandNameFieldData.left = new FormAttachment(folderNameLabel, 10);
			brandNameFieldData.top = new FormAttachment(0, 29);
			brandNameFieldData.right = new FormAttachment(100, -10);
			documentNameField.setLayoutData(brandNameFieldData);
			setControl(comp);
		}
	}
}
