package org.eclipse.vtp.desktop.model.elements.core.configuration;

public class OutputItem
{
	private String value = null;
	
	public OutputItem(String value)
	{
		super();
		this.value = value;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
}
