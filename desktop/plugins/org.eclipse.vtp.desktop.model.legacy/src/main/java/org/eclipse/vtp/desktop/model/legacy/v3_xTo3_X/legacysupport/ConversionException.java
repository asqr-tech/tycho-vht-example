package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.legacysupport;

@SuppressWarnings("serial")
public class ConversionException extends Exception
{

	public ConversionException()
	{
	}

	/**
	 * @param message
	 */
	public ConversionException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public ConversionException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConversionException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
