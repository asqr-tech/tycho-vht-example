package org.eclipse.vtp.desktop.model.core.schema;

public class CardinalItem extends Item
{
	public static final int UNBOUNDED = Integer.MAX_VALUE;
	int minOccurs = 1;
	int maxOccurs = 1;

	public CardinalItem(Schema owner)
	{
		super(owner);
	}

	public int getMinOccurs()
	{
		return minOccurs;
	}
	
	public void setMinOccurs(int minOccurs)
	{
		this.minOccurs = minOccurs;
	}
	
	public int getMaxOccurs()
	{
		return maxOccurs;
	}
	
	public void setMaxOccurs(int maxOccurs)
	{
		this.maxOccurs = maxOccurs;
	}
}
