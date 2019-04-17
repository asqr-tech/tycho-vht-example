/**
 * 
 */
package org.eclipse.vtp.desktop.model.interactive.core.internal;

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
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowModel;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObjectContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProjectFactory;
import org.osgi.framework.Bundle;

/**
 * @author trip
 *
 */
public class InteractiveWorkflowModel implements IInteractiveWorkflowModel
{
	public static final String mediaProjectExtensionId = "org.eclipse.vtp.desktop.model.interactive.core.mediaProjects";

	private Map<String, IMediaProjectFactory> projectFactories =
		new HashMap<String, IMediaProjectFactory>();

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	public InteractiveWorkflowModel()
	{
		IConfigurationElement[] primitiveExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(mediaProjectExtensionId);
		for(int i = 0; i < primitiveExtensions.length; i++)
		{
			//TODO review these attributes to see if they need logged or removed
			String id = primitiveExtensions[i].getAttribute("id");
			String name = primitiveExtensions[i].getAttribute("name");
			String nature = primitiveExtensions[i].getAttribute("nature-id");
			String interactionType = primitiveExtensions[i].getAttribute("interaction-type");
			String className = primitiveExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(primitiveExtensions[i].getContributor().getName());
			try
			{
				@SuppressWarnings("unchecked")
				Class<IMediaProjectFactory> factoryClass = (Class<IMediaProjectFactory>) contributor.loadClass(className);
				if(IMediaProjectFactory.class.isAssignableFrom(factoryClass))
				{
					projectFactories.put(nature, factoryClass.newInstance());
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowModel#convertToMediaProject(org.eclipse.core.resources.IProject)
	 */
	public IMediaProject convertToMediaProject(IProject project)
	{
		if(!project.isOpen())
			return null;
		try
		{
			String[] natureIds = project.getDescription().getNatureIds();
			for(String natureId : natureIds)
			{
				IMediaProjectFactory factory = projectFactories.get(natureId);
				if(factory != null)
				{
					return factory.convertToMediaProject(project);
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
	 * @see org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowModel#createMediaProject(java.lang.String)
	 */
	public IMediaProject createMediaProject(String natureId, String languagePackId, String name)
	{
		IMediaProjectFactory factory = projectFactories.get(natureId);
		if(factory != null)
		{
			return factory.createMediaProject(name, languagePackId);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowModel#getMediaProject(java.lang.String)
	 */
	public IMediaProject getMediaProject(String id)
	{
		List<IMediaProject> projects = this.listMediaProjects();
		for(IMediaProject mediaProject : projects)
		{
			if(mediaProject.getId().equals(id))
			{
				return mediaProject;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowModel#isMediaProject(org.eclipse.core.resources.IProject)
	 */
	public boolean isMediaProject(IProject project)
	{
		if(!project.isOpen())
			return false;
		try
		{
			String[] natureIds = project.getDescription().getNatureIds();
			for(String natureId : natureIds)
			{
				IMediaProjectFactory factory = projectFactories.get(natureId);
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
	 * @see org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowModel#listMediaProjects()
	 */
	public List<IMediaProject> listMediaProjects()
	{
		List<IMediaProject> projects = new ArrayList<IMediaProject>();
		IProject[] rawProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(IProject project : rawProjects)
		{
			try
			{
				String[] natureIds = project.getDescription().getNatureIds();
				for(String natureId : natureIds)
				{
					IMediaProjectFactory factory = projectFactories.get(natureId);
					if(factory != null)
					{
						projects.add(factory.convertToMediaProject(project));
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

	public IMediaObject convertToMediaObject(IResource resource)
	{
		if(resource instanceof IProject)
			return convertToMediaProject((IProject)resource);
		IProject project = resource.getProject();
		if(project != null && isMediaProject(project))
		{
			IMediaProject mediaProject = convertToMediaProject(project);
			List<IResource> containers = new LinkedList<IResource>();
			containers.add(resource);
			IContainer container = resource.getParent();
			while(container != null && !container.equals(project))
			{
				containers.add(0, container);
				container = container.getParent();
			}
			return locateMediaObject(mediaProject, containers);
		}
		return null;
	}

	private IMediaObject locateMediaObject(IMediaObjectContainer parentResource, List<IResource> path)
	{
		IResource resource = path.remove(0);
		for(IMediaObject child : parentResource.getChildren())
		{
			IResource adaptedResource = (IResource)child.getAdapter(IResource.class);
			if(adaptedResource != null && adaptedResource.equals(resource))
			{
				if(path.isEmpty())
					return child;
				if(child instanceof IMediaObjectContainer)
					return locateMediaObject((IMediaObjectContainer)child, path);
				return null;
			}
		}
		return null;
	}
}
