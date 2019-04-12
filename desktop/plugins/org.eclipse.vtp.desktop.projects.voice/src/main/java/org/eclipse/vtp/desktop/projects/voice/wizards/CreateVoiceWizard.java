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
package org.eclipse.vtp.desktop.projects.voice.wizards;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.vtp.desktop.media.core.FormatterRegistration;
import org.eclipse.vtp.desktop.media.core.FormatterRegistrationManager;
import org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.voice.natures.VoiceProjectNature;

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
public class CreateVoiceWizard extends Wizard implements INewWizard,
	IExecutableExtension
{
	/**
	 * The wizard page that collects the name of the new persona.
	 */
	private PersonaInformationPage personaInformationPage = null;

	/**
	 * Allows access to the eclipse workbench configuration.
	 */
	private IConfigurationElement configElement = null;
	
	private List<FormatterRegistration> formatters = null;

	/**
	 * Creates a new <code>CreateVoiceWizard</code> instance with
	 * default values.
	 */
	public CreateVoiceWizard()
	{
		super();
		this.setWindowTitle("New Voice Wizard");
		formatters = FormatterRegistrationManager.getInstance().getFormattersForInteractionType("org.eclipse.vtp.framework.interactions.voice.interaction");
		personaInformationPage = new PersonaInformationPage();
		addPage(personaInformationPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		FormatterRegistration fr = formatters.get(personaInformationPage.languagePackSelector.getSelectionIndex());
		InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().createMediaProject(VoiceProjectNature.NATURE_ID, fr.getId(), personaInformationPage.personaName
			.getText());
		BasicNewProjectResourceWizard.updatePerspective(configElement);
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement cfig,
		String propertyName, Object data)
	{
		configElement = cfig;
	}

	private class PersonaInformationPage extends WizardPage
	{
		Text personaName;
		Combo languagePackSelector;
//		List languagePacks = new ArrayList();

		public PersonaInformationPage()
		{
			super("VoiceInformationPage", "Voice Information", null);
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
			nameLabel.setText("Name:");
			personaName = new Text(comp, SWT.SINGLE | SWT.BORDER);
			personaName.addKeyListener(new KeyListener()
				{
					public void keyPressed(KeyEvent e)
					{
					}

					public void keyReleased(KeyEvent e)
					{
						if(personaName.getText().length() == 0)
						{
							setPageComplete(false);
						}
						else
						{
							IProject[] projs =
								ResourcesPlugin.getWorkspace().getRoot()
								.getProjects();
							
							for(int i = 0; i < projs.length; i++)
							{
								if(projs[i].getName()
										.equalsIgnoreCase(personaName.getText()))
								{
									setPageComplete(false);
									setErrorMessage(
									"There is already a project with that name.");
									return;
								}
							}
							setPageComplete(true);
							setErrorMessage(null);
						}
					}
				});
			personaName.addVerifyListener(new VerifyListener()
			{

				public void verifyText(VerifyEvent e)
                {
	                String text = e.text;
	                char[] chars = text.toCharArray();
					String currentName = personaName.getText().substring(0, e.start) + e.text + personaName.getText(e.end, (personaName.getText().length() - 1));
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
	                	e.doit = false;
	                	return;
	                }

                }
				
			});


			Label languageLabel = new Label(comp, SWT.NONE);
			languageLabel.setText("Formatter:");
			languagePackSelector = new Combo(comp,
					SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
			nameLabel.setLayoutData(new GridData());
			personaName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			languageLabel.setLayoutData(new GridData());
			languagePackSelector.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			setControl(comp);
			for(FormatterRegistration fr : formatters)
			{
				languagePackSelector.add(fr.getName() + "(" + fr.getVendor() + ")");
			}
			languagePackSelector.select(0);
		}
	}
}
