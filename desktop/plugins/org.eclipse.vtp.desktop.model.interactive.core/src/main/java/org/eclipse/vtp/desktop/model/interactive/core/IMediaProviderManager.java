package org.eclipse.vtp.desktop.model.interactive.core;

import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;

public interface IMediaProviderManager
{
	public IInteractiveWorkflowProject getInteractiveWorkflowProject();
	
	public IMediaProject getMediaProject(String interactionType, IBrand brand, String language);
	
	public IMediaProvider getMediaProvider(String interactionType, IBrand brand, String language);
}
