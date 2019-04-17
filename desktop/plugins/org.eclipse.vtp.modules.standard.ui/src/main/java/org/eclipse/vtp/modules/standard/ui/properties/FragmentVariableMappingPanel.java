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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IWorkflowEntry;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.branding.BrandContext;
import org.eclipse.vtp.desktop.model.elements.core.configuration.FragmentConfigurationListener;
import org.eclipse.vtp.desktop.model.elements.core.configuration.FragmentConfigurationManager;
import org.eclipse.vtp.desktop.model.elements.core.configuration.InputBinding;
import org.eclipse.vtp.desktop.model.elements.core.configuration.InputBrandBinding;
import org.eclipse.vtp.desktop.model.elements.core.configuration.InputItem;
import org.eclipse.vtp.desktop.model.elements.core.internal.ApplicationFragmentElement;

public class FragmentVariableMappingPanel extends DesignElementPropertiesPanel implements FragmentConfigurationListener
{
	private TableViewer expectedVariableViewer = null;
	private Button inheritButton = null;
	private Button noChangeButton = null;
	private Button staticButton = null;
	private Text staticText = null;
	private Button expressionButton = null;
	private Text expressionText = null;
	private Button appVariableButton = null;
	private TableViewer appVariableViewer = null;
	private List<Variable> inputVariables = Collections.emptyList();
	
	private FragmentConfigurationManager manager = null;
	private IBrand currentBrand = null;
	List<Variable> applicationVariables = new ArrayList<Variable>();
	private boolean updating = false;
	private Composite mappingComposite = null;

	public FragmentVariableMappingPanel(String name, IDesignElement element)
	{
		super(name, element);
		manager = (FragmentConfigurationManager)element.getConfigurationManager(FragmentConfigurationManager.TYPE_ID);
		updateInputValues();
		manager.addListener(this);
		List<Variable> vars = element.getDesign().getVariablesFor(element);
outer:	for(Variable v : vars)
		{
			for(int i = 0; i < applicationVariables.size(); i++)
			{
				if(applicationVariables.get(i).getName().compareToIgnoreCase(v.getName()) > 0)
				{
					applicationVariables.add(i, v);
					continue outer;
				}
			}
			applicationVariables.add(v);
		}
	}

	public void createControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBackground(parent.getBackground());
		comp.setLayout(new GridLayout(2, true));

		Table expectedVariableTable = new Table(comp, SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		expectedVariableTable.setHeaderVisible(true);
		TableColumn nameColumn = new TableColumn(expectedVariableTable, SWT.NONE);
		nameColumn.setText("Variable Name");
		nameColumn.setWidth(150);
		TableColumn typeColumn = new TableColumn(expectedVariableTable, SWT.NONE);
		typeColumn.setText("Variable Type");
		typeColumn.setWidth(150);
		expectedVariableViewer = new TableViewer(expectedVariableTable);
		expectedVariableViewer.setContentProvider(new ExpectedVariableContentProvider());
		expectedVariableViewer.setLabelProvider(new ExpectedVariableLabelProvider());
		expectedVariableViewer.setInput(this);
		GridData gd = new GridData(GridData.FILL_BOTH);
		expectedVariableTable.setLayoutData(gd);
		expectedVariableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
            {
				if(currentBrand == null)
					return;
				try
                {
	                StructuredSelection selection = (StructuredSelection)event.getSelection();
	                if(!selection.isEmpty())
	                {
	                	updating = true;
	                	InputBinding vd = (InputBinding)selection.getFirstElement();
	                	InputBrandBinding brandBinding = vd.getBrandBinding(currentBrand);
	                	InputItem inputItem = brandBinding.getValue();
	                	inheritButton.setSelection(false);
	                	noChangeButton.setSelection(false);
	                	staticButton.setSelection(false);
	                	expressionButton.setSelection(false);
	                	appVariableButton.setSelection(false);
	                	if(brandBinding.hasParent() && brandBinding.isInherited())
	                	{
	                		inheritButton.setSelection(true);
    	                	staticText.setText("");
    	                	staticText.setEnabled(false);
    	                	expressionText.setText("");
    	                	expressionText.setEnabled(false);
    	                	appVariableViewer.setSelection(null);
    	                	appVariableViewer.getControl().setEnabled(false);
	                	}
	                	else
	                	{
	                		String type = inputItem.getType();
	                		if(type.equals(InputItem.NONE))
	                		{
	                			noChangeButton.setSelection(true);
	    	                	staticText.setText("");
	    	                	staticText.setEnabled(false);
	    	                	expressionText.setText("");
	    	                	expressionText.setEnabled(false);
	    	                	appVariableViewer.setSelection(null);
	    	                	appVariableViewer.getControl().setEnabled(false);
	                		}
	                		else if(type.equals(InputItem.STATIC))
	                		{
	                			staticButton.setSelection(true);
	                			staticText.setText(inputItem.getValue());
	                			staticText.setEnabled(true);
	                			expressionText.setText("");
	                			expressionText.setEnabled(false);
	                			appVariableViewer.setSelection(null);
	                			appVariableViewer.getControl().setEnabled(false);
	                		}
	                		else if(type.equals(InputItem.EXPRESSION))
	                		{
	                			expressionButton.setSelection(true);
	                			staticText.setText("");
	                			staticText.setEnabled(false);
	                			expressionText.setText(inputItem.getValue());
	                			expressionText.setEnabled(true);
	    	                	appVariableViewer.setSelection(null);
	    	                	appVariableViewer.getControl().setEnabled(false);
	                		}
	                		else if(type.equals(InputItem.VARIABLE))
	                		{
	                			staticText.setText("");
	                			staticText.setEnabled(false);
	                			expressionText.setText("");
	                			expressionText.setEnabled(false);
	                			appVariableButton.setSelection(true);
	                			for(Iterator<Variable> i = applicationVariables.iterator(); i
	                                    .hasNext();)
	                            {
	                                Variable v = i.next();
	                                if(v.getName().equals(inputItem.getValue()))
	                                {
	                                	appVariableViewer.setSelection(new StructuredSelection(v));
	                                }
	                            }
	                			appVariableViewer.getControl().setEnabled(true);
	                		}
		                	else
		                		throw new RuntimeException("Unknown mapping type found.");
	                	}
	                	updating = false;
	                }
                }
                catch(RuntimeException e)
                {
	                e.printStackTrace();
                }
            }
		});

		mappingComposite = new Composite(comp, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		mappingComposite.setLayoutData(gd);
		mappingComposite.setBackground(comp.getBackground());
		mappingComposite.setLayout(new GridLayout(1, false));
		
		inheritButton = new Button(mappingComposite, SWT.RADIO);
		inheritButton.setBackground(mappingComposite.getBackground());
		inheritButton.setText("Inherit from parent Brand");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.exclude = true;
		inheritButton.setLayoutData(gd);
		inheritButton.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
            {
            }

			public void widgetSelected(SelectionEvent e)
            {
				if(updating)
					return;
				updating = true;
				if(inheritButton.getSelection())
				{
					StructuredSelection selection = (StructuredSelection)expectedVariableViewer.getSelection();
					if(!selection.isEmpty())
					{
						staticText.setText("");
						staticText.setEnabled(false);
						expressionText.setText("");
						expressionText.setEnabled(false);
						appVariableViewer.setSelection(null);
						appVariableViewer.getControl().setEnabled(false);
	                	InputBinding vd = (InputBinding)selection.getFirstElement();
	                	InputBrandBinding brandBinding = vd.getBrandBinding(currentBrand);
	                	brandBinding.setValue(null);
					}
				}
				updating = false;
            }
		});
		
		noChangeButton = new Button(mappingComposite, SWT.RADIO);
		noChangeButton.setBackground(mappingComposite.getBackground());
		noChangeButton.setText("Do not change this variable");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		noChangeButton.setLayoutData(gd);
		noChangeButton.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
            {
            }

			public void widgetSelected(SelectionEvent e)
            {
				if(updating)
					return;
				updating = true;
				if(noChangeButton.getSelection())
				{
					StructuredSelection selection = (StructuredSelection)expectedVariableViewer.getSelection();
					if(!selection.isEmpty())
					{
						staticText.setText("");
						staticText.setEnabled(false);
						expressionText.setText("");
						expressionText.setEnabled(false);
						appVariableViewer.setSelection(null);
						appVariableViewer.getControl().setEnabled(false);
	                	InputBinding vd = (InputBinding)selection.getFirstElement();
	                	InputBrandBinding brandBinding = vd.getBrandBinding(currentBrand);
	                	InputItem inputItem = brandBinding.getValue();
	                	if(brandBinding.isInherited())
	                	{
	                		inputItem = new InputItem();
	                	}
	                	inputItem.setType(InputItem.NONE);
	                	inputItem.setValue("");
	                	brandBinding.setValue(inputItem);
					}
				}
				updating = false;
            }
			
		});
		
		staticButton = new Button(mappingComposite, SWT.RADIO);
		staticButton.setBackground(mappingComposite.getBackground());
		staticButton.setText("Use this value");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		staticButton.setLayoutData(gd);
		staticButton.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
            {
            }

			public void widgetSelected(SelectionEvent e)
            {
				if(updating)
					return;
				updating = true;
				if(staticButton.getSelection())
				{
					StructuredSelection selection = (StructuredSelection)expectedVariableViewer.getSelection();
					if(!selection.isEmpty())
					{
						staticText.setText("");
						staticText.setEnabled(true);
						expressionText.setText("");
						expressionText.setEnabled(false);
						appVariableViewer.setSelection(null);
						appVariableViewer.getControl().setEnabled(false);
	                	InputBinding vd = (InputBinding)selection.getFirstElement();
	                	InputBrandBinding brandBinding = vd.getBrandBinding(currentBrand);
	                	InputItem inputItem = brandBinding.getValue();
	                	if(brandBinding.isInherited())
	                	{
	                		inputItem = new InputItem();
	                	}
	                	inputItem.setType(InputItem.STATIC);
	                	inputItem.setValue("");
	                	brandBinding.setValue(inputItem);
					}
				}
				updating = false;
            }
			
		});
		
		staticText = new Text(mappingComposite, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = 15;
		staticText.setLayoutData(gd);
		staticText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
            {
				if(updating)
					return;
				StructuredSelection selection = (StructuredSelection)expectedVariableViewer.getSelection();
				if(!selection.isEmpty())
				{
                	InputBinding vd = (InputBinding)selection.getFirstElement();
                	InputBrandBinding brandBinding = vd.getBrandBinding(currentBrand);
                	InputItem inputItem = brandBinding.getValue();
                	if(brandBinding.isInherited())
                	{
                		inputItem = new InputItem();
                	}
//					if(inputItem.getType().equals(InputItem.STATIC))
//					{
						inputItem.setValue(staticText.getText());
						brandBinding.setValue(inputItem);
//					}
				}
            }
			
		});
		
		expressionButton = new Button(mappingComposite, SWT.RADIO);
		expressionButton.setBackground(mappingComposite.getBackground());
		expressionButton.setText("Use this Javascript expression");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		expressionButton.setLayoutData(gd);
		expressionButton.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
            {
            }

			public void widgetSelected(SelectionEvent e)
            {
				if(updating)
					return;
				updating = true;
				if(expressionButton.getSelection())
				{
					StructuredSelection selection = (StructuredSelection)expectedVariableViewer.getSelection();
					if(!selection.isEmpty())
					{
						staticText.setText("");
						staticText.setEnabled(false);
						expressionText.setText("");
						expressionText.setEnabled(true);
						appVariableViewer.setSelection(null);
						appVariableViewer.getControl().setEnabled(false);
	                	InputBinding vd = (InputBinding)selection.getFirstElement();
	                	InputBrandBinding brandBinding = vd.getBrandBinding(currentBrand);
	                	InputItem inputItem = brandBinding.getValue();
	                	if(brandBinding.isInherited())
	                	{
	                		inputItem = new InputItem();
	                	}
	                	inputItem.setType(InputItem.EXPRESSION);
	                	inputItem.setValue("");
	                	brandBinding.setValue(inputItem);
					}
				}
				updating = false;
            }
			
		});
		
		expressionText = new Text(mappingComposite, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = 15;
		expressionText.setLayoutData(gd);
		expressionText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
            {
				if(updating)
					return;
				StructuredSelection selection = (StructuredSelection)expectedVariableViewer.getSelection();
				if(!selection.isEmpty())
				{
                	InputBinding vd = (InputBinding)selection.getFirstElement();
                	InputBrandBinding brandBinding = vd.getBrandBinding(currentBrand);
                	InputItem inputItem = brandBinding.getValue();
                	if(brandBinding.isInherited())
                	{
                		inputItem = new InputItem();
                	}
//					if(inputItem.getType().equals(InputItem.EXPRESSION))
//					{
						inputItem.setValue(expressionText.getText());
						brandBinding.setValue(inputItem);
//					}
				}
            }
			
		});
		
		appVariableButton = new Button(mappingComposite, SWT.RADIO);
		appVariableButton.setBackground(mappingComposite.getBackground());
		appVariableButton.setText("Use this application variable");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		appVariableButton.setLayoutData(gd);
		appVariableButton.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
            {
            }

			public void widgetSelected(SelectionEvent e)
            {
				if(updating)
					return;
				updating = true;
				if(appVariableButton.getSelection())
				{
					StructuredSelection selection = (StructuredSelection)expectedVariableViewer.getSelection();
					if(!selection.isEmpty())
					{
						staticText.setText("");
						staticText.setEnabled(false);
						expressionText.setText("");
						expressionText.setEnabled(false);
						appVariableViewer.setSelection(null);
						appVariableViewer.getControl().setEnabled(true);
	                	InputBinding vd = (InputBinding)selection.getFirstElement();
	                	InputBrandBinding brandBinding = vd.getBrandBinding(currentBrand);
	                	InputItem inputItem = brandBinding.getValue();
	                	if(brandBinding.isInherited())
	                	{
	                		inputItem = new InputItem();
	                	}
	                	inputItem.setType(InputItem.VARIABLE);
	                	inputItem.setValue("");
						brandBinding.setValue(inputItem);
					}
				}
				updating = false;
            }
			
		});
		
		Table appVariableTable = new Table(mappingComposite, SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		appVariableTable.setHeaderVisible(true);
		TableColumn appVariableNameColumn = new TableColumn(appVariableTable, SWT.NONE);
		appVariableNameColumn.setText("Variable Name");
		appVariableNameColumn.setWidth(150);
		TableColumn appVariableTypeColumn = new TableColumn(appVariableTable, SWT.NONE);
		appVariableTypeColumn.setText("Variable Type");
		appVariableTypeColumn.setWidth(150);
		appVariableViewer = new TableViewer(appVariableTable);
		appVariableViewer.setContentProvider(new ApplicationVariableContentProvider());
		appVariableViewer.setLabelProvider(new ApplicationVariableLabelProvider());
		appVariableViewer.setInput(this);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalIndent = 15;
		appVariableViewer.getControl().setLayoutData(gd);
		appVariableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
            {
				if(updating)
					return;
				StructuredSelection selection = (StructuredSelection)expectedVariableViewer.getSelection();
				if(!selection.isEmpty())
				{
                	InputBinding vd = (InputBinding)selection.getFirstElement();
                	InputBrandBinding brandBinding = vd.getBrandBinding(currentBrand);
                	InputItem inputItem = brandBinding.getValue();
                	if(brandBinding.isInherited())
                	{
                		inputItem = new InputItem();
                	}
//					if(mapping.getMappingType() == ApplicationFragmentVariableMapping.MAPPING_VARIABLE)
//					{
						StructuredSelection appSel = (StructuredSelection)event.getSelection();
						if(appSel.isEmpty())
							inputItem.setValue("");
						else
							inputItem.setValue(((Variable)appSel.getFirstElement()).getName());
						brandBinding.setValue(inputItem);
//					}
				}
            }
			
		});
		
		this.setControl(comp);
		
	}

	public void save()
	{
		getElement().commitConfigurationChanges(manager);
	}
	
	public void cancel()
	{
		getElement().rollbackConfigurationChanges(manager);
	}

	public void setConfigurationContext(Map<String, Object> values)
	{
		Object obj = values.get(BrandContext.CONTEXT_ID);
		if(obj != null)
		{
			currentBrand = (IBrand)obj;
			((GridData)inheritButton.getLayoutData()).exclude = currentBrand.getParent() == null;
			inheritButton.setVisible(currentBrand.getParent() != null);
			mappingComposite.layout(true, true);
			//initialize selection and value states
			expectedVariableViewer.setSelection(manager.getInputBindings().size() < 1 ? StructuredSelection.EMPTY : new StructuredSelection(manager.getInputBindings().get(0)));
		}
	}

	public void updateInputValues()
	{
		inputVariables = Collections.emptyList();
		ApplicationFragmentElement applicationFragmentElement = (ApplicationFragmentElement)getElement();
		if(applicationFragmentElement.isModelPresent())
		{
			String entryId = manager.getEntryId();
			if(entryId != null && !entryId.equals(""))
			{
				IWorkflowEntry entry = applicationFragmentElement.getReferencedModel().getWorkflowEntry(entryId);
				if(entry != null)
				{
					inputVariables = entry.getInputVariables();
					System.err.println("inputVariables: " + inputVariables);
				}
			}
		}
		List<InputBinding> oldBindings = new LinkedList<InputBinding>(manager.getInputBindings());
		for(Variable v : inputVariables)
		{
			if(v.getType().isObject() || (v.getType().hasBaseType() && v.getType().isObjectBaseType()))
				continue;
			manager.addInputBinding(v.getName());
		}
outer:	for(InputBinding binding : oldBindings)
		{
			for(Variable v : inputVariables)
			{
				if(v.getName().equals(binding.getInput()))
					continue outer;
			}
			manager.removeInputBinding(binding.getInput());
		}
		System.err.println(manager.getInputBindings());
	}

	public void entryChanged(FragmentConfigurationManager manager)
	{
		updateInputValues();
		if(expectedVariableViewer != null)
		{
			expectedVariableViewer.refresh();
			if(expectedVariableViewer.getSelection().isEmpty())
				expectedVariableViewer.setSelection(manager.getInputBindings().size() < 1 ? StructuredSelection.EMPTY : new StructuredSelection(manager.getInputBindings().get(0)));
		}
	}

	private class ExpectedVariableContentProvider implements IStructuredContentProvider
	{

		public Object[] getElements(Object inputElement)
        {
	        return manager.getInputBindings().toArray();
        }

		public void dispose()
        {
        }

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }
		
	}
	
	private class ExpectedVariableLabelProvider extends LabelProvider implements ITableLabelProvider
	{

		public Image getColumnImage(Object element, int columnIndex)
        {
	        return null;
        }

		public String getColumnText(Object element, int columnIndex)
        {
			InputBinding vd = (InputBinding)element;
			if(columnIndex == 0) //variable name
			{
				return vd.getInput();
			}
			else if(columnIndex == 1) //variable type
			{
				for(Variable v : inputVariables)
				{
					if(v.getName().equals(vd.getInput()))
					{
						StringBuffer buf = new StringBuffer();
						buf.append(v.getType().getName());
						if(v.getType().hasBaseType()) //is array or map
						{
							buf.append(" of ");
							buf.append(v.getType().getBaseTypeName());
						}
						return buf.toString();
					}
				}
				return "";
			}
	        return "Unknown Column";
        }
		
	}

	private class ApplicationVariableContentProvider implements IStructuredContentProvider
	{

		public Object[] getElements(Object inputElement)
        {
	        return applicationVariables.toArray();
        }

		public void dispose()
        {
        }

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }
		
	}
	
	private class ApplicationVariableLabelProvider extends LabelProvider implements ITableLabelProvider
	{

		public Image getColumnImage(Object element, int columnIndex)
        {
	        return null;
        }

		public String getColumnText(Object element, int columnIndex)
        {
			Variable v = (Variable)element;
			if(columnIndex == 0) //variable name
			{
				return v.getName();
			}
			else if(columnIndex == 1) //variable type
			{
				StringBuffer buf = new StringBuffer();
				buf.append(v.getType().getName());
				if(v.getType().hasBaseType()) //is array or map
				{
					buf.append(" of ");
					buf.append(v.getType().getBaseTypeName());
				}
				return buf.toString();
			}
	        return "Unknown Column";
        }
		
	}

}
