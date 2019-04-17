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

import java.util.LinkedList;
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.internal.branding.Brand;
import org.eclipse.vtp.desktop.model.core.internal.branding.DefaultBrandManager;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionTypeManager;
import org.eclipse.vtp.desktop.model.interactive.core.internal.InteractionTypeSupport;
import org.eclipse.vtp.desktop.model.interactive.core.internal.InteractiveWorkflowProject;
import org.eclipse.vtp.desktop.model.interactive.core.natures.InteractiveWorkflowProjectNature;
import org.eclipse.vtp.desktop.projects.core.util.BrandConfigurationScreen;
import org.eclipse.vtp.desktop.projects.core.util.ConfigurationBrandManager;
import org.eclipse.vtp.desktop.projects.interactive.core.util.InteractionSupportManager;
import org.eclipse.vtp.desktop.projects.interactive.core.util.InteractionTypeConfigurationScreen;
import org.eclipse.vtp.desktop.projects.interactive.core.util.LanguageConfigurationScreen;
import org.eclipse.vtp.framework.util.Guid;

/**
 * This wizard walks the user through the steps required to create
 * a new OpenVXML voice application project.
 *
 * <b>Step 1)</b> Enter a name for the application.<br>
 * The name must be unique among the other projects contained in the
 * eclipse workspace.  The user will not be able to move to the next
 * step until the name is entered and is unique.
 * <br>
 * <b>Step 2)</b> Determine supported languages.<br>
 * A persona project must be associated with each language the application
 * will support.<br>
 *
 * Note: Currently all applications must support both English and Spanish.
 *
 * The new project is created by this wizard automatically and requires
 * nothing of the caller of the wizard.
 *
 * @author Trip
 * @version 1.0
 */
public class CreateInteractiveWorkflowProjectWizard extends Wizard implements INewWizard,
	IExecutableExtension
{
	/**
	 * Wizard page that collects the name of the new application project.
	 */
	ApplicationPage applicationPage = null;
	
	/**
	 * Wizard page that configures the build path of the new application project.
	 */
	BuildPathPage buildPathPage = null;
	InteractionTypePage interactionTypePage = null;
	LanguagePage languagePage = null;

	IConfigurationElement configElement = null;

	private ConfigurationBrandManager brandManager = null;
	private InteractionSupportManager supportManager = null;

	/**
	 * Creates a new <code>CreateApplicationWizard</code> instance with default
	 * values.
	 */
	public CreateInteractiveWorkflowProjectWizard()
	{
		super();
		DefaultBrandManager defaultManager = new DefaultBrandManager();
		defaultManager.setDefaultBrand(new Brand(Guid.createGUID(), "Default"));
		brandManager = new ConfigurationBrandManager(defaultManager);
		List<InteractionTypeSupport> supportList = new LinkedList<InteractionTypeSupport>();
		List<InteractionType> installedTypes = InteractionTypeManager.getInstance().getInteractionTypes();
		for(InteractionType installedType : installedTypes)
		{
			InteractionTypeSupport typeSupport = new InteractionTypeSupport(installedType.getId(), installedType.getName());
			supportList.add(typeSupport);
			typeSupport.addLanguageSupport("English");
		}
		supportManager = new InteractionSupportManager();
		supportManager.init(supportList);
		this.applicationPage = new ApplicationPage();
		this.buildPathPage= new BuildPathPage();
		this.interactionTypePage = new InteractionTypePage();
		this.languagePage = new LanguagePage();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages()
	{
		addPage(applicationPage);
		addPage(buildPathPage);
		addPage(interactionTypePage);
		addPage(languagePage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		InteractiveWorkflowProject project = (InteractiveWorkflowProject)WorkflowCore.getDefault().getWorkflowModel().createWorkflowProject(InteractiveWorkflowProjectNature.NATURE_ID, applicationPage.nameField.getText());
		buildPathPage.configureBuildPath(project);
		project.setInteractionTypeSupport(supportManager.getSupport());
		BasicNewProjectResourceWizard.updatePerspective(configElement);
		return true;
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
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	public boolean canFinish(){
		return applicationPage.isPageComplete();//buildPathPage.equals(getContainer().getCurrentPage());
	}

	public class ApplicationPage extends WizardPage
	{
		Text nameField = null;

		public ApplicationPage()
		{
			super("CreateApplicationPage", "Create Application", null);
			this.setPageComplete(false);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			setControl(comp);

			Label hostLabel = new Label(comp, SWT.NONE);
			hostLabel.setBackground(comp.getBackground());
			hostLabel.setText("Application Name:");
			hostLabel.setSize(hostLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			nameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
			nameField.addVerifyListener(new VerifyListener()
			{

				public void verifyText(VerifyEvent e)
                {
	                String text = e.text;
	                char[] chars = text.toCharArray();
					String currentName = nameField.getText().substring(0, e.start) + e.text + nameField.getText(e.end, (nameField.getText().length() - 1));
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
			nameField.addKeyListener(new KeyListener()
				{
					public void keyPressed(KeyEvent e)
					{
					}

					public void keyReleased(KeyEvent e)
					{
						if(nameField.getText().length() == 0)
						{
							setPageComplete(false);
						}
						else
						{
							IProject[] existingProjects =
								ResourcesPlugin.getWorkspace().getRoot()
											   .getProjects();

							for(int i = 0; i < existingProjects.length; i++)
							{
								if(nameField.getText()
												.equalsIgnoreCase(existingProjects[i]
											.getName()))
								{
									setPageComplete(false);
									setErrorMessage(
										"Another project already exists with that name.");

									return;
								}
							}

							setPageComplete(true);
							setErrorMessage(null);
						}
						ApplicationPage.this.getWizard().getContainer().updateButtons();
					}
				});

			FormLayout formLayout = new FormLayout();
			formLayout.marginHeight = 10;
			formLayout.marginWidth = 10;
			comp.setLayout(formLayout);

			FormData hostLabelFormData = new FormData();
			hostLabelFormData.left = new FormAttachment(0, 10);
			hostLabelFormData.top = new FormAttachment(0, 10);
			hostLabelFormData.right = new FormAttachment(0,
					10 + hostLabel.getSize().x);
			hostLabelFormData.bottom = new FormAttachment(0,
					10 + hostLabel.getSize().y);
			hostLabel.setLayoutData(hostLabelFormData);

			FormData hostFieldFormData = new FormData();
			hostFieldFormData.left = new FormAttachment(hostLabel, 6);
			hostFieldFormData.top = new FormAttachment(0, 10);
			hostFieldFormData.right = new FormAttachment(100, -10);
			nameField.setLayoutData(hostFieldFormData);
		}

		/**
		 * @return
		 */
		public String getApplicationName()
		{
			return nameField.getText();
		}
	
	}

	public class BuildPathPage extends WizardPage
	{
		BrandConfigurationScreen screen = new BrandConfigurationScreen();

		public BuildPathPage()
		{
			super("ConfigureBuildPathPage", "Branding", null);
			screen.init(brandManager);
			this.setPageComplete(true);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new FillLayout());
			setControl(comp);
			screen.createContents(comp);
		}

		/**
		 * @param project
		 */
		void configureBuildPath(IWorkflowProject project) {
			brandManager.saveTo(project.getBrandManager(), true);
		}
		
	}

	public class InteractionTypePage extends WizardPage
	{
		InteractionTypeConfigurationScreen screen = new InteractionTypeConfigurationScreen();

		public InteractionTypePage()
		{
			super("InteractionTypePage", "Interaction Type Support", null);
			screen.setSupport(supportManager);
			this.setPageComplete(true);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new FillLayout());
			setControl(comp);
			screen.createContents(comp);
		}

		/**
		 * @param project
		 */
		void configureBuildPath(InteractiveWorkflowProject project)
		{
		}
		
	}

	public class LanguagePage extends WizardPage
	{
		LanguageConfigurationScreen screen = new LanguageConfigurationScreen();

		public LanguagePage()
		{
			super("LanguagePage", "Languge Support", null);
			screen.init(brandManager, supportManager);
			this.setPageComplete(true);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new FillLayout());
			setControl(comp);
			screen.createContents(comp);
		}

		/**
		 * @param project
		 */
		void configureBuildPath(InteractiveWorkflowProject project)
		{
		}
		
	}
}
