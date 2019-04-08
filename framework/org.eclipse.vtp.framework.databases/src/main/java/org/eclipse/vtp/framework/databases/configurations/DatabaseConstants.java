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
package org.eclipse.vtp.framework.databases.configurations;

/**
 * Configuration constants for the database extensions.
 * 
 * @author Lonnie Pryor
 */
public interface DatabaseConstants
{
	/** The name space URI of the database configuration objects. */
	String NAMESPACE_URI = //
	"http://eclipse.org/vtp/xml/framework/databases/configurations"; //$NON-NLS-1$

	/** The "criteria" name constant. */
	String NAME_CRITERIA = "criteria"; //$NON-NLS-1$
	/** The "column" name constant. */
	String NAME_COLUMN = "column"; //$NON-NLS-1$
	/** The "comparison" name constant. */
	String NAME_COMPARISON = "comparison"; //$NON-NLS-1$
	/** The "database" name constant. */
	String NAME_DATABASE = "database"; //$NON-NLS-1$
	/** The "driver" name constant. */
	String NAME_DRIVER = "driver"; //$NON-NLS-1$
	/** The "jdbc-database" name constant. */
	String NAME_JDBC_DATABASE = "jdbc-database"; //$NON-NLS-1$
	/** The "jndi-database" name constant. */
	String NAME_JNDI_DATABASE = "jndi-database"; //$NON-NLS-1$
	/** The "mapping" name constant. */
	String NAME_MAPPING = "mapping"; //$NON-NLS-1$
	/** The "name" name constant. */
	String NAME_NAME = "name"; //$NON-NLS-1$
	/** The "password" name constant. */
	String NAME_PASSWORD = "password"; //$NON-NLS-1$
	/** The "query" name constant. */
	String NAME_QUERY = "query"; //$NON-NLS-1$
	/** The "result-cardinality" name constant. */
	String NAME_RESULT_CARDINALITY = "result-cardinality"; //$NON-NLS-1$
	/** The "result-name" name constant. */
	String NAME_RESULT_NAME = "result-name"; //$NON-NLS-1$
	/** The "result-limit" name constant. */
	String NAME_RESULT_LIMIT = "result-limit"; //$NON-NLS-1$
	/** The "result-type" name constant. */
	String NAME_RESULT_TYPE = "result-type"; //$NON-NLS-1$
	/** The "table" name constant. */
	String NAME_TABLE = "table"; //$NON-NLS-1$
	/** The "timeout" name constant. */
	String NAME_TIMEOUT = "timeout"; //$NON-NLS-1$
	/** The "type" name constant. */
	String NAME_TYPE = "type"; //$NON-NLS-1$
	/** The "uri" name constant. */
	String NAME_URI = "uri"; //$NON-NLS-1$
	/** The "url" name constant. */
	String NAME_URL = "url"; //$NON-NLS-1$
	/** The "username" name constant. */
	String NAME_USERNAME = "username"; //$NON-NLS-1$
	/** The "value" name constant. */
	String NAME_VALUE = "value"; //$NON-NLS-1$
	String NAME_SECURED = "secured";
}
