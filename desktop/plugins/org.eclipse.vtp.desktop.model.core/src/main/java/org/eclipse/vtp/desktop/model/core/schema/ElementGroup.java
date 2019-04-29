package org.eclipse.vtp.desktop.model.core.schema;

import java.util.LinkedList;
import java.util.List;

public class ElementGroup extends AbstractElementObject
{
	public static final String ALL = "all";
	public static final String SEQUENCE = "sequence";
	public static final String CHOICE = "choice";
	public static final String ANY = "any";
	
	private String type = null;
	private List<AbstractElementObject> elementObjects = new LinkedList<AbstractElementObject>();

	public ElementGroup(Schema owner, String type)
	{
		super(owner);
		this.type = type;
	}

	public String getType()
	{
		return type;
	}
	
	public List<AbstractElementObject> getElementObjects()
	{
		return elementObjects;
	}
	
	public void addElementObject(AbstractElementObject elementObject)
	{
		this.elementObjects.add(elementObject);
	}
}
