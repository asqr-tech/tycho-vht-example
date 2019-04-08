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

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A configuration for a database query.
 * 
 * @author Lonnie Pryor
 */
public class DatabaseQueryConfiguration implements IConfiguration,
		DatabaseConstants
{
	/** The name of the database to query. */
	private String database = ""; //$NON-NLS-1$
	/** The name of the table to query. */
	private String table = ""; //$NON-NLS-1$
	/** The name of the result to populate. */
	private String resultName = ""; //$NON-NLS-1$
	/** The name of the type of result to create. */
	private String resultType = null;
	/** True if multiple records should be returned. */
	private boolean resultArray = true;
	private boolean resultSecured = false;
	/** The maximum number of records to return. */
	private int resultLimit = 0;
	/** The criteria specified for the query. */
	private final Set<DatabaseCriteriaConfiguration> criteria = new LinkedHashSet<DatabaseCriteriaConfiguration>(); //$NON-NLS-1$
	/** The mappings specified to the query. */
	private final Set<DatabaseMappingConfiguration> mappings = new LinkedHashSet<DatabaseMappingConfiguration>();
	/** The maximum number of seconds to wait for the query. */
	private int queryTimeout = 0;

	/**
	 * Creates a new DatabaseQueryConfiguration.
	 */
	public DatabaseQueryConfiguration()
	{
	}

	/**
	 * Returns the name of the database to query.
	 * 
	 * @return The name of the database to query.
	 */
	public String getDatabase()
	{
		return database;
	}

	/**
	 * Sets the name of the database to query.
	 * 
	 * @param database The name of the database to query.
	 */
	public void setDatabase(String database)
	{
		this.database = database == null ? "" : database; //$NON-NLS-1$
	}

	/**
	 * Returns the name of the table to query.
	 * 
	 * @return The name of the table to query.
	 */
	public String getTable()
	{
		return table;
	}

	/**
	 * Sets the name of the table to query.
	 * 
	 * @param table The name of the table to query.
	 */
	public void setTable(String table)
	{
		this.table = table == null ? "" : table; //$NON-NLS-1$
	}

	/**
	 * Returns the name of the result to populate.
	 * 
	 * @return The name of the result to populate.
	 */
	public String getResultName()
	{
		return resultName;
	}

	/**
	 * Sets the name of the result to populate.
	 * 
	 * @param resultName The name of the result to populate.
	 */
	public void setResultName(String resultName)
	{
		this.resultName = resultName == null ? "" : resultName; //$NON-NLS-1$
	}

	/**
	 * Returns the name of the type of result to create.
	 * 
	 * @return The name of the type of result to create.
	 */
	public String getResultType()
	{
		return resultType;
	}

	/**
	 * Sets the name of the type of result to create.
	 * 
	 * @param resultType The name of the type of result to create.
	 */
	public void setResultType(String resultType)
	{
		this.resultType = resultType;
	}

	/**
	 * Returns true if multiple records should be returned.
	 * 
	 * @return True if multiple records should be returned.
	 */
	public boolean isResultArray()
	{
		return resultArray;
	}

	/**
	 * Changes the multiple records setting.
	 * 
	 * @param resultArray True if multiple records should be returned.
	 */
	public void setResultArray(boolean resultArray)
	{
		this.resultArray = resultArray;
	}
	
	public boolean isResultSecured()
	{
		return this.resultSecured;
	}
	
	public void setResultSecured(boolean resultSecured)
	{
		this.resultSecured = resultSecured;
	}

	/**
	 * Returns the maximum number of records to return.
	 * 
	 * @return The maximum number of records to return.
	 */
	public int getResultLimit()
	{
		return resultLimit;
	}

	/**
	 * Sets the maximum number of records to return.
	 * 
	 * @param resultLimit The maximum number of records to return.
	 */
	public void setResultLimit(int resultLimit)
	{
		this.resultLimit = resultLimit < 0 ? 0 : resultLimit;
	}

	/**
	 * Returns the criteria specified for the query.
	 * 
	 * @return The criteria specified for the query.
	 */
	public DatabaseCriteriaConfiguration[] getCriteria()
	{
		return criteria.toArray(new DatabaseCriteriaConfiguration[criteria.size()]);
	}

	/**
	 * Adds a criteria to this query.
	 * 
	 * @param criteria The criteria to add.
	 */
	public void addCriteria(DatabaseCriteriaConfiguration criteria)
	{
		if (criteria != null)
			this.criteria.add(criteria);
	}

	/**
	 * Removes a criteria from this query.
	 * 
	 * @param criteria The criteria to remove.
	 */
	public void removeCriteria(DatabaseCriteriaConfiguration criteria)
	{
		if (criteria != null)
			this.criteria.remove(criteria);
	}

	/**
	 * Returns the mappings specified to the query.
	 * 
	 * @return The mappings specified to the query.
	 */
	public DatabaseMappingConfiguration[] getMappings()
	{
		return mappings.toArray(new DatabaseMappingConfiguration[mappings.size()]);
	}

	/**
	 * Adds a mapping to this query.
	 * 
	 * @param mapping The mapping to add.
	 */
	public void addMapping(DatabaseMappingConfiguration mapping)
	{
		if (mapping != null)
			this.mappings.add(mapping);
	}

	/**
	 * Removes a mapping from this query.
	 * 
	 * @param mapping The mapping to remove.
	 */
	public void removeMapping(DatabaseMappingConfiguration mapping)
	{
		if (mapping != null)
			this.mappings.remove(mapping);
	}

	/**
	 * Returns the maximum number of seconds to wait for the query.
	 * 
	 * @return The maximum number of seconds to wait for the query.
	 */
	public int getQueryTimeout()
	{
		return queryTimeout;
	}

	/**
	 * Sets the maximum number of seconds to wait for the query.
	 * 
	 * @param queryTimeout The maximum number of seconds to wait for the query.
	 */
	public void setQueryTimeout(int queryTimeout)
	{
		this.queryTimeout = Math.max(0, queryTimeout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(
	 *      org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		database = configurationElement.getAttribute(NAME_DATABASE);
		table = configurationElement.getAttribute(NAME_TABLE);
		resultName = configurationElement.getAttribute(NAME_RESULT_NAME);
		if (configurationElement.hasAttribute(NAME_RESULT_TYPE))
			resultType = configurationElement.getAttribute(NAME_RESULT_TYPE);
		else
			resultType = null;
		resultSecured = Boolean.parseBoolean(configurationElement.getAttribute(NAME_SECURED));
		resultArray = "many".equalsIgnoreCase(configurationElement //$NON-NLS-1$
				.getAttribute(NAME_RESULT_CARDINALITY));
		if (configurationElement.hasAttribute(NAME_RESULT_LIMIT))
			resultLimit = Integer.parseInt(configurationElement
					.getAttribute(NAME_RESULT_LIMIT));
		else
			resultLimit = 0;
		criteria.clear();
		NodeList list = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_CRITERIA);
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element)list.item(i);
			DatabaseCriteriaConfiguration item = new DatabaseCriteriaConfiguration();
			item.load(element);
			criteria.add(item);
		}
		mappings.clear();
		list = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_MAPPING);
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element)list.item(i);
			DatabaseMappingConfiguration item = new DatabaseMappingConfiguration();
			item.load(element);
			mappings.add(item);
		}
		if (configurationElement.hasAttribute(NAME_TIMEOUT))
			setQueryTimeout(Integer.parseInt(configurationElement
					.getAttribute(NAME_TIMEOUT)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(
	 *      org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute(NAME_DATABASE, database);
		configurationElement.setAttribute(NAME_TABLE, table);
		configurationElement.setAttribute(NAME_RESULT_NAME, resultName);
		if (resultType != null)
			configurationElement.setAttribute(NAME_RESULT_TYPE, resultType);
		configurationElement.setAttribute(NAME_SECURED, Boolean.toString(resultSecured));
		if (resultArray)
			configurationElement.setAttribute(NAME_RESULT_CARDINALITY, "many"); //$NON-NLS-1$
		if (resultLimit > 0)
			configurationElement.setAttribute(NAME_RESULT_LIMIT, String
					.valueOf(resultLimit));
		String criteriaName = NAME_CRITERIA;
		String mappingName = NAME_MAPPING;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
		{
			criteriaName = prefix + ":" + criteriaName; //$NON-NLS-1$
			mappingName = prefix + ":" + mappingName; //$NON-NLS-1$
		}
		for (DatabaseCriteriaConfiguration item : criteria)
		{
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, criteriaName);
			item.save(element);
			configurationElement.appendChild(element);
		}
		for (DatabaseMappingConfiguration item : mappings)
		{
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, mappingName);
			item.save(element);
			configurationElement.appendChild(element);
		}
		if (queryTimeout != 0)
			configurationElement.setAttribute(NAME_TIMEOUT, String
					.valueOf(queryTimeout));
	}
}
