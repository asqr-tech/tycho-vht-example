package org.eclipse.vtp.desktop.editors.core.widgets;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.core.custom.ToggleButton;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.ObjectField;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.BrandBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PropertyBindingItem;

public class ValueStack implements ToggleButton.ToggleButtonListener
{
	private String settingName = null;
	private StackLayout stackLayout = null;
	private Composite mainComp = null;
	private Composite stackComp = null;
	private Composite staticComp = null;
	private Label staticValue = null;
	private Composite valueComp = null;
	private ValueControl valueControl = null;
	private Composite variableComp = null;
	private Combo variableCombo = null;
	private Composite expressionComp = null;
	private Text expressionText = null;
	private Composite buttonComp = null;
	private ToggleButton defaultButton = null;
	private ToggleButton staticButton = null;
	private ToggleButton variableButton = null;
	private ToggleButton expressionButton = null;
	private BrandBinding setting = null;
	private List<ValueStackListener> listeners = new LinkedList<ValueStackListener>();
	private String ultimateDefault = "";
	private List<Variable> variables = new ArrayList<Variable>();
	
	public ValueStack(String settingName, String ultimateDefault, List<Variable> variables)
	{
		this.settingName = settingName;
		this.ultimateDefault = ultimateDefault;
		this.variables = variables;
	}
	
	public void createControls(Composite parent)
	{
		mainComp = new Composite(parent, SWT.NONE);
		mainComp.setBackground(parent.getBackground());
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
		stackComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));//(parent.getBackground());
		stackComp.setLayout(stackLayout = new StackLayout());
		stackComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		staticComp = new Composite(stackComp, SWT.NONE);
		staticComp.setBackground(parent.getBackground());
		gridLayout = new GridLayout(1, true);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 4;
		staticComp.setLayout(gridLayout);
		staticValue = new Label(staticComp, SWT.NONE);
		staticValue.setBackground(parent.getBackground());
		gd = new GridData(GridData.FILL_BOTH);
		gd.verticalAlignment = SWT.CENTER;
		gd.horizontalIndent = 4;
		staticValue.setLayoutData(gd);
		staticValue.setAlignment(SWT.RIGHT);
		staticValue.setText("null");

		valueComp = new Composite(stackComp, SWT.NONE);
		valueComp.setBackground(parent.getBackground());
		gridLayout = new GridLayout(1, true);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 4;
		valueComp.setLayout(gridLayout);
		
		variableComp = new Composite(stackComp, SWT.NONE);
		variableComp.setBackground(parent.getBackground());
		gridLayout = new GridLayout(1, true);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 4;
		variableComp.setLayout(gridLayout);
		variableCombo = new Combo(variableComp, SWT.DROP_DOWN | SWT.READ_ONLY);
		for(Variable var : variables)
		{
			addVariable(var);
		}
		variableCombo.select(0);
		gd = new GridData(GridData.FILL_BOTH);
		gd.verticalAlignment = SWT.CENTER;
		gd.horizontalIndent = 4;
		variableCombo.setLayoutData(gd);

		expressionComp = new Composite(stackComp, SWT.NONE);
		expressionComp.setBackground(parent.getBackground());
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
		buttonComp.setBackground(parent.getBackground());
		gridLayout = new GridLayout(4, true);
		gridLayout.horizontalSpacing = 3;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		buttonComp.setLayout(gridLayout);
		buttonComp.setLayoutData(new GridData());

		GridData gridData = null;
		staticButton = new ToggleButton(buttonComp);
		staticButton.setBackground(parent.getBackground());
		staticButton.setText("S");
		gridData = new GridData();
		gridData.widthHint = 16;
		gridData.heightHint = 16;
		staticButton.setLayoutData(gridData);
		staticButton.setToggleDownOnly(true);
		staticButton.addSelectionListener(this);

		variableButton = new ToggleButton(buttonComp);
		variableButton.setBackground(parent.getBackground());
		variableButton.setText("V");
		gridData = new GridData();
		gridData.widthHint = 16;
		gridData.heightHint = 16;
		variableButton.setLayoutData(gridData);
		variableButton.setSelected(true);
		variableButton.setToggleDownOnly(true);
		variableButton.addSelectionListener(this);
		
		expressionButton = new ToggleButton(buttonComp);
		expressionButton.setBackground(parent.getBackground());
		expressionButton.setText("E");
		gridData = new GridData();
		gridData.widthHint = 16;
		gridData.heightHint = 16;
		expressionButton.setLayoutData(gridData);
		expressionButton.setSelected(true);
		expressionButton.setToggleDownOnly(true);
		expressionButton.addSelectionListener(this);
		
		defaultButton = new ToggleButton(buttonComp);
		defaultButton.setBackground(parent.getBackground());
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
	
	public Composite getValueComposite()
	{
		return valueComp;
	}
	
	public void setValueControl(ValueControl valueControl)
	{
		this.valueControl = valueControl;
	}
	
	public String getSettingName()
	{
		return settingName;
	}
	
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
			mainComp.layout(true, true);
		}
		else
		{
			GridLayout layout = (GridLayout)buttonComp.getLayout();
			layout.numColumns = 4;
			((GridData)defaultButton.getLayoutData()).exclude = false;
			defaultButton.setVisible(true);
			mainComp.layout(true, true);
		}
		PropertyBindingItem pbi = (PropertyBindingItem)setting.getBindingItem();
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
			staticValue.setText(pbi.getValue());
			stackLayout.topControl = staticComp;
			defaultButton.setSelected(true);
			staticButton.setSelected(false);
			variableButton.setSelected(false);
			expressionButton.setSelected(false);
			valueControl.setValue(pbi.getValue());
		}
		else
		{
			staticValue.setText("");
			defaultButton.setSelected(false);
			if(pbi.getValueType().equals(PropertyBindingItem.STATIC))
			{
				stackLayout.topControl = valueComp;
				staticButton.setSelected(true);
				variableButton.setSelected(false);
				expressionButton.setSelected(false);
				valueControl.setValue(pbi.getValue());
			}
			else if(pbi.getValueType().equals(PropertyBindingItem.VARIABLE))
			{
				stackLayout.topControl = variableComp;
				staticButton.setSelected(false);
				variableButton.setSelected(true);
				expressionButton.setSelected(false);
				variableCombo.select(0);
				for(int i = 0; i < variableCombo.getItemCount(); i++)
				{
					if(variableCombo.getItem(i).equals(pbi.getValue()))
					{
						variableCombo.select(i);
						break;
					}
				}
			}
			else
			{
				stackLayout.topControl = expressionComp;
				staticButton.setSelected(false);
				variableButton.setSelected(false);
				expressionButton.setSelected(true);
				expressionText.setText(pbi.getValue());
			}
		}
		mainComp.layout(true, true);
		stackComp.layout(true, true);
	}
	
	public void save()
	{
		if(defaultButton.isSelected())
		{
			setting.setBindingItem(null);
		}
		else if(variableButton != null && variableButton.isSelected())
		{
			PropertyBindingItem pbi = (PropertyBindingItem)setting.getBindingItem();
			if(pbi == null || setting.isInherited())
			{
				pbi = new PropertyBindingItem();
			}
			pbi.setVariable(variableCombo.getItem(variableCombo.getSelectionIndex()));
			setting.setBindingItem(pbi);
		}
		else if(expressionButton != null && expressionButton.isSelected())
		{
			PropertyBindingItem pbi = (PropertyBindingItem)setting.getBindingItem();
			if(pbi == null || setting.isInherited())
			{
				pbi = new PropertyBindingItem();
			}
			pbi.setExpression(expressionText.getText());
			setting.setBindingItem(pbi);
		}
		else
		{
			PropertyBindingItem pbi = (PropertyBindingItem)setting.getBindingItem();
			if(pbi == null || setting.isInherited())
			{
				pbi = new PropertyBindingItem();
			}
			pbi.setStaticValue(valueControl.getValue());
			setting.setBindingItem(pbi);
		}
	}

	public void toggleButtonSelected(ToggleButton button)
    {
		try
        {
	        if(button == defaultButton)
	        {
	        	PropertyBindingItem currentItem = (PropertyBindingItem)setting.getBindingItem();
	        	setting.setBindingItem(null);
	        	PropertyBindingItem inheritedItem = (PropertyBindingItem)setting.getBindingItem();
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
			return variableCombo.getItem(variableCombo.getSelectionIndex());
		}
		else if(expressionButton != null && expressionButton.isSelected())
		{
			return expressionText.getText();
		}
		return null;
	}
	
	private void fireTypeChange()
	{
		for(ValueStackListener listener : listeners)
		{
			listener.valueTypeChanged(this);
		}
	}
	
	public boolean isStaticValue()
	{
		return staticButton.isSelected();
	}
	
	public void addListener(ValueStackListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}
	
	public void removeListener(ValueStackListener listener)
	{
		listeners.remove(listener);
	}
	
	private void addVariable(ObjectDefinition var)
	{
		if(var.getType().hasValue())
		{
			variableCombo.add(var.getPath());
		}
		else
		{
			List<ObjectField> fields = var.getFields();
			for(ObjectField field : fields)
			{
				addVariable(field);
			}
		}
	}
}
