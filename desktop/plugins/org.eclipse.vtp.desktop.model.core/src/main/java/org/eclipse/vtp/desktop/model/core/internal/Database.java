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
import org.eclipse.vtp.desktop.model.core.IDatabaseTable;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;

/**
 * This is a concrete implementation of <code>IDatabase</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class Database extends WorkflowResource implements IDatabase
{
	/**
	 * Constant string template for the XML format of the database table definition
	 * document.
	 */
	private static final String tableTemplate =
		"<database-table name=\"[name]\">" + "<columns></columns>"
		+ "</database-table>";

	/**
	 * The parent database set of this database folder resource.
	 */
	DatabaseSet databaseSet;

	/**
	 * The eclipse folder resource this database represents.
	 */
	IFolder folder;

	/**
	 * The name of this database.
	 */
	String name;

	/**
	 * Creates a new <code>Database</code> with the given parent
	 * database set and eclipse folder resource.
	 *
	 * @param databaseSet The parent database set
	 * @param folder The eclipse folder resource this database represents
	 */
	public Database(DatabaseSet databaseSet, IFolder folder)
	{
		super();
		this.databaseSet = databaseSet;
		this.folder = folder;
		name = folder.getName();
		activateEvents();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.VoiceResource#getObjectId()
	 */
	protected String getObjectId()
	{
		return folder.getFullPath().toPortableString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IDatabase#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IDatabase#getTables()
	 */
	public List<IDatabaseTable> getTables()
	{
		List<IDatabaseTable> ret = new ArrayList<IDatabaseTable>();

		try
		{
			IResource[] res = folder.members();

			for(int i = 0; i < res.length; i++)
			{
				if(res[i] instanceof IFile)
				{
					IFile file = (IFile)res[i];

					if((file.getFileExtension() != null)
							&& file.getFileExtension().equalsIgnoreCase("dbt"))
					{
						DatabaseTable table = new DatabaseTable(this, file);
						ret.add(table);
					}
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
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getParent()
	 */
	public IWorkflowResource getParent()
	{
		return databaseSet;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IDatabase#createTable(java.lang.String)
	 */
	public IDatabaseTable createTable(String name) throws CoreException
	{
		IFile databaseTableFile = folder.getFile(name + ".dbt");

		if(databaseTableFile.exists())
		{
			throw new IllegalArgumentException(
				"A Database table with that name already exists: " + name);
		}

		String template = new String(tableTemplate);
		template = template.replaceAll("\\[name\\]", name);
		//CGI.searchAndReplace(template, "[name]", name);
		databaseTableFile.create(new ByteArrayInputStream(
				template.toString().getBytes()), true, null);

		DatabaseTable database = new DatabaseTable(this, databaseTableFile);

		return database;
	}
	
	public IFolder getUnderlyingFolder()
	{
		return folder;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.projects.core.IDatabase#delete()
	 */
	public void delete() throws CoreException
	{
		folder.delete(false, null);
		databaseSet.refresh();
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof Database)
		{
			return folder.equals(((Database)obj).getUnderlyingFolder());
		}
		return false;
	}
	
	public int hashCode()
	{
		return folder.toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass)
	{
		if(IResource.class.isAssignableFrom(adapterClass) && adapterClass.isAssignableFrom(folder.getClass()))
			return folder;
		if(Database.class.isAssignableFrom(adapterClass))
			return this;
		return super.getAdapter(adapterClass);
	}

	public List<IWorkflowResource> getChildren()
	{
		return new ArrayList<IWorkflowResource>(getTables());
	}
}
