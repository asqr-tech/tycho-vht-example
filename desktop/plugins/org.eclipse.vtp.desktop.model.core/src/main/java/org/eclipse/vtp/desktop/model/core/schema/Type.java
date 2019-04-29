package org.eclipse.vtp.desktop.model.core.schema;

public class Type
{
	private Schema owner = null;
	private String namespace = null;
	private String name = null;

	public Type(Schema owner, String name)
	{
		this(owner, null, name);
	}
	
	public Type(Schema owner, String namespace, String name)
	{
		super();
		this.owner = owner;
		this.namespace = namespace;
		this.name = name;
	}

	public String getNamespace()
	{
		return namespace;
	}
	
	public String getName()
	{
		return name;
	}

	public Schema getOwnerSchema()
	{
		return owner;
	}
}
