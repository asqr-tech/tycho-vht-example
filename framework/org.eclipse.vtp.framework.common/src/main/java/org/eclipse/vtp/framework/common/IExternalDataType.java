package org.eclipse.vtp.framework.common;

public interface IExternalDataType
{
	IDataObject createInstance(IVariableStorage storage, IDataType dataType, String id);
}
