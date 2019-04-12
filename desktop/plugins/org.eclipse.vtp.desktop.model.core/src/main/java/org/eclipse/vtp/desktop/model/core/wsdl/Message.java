package org.eclipse.vtp.desktop.model.core.wsdl;

import java.util.ArrayList;
import java.util.List;

public class Message
{
	private String name = null;
	private List<Part> parts = new ArrayList<Part>();
	
	public Message(String name)
	{
		super();
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<Part> getParts()
	{
		return parts;
	}
	
	public Part getPart(String partName)
	{
		for(Part part : parts)
		{
			if(part.getName().equals(partName))
				return part;
		}
		return null;
	}
	
	public void addPart(Part part)
	{
		parts.add(part);
	}
}
