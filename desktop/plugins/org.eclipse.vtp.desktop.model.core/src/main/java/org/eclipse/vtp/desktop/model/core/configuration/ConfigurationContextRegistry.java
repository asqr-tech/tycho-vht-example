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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.osgi.framework.Bundle;

public class ConfigurationContextRegistry
{
	public static final String configurationManagerExtensionId = "org.eclipse.vtp.desktop.model.core.configurationContexts";
	private static final ConfigurationContextRegistry instance = new ConfigurationContextRegistry();
	
	public static ConfigurationContextRegistry getInstance()
	{
		return instance;
	}

	private List<ContextRecord> managerRecords = new ArrayList<ContextRecord>();
	
	public ConfigurationContextRegistry()
	{
		super();
		Map<String, ContextRecord> recordsById = new HashMap<String, ContextRecord>();
		IConfigurationElement[] managerExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(configurationManagerExtensionId);
		//process the context declarations first
		for(int i = 0; i < managerExtensions.length; i++)
		{
			if(managerExtensions[i].getName().equals("context"))
			{
				String managerId = managerExtensions[i].getAttribute("id");
				String className = managerExtensions[i].getAttribute("class");
				Bundle contributor = Platform.getBundle(managerExtensions[i].getContributor().getName());
				try
				{
					@SuppressWarnings("unchecked")
					Class<ConfigurationContext> managerClass = (Class<ConfigurationContext>)contributor.loadClass(className);
					ContextRecord cmr = new ContextRecord(managerId, managerClass);
					managerRecords.add(cmr);
					recordsById.put(managerId, cmr);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					continue;
				}
			}
		}
		for(int i = 0; i < managerExtensions.length; i++)
		{
			if(managerExtensions[i].getName().equals("context-binding"))
			{
				String contextId = managerExtensions[i].getAttribute("context-id");
				String className = managerExtensions[i].getAttribute("filter-class");
				Bundle contributor = Platform.getBundle(managerExtensions[i].getContributor().getName());
				try
				{
					@SuppressWarnings("rawtypes")
					Class filterClass = contributor.loadClass(className);
					ContextRecord cmr = recordsById.get(contextId);
					if(cmr != null)
					{
						cmr.addFilter(filterClass);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	public List<ConfigurationContext> getConfigurationContextsFor(IWorkflowProject project)
	{
		List<ConfigurationContext> ret = new ArrayList<ConfigurationContext>();
		for(ContextRecord cr : managerRecords)
		{
			if(cr.isApplicableTo(project))
			{
				ConfigurationContext context = cr.getManagerInstance();
				context.setProject(project);
				ret.add(context);
			}
		}
		return ret;
	}
	
	private class ContextRecord
	{
		@SuppressWarnings("unused")
		private String contextId;
		private Class<ConfigurationContext> managerClass;
		@SuppressWarnings("rawtypes")
		private List<Class> adaptableFilters = new ArrayList<Class>();
		
		public ContextRecord(String managerId, Class<ConfigurationContext> managerClass) throws Exception
		{
			super();
			this.contextId = managerId;
			this.managerClass = managerClass;
		}
		
		public void addFilter(@SuppressWarnings("rawtypes") Class filter)
		{
			adaptableFilters.add(filter);
		}
		
		public boolean isApplicableTo(IWorkflowProject project)
		{		
			for(@SuppressWarnings("rawtypes") Class c : adaptableFilters)
			{
				if(project.getAdapter(c) != null)
					return true;
			}
			return false;
		}

		public ConfigurationContext getManagerInstance()
		{
			try
			{
				return managerClass.newInstance();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return null;
		}
	}
}
