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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.vtp.desktop.core.dialogs.FramedDialog;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.ObjectField;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.framework.util.QuickSort;
import org.eclipse.vtp.framework.util.VariableNameValidator;
import org.eclipse.vtp.modules.interactive.ui.SubdialogInformationProvider;
import org.eclipse.vtp.modules.interactive.ui.SubdialogInformationProvider.SubdialogInput;
import org.eclipse.vtp.modules.interactive.ui.SubdialogInformationProvider.SubdialogOutput;
import org.eclipse.vtp.modules.interactive.ui.SubdialogInformationProvider.SubdialogParameter;

/**
 * The UI used to configure a Subdialog module.
 * 
 * @author Trip
 */
public class SubdialogPropertiesPanel extends DesignElementPropertiesPanel
{
	/** A list of all SubdialogParameter objects configured in this Subdialog module*/
	List<SubdialogParameter> urlParameters = new ArrayList<SubdialogParameter>();
	/** A list of all SubdialogInput objects configured in this Subdialog module*/
	List<SubdialogInput> inputs = new ArrayList<SubdialogInput>();
	/** A list of all SubdialogOutput objects configured in this Subdialog module*/
	List<SubdialogOutput> outputs = new ArrayList<SubdialogOutput>();
	/** A text field used to display/change the name of this particular Subdialog module*/
	Text nameField = null;
	/** A Label used to label the Text field for the name of the subdialog*/
	Label nameLabel = null;
	/** A text field used to display/change the url of subdialog called by this particular Subdialog module*/
	Text urlField = null;
	/** A UI table of all SubdialogInput objects configured in this Subdialog module*/
	TableViewer inputTable = null;
	/** A UI table of all SubdialogOutput objects configured in this Subdialog module*/
	TableViewer outputTable = null;
	/** A UI table of all SubdialogParameter objects configured in this Subdialog module*/
	TableViewer urlParamTable = null;
	private SubdialogInformationProvider info = null;

	/**
	 * Creates a new SubdialogPropertiesPanel
	 * 
	 * @param name
	 * @param subdialogElement 
	 */
	public SubdialogPropertiesPanel(String name, IDesignElement subdialogElement)
	{
		super(name, subdialogElement);
		PrimitiveElement pe = (PrimitiveElement)subdialogElement;
		info = (SubdialogInformationProvider)pe.getInformationProvider();
		inputs.addAll(info.getInputs());
		outputs.addAll(info.getOutputs());
		urlParameters.addAll(info.getURLParameters());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.ComponentPropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NONE);
		container.setBackground(parent.getBackground());
		container.setLayout(new GridLayout(2, false));
		nameLabel = new Label(container, SWT.NONE);
		nameLabel.setText("Name");
		nameLabel.setBackground(container.getBackground());
		nameLabel.setLayoutData(new GridData());
		nameField = new Text(container, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getElement().getName());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameField.addVerifyListener(new VerifyListener()
		{
			public void verifyText(VerifyEvent e)
			{
				String currentName = nameField.getText().substring(0, e.start) + e.text + nameField.getText(e.end, (nameField.getText().length() - 1));
				if(VariableNameValidator.followsVtpNamingRules(currentName))
				{
					nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
					nameField.setForeground(nameField.getDisplay().getSystemColor(SWT.COLOR_BLACK));
					getContainer().setCanFinish(true);
				}
				else
				{
					nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
					nameField.setForeground(nameField.getDisplay().getSystemColor(SWT.COLOR_RED));
					getContainer().setCanFinish(false);
				}
            }
		});
		if(VariableNameValidator.followsVtpNamingRules(nameField.getText()))
		{
			nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			nameField.setForeground(nameField.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			getContainer().setCanFinish(true);
		}
		else
		{
			nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
			nameField.setForeground(nameField.getDisplay().getSystemColor(SWT.COLOR_RED));
			getContainer().setCanFinish(false);
		}
		Label urlLabel = new Label(container, SWT.NONE);
		urlLabel.setText("URL");
		urlLabel.setBackground(container.getBackground());
		urlLabel.setLayoutData(new GridData());
		urlField = new Text(container, SWT.SINGLE | SWT.BORDER);
		urlField.setText(info.getURL());
		urlField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		
		Group paramsGroup = new Group(container, SWT.NONE);
		paramsGroup.setBackground(container.getBackground());
		paramsGroup.setText("URL Settings");
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		paramsGroup.setLayoutData(gd);
		paramsGroup.setLayout(new GridLayout(2, true));
		urlParamTable = new TableViewer(paramsGroup, SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		urlParamTable.getTable().setHeaderVisible(true);
		urlParamTable.getTable().setLinesVisible(true);
		TableColumn paramNameColumn = new TableColumn(urlParamTable.getTable(), SWT.NONE);
		paramNameColumn.setText("Name");
		paramNameColumn.setWidth(150);
		TableColumn paramValueColumn = new TableColumn(urlParamTable.getTable(), SWT.NONE);
		paramValueColumn.setText("Value");
		paramValueColumn.setWidth(150);
		urlParamTable.setContentProvider(new ParamTableContentProvider());
		urlParamTable.setLabelProvider(new ParamTableLabelProvider());
		urlParamTable.setInput(this);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.heightHint = 90;
		gd.widthHint = 300;
		urlParamTable.getTable().setLayoutData(gd);
		urlParamTable.addDoubleClickListener(new IDoubleClickListener(){

			public void doubleClick(DoubleClickEvent event)
			{
				if(!urlParamTable.getSelection().isEmpty())
				{
					ParameterValueDialog vd = new ParameterValueDialog(Display.getCurrent().getActiveShell());
					SubdialogParameter si = (SubdialogParameter)((IStructuredSelection)urlParamTable.getSelection()).getFirstElement();
					vd.setValue(si);
					if(vd.open() == SWT.OK)
					{
						urlParamTable.refresh();
					}
				}
			}
			
		});
		urlParamTable.getControl().addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.character == SWT.DEL && !urlParamTable.getSelection().isEmpty())
				{
					SubdialogParameter si = (SubdialogParameter)((IStructuredSelection)urlParamTable.getSelection()).getFirstElement();
					urlParameters.remove(si);
					urlParamTable.refresh();
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
			}
		});
		Button addButton = new Button(paramsGroup, SWT.PUSH);
		addButton.setText("Add Parameter");
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		addButton.setLayoutData(gd);
		addButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				ParameterValueDialog vd = new ParameterValueDialog(Display.getCurrent().getActiveShell());
				SubdialogParameter si = info.new SubdialogParameter("", 0, "");
				vd.setValue(si);
				if(vd.open() == SWT.OK)
				{
					urlParameters.add(si);
					urlParamTable.refresh();
				}
			}
			
		});
		Button deleteButton = new Button(paramsGroup, SWT.PUSH);
		deleteButton.setText("Delete Parameter");
		deleteButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				if(!urlParamTable.getSelection().isEmpty())
				{
					SubdialogParameter si = (SubdialogParameter)((IStructuredSelection)urlParamTable.getSelection()).getFirstElement();
					urlParameters.remove(si);
					urlParamTable.refresh();
				}
			}
			
		});
		gd = new GridData();
		deleteButton.setLayoutData(gd);
		
		
		Group inputsGroup = new Group(container, SWT.NONE);
		inputsGroup.setBackground(container.getBackground());
		inputsGroup.setText("Inputs");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		inputsGroup.setLayoutData(gd);
		inputsGroup.setLayout(new GridLayout(2, true));
		inputTable = new TableViewer(inputsGroup, SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		inputTable.getTable().setHeaderVisible(true);
		inputTable.getTable().setLinesVisible(true);
		TableColumn inputNameColumn = new TableColumn(inputTable.getTable(), SWT.NONE);
		inputNameColumn.setText("Name");
		inputNameColumn.setWidth(150);
		TableColumn inputValueColumn = new TableColumn(inputTable.getTable(), SWT.NONE);
		inputValueColumn.setText("Value");
		inputValueColumn.setWidth(150);
		inputTable.setContentProvider(new InputTableContentProvider());
		inputTable.setLabelProvider(new InputTableLabelProvider());
		inputTable.setInput(this);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.heightHint = 90;
		gd.widthHint = 300;
		inputTable.getTable().setLayoutData(gd);
		inputTable.addDoubleClickListener(new IDoubleClickListener(){

			public void doubleClick(DoubleClickEvent event)
			{
				if(!inputTable.getSelection().isEmpty())
				{
					InputValueDialog vd = new InputValueDialog(Display.getCurrent().getActiveShell());
					SubdialogInput si = (SubdialogInput)((IStructuredSelection)inputTable.getSelection()).getFirstElement();
					vd.setValue(si);
					if(vd.open() == SWT.OK)
					{
						inputTable.refresh();
					}
				}
			}
			
		});
		inputTable.getControl().addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.character == SWT.DEL && !inputTable.getSelection().isEmpty())
				{
					SubdialogInput si = (SubdialogInput)((IStructuredSelection)inputTable.getSelection()).getFirstElement();
					inputs.remove(si);
					inputTable.refresh();
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
			}
		});

		addButton = new Button(inputsGroup, SWT.PUSH);
		addButton.setText("Add Input");
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		addButton.setLayoutData(gd);
		addButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				InputValueDialog vd = new InputValueDialog(Display.getCurrent().getActiveShell());
				SubdialogInput si = info.new SubdialogInput("", 0, "");
				vd.setValue(si);
				if(vd.open() == SWT.OK)
				{
					inputs.add(si);
					inputTable.refresh();
				}
			}
			
		});
		deleteButton = new Button(inputsGroup, SWT.PUSH);
		deleteButton.setText("Delete Input");
		deleteButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				if(!inputTable.getSelection().isEmpty())
				{
					SubdialogInput si = (SubdialogInput)((IStructuredSelection)inputTable.getSelection()).getFirstElement();
					inputs.remove(si);
					inputTable.refresh();
				}
			}
			
		});
		gd = new GridData();
		deleteButton.setLayoutData(gd);
		
		
		Group outputsGroup = new Group(container, SWT.NONE);
		outputsGroup.setBackground(container.getBackground());
		outputsGroup.setText("Outputs");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		outputsGroup.setLayoutData(gd);
		outputsGroup.setLayout(new GridLayout(2, true));
		outputTable = new TableViewer(outputsGroup, SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		outputTable.getTable().setHeaderVisible(true);
		outputTable.getTable().setLinesVisible(true);
		TableColumn outputNameColumn = new TableColumn(outputTable.getTable(), SWT.NONE);
		outputNameColumn.setText("Name");
		outputNameColumn.setWidth(150);
		TableColumn outputValueColumn = new TableColumn(outputTable.getTable(), SWT.NONE);
		outputValueColumn.setText("Value");
		outputValueColumn.setWidth(150);
		outputTable.setContentProvider(new OutputTableContentProvider());
		outputTable.setLabelProvider(new OutputTableLabelProvider());
		outputTable.setInput(this);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.heightHint = 90;
		gd.widthHint = 300;
		outputTable.getTable().setLayoutData(gd);
		outputTable.addDoubleClickListener(new IDoubleClickListener(){

			public void doubleClick(DoubleClickEvent event)
			{
				if(!outputTable.getSelection().isEmpty())
				{
					OutputValueDialog vd = new OutputValueDialog(Display.getCurrent().getActiveShell());
					SubdialogOutput so = (SubdialogOutput)((IStructuredSelection)outputTable.getSelection()).getFirstElement();
					vd.setValue(so);
					if(vd.open() == SWT.OK)
					{
						outputTable.refresh();
					}
				}
			}
			
		});
		outputTable.getControl().addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.character == SWT.DEL && !outputTable.getSelection().isEmpty())
				{
					SubdialogOutput so = (SubdialogOutput)((IStructuredSelection)outputTable.getSelection()).getFirstElement();
					outputs.remove(so);
					outputTable.refresh();
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
			}
		});
		addButton = new Button(outputsGroup, SWT.PUSH);
		addButton.setText("Add Ouput");
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		addButton.setLayoutData(gd);
		addButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				OutputValueDialog vd = new OutputValueDialog(Display.getCurrent().getActiveShell());
				SubdialogOutput so = info.new SubdialogOutput("", "");
				vd.setValue(so);
				if(vd.open() == SWT.OK)
				{
					outputs.add(so);
					outputTable.refresh();
				}
			}
			
		});
		deleteButton = new Button(outputsGroup, SWT.PUSH);
		deleteButton.setText("Delete Output");
		gd = new GridData();
		deleteButton.setLayoutData(gd);
		deleteButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				if(!outputTable.getSelection().isEmpty())
				{
					SubdialogOutput so = (SubdialogOutput)((IStructuredSelection)outputTable.getSelection()).getFirstElement();
					outputs.remove(so);
					outputTable.refresh();
				}
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.ComponentPropertiesPanel#save()
	 */
	public void save()
	{
		getElement().setName(nameField.getText());
		info.setURL(urlField.getText());
		info.setInputs(inputs);
		info.setOutputs(outputs);
		info.setURLParameters(urlParameters);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ComponentPropertiesPanel#cancel()
	 */
	public void cancel()
	{
		
	}

	public class ParamTableContentProvider implements IStructuredContentProvider
	{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return urlParameters.toArray();
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
	
	public class ParamTableLabelProvider implements ITableLabelProvider
	{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			SubdialogParameter si = (SubdialogParameter)element;
			if(columnIndex == 0)
				return si.name;
			return si.value;
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

	public class InputTableContentProvider implements IStructuredContentProvider
	{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return inputs.toArray();
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
	
	public class InputTableLabelProvider implements ITableLabelProvider
	{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			SubdialogInput si = (SubdialogInput)element;
			if(columnIndex == 0)
				return si.name;
			return si.value;
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

	public class OutputTableContentProvider implements IStructuredContentProvider
	{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return outputs.toArray();
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
	
	public class OutputTableLabelProvider implements ITableLabelProvider
	{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			SubdialogOutput so = (SubdialogOutput)element;
			if(columnIndex == 0)
				return so.varName;
			return so.valueName;
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
	
	/**
	 * A dialog used to configure the properties of a SubdialogInput object.
	 */
	public class InputValueDialog extends FramedDialog
	{
		/** A text field used to display/modify the name of the variable */
		Text variableNameField;
		TreeViewer variableViewer;
		/** A text field used to display/modify a static value for the variable */
		Text staticValueField;
		/** The SubdialogInput object this dialog will modify*/
		SubdialogInformationProvider.SubdialogInput value;
		Color darkBlue;
		Color lightBlue;
		List<ObjectDefinition> vars;
		Composite staticValueComp;
		Composite variableTreeComp;
		Composite valueComp;
		StackLayout valueLayout;
		Combo valueType;
		Label nameLabel;
		/** The button used to dismiss the dialog and keep the changes */
		Button okButton;

		/**
		 * Creates a new InputValueDialog
		 * @param shellProvider
		 */
		@SuppressWarnings("unchecked")
		public InputValueDialog(Shell shell)
		{
			super(shell);
			this.setSideBarSize(40);
			this.setTitle("Select a value");
			
			List<Variable> unsortedVars = getElement().getDesign().getVariablesFor(getElement());
			if (unsortedVars == null)
			{
				this.vars = null;
			}
			else
			{
				Comparable<Comparer>[] comp = new Comparable[unsortedVars.size()];
				for (int b = 0; b < unsortedVars.size(); b++)
				{
					Object obj = unsortedVars.get(b);
					Comparer compr = new Comparer((ObjectDefinition)obj);
					comp[b] = compr;
				}
				comp = QuickSort.comparableSort(comp);
				this.vars = new ArrayList<ObjectDefinition>();
				for (int b = 0; b < comp.length; b++)
				{
					this.vars.add(((Comparer)comp[b]).od);
				}
			}
		}

		/**
		 * Specifies which SubdialogInput object to modify
		 * @param value - the SubdialogInput object to modify
		 */
		public void setValue(SubdialogInformationProvider.SubdialogInput value)
		{
			this.value = value;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
		 */
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

			okButton = new Button(buttons, SWT.PUSH);
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

		/**
		 * Saves any changes made to this object and exits with a return code of SWT.OK
		 */
		public void okPressed()
		{
			value.name = variableNameField.getText();
			if(valueType.getSelectionIndex() == 1)
			{
				value.type = 1;
				if(!variableViewer.getSelection().isEmpty())
				{
					value.value = ((ObjectDefinition)((IStructuredSelection)variableViewer.getSelection()).getFirstElement()).getPath();
				}
				else
				{
					value.value = "";
				}
			}
			else
			{
				value.type = 0;
				value.value = staticValueField.getText();
			}
			this.setReturnCode(SWT.OK);
			close();
		}

		/**
		 * Cancels any changes made to this object and exits with a return code of SWT.CANCEL
		 */
		public void cancelPressed()
		{
			this.setReturnCode(SWT.CANCEL);
			close();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createDialogContents(org.eclipse.swt.widgets.Composite)
		 */
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
			
			nameLabel = new Label(parent, SWT.NONE);
			nameLabel.setText("Input Name");
			nameLabel.setBackground(parent.getBackground());
			GridData gd = new GridData();
			nameLabel.setLayoutData(gd);
			
			variableNameField = new Text(parent, SWT.SINGLE | SWT.BORDER);
			variableNameField.setText(value.name);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			variableNameField.setLayoutData(gd);
			variableNameField.addVerifyListener(new VerifyListener()
			{
				public void verifyText(VerifyEvent e)
				{
					String currentName = variableNameField.getText().substring(0, e.start) + e.text + variableNameField.getText(e.end, (variableNameField.getText().length() - 1));
					if(VariableNameValidator.followsEcmaNamingRules(currentName))
					{
						nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						okButton.setEnabled(true);
//TODO check for name collisions
//						for(int b = 0; b < reservedNames.size(); b++)
//						{
//							if(currentName.equals(reservedNames.get(b))) //Is this name taken?
//							{
//								nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
//								variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_RED));
//								okButton.setEnabled(false);	                		
//							}
//						}
					}
					else
					{
						nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
						variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_RED));
						okButton.setEnabled(false);
					}
	            }
			});
			okButton.setEnabled(VariableNameValidator.followsEcmaNamingRules(variableNameField.getText()));

			valueType = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
			GridData valueTypeGridData = new GridData();
			valueTypeGridData.verticalAlignment = SWT.TOP;
			valueType.setLayoutData(valueTypeGridData);
			valueType.add("Value");
			valueType.add("Variable");
			valueType.select(value.type == 1 ? 1 : 0);
			valueType.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					valueTypeChanged();
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

			valueComp = new Composite(parent, SWT.None);
			valueComp.setBackground(parent.getBackground());
			valueComp.setLayout(valueLayout = new StackLayout());
			valueComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			staticValueComp = new Composite(valueComp, SWT.NONE);
			staticValueComp.setBackground(valueComp.getBackground());
			GridLayout layout = new GridLayout(1, false);
			layout.marginWidth = layout.marginHeight = 0;
			staticValueComp.setLayout(layout);
			
			variableTreeComp = new Composite(valueComp, SWT.NONE);
			variableTreeComp.setBackground(valueComp.getBackground());
			layout = new GridLayout(1, false);
			layout.marginWidth = layout.marginHeight = 0;
			variableTreeComp.setLayout(layout);
			
			Tree variableTree = new Tree(variableTreeComp, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
			gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalIndent = 10;
			gd.horizontalSpan = 2;
			variableTree.setLayoutData(gd);
			variableViewer = new TreeViewer(variableTree);
			variableViewer.setContentProvider(new VariableContentProvider());
			variableViewer.setLabelProvider(new VariableLabelProvider());
			variableViewer.setInput(this);
			
			staticValueField = new Text(staticValueComp, SWT.BORDER | SWT.SINGLE);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			if(value.type == 1)
			{
				for(int i = 0; i < vars.size(); i++)
				{
					Variable v = (Variable)vars.get(i);

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

			if(value.type == 1)
			{
				valueLayout.topControl = variableTreeComp;
			}
			else
			{
				staticValueField.setText((value.value == null) ? "" : value.value);
				valueLayout.topControl = staticValueComp;
			}
		}
		
		/**
		 * Sets which controls are visible based on the value type
		 */
		private void valueTypeChanged()
		{
			switch (valueType.getSelectionIndex())
			{
			case 1:
				valueLayout.topControl = variableTreeComp;
				break;
			default:
				valueLayout.topControl = staticValueComp;
			}
			valueComp.layout();
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

	/**
	 * A dialog used to configure the properties of a SubdialogOutput object.
	 */
	public class OutputValueDialog extends FramedDialog
	{
		/** A text field used to display/modify the name of the variable */
		Text variableNameField;
		/** A text field used to display/modify a static value for the variable */
		Text staticValueField;
		/** The SubdialogOutput object this dialog will modify*/
		SubdialogInformationProvider.SubdialogOutput value;
		Color darkBlue;
		Color lightBlue;
		Button okButton;
		Label nameLabel;
		Label valueLabel;

		/**
		 * Creates a new OutputValueDialog
		 * @param shellProvider
		 */
		public OutputValueDialog(Shell shell)
		{
			super(shell);
			this.setSideBarSize(40);
			this.setTitle("Select a value");
		}

		/**
		 * Specifies which SubdialogOutput object to modify
		 * @param value - the SubdialogOutput object to modify
		 */
		public void setValue(SubdialogInformationProvider.SubdialogOutput value)
		{
			this.value = value;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
		 */
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

			okButton = new Button(buttons, SWT.PUSH);
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

		/**
		 * Saves any changes made to this object and exits with a return code of SWT.OK
		 */
		public void okPressed()
		{
			value.varName = variableNameField.getText();
			value.valueName = staticValueField.getText();
			this.setReturnCode(SWT.OK);
			close();
		}

		/**
		 * Cancels any changes made to this object and exits with a return code of SWT.CANCEL
		 */
		public void cancelPressed()
		{
			this.setReturnCode(SWT.CANCEL);
			close();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createDialogContents(org.eclipse.swt.widgets.Composite)
		 */
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

			nameLabel = new Label(parent, SWT.NONE);
			nameLabel.setText("Target Variable Name");
			nameLabel.setBackground(parent.getBackground());
			GridData gd = new GridData();
			nameLabel.setLayoutData(gd);
			
			variableNameField = new Text(parent, SWT.SINGLE | SWT.BORDER);
			variableNameField.setText(value.varName);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			variableNameField.setLayoutData(gd);
			variableNameField.addVerifyListener(new VerifyListener()
			{
				public void verifyText(VerifyEvent e)
				{
					String currentName = variableNameField.getText().substring(0, e.start) + e.text + variableNameField.getText(e.end, (variableNameField.getText().length() - 1));
					if(VariableNameValidator.followsEcmaNamingRules(currentName))
					{
						nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						if(VariableNameValidator.followsEcmaNamingRules(staticValueField.getText()))
							okButton.setEnabled(true);
//TODO check for name collisions
//						for(int b = 0; b < reservedNames.size(); b++)
//						{
//							if(currentName.equals(reservedNames.get(b))) //Is this name taken?
//							{
//								nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
//								variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_RED));
//								okButton.setEnabled(false);	                		
//							}
//						}
					}
					else
					{
						nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
						variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_RED));
						okButton.setEnabled(false);
					}
	            }
			});


			valueLabel = new Label(parent, SWT.NONE);
			valueLabel.setText("Subdialog Return Value");
			valueLabel.setBackground(parent.getBackground());
			gd = new GridData();
			valueLabel.setLayoutData(gd);
			
			staticValueField = new Text(parent, SWT.SINGLE | SWT.BORDER);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			staticValueField.setLayoutData(gd);
			staticValueField.setText((value.valueName == null) ? "" : value.valueName);
			staticValueField.addVerifyListener(new VerifyListener()
			{
				public void verifyText(VerifyEvent e)
				{
					String currentName = staticValueField.getText().substring(0, e.start) + e.text + staticValueField.getText(e.end, (staticValueField.getText().length() - 1));
					if(VariableNameValidator.followsEcmaNamingRules(currentName))
					{
						valueLabel.setForeground(valueLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						staticValueField.setForeground(staticValueField.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						if(VariableNameValidator.followsEcmaNamingRules(variableNameField.getText()))
							okButton.setEnabled(true);
					}
					else
					{
						valueLabel.setForeground(valueLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
						staticValueField.setForeground(staticValueField.getDisplay().getSystemColor(SWT.COLOR_RED));
						okButton.setEnabled(false);
					}
	            }
			});
			okButton.setEnabled(VariableNameValidator.followsEcmaNamingRules(variableNameField.getText()) && VariableNameValidator.followsEcmaNamingRules(staticValueField.getText()));

		}

	}

	/**
	 * A dialog used to configure the properties of a SubdialogParameter object.
	 */
	public class ParameterValueDialog extends FramedDialog
	{
		/** A text field used to display/modify the name of the variable */
		Text variableNameField;
		TreeViewer variableViewer;
		/** A text field used to display/modify a static value for the variable */
		Text staticValueField;
		/** The SubdialogParameter object this dialog will modify*/
		SubdialogInformationProvider.SubdialogParameter value;
		Color darkBlue;
		Color lightBlue;
		List<ObjectDefinition> vars;
		Composite staticValueComp;
		Composite variableTreeComp;
		Composite valueComp;
		StackLayout valueLayout;
		Combo valueType;
		Label nameLabel;
		/** The button used to dismiss the dialog and keep the changes */
		Button okButton;

		/**
		 * Creates a new ParameterValueDialog
		 * @param shellProvider
		 */
		@SuppressWarnings("unchecked")
		public ParameterValueDialog(Shell shell)
		{
			super(shell);
			this.setSideBarSize(40);
			this.setTitle("Select a value");

			List<Variable> unsortedVars = getElement().getDesign().getVariablesFor(getElement());
			if (unsortedVars == null)
			{
				this.vars = null;
			}
			else
			{
				Comparable<Comparer>[] comp = new Comparable[unsortedVars.size()];
				for (int b = 0; b < unsortedVars.size(); b++)
				{
					Comparer compr = new Comparer(unsortedVars.get(b));
					comp[b] = compr;
				}
				comp = QuickSort.comparableSort(comp);
				this.vars = new ArrayList<ObjectDefinition>();
				for (int b = 0; b < comp.length; b++)
				{
					this.vars.add(((Comparer)comp[b]).od);
				}
			}
		}

		/**
		 * Specifies which SubdialogParameter object to modify
		 * @param value - the SubdialogParameter object to modify
		 */
		public void setValue(SubdialogInformationProvider.SubdialogParameter value)
		{
			this.value = value;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
		 */
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

			okButton = new Button(buttons, SWT.PUSH);
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

		/**
		 * Saves any changes made to this object and exits with a return code of SWT.OK
		 */
		public void okPressed()
		{
			value.name = variableNameField.getText();
			if(valueType.getSelectionIndex() == 1)
			{
				value.type = 1;
				if(!variableViewer.getSelection().isEmpty())
				{
					value.value = ((ObjectDefinition)((IStructuredSelection)variableViewer.getSelection()).getFirstElement()).getPath();
				}
				else
				{
					value.value = "";
				}
			}
			else
			{
				value.type = 0;
				value.value = staticValueField.getText();
			}
			this.setReturnCode(SWT.OK);
			close();
		}

		/**
		 * Cancels any changes made to this object and exits with a return code of SWT.CANCEL
		 */
		public void cancelPressed()
		{
			this.setReturnCode(SWT.CANCEL);
			close();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createDialogContents(org.eclipse.swt.widgets.Composite)
		 */
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

			nameLabel = new Label(parent, SWT.NONE);
			nameLabel.setText("Parameter Name");
			nameLabel.setBackground(parent.getBackground());
			GridData gd = new GridData();
			nameLabel.setLayoutData(gd);
			
			variableNameField = new Text(parent, SWT.SINGLE | SWT.BORDER);
			variableNameField.setText(value.name);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			variableNameField.setLayoutData(gd);
			variableNameField.addVerifyListener(new VerifyListener()
			{
				public void verifyText(VerifyEvent e)
				{
					String currentName = variableNameField.getText().substring(0, e.start) + e.text + variableNameField.getText(e.end, (variableNameField.getText().length() - 1));
					if(VariableNameValidator.followsEcmaNamingRules(currentName))
					{
						nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						okButton.setEnabled(true);
//TODO check for name collisions
//						for(int b = 0; b < reservedNames.size(); b++)
//						{
//							if(currentName.equals(reservedNames.get(b))) //Is this name taken?
//							{
//								nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
//								variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_RED));
//								okButton.setEnabled(false);	                		
//							}
//						}
					}
					else
					{
						nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
						variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_RED));
						okButton.setEnabled(false);
					}
	            }
			});
			okButton.setEnabled(VariableNameValidator.followsEcmaNamingRules(variableNameField.getText()));

			valueType = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
			GridData valueTypeGridData = new GridData();
			valueTypeGridData.verticalAlignment = SWT.TOP;
			valueType.setLayoutData(valueTypeGridData);
			valueType.add("Value");
			valueType.add("Variable");
			valueType.select(value.type == 1 ? 1 : 0);
			valueType.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					valueTypeChanged();
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

			valueComp = new Composite(parent, SWT.None);
			valueComp.setBackground(parent.getBackground());
			valueComp.setLayout(valueLayout = new StackLayout());
			valueComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			staticValueComp = new Composite(valueComp, SWT.NONE);
			staticValueComp.setBackground(valueComp.getBackground());
			GridLayout layout = new GridLayout(1, false);
			layout.marginWidth = layout.marginHeight = 0;
			staticValueComp.setLayout(layout);
			
			variableTreeComp = new Composite(valueComp, SWT.NONE);
			variableTreeComp.setBackground(valueComp.getBackground());
			layout = new GridLayout(1, false);
			layout.marginWidth = layout.marginHeight = 0;
			variableTreeComp.setLayout(layout);
			
			Tree variableTree = new Tree(variableTreeComp, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
			gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalIndent = 10;
			gd.horizontalSpan = 2;
			variableTree.setLayoutData(gd);
			variableViewer = new TreeViewer(variableTree);
			variableViewer.setContentProvider(new VariableContentProvider());
			variableViewer.setLabelProvider(new VariableLabelProvider());
			variableViewer.setInput(this);
			
			staticValueField = new Text(staticValueComp, SWT.BORDER | SWT.SINGLE);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;

			if(value.type == 1)
			{
				for(int i = 0; i < vars.size(); i++)
				{
					Variable v = (Variable)vars.get(i);

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

			if(value.type == 1)
			{
				valueLayout.topControl = variableTreeComp;
			}
			else
			{
				staticValueField.setText((value.value == null) ? "" : value.value);
				valueLayout.topControl = staticValueComp;
			}
		}
		
		/**
		 * Sets which controls are visible based on the value type
		 */
		private void valueTypeChanged()
		{
			switch (valueType.getSelectionIndex())
			{
			case 1:
				valueLayout.topControl = variableTreeComp;
				break;
			default:
				valueLayout.topControl = staticValueComp;
			}
			valueComp.layout();
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

	private class Comparer implements Comparable<Comparer>
	{
		ObjectDefinition od = null;
		
		/**
		 * @param od
		 */
		public Comparer(ObjectDefinition od){
			this.od = od;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Comparer arg0)
		{
			return od.getName().compareToIgnoreCase(arg0.od.getName());
		}
		
	}

	public void setConfigurationContext(Map<String, Object> values)
	{
	}
	
	public List<String> getApplicableContexts()
	{
		return Collections.emptyList();
	}
}
