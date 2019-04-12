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
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.vtp.desktop.core.dialogs.FramedDialog;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectSet;
import org.eclipse.vtp.desktop.model.core.IDatabase;
import org.eclipse.vtp.desktop.model.core.IDatabaseTable;
import org.eclipse.vtp.desktop.model.core.IDatabaseTableColumn;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.ObjectField;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.database.ui.DatabaseQueryInformationProvider;

/**
 * @author Trip
 *
 */
public class DatabaseQuerySearchCriteriaPropertiesPanel
	extends DesignElementPropertiesPanel implements DatabaseQuerySettingsListener
{
	DatabaseQueryInformationProvider queryElement;
	DatabaseQuerySettingsStructure settings;
	TableViewer criteriaViewer;
	Button resultLimitButton;
	Text resultLimitField;
	String[] comparisonNames =
		new String[]
		{
			"Equals", "Not Equals", "Less Than", "Less Than or Equal",
			"Greater Than", "Greater Than or Equal"
		};
	ComboBoxCellEditor comparisonEditor;
	ValueCellEditor valueEditor;
	IBusinessObjectSet businessObjectSet = null;

	public DatabaseQuerySearchCriteriaPropertiesPanel(
		PrimitiveElement dqe, DatabaseQuerySettingsStructure settings)
	{
		super("Search Criteria", dqe);
		this.queryElement = (DatabaseQueryInformationProvider)dqe.getInformationProvider();
		this.settings = settings;
		businessObjectSet = dqe.getDesign().getDocument().getProject().getBusinessObjectSet();
	}

	public void createControls(Composite parent)
	{
		settings.addSettingsListener(this);

		GridLayout gl = new GridLayout(2, false);
		parent.setLayout(gl);

		Table criteriaTable =
			new Table(parent,
				SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE | SWT.CHECK);
		criteriaTable.setHeaderVisible(true);

		TableColumn columnNameColumn = new TableColumn(criteriaTable, SWT.NONE);
		columnNameColumn.setText("Column Name");
		columnNameColumn.setWidth(150);

		TableColumn comparisonColumn = new TableColumn(criteriaTable, SWT.NONE);
		comparisonColumn.setText("Comparison");
		comparisonColumn.setWidth(80);

		TableColumn matchingValueColumn =
			new TableColumn(criteriaTable, SWT.NONE);
		matchingValueColumn.setText("Matching Value");
		matchingValueColumn.setWidth(200);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		criteriaTable.setLayoutData(gd);
		comparisonEditor = new ComboBoxCellEditor(criteriaTable,
				comparisonNames, SWT.READ_ONLY | SWT.DROP_DOWN);
		valueEditor = new ValueCellEditor(criteriaTable);
		criteriaViewer = new TableViewer(criteriaTable);
		criteriaViewer.setColumnProperties(new String[]
			{
				"Name", "Comparison", "Value"
			});
		criteriaViewer.setCellModifier(new ValueCellModifier());
		criteriaViewer.setCellEditors(new CellEditor[]
			{
				null, comparisonEditor, valueEditor
			});
		criteriaViewer.setContentProvider(new CriteriaContentProvider());
		criteriaViewer.setLabelProvider(new CriteriaLabelProvider());
		criteriaViewer.setInput(this);
		resultLimitButton = new Button(parent, SWT.CHECK);
		resultLimitButton.setText("Limit results to this many records");
		resultLimitButton.setSelection(settings.resultLimit != -1);
		resultLimitButton.setEnabled(!settings.targetVariableType.isObject() && settings.targetVariableType.getPrimitiveType() == Primitive.ARRAY);
		resultLimitButton.setBackground(parent.getBackground());
		resultLimitButton.setLayoutData(new GridData());
		resultLimitButton.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					if(resultLimitButton.getSelection())
					{
						settings.resultLimit = Integer.parseInt((
									resultLimitField.getText() == null
								) ? "0"
								  : (
									resultLimitField.getText().equals("") ? "0"
																		  : resultLimitField
									.getText()
								));
					}
					else
					{
						settings.resultLimit = -1;
					}
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		resultLimitField = new Text(parent, SWT.BORDER);

		if(settings.resultLimit != -1)
		{
			resultLimitField.setText(Integer.toString(settings.resultLimit));
		}

		resultLimitField.addKeyListener(new KeyListener()
			{
				public void keyPressed(KeyEvent e)
				{
				}

				public void keyReleased(KeyEvent e)
				{
					if(resultLimitButton.getSelection())
					{
						settings.resultLimit = Integer.parseInt((
									resultLimitField.getText() == null
								) ? "0"
								  : (
									resultLimitField.getText().equals("") ? "0"
																		  : resultLimitField
									.getText()
								));
					}
					else
					{
						settings.resultLimit = -1;
					}
				}
			});
		resultLimitField.addVerifyListener(new VerifyListener()
		{

			public void verifyText(VerifyEvent e)
            {
				char[] chars = e.text.toCharArray();
				for(int i = 0; i < chars.length; i++)
				{
					if(!Character.isDigit(chars[i]))
					{
						e.doit = false;
						return;
					}
				}
            }
			
		});
		GridData rlfdata = new GridData();
		rlfdata.widthHint = 60;
		resultLimitField.setLayoutData(rlfdata);

		for(int i = 0; i < settings.criteria.size(); i++)
		{
			DatabaseQuerySettingsStructure.SelectionCriteria criteria =
				settings.criteria.get(i);
			criteriaTable.getItem(i).setChecked(criteria.comparison != -1);
		}

		criteriaTable.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					if(e.detail == 32)
					{
						DatabaseQuerySettingsStructure.SelectionCriteria criteria =
							(DatabaseQuerySettingsStructure.SelectionCriteria)((TableItem)e.item).getData();
						if(((TableItem)e.item).getChecked())
						{
							criteria.comparison = 0;
						}
						else
						{
							criteria.comparison = -1;
							criteria.type = -1;
							criteria.value = null;
						}
						criteriaViewer.refresh();
					}
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
	}

	public void save()
	{
	}
	
	public void cancel()
	{
		
	}

	public void targetVariableChanged()
	{
		resultLimitButton.setEnabled(!settings.targetVariableType.isObject() && settings.targetVariableType.getPrimitiveType() == Primitive.ARRAY);
		resultLimitButton.setSelection(false);
		resultLimitField.setText("");
	}

	public void sourceDatabaseChanged()
	{
		settings.criteria.clear();

		if((settings.sourceDatabase != null)
				&& (settings.sourceDatabaseTable != null)
				&& !settings.sourceDatabase.equals("")
				&& !settings.sourceDatabaseTable.equals(""))
		{
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
								settings.criteria.add(settings.new SelectionCriteria(
										column.getName()));
							}
						}
					}
				}
			}
		}

		this.criteriaViewer.refresh();
	}

	public void dataMappingChanged()
	{
	}

	public void searchCriteriaChanged()
	{
	}

	public class CriteriaContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			return settings.criteria.toArray();
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	public class CriteriaLabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			DatabaseQuerySettingsStructure.SelectionCriteria criteria =
				(DatabaseQuerySettingsStructure.SelectionCriteria)element;

			if(columnIndex == 0)
			{
				return criteria.columnName;
			}
			else if(columnIndex == 1)
			{
				if(criteria.comparison != -1)
				{
					return comparisonNames[criteria.comparison];
				}
			}
			else if(columnIndex == 2)
			{
				return criteria.value;
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

	public class ValueCellModifier implements ICellModifier
	{
		public boolean canModify(Object element, String property)
		{
			DatabaseQuerySettingsStructure.SelectionCriteria criteria =
				(DatabaseQuerySettingsStructure.SelectionCriteria)element;

			if(property.equals("Value") || property.equals("Comparison"))
			{
				if(criteria.comparison != -1)
				{
					return true;
				}
			}

			return false;
		}

		public Object getValue(Object element, String property)
		{
			DatabaseQuerySettingsStructure.SelectionCriteria criteria =
				(DatabaseQuerySettingsStructure.SelectionCriteria)element;

			if(property.equals("Comparison"))
			{
				return new Integer(criteria.comparison);
			}

			return element;
		}

		public void modify(Object element, String property, Object value)
		{
			DatabaseQuerySettingsStructure.SelectionCriteria criteria =
				(DatabaseQuerySettingsStructure.SelectionCriteria)((TableItem)element)
				.getData();

			if(property.equals("Comparison"))
			{
				criteria.comparison = ((Integer)value).intValue();
			}

			criteriaViewer.refresh();
		}
	}

	public class ValueCellEditor extends DialogCellEditor
	{
		public ValueCellEditor(Composite parent)
		{
			super(parent);
		}

		protected void updateContents(Object value)
		{
			DatabaseQuerySettingsStructure.SelectionCriteria mapping =
				(DatabaseQuerySettingsStructure.SelectionCriteria)value;

			if(mapping != null)
			{
				if(mapping.type == -1) //not used
				{
					this.getDefaultLabel().setText("Not Used");
				}
				else if(mapping.type == 0) //static value
				{
					this.getDefaultLabel().setText(mapping.value);
				}
				else // variable
				{
					this.getDefaultLabel().setText(mapping.value);
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
			vd.setValue((DatabaseQuerySettingsStructure.SelectionCriteria)this
				.getValue());
			vd.open();

			return this.getValue();
		}
	}

	public class ValueDialog extends FramedDialog
	{
		Button variableButton;
		TreeViewer variableViewer;
		Button staticValueButton;
		Text staticValueField;
		DatabaseQuerySettingsStructure.SelectionCriteria value;
		Color darkBlue;
		Color lightBlue;
		int originalType;
		String originalValue;
		List<Variable> vars;

		/**
		 * @param shellProvider
		 */
		public ValueDialog(Shell shell)
		{
			super(shell);
			this.setSideBarSize(40);
			this.setTitle("Select a value");
			vars = getElement().getDesign().getVariablesFor(getElement());
		}

		public void setValue(
			DatabaseQuerySettingsStructure.SelectionCriteria value)
		{
			this.value = value;
			originalType = value.type;
			originalValue = value.value;
		}

		public void updateValue()
		{
			if(staticValueButton.getSelection())
			{
				value.type = 0;
				value.value = staticValueField.getText();
			}
			else
			{
				value.type = 1;

				if(!variableViewer.getSelection().isEmpty())
				{
					value.value = ((ObjectDefinition)((IStructuredSelection)variableViewer
						.getSelection()).getFirstElement()).getPath();
				}
				else
				{
					value.value = "";
				}
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
			value.type = originalType;
			value.value = originalValue;
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
			parent.setLayout(new GridLayout(2, false));

			Label preambleLabel = new Label(parent, SWT.NONE);
			preambleLabel.setText("Select a value to match against");
			preambleLabel.setBackground(parent.getBackground());

			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			preambleLabel.setLayoutData(gd);
			variableButton = new Button(parent, SWT.RADIO);
			variableButton.setText("This variable's current value");
			variableButton.setBackground(parent.getBackground());
			variableButton.setSelection(value.type == 1);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			variableButton.setLayoutData(gd);

			Tree variableTree =
				new Tree(parent, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
			gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalIndent = 10;
			gd.horizontalSpan = 2;
			variableTree.setLayoutData(gd);
			variableViewer = new TreeViewer(variableTree);
			variableViewer.setContentProvider(new VariableContentProvider());
			variableViewer.setLabelProvider(new VariableLabelProvider());
			variableViewer.setInput(this);
			staticValueButton = new Button(parent, SWT.RADIO);
			staticValueButton.setText("The value I've entered");
			staticValueButton.setBackground(parent.getBackground());
			staticValueButton.setSelection(value.type <= 0);
			gd = new GridData();
			staticValueButton.setLayoutData(gd);
			staticValueField = new Text(parent, SWT.BORDER | SWT.SINGLE);
			gd = new GridData(GridData.FILL_HORIZONTAL);

			if(value.type == 1)
			{
				for(int i = 0; i < vars.size(); i++)
				{
					Variable v = vars.get(i);

					if(v.getName().equals(value.value))
					{
						variableViewer.setSelection(new StructuredSelection(v));
					}
					else if((value.value != null)
							&& value.value.startsWith(v.getName()))
					{
						List<ObjectField> objectFields = v.getFields();

						for(int f = 0; f < objectFields.size(); f++)
						{
							ObjectField of = objectFields.get(f);

							if(of.getPath().equals(value.value))
							{
								variableViewer.setSelection(new StructuredSelection(
										of));
							}
						}
					}
				}
			}

			staticValueField.setLayoutData(gd);

			if(value.type == 0)
			{
				staticValueField.setText((value.value == null) ? "" : value.value);
			}

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
			variableButton.addSelectionListener(new SelectionListener()
				{
					public void widgetSelected(SelectionEvent e)
					{
						if(variableButton.getSelection())
						{
							updateValue();
						}
					}

					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});
			variableViewer.addSelectionChangedListener(new ISelectionChangedListener()
				{
					public void selectionChanged(SelectionChangedEvent event)
					{
						updateValue();
					}
				});
		}

		public class VariableContentProvider implements ITreeContentProvider
		{
			public Object[] getElements(Object inputElement)
			{
				return vars.toArray();
			}

			public void dispose()
			{
			}

			public void inputChanged(Viewer viewer, Object oldInput,
				Object newInput)
			{
			}

			public Object[] getChildren(Object parentElement)
			{
				return ((ObjectDefinition)parentElement).getFields().toArray();
			}

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

			public boolean hasChildren(Object element)
			{
				return true;
			}
		}

		public class VariableLabelProvider extends LabelProvider
		{
			public Image getImage(Object element)
			{
				return org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry()
										  .get("ICON_TINY_SQUARE");
			}

			public String getText(Object element)
			{
				return ((ObjectDefinition)element).getName();
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
