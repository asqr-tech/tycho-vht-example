package org.eclipse.vtp.desktop.model.core.wsdl;

public class WSDLProblem
{
	private String message;
	private int lineNumber = -1;
	private Throwable cause = null;
	
	public WSDLProblem(String message, int lineNumber)
	{
		this(message, lineNumber, null);
	}
	
	public WSDLProblem(String message, int lineNumber, Throwable t)
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
