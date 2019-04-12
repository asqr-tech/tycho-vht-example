package org.eclipse.vtp.desktop.model.core.configuration;

public class ConfigurationException extends Exception
{
	public static final int VERSION_NOT_SUPPORTED = 1;
	public static final int FUTURE_VERSION = 2;
	public static final int INVALID_CONFIGURATION = 3;
	
	private static final String[] STANDARD_MESSAGES = new String[]
	{ "Unknow exception occured in configuration",
	  "The configuration version is not supported",
	  "The configuration appears to be from a newer version",
	  "The configuration content is invalid"
	};
	
	private final int type;

	public ConfigurationException(int type)
	{
		super(STANDARD_MESSAGES[(type < 1 || type > 3) ? 0 : type]);
		this.type = type;
	}

	public ConfigurationException(int type, String message)
	{
		super(message);
		this.type = type;
	}

	public ConfigurationException(int type, Throwable cause)
	{
		super(STANDARD_MESSAGES[(type < 1 || type > 3) ? 0 : type], cause);
		this.type = type;
	}

	public ConfigurationException(int type, String message, Throwable cause)
	{
		super(message, cause);
		this.type = type;
	}

	public int getType()
	{
		return type;
	}
}
