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
package org.eclipse.vtp.desktop.model.interactive.voice.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.media.core.FormatterRegistration;
import org.eclipse.vtp.desktop.media.core.FormatterRegistrationManager;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaResource;
import org.eclipse.vtp.desktop.model.interactive.core.content.ContentType;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.FormattableContent;
import org.eclipse.vtp.framework.interactions.core.media.IContentType;
import org.eclipse.vtp.framework.interactions.core.media.IFormatManager;
import org.eclipse.vtp.framework.interactions.core.media.IFormatter;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;
import org.eclipse.vtp.framework.interactions.core.media.IResourceManager;
import org.eclipse.vtp.framework.interactions.core.media.ISharedContentProvider;

public class VoiceMediaProvider implements IMediaProvider{
	IFormatManager formatManager = new VoiceFormatManager();
	IResourceManager resourceManager = new VoiceResourceManager();
	ISharedContentProvider sharedContentProvider = new VoiceSharedContentManager();
	VoiceProject project = null;
	VoiceModel model = null;
	IFormatter formatter = null;

	public VoiceMediaProvider(VoiceProject project) {
		super();
		this.project = project;
		model = new VoiceModel(project);
		try {
			model.load();
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		try {
			FormatterRegistration fr = FormatterRegistrationManager
					.getInstance().getFormatter(project.getLanguagePackId());
			formatter = fr.getFormatter();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public IFormatManager getFormatManager() {
		return formatManager;
	}

	public IFormatter getFormatter() {
		return formatter;
	}

	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	public ISharedContentProvider getSharedContentProvider() {
		return sharedContentProvider;
	}

	public List<IContentType> getSupportedContentTypes() {
		List<IContentType> types = new ArrayList<IContentType>();
		types.add(new ContentType(
			"org.eclipse.vtp.framework.interactions.voice.media.content.audio",
			"Audio File"));
		types.add(new ContentType(
			"org.eclipse.vtp.framework.interactions.core.media.content.letters",
			"Characters"));
		types.add(new ContentType(
			"org.eclipse.vtp.framework.interactions.core.media.content.date", "Date"));
		types.add(new ContentType(
			"org.eclipse.vtp.framework.interactions.core.media.content.digits", "Digits"));
		types.add(new ContentType(
			"org.eclipse.vtp.framework.interactions.core.media.content.money", "Money"));
		types.add(new ContentType(
			"org.eclipse.vtp.framework.interactions.core.media.content.number", "Number"));
		types.add(new ContentType(
			"org.eclipse.vtp.framework.interactions.core.media.content.ordinal", "Ordinal"));
		types.add(new ContentType(
				"org.eclipse.vtp.framework.interactions.core.media.content.referenced",
				"Reference"));
		types.add(new ContentType(
			"org.eclipse.vtp.framework.interactions.core.media.content.text", "Text"));
		return types;
	}

	public boolean hasSharedContent() {
		return true;
	}

	private class VoiceFormatManager implements IFormatManager {

		public String getFormat(FormattableContent content, String formatName) {
			if(formatName.equals("Default"))
				return "Default";
			return formatter.getDefaultFormatDefintion(content, formatName);
		}

		public List<String> getFormats(FormattableContent content) {
			List<String> ret = new ArrayList<String>();
			ret.add("Default");
			ret.addAll(formatter.getDefaultFormats(content));
			return ret;
		}

	}

	private class VoiceResourceManager implements IResourceManager
	{

		public boolean isDirectoryResource(String path)
		{
			return path == null || (path.endsWith("/") && find(path) != null);
		}

		public boolean isFileResource(String path)
		{
			return path != null && !path.endsWith("/") && find(path) != null;
		}

		public String[] listResources(String directoryPath)
		{
			IMediaObject resource = find(directoryPath);
			if (resource instanceof IMediaContainer)
			{
				List<IMediaResource> resources = null;
				try
				{
					resources = ((IMediaContainer) resource)
							.listMediaResources();
				}
				catch (CoreException e)
				{
					e.printStackTrace();
					return null;
				}
				String[] results = new String[resources.size()];
				for (int i = 0; i < results.length; ++i)
				{
					IMediaResource item = resources.get(i);
					results[i] = item.getName();
					if (item instanceof IMediaContainer)
						results[i] += "/";
				}
				return results;
			}
			return null;
		}

		private IMediaObject find(String path)
		{
			if (path == null || path.length() == 0)
				path = "/";
			IMediaContainer folder = project.getMediaFilesFolder();
			IMediaObject result = folder;
			for (StringTokenizer st = new StringTokenizer(path, "/"); st
					.hasMoreTokens();) {
				String token = st.nextToken();
				if (st.hasMoreTokens()) {
					folder = folder.getMediaFolder(token);
					if (folder == null)
						return null;
				} else {
					result = folder.getMediaFolder(token);
					if (result == null)
						result = folder.getMediaFile(token);
				}
			}
			return result;
		}
	}

	private class VoiceSharedContentManager implements ISharedContentProvider {

		public Content getSharedContent(String contentName) {
			return model.getSharedContent(contentName);
		}

		public List<String> listSharedContent() {
			return Arrays.asList(model.getSharedContentNames());
		}

	}
}
