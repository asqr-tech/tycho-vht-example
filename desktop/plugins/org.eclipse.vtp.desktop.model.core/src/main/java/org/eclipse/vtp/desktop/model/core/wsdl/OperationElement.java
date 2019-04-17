package org.eclipse.vtp.desktop.model.core.wsdl;

public class OperationElement
{
	public static final String INPUT = "input";
	public static final String OUTPUT = "output";
	public static final String FAULT = "fault";
	
	private String name = null;
	private String type = null;
	private Message message = null;

	public OperationElement(String type)
	{
		super();
		this.type = type;
	}
	
	public OperationElement(String type, String name)
	{
		super();
		this.type = type;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getType()
	{
		return type;
	}
	
	public Message getMessage()
	{
		return message;
	}
	
	public void setMessage(Message message)
	{
		this.message = message;
	}
}
