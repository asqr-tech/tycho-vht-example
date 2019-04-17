package org.eclipse.vtp.desktop.media.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.vtp.desktop.core.custom.ToggleButton;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSetting;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSettings;

public class DefaultValueStack implements ToggleButton.ToggleButtonListener
{
	private String settingName = null;
	private StackLayout stackLayout = null;
	private Composite mainComp = null;
	private Composite stackComp = null;
	private Composite staticComp = null;
	private Label staticValue = null;
	private Composite valueComp = null;
	private Composite buttonComp = null;
	private ToggleButton defaultButton = null;
	private ToggleButton staticButton = null;
	private IMediaDefaultSetting setting = null;
	private ValueControl valueControl = null;
	private String interactionType = null;
	private String elementType = null;
	
	/**
	 * @param interactionType
	 * @param elementType
	 * @param settingName
	 */
	public DefaultValueStack(String interactionType, String elementType, String settingName)
	{
		this.interactionType = interactionType;
		this.elementType = elementType;
		this.settingName = settingName;
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
		stackLayout.topControl = staticComp;
		buttonComp = new Composite(mainComp, SWT.NONE);
		buttonComp.setBackground(parent.getBackground());
		gridLayout = new GridLayout(2, true);
		gridLayout.horizontalSpacing = 3;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		buttonComp.setLayout(gridLayout);
		buttonComp.setLayoutData(new GridData());
		staticButton = new ToggleButton(buttonComp);
		staticButton.setBackground(parent.getBackground());
		staticButton.setText("S");
		GridData gridData = new GridData();
		gridData.widthHint = 16;
		gridData.heightHint = 16;
		staticButton.setLayoutData(gridData);
		staticButton.setToggleDownOnly(true);
		staticButton.addSelectionListener(this);
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
	 */
	public void setSetting(IMediaDefaultSettings settings)
	{
		this.setting = settings.getDefaultSetting(interactionType, elementType, settingName);
		staticValue.setText(setting.isValueInherited() ? setting.getValue() : "");
		if(setting.isValueInherited())
		{
			stackLayout.topControl = staticComp;
			defaultButton.setSelected(true);
			staticButton.setSelected(false);
		}
		else
		{
			stackLayout.topControl = valueComp;
			defaultButton.setSelected(false);
			staticButton.setSelected(true);
		}
		valueControl.setValue(setting.getValue());
		mainComp.layout(true, true);
		stackComp.layout(true, true);
	}
	
	public void save()
	{
		if(defaultButton.isSelected())
		{
			setting.setValue(null);
		}
		else
		{
			setting.setValue(valueControl.getValue());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.custom.ToggleButton.ToggleButtonListener#toggleButtonSelected(org.eclipse.vtp.desktop.core.custom.ToggleButton)
	 */
	public void toggleButtonSelected(ToggleButton button)
    {
		if(button == defaultButton)
		{
			String currentSetting = setting.getValue();
			setting.setValue(null);
			staticValue.setText(setting.getValue());
			setting.setValue(currentSetting);
			stackLayout.topControl = staticComp;
			staticButton.setSelected(false);
		}
		else
		{
			stackLayout.topControl = valueComp;
			defaultButton.setSelected(false);
		}
		mainComp.layout(true, true);
		stackComp.layout(true, true);
   }
}
