package org.eclipse.vtp.desktop.model.core.wsdl.soap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.wsdl.Message;
import org.eclipse.vtp.desktop.model.core.wsdl.Part;

public class SoapHeader
{
	private Message message = null;
	private Part part = null;
	private String use = null;
	private String encodingStyle = null;
	private String namespace = null;
	private List<SoapHeaderFault> headerFaults = new ArrayList<SoapHeaderFault>();

	public SoapHeader(Message message, Part part, String use)
	{
		super();
		this.message = message;
		this.part = part;
		this.use = use;
	}

	public Message getMessage()
	{
		return message;
	}
	
	public Part getPart()
	{
		return part;
	}
	
	public String getUsage()
	{
		return use;
	}
	
	public String getEncodingStyle()
	{
		return encodingStyle;
	}
	
	public void setEncodingStyle(String encodingStyle)
	{
		this.encodingStyle = encodingStyle;
	}
	
	public String getNamespace()
	{
		return namespace;
	}
	
	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}
	
	public List<SoapHeaderFault> getHeaderFaults()
	{
		return headerFaults;
	}
	
	public void addHeaderFault(SoapHeaderFault headerFault)
	{
		headerFaults.add(headerFault);
	}
}
