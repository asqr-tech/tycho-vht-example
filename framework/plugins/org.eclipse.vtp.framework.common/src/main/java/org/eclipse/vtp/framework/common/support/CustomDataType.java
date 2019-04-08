package org.eclipse.vtp.framework.common.support;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.vtp.framework.common.IDataType;
import org.eclipse.vtp.framework.common.IExternalDataType;

/**
 * CustomDataType.
 * 
 * @author Lonnie Pryor
 */
public class CustomDataType implements IDataType
{
	/** Comment for name. */
	private final String name;
	/** Comment for primary. */
	private final String primaryFieldName;
	/** Comment for fields. */
	private final Map<String, CustomDataField> fields;

	/**
	 * Creates a new CustomDataType.
	 * 
	 * @param name
	 */
	public CustomDataType(String name, String primaryFieldName, CustomDataField[] fields)
	{
		this.name = name;
		this.primaryFieldName = primaryFieldName;
		this.fields = new HashMap<String, CustomDataField>(fields.length);
		for (int i = 0; i < fields.length; ++i)
			this.fields.put(fields[i].getName(), fields[i]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.IDataType#getName()
	 */
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.IDataType#getPrimaryFieldName()
	 */
	public String getPrimaryFieldName()
	{
		return primaryFieldName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.IDataType#getFieldNames()
	 */
	public String[] getFieldNames()
	{
		return fields.keySet().toArray(new String[fields.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.IDataType#getFieldInitialValue(
	 *      java.lang.String)
	 */
	public String getFieldInitialValue(String fieldName)
	{
		return fields.get(fieldName).getInitialValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.IDataType#getFieldType(java.lang.String)
	 */
	public IDataType getFieldType(String fieldName)
	{
		return fields.get(fieldName).getType();
	}
	
	public boolean isFieldSecured(String fieldName)
	{
		return fields.get(fieldName).isSecured();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.IDataType#isSimpleType()
	 */
	public boolean isSimpleType()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.IDataType#isArrayType()
	 */
	public boolean isArrayType()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.IDataType#isComplexType()
	 */
	public boolean isComplexType()
	{
		return true;
	}

	@Override
	public boolean isExternalType()
	{
		return false;
	}

	@Override
	public IExternalDataType getExternalFactory()
	{
		return null;
	}
}
