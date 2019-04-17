package org.eclipse.vtp.desktop.model.core.schema;

public class ElementGrouping extends Item
{
	private String name = null;
	private ElementGroup elementGroup = null;

	public ElementGrouping(Schema owner, String name)
	{
		super(owner);
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	public ElementGroup getElementGroup()
	{
		return elementGroup;
	}
	
	public void setElementGroup(ElementGroup elementGroup)
	{
		this.elementGroup = elementGroup;
	}
}
