package org.eclipse.vtp.desktop.model.core.wsdl;

import org.eclipse.vtp.desktop.model.core.schema.Type;


public class TypedPart extends Part
{
	private Type type = null;

	public TypedPart(String name)
	{
		super(name);
	}

	public Type getType()
	{
		return type;
	}
	
	public void setType(Type type)
	{
		this.type = type;
	}
}
