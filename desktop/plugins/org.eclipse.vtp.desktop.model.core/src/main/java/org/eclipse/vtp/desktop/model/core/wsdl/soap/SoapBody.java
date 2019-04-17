package org.eclipse.vtp.desktop.model.core.wsdl.soap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.wsdl.Part;

public class SoapBody
{
	private String use = SoapConstants.LITERAL;
	private String encodingStyle = null;
	private String namespace = null;
	private List<Part> parts = new ArrayList<Part>();
	
	public SoapBody()
	{
		super();
	}
	
	public String getUsage()
	{
		return use;
	}
	
	public void setUsage(String use)
	{
		this.use = use;
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
	
	public List<Part> getParts()
	{
		return parts;
	}
	
	public void addPart(Part part)
	{
		parts.add(part);
	}
}
