/**
 * 
 */
package org.eclipse.vtp.desktop.model.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.model.core.IWorkflowModel;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowProjectFactory;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.IWorkflowResourceContainer;
import org.osgi.framework.Bundle;

/**
 * @author trip
 *
 */
public class WorkflowModel implements IWorkflowModel
{
	public static final String workflowProjectFactoryExtensionId = "org.eclipse.vtp.desktop.model.core.workflowProjectFactories";

	private Map<String, IWorkflowProjectFactory> projectFactories =
		new HashMap<String, IWorkflowProjectFactory>();
	
	/**
	 * 
	 */
	public WorkflowModel()
	{
		IConfigurationElement[] projectFactoryExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(workflowProjectFactoryExtensionId);
		for(int i = 0; i < projectFactoryExtensions.length; i++)
		{
			String natureId = projectFactoryExtensions[i].getAttribute("nature-id");
			String className = projectFactoryExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(projectFactoryExtensions[i].getContributor().getName());
			try
			{
				@SuppressWarnings("unchecked")
				Class<IWorkflowProjectFactory> factoryClass = (Class<IWorkflowProjectFactory>)contributor.loadClass(className);
				IWorkflowProjectFactory factory = factoryClass.newInstance();
				projectFactories.put(natureId, factory);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowModel#convertToWorkflowProject(org.eclipse.core.resources.IProject)
	 */
	public IWorkflowProject convertToWorkflowProject(IProject project)
	{
		try
		{
			String[] natureIds = project.getDescription().getNatureIds();
			for(String natureId : natureIds)
			{
				IWorkflowProjectFactory factory = projectFactories.get(natureId);
				if(factory != null)
				{
					return factory.convertToWorkflowProject(project);
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowModel#createWorkflowProject(java.lang.String)
	 */
	public IWorkflowProject createWorkflowProject(String natureId, String name)
	{
		IWorkflowProjectFactory factory = projectFactories.get(natureId);
		if(factory != null)
		{
			return factory.createWorkflowProject(name);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowModel#getWorkflowProject(java.lang.String)
	 */
	public IWorkflowProject getWorkflowProject(String id)
	{
		List<IWorkflowProject> projects = this.listWorkflowProjects();
		for(IWorkflowProject workflowProject : projects)
		{
			if(workflowProject.getId().equals(id))
			{
				return workflowProject;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowModel#isWorkflowProject(org.eclipse.core.resources.IProject)
	 */
	public boolean isWorkflowProject(IProject project)
	{
		if(!project.isOpen())
			return false;
		try
		{
			String[] natureIds = project.getDescription().getNatureIds();
			for(String natureId : natureIds)
			{
				IWorkflowProjectFactory factory = projectFactories.get(natureId);
				if(factory != null)
				{
					return true;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowModel#listWorkflowProjects()
	 */
	public List<IWorkflowProject> listWorkflowProjects()
	{
		List<IWorkflowProject> projects = new ArrayList<IWorkflowProject>();
		IProject[] rawProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(IProject project : rawProjects)
		{
			try
			{
				String[] natureIds = project.getDescription().getNatureIds();
				for(String natureId : natureIds)
				{
					IWorkflowProjectFactory factory = projectFactories.get(natureId);
					if(factory != null)
					{
						projects.add(factory.convertToWorkflowProject(project));
						break;
					}
				}
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
		}
		return projects;
	}

	public IWorkflowResource convertToWorkflowResource(IResource resource)
	{
		if(resource instanceof IProject && ((IProject)resource).isOpen() && isWorkflowProject((IProject)resource))
			return convertToWorkflowProject((IProject)resource);
		IProject project = resource.getProject();
		if(project != null && isWorkflowProject(project))
		{
			IWorkflowProject workflowProject = convertToWorkflowProject(project);
			List<IResource> containers = new LinkedList<IResource>();
			containers.add(resource);
			IContainer container = resource.getParent();
			while(container != null && !container.equals(project))
			{
				containers.add(0, container);
				container = container.getParent();
			}
			return locateWorkflowResource(workflowProject, containers);
		}
		return null;
	}

	private IWorkflowResource locateWorkflowResource(IWorkflowResourceContainer parentResource, List<IResource> path)
	{
		IResource resource = path.remove(0);
		for(IWorkflowResource child : parentResource.getChildren())
		{
			IResource adaptedResource = (IResource)child.getAdapter(IResource.class);
			if(adaptedResource != null && adaptedResource.equals(resource))
			{
				if(path.isEmpty())
					return child;
				if(child instanceof IWorkflowResourceContainer)
					return locateWorkflowResource((IWorkflowResourceContainer)child, path);
				return null;
			}
		}
		return null;
	}
	
}
