package org.eclipse.vtp.desktop.export.internal;

import org.eclipse.vtp.desktop.export.IExportAgent;

public final class ExportAgent
{
	private final String id;

	private final IExportAgent value;

	public ExportAgent(String id, IExportAgent value) {
		this.id = id;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public IExportAgent getValue() {
		return value;
	}
	
}
