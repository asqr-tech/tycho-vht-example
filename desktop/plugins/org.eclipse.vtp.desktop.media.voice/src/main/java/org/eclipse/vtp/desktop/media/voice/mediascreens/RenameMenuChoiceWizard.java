package org.eclipse.vtp.desktop.media.voice.mediascreens;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.interactive.core.internal.MenuChoice;
import org.eclipse.vtp.modules.interactive.ui.properties.MenuChoiceBindingManager;

/**
 * This wizard walks the user through the steps required to rename an
 * option in a menu dialog. The user is prompted to enter the new name of the
 * menu option. The name must be unique among the current options of
 * the menu dialog. 
 *
 */
public class RenameMenuChoiceWizard extends Wizard
{
	/**
	 * The wizard page that collects the name of the menu option.
	 */
	private ApplicationPage page = null;

	private IBrand brand = null;

	private MenuChoice option = null;
	
	private MenuChoiceBindingManager mcBindingManager = null;
	
	/**
	 * Creates a new <code>RenameMenuChoiceWizard</code> for the given
	 * menu.
	 *
	 * @param menu The menu that will contain the new option.
	 */
	public RenameMenuChoiceWizard(IBrand brand, MenuChoice option, MenuChoiceBindingManager mcBindingManager)
	{
		super();
		this.page = new ApplicationPage();
		addPage(page);
		this.brand = brand;
		this.option = option;
		this.mcBindingManager = mcBindingManager;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		option.setOptionName(page.nameField.getText());
		return true;
	}


	private class ApplicationPage extends WizardPage
	{
		Text nameField = null;

		public ApplicationPage()
		{
			super("CreateApplicationPage", "Rename Menu Option", null);
			setPageComplete(false);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			setControl(parent);

			Label hostLabel = new Label(parent, SWT.NONE);
			hostLabel.setText("Option Name:");
			hostLabel.setSize(hostLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			nameField = new Text(parent, SWT.SINGLE | SWT.BORDER);
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
							List<MenuChoice> options = mcBindingManager.getChoicesByBrand(brand);
							for(MenuChoice mc : options)
							{
								if(nameField.getText()
												.equalsIgnoreCase(mc.getOptionName()))
								{
									setPageComplete(false);
									setErrorMessage(
										"Another option already exists with that name.");
									return;
								}
							}

							setPageComplete(true);
							setErrorMessage(null);
						}
					}
				});

			FormLayout formLayout = new FormLayout();
			formLayout.marginHeight = 10;
			formLayout.marginWidth = 10;
			parent.setLayout(formLayout);

			FormData hostLabelFormData = new FormData();
			hostLabelFormData.left = new FormAttachment(0, 10);
			hostLabelFormData.top = new FormAttachment(0, 13);
			hostLabelFormData.right = new FormAttachment(0,
					10 + hostLabel.getSize().x);
			hostLabelFormData.bottom = new FormAttachment(0,
					13 + hostLabel.getSize().y);
			hostLabel.setLayoutData(hostLabelFormData);

			FormData hostFieldFormData = new FormData();
			hostFieldFormData.left = new FormAttachment(hostLabel, 6);
			hostFieldFormData.top = new FormAttachment(0, 10);
			hostFieldFormData.right = new FormAttachment(100, -10);
			nameField.setLayoutData(hostFieldFormData);
		}
	}
}
