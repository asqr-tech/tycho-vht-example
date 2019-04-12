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
package org.eclipse.vtp.desktop.projects.interactive.core.wizards;

import java.util.List;

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
import org.eclipse.vtp.desktop.model.interactive.core.IMediaContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFile;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFolder;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaResource;

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
public class CreateMediaFolderWizard extends Wizard implements INewWizard,
IExecutableExtension
{
	/**
	 * The media container (folder) that will contain the new folder.
	 */
	private IMediaContainer container = null;

	/**
	 * The wizard page that collects the name of the new folder.
	 */
	private MediaFolderWizardPage mfwp = null;

	IConfigurationElement configElement = null;
	/**
	 * Creates a new <code>CreateMediaFolderWizard</code> instance for
	 * the given media container.
	 *
	 * @param container The media container that will contain the new folder.
	 */
	public CreateMediaFolderWizard()
	{
		super();
		mfwp = new MediaFolderWizardPage();
		addPage(mfwp);
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
		Object sel = selection.getFirstElement();
		if(sel instanceof IMediaContainer)
			container = (IMediaContainer)sel;
		else if(sel instanceof IMediaFile)
			container = ((IMediaFile)sel).getParentMediaContainer();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		try
		{
			container.makeDirectory(mfwp.mediaFolderNameField.getText());

			return true;
		}
		catch(CoreException e)
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
			super("MediaFolderPage", "Enter a name for the folder", null);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			setPageComplete(false);

			Composite comp = new Composite(parent, SWT.NONE);
			Label folderNameLabel = new Label(comp, SWT.NONE);
			folderNameLabel.setText("Folder Name:");
			folderNameLabel.setSize(folderNameLabel.computeSize(SWT.DEFAULT,
					SWT.DEFAULT));
			mediaFolderNameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
			mediaFolderNameField.addModifyListener(new ModifyListener()
				{
					public void modifyText(ModifyEvent e)
					{
						String n = mediaFolderNameField.getText();
						List<IMediaResource> resources = null;

						try
						{
							resources = container.listMediaResources();
						}
						catch(CoreException e1)
						{
							e1.printStackTrace();
							setPageComplete(false);
							setErrorMessage(
								"Could not enumerate existing resources.");
							mediaFolderNameField.setEnabled(false);
						}

						for(IMediaResource resource : resources)
						{
							if(resource instanceof IMediaFolder)
							{
								if(resource.getName().equals(n))
								{
									setErrorMessage(
										"A folder already exists with that name.");
									setPageComplete(false);

									return;
								}
							}
						}

						setErrorMessage(null);
						setPageComplete(true);
					}
				});
			mediaFolderNameField.addVerifyListener(new VerifyListener()
			{

				public void verifyText(VerifyEvent e)
                {
					String currentName = mediaFolderNameField.getText().substring(0, e.start) + e.text + mediaFolderNameField.getText(e.end, (mediaFolderNameField.getText().length() - 1));
	                if(currentName.length() > 255)
	                {
	                	e.doit = false;
	                	return;
	                }
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

			FormData folderNameFieldData = new FormData();
			folderNameFieldData.left = new FormAttachment(folderNameLabel, 10);
			folderNameFieldData.top = new FormAttachment(0, 29);
			folderNameFieldData.right = new FormAttachment(100, -10);
			mediaFolderNameField.setLayoutData(folderNameFieldData);
			setControl(comp);
		}
	}
}
