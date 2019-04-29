package org.eclipse.vtp.desktop.model.core.schema;

public abstract class Item
{
	private Schema owner = null;
	
	public Item(Schema owner)
	{
		super();
		this.owner = owner;
	}
	
	public Schema getOwnerSchema()
	{
		return owner;
	}
}
