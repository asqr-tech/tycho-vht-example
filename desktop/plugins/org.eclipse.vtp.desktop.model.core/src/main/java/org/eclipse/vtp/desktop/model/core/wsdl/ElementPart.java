package org.eclipse.vtp.desktop.model.core.wsdl;

import org.eclipse.vtp.desktop.model.core.schema.ElementItem;

public class ElementPart extends Part
{
	private ElementItem elementItem = null;

	public ElementPart(String name)
	{
		super(name);
	}

	public ElementItem getElementItem()
	{
		return elementItem;
	}
	
	public void setElementItem(ElementItem elementItem)
	{
		this.elementItem = elementItem;
	}
}
