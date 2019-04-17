package org.eclipse.vtp.desktop.model.core;

import org.eclipse.core.resources.IProject;

/**
 * @author trip
 *
 */
public interface IWorkflowProjectFactory
{	
	public IWorkflowProject createWorkflowProject(String name);
	
	public IWorkflowProject convertToWorkflowProject(IProject project);
}
