package org.eclipse.vtp.modules.interactive.ui.properties;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
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

@SuppressWarnings("restriction")
public class CombinedMediaPropertiesPanel extends DesignElementPropertiesPanel implements MediaConfigurationScreenContainer
{
	StackLayout stackLayout = null;
	Map<String, MediaConfigurationScreen> screensByType;
	Composite comp = null;
	FormToolkit toolkit = null;
	Text nameField = null;
	Button secureElementButton = null;
	
	public CombinedMediaPropertiesPanel(String name, IDesignElement element)
	{
		super(name, element);
		PrimitiveElement pe = (PrimitiveElement)element;
		screensByType = MediaConfigurationScreenManager.getInstance().getIndexedMediaConfigurationScreens(pe.getSubTypeId(), this);
	}

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
		
		PrimitiveElement pe = (PrimitiveElement)getElement();
		if(ISecurableElement.class.isAssignableFrom(pe.getInformationProvider().getClass()))
		{
			secureElementButton = new Button(mainComp, SWT.CHECK);
			secureElementButton.setText("This element may contain sensitive data and should be secured");
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;
			secureElementButton.setLayoutData(gridData);
			secureElementButton.setSelection(((ISecurableElement)pe.getInformationProvider()).isSecured());
		}
		
		comp = new Composite(mainComp, SWT.NONE);
		comp.setBackground(parent.getBackground());
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalIndent = 20;
		gridData.horizontalSpan = 2;
		gridData.widthHint = 400;
		gridData.heightHint = 300;
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

	public void save()
	{
		PrimitiveElement pe = (PrimitiveElement)getElement();
		getElement().setName(nameField.getText());
		if(ISecurableElement.class.isAssignableFrom(pe.getInformationProvider().getClass()))
		{
			((ISecurableElement)pe.getInformationProvider()).setSecured(secureElementButton.getSelection());
		}
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
