package org.eclipse.vtp.desktop.model.core.schema;

import java.util.LinkedList;
import java.util.List;

public class AttributeGrouping extends Item implements AttributeItemContainer
{
	private String name = null;
	private List<AttributeItem> attributes = new LinkedList<AttributeItem>();

	public AttributeGrouping(Schema owner, String name)
	{
		super(owner);
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	public List<AttributeItem> getAttributes()
	{
		return attributes;
	}
	
	public void addAttribute(AttributeItem attribute)
	{
		attributes.add(attribute);
	}
}
