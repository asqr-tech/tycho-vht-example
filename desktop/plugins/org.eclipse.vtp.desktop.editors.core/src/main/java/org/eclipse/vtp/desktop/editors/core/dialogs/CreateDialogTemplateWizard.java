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
package org.eclipse.vtp.desktop.editors.core.dialogs;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.desktop.model.elements.core.Activator;
import org.eclipse.vtp.desktop.model.elements.core.internal.DialogElement;

/**
 * This wizard walks the user through the steps required to create a new
 * folder in the Media Files section of an application.  The user is
 * prompted to enter a name for the new folder.  This name must be unique
 * among the current folders in the current directory.  The folder is
 * automatically created by this wizard and so requires no actions from
 * the caller of the wizard.
 *
 * @author Trip
 */
public class CreateDialogTemplateWizard extends Wizard
{
	/**
	 * The wizard page that collects the name of the new folder.
	 */
	private MediaFolderWizardPage mfwp = null;
	
	private DialogElement dialogElement = null;

	/**
	 * Creates a new <code>CreateMediaFolderWizard</code> instance for
	 * the given media container.
	 *
	 * @param container The media container that will contain the new folder.
	 */
	public CreateDialogTemplateWizard(DialogElement dialogElement)
	{
		super();
		this.dialogElement = dialogElement;
		mfwp = new MediaFolderWizardPage();
		addPage(mfwp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		try
		{
			IDesign dialogDesign = null;
			List<IDesign> dialogs = dialogElement.getDesign().getDocument().getDialogDesigns();
			for(IDesign dialog : dialogs)
			{
				if(dialog.getDesignId().equals(dialogElement.getId()))
				{
					dialogDesign = dialog;
					break;
				}
			}
			if(dialogDesign != null)
				Activator.getDefault().addLocalDialog(mfwp.mediaFolderNameField.getText(), (Design)dialogDesign);
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	private class MediaFolderWizardPage extends WizardPage
	{
		Text mediaFolderNameField = null;

		public MediaFolderWizardPage()
		{
			super("MediaFolderPage", "Enter a name for your dialog", null);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{

			Composite comp = new Composite(parent, SWT.NONE);
			Label folderNameLabel = new Label(comp, SWT.NONE);
			folderNameLabel.setText("Dialog Name:");
			folderNameLabel.setSize(folderNameLabel.computeSize(SWT.DEFAULT,
					SWT.DEFAULT));
			mediaFolderNameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
			comp.setLayout(new FormLayout());

			FormData folderNameLabelData = new FormData();
			folderNameLabelData.left = new FormAttachment(0, 10);
			folderNameLabelData.top = new FormAttachment(0, 30);
			folderNameLabelData.right = new FormAttachment(0,
					10 + folderNameLabel.getSize().x);
			folderNameLabelData.bottom = new FormAttachment(0,
					30 + folderNameLabel.getSize().y);
			folderNameLabel.setLayoutData(folderNameLabelData);

			FormData folderNameFieldData = new FormData();
			folderNameFieldData.left = new FormAttachment(folderNameLabel, 10);
			folderNameFieldData.top = new FormAttachment(0, 29);
			folderNameFieldData.right = new FormAttachment(100, -10);
			mediaFolderNameField.setLayoutData(folderNameFieldData);
			setControl(comp);
		}
	}
}
