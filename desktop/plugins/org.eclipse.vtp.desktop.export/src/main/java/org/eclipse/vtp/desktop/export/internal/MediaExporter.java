package org.eclipse.vtp.desktop.export.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.vtp.desktop.export.IMediaExporter;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;

public final class MediaExporter extends ProjectExporter implements
		IMediaExporter {
	
	private final IMediaProject project;

	public MediaExporter(Exporter exporter, IMediaProject project) {
		this.project = project;
		loadSettings(exporter);
	}
	
	public String getFormatter () {
		return project.getLanguagePackId();
	}

	public IMediaProject getMediaProject() {
		return project;
	}

	public IProject getProject() {
		return project.getUnderlyingProject();
	}

}
