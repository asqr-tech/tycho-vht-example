package org.eclipse.vtp.desktop.export;

import org.eclipse.vtp.desktop.model.core.IWorkflowProject;

public interface IWorkflowExporter extends IProjectExporter {
	
	IWorkflowProject getWorkflowProject();
	
}
