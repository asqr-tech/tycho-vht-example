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
package org.eclipse.vtp.desktop.model.interactive.core.content;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.IContentFactory;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;

public class ContentLoadingManager implements IContentFactory
{
	public static final String contentTypeExtensionId = "org.eclipse.vtp.framework.interactions.core.contenttypes";
	private static final ContentLoadingManager instance = new ContentLoadingManager();

	public static ContentLoadingManager getInstance()
	{
		return instance;
	}

	private Map<String, ContentRegistration> contentTypes = new HashMap<String, ContentRegistration>();
	private Map<String, ContentRegistration> contentTypesById = new HashMap<String, ContentRegistration>();

	public ContentLoadingManager()
	{
		super();
		IConfigurationElement[] primitiveExtensions = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						contentTypeExtensionId);
		for (int i = 0; i < primitiveExtensions.length; i++)
		{
			String contentElementURI = primitiveExtensions[i]
					.getAttribute("element-uri");
			String contentElementName = primitiveExtensions[i]
					.getAttribute("element-name");
			String contentClassName = primitiveExtensions[i].getAttribute("class");
			if (contentElementURI == null || contentElementName == null
					|| contentClassName == null)
				continue;
			Bundle contributor = Platform.getBundle(primitiveExtensions[i]
					.getContributor().getName());
			try
			{
				@SuppressWarnings("unchecked")
				Class<Content> providerClass = (Class<Content>) contributor.loadClass(contentClassName);
				ContentRegistration reg = new ContentRegistration();
				reg.id = primitiveExtensions[i].getAttribute("id");
				reg.contentElementURI = contentElementURI;
				reg.contentElementName = contentElementName;
				reg.contentClass = providerClass;
				contentTypes.put(contentElementURI + contentElementName, reg);
				contentTypesById.put(reg.id, reg);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}

	public Content loadContent(Element contentElement)
	{
		try
		{
			String uri = contentElement.getNamespaceURI();
			String name = contentElement.getTagName();
			ContentRegistration reg = contentTypes.get(uri + name);
			if (reg == null)
				return null;
			try
			{
				return reg.contentClass.getConstructor(
						new Class[] { IContentFactory.class, Element.class }).newInstance(
						new Object[] { this, contentElement });
			}
			catch (NoSuchMethodException e)
			{
				return reg.contentClass.getConstructor(
						new Class[] { Element.class }).newInstance(
						new Object[] { contentElement });
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private class ContentRegistration
	{
		String id;
		@SuppressWarnings("unused")
		//TODO review these for removal
		String contentElementURI;
		@SuppressWarnings("unused")
		String contentElementName;
		Class<Content> contentClass;
	}
}
