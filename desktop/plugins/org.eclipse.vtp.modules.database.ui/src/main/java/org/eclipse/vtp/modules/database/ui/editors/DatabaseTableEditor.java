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
/**
 *
 */
package org.eclipse.vtp.modules.database.ui.editors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.vtp.desktop.model.core.ColumnType;
import org.eclipse.vtp.desktop.model.core.IDatabaseTable;
import org.eclipse.vtp.desktop.model.core.IDatabaseTableColumn;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Trip
 *
 */
public class DatabaseTableEditor extends EditorPart
{
	IDatabaseTable databaseTable;
	boolean dirty = false;
	TableViewer viewer;
	List<ColumnRecord> columnRecords = new ArrayList<ColumnRecord>();
	TextCellEditor nameEditor;
	ComboBoxCellEditor typeEditor;
	TextCellEditor lengthEditor;
	CheckboxCellEditor nullableEditor;
	CheckboxCellEditor autoIncrementEditor;
	List<String> currentTypes = new ArrayList<String>();
	int tumbler = 0;

	/**
	 *
	 */
	public DatabaseTableEditor()
	{
		super();
		currentTypes.add("Varchar");
		currentTypes.add("Number");
		currentTypes.add("Big Number");
		currentTypes.add("Decimal");
		currentTypes.add("Big Decimal");
		currentTypes.add("Boolean");
		currentTypes.add("DateTime");
		currentTypes.add("Text");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{
		try
		{
			Document doc =
				DocumentBuilderFactory.newInstance().newDocumentBuilder()
									  .newDocument();
			Element rootElement = doc.createElement("database-table");
			doc.appendChild(rootElement);
			rootElement.setAttribute("name", databaseTable.getName());

			Element fields =
				rootElement.getOwnerDocument().createElement("columns");
			rootElement.appendChild(fields);

			for(int i = 0; i < columnRecords.size(); i++)
			{
				ColumnRecord fr = columnRecords.get(i);
				Element fieldElement =
					fields.getOwnerDocument().createElement("column");
				fields.appendChild(fieldElement);
				fieldElement.setAttribute("name", fr.name);

				ColumnType dt =
					ColumnType.custom(fr.type, fr.length, fr.autoIncrement,
						fr.nullable);
				dt.write(fieldElement);
			}

			DOMSource source = new DOMSource(doc);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer trans =
				TransformerFactory.newInstance().newTransformer();
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			trans.transform(source, new XMLWriter(baos).toXMLResult());
			databaseTable.write(new ByteArrayInputStream(baos.toByteArray()));
			dirty = false;
			this.firePropertyChange(IEditorPart.PROP_DIRTY);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(FactoryConfigurationError e)
		{
			e.printStackTrace();
		}
		catch(TransformerFactoryConfigurationError e)
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input)
		throws PartInitException
	{
		if(!(input instanceof FileEditorInput))
		{
			throw new PartInitException("Cannot edit: " + input);
		}

		setInput(input);
		setSite(site);
		setPartName(input.getName());
		IFile file = ((FileEditorInput)input).getFile();
		databaseTable = (IDatabaseTable)WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(file);

		List<IDatabaseTableColumn> fields = databaseTable.getColumns();
		Iterator<IDatabaseTableColumn> iterator = fields.iterator();

		while(iterator.hasNext())
		{
			IDatabaseTableColumn bof = iterator.next();
			ColumnRecord fr = new ColumnRecord();
			fr.name = bof.getName();
			fr.type = bof.getColumnType().getTypeName();
			fr.autoIncrement = bof.getColumnType().isAutoIncrement();
			fr.nullable = bof.getColumnType().isNullable();
			fr.length = bof.getColumnType().getLength();
			columnRecords.add(fr);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	public boolean isDirty()
	{
		return dirty;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
//		Composite comp = new Composite(parent, SWT.NONE);
		Table table = new Table(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);

		TableColumn fieldName = new TableColumn(table, SWT.NONE);
		fieldName.setText("Name");
		fieldName.setWidth(150);

		TableColumn dataTypeColumn = new TableColumn(table, SWT.NONE);
		dataTypeColumn.setText("Type");
		dataTypeColumn.setWidth(200);

		TableColumn lengthColumn = new TableColumn(table, SWT.NONE);
		lengthColumn.setText("Length");
		lengthColumn.setWidth(60);

		TableColumn nullableColumn = new TableColumn(table, SWT.NONE);
		nullableColumn.setText("Nullable");
		nullableColumn.setWidth(80);

		TableColumn autoIncrementColumn = new TableColumn(table, SWT.NONE);
		autoIncrementColumn.setText("Auto Inc");
		autoIncrementColumn.setWidth(80);
		nameEditor = new TextCellEditor(table);
		nameEditor.setValidator(new ICellEditorValidator()
			{
				public String isValid(Object value)
				{
					String text = (String)value;

					if(text == null)
					{
						text = "";
					}

					ColumnRecord fr =
						(ColumnRecord)((IStructuredSelection)viewer.getSelection())
						.getFirstElement();

					for(int i = 0; i < columnRecords.size(); i++)
					{
						if(fr != columnRecords.get(i))
						{
							if(columnRecords.get(i).name
									.equalsIgnoreCase(text))
							{
								return "A column with that name already exists.";
							}
						}
					}

					return null;
				}
			});
		nameEditor.addListener(new ICellEditorListener()
			{
				public void applyEditorValue()
				{
				}

				public void cancelEditor()
				{
				}

				public void editorValueChanged(boolean oldValidState,
					boolean newValidState)
				{
					if(!newValidState)
					{
						nameEditor.getControl()
								  .setForeground(nameEditor.getControl()
														   .getDisplay()
														   .getSystemColor(SWT.COLOR_RED));
					}
					else
					{
						nameEditor.getControl()
								  .setForeground(nameEditor.getControl()
														   .getDisplay()
														   .getSystemColor(SWT.COLOR_BLACK));
					}
				}
			});
		typeEditor = new ComboBoxCellEditor(table,
				currentTypes.toArray(new String[currentTypes.size()]),
				SWT.READ_ONLY | SWT.DROP_DOWN);
		lengthEditor = new TextCellEditor(table);
		lengthEditor.setValidator(new ICellEditorValidator()
			{
				public String isValid(Object value)
				{
					String text = (String)value;

					if(text == null)
					{
						return "Must be a number";
					}

					try
					{
						Integer.parseInt(text);
					}
					catch(Exception e)
					{
						return "Must be a number";
					}

					return null;
				}
			});
		lengthEditor.addListener(new ICellEditorListener()
			{
				public void applyEditorValue()
				{
				}

				public void cancelEditor()
				{
				}

				public void editorValueChanged(boolean oldValidState,
					boolean newValidState)
				{
					if(!newValidState)
					{
						lengthEditor.getControl()
									.setForeground(nameEditor.getControl()
															 .getDisplay()
															 .getSystemColor(SWT.COLOR_RED));
					}
					else
					{
						lengthEditor.getControl()
									.setForeground(nameEditor.getControl()
															 .getDisplay()
															 .getSystemColor(SWT.COLOR_BLACK));
					}
				}
			});
		nullableEditor = new CheckboxCellEditor(table);
		autoIncrementEditor = new CheckboxCellEditor(table);
		viewer = new TableViewer(table);
		viewer.setColumnProperties(new String[]
			{
				"Name", "Type", "Length", "Nullable", "AutoIncrement"
			});
		viewer.setContentProvider(new FieldContentProvider());
		viewer.setLabelProvider(new FieldLabelProvider());
		viewer.setCellModifier(new FieldCellModifier());
		viewer.setCellEditors(new CellEditor[]
			{
				nameEditor, typeEditor, lengthEditor, nullableEditor,
				autoIncrementEditor
			});
		viewer.setInput(this);
		hookContextMenu();
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
			{
				public void menuAboutToShow(IMenuManager manager)
				{
					DatabaseTableEditor.this.fillContextMenu(manager);
				}
			});

		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(new Action("Add Column")
			{
				public void run()
				{
					ColumnRecord newField = new ColumnRecord();
					newField.name = "";
					newField.type = "Varchar";
					newField.length = 45;
					newField.nullable = false;
					newField.autoIncrement = false;
					columnRecords.add(newField);
					viewer.refresh();
					viewer.editElement(newField, 0);
					fireModified();
				}
			});

		if(!viewer.getSelection().isEmpty())
		{
			final ColumnRecord fr =
				(ColumnRecord)((IStructuredSelection)viewer.getSelection())
				.getFirstElement();
			manager.add(new Action("Remove Column")
				{
					public void run()
					{
						MessageBox mb =
							new MessageBox(viewer.getControl().getShell(),
								SWT.YES | SWT.NO | SWT.ICON_WARNING);
						mb.setMessage("Are you sure you want to delete this?");

						int result = mb.open();

						if(result == SWT.YES)
						{
							columnRecords.remove(fr);
							viewer.refresh();
							fireModified();
						}
					}
				});
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
	}

	private void fireModified()
	{
		dirty = true;
		this.firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	public class FieldContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			tumbler = 0;

			return columnRecords.toArray();
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	public class FieldLabelProvider implements ITableLabelProvider,
		IColorProvider
	{
		Color alternateBackground =
			new Color(viewer.getControl().getDisplay(), 216, 238, 255);
		Color background =
			viewer.getControl().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		Color foreground =
			viewer.getControl().getDisplay().getSystemColor(SWT.COLOR_BLACK);

		public FieldLabelProvider()
		{
			super();
		}

		public Image getColumnImage(Object element, int columnIndex)
		{
			ColumnRecord fr = (ColumnRecord)element;

			if(columnIndex == 0)
			{
				return org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry()
										  .get("ICON_TINY_SQUARE");
			}
			else if(columnIndex == 3)
			{
				if(fr.nullable)
				{
					return org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry()
											  .get("ICON_CHECKBOX_TRUE");
				}
				else
				{
					return org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry()
											  .get("ICON_CHECKBOX_FALSE");
				}
			}
			else if(columnIndex == 4)
			{
				if(fr.type.equals("Big Number") || fr.type.equals("Number"))
				{
					if(fr.autoIncrement)
					{
						return org.eclipse.vtp.desktop.core.Activator.getDefault()
												  .getImageRegistry()
												  .get("ICON_CHECKBOX_TRUE");
					}
					else
					{
						return org.eclipse.vtp.desktop.core.Activator.getDefault()
												  .getImageRegistry()
												  .get("ICON_CHECKBOX_FALSE");
					}
				}
			}

			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			ColumnRecord fr = (ColumnRecord)element;

			if(columnIndex == 0)
			{
				return fr.name;
			}
			else if(columnIndex == 1)
			{
				return fr.type;
			}
			else if(columnIndex == 2)
			{
				return Integer.toString(fr.length);
			}

			return null;
		}

		public void addListener(ILabelProviderListener listener)
		{
		}

		public void dispose()
		{
			this.alternateBackground.dispose();
		}

		public boolean isLabelProperty(Object element, String property)
		{
			return false;
		}

		public void removeListener(ILabelProviderListener listener)
		{
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
		 */
		public Color getForeground(Object element)
		{
			return foreground;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
		 */
		public Color getBackground(Object element)
		{
			Color bg = null;

			if((tumbler % 2) == 0)
			{
				bg = background;
			}
			else
			{
				bg = this.alternateBackground;
			}

			tumbler++;

			return bg;
		}
	}

	public class FieldCellModifier implements ICellModifier
	{
		public boolean canModify(Object element, String property)
		{
			ColumnRecord fr = (ColumnRecord)element;

			if(property.equals("Nullable") || property.equals("Type")
					|| property.equals("Name"))
			{
				return true;
			}
			else if(property.equals("Length"))
			{
				return fr.type.equals("Varchar");
			}
			else if(property.equals("AutoIncrement"))
			{
				return fr.type.equals("Big Number") || fr.type.equals("Number");
			}

			return false;
		}

		public Object getValue(Object element, String property)
		{
			ColumnRecord fr = (ColumnRecord)element;

			if(property.equals("Nullable"))
			{
				return new Boolean(fr.nullable);
			}
			else if(property.equals("Length"))
			{
				return Integer.toString(fr.length);
			}
			else if(property.equals("Name"))
			{
				return (fr.name == null) ? "" : fr.name;
			}
			else if(property.equals("Type"))
			{
				if(fr.type.equals("Varchar"))
				{
					return new Integer(0);
				}
				else if(fr.type.equals("Number"))
				{
					return new Integer(1);
				}
				else if(fr.type.equals("Big Number"))
				{
					return new Integer(2);
				}
				else if(fr.type.equals("Decimal"))
				{
					return new Integer(3);
				}
				else if(fr.type.equals("Big Decimal"))
				{
					return new Integer(4);
				}
				else if(fr.type.equals("Boolean"))
				{
					return new Integer(5);
				}
				else if(fr.type.equals("DateTime"))
				{
					return new Integer(6);
				}
				else if(fr.type.equals("Text"))
				{
					return new Integer(7);
				}
			}
			else if(property.equals("AutoIncrement"))
			{
				return new Boolean(fr.autoIncrement);
			}

			return null;
		}

		public void modify(Object element, String property, Object value)
		{
			TableItem ti = (TableItem)element;
			ColumnRecord fr = (ColumnRecord)ti.getData();

			if(property.equals("Nullable"))
			{
				boolean b = fr.nullable;
				fr.nullable = ((Boolean)value).booleanValue();

				if(b != fr.nullable)
				{
					fireModified();
				}
			}
			else if(property.equals("AutoIncrement"))
			{
				boolean b = fr.autoIncrement;
				fr.autoIncrement = ((Boolean)value).booleanValue();

				if(b != fr.autoIncrement)
				{
					fireModified();
				}
			}
			else if(property.equals("Name"))
			{
				if(value != null)
				{
					String oldName = fr.name;
					fr.name = (String)value;

					if(!fr.name.equals(oldName))
					{
						fireModified();
					}
				}
			}
			else if(property.equals("Length"))
			{
				if(value != null)
				{
					int oldLength = fr.length;
					fr.length = Integer.parseInt((String)value);

					if(fr.length != oldLength)
					{
						fireModified();
					}
				}
			}
			else if(property.equals("Type"))
			{
				int sel = ((Integer)value).intValue();
				String oldType = fr.type;
				fr.type = currentTypes.get(sel);

				if(!fr.type.equals(oldType))
				{
					if(fr.type.equals("Varchar"))
					{
						fr.length = 45;
					}
					else if(fr.type.equals("Number")
							|| fr.type.equals("Decimal"))
					{
						fr.length = 4;
					}
					else if(fr.type.equals("Big Number")
							|| fr.type.equals("Big Decimal"))
					{
						fr.length = 8;
					}
					else if(fr.type.equals("Text"))
					{
						fr.length = 16;
					}
					else if(fr.type.equals("Boolean"))
					{
						fr.length = 1;
					}

					fr.autoIncrement = false;
					fireModified();
				}
			}

			viewer.refresh(true);
		}
	}

	public class ColumnRecord
	{
		String name;
		String type;
		boolean nullable;
		boolean autoIncrement;
		int length;
	}
}
