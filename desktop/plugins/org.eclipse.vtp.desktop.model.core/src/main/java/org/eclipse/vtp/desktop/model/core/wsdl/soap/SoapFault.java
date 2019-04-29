package org.eclipse.vtp.desktop.model.core.wsdl.soap;

public class SoapFault
{
	private String name = null;
	private String use = null;
	private String encodingStyle = null;
	private String namespace = null;

	public SoapFault(String name, String use)
	{
		super();
		this.name = name;
		this.use = use;
	}
	
	public String getName()
	{
		return name;
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
