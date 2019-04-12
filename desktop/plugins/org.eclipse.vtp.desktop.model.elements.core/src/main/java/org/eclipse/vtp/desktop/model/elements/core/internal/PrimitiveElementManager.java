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
package org.eclipse.vtp.desktop.model.elements.core.internal;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.views.PalletItemFilter;
import org.osgi.framework.Bundle;

public class PrimitiveElementManager
{
	public static String primitiveExtensionPointId = "org.eclipse.vtp.desktop.model.elements.core.primitiveElement";
	private static PrimitiveElementManager instance = new PrimitiveElementManager();
	
	public static PrimitiveElementManager getDefault()
	{
		return instance;
	}
	
	private Map<String, PrimitiveElementTemplate> primitiveTypes;
	
	public PrimitiveElementManager()
	{
		super();
		primitiveTypes = new HashMap<String, PrimitiveElementTemplate>();
		IConfigurationElement[] primitiveExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(primitiveExtensionPointId);
		for(int i = 0; i < primitiveExtensions.length; i++)
		{
			PrimitiveElementTemplate template = null;
			String id = primitiveExtensions[i].getAttribute("id");
			String name = primitiveExtensions[i].getAttribute("name");
			String iconPath = primitiveExtensions[i].getAttribute("icon");

			//TODO Review this unique name section
			@SuppressWarnings("unused")
			boolean uniqueName = false;
			String uniqueNameString = primitiveExtensions[i]
					.getAttribute("unique-name");
			if (uniqueNameString != null)
			{
				uniqueName = Boolean.parseBoolean(uniqueNameString);
			}

			Bundle contributor = Platform.getBundle(primitiveExtensions[i].getContributor().getName());
			PalletItemFilter filter = null;
			String filterName = primitiveExtensions[i].getAttribute("filter");
			if (filterName != null)
			{
				try
				{
					@SuppressWarnings("unchecked")
					Class<PalletItemFilter> filterClass = (Class<PalletItemFilter>)contributor.loadClass(filterName);
					filter = filterClass.newInstance();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			if(iconPath != null)
				org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry().put(id, ImageDescriptor.createFromURL(contributor.getEntry(iconPath)));
			IConfigurationElement[] scriptElements = primitiveExtensions[i].getChildren("information_script");
			if(scriptElements.length < 1) //not a scripted element, check for implementing class
			{
				scriptElements = primitiveExtensions[i].getChildren("information_provider");
				if(scriptElements.length < 1) //not a valid primitive element declaration, skip it
					continue;
				String className = scriptElements[0].getAttribute("class");
				try
				{
					@SuppressWarnings("unchecked")
					Class<PrimitiveInformationProvider> providerClass = (Class<PrimitiveInformationProvider>)contributor.loadClass(className);
					template = new ImplementedClassTemplate(id, name, filter, providerClass);
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					continue;
				}
			}
			else //scripted element
			{
				template = new ScriptedTemplate(id, name, filter, scriptElements[0]);
			}
			primitiveTypes.put(template.getId(), template);
		}
	}
	
	public PrimitiveElementTemplate getElementTemplate(String typeId)
	{
		return primitiveTypes.get(typeId);
	}

	public abstract class PrimitiveElementTemplate
	{
		private String id;
		private String name;
		private PalletItemFilter filter = null;
		
		public PrimitiveElementTemplate(String id, String name, PalletItemFilter filter)
		{
			super();
			this.id = id;
			this.name = name;
			this.filter = filter;
		}
		
		public String getId()
		{
			return id;
		}
		
		public String getName()
		{
			return name;
		}
		
		public PalletItemFilter getFilter()
		{
			return filter;
		}
		
		public abstract PrimitiveInformationProvider getInformationProviderInstance(PrimitiveElement element);
	}
	
	public class ScriptedTemplate extends PrimitiveElementTemplate
	{
		private IConfigurationElement scriptElement;
		
		public ScriptedTemplate(String id, String name, PalletItemFilter filter, IConfigurationElement scriptElement)
		{
			super(id, name, filter);
			this.scriptElement = scriptElement;
		}

		public PrimitiveInformationProvider getInformationProviderInstance(PrimitiveElement element)
		{
			ScriptedPrimitiveInformationProvider ret = null;
			if(scriptElement.getAttribute("securable") == null || !Boolean.parseBoolean(scriptElement.getAttribute("securable")))
				ret = new ScriptedPrimitiveInformationProvider(element);
			else
				ret = new SecurableScriptedPrimitiveInformationProvider(element);
			ret.init(scriptElement);
			return ret;
		}
	}
	
	public class ImplementedClassTemplate extends PrimitiveElementTemplate
	{
		Class<PrimitiveInformationProvider> informationProviderClass;
		
		public ImplementedClassTemplate(String id, String name, PalletItemFilter filter, Class<PrimitiveInformationProvider> informationProviderClass)
		{
			super(id, name, filter);
			this.informationProviderClass = informationProviderClass;
		}

		public PrimitiveInformationProvider getInformationProviderInstance(PrimitiveElement element)
		{
			try
			{
				Constructor<PrimitiveInformationProvider> con = informationProviderClass.getConstructor(new Class[] {PrimitiveElement.class});
				return con.newInstance(new Object[] {element});
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
