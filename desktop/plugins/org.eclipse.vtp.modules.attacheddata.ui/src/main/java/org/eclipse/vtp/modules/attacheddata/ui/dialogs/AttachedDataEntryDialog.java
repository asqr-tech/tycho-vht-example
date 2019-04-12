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
package org.eclipse.vtp.modules.attacheddata.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.ObjectField;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.modules.attacheddata.ui.configuration.post.AttachedDataItemEntry;

public class AttachedDataEntryDialog extends Dialog
{
	AttachedDataItemEntry entry = new AttachedDataItemEntry();
	Button mapRadio = null;
	Combo mapCombo = null;
	Button singleRadio = null;
	Text nameField = null;
	Button staticButton = null;
	Text staticField = null;
	Button expressionButton = null;
	Text expressionField = null;
	Button variableButton = null;
	TreeViewer variableViewer = null;
	IDesignConnector connector = null;
	String exitPath = null;
	List<Variable> incomingVariables = new ArrayList<Variable>();
	List<Variable> incomingMaps = new ArrayList<Variable>();

	public AttachedDataEntryDialog(Shell parentShell)
	{
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL
				| getDefaultOrientation());
	}

	public AttachedDataEntryDialog(IShellProvider parentShell)
	{
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL
				| getDefaultOrientation());
	}
	
	public void setConnector(IDesignConnector connector, String exitPath)
	{
		this.connector = connector;
		this.exitPath = exitPath;
	}
	
	public void setEntry(AttachedDataItemEntry entry)
	{
		this.entry = entry;
	}
	
	public AttachedDataItemEntry getEntry()
	{
		return entry;
	}

	protected Control createDialogArea(Composite parent)
    {
		List<Variable> vars = connector.getOrigin().getDesign().getVariablesFrom(connector.getOrigin(), exitPath);
		outer:	for(Variable v : vars)
				{
					for(int i = 0; i < incomingVariables.size(); i++)
					{
						if(incomingVariables.get(i).getName().compareToIgnoreCase(v.getName()) > 0)
						{
							incomingVariables.add(i, v);
							continue outer;
						}
					}
					incomingVariables.add(v);
				}
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp.setLayout(new GridLayout(1, false));
		
		mapRadio = new Button(comp, SWT.RADIO);
		mapRadio.setText("Add all values from a Map");
		mapRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		System.out.println("Data Type " + entry.getDataType());
		mapRadio.setSelection(entry.getDataType() == AttachedDataItemEntry.TYPE_MAP);
		
		Composite mapComp = new Composite(comp, SWT.NONE);
		mapComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mapComp.setLayout(new GridLayout(2, false));
		
		Label mapLabel = new Label(mapComp, SWT.NONE);
		mapLabel.setText("Use this map");
		mapLabel.setLayoutData(new GridData());
		
		mapCombo = new Combo(mapComp, SWT.DROP_DOWN | SWT.READ_ONLY);
		mapCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for(Variable v : incomingVariables)
		{
			if(v.getType().hasBaseType() && v.getType().getPrimitiveType() == Primitive.MAP)
			{
				incomingMaps.add(v);
				mapCombo.add(v.getName());
			}
		}
		if(incomingMaps.size() == 0)
		{
			mapCombo.setEnabled(false);
			mapRadio.setEnabled(false);
		}
		else
		{
			if(entry.getValue() == null)
				mapCombo.select(0);
			else
				for(int i = 0; i < incomingMaps.size(); i++)
				{
					Variable v = incomingMaps.get(i);
					if(v.getName().equals(entry.getValue()))
					{
						mapCombo.select(i);
					}
				}
		}
		
		singleRadio = new Button(comp, SWT.RADIO);
		singleRadio.setText("Add a single value");
		singleRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		singleRadio.setSelection(entry.getDataType() != AttachedDataItemEntry.TYPE_MAP);
		
		Composite singleComp = new Composite(comp, SWT.NONE);
		singleComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		singleComp.setLayout(new GridLayout(2, false));
		
		Label nameLabel = new Label(singleComp, SWT.NONE);
		nameLabel.setText("Attached Data Name");
		GridData layoutData = new GridData();
		layoutData.verticalIndent = 10;
		nameLabel.setLayoutData(layoutData);
		nameField = new Text(singleComp, SWT.BORDER | SWT.SINGLE);
		if(entry.getDataType() != AttachedDataItemEntry.TYPE_MAP)
			nameField.setText(entry.getName() == null ? "" : entry.getName());
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalIndent = 10;
		nameField.setLayoutData(layoutData);
		
		staticButton = new Button(singleComp, SWT.RADIO);
		staticButton.setText("Use this value");
		staticButton.setSelection(entry.getDataType() == AttachedDataItemEntry.TYPE_STATIC);
		staticButton.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
            {
            }

			public void widgetSelected(SelectionEvent e)
            {
				if(!staticButton.getSelection())
					staticField.setText("");
				else
				{
					entry.setValue("");
					entry.setDataType(AttachedDataItemEntry.TYPE_STATIC);
				}
            }
			
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		staticButton.setLayoutData(gd);
		staticField = new Text(singleComp, SWT.BORDER | SWT.SINGLE);
		if(entry.getDataType() == AttachedDataItemEntry.TYPE_STATIC)
		{
			staticField.setText(entry.getValue() == null ? "" : entry.getValue());
		}
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = 15;
		gd.horizontalSpan = 2;
		staticField.setLayoutData(gd);
		
		expressionButton = new Button(singleComp, SWT.RADIO);
		expressionButton.setText("Use this Javascript expression");
		expressionButton.setSelection(entry.getDataType() == AttachedDataItemEntry.TYPE_EXPRESSION);
		expressionButton.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
            {
            }

			public void widgetSelected(SelectionEvent e)
            {
				if(!expressionButton.getSelection())
					expressionField.setText("");
				else
				{
					entry.setValue("");
					entry.setDataType(AttachedDataItemEntry.TYPE_EXPRESSION);
				}
            }
			
		});
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		expressionButton.setLayoutData(gd);
		expressionField = new Text(singleComp, SWT.SINGLE | SWT.BORDER);
		if(entry.getDataType() == AttachedDataItemEntry.TYPE_EXPRESSION)
		{
			expressionField.setText(entry.getValue() == null ? "" : entry.getValue());
		}
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = 15;
		gd.horizontalSpan = 2;
		expressionField.setLayoutData(gd);
		
		variableButton = new Button(singleComp, SWT.RADIO);
		variableButton.setText("Use the contents of this variable");
		variableButton.setSelection(entry.getDataType() == AttachedDataItemEntry.TYPE_VARIABLE);
		variableButton.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
            {
            }

			public void widgetSelected(SelectionEvent e)
            {
				if(!variableButton.getSelection())
					variableViewer.setSelection(new StructuredSelection((Object)null));
				else
				{
					entry.setValue("");
					entry.setDataType(AttachedDataItemEntry.TYPE_VARIABLE);
				}
            }
			
		});
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		variableButton.setLayoutData(gd);
		Tree variableTable = new Tree(singleComp, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		variableTable.setHeaderVisible(true);
		TreeColumn nameColumn = new TreeColumn(variableTable, SWT.NONE);
		nameColumn.setText("Variable Name");
		nameColumn.setWidth(225);
		TreeColumn typeColumn = new TreeColumn(variableTable, SWT.NONE);
		typeColumn.setText("Type");
		typeColumn.setWidth(125);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = 15;
		gd.horizontalSpan = 2;
		gd.widthHint = 350;
		gd.heightHint = 400;
		variableTable.setLayoutData(gd);
		variableViewer = new TreeViewer(variableTable);
		variableViewer.setContentProvider(new VariableContentProvider());
		variableViewer.setLabelProvider(new VariableLabelProvider());
		variableViewer.setInput(this);
		if(variableButton.getSelection())
		{
			for(int i = 0; i < incomingVariables.size(); i++)
			{
				Variable v = incomingVariables.get(i);
	
				if(v.getName().equals(entry.getValue()))
				{
					variableViewer.setSelection(new StructuredSelection(
							v));
				}
				else if((entry.getValue() != null)
						&& entry.getValue().startsWith(v.getName()))
				{
					List<ObjectField> objectFields = v.getFields();
	
					for(int f = 0; f < objectFields.size(); f++)
					{
						ObjectField of = objectFields.get(f);
	
						if(of.getPath().equals(entry.getValue()))
						{
							variableViewer.setSelection(new StructuredSelection(
									of));
						}
					}
				}
			}
		}
		return comp;
    }

	protected void okPressed()
    {
		if(mapRadio.getSelection())
		{
			entry.setName("All items in");
			entry.setDataType(AttachedDataItemEntry.TYPE_MAP);
			entry.setValue(incomingMaps.get(mapCombo.getSelectionIndex()).getName());
		}
		else
		{
			entry.setName(nameField.getText());
			if(staticButton.getSelection())
			{
				entry.setValue(staticField.getText());
			}
			else if(expressionButton.getSelection())
			{
				entry.setValue(expressionField.getText());
			}
			else //variable
			{
				System.err.println(((StructuredSelection)variableViewer.getSelection()).getFirstElement());
				entry.setValue(((ObjectDefinition)((StructuredSelection)variableViewer.getSelection()).getFirstElement()).getPath());
			}
		}
	    super.okPressed();
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
					ret = " Of " + ft.getBaseTypeName();
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
	
}
