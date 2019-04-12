package org.eclipse.vtp.modules.webservice.ui.configuration;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class WebserviceServiceBinding
{
	private String url = "";
	private String descriptor = null;
	private String service = null;
	private String port = null;
	private String operation = null;
	private String soapAction = null;

	public WebserviceServiceBinding()
	{
		super();
	}
	
	public String getURL()
	{
		return url;
	}
	
	public void setURL(String url)
	{
		this.url = url;
	}
	
	public String getDescriptor()
	{
		return descriptor;
	}
	
	public void setDescriptor(String descriptor)
	{
		this.descriptor = descriptor;
	}
	
	public String getService()
	{
		return service;
	}
	
	public void setService(String service)
	{
		this.service = service;
	}
	
	public String getPort()
	{
		return port;
	}
	
	public void setPort(String port)
	{
		this.port = port;
	}
	
	public String getOperation()
	{
		return operation;
	}
	
	public void setOperation(String operation)
	{
		this.operation = operation;
	}
	
	public String getSoapAction()
	{
		return soapAction;
	}
	
	public void setSoapAction(String soapAction)
	{
		this.soapAction = soapAction;
	}

	/**
	 * Reads the configuration data stored in the given DOM element into this
	 * service binding instance.  Any previous information stored in this
	 * service binding is lost.
	 * 
	 * @param interactionElement The DOM element containing the configuration
	 */
	public void readConfiguration(Element serviceElement)
	{
		url = serviceElement.getAttribute("url");
		Attr descriptorAttr = serviceElement.getAttributeNode("descriptor");
		if(descriptorAttr != null)
		{
			descriptor = descriptorAttr.getValue();
		}
		else
			descriptor = null;
		Attr serviceAttr = serviceElement.getAttributeNode("service");
		if(serviceAttr != null)
		{
			service = serviceAttr.getValue();
		}
		else
			service = null;
		Attr portAttr = serviceElement.getAttributeNode("port");
		if(portAttr != null)
		{
			port = portAttr.getValue();
		}
		else
			port = null;
		Attr operationAttr = serviceElement.getAttributeNode("operation");
		if(operationAttr != null)
		{
			operation = operationAttr.getValue();
		}
		else
			operation = null;
		Attr soapActionAttr = serviceElement.getAttributeNode("soap-action");
		if(soapActionAttr != null)
			soapAction = soapActionAttr.getValue();
		else
			soapAction = null;
	}
	
	/**
	 * Stores this service binding's information into the given DOM element.
	 * 
	 * @param interactionElement The DOM element to hold this binding's data
	 */
	public void writeConfiguration(Element serviceElement)
	{
		serviceElement.setAttribute("url", url);
		if(descriptor != null)
		{
			serviceElement.setAttribute("descriptor", descriptor);
			serviceElement.setAttribute("service", service);
			serviceElement.setAttribute("port", port);
			serviceElement.setAttribute("operation", operation);
			serviceElement.setAttribute("soap-action", soapAction);
		}
	}

}
