package org.eclipse.vtp.desktop.media.core;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSettings;

public interface IMediaDefaultPanel
{
	public String getTitle();
	
	public Control createControls(Composite parent, boolean supportDefaults);
	
	public void setDefaultSettings(IMediaDefaultSettings defaultSettings);
	
	public void save();
}
