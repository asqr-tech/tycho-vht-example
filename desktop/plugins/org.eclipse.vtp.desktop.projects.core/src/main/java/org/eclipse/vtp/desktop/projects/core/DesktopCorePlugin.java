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
package org.eclipse.vtp.desktop.projects.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class DesktopCorePlugin extends Plugin
{
	// The shared instance.
	private static DesktopCorePlugin plugin;

	// Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public DesktopCorePlugin()
	{
		super();
		plugin = this;

		try
		{
			resourceBundle = ResourceBundle.getBundle(
					"org.eclipse.vtp.desktop.core.ProvisioningCorePluginResources");
		}
		catch(MissingResourceException x)
		{
			resourceBundle = null;
		}
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception
	{
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * @return 
	 */
	public static DesktopCorePlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 * @param key 
	 * @return 
	 */
	public static String getResourceString(String key)
	{
		ResourceBundle bundle =
			DesktopCorePlugin.getDefault().getResourceBundle();

		try
		{
			return (bundle != null) ? bundle.getString(key) : key;
		}
		catch(MissingResourceException e)
		{
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 * @return the plugin's ResourceBundle
	 */
	public ResourceBundle getResourceBundle()
	{
		return resourceBundle;
	}

}
