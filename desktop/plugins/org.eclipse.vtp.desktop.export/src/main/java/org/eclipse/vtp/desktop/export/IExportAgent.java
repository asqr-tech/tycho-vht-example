package org.eclipse.vtp.desktop.export;

import java.util.Collection;

import org.eclipse.jface.wizard.IWizardPage;
import org.w3c.dom.Element;

public interface IExportAgent {
	
	Collection<IWizardPage> init();
	
	void setProjects(
			Collection<? extends IWorkflowExporter> workflowProjects,
			Collection<? extends IMediaExporter> mediaProjects);
	
	boolean shouldBeShown(IWizardPage page);
	
	boolean canFinish();
	
	void configureServices(IWorkflowExporter exporter, Element servicesElement);
	
}
