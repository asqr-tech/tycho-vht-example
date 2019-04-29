package org.eclipse.vtp.desktop.model.core.wsdl;

import java.util.ArrayList;
import java.util.List;

public class PortType
{
	private String name = null;
	private List<Operation> operations = new ArrayList<Operation>();

	public PortType(String name)
	{
		super();
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public List<Operation> getOperations()
	{
		return operations;
	}
	
	public Operation getOperation(String name)
	{
		for(Operation operation : operations)
		{
			if(operation.getName().equals(name))
				return operation;
		}
		return null;
	}
	
	public void addOperation(Operation operation)
	{
		this.operations.add(operation);
	}
}
