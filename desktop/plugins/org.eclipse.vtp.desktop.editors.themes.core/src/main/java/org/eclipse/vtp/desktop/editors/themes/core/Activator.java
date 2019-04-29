package org.eclipse.vtp.desktop.editors.themes.core;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author trip
 */
public class Activator extends AbstractUIPlugin
{
	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.eclipse.vtp.desktop.editors.themes.core";

	/** The shared instance */
	private static Activator plugin;

	/**
	 * Creates a new instance of this bundle activator.
	 */
	public Activator()
	{
		super();
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return plugin;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeDefaultPluginPreferences()
	 */
	@Override
    protected void initializeDefaultPluginPreferences()
    {
		IPreferenceStore preferenceStore = getPreferenceStore();
		preferenceStore.setDefault("CurrentTheme", "org.eclipse.vtp.desktop.editors.themes.attraction");
    }

}
