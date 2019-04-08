package org.eclipse.vtp.framework.common.support;

import org.eclipse.vtp.framework.common.IDataType;

/**
 * CustomDataField.
 * 
 * @author Lonnie Pryor
 */
public class CustomDataField
{
	/** Comment for name. */
	private final String name;
	/** Comment for type. */
	private final IDataType type;
	/** Comment for initialValue. */
	private final String initialValue;
	private final boolean secured;

	/**
	 * Creates a new CustomDataField.
	 * 
	 * @param name
	 * @param type
	 * @param initialValue
	 */
	public CustomDataField(String name, IDataType type, String initialValue)
	{
		this(name, type, initialValue, false);
	}
	
	public CustomDataField(String name, IDataType type, String initialValue, boolean secured)
	{
		this.name = name;
		this.type = type;
		this.initialValue = initialValue;
		this.secured = secured;
	}

	/**
	 * Returns the name.
	 * 
	 * @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the type.
	 * 
	 * @return The type.
	 */
	public IDataType getType()
	{
		return type;
	}

	/**
	 * Returns the initialValue.
	 * 
	 * @return The initialValue.
	 */
	public String getInitialValue()
	{
		return initialValue;
	}
	
	public boolean isSecured()
	{
		return secured;
	}
}
