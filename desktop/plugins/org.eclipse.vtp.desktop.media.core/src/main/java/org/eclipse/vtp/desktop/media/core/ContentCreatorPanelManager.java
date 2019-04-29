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
import org.eclipse.vtp.desktop.model.interactive.core.content.ContentType;
import org.osgi.framework.Bundle;

public class ContentCreatorPanelManager
{
	public static final String contentCreatorPanelExtensionId = "org.eclipse.vtp.desktop.media.core.contentCreatorPanels";
	private static final ContentCreatorPanelManager INSTANCE = new ContentCreatorPanelManager();
	
	public static ContentCreatorPanelManager getInstance()
	{
		return INSTANCE;
	} 
	
	Map<String, ContentCreatorRecord> creatorPanels = new HashMap<String, ContentCreatorRecord>();

	@SuppressWarnings("unchecked")
	public ContentCreatorPanelManager()
	{
		IConfigurationElement[] creatorExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(contentCreatorPanelExtensionId);
		for(int i = 0; i < creatorExtensions.length; i++)
		{
			ContentCreatorRecord ccr = new ContentCreatorRecord();
			ccr.contentType = creatorExtensions[i].getAttribute("content-type");
			String className = creatorExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(creatorExtensions[i].getContributor().getName());
			try
			{
				ccr.creatorClass = (Class<ContentCreatorPanel>)contributor.loadClass(className);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
			creatorPanels.put(ccr.contentType, ccr);
		}
	}
	
	/**
	 * @param contentType
	 * @return
	 */
	public ContentCreatorPanel getCreatorPanel(ContentType contentType)
	{
		ContentCreatorRecord ccr = creatorPanels.get(contentType.getId());
		if(ccr != null)
		{
			try
			{
				return ccr.creatorClass.newInstance();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return new MissingContentCreatorPanel();
	}

	private class ContentCreatorRecord
	{
		String contentType;
		Class<ContentCreatorPanel> creatorClass;
	}
}
