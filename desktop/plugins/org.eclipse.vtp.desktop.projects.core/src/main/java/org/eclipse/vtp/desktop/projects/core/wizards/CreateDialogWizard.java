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

import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * This wizard walks the user through the steps required to create a new
 * persona project in the eclipse workspace.  The user is prompted to
 * enter a name for the new persona.  This name must be unique
 * among the current projects in the workspace, not just other personas.
 * The persona project is automatically created by this wizard and so
 * requires no actions from the caller of the wizard.
 *
 * @author Trip
 */
public class CreateDialogWizard extends Wizard implements INewWizard,
	IExecutableExtension
{
	/**
	 * The wizard page that collects the name of the new persona.
	 */
	private DialogInformationPage personaInformationPage = null;

	IContainer targetFileContainer;

	/**
	 * Creates a new <code>CreatePersonaWizard</code> instance with
	 * default values.
	 */
	public CreateDialogWizard()
	{
		super();
		personaInformationPage = new DialogInformationPage();
		addPage(personaInformationPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		String fname = personaInformationPage.fileName.getText();
		if(!fname.endsWith(".dialog"))
			fname = fname + ".dialog";
		IFile newFile = targetFileContainer.getFile(targetFileContainer.getProjectRelativePath().append("/" + fname));
		try
		{
			InputStream in = getClass().getClassLoader().getResourceAsStream("dialog_template.xml");
			newFile.create(in, true, null);
			in.close();
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			  .getActivePage()
			  .openEditor(new FileEditorInput(newFile),
					  "org.eclipse.vtp.desktop.editors.core.dialogeditor", true);
		}
		catch(Exception ce)
		{
			ce.printStackTrace();
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		Object sel = selection.getFirstElement();
		if(!(sel instanceof IResource))
		{
			if(sel instanceof IAdaptable)
				sel = ((IAdaptable)sel).getAdapter(IResource.class);
			else
				throw new RuntimeException("don't know what to do with what was selected!");
		}
		IResource resource = (IResource)sel;
		if(!(resource instanceof IContainer))
			resource = resource.getParent();
		this.targetFileContainer = (IContainer)resource;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement cfig,
		String propertyName, Object data)
	{
	}

	private class DialogInformationPage extends WizardPage
	{
		Text dialogName;
		Text fileName;

		public DialogInformationPage()
		{
			super("DialogInformationPage", "Dialog Information", null);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			setPageComplete(false);

			Composite comp = new Composite(parent, SWT.NONE);
			GridLayout gl = new GridLayout();
			gl.numColumns = 2;
			gl.makeColumnsEqualWidth = false;
			comp.setLayout(gl);

			Label nameLabel = new Label(comp, SWT.NONE);
			nameLabel.setText("Dialog Name:");
			dialogName = new Text(comp, SWT.SINGLE | SWT.BORDER);

			Label fileLabel = new Label(comp, SWT.NONE);
			fileLabel.setText("File:");
			fileName = new Text(comp, SWT.SINGLE | SWT.BORDER);
			fileName.addKeyListener(new KeyListener()
			{
				public void keyPressed(KeyEvent e)
				{
				}

				public void keyReleased(KeyEvent e)
				{
					String fname = fileName.getText();
					if(!fname.endsWith(".dialog"))
						fname = fname + ".dialog";
					try
					{
						boolean ok = true;
						IResource[] memebers = targetFileContainer.members();
	
						for(int i = 0; i < memebers.length; i++)
						{
							if(memebers[i].getName()
										   .equalsIgnoreCase(fname))
							{
								setPageComplete(false);
								setErrorMessage(
									"There is already a dialog with that name.");
								ok = false;
							}
						}
	
						if(ok)
						{
							setPageComplete(true);
							setErrorMessage(null);
						}
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});
			fileName.addVerifyListener(new VerifyListener()
			{

				public void verifyText(VerifyEvent e)
                {
					String currentName = fileName.getText().substring(0, e.start) + e.text + fileName.getText(e.end, (fileName.getText().length() - 1));
	                if(currentName.length() > 255)
	                {
	                	e.doit = false;
	                	return;
	                }
                }
				
			});

			nameLabel.setLayoutData(new GridData());
			dialogName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fileLabel.setLayoutData(new GridData());
			fileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.setControl(comp);
		}
	}
}
