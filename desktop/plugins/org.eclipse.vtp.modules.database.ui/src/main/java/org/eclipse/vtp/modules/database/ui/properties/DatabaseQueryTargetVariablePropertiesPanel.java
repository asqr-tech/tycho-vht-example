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
package org.eclipse.vtp.modules.database.ui.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.IBusinessObject;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.ObjectField;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.framework.util.VariableNameValidator;
import org.eclipse.vtp.modules.database.ui.DatabaseQueryInformationProvider;

/**
 * @author Trip
 *
 */
public class DatabaseQueryTargetVariablePropertiesPanel
	extends DesignElementPropertiesPanel
{
	DatabaseQueryInformationProvider queryElement;
	DatabaseQuerySettingsStructure settings;
	Button existingVariableButton;
	Button newVariableButton;
	TreeViewer existingVariableViewer;
	Text newVariableNameField;
	Combo multiplicityCombo;
	Combo typeCombo;
	List<String> typeList;
	List<Variable> incomingVariables;
	Button newVariableSecureButton;

	/**
	 * @param name
	 */
	public DatabaseQueryTargetVariablePropertiesPanel(
		org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement dqe, DatabaseQuerySettingsStructure settings)
	{
		super("Target Variable", dqe);
		this.queryElement = (DatabaseQueryInformationProvider)dqe.getInformationProvider();
		this.settings = settings;
		incomingVariables = dqe.getDesign().getVariablesFor(dqe);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.ComponentPropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{

		final Font bold = new Font(parent.getDisplay(), "Arial", 8, SWT.BOLD);
		parent.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent e)
				{
					bold.dispose();
				}
			});
		existingVariableButton = new Button(parent, SWT.RADIO);
		existingVariableButton.setText("Existing Variable");
		existingVariableButton.setSelection(settings.targetVariableExists);
		existingVariableButton.setFont(bold);
		existingVariableButton.setBackground(parent.getBackground());

		Tree existingVariableTree =
			new Tree(parent, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);
		existingVariableTree.setHeaderVisible(true);
		existingVariableTree.setEnabled(settings.targetVariableExists);

		TreeColumn nameColumn = new TreeColumn(existingVariableTree, SWT.NONE);
		nameColumn.setText("Variable Name");
		nameColumn.setWidth(100);

		TreeColumn typeColumn = new TreeColumn(existingVariableTree, SWT.NONE);
		typeColumn.setText("Type");
		typeColumn.setWidth(100);
		existingVariableViewer = new TreeViewer(existingVariableTree);
		existingVariableViewer.setContentProvider(new VariableContentProvider());
		existingVariableViewer.setLabelProvider(new VariableLabelProvider());
		existingVariableViewer.setInput(this);

		if(settings.targetVariableExists)
		{
			for(int i = 0; i < incomingVariables.size(); i++)
			{
				Variable v = incomingVariables.get(i);

				if(v.getName().equals(settings.targetVariableName))
				{
					existingVariableViewer.setSelection(new StructuredSelection(
							v));
				}
				else if((settings.targetVariableName != null)
						&& settings.targetVariableName.startsWith(v.getName()))
				{
					List<ObjectField> objectFields = v.getFields();

					for(int f = 0; f < objectFields.size(); f++)
					{
						ObjectField of = objectFields.get(f);

						if(of.getPath().equals(settings.targetVariableName))
						{
							existingVariableViewer.setSelection(new StructuredSelection(
									of));
						}
					}
				}
			}
		}

		newVariableButton = new Button(parent, SWT.RADIO);
		newVariableButton.setText("New Variable");
		newVariableButton.setSelection(!settings.targetVariableExists);
		newVariableButton.setFont(bold);
		newVariableButton.setBackground(parent.getBackground());

		Label newVariableNameLabel = new Label(parent, SWT.NONE);
		newVariableNameLabel.setText("Name:");
		newVariableNameLabel.setBackground(parent.getBackground());
		newVariableNameField = new Text(parent, SWT.SINGLE | SWT.BORDER);
		newVariableNameField.addVerifyListener(new VerifyListener()
		{
			public void verifyText(VerifyEvent e)
			{
				String currentName = newVariableNameField.getText().substring(0, e.start) + e.text + newVariableNameField.getText(e.end, (newVariableNameField.getText().length() - 1));
				if(VariableNameValidator.followsVtpNamingRules(currentName) || currentName.equals(""))
				{
					newVariableNameField.setForeground(newVariableNameField.getDisplay().getSystemColor(SWT.COLOR_BLACK));
					getContainer().setCanFinish(true);
				}
				else
				{
					newVariableNameField.setForeground(newVariableNameField.getDisplay().getSystemColor(SWT.COLOR_RED));
					getContainer().setCanFinish(false);
				}
			}
		});

		if(!settings.targetVariableExists)
		{
			newVariableNameField.setText(settings.targetVariableName);
		}

		newVariableNameField.setEnabled(!settings.targetVariableExists);
		newVariableNameField.addKeyListener(new KeyListener()
			{
				public void keyPressed(KeyEvent e)
				{
				}

				public void keyReleased(KeyEvent e)
				{
					settings.targetVariableName = newVariableNameField.getText();
					settings.fireTargetChanged();
				}
			});

		Label newVariableTypeLabel = new Label(parent, SWT.NONE);
		newVariableTypeLabel.setText("Type:");
		newVariableTypeLabel.setBackground(parent.getBackground());
		multiplicityCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		multiplicityCombo.add("One of");
		multiplicityCombo.add("Array of");
		multiplicityCombo.setEnabled(!settings.targetVariableExists);

		if(!settings.targetVariableExists)
		{
			multiplicityCombo.select(settings.targetVariableType.hasBaseType() ? 1 : 0);
		}
		else
		{
			multiplicityCombo.select(0);
		}

		multiplicityCombo.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					if(multiplicityCombo.getSelectionIndex() == 0)
					{
						Primitive prim = Primitive.find(typeList.get(typeCombo.getSelectionIndex()));
						if(prim != null)
						{
							settings.targetVariableType = new FieldType(prim);
						}
						else
							settings.targetVariableType = new FieldType(getElement().getDesign().getDocument().getProject().getBusinessObjectSet().getBusinessObject(typeList.get(typeCombo.getSelectionIndex())));
					}
					else
					{
						Primitive prim = Primitive.find(typeList.get(typeCombo.getSelectionIndex()));
						if(prim != null)
						{
							settings.targetVariableType = new FieldType(Primitive.ARRAY, prim);
						}
						else
							settings.targetVariableType = new FieldType(Primitive.ARRAY, getElement().getDesign().getDocument().getProject().getBusinessObjectSet().getBusinessObject(typeList.get(typeCombo.getSelectionIndex())));
					}
					settings.fireTargetChanged();
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		typeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeList = new ArrayList<String>();
		typeList.add("String");
		typeList.add("Number");
		typeList.add("Decimal");
		typeList.add("Boolean");
		typeList.add("DateTime");

		List<IBusinessObject> bol =
			getElement().getDesign().getDocument().getProject().getBusinessObjectSet().getBusinessObjects();

		for(int i = 0; i < bol.size(); i++)
		{
			typeList.add(bol.get(i).getName());
		}

		int sel = 0;

		for(int i = 0; i < typeList.size(); i++)
		{
			typeCombo.add(typeList.get(i));

			if(!settings.targetVariableExists)
			{
				if(typeList.get(i).equals(settings.targetVariableType.hasBaseType() ? settings.targetVariableType.getBaseTypeName() : settings.targetVariableType.getName()))
				{
					sel = i;
				}
			}
		}

		typeCombo.setEnabled(!settings.targetVariableExists);
		typeCombo.select(sel);
		typeCombo.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					if(multiplicityCombo.getSelectionIndex() == 0)
					{
						Primitive prim = Primitive.find(typeList.get(typeCombo.getSelectionIndex()));
						if(prim != null)
						{
							settings.targetVariableType = new FieldType(prim);
						}
						else
							settings.targetVariableType = new FieldType(getElement().getDesign().getDocument().getProject().getBusinessObjectSet().getBusinessObject(typeList.get(typeCombo.getSelectionIndex())));
					}
					else
					{
						Primitive prim = Primitive.find(typeList.get(typeCombo.getSelectionIndex()));
						if(prim != null)
						{
							settings.targetVariableType = new FieldType(Primitive.ARRAY, prim);
						}
						else
							settings.targetVariableType = new FieldType(Primitive.ARRAY, getElement().getDesign().getDocument().getProject().getBusinessObjectSet().getBusinessObject(typeList.get(typeCombo.getSelectionIndex())));
					}
					settings.fireTargetChanged();
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		
		newVariableSecureButton = new Button(parent, SWT.CHECK);
		newVariableSecureButton.setText("This variable should be secured.");
		newVariableSecureButton.setSelection(!settings.targetVariableExists && settings.isTargetVariableSecure());
		newVariableSecureButton.setEnabled(!settings.targetVariableExists);
		newVariableSecureButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
            {
            }

			public void widgetSelected(SelectionEvent e)
            {
				settings.setTargetVariableSecure(newVariableSecureButton.getSelection());
            }
		});

		parent.setLayout(new FormLayout());

		FormData existingVariableButtonData = new FormData();
		existingVariableButtonData.left = new FormAttachment(0, 20);
		existingVariableButtonData.top = new FormAttachment(0, 10);
		existingVariableButtonData.right = new FormAttachment(100, -10);
		existingVariableButton.setLayoutData(existingVariableButtonData);

		FormData existingVariableViewerData = new FormData();
		existingVariableViewerData.left = new FormAttachment(0, 20);
		existingVariableViewerData.top = new FormAttachment(existingVariableButton,
				10);
		existingVariableViewerData.right = new FormAttachment(100, -10);
		existingVariableViewerData.height = 80;
		existingVariableViewer.getControl()
							  .setLayoutData(existingVariableViewerData);

		FormData newVariableButtonData = new FormData();
		newVariableButtonData.left = new FormAttachment(0, 20);
		newVariableButtonData.top = new FormAttachment(existingVariableViewer
				.getControl(), 20);
		newVariableButtonData.right = new FormAttachment(100, -10);
		newVariableButton.setLayoutData(newVariableButtonData);

		FormData newVariableNameLabelData = new FormData();
		newVariableNameLabelData.left = new FormAttachment(0, 20);
		newVariableNameLabelData.top = new FormAttachment(newVariableButton, 12);
		newVariableNameLabel.setLayoutData(newVariableNameLabelData);

		FormData newVariableNameFieldData = new FormData();
		newVariableNameFieldData.left = new FormAttachment(newVariableNameLabel,
				10);
		newVariableNameFieldData.top = new FormAttachment(newVariableButton, 10);
		newVariableNameFieldData.right = new FormAttachment(100, -10);
		newVariableNameField.setLayoutData(newVariableNameFieldData);

		FormData newVariableTypeLabelData = new FormData();
		newVariableTypeLabelData.left = new FormAttachment(0, 20);
		newVariableTypeLabelData.top = new FormAttachment(newVariableNameField,
				10);
		newVariableTypeLabelData.right = new FormAttachment(100, -10);
		newVariableTypeLabel.setLayoutData(newVariableTypeLabelData);

		FormData multiplicityComboData = new FormData();
		multiplicityComboData.left = new FormAttachment(0, 20);
		multiplicityComboData.top = new FormAttachment(newVariableTypeLabel, 10);
		multiplicityCombo.setLayoutData(multiplicityComboData);

		FormData typeComboData = new FormData();
		typeComboData.left = new FormAttachment(multiplicityCombo, 20);
		typeComboData.top = new FormAttachment(newVariableTypeLabel, 10);
		typeComboData.right = new FormAttachment(100, -10);
		typeCombo.setLayoutData(typeComboData);
		
		FormData secureData = new FormData();
		secureData.left = new FormAttachment(0, 20);
		secureData.top = new FormAttachment(typeCombo, 10);
		secureData.right = new FormAttachment(100, -10);
		newVariableSecureButton.setLayoutData(secureData);

		existingVariableViewer.addSelectionChangedListener(new ISelectionChangedListener()
			{
				public void selectionChanged(SelectionChangedEvent event)
				{
					IStructuredSelection selection =
						(IStructuredSelection)event.getSelection();

					if(selection.isEmpty())
					{
						settings.targetVariableName = null;
						settings.targetVariableType = null;
					}
					else //there is a variable selected
					{
						if(selection.getFirstElement() instanceof Variable)
						{
							Variable v = (Variable)selection.getFirstElement();
							settings.targetVariableName = v.getName();
							settings.targetVariableType = v.getType();
						}
						else //instance of ObjectField 
						{
							ObjectField field =
								(ObjectField)selection.getFirstElement();
							settings.targetVariableName = field.getPath();
							settings.targetVariableType = field.getType();
						}
					}

					settings.fireTargetChanged();
				}
			});

		existingVariableButton.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					existingVariableViewer.getControl()
										  .setEnabled(existingVariableButton
						.getSelection());
					newVariableNameField.setEnabled(!existingVariableButton
						.getSelection());
					multiplicityCombo.setEnabled(!existingVariableButton
						.getSelection());
					typeCombo.setEnabled(!existingVariableButton.getSelection());
					newVariableSecureButton.setEnabled(!existingVariableButton.getSelection());
					settings.targetVariableExists = existingVariableButton
						.getSelection();

					if(existingVariableButton.getSelection())
					{
						IStructuredSelection selection =
							(IStructuredSelection)existingVariableViewer
							.getSelection();

						if(selection.isEmpty())
						{
							settings.targetVariableName = null;
							settings.targetVariableType = null;
						}
						else //there is a variable selected
						{
							if(selection.getFirstElement() instanceof Variable)
							{
								Variable v =
									(Variable)selection.getFirstElement();
								settings.targetVariableName = v.getName();
								settings.targetVariableType = v.getType();
							}
							else //instance of ObjectField 
							{
								ObjectField field =
									(ObjectField)selection.getFirstElement();
								settings.targetVariableName = field.getPath();
								settings.targetVariableType = field.getType();
							}
						}
						getContainer().setCanFinish(true);
					}
					else //this is a new variable
					{
						settings.targetVariableName = newVariableNameField
							.getText();
						if(multiplicityCombo.getSelectionIndex() == 0)
						{
							Primitive prim = Primitive.find(typeList.get(typeCombo.getSelectionIndex()));
							if(prim != null)
							{
								settings.targetVariableType = new FieldType(prim);
							}
							else
								settings.targetVariableType = new FieldType(getElement().getDesign().getDocument().getProject().getBusinessObjectSet().getBusinessObject(typeList.get(typeCombo.getSelectionIndex())));
						}
						else
						{
							Primitive prim = Primitive.find(typeList.get(typeCombo.getSelectionIndex()));
							if(prim != null)
							{
								settings.targetVariableType = new FieldType(Primitive.ARRAY, prim);
							}
							else
								settings.targetVariableType = new FieldType(Primitive.ARRAY, getElement().getDesign().getDocument().getProject().getBusinessObjectSet().getBusinessObject(typeList.get(typeCombo.getSelectionIndex())));
						}
						settings.setTargetVariableSecure(newVariableSecureButton.getSelection());
						if(VariableNameValidator.followsVtpNamingRules(newVariableNameField.getText()) || newVariableNameField.getText().equals(""))
						{
							getContainer().setCanFinish(true);
						}
						else
						{
							getContainer().setCanFinish(false);
						}
					}

					settings.fireTargetChanged();
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.ComponentPropertiesPanel#save()
	 */
	public void save()
	{
		queryElement.getElement().setName(queryElement.getElement().getName());
		queryElement.setSettings(settings);
	}
	
	public void cancel()
	{
		
	}

	public class VariableContentProvider implements ITreeContentProvider
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return incomingVariables.toArray();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element)
		{
			if(element instanceof Variable)
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
			return true;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement)
		{
			return ((ObjectDefinition)parentElement).getFields().toArray();
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	public class VariableLabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if(columnIndex == 0)
			{
				return ((ObjectDefinition)element).getName();
			}
			else
			{
				FieldType ft = ((ObjectDefinition)element).getType();
				String ret = "";
				ret += ft.getName();
				if(ft.hasBaseType())
				{
					ret += "Array Of ";
					ret += ft.getBaseTypeName();
				}

				return ret;
			}
		}

		public void dispose()
		{
		}

		public boolean isLabelProperty(Object element, String property)
		{
			return false;
		}

		public void addListener(ILabelProviderListener listener)
		{
		}

		public void removeListener(ILabelProviderListener listener)
		{
		}
	}

	@Override
	public void setConfigurationContext(Map<String, Object> values)
	{
	}

	@Override
	public List<String> getApplicableContexts()
	{
		return Collections.emptyList();
	}

}
