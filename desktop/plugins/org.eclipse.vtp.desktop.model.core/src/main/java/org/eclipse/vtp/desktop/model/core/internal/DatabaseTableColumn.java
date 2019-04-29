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

import org.eclipse.vtp.desktop.model.core.ColumnType;
import org.eclipse.vtp.desktop.model.core.IDatabaseTableColumn;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;

/**
 * This is a concrete implementation of <code>IDatabaseTableColumn</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class DatabaseTableColumn extends WorkflowResource
	implements IDatabaseTableColumn
{
	/**
	 * The database table this column is defined for.
	 */
	DatabaseTable table;

	/**
	 * The name of this database table column.
	 */
	String name;

	/**
	 * The data type definition of this database table column.
	 */
	ColumnType columnType;

	/**
	 * Creates a new <code>DatabaseTableColumn</code> in the given database
	 * table with the provided name and column type definition.
	 *
	 * @param table The database table that will contain this column
	 * @param name The name of this database table column
	 * @param columnType The data type defintion of this column
	 */
	public DatabaseTableColumn(DatabaseTable table, String name,
		ColumnType columnType)
	{
		super();
		this.table = table;
		this.name = name;
		this.columnType = columnType;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.VoiceResource#getObjectId()
	 */
	protected String getObjectId()
	{
		return table.getObjectId() + "#" + name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IDatabaseTableColumn#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IDatabaseTableColumn#getDataType()
	 */
	public ColumnType getColumnType()
	{
		return columnType;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getParent()
	 */
	public IWorkflowResource getParent()
	{
		return table;
	}
}
