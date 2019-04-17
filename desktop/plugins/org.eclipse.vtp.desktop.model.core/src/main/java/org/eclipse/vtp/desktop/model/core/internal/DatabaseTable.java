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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.ColumnType;
import org.eclipse.vtp.desktop.model.core.IDatabaseTable;
import org.eclipse.vtp.desktop.model.core.IDatabaseTableColumn;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.event.ReloadObjectDataEvent;
import org.eclipse.vtp.desktop.model.core.internal.event.ObjectEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This is a concrete implementation of <code>IDatabaseTable</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class DatabaseTable extends WorkflowResource implements IDatabaseTable
{
	/**
	 * The parent database.
	 */
	Database database;

	/**
	 * The eclipse file resource that contains the definition of the
	 * coloumns used in this database table.
	 */
	IFile file;

	/**
	 * The name of this database table.
	 */
	String name;

	/**
	 * The list of <code>IDatabaseTablecColumn</code>s defined for this
	 * database table.
	 */
	List<DatabaseTableColumn> columns;

	/**
	 * Creates a new <code>DatabaseTable</code> with the given parent
	 * database and eclipse file resource.
	 *
	 * @param database The parent database
	 * @param file The eclipse file resource that contains the definition
	 * of table columns
	 */
	public DatabaseTable(Database database, IFile file)
	{
		super();
		this.database = database;
		this.file = file;
		activateEvents();
		loadModel();
	}

	/**
	 * Initializes the member variables and column definitions of this
	 * database table from the information stored in the file resource.
	 */
	private void loadModel()
	{
		try
		{
			Document document =
				DocumentBuilderFactory.newInstance().newDocumentBuilder()
									  .parse(file.getContents());
			Element rootElement = document.getDocumentElement();
			name = rootElement.getAttribute("name");
			columns = new ArrayList<DatabaseTableColumn>();

			NodeList nl = rootElement.getElementsByTagName("column");

			for(int i = 0; i < nl.getLength(); i++)
			{
				Element fieldElement = (Element)nl.item(i);
				String fieldName = fieldElement.getAttribute("name");
				NodeList dtnl =
					fieldElement.getElementsByTagName("column-type");

				if(dtnl.getLength() > 0)
				{
					ColumnType fieldDataType =
						ColumnType.load((Element)dtnl.item(0));
					DatabaseTableColumn dtc =
						new DatabaseTableColumn(this, fieldName, fieldDataType);
					columns.add(dtc);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(FactoryConfigurationError e)
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.VoiceResource#getObjectId()
	 */
	protected String getObjectId()
	{
		return file.getFullPath().toPortableString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IDatabaseTable#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IDatabaseTable#getColumns()
	 */
	public List<IDatabaseTableColumn> getColumns()
	{
		return new ArrayList<IDatabaseTableColumn>(columns);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getParent()
	 */
	public IWorkflowResource getParent()
	{
		return database;
	}

	/**
	 * Requests that the contents of the column definition resource
	 * be replace with the data provided by the given input stream.
	 *
	 * @param source The input stream containing the new contents.
	 * @throws CoreException If an error occured during the file
	 * update
	 */
	public void write(InputStream source) throws CoreException
	{
		file.setContents(source, true, true, null);
		WorkflowCore.getDefault()
						 .postObjectEvent(new ReloadObjectDataEvent(
				getObjectId()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.event.ObjectListener#processObjectEvent(org.eclipse.vtp.desktop.core.project.internals.event.ObjectEvent)
	 */
	public void processObjectEvent(ObjectEvent event)
	{
		if(event instanceof ReloadObjectDataEvent)
		{
			loadModel();
			refresh();
		}
		else
		{
			super.processObjectEvent(event);
		}
	}

	public void delete() throws CoreException
	{
		file.delete(false, null);
		database.refresh();
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof DatabaseTable)
		{
			return file.equals(((DatabaseTable)obj).getUnderlyingFile());
		}
		return false;
	}
	
	public int hashCode()
	{
		return file.toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass)
	{
		if(IResource.class.isAssignableFrom(adapterClass) && adapterClass.isAssignableFrom(file.getClass()))
			return file;
		if(DatabaseTable.class.isAssignableFrom(adapterClass))
			return this;
		return super.getAdapter(adapterClass);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.projects.core.IDatabaseTable#getUnderlyingFile()
	 */
	public IFile getUnderlyingFile()
	{
		return file;
	}
}
