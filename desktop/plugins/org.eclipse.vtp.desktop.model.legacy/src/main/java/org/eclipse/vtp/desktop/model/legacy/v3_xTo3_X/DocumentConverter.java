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
package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.legacysupport.ConversionException;
import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.legacysupport.LegacySupportManager;
import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.update.UpgradeSupportManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class DocumentConverter
{
	public static final String XML_VERSION = "1.0.1";
	private Map<String, Object> dataServices = new HashMap<String, Object>();
	
	public DocumentConverter()
	{
		super();
	}
	
	public void convertDocument(Document document) throws Exception
	{
		Element rootElement = document.getDocumentElement();
		NodeList nl = rootElement.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++)
		{
			if(nl.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element element = (Element)nl.item(i);
			if(element.getTagName().equals("dialogs"))
			{
				NodeList dialogList = element.getElementsByTagName("dialogs");
				for(int d = 0; d < dialogList.getLength(); d++)
				{
					Element dialogElement = (Element)dialogList.item(d);
					NodeList modelList = dialogElement.getElementsByTagName("model");
					if(modelList.getLength() > 0)
					{
						Element modelElement = (Element)modelList.item(0);
						convertModel(modelElement);
					}
				}
			}
			else //application or fragment element
			{
				NodeList modelList = element.getElementsByTagName("model");
				if(modelList.getLength() > 0)
				{
					Element modelElement = (Element)modelList.item(0);
					convertModel(modelElement);
				}
			}
		}
	}
	
	/**
	 * @param modelElement
	 * @param resolutionVisitor
	 * @return
	 * @throws Exception
	 */
	public boolean convertModel(org.w3c.dom.Element modelElement) throws Exception
	{
		boolean neededConversion = false;
		//verify XML version and convert if necessary
		String modelXMLVersion = modelElement.getAttribute("xml-version");
		while(!XML_VERSION.equals(modelXMLVersion)) //model version is incorrect
		{
			neededConversion = true;
			System.err.println("Model XML version mismatch: expected=\"" + XML_VERSION + "\" found=\"" + modelXMLVersion + "\"");
			if(modelXMLVersion.equals("")) //attribute missing
			{
				System.err.println("Defaulting XML version to \"0.0.0\"");
				modelXMLVersion = "0.0.0";
			}
			XMLConverter converter = LegacySupportManager.getInstance().getModelConverter(modelXMLVersion);
			if(converter == null)
			{
				System.err.println("Could not locate model converter...throwing Exception.");
				throw new Exception("Could not convert model xml to new format.");
			}
			System.err.println("Found model converter: " + converter);
			System.err.println("Starting conversion process...");
			converter.convert(modelElement);
			System.err.println("Model conversion complete.");
			String tempVersion = modelElement.getAttribute("xml-version");
			if(tempVersion.equals(modelXMLVersion)) //avoid infinite loop caused by bad converters
			{
				System.err.println("Conversion process failed to update xml version.");
				System.err.println("Avoiding infinite loop by throwing exception.");
				throw new Exception("Conversion process failed to update xml version.");
			}
			System.err.println("Model converted to version: \"" + tempVersion + "\"");
			modelXMLVersion = tempVersion;
		}
		NodeList elementsElementList = modelElement.getElementsByTagName("elements");
		if(elementsElementList.getLength() > 0)
		{
			org.w3c.dom.Element elementsElement = (org.w3c.dom.Element)elementsElementList.item(0);
			NodeList elementList = elementsElement.getElementsByTagName("element");
			for(int i = 0; i < elementList.getLength(); i++)
			{
				org.w3c.dom.Element elementElement = (org.w3c.dom.Element)elementList.item(i);
				Properties properties = new Properties();
				NodeList propertiesList = elementElement.getElementsByTagName("properties");
				if(propertiesList.getLength() > 0)
				{
					org.w3c.dom.Element propertiesElement = (org.w3c.dom.Element)propertiesList.item(0);
					NodeList propertyList = propertiesElement.getElementsByTagName("property");
					for(int p = 0; p < propertyList.getLength(); p++)
					{
						org.w3c.dom.Element propertyElement = (org.w3c.dom.Element)propertyList.item(p);
						properties.setProperty(propertyElement.getAttribute("name"), propertyElement.getAttribute("value"));
					}
				}
				neededConversion |= convertElementConfiguration(elementElement);
			}
		}
		return neededConversion;
	}
	
	public final boolean convertElementConfiguration(org.w3c.dom.Element elementElement)
	{
		boolean neededConversion = false;
		NodeList configurationList = elementElement.getElementsByTagName("configuration");
		if(configurationList.getLength() > 0)
		{
			org.w3c.dom.Element configurationElement = (org.w3c.dom.Element)configurationList.item(0);
			NodeList managedConfigSectionList = configurationElement.getElementsByTagName("managed-config");
			boolean localNeededConversion = false;
			do
			{
				localNeededConversion = false;
				for(int c = 0; c < managedConfigSectionList.getLength(); c++)
				{
					org.w3c.dom.Element configSectionElement = (org.w3c.dom.Element)managedConfigSectionList.item(c);
					String managerType = configSectionElement.getAttribute("type");
					String xmlVersion = configSectionElement.getAttribute("xml-version");
					if(xmlVersion.equals(""))
						xmlVersion = "0.0.0";
					XMLConverter converter = UpgradeSupportManager.getInstance().getLegacyConfigurationManagerConverter(managerType, xmlVersion, getDataServices());
					if(converter != null)
					{
						neededConversion = true;
						localNeededConversion = true;
						try
                        {
	                        converter.convert(configSectionElement);
                        }
                        catch(ConversionException e)
                        {
	                        e.printStackTrace();
                        }
					}
				}
				managedConfigSectionList = configurationElement.getElementsByTagName("managed-config");
			}
			while(localNeededConversion);
		}
		return neededConversion;
	}
	
	/**
	 * @param name
	 * @return
	 */
	public Object getDataService(String name)
	{
		return dataServices.get(name);
	}
	
	/**
	 * @return
	 */
	public List<Object> getDataServices()
	{
		List<Object> ret = new LinkedList<Object>();
		ret.addAll(dataServices.values());
		return ret;
	}
	
	/**
	 * @param name
	 * @param service
	 */
	public void putDataService(String name, Object service)
	{
		dataServices.put(name, service);
	}
	
}
