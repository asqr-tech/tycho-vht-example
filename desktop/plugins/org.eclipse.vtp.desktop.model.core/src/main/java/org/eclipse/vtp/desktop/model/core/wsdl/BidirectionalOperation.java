package org.eclipse.vtp.desktop.model.core.wsdl;

import java.util.ArrayList;
import java.util.List;

public class BidirectionalOperation extends Operation
{
	public static final String REQUEST = "request";
	public static final String SOLICIT = "solicit";
	
	private String type = REQUEST;
	private List<String> parameterOrder = new ArrayList<String>();
	private OperationElement inputElement = null;
	private OperationElement outputElement = null;
	private List<OperationElement> faults = new ArrayList<OperationElement>();

	public BidirectionalOperation(String name)
	{
		super(name);
	}
	
	public String getType()
	{
		return type;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}

	public List<String> getParameterOrder()
	{
		return this.parameterOrder;
	}
	
	public void setParameterOrder(List<String> parameterOrder)
	{
		this.parameterOrder = parameterOrder;
	}
	
	public OperationElement getInputElement()
	{
		return inputElement;
	}
	
	public void setInputElement(OperationElement inputElement)
	{
		this.inputElement = inputElement;
	}
	
	public OperationElement getOutputElement()
	{
		return this.outputElement;
	}
	
	public void setOutputElement(OperationElement outputElement)
	{
		this.outputElement = outputElement;
	}
	
	public List<OperationElement> getFaults()
	{
		return faults;
	}
	
	public void addFault(OperationElement fault)
	{
		faults.add(fault);
	}
}
