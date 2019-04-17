package org.eclipse.vtp.desktop.model.core.wsdl;

import java.util.ArrayList;
import java.util.List;

public class Binding
{
	private String name = null;
	private List<BindingOperation> bindingOperations = new ArrayList<BindingOperation>();

	public Binding(String name)
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

	public List<BindingOperation> getOperations()
	{
		return bindingOperations;
	}
	
	public void addOperation(BindingOperation bindingOperation)
	{
		bindingOperations.add(bindingOperation);
	}
}
