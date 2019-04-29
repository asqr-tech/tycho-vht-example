package org.eclipse.vtp.desktop.model.interactive.core;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.vtp.desktop.model.interactive.core.internal.InteractiveWorkflowModel;
import org.eclipse.vtp.desktop.model.interactive.core.internal.mediadefaults.DefaultSettingsRegistry;
import org.eclipse.vtp.desktop.model.interactive.core.internal.mediadefaults.DefaultSettingsRegistry.DefaultSettingGroupRecord;
import org.osgi.framework.BundleContext;

public class InteractiveWorkflowCore extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.vtp.desktop.model.interactive.core";
	// The shared instance
	private static InteractiveWorkflowCore plugin;
	
	private InteractiveWorkflowModel model = null;

	public InteractiveWorkflowCore()
	{
		plugin = this;
	}

	public void start(BundleContext context) throws Exception
	{
		model = new InteractiveWorkflowModel();
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception
	{
		super.stop(context);
	}

	public IInteractiveWorkflowModel getInteractiveWorkflowModel()
	{
		return model;
	}
	
	public static InteractiveWorkflowCore getDefault()
	{
		return plugin;
	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store)
	{
		System.err.println("initializing default preferences...");
		List<DefaultSettingGroupRecord> records = DefaultSettingsRegistry.getInstance().getDefaultRecords();
		for(DefaultSettingGroupRecord record : records)
		{
			for(Map.Entry<String, String> entry : record.defaultSettings.entrySet())
			{
				System.err.println("set default [" + record.interactionType + ":" + record.elementType + ":" + entry.getKey() + ", " + entry.getValue() + "]");
				store.setDefault(record.interactionType + ":" + record.elementType + ":" + entry.getKey(), entry.getValue());
			}
		}
		System.err.println("value of org.eclipse.vtp.framework.interactions.voice.interaction:org.eclipse.vtp.modules.interactive.playPrompt:barge-in: " + store.getString("org.eclipse.vtp.framework.interactions.voice.interaction:org.eclipse.vtp.modules.interactive.playPrompt:barge-in"));
	}

}
