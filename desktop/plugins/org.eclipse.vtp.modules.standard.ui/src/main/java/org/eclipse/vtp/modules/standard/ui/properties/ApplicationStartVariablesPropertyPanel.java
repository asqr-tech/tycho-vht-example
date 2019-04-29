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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.core.Activator;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.standard.ui.BeginInformationProvider;
import org.eclipse.vtp.modules.standard.ui.VariableDeclaration;

/**
 * The graphical user interface used to configure an application's begin module
 * 
 * @author Trip Gilman
 */
public class ApplicationStartVariablesPropertyPanel
	extends DesignElementPropertiesPanel
{
	List<VariableDeclaration> declarations;
	TableViewer variableViewer;
	/**	The button used to add a new variable */
	Button addButton;
	/** The button used to remove the selected variable */
	Button removeButton;
	TextCellEditor valueEditor;
	/** A checkbox used to denote whether a variable may contain sensitive data */
	CheckboxCellEditor secureEditor;
	private Text nameText = null;
	private Combo defaultBrandCombo = null;
	private List<IBrand> brands = new ArrayList<IBrand>();

	/**
	 * @param name
	 */
	public ApplicationStartVariablesPropertyPanel(String name, IDesignElement element)
	{
		super(name, element);
		IWorkflowProject wp = getElement().getDesign().getDocument().getProject();
		declarations = ((BeginInformationProvider)((PrimitiveElement)element).getInformationProvider()).getDeclarations();
		BrandManager bm = wp.getBrandManager();
		IBrand b = bm.getDefaultBrand();
		addBrand(b);
	}
	
	private void addBrand(IBrand b)
	{
		brands.add(b);
		for(IBrand c : b.getChildBrands())
			addBrand(c);
	}
	
	private String getLabel(IBrand b)
	{
		StringBuilder sb = new StringBuilder();
		IBrand parent = b.getParent();
		while(parent != null)
		{
			sb.append("  ");
			parent = parent.getParent();
		}
		sb.append(b.getName());
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		parent.setBackgroundMode(SWT.INHERIT_DEFAULT);

		Composite nameComp = new Composite(parent, SWT.NONE);
		nameComp.setBackground(parent.getBackground());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		nameComp.setLayoutData(gd);
		nameComp.setLayout(new GridLayout(2, false));
		
		Label nameLabel = new Label(nameComp, SWT.NONE);
		nameLabel.setText("Entry Name:");
		nameLabel.setLayoutData(new GridData());
		nameText = new Text(nameComp, SWT.BORDER | SWT.SINGLE);
		nameText.setText(getElement().getName());
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label defaultBrandLabel = new Label(nameComp, SWT.NONE);
		defaultBrandLabel.setText("Default Brand");
		defaultBrandLabel.setLayoutData(new GridData());
		defaultBrandCombo = new Combo(nameComp, SWT.READ_ONLY | SWT.DROP_DOWN);
		defaultBrandCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for(IBrand b : brands)
		{
			defaultBrandCombo.add(getLabel(b));
		}
		String currentDefault = ((BeginInformationProvider)((PrimitiveElement)getElement()).getInformationProvider()).getDefaultBrand();
		for(int i = 0; i < brands.size(); i++)
		{
			if(brands.get(i).getId().equals(currentDefault))
			{
				defaultBrandCombo.select(i);
				break;
			}
		}
		if(defaultBrandCombo.getSelectionIndex() == -1)
			defaultBrandCombo.select(0);
		
		Table variableTable =
			new Table(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		variableTable.setHeaderVisible(true);
		
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
		gd.widthHint = 505;
		gd.heightHint = 200;
		variableTable.setLayoutData(gd);
		valueEditor = new TextCellEditor(variableTable);
		secureEditor = new CheckboxCellEditor(variableTable);
		variableViewer = new TableViewer(variableTable);
		variableViewer.setColumnProperties(new String[] {"Secure", "Name", "Type", "Value"});
		variableViewer.setCellEditors(new CellEditor[] {secureEditor, null, null, valueEditor});
		variableViewer.setCellModifier(new ValueCellModifier());
		variableViewer.setContentProvider(new VariableContentProvider());
		variableViewer.setLabelProvider(new VariableLabelProvider());
		variableViewer.setInput(this);
		variableViewer.setComparator(new ViewerComparator()
		{

			@Override
            public int compare(Viewer viewer, Object e1, Object e2)
            {
				VariableDeclaration v1 = (VariableDeclaration)e1;
				VariableDeclaration v2 = (VariableDeclaration)e2;
	            return v1.getName().compareTo(v2.getName());
            }
			
		});
		variableViewer.addSelectionChangedListener(new ISelectionChangedListener()
			{
				public void selectionChanged(SelectionChangedEvent event)
				{
					removeButton.setEnabled(!event.getSelection().isEmpty());
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
		Composite buttonComp = new Composite(parent, SWT.NONE);
		buttonComp.setLayout(new GridLayout(1, false));
		buttonComp.setLayoutData(new GridData());
		addButton = new Button(buttonComp, SWT.PUSH);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
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
						updateVariables();
					}
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		removeButton = new Button(buttonComp, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setEnabled(false);
		removeButton.setLayoutData(new GridData());
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
		declarations.remove(((IStructuredSelection)variableViewer
			.getSelection()).getFirstElement());
		updateVariables();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#save()
	 */
	public void save()
	{
		getElement().setName(nameText.getText());
		BeginInformationProvider ip = (BeginInformationProvider)((PrimitiveElement)getElement()).getInformationProvider();
		ip.setDeclarations(declarations);
		ip.setDefaultBrand(brands.get(defaultBrandCombo.getSelectionIndex()).getId());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ComponentPropertiesPanel#cancel()
	 */
	public void cancel()
	{
	}
	
	private void updateVariables()
	{
		variableViewer.refresh();
	}

	public class VariableContentProvider implements IStructuredContentProvider
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return declarations.toArray();
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
			VariableDeclaration vd = (VariableDeclaration)element;
			if(columnIndex == 0 && vd.isSecure())
				return Activator.getDefault().getImageRegistry().get("ICON_LOCK");
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			VariableDeclaration vd = (VariableDeclaration)element;

			if(columnIndex == 0)
			{
				return "";
			}
			else if(columnIndex == 1)
			{
				return vd.getName();
			}
			else if(columnIndex == 2)
			{
				String ret = vd.getType().getName();
				if(vd.getType().hasBaseType())
				{
					ret += " Of ";
//					ret += vd.getType().getBaseTypeName();
					if(vd.getType().isObjectBaseType())
						ret += vd.getType().getObjectBaseType().getName();
					else
						ret += vd.getType().getPrimitiveBaseType().getName();
				}
				return ret;
			}
			else if(columnIndex == 3)
			{
				if(vd.getType().hasValue())
				{
					return vd.getValue();
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
			VariableDeclaration vd = (VariableDeclaration)element;

			if(property.equals("Value") && vd.getType().hasValue())
			{
				return true;
			}
			else if(property.equals("Secure"))
				return true;

			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
		 */
		public Object getValue(Object element, String property)
		{
			VariableDeclaration vd = (VariableDeclaration)element;

			if(property.equals("Value"))
			{
				return (vd.getValue() == null) ? "" : vd.getValue();
			}
			else if(property.equals("Secure"))
				return new Boolean(vd.isSecure());

			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
		 */
		public void modify(Object element, String property, Object value)
		{
			TableItem ti = (TableItem)element;
			VariableDeclaration vd = (VariableDeclaration)ti.getData();

			if(property.equals("Value"))
			{
				vd.setValueType(0);
				vd.setValue((String)value);
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
