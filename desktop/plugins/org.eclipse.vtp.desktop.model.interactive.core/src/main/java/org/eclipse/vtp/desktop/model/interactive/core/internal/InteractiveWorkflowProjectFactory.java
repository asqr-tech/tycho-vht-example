/**
 * 
 */
package org.eclipse.vtp.desktop.model.interactive.core.internal;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowProjectFactory;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.core.natures.InteractiveWorkflowProjectNature;
import org.eclipse.vtp.framework.util.Guid;

/**
 * @author trip
 *
 */
public class InteractiveWorkflowProjectFactory implements IWorkflowProjectFactory
{

	/**
	 * 
	 */
	public InteractiveWorkflowProjectFactory()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowProjectFactory#convertToWorkflowProject(org.eclipse.core.resources.IProject)
	 */
	public IWorkflowProject convertToWorkflowProject(IProject project)
	{
		return new InteractiveWorkflowProject(project);
	}

	public IWorkflowProject createWorkflowProject(String name)
	{
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot()
		    .getProject(name);
		try
		{
			newProject.create(null);
			newProject.open(null);
			
			IFolder boFolder = newProject.getFolder("Business Objects");
			boFolder.create(true, true, null);
			IFolder dbFolder = newProject.getFolder("Databases");
			dbFolder.create(true, true, null);
			IFolder wsFolder = newProject.getFolder("Webservices");
			wsFolder.create(true, true, null);
			IFolder depFolder = newProject.getFolder("Dependencies");
			depFolder.create(true, true, null);
			IFolder desRootFolder = newProject.getFolder("Workflow Design");
			desRootFolder.create(true, true, null);
			
			IFile mainDesignDocument = desRootFolder.getFile("Main Canvas.canvas");
			String template = WorkflowCore.getDefault().getTemplate("primary_design_document_template.xml");
			if(template != null)
			{
				template = template.replaceAll("\\[\\[flow_id\\]\\]", Guid.createGUID());
				template = template.replaceAll("\\[\\[begin_id\\]\\]", Guid.createGUID());
				
				mainDesignDocument.create(new ByteArrayInputStream(template.getBytes()), true, null);
			}

			IProjectDescription desc = newProject.getDescription();
			desc.setNatureIds(new String[] {InteractiveWorkflowProjectNature.NATURE_ID});
			newProject.setDescription(desc, null);
			return convertToWorkflowProject(newProject);
		}
		catch(Exception ce)
		{
			ce.printStackTrace();
		}
		return null;
	}

}
