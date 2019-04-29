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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.ObjectField;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.branding.BrandContext;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.BrandBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.GenericBindingManager;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.InteractionBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.LanguageBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.NamedBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PropertyBindingItem;
import org.eclipse.vtp.desktop.model.interactive.core.internal.context.InteractionTypeContext;
import org.eclipse.vtp.desktop.model.interactive.core.internal.context.LanguageContext;

/**
 * The graphical user interface used to configure an advanced transfer module
 */
@SuppressWarnings("restriction")
public class AdvancedTransferGeneralPropertiesPanel extends DesignElementPropertiesPanel
{
	GenericBindingManager bindingManager;
	IBrand currentBrand;
	String currentLanguage;
	String interactionType;
	Label nameLabel;
	/** The text field used to set name of this particular Advanced Transfer module */
	Text nameField;
	Combo destinationType;
	Composite destinationComp;
	StackLayout destinationLayout;
	Composite destinationValueComp;
	Text destinationValue;
	Composite destinationExprComp;
	Text destinationExpr;
	Composite destinationTreeComp;
	TreeViewer destinationTree;
	List<Variable> variables = new ArrayList<Variable>();
	Label transferTypeLabel;
	Combo transferType;

	public AdvancedTransferGeneralPropertiesPanel(String name, IDesignElement ppe)
	{
		super(name, ppe);
		bindingManager = (GenericBindingManager)ppe.getConfigurationManager(GenericBindingManager.TYPE_ID);
		List<Variable> vars = ppe.getDesign().getVariablesFor(ppe);
outer:	for(Variable v : vars)
		{
			for(int i = 0; i < variables.size(); i++)
			{
				if(variables.get(i).getName().compareToIgnoreCase(v.getName()) > 0)
				{
					variables.add(i, v);
					continue outer;
				}
			}
			variables.add(v);
		}
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.ComponentPropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBackground(parent.getBackground());
		comp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		comp.setLayout(new GridLayout(2, false));
		nameLabel = new Label(comp, SWT.NONE);
		nameLabel.setText("Name: ");
		nameLabel.setBackground(comp.getBackground());
		nameLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		nameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getElement().getName());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		transferTypeLabel = new Label(comp, SWT.NONE);
		transferTypeLabel.setText("Transfer Type: ");
		transferTypeLabel.setLayoutData(new GridData());
		
		transferType = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SINGLE);
		transferType.setText("Transfer Type: ");
		transferType.add("Blind");
		transferType.add("Bridge");
		transferType.add("Consultation");
		transferType.select(0);
		transferType.setLayoutData(new GridData());
		
		Label valueLabel = new Label(comp, SWT.NONE);
		valueLabel.setText("Destination: ");
		valueLabel.setBackground(comp.getBackground());

		GridData gd3 = new GridData();
		gd3.verticalAlignment = SWT.TOP;
		valueLabel.setLayoutData(gd3);
		
		Composite dc2 = new Composite(comp, SWT.NONE);
		dc2.setBackground(comp.getBackground());
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		dc2.setLayout(gridLayout);
		GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		dc2.setLayoutData(gd2);

		destinationType = new Combo(dc2, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gd4 = new GridData();
		gd4.verticalAlignment = SWT.TOP;
		destinationType.setLayoutData(gd4);
		destinationType.add("Value");
		destinationType.add("Expression");
		destinationType.add("Variable");
		destinationType.select(0);
		destinationType.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				destinationTypeChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
		destinationComp = new Composite(dc2, SWT.NONE);
		destinationComp.setBackground(comp.getBackground());
		destinationComp.setLayout(destinationLayout = new StackLayout());
		destinationComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		destinationValueComp = new Composite(destinationComp, SWT.NONE);
		destinationValueComp.setBackground(destinationComp.getBackground());
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 0;
		destinationValueComp.setLayout(layout);
		destinationValue = new Text(destinationValueComp, SWT.SINGLE | SWT.BORDER);
		destinationValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		destinationExprComp = new Composite(destinationComp, SWT.NONE);
		destinationExprComp.setBackground(destinationComp.getBackground());
		layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 0;
		destinationExprComp.setLayout(layout);
		destinationExpr = new Text(destinationExprComp, SWT.SINGLE | SWT.BORDER);
		destinationExpr.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		destinationTreeComp = new Composite(destinationComp, SWT.NONE);
		destinationTreeComp.setBackground(destinationComp.getBackground());
		FormLayout fl = new FormLayout();
		destinationTreeComp.setLayout(fl);
		destinationTree = new TreeViewer(destinationTreeComp, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER | SWT.SINGLE);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0);
		fd.top = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.bottom = new FormAttachment(100);
		fd.height = 175;
		destinationTree.getControl().setLayoutData(
				fd);
		destinationTree.setContentProvider(new VariableContentProvider());
		destinationTree.setLabelProvider(new VariableLabelProvider());
		destinationTree.setInput(this);
		destinationLayout.topControl = destinationValueComp;
		destinationComp.layout();
		setControl(comp);
	}

	/**
	 * Sets which controls are visible based on the destination type
	 */
	private void destinationTypeChanged()
	{
		switch (destinationType.getSelectionIndex())
		{
		case 2:
			destinationLayout.topControl = destinationTreeComp;
			break;
		case 1:
			destinationLayout.topControl = destinationExprComp;
			break;
		default:
			destinationLayout.topControl = destinationValueComp;
		}
		destinationComp.layout();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#setConfigurationContext(org.eclipse.vtp.desktop.core.configuration.Brand, java.lang.String, java.lang.String)
	 */
	public void setConfigurationContext(Map<String, Object> values)
	{
		if(!(currentBrand == null))
		{
			storeBindings();
		}
		
		currentBrand = (IBrand)values.get(BrandContext.CONTEXT_ID);
		currentLanguage = (String)values.get(LanguageContext.CONTEXT_ID);
		Object object = values.get(InteractionTypeContext.CONTEXT_ID);
		if(currentBrand == null || currentLanguage == null || object == null)
		{
			final IWorkflowProject project = getElement().getDesign().getDocument().getProject();
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
		this.interactionType = ((InteractionType)object).getId();
		int typeIndex = 0;
		InteractionBinding interactionBinding = bindingManager.getInteractionBinding(interactionType);
		NamedBinding namedBinding = interactionBinding.getNamedBinding("type");
		LanguageBinding languageBinding = namedBinding.getLanguageBinding(currentLanguage);
		BrandBinding brandBinding = languageBinding.getBrandBinding(currentBrand);
		PropertyBindingItem typePropertyItem = (PropertyBindingItem)brandBinding.getBindingItem();
		if(typePropertyItem == null)
			typePropertyItem = new PropertyBindingItem();
		if ("variable".equalsIgnoreCase(typePropertyItem.getValue()))
			typeIndex = 2;
		else if ("expression".equalsIgnoreCase(typePropertyItem.getValue()))
			typeIndex = 1;
		destinationType.select(typeIndex);
		namedBinding = interactionBinding.getNamedBinding("destination");
		if(namedBinding.getLanguageBinding(currentLanguage).getBrandBinding(currentBrand).getBindingItem() == null)
		{
			languageBinding = namedBinding.getLanguageBinding("");
		}
		else
		{
			languageBinding = namedBinding.getLanguageBinding(currentLanguage);
		}
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		PropertyBindingItem valuePropertyItem = (PropertyBindingItem)brandBinding.getBindingItem();
		if(valuePropertyItem == null)
			valuePropertyItem = new PropertyBindingItem();
		if (valuePropertyItem.getValue() != null)
		{
			switch (typeIndex)
			{
			case 2:
				ObjectDefinition od =
					getObjectDefinitionFromVariables(valuePropertyItem.getValue());
				StructuredSelection ss =
					(od == null) ? StructuredSelection.EMPTY
								 : new StructuredSelection(od);
				destinationTree.setSelection(ss);
				break;
			case 1:
				destinationExpr.setText(valuePropertyItem.getValue());
				break;
			default:
				destinationValue.setText(valuePropertyItem.getValue());
			}
		}
		destinationTypeChanged();
		namedBinding = interactionBinding.getNamedBinding("transferType");
		languageBinding = namedBinding.getLanguageBinding(currentLanguage);
		brandBinding = languageBinding.getBrandBinding(currentBrand);

		PropertyBindingItem transferTypePropertyItem = (PropertyBindingItem)brandBinding.getBindingItem();
		if(transferTypePropertyItem == null)
			transferTypePropertyItem = new PropertyBindingItem();
		if (transferTypePropertyItem.getValue() != null)
		{
			if(transferTypePropertyItem.getValue().equals("bridge"))
				transferType.select(1);
			else if(transferTypePropertyItem.getValue().equals("consultation"))
				transferType.select(2);
			else
				transferType.select(0);
		}
		else
			transferType.select(0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#save()
	 */
	public void save()
	{
		try
		{
			getElement().setName(nameField.getText());
			storeBindings();
			getElement().commitConfigurationChanges(bindingManager);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ComponentPropertiesPanel#cancel()
	 */
	public void cancel()
	{
		getElement().rollbackConfigurationChanges(bindingManager);
	}

	/**
	 * @param name
	 * @return
	 */
	public ObjectDefinition getObjectDefinitionFromVariables(String name)
	{
		ObjectDefinition ret = null;
		List<Variable> vars = getElement().getDesign().getVariablesFor(getElement());

		for(int i = 0; i < vars.size(); i++)
		{
			String varName = name;
			boolean sub = false;

			if(name.indexOf(".") != -1)
			{
				varName = name.substring(0, name.indexOf("."));
				sub = true;
			}

			Variable v = vars.get(i);

			if(v.getName().equals(varName))
			{
				if(sub)
				{
					//dig deeper
					ret = getObjectDefinitionFromFields(name.substring(name
								.indexOf(".") + 1), v);
				}
				else
				{
					ret = v;

					break;
				}
			}
		}

		return ret;
	}

	/**
	 * @param name
	 * @param parent
	 * @return
	 */
	public ObjectDefinition getObjectDefinitionFromFields(String name,
		ObjectDefinition parent)
	{
		ObjectDefinition ret = null;
		List<ObjectField> fields = parent.getFields();

		for(int i = 0; i < fields.size(); i++)
		{
			String varName = name;
			boolean sub = false;

			if(name.indexOf('.') != -1)
			{
				varName = name.substring(0, name.indexOf("."));
				sub = true;
			}

			ObjectField of = fields.get(i);

			if(of.getName().equals(varName))
			{
				if(sub)
				{
					ret = getObjectDefinitionFromFields(name.substring(name
								.indexOf(".") + 1), of);
				}
				else
				{
					ret = of;

					break;
				}
			}
		}

		return ret;
	}

	private void storeBindings()
	{
		try
		{
			String type, value;
			switch (destinationType.getSelectionIndex())
			{
			case 2:
				type = "variable";
				value = "";
				ISelection selection = destinationTree.getSelection();
				if((selection != null) && !selection.isEmpty()
						&& selection instanceof IStructuredSelection)
				{
					Object selObj = ((IStructuredSelection)selection).getFirstElement();
					if(selObj instanceof ObjectDefinition)
						value = ((ObjectDefinition)selObj).getPath();
				}
				break;
			case 1:
				type = "expression";
				value = destinationExpr.getText();
				break;
			default:
				type = "value";
				value = destinationValue.getText();
			}
			InteractionBinding interactionBinding = bindingManager.getInteractionBinding(interactionType);
			NamedBinding namedBinding = interactionBinding.getNamedBinding("type");
			LanguageBinding languageBinding = namedBinding.getLanguageBinding(currentLanguage);
			BrandBinding brandBinding = languageBinding.getBrandBinding(currentBrand);
			PropertyBindingItem typePropertyItem = (PropertyBindingItem)brandBinding.getBindingItem();
			if(typePropertyItem == null)
				typePropertyItem = new PropertyBindingItem();
			else
				typePropertyItem = (PropertyBindingItem)typePropertyItem.clone();
			typePropertyItem.setValue(type);
			brandBinding.setBindingItem(typePropertyItem);

			namedBinding = interactionBinding.getNamedBinding("destination");
			languageBinding = namedBinding.getLanguageBinding(currentLanguage);
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			PropertyBindingItem valuePropertyItem = (PropertyBindingItem)brandBinding.getBindingItem();
			if(valuePropertyItem == null)
				valuePropertyItem = new PropertyBindingItem();
			else
				valuePropertyItem = (PropertyBindingItem)valuePropertyItem.clone();
			valuePropertyItem.setValue(value);
			brandBinding.setBindingItem(valuePropertyItem);
			
			namedBinding = interactionBinding.getNamedBinding("transferType");
			languageBinding = namedBinding.getLanguageBinding(currentLanguage);
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			PropertyBindingItem transferTypePropertyItem = (PropertyBindingItem)brandBinding.getBindingItem();
			if(transferTypePropertyItem == null)
				transferTypePropertyItem = new PropertyBindingItem();
			else
				transferTypePropertyItem = (PropertyBindingItem)transferTypePropertyItem.clone();
			switch(transferType.getSelectionIndex())
			{
			case(1):
				transferTypePropertyItem.setValue("bridge");
				break;
			case(2):
				transferTypePropertyItem.setValue("consultation");
				break;
			default:
				transferTypePropertyItem.setValue("blind");
				break;
			}
			brandBinding.setBindingItem(transferTypePropertyItem);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * VariableContentProvider.
	 * 
	 * @author Lonnie Pryor
	 */
	public class VariableContentProvider implements ITreeContentProvider
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement)
		{
			if (parentElement instanceof ObjectDefinition)
			{
				ObjectDefinition v = (ObjectDefinition)parentElement;

				return v.getFields().toArray();
			}

			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element)
		{
			if (element instanceof Variable)
			{
				return null;
			}
			else
			{
				return ((ObjectField)element).getParent();
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element)
		{
			return ((ObjectDefinition)element).getFields().size() > 0;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return variables.toArray();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	/**
	 * VariableLabelProvider.
	 * 
	 * @author Lonnie Pryor
	 */
	public class VariableLabelProvider implements ILabelProvider
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element)
		{
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element)
		{
			return ((ObjectDefinition)element).getName();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener)
		{
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose()
		{
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property)
		{
			return true;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener)
		{
		}
	}

	public List<String> getApplicableContexts()
	{
		List<String> ret = super.getApplicableContexts();
		ret.add(LanguageContext.CONTEXT_ID);
		ret.add(InteractionTypeContext.CONTEXT_ID);
		return ret;
	}
}
