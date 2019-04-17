package org.eclipse.vtp.desktop.model.core.wsdl.soap;

import org.eclipse.vtp.desktop.model.core.wsdl.Message;
import org.eclipse.vtp.desktop.model.core.wsdl.Part;


public class SoapHeaderFault
{
	private Message message = null;
	private Part part = null;
	private String use = null;
	private String encodingStyle = null;
	private String namespace = null;


	public SoapHeaderFault(Message message, Part part, String use)
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

}
