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
package org.eclipse.vtp.desktop.editors.core.configuration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.osgi.framework.Bundle;

public class ComponentPropertiesPanelProviderRegistry
{
	public static String PANEL_PROVIDER_EXTENSION_ID = "org.eclipse.vtp.desktop.editors.core.componentPropertiesPanelProvider";
	private static ComponentPropertiesPanelProviderRegistry instance = new ComponentPropertiesPanelProviderRegistry();
	
	public static ComponentPropertiesPanelProviderRegistry getInstance()
	{
		return instance;
	}
	
	private List<ComponentPropertiesPanelProvider> providers =
		new LinkedList<ComponentPropertiesPanelProvider>();
	
	public ComponentPropertiesPanelProviderRegistry()
	{
		IConfigurationElement[] primitiveExtensions = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						PANEL_PROVIDER_EXTENSION_ID);
		for (int i = 0; i < primitiveExtensions.length; i++)
		{
			String className = primitiveExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(primitiveExtensions[i]
					.getContributor().getName());
			try
			{
				@SuppressWarnings("rawtypes")
				Class providerClass = contributor.loadClass(className);
				if (!ComponentPropertiesPanelProvider.class.isAssignableFrom(providerClass))
					throw new IllegalArgumentException(
							"The provided class is not a ComponentPropertiesPanelProvider: "
									+ providerClass);
				ComponentPropertiesPanelProvider providerObject =
					(ComponentPropertiesPanelProvider)providerClass.newInstance();
				providers.add(providerObject);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public boolean hasPropertiesPanels(IDesignComponent component)
	{
		for(ComponentPropertiesPanelProvider provider : providers)
		{
			if(provider.isApplicableFor(component))
			{
				return true;
			}
		}
		return false;
	}
	
	public List<ComponentPropertiesPanel> getPropertiesPanels(IDesignComponent component)
	{
		List<ComponentPropertiesPanel> ret = new ArrayList<ComponentPropertiesPanel>();
		for(ComponentPropertiesPanelProvider provider : providers)
		{
			if(provider.isApplicableFor(component))
			{
				List<ComponentPropertiesPanel> ps = provider.getPropertiesPanels(component);
				for(ComponentPropertiesPanel cpp : ps)
				{
					boolean inserted = false;
					for(int i = 0; i < ret.size(); i++)
					{
						if(cpp.getRanking() < ret.get(i).getRanking())
						{
							ret.add(i, cpp);
							inserted = true;
							break;
						}
					}
					if(!inserted)
						ret.add(cpp);
				}
			}
		}
		return ret;
	}
}
