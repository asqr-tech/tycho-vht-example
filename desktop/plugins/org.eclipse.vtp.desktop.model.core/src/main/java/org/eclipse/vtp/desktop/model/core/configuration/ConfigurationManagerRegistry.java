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
package org.eclipse.vtp.desktop.model.core.configuration;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.osgi.framework.Bundle;

public class ConfigurationManagerRegistry
{
	public static final String configurationManagerExtensionId = "org.eclipse.vtp.desktop.model.core.configurationManagers";
	private static final ConfigurationManagerRegistry instance = new ConfigurationManagerRegistry();
	
	public static ConfigurationManagerRegistry getInstance()
	{
		return instance;
	}

	List<ConfigurationManagerRecord> managerRecords = new ArrayList<ConfigurationManagerRecord>();
	
	public ConfigurationManagerRegistry()
	{
		super();
		IConfigurationElement[] managerExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(configurationManagerExtensionId);
		for(int i = 0; i < managerExtensions.length; i++)
		{
			String managerId = managerExtensions[i].getAttribute("id");
			String namespace = managerExtensions[i].getAttribute("xml-namespace");
			String element = managerExtensions[i].getAttribute("xml-tag");
			String className = managerExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(managerExtensions[i].getContributor().getName());
			try
			{
				@SuppressWarnings("unchecked")
				Class<ConfigurationManager> managerClass = (Class<ConfigurationManager>)contributor.loadClass(className);
				ConfigurationManagerRecord cmr = new ConfigurationManagerRecord(managerId, namespace, element, managerClass);
				managerRecords.add(cmr);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}

	public ConfigurationManager getConfigurationManager(IDesign design, String managerId)
	{
		for(int i = 0; i < managerRecords.size(); i++)
		{
			ConfigurationManagerRecord cmr = managerRecords.get(i);
			if(cmr.getManagerId().equals(managerId))
				return cmr.getManagerInstance(design);
		}
		return null;
	}
	
	public String convertLegacyXMLtoId(String namespace, String element)
	{
		for(int i = 0; i < managerRecords.size(); i++)
		{
			ConfigurationManagerRecord cmr = managerRecords.get(i);
			if(element.equals(cmr.getElement()) && namespace.equals(cmr.getNamespace()))
				return cmr.managerId;
		}
		return null;
	}
	
	private class ConfigurationManagerRecord
	{
		private String managerId;
		private String namespace;
		private String element;
		@SuppressWarnings("unused")
		private Class<ConfigurationManager> managerClass;
		private Constructor<ConfigurationManager> constructor = null;
		
		public ConfigurationManagerRecord(String managerId, String namespace, String element, Class<ConfigurationManager> managerClass) throws Exception
		{
			super();
			this.managerId = managerId;
			this.namespace = namespace;
			this.element = element;
			this.managerClass = managerClass;
			constructor = managerClass.getConstructor(IDesign.class);
		}
		
		public String getManagerId()
		{
			return managerId;
		}

		public String getElement()
		{
			return element;
		}

		public String getNamespace()
		{
			return namespace;
		}
		
		public ConfigurationManager getManagerInstance(IDesign design)
		{
			try
			{
				//if we have made it here, params is filled with injection values
				return constructor.newInstance(new Object[] {design});
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return null;
		}
	}
}
