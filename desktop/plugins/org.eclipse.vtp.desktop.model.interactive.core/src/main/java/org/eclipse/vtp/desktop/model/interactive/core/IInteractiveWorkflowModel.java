package org.eclipse.vtp.desktop.model.interactive.core;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

public interface IInteractiveWorkflowModel
{
	public IMediaProject getMediaProject(String id);
	
	public boolean isMediaProject(IProject project);
	
	public List<IMediaProject> listMediaProjects();
	
	public IMediaProject createMediaProject(String natureId, String languagePackId, String name);
	
	public IMediaProject convertToMediaProject(IProject project);

	public IMediaObject convertToMediaObject(IResource resource);
}
