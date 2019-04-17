package org.eclipse.vtp.desktop.model.interactive.core.mediadefaults;

public interface IMediaDefaultSetting
{
	public String getInteractionType();
	
	public String getElementType();
	
	public String getName();
	
	public boolean isValueInherited();
	
	public String getValue();
	
	public void setValue(String value);
}
