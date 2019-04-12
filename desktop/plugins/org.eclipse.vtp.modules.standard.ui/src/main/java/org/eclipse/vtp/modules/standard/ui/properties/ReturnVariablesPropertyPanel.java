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

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.standard.ui.ReturnInformationProvider;

/**
 * @author Trip
 *
 */
public class ReturnVariablesPropertyPanel extends DesignElementPropertiesPanel
{
	List<String> exportedVars;
	CheckboxTableViewer variableViewer;
	List<Variable> variables = new ArrayList<Variable>();
	private ReturnInformationProvider info = null;

	/**
	 * @param name
	 */
	public ReturnVariablesPropertyPanel(String name, IDesignElement element)
	{
		super(name, element);
		info = (ReturnInformationProvider)((PrimitiveElement)element).getInformationProvider();
		exportedVars = new ArrayList<String>(info.getExports());
		List<Variable> vars = element.getDesign().getVariablesFor(element);
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

	public void createControls(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));

		Table variableTable =
			new Table(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		variableTable.setHeaderVisible(true);

		TableColumn nameColumn = new TableColumn(variableTable, SWT.NONE);
		nameColumn.setText("Variable Name");
		nameColumn.setWidth(150);

		TableColumn typeColumn = new TableColumn(variableTable, SWT.NONE);
		typeColumn.setText("Type");
		typeColumn.setWidth(150);

		GridData gd = new GridData(GridData.FILL_VERTICAL);
		gd.verticalSpan = 2;
		gd.widthHint = 505;
		gd.heightHint = 200;
		variableTable.setLayoutData(gd);
		variableViewer = new CheckboxTableViewer(variableTable);
		variableViewer.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event)
            {
				exportedVars.remove(((Variable)event.getElement()).getName());
				if(event.getChecked())
					exportedVars.add(((Variable)event.getElement()).getName());
            }
			
		});
		variableViewer.setColumnProperties(new String[] {"Name", "Type", "Value"});
		variableViewer.setContentProvider(new VariableContentProvider());
		variableViewer.setLabelProvider(new VariableLabelProvider());
		variableViewer.setInput(this);
		for(Variable vd : variables)
		{
			variableViewer.setChecked(vd, exportedVars.contains(vd.getName()));
		}

	}

	public void save()
	{
		info.setExports(exportedVars);
	}
	
	public void cancel()
	{
		
	}

	public class VariableContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			List<Variable> ret = new ArrayList<Variable>();
			for(int i = 0; i < variables.size(); i++)
			{
				if(!variables.get(i).getType().isObject())
					ret.add(variables.get(i));
			}
			return ret.toArray();
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
			Variable vd = (Variable)element;

			if(columnIndex == 0)
			{
				return vd.getName();
			}
			else if(columnIndex == 1)
			{
				StringBuffer buf = new StringBuffer();
				buf.append(vd.getType().getName());
				if(vd.getType().hasBaseType()) //is array or map
				{
					buf.append(" of ");
					buf.append(vd.getType().getBaseTypeName());
				}
				return buf.toString();
			}

			return "N/A";
		}

		public void addListener(ILabelProviderListener listener)
		{
		}

		public void dispose()
		{
		}

		public boolean isLabelProperty(Object element, String property)
		{
			return false;
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
