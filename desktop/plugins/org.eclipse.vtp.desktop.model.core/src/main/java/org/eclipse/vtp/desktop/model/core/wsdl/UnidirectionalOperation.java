package org.eclipse.vtp.desktop.model.core.wsdl;

public class UnidirectionalOperation extends Operation
{
	private OperationElement operationElement = null;

	public UnidirectionalOperation(String name)
	{
		super(name);
	}

	public OperationElement getOperationElement()
	{
		return operationElement;
	}
	
	public void setOperationElement(OperationElement operationElement)
	{
		this.operationElement = operationElement;
	}
}
