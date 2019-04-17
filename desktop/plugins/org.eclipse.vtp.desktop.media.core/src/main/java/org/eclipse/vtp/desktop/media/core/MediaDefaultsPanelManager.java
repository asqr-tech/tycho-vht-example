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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class MediaDefaultsPanelManager
{
	public static final String mediaDefaultPanelExtensionId = "org.eclipse.vtp.desktop.media.core.mediaDefaultPanel";
	private static final MediaDefaultsPanelManager INSTANCE = new MediaDefaultsPanelManager();
	
	public static MediaDefaultsPanelManager getInstance()
	{
		return INSTANCE;
	}

	Map<String, Map<String, DefaultsPanelRecord>> configurationScreens = null;

	@SuppressWarnings("unchecked")
	public MediaDefaultsPanelManager()
	{
		super();
		configurationScreens = new HashMap<String, Map<String, DefaultsPanelRecord>>();
		IConfigurationElement[] screenExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(mediaDefaultPanelExtensionId);
		for(int i = 0; i < screenExtensions.length; i++)
		{
			DefaultsPanelRecord dpr = new DefaultsPanelRecord();
			dpr.primitiveTypeId = screenExtensions[i].getAttribute("primitive-id");
			dpr.interactionType = screenExtensions[i].getAttribute("interaction-type");
			String className = screenExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(screenExtensions[i].getContributor().getName());
			try
			{
				dpr.screenClass = (Class<IMediaDefaultPanel>)contributor.loadClass(className);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
			Map<String, DefaultsPanelRecord> byInteraction = configurationScreens.get(dpr.interactionType);
			if(byInteraction == null)
			{
				byInteraction = new HashMap<String, DefaultsPanelRecord>();
				configurationScreens.put(dpr.interactionType, byInteraction);
			}
			byInteraction.put(dpr.primitiveTypeId, dpr);
		}
	}

	public IMediaDefaultPanel getMediaDefaultsPanel(String primitiveTypeId, String interactionType)
	{
		IMediaDefaultPanel ret = null;
		Map<String, DefaultsPanelRecord> byInteraction = configurationScreens.get(interactionType);
		if(byInteraction != null)
		{
			DefaultsPanelRecord csr = byInteraction.get(primitiveTypeId);
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
	
	public Map<String, IMediaDefaultPanel> getIndexedMediaDefaultsPanels(String interactionType)
	{
		Map<String, IMediaDefaultPanel> ret = new HashMap<String, IMediaDefaultPanel>();
		Map<String, DefaultsPanelRecord> byInteraction = configurationScreens.get(interactionType);
		if(byInteraction != null)
		{
			for(Map.Entry<String, DefaultsPanelRecord> entry : byInteraction.entrySet())
			{
				DefaultsPanelRecord csr = entry.getValue();
				if(csr != null)
				{
					try
					{
						ret.put(entry.getKey(), csr.screenClass.newInstance());
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

	private class DefaultsPanelRecord
	{
		String primitiveTypeId;
		String interactionType;
		Class<IMediaDefaultPanel> screenClass;
	}
}
