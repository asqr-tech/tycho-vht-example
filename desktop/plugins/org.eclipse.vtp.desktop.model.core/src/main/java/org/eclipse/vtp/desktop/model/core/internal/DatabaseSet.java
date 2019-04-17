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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IDatabase;
import org.eclipse.vtp.desktop.model.core.IDatabaseSet;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;

/**
 * This is a concrete implementation of <code>IDatabaseSet</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class DatabaseSet extends WorkflowResource implements IDatabaseSet
{
	/**
	 * The parent application project.
	 */
	private WorkflowProject project;

	/**
	 * The eclipse folder resource this database set represents.
	 */
	private IFolder folder;

	/**
	 * Creates a new <code>DatabaseSet</code> with the given application
	 * project and eclipse folder resource.
	 *
	 * @param project The parent application project
	 * @param folder The eclipse folder resource this database set represents
	 */
	public DatabaseSet(WorkflowProject project, IFolder folder)
	{
		super();
		this.project = project;
		this.folder = folder;
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

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IDatabaseSet#getDatabases()
	 */
	public List<IDatabase> getDatabases()
	{
		List<IDatabase> ret = new ArrayList<IDatabase>();

		try
		{
			IResource[] res = folder.members();

			for(int i = 0; i < res.length; i++)
			{
				if(res[i] instanceof IFolder
						&& !res[i].getName().startsWith("."))
				{
					Database database = new Database(this, (IFolder)res[i]);
					ret.add(database);
				}
			}
		}
		catch(CoreException e)
		{
			e.printStackTrace();
		}

		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IDatabaseSet#createDatabase(java.lang.String)
	 */
	public IDatabase createDatabase(String name) throws CoreException
	{
		IFolder databaseFolder = folder.getFolder(name);

		if(databaseFolder.exists())
		{
			throw new IllegalArgumentException(
				"A Database with that name already exists: " + name);
		}

		databaseFolder.create(true, false, null);
		IFile workaround = databaseFolder.getFile(".gitworkaround");
		workaround.create(new ByteArrayInputStream("This is a workaround for Git's lack of empty directory support.".getBytes()), true, null);

		Database database = new Database(this, databaseFolder);
		refresh();

		return database;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IDatabaseSet#deleteDatabase(org.eclipse.vtp.desktop.core.project.IDatabase)
	 */
	public void deleteDatabase(IDatabase database)
	{
	}
	
	public IFolder getUnderlyingFolder()
	{
		return folder;
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof DatabaseSet)
		{
			return folder.equals(((DatabaseSet)obj).getUnderlyingFolder());
		}
		return false;
	}
	
	public int hashCode()
	{
		return folder.toString().hashCode();
	}

	public List<IWorkflowResource> getChildren()
	{
		return new ArrayList<IWorkflowResource>(getDatabases());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass)
	{
		if(IResource.class.isAssignableFrom(adapterClass) && adapterClass.isAssignableFrom(folder.getClass()))
			return folder;
		if(DatabaseSet.class.isAssignableFrom(adapterClass))
			return this;
		return super.getAdapter(adapterClass);
	}

}
