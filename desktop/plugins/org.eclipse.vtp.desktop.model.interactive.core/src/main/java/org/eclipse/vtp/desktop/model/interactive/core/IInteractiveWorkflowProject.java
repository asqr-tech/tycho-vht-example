package org.eclipse.vtp.desktop.model.interactive.core;

import java.util.List;

import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSettings;

public interface IInteractiveWorkflowProject extends IWorkflowProject
{
	public List<String> getSupportedInteractionTypes();
	
	public List<String> getSupportedLanguages(String interactionType);
	
	public IMediaProviderManager getMediaProviderManager();
	
	public IMediaDefaultSettings getMediaDefaultSettings();

}
