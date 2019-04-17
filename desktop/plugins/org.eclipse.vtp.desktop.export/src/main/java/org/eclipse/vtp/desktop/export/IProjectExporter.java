package org.eclipse.vtp.desktop.export;

import org.eclipse.core.resources.IProject;

public interface IProjectExporter {
	
	String getSetting(String key);
	
	String getSetting(String key, String defaultValue);
	
	void putSetting(String key, String value);
	
	void clearSetting(String key);
	
	IProject getProject();
	
}
