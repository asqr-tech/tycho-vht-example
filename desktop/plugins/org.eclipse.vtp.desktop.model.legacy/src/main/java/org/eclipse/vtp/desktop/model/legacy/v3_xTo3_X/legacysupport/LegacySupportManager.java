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
package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.legacysupport;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.XMLConverter;
import org.osgi.framework.Bundle;


public class LegacySupportManager
{
	public static final String modelConvertsExtensionId = "org.eclipse.vtp.desktop.model.legacy.modelConverters";
	public static final String elementConvertsExtensionId = "org.eclipse.vtp.desktop.model.legacy.legacyElementConverters";
	public static final String configurationManagerConvertersExtensionId = "org.eclipse.vtp.desktop.model.legacy.legacyConfigurationManagerConverters";
	private static final LegacySupportManager INSTANCE = new LegacySupportManager();
	
	public static LegacySupportManager getInstance()
	{
		return INSTANCE;
	}
	
	private Map<String, ModelConverter> modelConverters;
	private Map<String, LegacyElementConverter> elementConverters;
	private Map<String, LegacyConfigurationManagerConverter> configurationManagerConverters;

	@SuppressWarnings("unchecked")
	public LegacySupportManager()
	{
		super();
		modelConverters = new HashMap<String, ModelConverter>();
		IConfigurationElement[] modelConverterExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(modelConvertsExtensionId);
		for(int i = 0; i < modelConverterExtensions.length; i++)
		{
			ModelConverter mc = new ModelConverter();
			mc.modelVersion = modelConverterExtensions[i].getAttribute("xml-version");
			String className = modelConverterExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(modelConverterExtensions[i].getContributor().getName());
			try
			{
				mc.converterClass = (Class<XMLConverter>) contributor.loadClass(className);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
			modelConverters.put(mc.modelVersion, mc);
		}
		elementConverters = new HashMap<String, LegacyElementConverter>();
		IConfigurationElement[] elementConverterExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(elementConvertsExtensionId);
		for(int i = 0; i < elementConverterExtensions.length; i++)
		{
			LegacyElementConverter lec = new LegacyElementConverter();
			lec.tagName = elementConverterExtensions[i].getAttribute("xml-element");
			lec.namespace = elementConverterExtensions[i].getAttribute("xml-namespace");
			if(lec.namespace == null)
				lec.namespace = "";
			String className = elementConverterExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(elementConverterExtensions[i].getContributor().getName());
			try
			{
				lec.converterClass = (Class<XMLConverter>) contributor.loadClass(className);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
			elementConverters.put(lec.namespace + lec.tagName, lec);
		}
		configurationManagerConverters = new HashMap<String, LegacyConfigurationManagerConverter>();
		IConfigurationElement[] configurationManagerConverterExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(configurationManagerConvertersExtensionId);
		for(int i = 0; i < configurationManagerConverterExtensions.length; i++)
		{
			LegacyConfigurationManagerConverter lec = new LegacyConfigurationManagerConverter();
			lec.tagName = configurationManagerConverterExtensions[i].getAttribute("xml-element");
			lec.namespace = configurationManagerConverterExtensions[i].getAttribute("xml-namespace");
			if(lec.namespace == null)
				lec.namespace = "";
			String className = configurationManagerConverterExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(configurationManagerConverterExtensions[i].getContributor().getName());
			try
			{
				lec.converterClass = (Class<XMLConverter>) contributor.loadClass(className);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
			configurationManagerConverters.put(lec.namespace + lec.tagName, lec);
		}
	}
	
	/**
	 * @param xmlVersion
	 * @return
	 */
	public XMLConverter getModelConverter(String xmlVersion)
	{
		ModelConverter modelConverter = modelConverters.get(xmlVersion);
		if(modelConverter != null)
		{
			try
			{
				return modelConverter.converterClass.newInstance();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * @param tagName
	 * @param namespace
	 * @return
	 */
	public XMLConverter getLegacyElementConverter(String tagName, String namespace)
	{
		String key = "";
		if(namespace != null)
			key = namespace;
		key += tagName;
		LegacyElementConverter legacyElementConverter = elementConverters.get(key);
		if(legacyElementConverter != null)
		{
			try
			{
				return legacyElementConverter.converterClass.newInstance();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * @param tagName
	 * @param namespace
	 * @return
	 */
	public XMLConverter getLegacyConfigurationManagerConverter(String tagName, String namespace)
	{
		String key = "";
		if(namespace != null)
			key = namespace;
		key += tagName;
		LegacyConfigurationManagerConverter legacyElementConverter = configurationManagerConverters.get(key);
		if(legacyElementConverter != null)
		{
			try
			{
				return legacyElementConverter.converterClass.newInstance();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private class ModelConverter
	{
		String modelVersion;
		Class<XMLConverter> converterClass;
	}
	
	private class LegacyElementConverter
	{
		String tagName;
		String namespace;
		Class<XMLConverter> converterClass;
	}

	private class LegacyConfigurationManagerConverter
	{
		String tagName;
		String namespace;
		Class<XMLConverter> converterClass;
	}
}
