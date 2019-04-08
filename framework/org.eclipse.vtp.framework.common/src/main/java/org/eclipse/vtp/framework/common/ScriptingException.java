package org.eclipse.vtp.framework.common;

public class ScriptingException extends RuntimeException
{
	private String title;
	private String description;
	
	public ScriptingException(String title, String description, Throwable arg0)
	{
		super(arg0);
		this.title = title;
		this.description = description;
	}
	
	public String getMessage()
	{
		return "Scripting error: " + title + " - " + description;
	}

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return description;
	}

}
