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
package org.eclipse.vtp.modules.standard.ui.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.vtp.desktop.core.Activator;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.VariableHelper;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.standard.ui.VariableAssignmentInformationProvider;
import org.eclipse.vtp.modules.standard.ui.VariableDeclaration;

/**
 * The graphical user interface used to configure the Variable Assignment module.
 * 
 * @author Trip Gilman
 *
 */
public class AssignmentVariablesPropertyPanel extends DesignElementPropertiesPanel
{
	List<VariableDeclaration> declarations;
	List<Variable> variables;
	TableViewer variableViewer;
	/** The button used to add a new variable */
	Button addButton;
	/** The button used to remove the selected variable */
	Button removeButton;
	TextCellEditor valueEditor;
	FormToolkit toolkit = null;
	/** The text field used to set name of this particular Variable Assignment module */
	Text nameField = null;
	/** A checkbox used to denote whether a variable may contain sensitive data */
	CheckboxCellEditor secureEditor;
	Button filterButton = null;

	/**
	 * @param name
	 */
	public AssignmentVariablesPropertyPanel(String name, IDesignElement element)
	{
		super(name, element);
		declarations = ((VariableAssignmentInformationProvider)((PrimitiveElement)element).getInformationProvider()).getDeclarations();
		populateVariables();
	}
	
	public void populateVariables()
	{
		variables = new ArrayList<Variable>();
		List<Variable> incomingVariables = getElement().getDesign().getVariablesFor(getElement());
		Map<String, Variable> lookup = new HashMap<String, Variable>();
		for(Variable v : incomingVariables)
			lookup.put(v.getName(), v);
		for(VariableDeclaration vd : declarations)
		{
			Variable v = VariableHelper.constructVariable(vd.getName(), getElement().getDesign()
				 .getDocument().getProject().getBusinessObjectSet(), vd.getType());
			if(v != null)
			{
				v.setSecure(vd.isSecure());
				lookup.put(v.getName(), v);
			}
		}
		for(Variable v : lookup.values())
			variables.add(v);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));

		toolkit = new FormToolkit(parent.getDisplay());
		final Section generalSection =
			toolkit.createSection(parent, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		generalSection.setLayoutData(gridData);
		generalSection.setText("General");

		Composite generalComp = new Composite(generalSection, SWT.NONE);
		generalComp.setBackground(parent.getBackground());
		generalComp.setLayout(new GridLayout(2, false));
		generalSection.setClient(generalComp);
		
		Label nameLabel = new Label(generalComp, SWT.NONE);
		nameLabel.setText("Name: ");
		nameLabel.setBackground(generalComp.getBackground());
		nameLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		nameField = new Text(generalComp, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getElement().getName());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));


		final Section contentSection =
			toolkit.createSection(parent, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		contentSection.setLayoutData(gridData);
		contentSection.setText("Variable Assignments");
		
		filterButton = new Button(parent, SWT.CHECK);
		filterButton.setText("Show only new or modified variables");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		filterButton.setLayoutData(gd);
		filterButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
            {
            }

			public void widgetSelected(SelectionEvent e)
            {
				variableViewer.refresh();
            }
		});
		
		Table variableTable =
			new Table(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		variableTable.setHeaderVisible(true);

		TableColumn newColumn = new TableColumn(variableTable, SWT.NONE);
		newColumn.setText("*");
		newColumn.setWidth(15);

		TableColumn secureColumn = new TableColumn(variableTable, SWT.NONE);
		secureColumn.setImage(Activator.getDefault().getImageRegistry().get("ICON_LOCK"));
		secureColumn.setAlignment(SWT.CENTER);
		secureColumn.setWidth(23);
		
		TableColumn nameColumn = new TableColumn(variableTable, SWT.NONE);
		nameColumn.setText("Variable Name");
		nameColumn.setWidth(150);

		TableColumn typeColumn = new TableColumn(variableTable, SWT.NONE);
		typeColumn.setText("Type");
		typeColumn.setWidth(150);

		TableColumn valueColumn = new TableColumn(variableTable, SWT.NONE);
		valueColumn.setText("Value");
		valueColumn.setWidth(200);

		gd = new GridData(GridData.FILL_BOTH);
		gd.verticalSpan = 2;
		gd.widthHint = 520;
		gd.heightHint = 200;
		variableTable.setLayoutData(gd);
		valueEditor = new TextCellEditor(variableTable);
		secureEditor = new CheckboxCellEditor(variableTable);
		variableViewer = new TableViewer(variableTable);
		variableViewer.setColumnProperties(new String[]
			{
				"New", "Secure", "Name", "Type", "Value"
			});
		variableViewer.setCellEditors(new CellEditor[]
			{
				null, secureEditor, null, null, valueEditor
			});
		variableViewer.setCellModifier(new ValueCellModifier());
		variableViewer.setContentProvider(new VariableContentProvider());
		variableViewer.setLabelProvider(new VariableLabelProvider());
		variableViewer.setInput(this);
		variableViewer.setComparator(new ViewerComparator()
		{

			@Override
            public int compare(Viewer viewer, Object e1, Object e2)
            {
	            Variable v1 = (Variable)e1;
	            Variable v2 = (Variable)e2;
	            return v1.getName().compareTo(v2.getName());
            }
			
		});
		variableViewer.addSelectionChangedListener(new ISelectionChangedListener()
			{
				public void selectionChanged(SelectionChangedEvent event)
				{
					if(variableViewer.getSelection().isEmpty())
					{
						removeButton.setEnabled(false);

						return;
					}

					Map<String, VariableDeclaration> vdm = new HashMap<String, VariableDeclaration>();

					for(int i = 0; i < declarations.size(); i++)
					{
						VariableDeclaration vd =
							declarations.get(i);
						vdm.put(vd.getName(), vd);
					}

					Variable v =
						(Variable)((IStructuredSelection)variableViewer
						.getSelection()).getFirstElement();
					removeButton.setEnabled(vdm.get(v.getName()) != null);
				}
			});
		variableViewer.getControl().addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
			}

			public void keyReleased(KeyEvent e)
			{
				if(e.keyCode == SWT.DEL || e.keyCode == SWT.BS)
					removeVariable();
			}
		});
		addButton = new Button(parent, SWT.PUSH);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		addButton.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					try
                    {
						List<String> reservedNames = new ArrayList<String>();
						for(VariableDeclaration vd : declarations)
						{
							reservedNames.add(vd.getName());
						}
						NewVariableDialog nvd =
							new NewVariableDialog(addButton.getShell(), reservedNames, getElement().getDesign()
									 .getDocument().getProject().getBusinessObjectSet());

	                    if(nvd.open() == SWT.OK)
	                    {
	                    	declarations.add(new VariableDeclaration(nvd.name,
	                    			nvd.type, 0, null, nvd.secure));
	                    	populateVariables();
	                    }
	                    variableViewer.refresh();
                    }
                    catch(RuntimeException e1)
                    {
	                    e1.printStackTrace();
                    }
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		removeButton = new Button(parent, SWT.PUSH);
		removeButton.setText("Clear");
		removeButton.setEnabled(false);
		removeButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		removeButton.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					removeVariable();
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
	}
	
	public void removeVariable()
	{
		Variable v =
			(Variable)((IStructuredSelection)variableViewer
			.getSelection()).getFirstElement();
		VariableDeclaration vd = null;

		for(int i = 0; i < declarations.size(); i++)
		{
			vd = declarations.get(i);

			if(!vd.getName().equals(v.getName()))
			{
				vd = null;
			}
			else
			{
				break;
			}
		}

		if(vd != null)
		{
			declarations.remove(vd);
			populateVariables();
		}

		variableViewer.refresh();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#save()
	 */
	public void save()
	{
		getElement().setName(nameField.getText());
		((VariableAssignmentInformationProvider)((PrimitiveElement)getElement()).getInformationProvider()).setDeclarations(declarations);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ComponentPropertiesPanel#cancel()
	 */
	public void cancel()
	{
		
	}

	public class VariableContentProvider implements IStructuredContentProvider
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			if(filterButton.getSelection())
			{
				Map<String, VariableDeclaration> vdm = new HashMap<String, VariableDeclaration>();
				for(int i = 0; i < declarations.size(); i++)
				{
					VariableDeclaration vd =
						declarations.get(i);
					vdm.put(vd.getName(), vd);
				}
				List<Variable> filteredList = new ArrayList<Variable>();
				for(Variable vd : variables)
				{
					if(vdm.get(vd.getName()) != null)
					{
						filteredList.add(vd);
					}
				}
				return filteredList.toArray();
			}
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

	public class VariableLabelProvider implements ITableLabelProvider
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			Variable v = (Variable)element;
			if(columnIndex == 1 && v.isSecure())
				return Activator.getDefault().getImageRegistry().get("ICON_LOCK");
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			Map<String, VariableDeclaration> vdm = new HashMap<String, VariableDeclaration>();

			for(int i = 0; i < declarations.size(); i++)
			{
				VariableDeclaration vd =
					declarations.get(i);
				vdm.put(vd.getName(), vd);
			}

			Variable vd = (Variable)element;

			if(columnIndex == 0)
			{
				if(vdm.get(vd.getName()) != null)
				{
					return "*";
				}

				return "";
			}
			else if(columnIndex == 1)
			{
				return "";
			}
			else if(columnIndex == 2)
			{
				return vd.getName();
			}
			else if(columnIndex == 3)
			{
				String ret = vd.getType().getName();
				if(vd.getType().hasBaseType())
				{
					ret += " Of ";
					ret += vd.getType().getBaseTypeName();
				}
				return ret;
			}
			else if(columnIndex == 4)
			{
				if(vd.getType().hasValue())
				{
					if(vdm.get(vd.getName()) != null)
					{
						return vdm.get(vd.getName()).getValue();
					}
				}
			}

			return "N/A";
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
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener)
		{
		}
	}

	public class ValueCellModifier implements ICellModifier
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
		 */
		public boolean canModify(Object element, String property)
		{
			Variable v = (Variable)element;
			if(property.equals("Value"))
			{

				if(v.getType().hasValue())
				{
					return true;
				}
			}
			else if(property.equals("Secure"))
			{
				Map<String, VariableDeclaration> vdm = new HashMap<String, VariableDeclaration>();

				for(int i = 0; i < declarations.size(); i++)
				{
					VariableDeclaration vd =
						declarations.get(i);
					vdm.put(vd.getName(), vd);
				}
				return vdm.get(v.getName()) != null;
			}

			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
		 */
		public Object getValue(Object element, String property)
		{
			Map<String, VariableDeclaration> vdm = new HashMap<String, VariableDeclaration>();

			for(int i = 0; i < declarations.size(); i++)
			{
				VariableDeclaration vd =
					declarations.get(i);
				vdm.put(vd.getName(), vd);
			}

			Variable vd = (Variable)element;

			if(property.equals("Value"))
			{
				return (vdm.get(vd.getName()) == null) ? ""
													   : (
					(
						vdm.get(vd.getName()).getValue() == null
					) ? "" : vdm.get(vd.getName()).getValue()
				);
			}
			else if(property.equals("Secure"))
				return new Boolean(vd.isSecure());

			return "";
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
		 */
		public void modify(Object element, String property, Object value)
		{
			Map<String, VariableDeclaration> vdm = new HashMap<String, VariableDeclaration>();

			for(int i = 0; i < declarations.size(); i++)
			{
				VariableDeclaration vd =
					declarations.get(i);
				vdm.put(vd.getName(), vd);
			}

			TableItem ti = (TableItem)element;
			Variable vd = (Variable)ti.getData();
			VariableDeclaration varDec =
				vdm.get(vd.getName());

			if(varDec == null)
			{
				varDec = new VariableDeclaration(vd.getName(),
						vd.getType(), 0, null);
				declarations.add(varDec);
			}

			if(property.equals("Value"))
			{
				varDec.setValueType(0);
				varDec.setValue((String)value);
				variableViewer.refresh(true);
			}
			else if(property.equals("Secure"))
			{
				vd.setSecure(((Boolean)value).booleanValue());
				variableViewer.refresh(true);
			}
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
