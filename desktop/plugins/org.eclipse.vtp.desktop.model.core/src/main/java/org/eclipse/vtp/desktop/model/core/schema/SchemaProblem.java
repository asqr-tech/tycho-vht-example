package org.eclipse.vtp.desktop.model.core.schema;

public class SchemaProblem
{
	private String message;
	private int lineNumber = -1;
	private Throwable cause = null;
	
	public SchemaProblem(String message, int lineNumber)
	{
		this(message, lineNumber, null);
	}
	
	public SchemaProblem(String message, int lineNumber, Throwable t)
	{
		super();
		this.message = message;
		this.lineNumber = lineNumber;
		this.cause = t;
	}

	public String getMessage()
	{
		return message;
	}
	
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	public Throwable getCause()
	{
		return cause;
	}
}
