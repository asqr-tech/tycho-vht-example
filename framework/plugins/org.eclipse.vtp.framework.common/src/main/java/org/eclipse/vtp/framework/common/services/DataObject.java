package org.eclipse.vtp.framework.common.services;

import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IDataType;
import org.eclipse.vtp.framework.common.IValueObject;
import org.eclipse.vtp.framework.common.IVariableStorage;
import org.eclipse.vtp.framework.util.Guid;

/**
 * Basic implementation of an {@link IDataObject}.
 * 
 * @author Lonnie Pryor
 */
public abstract class DataObject implements IDataObject
{
	/**
	 * 
	 */
	private IVariableStorage variableStorage;
	/** The ID of this variable. */
	final String id;
	/** The type of this variable. */
	final IDataType type;
	boolean secured = false;

	/**
	 * Creates a new DataObject.
	 * 
	 * @param id The ID of this instance or <code>null</code> to generate a
	 *          new ID.
	 * @param type The type of this instance.
	 */
	protected DataObject(IVariableStorage variableStorage, String id, IDataType type)
	{
		this.variableStorage = variableStorage;
		if (id == null)
			this.id = VariableRegistry.RECORD_PREFIX + Guid.createGUID();
		else
			this.id = id;
		this.type = type;
	}
	
	public String getId()
	{
		return id;
	}

	/**
	 * Loads this variable's data from the registry.
	 * 
	 * @return This variable's data from the registry.
	 */
	protected Object load()
	{
		Object[] record = variableStorage.getRecord(id);
		if (record == null)
			return null;
		Object[] value = (Object[]) record[1];
		this.secured = ((Boolean)value[0]).booleanValue();
		return value[1];
	}

	/**
	 * Saves this variable's data in the registry.
	 * 
	 * @param The value to save in the registry.
	 * @throws IllegalStateException If this object is read-only.
	 */
	protected void save(Object value) throws IllegalStateException
	{
		if (isReadOnly())
			throw new IllegalStateException();
		variableStorage.setRecord(id, type, new Object[] {new Boolean(secured), value});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IDataObject#getType()
	 */
	public final IDataType getType()
	{
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IDataObject#isReadOnly()
	 */
	public boolean isReadOnly()
	{
		return false;
	}
	
	public boolean isSecured()
	{
		return secured;
	}
	
	public void setSecured(boolean secured)
	{
		Object load = load();
		this.secured = secured;
		save(load);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IDataObject#getField(
	 *      java.lang.String)
	 */
	public IDataObject getField(String fieldName)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IDataObject#setField(
	 *      java.lang.String, org.eclipse.vtp.framework.core.IDataObject)
	 */
	public boolean setField(String fieldName, IDataObject variable)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IDataObject#isEqualTo(
	 *      java.lang.Object)
	 */
	public boolean isEqualTo(Object object)
	{
		return equals(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.AbstractSessionScope.DataObject#
	 *      isGreaterThan(java.lang.Object)
	 */
	public boolean isGreaterThan(Object object)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.AbstractSessionScope.DataObject#
	 *      isGreaterThanOrEqualTo(java.lang.Object)
	 */
	public boolean isGreaterThanOrEqualTo(Object object)
	{
		return isGreaterThan(object) || isEqualTo(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.AbstractSessionScope.DataObject#
	 *      isLessThan(java.lang.Object)
	 */
	public boolean isLessThan(Object object)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.AbstractSessionScope.DataObject#
	 *      isLessThanOrEqualTo(java.lang.Object)
	 */
	public boolean isLessThanOrEqualTo(Object object)
	{
		return isLessThan(object) || isEqualTo(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public final boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (!(obj instanceof IDataObject))
			return false;
		return id.equals(((IDataObject)obj).getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
	 */
	public final String getName()
	{
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
	 */
	public boolean hasValue()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
	 */
	public Object toValue()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#
	 *      getFunctionNames()
	 */
	public String[] getFunctionNames()
	{
		return new String[] { "getType", //$NON-NLS-1$
				"isReadOnly", //$NON-NLS-1$
				"isEqualTo", //$NON-NLS-1$
				"isGreaterThan", //$NON-NLS-1$
				"isGreaterThanOrEqualTo", //$NON-NLS-1$
				"isLessThan", //$NON-NLS-1$
				"isLessThanOrEqualTo", //$NON-NLS-1$
				"isSecured", //$NON-NLS-1$
				"toString"}; //$NON-NLS-1$
	}
	
	public String[] getPropertyNames()
	{
		return type.getFieldNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
	 *      java.lang.String, java.lang.Object[])
	 */
	public Object invokeFunction(String name, Object[] arguments) throws Exception
	{
		if ("getType".equals(name)) //$NON-NLS-1$
			return getType();
		if ("isReadOnly".equals(name)) //$NON-NLS-1$
			return isReadOnly() ? Boolean.TRUE : Boolean.FALSE;
		if ("isSecured".equals(name)) //$NON-NLS-1$
			return isSecured() ? Boolean.TRUE : Boolean.FALSE;
		if (arguments.length > 0)
		{
			if ("isEqualTo".equals(name)) //$NON-NLS-1$
				return isEqualTo(arguments[0]) ? Boolean.TRUE : Boolean.FALSE;
			if ("isGreaterThan".equals(name)) //$NON-NLS-1$
				return isGreaterThan(arguments[0]) ? Boolean.TRUE : Boolean.FALSE;
			if ("isGreaterThanOrEqualTo".equals(name)) //$NON-NLS-1$
				return isGreaterThanOrEqualTo(arguments[0]) ? Boolean.TRUE
						: Boolean.FALSE;
			if ("isLessThan".equals(name)) //$NON-NLS-1$
				return isLessThan(arguments[0]) ? Boolean.TRUE : Boolean.FALSE;
			if ("isLessThanOrEqualTo".equals(name)) //$NON-NLS-1$
				return isLessThanOrEqualTo(arguments[0]) ? Boolean.TRUE
						: Boolean.FALSE;
		}
		if("toString".equals(name)) //$NON-NLS-1$
			return toString();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
	 */
	public boolean hasItem(int index)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasEntry(
	 *      java.lang.String)
	 */
	public boolean hasEntry(String name)
	{
		return getField(name) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
	 */
	public Object getItem(int index)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getEntry(
	 *      java.lang.String)
	 */
	public Object getEntry(String name)
	{
		return getField(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
	 *      java.lang.Object)
	 */
	public boolean setItem(int index, Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setEntry(
	 *      java.lang.String, java.lang.Object)
	 */
	public boolean setEntry(String name, Object value)
	{
		if (value instanceof IDataObject)
			return setField(name, (IDataObject)value);
		IDataObject variable = getField(name);
		if (variable == null)
			setField(name, variable = variableStorage.createVariable(type.getFieldType(name)));
		if (variable instanceof IValueObject)
			((IValueObject)variable).setValue(value);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
	 */
	public boolean clearItem(int index)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearEntry(
	 *      java.lang.String)
	 */
	public boolean clearEntry(String name)
	{
		return setField(name, null);
	}
	
	public boolean isMutable()
	{
		return false;
	}
	
	protected IVariableStorage getStorage()
	{
		return variableStorage;
	}
}