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
package org.eclipse.vtp.framework.databases;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.vtp.framework.common.IDataType;

/**
 * IDatabase.
 *
 * @author Lonnie Pryor
 */
public interface IDatabase
{
	String getName();
	
	String[] getTableNames();
	
	String[] getColumnNames(String tableName);
	
	IDataType getColumnType(String tableName, String columnName);
	
	/**
	 * Returns a connection to this database.
	 * 
	 * @return A connection to this database.
	 * @throws SQLException If the connection cannot be created.
	 */
	Connection getConnection() throws SQLException;
}
