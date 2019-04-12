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
package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.update;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.XMLConverter;
import org.osgi.framework.Bundle;

public class UpgradeSupportManager
{
	public static final String elementConvertsExtensionId = "org.eclipse.vtp.desktop.model.legacy.elementConverters";
	public static final String configurationManagerConvertersExtensionId = "org.eclipse.vtp.desktop.model.legacy.configurationManagerConverters";
	private static final UpgradeSupportManager INSTANCE = new UpgradeSupportManager();
	
	/**
	 * @return
	 */
	public static UpgradeSupportManager getInstance()
	{
		return INSTANCE;
	}
	
	private Map<String, ElementConverter> elementConverters;
	private Map<String, ConfigurationManagerConverter> configurationManagerConverters;

	public UpgradeSupportManager()
	{
		super();
		elementConverters = new HashMap<String, ElementConverter>();
		IConfigurationElement[] elementConverterExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(elementConvertsExtensionId);
		for(int i = 0; i < elementConverterExtensions.length; i++)
		{
			String typeId = elementConverterExtensions[i].getAttribute("type");
			String version = elementConverterExtensions[i].getAttribute("version");
			String className = elementConverterExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(elementConverterExtensions[i].getContributor().getName());
			try
			{
				@SuppressWarnings("unchecked")
				Class<XMLConverter> converterClass = (Class<XMLConverter>) contributor.loadClass(className);
				ElementConverter lec = new ElementConverter(typeId, version, converterClass);
				elementConverters.put(lec.typeId + lec.version, lec);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
		}
		configurationManagerConverters = new HashMap<String, ConfigurationManagerConverter>();
		IConfigurationElement[] configurationManagerConverterExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(configurationManagerConvertersExtensionId);
		for(int i = 0; i < configurationManagerConverterExtensions.length; i++)
		{
			String typeId = configurationManagerConverterExtensions[i].getAttribute("type");
			String version = configurationManagerConverterExtensions[i].getAttribute("version");
			String className = configurationManagerConverterExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(configurationManagerConverterExtensions[i].getContributor().getName());
			try
			{
				@SuppressWarnings("unchecked")
				Class<XMLConverter> converterClass = (Class<XMLConverter>) contributor.loadClass(className);
				ConfigurationManagerConverter lec = new ConfigurationManagerConverter(typeId, version, converterClass);
				configurationManagerConverters.put(lec.typeId + lec.version, lec);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}
	
	/**
	 * @param type
	 * @param version
	 * @param dataServices
	 * @return
	 */
	public XMLConverter getLegacyElementConverter(String type, String version, List<Object> dataServices)
	{
		String key = type + version;
		ElementConverter elementConverter = elementConverters.get(key);
		if(elementConverter != null)
		{
			return getConverterInstance(elementConverter.constructors, dataServices);
		}
		return null;
	}
	
	/**
	 * @param type
	 * @param version
	 * @param dataServices
	 * @return
	 */
	public XMLConverter getLegacyConfigurationManagerConverter(String type, String version, List<Object> dataServices)
	{
		String key = type + version;
		ConfigurationManagerConverter configurationManagerConverter = configurationManagerConverters.get(key);
		if(configurationManagerConverter != null)
		{
			return getConverterInstance(configurationManagerConverter.constructors, dataServices);
		}
		return null;
	}
	
	/**
	 * @param constructors
	 * @param dataServices
	 * @return
	 */
	public XMLConverter getConverterInstance(Constructor<?>[] constructors, List<Object> dataServices)
	{
		try
		{
outerfor:		for(int i = 0; i < constructors.length; i++)
			{
				Class<?>[] paramClasses = constructors[i].getParameterTypes();
				Object[] params = new Object[paramClasses.length];
				for(int c = 0; c < paramClasses.length; c++)
				{
					if(paramClasses[c].isArray())
					{
						Class<?> arrayClass = paramClasses[c].getComponentType();
						List<Object> paramValueList = new LinkedList<Object>();
						for(int s = 0; s < dataServices.size(); s++)
						{
							Object dataService = dataServices.get(s);
							if(arrayClass.isAssignableFrom(dataService.getClass()))
							{
								paramValueList.add(dataService);
							}
						}
						params[c] = paramValueList.toArray((Object[])Array.newInstance(arrayClass, paramValueList.size()));
					}
					else
					{
						for(int s = 0; s < dataServices.size(); s++)
						{
							Object dataService = dataServices.get(s);
							if(paramClasses[c].isAssignableFrom(dataService.getClass()))
							{
								params[c] = dataService;
								break;
							}
						}
						if(params[c] == null) //parameter not found
						{
							continue outerfor; //try next constructor
						}
					}
				}
				//if we have made it here, params is filled with injection values
				return (XMLConverter)constructors[i].newInstance(params);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	private class ElementConverter
	{
		String typeId;
		String version;
		Class<XMLConverter> converterClass;
		Constructor<?>[] constructors = null;
		
		/**
		 * @param typeId
		 * @param version
		 * @param converterClass
		 */
		ElementConverter(String typeId, String version, Class<XMLConverter> converterClass)
		{
			super();
			this.typeId = typeId;
			this.version = version;
			this.converterClass = converterClass;
			constructors = this.converterClass.getConstructors();
			for(int i = constructors.length - 1; i >= 0; i--)
			{
				for(int j = 1; j <= i; j++)
				{
					if(constructors[j-1].getParameterTypes().length < constructors[j].getParameterTypes().length)
					{
						Constructor<?> temp = constructors[j];
						constructors[j] = constructors[j-1];
						constructors[j-1] = temp;
					}
				}
			}
		}
		
	}

	private class ConfigurationManagerConverter
	{
		String typeId;
		String version;
		Class<XMLConverter> converterClass;
		Constructor<?>[] constructors = null;
		
		/**
		 * @param typeId
		 * @param version
		 * @param converterClass
		 */
		ConfigurationManagerConverter(String typeId, String version, Class<XMLConverter> converterClass)
		{
			super();
			this.typeId = typeId;
			this.version = version;
			this.converterClass = converterClass;
			constructors = this.converterClass.getConstructors();
			for(int i = constructors.length - 1; i >= 0; i--)
			{
				for(int j = 1; j <= i; j++)
				{
					if(constructors[j-1].getParameterTypes().length < constructors[j].getParameterTypes().length)
					{
						Constructor<?> temp = constructors[j];
						constructors[j] = constructors[j-1];
						constructors[j-1] = temp;
					}
				}
			}
		}
	}
}
