package org.eclipse.vtp.desktop.model.interactive.core.mediadefaults;

public interface IMediaDefaultSettings
{
	public boolean inheritanceSupported();
	
	public IMediaDefaultSetting getDefaultSetting(String interactionType, String elementType, String setting);
	
}
