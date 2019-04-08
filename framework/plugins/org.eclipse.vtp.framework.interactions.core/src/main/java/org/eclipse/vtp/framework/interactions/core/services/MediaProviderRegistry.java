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
package org.eclipse.vtp.framework.interactions.core.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.framework.core.IProcessContext;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaProviderBindingConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaProviderConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.SharedContentConfiguration;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.FormattableContent;
import org.eclipse.vtp.framework.interactions.core.media.IContentTypeRegistry;
import org.eclipse.vtp.framework.interactions.core.media.IFormatManager;
import org.eclipse.vtp.framework.interactions.core.media.IFormatter;
import org.eclipse.vtp.framework.interactions.core.media.IFormatterRegistry;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProviderRegistry;
import org.eclipse.vtp.framework.interactions.core.media.IResourceManager;
import org.eclipse.vtp.framework.interactions.core.media.IResourceManagerRegistry;
import org.eclipse.vtp.framework.interactions.core.media.ISharedContentProvider;

/**
 * Implementation of {@link IMediaProviderRegistry}.
 * 
 * @author Lonnie Pryor
 */
public class MediaProviderRegistry implements IMediaProviderRegistry
{
	/** The media providers by ID. */
	private final Map mediaProviders;
	/** The media providers by ID. */
	private final Map mediaProviderIndex;

	/**
	 * Creates a new MediaProviderRegistry.
	 * 
	 * @param context The context to operate in.
	 * @param content The content manager.
	 * @param formatters The formatters.
	 * @param resources The resource managers.
	 * @param configurations The configurations to use.
	 */
	public MediaProviderRegistry(IProcessContext context,
			IContentTypeRegistry content, IFormatterRegistry formatters,
			IResourceManagerRegistry resources,
			MediaProviderConfiguration[] configurations,
			MediaProviderBindingConfiguration[] bindings)
	{
		Map mediaProviders = new HashMap(configurations.length);
		Map contentTypes = new HashMap();
		for (int i = 0; i < configurations.length; ++i)
			mediaProviders.put(configurations[i].getID(), new MediaProvider(context,
					content, formatters, resources, configurations[i], contentTypes));
		this.mediaProviders = Collections.unmodifiableMap(mediaProviders);
		Map mediaProviderIndex = new HashMap(bindings.length);
		for (int i = 0; i < bindings.length; ++i)
			mediaProviderIndex.put(bindings[i].getKey(), bindings[i]
					.getMediaProviderID());
		this.mediaProviderIndex = Collections.unmodifiableMap(mediaProviderIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.
	 *      IMediaProviderRegistry#getMediaProviderIDs()
	 */
	public String[] getMediaProviderIDs()
	{
		return (String[])mediaProviders.keySet().toArray(
				new String[mediaProviders.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.
	 *      IMediaProviderRegistry#getMediaProvider(java.lang.String)
	 */
	public IMediaProvider getMediaProvider(String mediaProviderID)
	{
		return (IMediaProvider)mediaProviders.get(mediaProviderID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.
	 *      IMediaProviderRegistry#lookupMediaProviderID(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public String lookupMediaProviderID(String brandID,
			String interactionTypeID, String langugageID)
	{
		return (String)mediaProviderIndex.get(brandID + ":" + interactionTypeID + ":" 
				+ langugageID);
	}

	/**
	 * Implementation of {@link IMediaProvider}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class MediaProvider implements IMediaProvider, IFormatManager,
			ISharedContentProvider
	{
		/** The supported content types. */
		private final List contentTypes;
		/** The formatter instance. */
		private final IFormatter formatter;
		/** The resource manager instance. */
		private final IResourceManager resourceManager;
		/** The supported content types. */
		private final Map sharedContent;

		/**
		 * Creates a new MediaProvider.
		 * 
		 * @param context The context to operate in.
		 * @param content The content manager.
		 * @param formatters The formatters.
		 * @param resources The resource managers.
		 * @param configuration The configuration for this item.
		 * @param cache The cache of embedded types.
		 */
		MediaProvider(IProcessContext context, IContentTypeRegistry content,
				IFormatterRegistry formatters, IResourceManagerRegistry resources,
				MediaProviderConfiguration configuration, Map cache)
		{
			List contentTypes = new ArrayList(Arrays.asList(content
					.getContentTypeIDs()));
			for (int i = 0; i < contentTypes.size(); ++i)
				contentTypes
						.set(i, content.getContentType((String)contentTypes.get(i)));
			this.contentTypes = Collections.unmodifiableList(contentTypes);
			this.formatter = formatters.getFormatter(configuration.getFormatterID());
			this.resourceManager = resources.getResourceManager(configuration
					.getResourceManagerID());
			SharedContentConfiguration[] contentConfigurations = configuration
					.getSharedContent();
			Map sharedContent = new HashMap(contentConfigurations.length);
			for (int i = 0; i < contentConfigurations.length; ++i)
				sharedContent.put(contentConfigurations[i].getName(),
						contentConfigurations[i].getContent().createCopy());
			this.sharedContent = Collections.unmodifiableMap(sharedContent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.media.IMediaProvider#
		 *      getSupportedContentTypes()
		 */
		public List getSupportedContentTypes()
		{
			return contentTypes;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.media.IMediaProvider#
		 *      getFormatManager()
		 */
		public IFormatManager getFormatManager()
		{
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.media.IMediaProvider#
		 *      getFormatter()
		 */
		public IFormatter getFormatter()
		{
			return formatter;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.media.IMediaProvider#
		 *      getResourceManager()
		 */
		public IResourceManager getResourceManager()
		{
			return resourceManager;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.media.IMediaProvider#
		 *      hasSharedContent()
		 */
		public boolean hasSharedContent()
		{
			return !sharedContent.isEmpty();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.media.IMediaProvider#
		 *      getSharedContentProvider()
		 */
		public ISharedContentProvider getSharedContentProvider()
		{
			return sharedContent.isEmpty() ? null : this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatManager#
		 *      getFormat(org.eclipse.vtp.framework.interactions.core.media.FormattableContent,
		 *      java.lang.String)
		 */
		public String getFormat(FormattableContent content, String formatName)
		{
			return formatter.getDefaultFormatDefintion(content, formatName);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatManager#
		 *      getFormats( org.eclipse.vtp.framework.interactions.core.media.
		 *      FormattableContent)
		 */
		public List getFormats(FormattableContent content)
		{
			return formatter.getDefaultFormats(content);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.media.
		 *      ISharedContentProvider#listSharedContent()
		 */
		public List listSharedContent()
		{
			return new ArrayList(sharedContent.keySet());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.media.
		 *      ISharedContentProvider#getSharedContent(java.lang.String)
		 */
		public Content getSharedContent(String contentName)
		{
			Content content = (Content)sharedContent.get(contentName);
			if (content == null)
				return null;
			return content.createCopy();
		}
	}

}
