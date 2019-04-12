package org.eclipse.vtp.desktop.model.core.schema;

public class AttributeItem extends Item
{
	private SimpleType type = null;
	private String name = null;
	private String defaultValue = null;
	private String fixedValue = null;
	private boolean qualifyOverride = false;
	private boolean qualified = false;
	private boolean required = false;

	public AttributeItem(Schema owner, String name)
	{
		super(owner);
		this.name = name;
	}

	public String getName()
	{
		return name;
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
	
	public SimpleType getType()
	{
		return type;
	}
	
	public void setType(SimpleType type)
	{
		this.type = type;
	}
	
	public boolean isRequired()
	{
		return required;
	}
	
	public void setRequired(boolean required)
	{
		this.required = required;
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
