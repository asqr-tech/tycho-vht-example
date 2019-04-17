package org.eclipse.vtp.desktop.model.core.schema;

import java.util.LinkedList;
import java.util.List;

public class ContentModel implements AttributeItemContainer
{
	List<AttributeItem> attributes = new LinkedList<AttributeItem>();

	public ContentModel()
	{
		super();
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
