package org.eclipse.vtp.framework.common;

import org.eclipse.vtp.framework.core.ILogger;

public interface IVariableStorage
{
	/** The key prefix for variable entries. */
	public static final String ENTRY_PREFIX = "variable.entries."; //$NON-NLS-1$
	/** The key prefix for variable records. */
	public static final String RECORD_PREFIX = "variable.records."; //$NON-NLS-1$
	
	/**
	 * Creates a new variable of the specified type.
	 * 
	 * @param typeName The name of the type of variable to create.
	 * @return A new variable of the specified type.
	 * @throws IllegalArgumentException If no type with the specified name exists.
	 * @throws NullPointerException If the supplied type name is <code>null</code>.
	 */
	IDataObject createVariable(String typeName) throws IllegalArgumentException,
			NullPointerException;

	IDataObject createVariable(String typeName, boolean secured) throws IllegalArgumentException,
	NullPointerException;
	
	/**
	 * Creates a new variable of the specified type.
	 * 
	 * @param type The type of variable to create.
	 * @return A new variable of the specified type.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 */
	IDataObject createVariable(IDataType type) throws IllegalArgumentException,
			NullPointerException;

	IDataObject createVariable(IDataType type, boolean secured) throws IllegalArgumentException,
	NullPointerException;

	public Object[] getRecord(String id);
	
	public void setRecord(String id, IDataType type, Object value);
	
	public IDataObject loadObject(String id);
	
	public ILogger getLogger();
}
