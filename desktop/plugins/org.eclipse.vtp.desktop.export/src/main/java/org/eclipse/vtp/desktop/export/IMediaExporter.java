package org.eclipse.vtp.desktop.export;

import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;

public interface IMediaExporter extends IProjectExporter {
	
	IMediaProject getMediaProject();
	
}
