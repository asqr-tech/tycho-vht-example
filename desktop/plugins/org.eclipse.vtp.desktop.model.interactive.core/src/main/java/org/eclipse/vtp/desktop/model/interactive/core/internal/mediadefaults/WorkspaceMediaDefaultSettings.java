package org.eclipse.vtp.desktop.model.interactive.core.internal.mediadefaults;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSetting;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSettings;

public class WorkspaceMediaDefaultSettings implements IMediaDefaultSettings
{
	private static final WorkspaceMediaDefaultSettings instance = new WorkspaceMediaDefaultSettings();
	
	public static WorkspaceMediaDefaultSettings getInstance()
	{
		return instance;
	}
	
	private WorkspaceMediaDefaultSettings()
	{
	}
	
	public IMediaDefaultSetting getDefaultSetting(String interactionType, String elementType,
	        String setting)
	{
		return new WorkspaceMediaDefaultSetting(interactionType, elementType, setting);
	}

	private class WorkspaceMediaDefaultSetting implements IMediaDefaultSetting
	{
		String interactionType = "";
		String elementType = "";
		String setting = "";
		
		public WorkspaceMediaDefaultSetting(String interactionType, String elementType, String setting)
		{
			super();
			this.interactionType = interactionType;
			this.elementType = elementType;
			this.setting = setting;
		}

		public String getElementType()
        {
	        return elementType;
        }

		public String getInteractionType()
        {
	        return interactionType;
        }

		public String getName()
        {
	        return setting;
        }

		public String getValue()
        {
			IPreferenceStore preferenceStore = InteractiveWorkflowCore.getDefault().getPreferenceStore();
			String settingValue = preferenceStore.getString(interactionType + ":" + elementType + ":" + setting);
			System.out.println("got preference value " + interactionType + ":" + elementType + ":" + setting + ", " + settingValue);
			return settingValue;
        }

		public boolean isValueInherited()
        {
	        return false;
        }

		public void setValue(String value)
        {
			InteractiveWorkflowCore.getDefault().getPreferenceStore().setValue(interactionType + ":" + elementType + ":" + setting, value);
        }
		
	}

	public boolean inheritanceSupported()
    {
	    return false;
    }
	
	public List<String> getDefaultSettingNames(String elementType, String interactionType)
	{
		return DefaultSettingsRegistry.getInstance().getDefaultSettingNames(elementType, interactionType);
	}
}
