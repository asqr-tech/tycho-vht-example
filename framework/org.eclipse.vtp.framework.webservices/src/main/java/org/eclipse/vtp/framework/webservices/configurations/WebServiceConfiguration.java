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
package org.eclipse.vtp.framework.webservices.configurations;

import java.util.List;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.eclipse.vtp.framework.webservices.configurations.document.InputDocumentStructure;
import org.w3c.dom.Element;

/**
 * A configuration for a web service connection factory.
 * 
 * @author Lonnie Pryor
 */
public class WebServiceConfiguration implements IConfiguration,
		WebServiceConstants
{
	private String serviceType = SOAP;
	private String urlType = STATIC;
	private String url = null;
	private String soapActionType = STATIC;
	private String soapAction = null;
	
	private InputDocumentStructure inputStructure = new InputDocumentStructure();
	
	private String variableName = "ws_output";
	private boolean process = false;
	private String outputScriptText = null;

	/**
	 * Creates a new DatabaseConfiguration.
	 */
	public WebServiceConfiguration()
	{
	}
	
	public String getServiceType()
	{
		return serviceType;
	}
	
	public void setServiceType(String serviceType)
	{
		this.serviceType = serviceType;
	}
	
	public String getURLType()
	{
		return urlType;
	}
	
	public String getURL()
	{
		return url;
	}
	
	public void setURL(String urlType, String url)
	{
		this.urlType = urlType;
		this.url = url;
	}

	public String getSoapActionType()
	{
		return this.soapActionType;
	}
	
	public String getSoapAction()
	{
		return this.soapAction;
	}
	
	public void setSoapAction(String soapActionType, String soapAction)
	{
		this.soapActionType = soapActionType;
		this.soapAction = soapAction;
	}
	
	public InputDocumentStructure getInputStructure()
	{
		return this.inputStructure;
	}
	
	public String getVariableName()
	{
		return variableName;
	}
	
	public void setVariableName(String variableName)
	{
		this.variableName = variableName;
	}
	
	public boolean shouldProcess()
	{
		return process;
	}
	
	public void setShouldProcess(boolean process)
	{
		this.process = process;
	}
	
	public String getOutputScriptText()
	{
		return this.outputScriptText;
	}
	
	public void setOutputScriptText(String outputScriptText)
	{
		this.outputScriptText = outputScriptText;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(
	 *      org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		List<Element> serviceElementList = XMLUtilities.getElementsByTagName(configurationElement, NAME_SERVICE_INFO, true);
		if(serviceElementList.size() > 0)
		{
			Element serviceElement = serviceElementList.get(0); 
			serviceType = serviceElement.getAttribute(NAME_SERVICE_TYPE);
			soapActionType = serviceElement.getAttribute(NAME_SOAP_ACTION_TYPE);
			soapAction = serviceElement.getAttribute(NAME_SOAP_ACTION);
			urlType = serviceElement.getAttribute(NAME_URL_TYPE);
			url = serviceElement.getAttribute(NAME_URL);
		}
		List<Element> inputStructureElementList = XMLUtilities.getElementsByTagName(configurationElement, NAME_INPUT_STRUCTURE, true);
		if(inputStructureElementList.size() > 0)
		{
			inputStructure.readConfiguration(inputStructureElementList.get(0));
		}
		List<Element> outputElementList = XMLUtilities.getElementsByTagName(configurationElement, NAME_OUTPUT, true);
		if(outputElementList.size() > 0)
		{
			Element outputElement = outputElementList.get(0);
			variableName = outputElement.getAttribute(NAME_VARIABLE);
			process = "true".equals(outputElement.getAttribute(NAME_PROCESS));
			try
			{
				outputScriptText = XMLUtilities.getElementTextData(outputElement, true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(
	 *      org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		Element serviceElement = configurationElement.getOwnerDocument().createElementNS(null, NAME_SERVICE_INFO);
		configurationElement.appendChild(serviceElement);
		serviceElement.setAttribute(NAME_SERVICE_TYPE, serviceType);
		serviceElement.setAttribute(NAME_SOAP_ACTION_TYPE, soapActionType);
		serviceElement.setAttribute(NAME_SOAP_ACTION, soapAction);
		serviceElement.setAttribute(NAME_URL_TYPE, urlType);
		serviceElement.setAttribute(NAME_URL, url);
		Element inputStructureElement = configurationElement.getOwnerDocument().createElementNS(null, NAME_INPUT_STRUCTURE);
		configurationElement.appendChild(inputStructureElement);
		inputStructure.writeConfiguration(inputStructureElement);
		Element outputElement = configurationElement.getOwnerDocument().createElementNS(null, NAME_OUTPUT);
		configurationElement.appendChild(outputElement);
		outputElement.setAttribute(NAME_VARIABLE, variableName);
		outputElement.setAttribute(NAME_PROCESS, process ? "true" : "false");
		outputElement.setTextContent(outputScriptText);
	}
}
