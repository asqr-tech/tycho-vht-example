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
package org.eclipse.vtp.desktop.media.core;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class MediaConfigurationScreenManager
{
	public static final String mediaConfigurationScreenExtensionId = "org.eclipse.vtp.desktop.media.core.mediaConfigurationScreens";
	private static final MediaConfigurationScreenManager INSTANCE = new MediaConfigurationScreenManager();
	
	public static MediaConfigurationScreenManager getInstance()
	{
		return INSTANCE;
	}
	
	Map<String, Map<String, ConfigurationScreenRecord>> configurationScreens = null;

	@SuppressWarnings("unchecked")
	public MediaConfigurationScreenManager()
	{
		super();
		configurationScreens = new HashMap<String, Map<String, ConfigurationScreenRecord>>();
		IConfigurationElement[] screenExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(mediaConfigurationScreenExtensionId);
		for(int i = 0; i < screenExtensions.length; i++)
		{
			ConfigurationScreenRecord csr = new ConfigurationScreenRecord();
			csr.primitiveTypeId = screenExtensions[i].getAttribute("primitive-id");
			csr.interactionType = screenExtensions[i].getAttribute("interaction-type");
			String className = screenExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(screenExtensions[i].getContributor().getName());
			try
			{
				csr.screenClass = (Class<MediaConfigurationScreen>)contributor.loadClass(className);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
			Map<String, ConfigurationScreenRecord> byInteraction = configurationScreens.get(csr.primitiveTypeId);
			if(byInteraction == null)
			{
				byInteraction = new HashMap<String, ConfigurationScreenRecord>();
				configurationScreens.put(csr.primitiveTypeId, byInteraction);
			}
			byInteraction.put(csr.interactionType, csr);
		}
	}
	
	/**
	 * @param primitiveTypeId
	 * @param interactionType
	 * @return
	 */
	public MediaConfigurationScreen getMediaConfigurationScreen(String primitiveTypeId, String interactionType)
	{
		MediaConfigurationScreen ret = null;
		Map<String, ConfigurationScreenRecord> byInteraction = configurationScreens.get(primitiveTypeId);
		if(byInteraction != null)
		{
			ConfigurationScreenRecord csr = byInteraction.get(interactionType);
			if(csr != null)
			{
				try
				{
					ret = csr.screenClass.newInstance();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	/**
	 * @param primitiveTypeId
	 * @param element
	 * @return
	 */
	public Map<String, MediaConfigurationScreen> getIndexedMediaConfigurationScreens(String primitiveTypeId, MediaConfigurationScreenContainer container)
	{
		Map<String, MediaConfigurationScreen> ret = new HashMap<String, MediaConfigurationScreen>();
		Map<String, ConfigurationScreenRecord> byInteraction = configurationScreens.get(primitiveTypeId);
		if(byInteraction != null)
		{
			for(Map.Entry<String, ConfigurationScreenRecord> entry : byInteraction.entrySet())
			{
				ConfigurationScreenRecord csr = entry.getValue();
				if(csr != null)
				{
					try
					{
						Constructor<MediaConfigurationScreen> con = csr.screenClass.getConstructor(new Class[] {MediaConfigurationScreenContainer.class});
						ret.put(entry.getKey(), con.newInstance(new Object[] { container}));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		return ret;
	}

	private class ConfigurationScreenRecord
	{
		String primitiveTypeId;
		String interactionType;
		Class<MediaConfigurationScreen> screenClass;
	}
}
