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
package org.eclipse.vtp.desktop.model.interactive.core.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFile;
import org.osgi.framework.Bundle;

public class MediaTypeManager
{
	public static final String mediaTypeExtensionId = "org.eclipse.vtp.desktop.model.interactive.core.mediaFileTypes";
	private static final MediaTypeManager INSTANCE = new MediaTypeManager();
	
	public static MediaTypeManager getInstance()
	{
		return INSTANCE;
	}

	private Map<String, MediaTypeRecord> mediaTypesByExtension;
	private Map<String, String> mimeTypesByExtension;
	
	@SuppressWarnings("unchecked")
	public MediaTypeManager()
	{
		super();
		mediaTypesByExtension = new HashMap<String, MediaTypeRecord>();
		mimeTypesByExtension = new HashMap<String, String>();
		IConfigurationElement[] mediaTypeExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(mediaTypeExtensionId);
		for(int i = 0; i < mediaTypeExtensions.length; i++)
		{
			MediaTypeRecord mtr = new MediaTypeRecord();
			mtr.id = mediaTypeExtensions[i].getAttribute("id");
			mtr.name = mediaTypeExtensions[i].getAttribute("type-name");
			String className = mediaTypeExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(mediaTypeExtensions[i].getContributor().getName());
			try
			{
				mtr.mediaClass = (Class<IMediaFileFactory>) contributor.loadClass(className);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
			IConfigurationElement[] mappings = mediaTypeExtensions[i].getChildren("mapping");
			for(int m = 0; m < mappings.length; m++)
			{
				String extension = mappings[m].getAttribute("extension").toLowerCase();
				String mimeType = mappings[m].getAttribute("mime-type");
				mimeTypesByExtension.put(extension, mimeType);
				mediaTypesByExtension.put(extension, mtr);
			}
		}
	}
	
	/**
	 * @param file
	 * @return
	 */
	public String getMimeType(IFile file)
	{
		String extension = file.getFileExtension().toLowerCase();
		return mimeTypesByExtension.get(extension);
	}
	
	/**
	 * @param container
	 * @param file
	 * @return
	 */
	public IMediaFile createMediaFile(IMediaContainer container, IFile file)
	{
		String extension = file.getFileExtension().toLowerCase();
		MediaTypeRecord mtr = mediaTypesByExtension.get(extension);
		if(mtr == null)
			return null;
		try
		{
			IMediaFileFactory factory = mtr.mediaClass.newInstance();
			return factory.createMediaFile(container, file);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	private class MediaTypeRecord
	{
		@SuppressWarnings("unused")
		String id;
		@SuppressWarnings("unused")
		String name;
		Class<IMediaFileFactory> mediaClass;
	}
}
