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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.core.dialogs.FramedDialog;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IBusinessObject;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectField;
import org.eclipse.vtp.desktop.model.core.IDatabase;
import org.eclipse.vtp.desktop.model.core.IDatabaseTable;
import org.eclipse.vtp.desktop.model.core.IDatabaseTableColumn;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.ObjectField;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.VariableHelper;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.database.ui.DatabaseQueryInformationProvider;

/**
 * @author Trip
 *
 */
public class DatabaseQueryDataMappingPropertiesPanel
	extends DesignElementPropertiesPanel implements DatabaseQuerySettingsListener
{
	DatabaseQueryInformationProvider queryElement;
	DatabaseQuerySettingsStructure settings;
	TableViewer mappingTableViewer;
	ObjectDefinition currentObject;
	ValueCellEditor valueCellEditor;
	List<Variable> incomingVariables = null;

	public DatabaseQueryDataMappingPropertiesPanel(PrimitiveElement dqe,
		DatabaseQuerySettingsStructure settings)
	{
		super("Fields", dqe);
		this.queryElement = (DatabaseQueryInformationProvider)dqe.getInformationProvider();
		this.settings = settings;
		settings.addSettingsListener(this);
		incomingVariables = dqe.getDesign().getVariablesFor(dqe);
	}

	public void createControls(Composite parent)
	{
		Table mappingTable =
			new Table(parent, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE);
		mappingTable.setHeaderVisible(true);

		TableColumn businessObjectFieldColumn =
			new TableColumn(mappingTable, SWT.NONE);
		businessObjectFieldColumn.setText("Business Object Field");
		businessObjectFieldColumn.setWidth(150);

		TableColumn valueColumn = new TableColumn(mappingTable, SWT.NONE);
		valueColumn.setText("Value");
		valueColumn.setWidth(200);
		valueCellEditor = new ValueCellEditor(mappingTable);
		mappingTableViewer = new TableViewer(mappingTable);
		mappingTableViewer.setColumnProperties(new String[] {"Field", "Value"});
		mappingTableViewer.setCellModifier(new MappingCellModifier());
		mappingTableViewer.setCellEditors(new CellEditor[] {null, valueCellEditor});
		mappingTableViewer.setContentProvider(new MappingContentProvider());
		mappingTableViewer.setLabelProvider(new MappingLabelProvider());
		mappingTableViewer.setInput(this);

		if((settings.targetVariableName != null)
				&& !settings.targetVariableName.equals("")
				&& (settings.targetVariableType != null)
				&& !settings.targetVariableType.equals(""))
		{
			if(settings.targetVariableExists)
			{
				String tname = settings.targetVariableName;
				String[] parts = tname.split("\\.");
				currentObject = findObjectDefinition(incomingVariables, parts[0]);

				for(int i = 1; i < parts.length; i++)
				{
					currentObject = findObjectDefinition(currentObject.getFields(),
							parts[i]);

					if(currentObject == null)
					{
						break;
					}
				}

				if(currentObject != null)
				{
					if(currentObject.getType().hasBaseType())
					{
						if(!currentObject.getType().isObject())
						{
							settings.dataMapping.add(settings.new DataMapping(
									"Value", -1, null));
						}
						else
						{
							List<IBusinessObject> businessObjects =
								getElement().getDesign().getDocument().getProject().getBusinessObjectSet()
								 .getBusinessObjects();

							for(int i = 0; i < businessObjects.size(); i++)
							{
								IBusinessObject ibo =
									businessObjects.get(i);

								if(ibo.getName()
										  .equals(settings.targetVariableType))
								{
									List<IBusinessObjectField> fields = ibo.getFields();

									for(int f = 0; f < fields.size(); f++)
									{
										IBusinessObjectField ibof =
											fields.get(f);
										settings.dataMapping.add(settings.new DataMapping(
												ibof.getName(), -1, null));
									}
								}
							}
						}
					}
					else
					{
						if(currentObject.getType().isObject())
						{
							List<ObjectField> fields = currentObject.getFields();

							for(int i = 0; i < fields.size(); i++)
							{
								ObjectField of = fields.get(i);
								settings.dataMapping.add(settings.new DataMapping(
										of.getName(), -1, null));
							}
						}
						else
						{
							settings.dataMapping.add(settings.new DataMapping(
									currentObject.getPath(), -1, null));
						}
					}
				}
			}
			else
			{
				currentObject = VariableHelper.constructVariable(settings.targetVariableName, getElement().getDesign().getDocument().getProject().getBusinessObjectSet(), settings.targetVariableType);
			}
		}

		mappingTableViewer.refresh();
	}

	public ObjectDefinition findObjectDefinition(List<? extends ObjectDefinition> posibilities, String name)
	{
		for(int i = 0; i < posibilities.size(); i++)
		{
			ObjectDefinition od = posibilities.get(i);

			if(od.getName().equals(name))
			{
				return od;
			}
		}

		return null;
	}

	public void save()
	{
	}
	
	public void cancel()
	{
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.DatabaseQuerySettingsListener#targetVariableChanged()
	 */
	public void targetVariableChanged()
	{
		if((settings.targetVariableName == null)
				|| (settings.targetVariableType == null)
				|| settings.targetVariableName.equals(""))
		{
			currentObject = null;
			mappingTableViewer.refresh();

			return;
		}

		if((currentObject == null)
				|| (
					!settings.targetVariableName.equals(currentObject.getPath())
					|| !settings.targetVariableType.equals(currentObject.getType())
				))
		{
			if(settings.targetVariableExists)
			{
				for(int i = 0; i < incomingVariables.size(); i++)
				{
					Variable v = incomingVariables.get(i);

					if(v.getName().equals(settings.targetVariableName))
					{
						currentObject = v;
					}
					else if((settings.targetVariableName != null)
							&& settings.targetVariableName.startsWith(
								v.getName()))
					{
						List<ObjectField> objectFields = v.getFields();

						for(int f = 0; f < objectFields.size(); f++)
						{
							ObjectField of = objectFields.get(f);

							if(of.getPath().equals(settings.targetVariableName))
							{
								currentObject = of;
							}
						}
					}
				}
			}
			else
			{
				currentObject = VariableHelper.constructVariable(settings.targetVariableName, getElement().getDesign().getDocument().getProject().getBusinessObjectSet(), settings.targetVariableType);
			}

			settings.dataMapping.clear();

			if(currentObject.getType().hasBaseType())
			{
				if(!currentObject.getType().isObject())
				{
					settings.dataMapping.add(settings.new DataMapping("Value",
							-1, null));
				}
				else
				{
					List<IBusinessObject> businessObjects =
						getElement().getDesign().getDocument().getProject().getBusinessObjectSet()
						 .getBusinessObjects();

					for(int i = 0; i < businessObjects.size(); i++)
					{
						IBusinessObject ibo =
							businessObjects.get(i);

						if(ibo.getName().equals(settings.targetVariableType))
						{
							List<IBusinessObjectField> fields = ibo.getFields();

							for(int f = 0; f < fields.size(); f++)
							{
								IBusinessObjectField ibof = fields.get(f);
								settings.dataMapping.add(settings.new DataMapping(
										ibof.getName(), -1, null));
							}
						}
					}
				}
			}
			else
			{
				if(currentObject.getType().isObject())
				{
					List<ObjectField> fields = currentObject.getFields();

					for(int i = 0; i < fields.size(); i++)
					{
						ObjectField of = fields.get(i);
						settings.dataMapping.add(settings.new DataMapping(
								of.getName(), -1, null));
					}
				}
				else
				{
					settings.dataMapping.add(settings.new DataMapping(
							currentObject.getPath(), -1, null));
				}
			}

			mappingTableViewer.refresh();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.DatabaseQuerySettingsListener#sourceDatabaseChanged()
	 */
	public void sourceDatabaseChanged()
	{
		settings.dataMapping.clear();

		if(currentObject != null && currentObject.getType().hasBaseType())
		{
			if(!currentObject.getType().isObject())
			{
				settings.dataMapping.add(settings.new DataMapping("Value",
						-1, null));
			}
			else
			{
				List<IBusinessObject> businessObjects =
					getElement().getDesign().getDocument().getProject().getBusinessObjectSet()
					 .getBusinessObjects();

				for(int i = 0; i < businessObjects.size(); i++)
				{
					IBusinessObject ibo = businessObjects.get(i);

					if(ibo.getName().equals(settings.targetVariableType))
					{
						List<IBusinessObjectField> fields = ibo.getFields();

						for(int f = 0; f < fields.size(); f++)
						{
							IBusinessObjectField ibof = fields.get(f);
							settings.dataMapping.add(settings.new DataMapping(
									ibof.getName(), -1, null));
						}
					}
				}
			}
		}
		else if(currentObject != null)
		{
			if(currentObject.getType().isObject())
			{
				List<ObjectField> fields = currentObject.getFields();

				for(int i = 0; i < fields.size(); i++)
				{
					ObjectField of = fields.get(i);
					settings.dataMapping.add(settings.new DataMapping(
							of.getName(), -1, null));
				}
			}
			else
			{
				settings.dataMapping.add(settings.new DataMapping(
						currentObject.getPath(), -1, null));
			}
		}

		mappingTableViewer.refresh();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.DatabaseQuerySettingsListener#dataMappingChanged()
	 */
	public void dataMappingChanged()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.DatabaseQuerySettingsListener#searchCriteriaChanged()
	 */
	public void searchCriteriaChanged()
	{
	}

	public class MappingContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			if(currentObject == null)
			{
				return new Object[0];
			}
			else
			{
				return settings.dataMapping.toArray();
			}
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	public class MappingLabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			DatabaseQuerySettingsStructure.DataMapping mapping =
				(DatabaseQuerySettingsStructure.DataMapping)element;

			if(columnIndex == 0)
			{
				return org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry()
										  .get("ICON_TINY_SQUARE");
			}
			else if(columnIndex == 1)
			{
				if(mapping.mappingType == 1)
				{
					return org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry()
											  .get("ICON_DOMAIN");
				}
			}

			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			DatabaseQuerySettingsStructure.DataMapping mapping =
				(DatabaseQuerySettingsStructure.DataMapping)element;

			if(columnIndex == 0)
			{
				return mapping.fieldName;
			}
			else
			{
				if(mapping == null)
				{
					return null;
				}
				else
				{
					switch(mapping.mappingType)
					{
						case -1: // not used and no change
							return "No Change";

						case 0: //static value
						case 1: //database column
							return mapping.mappingValue;
					}
				}
			}

			return null;
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

	public class MappingCellModifier implements ICellModifier
	{
		public boolean canModify(Object element, String property)
		{
			if(property.equals("Value"))
			{
				return true;
			}

			return false;
		}

		public Object getValue(Object element, String property)
		{
			return element;
		}

		public void modify(Object element, String property, Object value)
		{
			mappingTableViewer.refresh();
		}
	}

	public class ValueCellEditor extends DialogCellEditor
	{
		Label iconLabel;
		Composite comp;

		public ValueCellEditor(Composite parent)
		{
			super(parent);
		}

		protected Control createContents(Composite cell)
		{
			return super.createContents(cell);
		}

		protected void updateContents(Object value)
		{
			DatabaseQuerySettingsStructure.DataMapping mapping =
				(DatabaseQuerySettingsStructure.DataMapping)value;

			if(mapping != null)
			{
				if(mapping.mappingType == -1) //not affected
				{
					this.getDefaultLabel().setText("No Change");
				}
				else if(mapping.mappingType == 0) //static value
				{
					this.getDefaultLabel().setText(mapping.mappingValue);
				}
				else // database column
				{
					this.getDefaultLabel().setText(mapping.mappingValue);
				}
			}
			else
			{
				super.updateContents(value);
			}
		}

		protected Object openDialogBox(Control cellEditorWindow)
		{
			ValueDialog vd = new ValueDialog(cellEditorWindow.getShell());
			vd.setValue((DatabaseQuerySettingsStructure.DataMapping)this
				.getValue());
			vd.open();

			return this.getValue();
		}
	}

	public class ValueDialog extends FramedDialog
	{
		Button noChangeButton;
		Button databaseColumnButton;
		Button staticValueButton;
		Combo databaseColumnCombo;
		Text staticValueField;
		DatabaseQuerySettingsStructure.DataMapping value;
		Color darkBlue;
		Color lightBlue;
		int originalMappingType;
		String originalMappingValue;

		/**
		 * @param shellProvider
		 */
		public ValueDialog(Shell shell)
		{
			super(shell);
			this.setSideBarSize(40);
			this.setTitle("Select a value");
		}

		public void setValue(DatabaseQuerySettingsStructure.DataMapping value)
		{
			this.value = value;
			originalMappingType = value.mappingType;
			originalMappingValue = value.mappingValue;
		}

		public void updateValue()
		{
			if(noChangeButton.getSelection())
			{
				value.mappingType = -1;
				value.mappingValue = "";
			}
			else if(staticValueButton.getSelection())
			{
				value.mappingType = 0;
				value.mappingValue = staticValueField.getText();
			}
			else
			{
				value.mappingType = 1;
				value.mappingValue = databaseColumnCombo.getItem(databaseColumnCombo
						.getSelectionIndex());
			}
		}

		protected void createButtonBar(Composite parent)
		{
			parent.setLayout(new GridLayout(1, true));

			Composite buttons = new Composite(parent, SWT.NONE);
			buttons.setBackground(parent.getBackground());

			GridData buttonsData = new GridData(GridData.FILL_BOTH);
			buttonsData.horizontalAlignment = SWT.RIGHT;
			buttons.setLayoutData(buttonsData);

			RowLayout rl = new RowLayout();
			rl.pack = false;
			rl.spacing = 5;
			buttons.setLayout(rl);

			final Button okButton = new Button(buttons, SWT.PUSH);
			okButton.setText("Ok");
			okButton.addSelectionListener(new SelectionListener()
				{
					public void widgetSelected(SelectionEvent e)
					{
						okPressed();
					}

					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});

			final Button cancelButton = new Button(buttons, SWT.PUSH);
			cancelButton.setText("Cancel");
			cancelButton.addSelectionListener(new SelectionListener()
				{
					public void widgetSelected(SelectionEvent e)
					{
						cancelPressed();
					}

					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});
			if(Display.getCurrent().getDismissalAlignment() == SWT.RIGHT)
			{
				cancelButton.moveAbove(okButton);
			}
			this.getShell().setDefaultButton(okButton);
		}

		public void okPressed()
		{
			this.setReturnCode(SWT.OK);
			close();
		}

		public void cancelPressed()
		{
			value.mappingType = originalMappingType;
			value.mappingValue = originalMappingValue;
			this.setReturnCode(SWT.CANCEL);
			close();
		}

		protected void createDialogContents(Composite parent)
		{
			darkBlue = new Color(parent.getDisplay(), 77, 113, 179);
			lightBlue = new Color(parent.getDisplay(), 240, 243, 249);
			parent.addDisposeListener(new DisposeListener()
				{
					public void widgetDisposed(DisposeEvent e)
					{
						darkBlue.dispose();
						lightBlue.dispose();
					}
				});
			this.setFrameColor(darkBlue);
			this.setSideBarColor(lightBlue);

			GridLayout gl = new GridLayout(2, false);
			gl.marginTop = 20;
			parent.setLayout(gl);
			noChangeButton = new Button(parent, SWT.RADIO);
			noChangeButton.setText("Do not change this field");
			noChangeButton.setBackground(parent.getBackground());
			noChangeButton.setSelection(value.mappingType == -1);

			GridData gd = new GridData();
			gd.horizontalSpan = 2;
			noChangeButton.setLayoutData(gd);
			databaseColumnButton = new Button(parent, SWT.RADIO);
			databaseColumnButton.setText("Use the value from this table column");
			databaseColumnButton.setBackground(parent.getBackground());
			databaseColumnButton.setSelection(value.mappingType == 1);
			databaseColumnButton.setLayoutData(new GridData());
			databaseColumnCombo = new Combo(parent,
					SWT.READ_ONLY | SWT.DROP_DOWN | SWT.BORDER);
			databaseColumnCombo.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			staticValueButton = new Button(parent, SWT.RADIO);
			staticValueButton.setText("Use the value I've entered");
			staticValueButton.setBackground(parent.getBackground());
			staticValueButton.setSelection(value.mappingType == 0);
			staticValueButton.setLayoutData(new GridData());
			staticValueField = new Text(parent, SWT.BORDER | SWT.SINGLE);
			staticValueField.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));

			int columnSel = 0;
			List<IDatabase> databases =
				getElement().getDesign().getDocument().getProject().getDatabaseSet().getDatabases();

			for(int i = 0; i < databases.size(); i++)
			{
				IDatabase database = databases.get(i);

				if(database.getName().equals(settings.sourceDatabase))
				{
					List<IDatabaseTable> tables = database.getTables();

					for(int t = 0; t < tables.size(); t++)
					{
						IDatabaseTable table = tables.get(t);

						if(table.getName().equals(settings.sourceDatabaseTable))
						{
							List<IDatabaseTableColumn> columns = table.getColumns();

							for(int c = 0; c < columns.size(); c++)
							{
								IDatabaseTableColumn column = columns.get(c);
								databaseColumnCombo.add(column.getName());

								if(column.getName().equals(value.mappingValue))
								{
									columnSel = c;
								}
							}
						}
					}
				}
			}

			databaseColumnCombo.select(columnSel);

			if(value.mappingType == 0)
			{
				staticValueField.setText((value.mappingValue == null) ? ""
																	  : value.mappingValue);
			}

			databaseColumnCombo.addSelectionListener(new SelectionListener()
				{
					public void widgetSelected(SelectionEvent e)
					{
						updateValue();
					}

					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});
			staticValueField.addKeyListener(new KeyListener()
				{
					public void keyPressed(KeyEvent e)
					{
					}

					public void keyReleased(KeyEvent e)
					{
						updateValue();
					}
				});
			noChangeButton.addSelectionListener(new SelectionListener()
				{
					public void widgetSelected(SelectionEvent e)
					{
						if(noChangeButton.getSelection())
						{
							updateValue();
						}
					}

					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});
			staticValueButton.addSelectionListener(new SelectionListener()
				{
					public void widgetSelected(SelectionEvent e)
					{
						if(staticValueButton.getSelection())
						{
							updateValue();
						}
					}

					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});
			databaseColumnButton.addSelectionListener(new SelectionListener()
				{
					public void widgetSelected(SelectionEvent e)
					{
						if(databaseColumnButton.getSelection())
						{
							updateValue();
						}
					}

					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});
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
