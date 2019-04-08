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

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.IContentFactory;
import org.eclipse.vtp.framework.interactions.core.media.IContentType;
import org.eclipse.vtp.framework.interactions.core.media.IContentTypeRegistry;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;

/**
 * Implementation of {@link IContentTypeRegistry} and {@link IContentFactory}.
 * 
 * @author Lonnie Pryor
 */
public class ContentManager implements IContentTypeRegistry, IContentFactory
{
	/** The content types. */
	private final Map contentTypes;
	/** The content type index. */
	private final Map contentTypeIndex;

	/**
	 * Creates a new ContentManager.
	 * 
	 * @param registry The extension registry to load from.
	 */
	public ContentManager(IExtensionRegistry registry)
	{
		IExtensionPoint point = registry.getExtensionPoint(//
				"org.eclipse.vtp.framework.interactions.core.contenttypes"); //$NON-NLS-1$
		IExtension[] extensions = point.getExtensions();
		Map contentTypes = new HashMap(extensions.length);
		Map contentTypeIndex = new HashMap(extensions.length);
		for (int i = 0; i < extensions.length; ++i)
		{
			Bundle bundle = Platform.getBundle(extensions[i].getContributor()
					.getName());
			IConfigurationElement[] elements = extensions[i]
					.getConfigurationElements();
			for (int j = 0; j < elements.length; ++j)
			{
				try
				{
					ContentType contentType = new ContentType(elements[j]
							.getAttribute("id"), //$NON-NLS-1$
							elements[j].getAttribute("class"), //$NON-NLS-1$
							bundle.loadClass(elements[j].getAttribute("class"))); //$NON-NLS-1$
					contentTypes.put(contentType.getId(), contentType);
					contentTypeIndex.put(elements[j].getAttribute("element-name") + //$NON-NLS-1$
							elements[j].getAttribute("element-uri"), contentType); //$NON-NLS-1$
				}
				catch (Exception e)
				{
					e.printStackTrace();
					continue;
				}
			}
		}
		this.contentTypes = Collections.unmodifiableMap(contentTypes);
		this.contentTypeIndex = Collections.unmodifiableMap(contentTypeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.
	 *      IContentTypeRegistry#getContentTypeIDs()
	 */
	public String[] getContentTypeIDs()
	{
		return (String[])contentTypes.keySet().toArray(
				new String[contentTypes.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.
	 *      IContentTypeRegistry#getContentType(java.lang.String)
	 */
	public IContentType getContentType(String contentTypeID)
	{
		return (IContentType)contentTypes.get(contentTypeID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.IContentFactory#
	 *      loadContent(org.w3c.dom.Element)
	 */
	public Content loadContent(Element configuration)
	{
		if (configuration == null)
			return null;
		ContentType contentType = (ContentType)contentTypeIndex.get(configuration
				.getLocalName()
				+ configuration.getNamespaceURI());
		if (contentType == null)
			return null;
		return contentType.newInstance(configuration);
	}

	/**
	 * Implementation of {@link IContentType}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ContentType implements IContentType
	{
		/** The ID of this content type. */
		private final String id;
		/** The name of this content type. */
		private final String name;
		/** The factory constructor to use. */
		private final Constructor constructor;

		/**
		 * Creates a new ContentType.
		 * 
		 * @param id The ID of this content type.
		 * @param name The name of this content type.
		 * @param contentClass The implementation type.
		 */
		ContentType(String id, String name, Class contentClass)
		{
			this.id = id;
			Constructor constructor = null;
			this.name = name;
			try
			{
				constructor = contentClass.getConstructor(new Class[] {
						IContentFactory.class, Element.class });
			}
			catch (NoSuchMethodException e)
			{
				try
				{
					constructor = contentClass
							.getConstructor(new Class[] { Element.class });
				}
				catch (NoSuchMethodException ex)
				{
					throw new IllegalStateException(ex);
				}
			}
			this.constructor = constructor;
		}

		/**
		 * Creates a new instance of this content type.
		 * 
		 * @param configuration The configuration to read.
		 * @return A new instance of this content type.
		 */
		Content newInstance(Element configuration)
		{
			try
			{
				if (constructor.getParameterTypes().length == 1)
					return (Content)constructor
							.newInstance(new Object[] { configuration });
				else
					return (Content)constructor.newInstance(new Object[] {
							ContentManager.this, configuration });
			}
			catch (Exception e)
			{
				throw new IllegalStateException(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.media.IContentType#
		 *      getId()
		 */
		public String getId()
		{
			return id;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.media.IContentType#
		 *      getName()
		 */
		public String getName()
		{
			return name;
		}
	}
}
