package org.eclipse.vtp.desktop.model.core.schema;

import java.util.List;

public class ElementGroupReference extends ElementGroup
{
	private ElementGrouping referencedGroup = null;

	public ElementGroupReference(Schema owner)
	{
		super(owner, ElementGroup.SEQUENCE);
	}

	@Override
	public void addElementObject(AbstractElementObject elementObject)
	{
		throw new UnsupportedOperationException("Cannot directly modify the contents of an element group reference.");
	}

	@Override
	public List<AbstractElementObject> getElementObjects()
	{
		return referencedGroup.getElementGroup().getElementObjects();
	}

	@Override
	public String getType()
	{
		return referencedGroup.getElementGroup().getType();
	}

	public ElementGroup getReferencedElementGroup()
	{
		return referencedGroup.getElementGroup();
	}
	
	public void setReferencedElementGroup(ElementGrouping referencedGroup)
	{
		this.referencedGroup = referencedGroup;
	}
}
