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
package org.eclipse.vtp.desktop.editors.core.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.vtp.desktop.core.Activator;
import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.IBusinessObject;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectField;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.internal.BusinessObject;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Trip
 *
 */
public class BusinessObjectEditor extends EditorPart {
	BusinessObject bo;
	boolean dirty = false;
	TableViewer viewer;
	List<FieldRecord> fieldRecords = new ArrayList<FieldRecord>();
	TextCellEditor nameEditor;
	ComboBoxCellEditor typeEditor;
	ComboBoxCellEditor precisionEditor;
	ComboBoxCellEditor styleEditor;
	CheckboxCellEditor securedEditor;
	TextCellEditor initialValueEditor;
	List<String> currentTypes = new ArrayList<String>();

	/**
	 *
	 */
	public BusinessObjectEditor() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element rootElement = doc.createElement("business-object");
			doc.appendChild(rootElement);
			rootElement.setAttribute("id", bo.getId());
			rootElement.setAttribute("name", bo.getName());

			Element fields = rootElement.getOwnerDocument().createElement("fields");
			rootElement.appendChild(fields);

			for (FieldRecord fr : fieldRecords) {
				Element fieldElement = fields.getOwnerDocument().createElement("field");
				fields.appendChild(fieldElement);
				fieldElement.setAttribute("name", fr.name);
				fieldElement.setAttribute("initialValue", fr.initialValue);
				fieldElement.setAttribute("secured", Boolean.toString(fr.secured));
				fr.type.write(fieldElement);
			}

			DOMSource source = new DOMSource(doc);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer trans = TransformerFactory.newInstance().newTransformer();
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			trans.transform(source, new XMLWriter(baos).toXMLResult());
			bo.write(new ByteArrayInputStream(baos.toByteArray()));
			dirty = false;
			this.firePropertyChange(IEditorPart.PROP_DIRTY);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof FileEditorInput)) {
			throw new PartInitException("Cannot edit: " + input);
		}

		setInput(input);
		setSite(site);
		setPartName(input.getName());
		IFile file = ((FileEditorInput) input).getFile();
		IProject project = file.getProject();
		try {
			IWorkflowProject workflowProject = WorkflowCore.getDefault().getWorkflowModel()
					.convertToWorkflowProject(project);
			List<IBusinessObject> dbs = workflowProject.getBusinessObjectSet().getBusinessObjects();
			for (IBusinessObject bus : dbs) {
				if (bus.getUnderlyingFile().equals(file)) {
					bo = (BusinessObject) bus;
					break;
				}
			}
			if (bo == null) {
				throw new PartInitException("database table file not associated correctly");
			}
		} catch (CoreException e) {
			e.printStackTrace();
			throw new PartInitException("not in application project", e);
		}

		List<IBusinessObjectField> fields = bo.getFields();

		for (IBusinessObjectField bof : fields) {
			FieldRecord fr = new FieldRecord();
			fr.name = bof.getName();
			fr.type = bof.getDataType();
			fr.initialValue = bof.getInitialValue();
			fr.secured = bof.isSecured();
			fieldRecords.add(fr);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	public boolean isDirty() {
		return dirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.
	 * Composite)
	 */
	public void createPartControl(Composite parent) {
//		Composite comp = new Composite(parent, SWT.NONE);
		Table table = new Table(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);

		TableColumn fieldLock = new TableColumn(table, SWT.NONE);
		fieldLock.setImage(Activator.getDefault().getImageRegistry().get("ICON_LOCK"));
		fieldLock.setWidth(23);

		TableColumn fieldName = new TableColumn(table, SWT.NONE);
		fieldName.setText("Name");
		fieldName.setWidth(200);

		TableColumn dataType = new TableColumn(table, SWT.NONE);
		dataType.setText("Type");
		dataType.setWidth(150);

		TableColumn precision = new TableColumn(table, SWT.NONE);
		precision.setText("Precision");
		precision.setWidth(60);

		TableColumn initialValue = new TableColumn(table, SWT.NONE);
		initialValue.setText("Initial Value");
		initialValue.setWidth(200);
		nameEditor = new TextCellEditor(table);
		nameEditor.setValidator(new ICellEditorValidator() {
			public String isValid(Object value) {
				String text = (String) value;

				if (text == null) {
					text = "";
				}

				FieldRecord fr = (FieldRecord) ((IStructuredSelection) viewer.getSelection()).getFirstElement();

				for (int i = 0; i < fieldRecords.size(); i++) {
					if (fr != fieldRecords.get(i)) {
						if (fieldRecords.get(i).name.equals(text)) {
							return "A field with that name already exists.";
						}
					}
				}

				return null;
			}
		});
		nameEditor.addListener(new ICellEditorListener() {
			public void applyEditorValue() {
			}

			public void cancelEditor() {
			}

			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
				if (!newValidState) {
					nameEditor.getControl()
							.setForeground(nameEditor.getControl().getDisplay().getSystemColor(SWT.COLOR_RED));
				} else {
					nameEditor.getControl()
							.setForeground(nameEditor.getControl().getDisplay().getSystemColor(SWT.COLOR_BLACK));
				}
			}
		});
		typeEditor = new ComboBoxCellEditor(table,
				new String[] { "String", "Number", "Decimal", "Boolean", "DateTime", "Array", "Map" },
				SWT.READ_ONLY | SWT.DROP_DOWN);
		precisionEditor = new ComboBoxCellEditor(table, new String[] { "Single", "Double", "Absolute" },
				SWT.READ_ONLY | SWT.DROP_DOWN);
		initialValueEditor = new TextCellEditor(table);
		securedEditor = new CheckboxCellEditor(table);
		viewer = new TableViewer(table);
		viewer.setColumnProperties(new String[] { "Secured", "Name", "Type", "Precision", "Initial" });
		viewer.setContentProvider(new FieldContentProvider());
		viewer.setLabelProvider(new FieldLabelProvider());
		viewer.setCellModifier(new FieldCellModifier());
		viewer.setCellEditors(
				new CellEditor[] { securedEditor, nameEditor, typeEditor, precisionEditor, initialValueEditor });
		viewer.setInput(this);
		hookContextMenu();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				BusinessObjectEditor.this.fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(new Action("Add Field") {
			public void run() {
				FieldRecord newField = new FieldRecord();
				newField.name = "";
				newField.type = FieldType.STRING;
				newField.initialValue = "";
				fieldRecords.add(newField);
				viewer.refresh();
				viewer.editElement(newField, 1);
				fireModified();
			}
		});

		if (!viewer.getSelection().isEmpty()) {
			final FieldRecord fr = (FieldRecord) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
			manager.add(new Action("Remove Field") {
				public void run() {
					MessageBox mb = new MessageBox(viewer.getControl().getShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
					mb.setMessage("Are you sure you want to delete this?");

					int result = mb.open();

					if (result == SWT.YES) {
						fieldRecords.remove(fr);
						viewer.refresh();
						fireModified();
					}
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}

	private void fireModified() {
		dirty = true;
		this.firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	public class FieldContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			return fieldRecords.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	public class FieldLabelProvider implements ITableLabelProvider, IColorProvider {
		Color alternateBackground = new Color(viewer.getControl().getDisplay(), 216, 238, 255);
		Color background = viewer.getControl().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		Color foreground = viewer.getControl().getDisplay().getSystemColor(SWT.COLOR_BLACK);

		public FieldLabelProvider() {
			super();
		}

		public Image getColumnImage(Object element, int columnIndex) {
			FieldRecord fr = (FieldRecord) element;

			if (columnIndex == 0) {
				if (fr.secured)
					return Activator.getDefault().getImageRegistry().get("ICON_LOCK");
			} else if (columnIndex == 1) {
				return Activator.getDefault().getImageRegistry().get("ICON_TINY_SQUARE");
			}

			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			FieldRecord fr = (FieldRecord) element;

			if (columnIndex == 0) {
				return "";
			} else if (columnIndex == 1) {
				return fr.name;
			} else if (columnIndex == 2) {
				StringBuilder builder = new StringBuilder(
						fr.type.isObject() ? fr.type.getObjectType().getName() : fr.type.getPrimitiveType().getName());
				if (fr.type.hasBaseType()) {
					builder.append(" <");
					if (fr.type.isObjectBaseType())
						builder.append(fr.type.getObjectBaseType().getName());
					else
						builder.append(fr.type.getPrimitiveBaseType().getName());
					builder.append(">");
				}
				return builder.toString();
			} else if (columnIndex == 3) {
				if (!fr.type.isObject() && fr.type.getPrimitiveType().hasPrecision()) {
					if (fr.type.getPrecision() == 0) // single
					{
						return "Single";
					} else if (fr.type.getPrecision() == 1) {
						return "Double";
					} else if (fr.type.getPrecision() == 2) {
						return "Absolute";
					}
				}

				return "";
			} else if (columnIndex == 4) {
				return (fr.initialValue == null) ? "" : fr.initialValue;
			}

			return null;
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
			this.alternateBackground.dispose();
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
		 */
		public Color getForeground(Object element) {
			return foreground;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
		 */
		public Color getBackground(Object element) {
			Color bg = null;
			for (int i = 0; i < fieldRecords.size(); i++) {
				if (fieldRecords.get(i).equals(element)) {
					if ((i % 2) == 0) {
						bg = background;
					} else {
						bg = this.alternateBackground;
					}
					break;
				}
			}
			return bg;
		}
	}

	public class FieldCellModifier implements ICellModifier {
		public boolean canModify(Object element, String property) {
			FieldRecord fr = (FieldRecord) element;

			if (property.equals("Type") || property.equals("Name") || property.equals("Initial")) {
				return true;
			} else if (property.equals("Precision")) {
				return !fr.type.isObject() && fr.type.getPrimitiveType().hasPrecision();
			} else if (property.equals("Secured"))
				return true;

			return false;
		}

		public Object getValue(Object element, String property) {
			FieldRecord fr = (FieldRecord) element;

			if (property.equals("Secured"))
				return new Boolean(fr.secured);
			else if (property.equals("Precision")) {
				return new Integer(fr.type.getPrecision());
			} else if (property.equals("Name")) {
				return (fr.name == null) ? "" : fr.name;
			} else if (property.equals("Initial")) {
				return (fr.initialValue == null) ? "" : fr.initialValue;
			} else if (property.equals("Type")) {
				// build new list of object types
				currentTypes.clear();
				currentTypes.add("String");
				currentTypes.add("Number");
				currentTypes.add("Decimal");
				currentTypes.add("Boolean");
				currentTypes.add("DateTime");
				currentTypes.add("Array");
				currentTypes.add("Map");

				int sel = -1;
				List<IBusinessObject> bos = bo.getBusinessObjectSet().getBusinessObjects();

				for (int i = 0; i < bos.size(); i++) {
					IBusinessObject b = bos.get(i);

					if (!b.getName().equals(bo.getName())) {
						currentTypes.add(b.getName());

						if (fr.type.isObject() && b.getName().equals(fr.type.getObjectType().getName())) {
							sel = i + 7;
						}
					}
				}

				typeEditor.setItems(currentTypes.toArray(new String[currentTypes.size()]));

				if (sel == -1)
					switch (fr.type.getPrimitiveType()) {
					case STRING:
						return new Integer(0);
					case NUMBER:
						return new Integer(1);
					case DECIMAL:
						return new Integer(2);
					case BOOLEAN:
						return new Integer(3);
					case DATETIME:
						return new Integer(4);
					case ARRAY:
						return new Integer(5);
					case MAP:
						return new Integer(6);
					default:
						break;
					}
				else {
					return new Integer(sel);
				}
			}

			return null;
		}

		public void modify(Object element, String property, Object value) {
			TableItem ti = (TableItem) element;
			FieldRecord fr = (FieldRecord) ti.getData();

			if (property.equals("Secured")) {
				fr.secured = ((Boolean) value).booleanValue();
			} else if (property.equals("Name")) {
				if (value != null) {
					String oldName = fr.name;
					fr.name = (String) value;

					if (!fr.name.equals(oldName)) {
						fireModified();
					}
				}
			} else if (property.equals("Initial")) {
				fr.initialValue = (value == null) ? "" : (String) value;
				fireModified();
			} else if (property.equals("Precision")) {
				fr.type.setPrecision(((Integer) value).intValue());
				fireModified();
			} else if (property.equals("Type")) {
				int sel = ((Integer) value).intValue();

				if (!fr.type.getName().equals(currentTypes.get(sel))) {
					fr.initialValue = "";
				}

				String selectedType = currentTypes.get(sel);
				FieldType.Primitive prim = FieldType.Primitive.find(selectedType);
				if (prim != null) {
					if (prim.hasBaseType()) {
						BaseTypeDialog btd = new BaseTypeDialog(BusinessObjectEditor.this.getEditorSite().getShell());
						if (fr.type.getPrimitiveType().hasBaseType())
							btd.setCurrentType(fr.type.getBaseTypeName());
						List<String> types = new LinkedList<String>();
						types.add("ANYTYPE");
						types.add("String");
						types.add("Number");
						types.add("Decimal");
						types.add("Boolean");
						types.add("DateTime");
						List<IBusinessObject> bos = bo.getBusinessObjectSet().getBusinessObjects();
						for (IBusinessObject b : bos) {
							if (!b.equals(bo))
								types.add(b.getName());
						}
						btd.setTypes(types);
						int d = btd.open();
						if (d == Dialog.OK) {
							Primitive basePrim = Primitive.find(btd.getBaseType());
							if (basePrim != null)
								fr.type = new FieldType(prim, basePrim);
						}
					} else {
						fr.type = new FieldType(prim);
					}
				} else {
					List<IBusinessObject> bos = bo.getBusinessObjectSet().getBusinessObjects();
					for (IBusinessObject b : bos) {
						if (b.getName().equals(selectedType)) {
							fr.type = new FieldType(b);
							break;
						}
					}
				}
				fireModified();
			}

			viewer.refresh(true);
		}
	}

	public class FieldRecord {
		String name;
		FieldType type;
		String initialValue;
		boolean secured = false;
	}

	public class BaseTypeDialog extends Dialog {
		String baseType;
		Combo typeCombo = null;
		List<String> types = null;

		/**
		 * @param parentShell
		 */
		public BaseTypeDialog(Shell parentShell) {
			super(parentShell);
		}

		/**
		 * @param parentShell
		 */
		public BaseTypeDialog(IShellProvider parentShell) {
			super(parentShell);
		}

		public void setCurrentType(String type) {
			baseType = type;
		}

		public void setTypes(List<String> types) {
			this.types = types;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.
		 * Composite)
		 */
		protected Control createDialogArea(Composite parent) {
			parent.setLayout(new GridLayout(2, false));
			Label nameLabel = new Label(parent, SWT.NONE);
			nameLabel.setText("Base Type:");
			nameLabel.setLayoutData(new GridData());
			typeCombo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
			typeCombo.setItems(types.toArray(new String[types.size()]));
			if (baseType == null) {
				baseType = types.get(0);
			} else {
				for (int i = 0; i < types.size(); i++) {
					if (types.get(i).equals(baseType))
						typeCombo.select(i);
				}
			}
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			typeCombo.setLayoutData(gd);
			typeCombo.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (typeCombo.getSelectionIndex() > 0)
						baseType = types.get(typeCombo.getSelectionIndex());
					else
						baseType = types.get(0);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			return parent;
		}

		/**
		 * @return
		 */
		public String getBaseType() {
			return baseType;
		}
	}
}
