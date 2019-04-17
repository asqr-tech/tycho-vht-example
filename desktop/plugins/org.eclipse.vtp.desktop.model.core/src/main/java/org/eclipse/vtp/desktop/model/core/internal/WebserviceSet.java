/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.core.internal;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IWebserviceDescriptor;
import org.eclipse.vtp.desktop.model.core.IWebserviceSet;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.event.ObjectRefreshEvent;

/**
 * WebserviceSet.
 * 
 * @author Trip Gilman
 */
public class WebserviceSet extends WorkflowResource implements IWebserviceSet
{
	/** Comment for project. */
	private final IWorkflowProject project;
	/** Comment for folder. */
	private final IFolder folder;

	/**
	 * Creates a new WebserviceSet.
	 * 
	 * @param project
	 * @param folder
	 */
	public WebserviceSet(final IWorkflowProject project, final IFolder folder)
	{
		this.project = project;
		this.folder = folder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.project.internals.VoiceResource#getObjectId()
	 */
	protected String getObjectId()
	{
		return folder.getFullPath().toPortableString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getName()
	 */
	public String getName()
	{
		return folder.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getParent()
	 */
	public IWorkflowResource getParent()
	{
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.project.IDependencySet#getDependencies()
	 */
	public List<IWebserviceDescriptor> getWebserviceDescriptors()
	{
		return getWebserviceDescriptors(false);
	}
	
	public List<IWebserviceDescriptor> getWebserviceDescriptors(boolean validate)
	{
		List<IWebserviceDescriptor> results = new LinkedList<IWebserviceDescriptor>();
		try
		{
			IResource[] members = folder.members();
			for (int i = 0; i < members.length; ++i)
			{
				if (members[i].getType() == IResource.FILE)
				{
					WebserviceDescriptor descriptor = new WebserviceDescriptor(this, (IFile)members[i]);
					if(validate)
					{
						try
						{
							descriptor.getWSDL();
						}
						catch(Exception ex)
						{
							continue;
						}
					}
					results.add(descriptor);
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.project.IDependencySet#createDependency(
	 *      java.lang.String, java.net.URL)
	 */
	public IWebserviceDescriptor createWebserviceDescriptor(String name, InputStream content)
	{
		IFile file = folder.getFile(name);
		try
		{
			if(!file.exists())
				file.create(content, true, null);
			else
				file.setContents(content, true, false, null);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
			return null;
		}
		processObjectEvent(new ObjectRefreshEvent(getObjectId()));
		refresh();
		return new WebserviceDescriptor(this, file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.project.IDependencySet#removeDependency(
	 *      org.eclipse.vtp.desktop.core.project.IDependency)
	 */
	public void deleteWebserviceDescriptor(IWebserviceDescriptor dependency)
	{
		IFile file = folder.getFile(dependency.getName());
		try
		{
			if (file.exists())
				file.delete(true, null);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		processObjectEvent(new ObjectRefreshEvent(getObjectId()));
		refresh();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass)
	{
		if(IResource.class.isAssignableFrom(adapterClass) && adapterClass.isAssignableFrom(folder.getClass()))
			return folder;
		if(WebserviceSet.class.isAssignableFrom(adapterClass))
			return this;
		return super.getAdapter(adapterClass);
	}

	public List<IWorkflowResource> getChildren()
	{
		return new LinkedList<IWorkflowResource>(getWebserviceDescriptors());
	}
	
	public IFolder getUnderlyingFolder()
	{
		return folder;
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof WebserviceSet)
		{
			return folder.equals(((WebserviceSet)obj).getUnderlyingFolder());
		}
		return false;
	}
	
	public int hashCode()
	{
		return folder.toString().hashCode();
	}
}
