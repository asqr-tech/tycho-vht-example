package org.eclipse.vtp.desktop.media.core;

public class ContentPlaceholder
{
	private String name;
	private String description;
	
	public ContentPlaceholder(String name, String description)
	{
		super();
		this.name = name;
		this.description = description;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
}
