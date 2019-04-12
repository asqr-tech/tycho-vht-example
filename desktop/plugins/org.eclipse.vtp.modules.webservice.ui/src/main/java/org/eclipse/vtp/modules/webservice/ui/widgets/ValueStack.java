/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. 
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.modules.webservice.ui.widgets;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.core.custom.ToggleButton;
import org.eclipse.vtp.desktop.editors.core.widgets.ValueControl;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.modules.webservice.ui.configuration.BindingValue;
import org.eclipse.vtp.modules.webservice.ui.configuration.BrandBinding;

/**
 * This class provides a UI widget that supports editing a configuration item.
 * The value can either be a static value, a variable name selected from a
 * drop-down, a javascript expression, or be inherited from a parent
 * configuration item.
 * 
 * The developer using this class supplies a UI control that will be used to
 * edit the item when a static value is being configured.  An editor interface
 * is used to get and set the value of this static UI control.
 * 
 * @author trip
 */
public class ValueStack implements ToggleButton.ToggleButtonListener
{
	/* UI Member Variables */
	private StackLayout stackLayout = null;
	private Composite mainComp = null;
	private Composite stackComp = null;
	private Composite staticComp = null;
	private Label staticValue = null;
	private Composite valueComp = null;
	private ValueControl valueControl = null;
	private Composite variableComp = null;
	private Text variableText = null;
	private Button variableBrowseButton = null;
	private Composite expressionComp = null;
	private Text expressionText = null;
	private Composite buttonComp = null;
	private ToggleButton defaultButton = null;
	private ToggleButton staticButton = null;
	private ToggleButton variableButton = null;
	private ToggleButton expressionButton = null;
	
	/** The name of the configuration item being edited */
	private String settingName = null;
	/** The default of the configuration item */
	private String ultimateDefault = "";
	/** The configuration item being edited */
	private BrandBinding setting = null;
	/** The set of listeners for this widget */
	private List<ValueStackListener> listeners = new LinkedList<ValueStackListener>();
	/** The set of variables available in the application */
	private List<Variable> variables = new ArrayList<Variable>();
	private ObjectDefinition variableSelection = null;
	private boolean updating = false;
	
	/**
	 * Creates a new value stack widget for the configuration item with given
	 * name, the supplied default value, and the list of available variables.
	 * 
	 * @param settingName The name of the configuration item being edited
	 * @param ultimateDefault The default value of the configuration item
	 * @param variables The variables available in the application
	 */
	public ValueStack(String settingName, String ultimateDefault, List<Variable> variables)
	{
		this.settingName = settingName;
		this.ultimateDefault = ultimateDefault;
		this.variables = variables;
	}
	
	public String getSettingName()
	{
		return settingName;
	}
	
	public void setVariables(List<Variable> variables)
	{
		this.variables = variables;
		if(variableButton != null && variableButton.isSelected() && variableSelection != null)
		{
			System.out.println("updating from variable: " + variableSelection);
			variableText.setText(variableSelection.getPath());
		}
	}
	
	/**
	 * Create the SWT controls that form this widget's UI.
	 * 
	 * @param parent The parent composite for this widget's UI
	 */
	public void createControls(Composite parent)
	{
		mainComp = new Composite(parent, SWT.NONE);
		mainComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		mainComp.setLayout(gridLayout);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.RIGHT;
		mainComp.setLayoutData(gd);
		stackComp = new Composite(mainComp, SWT.NONE);
		stackComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		stackComp.setLayout(stackLayout = new StackLayout());
		stackComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		staticComp = new Composite(stackComp, SWT.NONE);
		staticComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		gridLayout = new GridLayout(1, true);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 4;
		staticComp.setLayout(gridLayout);
		staticValue = new Label(staticComp, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		gd.verticalAlignment = SWT.CENTER;
		gd.horizontalIndent = 4;
		staticValue.setLayoutData(gd);
		staticValue.setAlignment(SWT.RIGHT);
		staticValue.setText("null");

		valueComp = new Composite(stackComp, SWT.NONE);
		valueComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		gridLayout = new GridLayout(1, true);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 4;
		valueComp.setLayout(gridLayout);
		
		variableComp = new Composite(stackComp, SWT.NONE);
		variableComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 4;
		variableComp.setLayout(gridLayout);
		
		variableText = new Text(variableComp, SWT.BORDER | SWT.SINGLE);
		variableText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				if(!updating) //avoid cascading updates
				{
					variableSelection = null;
					String[] parts = variableText.getText().split("\\.");
					List<ObjectDefinition> defs = new LinkedList<ObjectDefinition>(variables);
outer:					for(int i = 0; i < parts.length; i++)
					{
						for(ObjectDefinition od : defs)
						{
							if(od.getName().equals(parts[i]))
							{
								if(i == parts.length - 1)
								{
									variableText.setBackground(null);
									variableSelection = od;
									break outer;
								}
							}
						}
					}
					if(variableSelection == null)
					{
						variableText.setBackground(variableText.getDisplay().getSystemColor(SWT.COLOR_RED));
					}
				}
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL);
		variableText.setLayoutData(gd);
		variableBrowseButton = new Button(variableComp, SWT.PUSH);
		variableBrowseButton.setText("Pick");
		variableBrowseButton.setLayoutData(new GridData());
		variableBrowseButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				VariableBrowserDialog browserDialog = new VariableBrowserDialog(variableBrowseButton.getDisplay().getActiveShell(), variables);
				if(browserDialog.open() == Dialog.OK)
				{
					updating = true;
					variableSelection = browserDialog.getSelectedDefinition();
					if(variableSelection != null)
					{
						variableText.setText(variableSelection.getName());
						variableText.setBackground(null);
					}
					else
					{
						variableText.setText("");
						variableText.setBackground(variableText.getDisplay().getSystemColor(SWT.COLOR_RED));
					}
					updating = false;
				}
			}
		});

		expressionComp = new Composite(stackComp, SWT.NONE);
		expressionComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		gridLayout = new GridLayout(1, true);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 4;
		expressionComp.setLayout(gridLayout);
		expressionText = new Text(expressionComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.verticalAlignment = SWT.CENTER;
		gd.horizontalIndent = 4;
		expressionText.setLayoutData(gd);

		stackLayout.topControl = staticComp;
		buttonComp = new Composite(mainComp, SWT.NONE);
		buttonComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		gridLayout = new GridLayout(4, true);
		gridLayout.horizontalSpacing = 3;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		buttonComp.setLayout(gridLayout);
		buttonComp.setLayoutData(new GridData());

		GridData gridData = null;
		staticButton = new ToggleButton(buttonComp);
		staticButton.setBackgroundMode(SWT.INHERIT_DEFAULT);
		staticButton.setText("S");
		gridData = new GridData();
		gridData.widthHint = 16;
		gridData.heightHint = 16;
		staticButton.setLayoutData(gridData);
		staticButton.setToggleDownOnly(true);
		staticButton.addSelectionListener(this);

		variableButton = new ToggleButton(buttonComp);
		variableButton.setBackgroundMode(SWT.INHERIT_DEFAULT);
		variableButton.setText("V");
		gridData = new GridData();
		gridData.widthHint = 16;
		gridData.heightHint = 16;
		variableButton.setLayoutData(gridData);
		variableButton.setSelected(true);
		variableButton.setToggleDownOnly(true);
		variableButton.addSelectionListener(this);
		
		expressionButton = new ToggleButton(buttonComp);
		expressionButton.setBackgroundMode(SWT.INHERIT_DEFAULT);
		expressionButton.setText("E");
		gridData = new GridData();
		gridData.widthHint = 16;
		gridData.heightHint = 16;
		expressionButton.setLayoutData(gridData);
		expressionButton.setSelected(true);
		expressionButton.setToggleDownOnly(true);
		expressionButton.addSelectionListener(this);
		
		defaultButton = new ToggleButton(buttonComp);
		defaultButton.setBackgroundMode(SWT.INHERIT_DEFAULT);
		defaultButton.setText("I");
		gridData = new GridData();
		gridData.widthHint = 16;
		gridData.heightHint = 16;
		defaultButton.setLayoutData(gridData);
		defaultButton.setSelected(true);
		defaultButton.setToggleDownOnly(true);
		defaultButton.addSelectionListener(this);

		mainComp.layout(true, true);
		stackComp.layout(true, true);
	}
	
	/**
	 * @return The parent composite for the static value editor control
	 */
	public Composite getValueComposite()
	{
		return valueComp;
	}
	
	public Composite getMainComposite()
	{
		return mainComp;
	}
	
	/**
	 * Provides an implementation for the editor interface that mediates reading
	 * and writing the static value.
	 * 
	 * @param valueControl The editor interface implementation
	 */
	public void setValueControl(ValueControl valueControl)
	{
		this.valueControl = valueControl;
	}
	
	/**
	 * Provides the configuration item to edit.  If an item is already being
	 * edited, the current state of the UI is stored in the previous item prior
	 * to being replaced by the new item.  The UI is updated to reflect the new
	 * item's values.
	 * 
	 * @param brandBinding The configuration item to edit
	 */
	public void setSetting(BrandBinding brandBinding)
	{
		if(setting != null)
			save();
		this.setting = brandBinding;
		if(!setting.hasParent())
		{
			GridLayout layout = (GridLayout)buttonComp.getLayout();
			layout.numColumns = 3;
			((GridData)defaultButton.getLayoutData()).exclude = true;
			defaultButton.setVisible(false);
//			mainComp.layout(true, true);
		}
		else
		{
			GridLayout layout = (GridLayout)buttonComp.getLayout();
			layout.numColumns = 4;
			((GridData)defaultButton.getLayoutData()).exclude = false;
			defaultButton.setVisible(true);
//			mainComp.layout(true, true);
		}
		BindingValue pbi = setting.getBindingItem();
		if(pbi == null && setting.isInherited()) //use default settings
		{
			if(setting.hasParent())
			{
				String value = ultimateDefault;
				staticValue.setText(value);
				stackLayout.topControl = staticComp;
				defaultButton.setSelected(true);
				staticButton.setSelected(false);
				variableButton.setSelected(false);
				expressionButton.setSelected(false);
				valueControl.setValue(value);
			}
			else
			{
				String value = ultimateDefault;
				valueControl.setValue(value);
				stackLayout.topControl = valueComp;
				defaultButton.setSelected(false);
				staticButton.setSelected(true);
				variableButton.setSelected(false);
				expressionButton.setSelected(false);
			}
		}
		else if(setting.isInherited()) //use pbi as static inherited value
		{
			String value = pbi.getValue();
			if(value == null)
				value = "";
			staticValue.setText(value);
			stackLayout.topControl = staticComp;
			defaultButton.setSelected(true);
			staticButton.setSelected(false);
			variableButton.setSelected(false);
			expressionButton.setSelected(false);
			valueControl.setValue(value);
		}
		else
		{
			String value = pbi.getValue();
			if(value == null)
				value = "";
			staticValue.setText("");
			defaultButton.setSelected(false);
			if(pbi.getValueType().equals(BindingValue.STATIC))
			{
				stackLayout.topControl = valueComp;
				staticButton.setSelected(true);
				variableButton.setSelected(false);
				expressionButton.setSelected(false);
				valueControl.setValue(value);
			}
			else if(pbi.getValueType().equals(BindingValue.VARIABLE))
			{
				stackLayout.topControl = variableComp;
				staticButton.setSelected(false);
				variableButton.setSelected(true);
				expressionButton.setSelected(false);
				variableText.setText(value);
			}
			else
			{
				stackLayout.topControl = expressionComp;
				staticButton.setSelected(false);
				variableButton.setSelected(false);
				expressionButton.setSelected(true);
				expressionText.setText(value);
			}
		}
		mainComp.layout(true, true);
		stackComp.layout(true, true);
	}
	
	/**
	 * Stores the current UI's state into the configuration item.
	 */
	public void save()
	{
		if(defaultButton.isSelected())
		{
			setting.setBindingItem(null);
		}
		else if(variableButton != null && variableButton.isSelected())
		{
			BindingValue pbi = setting.getBindingItem();
			if(pbi == null || setting.isInherited())
			{
				pbi = new BindingValue();
			}
			pbi.setVariable(variableText.getText());
			setting.setBindingItem(pbi);
		}
		else if(expressionButton != null && expressionButton.isSelected())
		{
			BindingValue pbi = setting.getBindingItem();
			if(pbi == null || setting.isInherited())
			{
				pbi = new BindingValue();
			}
			pbi.setExpression(expressionText.getText());
			setting.setBindingItem(pbi);
		}
		else
		{
			BindingValue pbi = setting.getBindingItem();
			if(pbi == null || setting.isInherited())
			{
				pbi = new BindingValue();
			}
			pbi.setStaticValue(valueControl.getValue());
			setting.setBindingItem(pbi);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.custom.ToggleButton.ToggleButtonListener#toggleButtonSelected(org.eclipse.vtp.desktop.core.custom.ToggleButton)
	 */
	public void toggleButtonSelected(ToggleButton button)
    {
		try
        {
	        if(button == defaultButton)
	        {
	        	BindingValue currentItem = setting.getBindingItem();
	        	setting.setBindingItem(null);
	        	BindingValue inheritedItem = setting.getBindingItem();
	        	staticValue.setText(inheritedItem == null ? ultimateDefault : inheritedItem.getValue());
	        	setting.setBindingItem(currentItem);
	        	stackLayout.topControl = staticComp;
	        	staticButton.setSelected(false);
	        	variableButton.setSelected(false);
	        	expressionButton.setSelected(false);
	        }
	        else if(button == variableButton)
	        {
	        	stackLayout.topControl = variableComp;
	        	staticButton.setSelected(false);
	        	defaultButton.setSelected(false);
	        	expressionButton.setSelected(false);
	        }
	        else if(button == expressionButton)
	        {
	        	stackLayout.topControl = expressionComp;
	        	staticButton.setSelected(false);
	        	variableButton.setSelected(false);
	        	defaultButton.setSelected(false);
	        }
	        else
	        {
	        	stackLayout.topControl = valueComp;
	        	defaultButton.setSelected(false);
	        	variableButton.setSelected(false);
	        	expressionButton.setSelected(false);
	        }
	        mainComp.layout(true, true);
	        stackComp.layout(true, true);
	        fireTypeChange();
        }
        catch(RuntimeException e)
        {
	        e.printStackTrace();
        }
    }
	
	/**
	 * @return The value of the configuration item as a string
	 */
	public String getValue()
	{
		if(defaultButton != null && defaultButton.isSelected())
		{
			return staticValue.getText();
		}
		else if(staticButton != null && staticButton.isSelected())
		{
			return valueControl.getValue();
		}
		else if(variableButton != null && variableButton.isSelected())
		{
			return variableText.getText();
		}
		else if(expressionButton != null && expressionButton.isSelected())
		{
			return expressionText.getText();
		}
		return null;
	}
	
	/**
	 * Dispatches a value type change event to all this widget's listeners.
	 */
	private void fireTypeChange()
	{
		for(ValueStackListener listener : listeners)
		{
			listener.valueTypeChanged(this);
		}
	}
	
	/**
	 * @return true if the value is currently static, false otherwise
	 */
	public boolean isStaticValue()
	{
		return staticButton.isSelected();
	}
	
	/**
	 * Adds the given listener to the set of listeners for this widget.  If the
	 * listener is already present, it is removed from this widget and re-added
	 * at the end of the list.
	 * 
	 * @param listener The listener to add
	 */
	public void addListener(ValueStackListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}
	
	/**
	 * Removes the given listener from this widget's list of listeners.  If the
	 * listener was not present, no action is taken.
	 * 
	 * @param listener The listener to remove
	 */
	public void removeListener(ValueStackListener listener)
	{
		listeners.remove(listener);
	}
	
}
