package org.eclipse.vtp.desktop.model.core.schema;

public class ElementItem extends AbstractElementObject
{
	private Type type = null;
	private String name = null;
	private boolean qualifyOverride = false;
	private boolean qualified = false;
	private String defaultValue = null;
	private String fixedValue = null;

	public ElementItem(Schema owner, String name)
	{
		super(owner);
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	public Type getType()
	{
		return type;
	}
	
	public void setType(Type type)
	{
		this.type = type;
	}

	public boolean hasQualifyOverride()
	{
		return qualifyOverride;
	}
	
	public void setQualifyOverride(boolean qualifyOverride)
	{
		this.qualifyOverride = qualifyOverride;
	}
	
	public boolean isQualified()
	{
		return qualified;
	}
	
	public void setQualified(boolean qualified)
	{
		this.qualified = qualified;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	public String getFixedValue()
	{
		return fixedValue;
	}

	public void setFixedValue(String fixedValue)
	{
		this.fixedValue = fixedValue;
	}
	
}
