package org.eclipse.vtp.desktop.media.core;

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
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.BrandBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PropertyBindingItem;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSetting;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSettings;

public class ValueStack implements ToggleButton.ToggleButtonListener
{
	public static final int VARIABLE = 1;
	public static final int EXPRESSION = 2;
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
	private IMediaDefaultSetting defaultSetting = null;
	private String elementType = null;
	private String interactionType = null;
	private BrandBinding setting = null;
	private int flags = 3;
	private List<ValueStackListener> listeners = new LinkedList<ValueStackListener>();
	private String ultimateDefault = "";
	
	/**
	 * @param settingName
	 * @param interactionType
	 * @param elementType
	 * @param ultimateDefault
	 * @param flags
	 */
	public ValueStack(String settingName, String interactionType, String elementType, String ultimateDefault, int flags)
	{
		if(flags < 0 || flags > 3)
			throw new IllegalArgumentException("Invalid flags: " + flags);
		this.settingName = settingName;
		this.interactionType = interactionType;
		this.elementType = elementType;
		this.flags = flags;
		this.ultimateDefault = ultimateDefault;
	}
	
	/**
	 * @param parent
	 */
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
		stackComp.setBackground(parent.getBackground());
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
		int cols = 2;
		
		if((flags & VARIABLE) > 0)
		{
			cols++;
			variableComp = new Composite(stackComp, SWT.NONE);
			variableComp.setBackground(parent.getBackground());
			gridLayout = new GridLayout(1, true);
			gridLayout.horizontalSpacing = 0;
			gridLayout.verticalSpacing = 0;
			gridLayout.marginHeight = 0;
			gridLayout.marginWidth = 4;
			variableComp.setLayout(gridLayout);
			variableCombo = new Combo(variableComp, SWT.DROP_DOWN | SWT.READ_ONLY);
			variableCombo.add("variable");
			variableCombo.select(0);
			gd = new GridData(GridData.FILL_BOTH);
			gd.verticalAlignment = SWT.CENTER;
			gd.horizontalIndent = 4;
			variableCombo.setLayoutData(gd);
		}

		if((flags & EXPRESSION) > 0)
		{
			cols++;
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
		}

		stackLayout.topControl = staticComp != null ? staticComp : valueComp != null ? valueComp : variableComp != null ? variableComp : expressionComp;
		buttonComp = new Composite(mainComp, SWT.NONE);
		buttonComp.setBackground(parent.getBackground());
		gridLayout = new GridLayout(cols, true);
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

		if((flags & VARIABLE) > 0)
		{
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
		}
		
		if((flags & EXPRESSION) > 0)
		{
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
		}
		
		defaultButton = new ToggleButton(buttonComp);
		defaultButton.setBackground(parent.getBackground());
		defaultButton.setText("D");
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
	 * @return
	 */
	public Composite getValueComposite()
	{
		return valueComp;
	}
	
	/**
	 * @param valueControl
	 */
	public void setValueControl(ValueControl valueControl)
	{
		this.valueControl = valueControl;
	}
	
	/**
	 * @param settings
	 * @param brandBinding
	 */
	public void setSetting(IMediaDefaultSettings settings, BrandBinding brandBinding)
	{
		if(setting != null && defaultSetting != null)
			save();
		this.setting = brandBinding;
		if(setting.getBrand().getParent() == null) //the default brand
			defaultButton.setText("D");
		else
			defaultButton.setText("I");
		this.defaultSetting = settings.getDefaultSetting(interactionType, elementType, settingName);
		PropertyBindingItem pbi = (PropertyBindingItem)setting.getBindingItem();
		if(pbi == null && setting.isInherited()) //use default settings
		{
			String value = defaultSetting.getValue();
			if(value == null || value.equals(""))
				value = ultimateDefault;
			staticValue.setText(value);
			stackLayout.topControl = staticComp;
			defaultButton.setSelected(true);
			staticButton.setSelected(false);
			if((flags & VARIABLE) > 0)
				variableButton.setSelected(false);
			if((flags & EXPRESSION) > 0)
				expressionButton.setSelected(false);
			valueControl.setValue(value);
		}
		else if(setting.isInherited()) //use pbi as static inherited value
		{
			staticValue.setText(pbi.getValue());
			stackLayout.topControl = staticComp;
			defaultButton.setSelected(true);
			staticButton.setSelected(false);
			if((flags & VARIABLE) > 0)
				variableButton.setSelected(false);
			if((flags & EXPRESSION) > 0)
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
				if((flags & VARIABLE) > 0)
					variableButton.setSelected(false);
				if((flags & EXPRESSION) > 0)
					expressionButton.setSelected(false);
				valueControl.setValue(pbi.getValue());
			}
			else if(pbi.getValueType().equals(PropertyBindingItem.VARIABLE))
			{
				stackLayout.topControl = variableComp;
				staticButton.setSelected(false);
				variableButton.setSelected(true);
				expressionButton.setSelected(false);
				//valueControl.setValue(pbi.getValue());
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
			pbi.setValueType(PropertyBindingItem.VARIABLE);
			pbi.setValue(variableCombo.getItem(variableCombo.getSelectionIndex()));
			setting.setBindingItem(pbi);
		}
		else if(expressionButton != null && expressionButton.isSelected())
		{
			PropertyBindingItem pbi = (PropertyBindingItem)setting.getBindingItem();
			if(pbi == null || setting.isInherited())
			{
				pbi = new PropertyBindingItem();
			}
			pbi.setValueType(PropertyBindingItem.EXPRESSION);
			pbi.setValue(expressionText.getText());
			setting.setBindingItem(pbi);
		}
		else
		{
			PropertyBindingItem pbi = (PropertyBindingItem)setting.getBindingItem();
			if(pbi == null || setting.isInherited())
			{
				pbi = new PropertyBindingItem();
			}
			pbi.setValueType(PropertyBindingItem.STATIC);
			pbi.setValue(valueControl.getValue());
			setting.setBindingItem(pbi);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.custom.ToggleButton.ToggleButtonListener#toggleButtonSelected(org.eclipse.vtp.desktop.core.custom.ToggleButton)
	 */
	public void toggleButtonSelected(ToggleButton button)
    {
		if(button == defaultButton)
		{
			PropertyBindingItem currentItem = (PropertyBindingItem)setting.getBindingItem();
			setting.setBindingItem(null);
			PropertyBindingItem inheritedItem = (PropertyBindingItem)setting.getBindingItem();
			staticValue.setText(inheritedItem == null ? defaultSetting.getValue() : inheritedItem.getValue());
			setting.setBindingItem(currentItem);
			stackLayout.topControl = staticComp;
			staticButton.setSelected(false);
			if((flags & VARIABLE) > 0)
				variableButton.setSelected(false);
			if((flags & EXPRESSION) > 0)
				expressionButton.setSelected(false);
		}
		else if(button == variableButton)
		{
			stackLayout.topControl = variableComp;
			staticButton.setSelected(false);
			defaultButton.setSelected(false);
			if((flags & EXPRESSION) > 0)
				expressionButton.setSelected(false);
		}
		else if(button == expressionButton)
		{
			stackLayout.topControl = expressionComp;
			staticButton.setSelected(false);
			if((flags & VARIABLE) > 0)
				variableButton.setSelected(false);
			defaultButton.setSelected(false);
		}
		else
		{
			stackLayout.topControl = valueComp;
			defaultButton.setSelected(false);
			if((flags & VARIABLE) > 0)
				variableButton.setSelected(false);
			if((flags & EXPRESSION) > 0)
				expressionButton.setSelected(false);
		}
		mainComp.layout(true, true);
		stackComp.layout(true, true);
		fireTypeChange();
    }
	
	/**
	 * @return
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
	
	/**
	 * @param listener
	 */
	public void addListener(ValueStackListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}
	
	/**
	 * @param listener
	 */
	public void removeListener(ValueStackListener listener)
	{
		listeners.remove(listener);
	}
}
