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
package org.eclipse.vtp.desktop.editors.themes.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Bundle;

/**
 * This class indexes the themes registered with the system and provides ways to
 * search for themes or to determine which theme is active and which should be
 * used by default.
 * 
 * @author trip
 */
public class ThemeManager
{
	/**	The identifier for the them extension point */
	public static String themeExtensionPointId = "org.eclipse.vtp.desktop.editors.themes.core.editorTheme";
	/**	The shared instance of the theme manager */
	private static ThemeManager instance = new ThemeManager();
	
	/**
	 * @return The shared instance of the theme manager
	 */
	public static ThemeManager getDefault()
	{
		return instance;
	}
	
	/**	Indexes the currently registered themes */
	private Map<String, Theme> themes;
	
	/**
	 * Constructs a new <code>ThemeManager</code>.
	 */
	public ThemeManager()
	{
		super();
		themes = new HashMap<String, Theme>();
		IConfigurationElement[] primitiveExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(themeExtensionPointId);
		for(int i = 0; i < primitiveExtensions.length; i++)
		{
			String id = primitiveExtensions[i].getAttribute("id");
			@SuppressWarnings("unused")
			String name = primitiveExtensions[i].getAttribute("name");
			String className = primitiveExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(primitiveExtensions[i].getContributor().getName());
			try
			{
				@SuppressWarnings("unchecked")
				Class<Theme> providerClass = (Class<Theme>)contributor.loadClass(className);
				themes.put(id, providerClass.newInstance());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns a list of available Themes
	 * @return the list of Themes
	 */
	public List<Theme> getInstalledThemes()
	{
		List<Theme> ret = new LinkedList<Theme>();
		ret.addAll(themes.values());
		return ret;
	}
	
	/**
	 * Returns the Theme to use as default.
	 * @return the default Theme
	 */
	public Theme getDefaultTheme()
	{
		return themes.get("org.eclipse.vtp.desktop.editors.themes.attraction");
//		return (Theme)primitiveTypes.get("org.eclipse.vtp.desktop.editors.themes.rust");
	}
	
	/**
	 * Returns the current Theme or the default Theme if none
	 * @return the current Theme or the default Theme if none
	 */
	public Theme getCurrentTheme()
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String themeId = store.getString("CurrentTheme");
		Theme theme = themes.get(themeId);
		if(theme == null)
			theme = getDefaultTheme();
		return theme;
	}
}
