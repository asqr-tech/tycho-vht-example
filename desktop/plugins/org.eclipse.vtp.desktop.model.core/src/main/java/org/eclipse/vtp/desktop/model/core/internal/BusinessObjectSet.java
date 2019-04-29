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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IBusinessObject;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectSet;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.framework.util.Guid;

/**
 * This is a concrete implementation of <code>IBusinessObjectSet</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class BusinessObjectSet extends WorkflowResource
	implements IBusinessObjectSet, IResourceChangeListener
{
	/**
	 * Constant template of the initial XML format definition of a business object.
	 */
	private static String businessObjectTemplate =
		"<business-object id=\"[id]\" name=\"[name]\">" + "\t<fields></fields>"
		+ "</business-object>";

	/**
	 * The application project that contains this business object set.
	 */
	private WorkflowProject project;

	/**
	 * The eclipse folder resource this business object set represents.
	 */
	private IFolder folder;
	
	private List<BusinessObject> currentObjects = new ArrayList<BusinessObject>();

	/**
	 * Creates a new <code>BusinessObjectSet</code> with the given parent
	 * application project and eclipse folder resource.
	 *
	 * @param project The parent application project
	 * @param folder The eclipse folder resource this business object set
	 * represents
	 */
	public BusinessObjectSet(WorkflowProject project, IFolder folder)
	{
		super();
		this.project = project;
		this.folder = folder;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		activateEvents();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getName()
	 */
	public String getName()
	{
		return folder.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.VoiceResource#getObjectId()
	 */
	protected String getObjectId()
	{
		return folder.getFullPath().toPortableString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getParent()
	 */
	public IWorkflowResource getParent()
	{
		return project;
	}

	public IBusinessObject getBusinessObject(String typeName)
	{
		List<IBusinessObject> businessObjects = getBusinessObjects();
		for(IBusinessObject businessObject : businessObjects)
		{
			if(businessObject.getName().equals(typeName))
				return businessObject;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IBusinessObjectSet#getBusinessObjects()
	 */
	public synchronized List<IBusinessObject> getBusinessObjects()
	{
		List<IBusinessObject> ret = new ArrayList<IBusinessObject>();
		if(currentObjects.isEmpty())
		{
			try
			{
				IResource[] res = folder.members();
	
				for(int i = 0; i < res.length; i++)
				{
					if(res[i] instanceof IFile)
					{
						BusinessObject bo = new BusinessObject(this, (IFile)res[i]);
						currentObjects.add(bo);
					}
				}
			}
			catch(CoreException e)
			{
				e.printStackTrace();
			}
			for(BusinessObject bo : currentObjects)
				bo.loadModel();
		}
		for(BusinessObject bo : currentObjects)
		{
			ret.add(bo);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IBusinessObjectSet#createBusinessObject(java.lang.String)
	 */
	public IBusinessObject createBusinessObject(String name)
		throws CoreException
	{
		IFile objectFile = folder.getFile(name + ".dod");

		if(objectFile.exists())
		{
			throw new IllegalArgumentException(
				"Business Object with that name already exists: " + name);
		}

		String template = new String(businessObjectTemplate);
		template = template.replaceAll("\\[id\\]", Guid.createGUID());
		template = template.replaceAll("\\[name\\]", name);
		objectFile.create(new ByteArrayInputStream(template.getBytes()), true,
			null);

		BusinessObject bo = new BusinessObject(this, objectFile);
		refresh();

		return bo;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IBusinessObjectSet#deleteBusinessObject(org.eclipse.vtp.desktop.core.project.IBusinessObject)
	 */
	public void deleteBusinessObject(IBusinessObject businessObject) throws CoreException
	{
		businessObject.getUnderlyingFile().delete(true, null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass)
	{
		if(IResource.class.isAssignableFrom(adapterClass) && adapterClass.isAssignableFrom(folder.getClass()))
			return folder;
		if(BusinessObjectSet.class.isAssignableFrom(adapterClass))
			return this;
		return super.getAdapter(adapterClass);
	}

	public List<IWorkflowResource> getChildren()
	{
		return new LinkedList<IWorkflowResource>(getBusinessObjects());
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof BusinessObjectSet)
		{
			return folder.equals(((BusinessObjectSet)obj).getAdapter(IFolder.class));
		}
		return false;
	}

	public int hashCode()
	{
		return folder.hashCode();
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event)
	{
		currentObjects.clear();
	}
}
