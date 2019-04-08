/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods), 
 *    Randy Childers (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.databases.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.vtp.framework.common.IArrayObject;
import org.eclipse.vtp.framework.common.IBooleanObject;
import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IDataType;
import org.eclipse.vtp.framework.common.IDataTypeRegistry;
import org.eclipse.vtp.framework.common.IDateObject;
import org.eclipse.vtp.framework.common.IDecimalObject;
import org.eclipse.vtp.framework.common.INumberObject;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.databases.IDatabase;
import org.eclipse.vtp.framework.databases.IDatabaseRegistry;
import org.eclipse.vtp.framework.databases.configurations.DatabaseCriteriaConfiguration;
import org.eclipse.vtp.framework.databases.configurations.DatabaseMappingConfiguration;
import org.eclipse.vtp.framework.databases.configurations.DatabaseQueryConfiguration;

/**
 * DatabaseQueryAction.Mapping
 * 
 * @author Lonnie Pryor
 */
public class DatabaseQueryAction implements IAction
{
	/** The context to use. */
	private final IActionContext context;
	/** The variable registry to use. */
	private final IVariableRegistry variables;
	/** The database to use. */
	private final IDatabase database;
	/** The table to use. */
	private final String table;
	/** The name of the result to populate. */
	private final String resultName;
	/** The name of the type of result to create. */
	private final IDataType arrayType;
	/** The name of the type of result to create. */
	private final IDataType resultType;
	/** The maximum number of records to return. */
	private final int resultLength;
	/** The database to use. */
	private final Map<String, IDataType> columns;
	/** The criteria to apply. */
	private final Set<Criteria> criteria;
	/** The mappings to apply. */
	private final Set<Mapping> mappings;
	/** The maximum number of seconds to wait for the query. */
	private final int queryTimeout;

	/**
	 * Creates a new DatabaseQueryAction.
	 * 
	 * @param context The context to use.
	 * @param types The data type registry to use.
	 * @param variables The variable registry to use.
	 * @param databases The database registry to use.
	 * @param configuration The configuration of the query to execute.
	 */
	public DatabaseQueryAction(IActionContext context, IDataTypeRegistry types,
			IVariableRegistry variables, IDatabaseRegistry databases,
			DatabaseQueryConfiguration configuration)
	{
		this.context = context;
		this.variables = variables;
		this.database = databases.getDatabase(configuration.getDatabase());
		this.table = configuration.getTable();
		this.resultName = configuration.getResultName();
		this.arrayType = types.getDataType(IArrayObject.TYPE_NAME);
		this.resultType = types.getDataType(configuration.getResultType());
		if (configuration.isResultArray())
			this.resultLength = configuration.getResultLimit() <= 0 ? Integer.MAX_VALUE
					: configuration.getResultLimit();
		else
			this.resultLength = 0;
		String[] columnNames = database.getColumnNames(table);
		Map<String, IDataType> columns = new LinkedHashMap<String, IDataType>(columnNames.length);
		for (int i = 0; i < columnNames.length; ++i)
			columns
					.put(columnNames[i], database.getColumnType(table, columnNames[i]));
		this.columns = Collections.unmodifiableMap(columns);
		DatabaseCriteriaConfiguration[] criteriaConfigurations = configuration
				.getCriteria();
		Set<Criteria> criteria = new LinkedHashSet<Criteria>(criteriaConfigurations.length);
		for (int i = 0; i < criteriaConfigurations.length; ++i)
			criteria.add(new Criteria(criteriaConfigurations[i]));
		this.criteria = Collections.unmodifiableSet(criteria);
		DatabaseMappingConfiguration[] mappingConfigurations = configuration
				.getMappings();
		Set<Mapping> mappings = new LinkedHashSet<Mapping>(mappingConfigurations.length);
		for (int i = 0; i < mappingConfigurations.length; ++i)
			mappings.add(new Mapping(mappingConfigurations[i]));
		this.mappings = Collections.unmodifiableSet(mappings);
		queryTimeout = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IAction#execute()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IActionResult execute()
	{
		Dictionary props = new Hashtable();
		props.put("event", "dbquery.prepare");
		props.put("dbquery.databse", database);
		props.put("dbquery.table", table);
		props.put("dbquery.target", resultName);
		context.report(IReporter.SEVERITY_INFO, "Preparing SQL statement.", props);
		IActionResult result = null;
		try
		{
			Connection connection = database.getConnection();
			if (connection != null)
			{
				try
				{
					PreparedStatement statement = buildStatement(connection);
					if (statement != null)
					{
						try
						{
							ResultSet resultSet = statement.executeQuery();
							if (resultSet != null)
							{
								try
								{
									result = processResults(resultSet);
								}
								finally
								{
									resultSet.close();
								}
							}
						}
						finally
						{
							statement.close();
						}
					}
				}
				finally
				{
					connection.close();
				}
			}
			props = new Hashtable((Hashtable)props);
			props.put("event", "dbquery.complete");
			context.report(IReporter.SEVERITY_INFO, "SQL query complete.", props);
		}
		catch (SQLException e)
		{
			if (result == null)
				result = context.createResult("error.database.connection", e); //$NON-NLS-1$
		}
		if (result == null)
			context.createResult("error.database.connection"); //$NON-NLS-1$
		return result;
	}

	/**
	 * Builds the query on the supplied connection.
	 * 
	 * @param connection The connection to build the query on.
	 * @throws SQLException If the statement cannot be created.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private PreparedStatement buildStatement(Connection connection)
			throws SQLException
	{
		StringBuffer buffer = new StringBuffer("select"); //$NON-NLS-1$
		int length = buffer.length();
		for (Mapping mapping : mappings)
		{
			if (buffer.length() > length)
				buffer.append(',');
			mapping.appendSql(buffer);
		}
		if (buffer.length() == length)
			buffer.append(' ').append('*');
		buffer.append(" from ").append(table); //$NON-NLS-1$
		boolean criteriaIsSecured = false;
		if (!criteria.isEmpty())
		{
			buffer.append(" where "); //$NON-NLS-1$
			length = buffer.length();
			for (Criteria criteriaElement : criteria)
			{
				criteriaElement.appendSql(buffer, buffer.length() > length);
				criteriaIsSecured |= criteriaElement.secured;
			}
			if (buffer.length() == length)
				buffer.setLength(length - 7);
		}
		// buffer.append(';');
		Dictionary props = new Hashtable();
		props.put("event", "dbquery.execute");
		props.put("dbquery.databse", database);
		props.put("dbquery.table", table);
		props.put("dbquery.target", resultName);
		props.put("dbquery.sql", criteriaIsSecured ? "**Secured**" : buffer.toString());
		context.report(IReporter.SEVERITY_INFO, "Executing SQL statement: "
				+ (criteriaIsSecured ? "**Secured**" : buffer.toString()), props);
		PreparedStatement statement = connection
				.prepareStatement(buffer.toString());
		int index = 1;
		for (Criteria criteriaElement : criteria)
			index = criteriaElement.setValue(statement, index);
		statement.setQueryTimeout(queryTimeout);
		return statement;
	}

	/**
	 * Process the results of a query.
	 * 
	 * @param resultSet The results to process.
	 * @return The result of the action.
	 * @throws SQLException If the results cannot be processed.
	 */
	private IActionResult processResults(ResultSet resultSet) throws SQLException
	{
		IArrayObject array = null;
		IDataObject variable = variables.getVariable(resultName);
		if (resultLength == 0)
		{
			if (variable == null || !resultType.equals(variable.getType()))
				variables.setVariable(resultName, variable = variables
						.createVariable(resultType));
		}
		else
		{
			if (variable == null || !arrayType.equals(variable.getType()))
				variables.setVariable(resultName, array = (IArrayObject)variables
						.createVariable(arrayType));
			else
				array = (IArrayObject)variable;
			variable = null;
		}
		int resultCount = 0;
		for (int i = 0; (resultLength == 0 || i < resultLength) && resultSet.next(); ++i)
		{
			resultCount++;
			if (variable == null)
				variable = variables.createVariable(resultType);
			for (Mapping mapping : mappings)
				mapping.setValue(variable, resultSet);
			if (array == null)
				break;
			array.addElement(variable);
			variable = null;
		}
		if (resultCount == 0 && array == null)
		{
			variables.clearVariable(resultName);
		}
		return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
	}

	/**
	 * A criteria that narrows the results of a query.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class Criteria
	{
		/** The name of the column. */
		final String name;
		/** The comparison to perform. */
		final String comparison;
		/** The type of the column. */
		final IDataType type;
		/** The value to compare against. */
		final IDataObject value;
		boolean secured = false;

		/**
		 * Creates a new Criteria.
		 * 
		 * @param configuration The configuration for this criteria.
		 */
		Criteria(DatabaseCriteriaConfiguration configuration)
		{
			name = configuration.getName();
			switch (configuration.getComparison())
			{
			case DatabaseCriteriaConfiguration.COMPARISON_NOT_EQUAL:
				comparison = " != "; //$NON-NLS-1$
				break;
			case DatabaseCriteriaConfiguration.COMPARISON_LESS_THAN:
				comparison = " < "; //$NON-NLS-1$
				break;
			case DatabaseCriteriaConfiguration.COMPARISON_LESS_THAN_OR_EQUAL:
				comparison = " <= "; //$NON-NLS-1$
				break;
			case DatabaseCriteriaConfiguration.COMPARISON_GREATER_THAN:
				comparison = " > "; //$NON-NLS-1$
				break;
			case DatabaseCriteriaConfiguration.COMPARISON_GREATER_THAN_OR_EQUAL:
				comparison = " >= "; //$NON-NLS-1$
				break;
			case DatabaseCriteriaConfiguration.COMPARISON_EQUAL:
				comparison = " = "; //$NON-NLS-1$
				break;
			default:
				comparison = null;
			}
			type = columns.get(name);
			String configuredValue = configuration.getValue();
			IDataObject value = null;
			switch (configuration.getType())
			{
			case DatabaseCriteriaConfiguration.TYPE_VARIABLE:
				value = variables.getVariable(configuredValue);
				if (value != null)
				{
					if (value.getType().equals(type))
						break;
					else
						configuredValue = value.toString();
				}
			case DatabaseCriteriaConfiguration.TYPE_STATIC:
				value = variables.createVariable(type);
				if (value instanceof IBooleanObject)
					((IBooleanObject)value).setValue(configuredValue);
				else if (value instanceof IDateObject)
					((IDateObject)value).setValue(configuredValue);
				else if (value instanceof IDecimalObject)
					((IDecimalObject)value).setValue(configuredValue);
				else if (value instanceof INumberObject)
					((INumberObject)value).setValue(configuredValue);
				else if (value instanceof IStringObject)
					((IStringObject)value).setValue(configuredValue);
				else
					value = null;
				break;
			}
			this.value = value;
		}

		/**
		 * Appends the SQL this criteria represents.
		 * 
		 * @param sql The buffer to append SQL to.
		 */
		void appendSql(StringBuffer sql, boolean and)
		{
			if (comparison == null || value == null)
				return;
			if (and)
				sql.append(" and "); //$NON-NLS-1$
			sql.append(name).append(comparison).append('?');
		}

		/**
		 * Sets the value sent to the database for a query.
		 * 
		 * @param statement The statement to configure.
		 * @param index The index to configure at.
		 * @return The next index to configure at.
		 * @throws SQLException If the statement cannot be configured.
		 */
		int setValue(PreparedStatement statement, int index) throws SQLException
		{
			if (comparison == null || value == null)
				return index;
			if (value instanceof IBooleanObject)
				statement.setBoolean(index, ((IBooleanObject)value).getValue()
						.booleanValue());
			else if (value instanceof IDateObject)
				statement.setTimestamp(index, new Timestamp(((IDateObject)value)
						.getValue().getTime().getTime()));
			else if (value instanceof IDecimalObject)
				statement.setBigDecimal(index, ((IDecimalObject)value).getValue());
			else if (value instanceof INumberObject)
				statement.setInt(index, ((INumberObject)value).getValue().intValue());
			else if (value instanceof IStringObject)
				statement.setString(index, ((IStringObject)value).getValue());
			else
				return index;
			context.info("Using parameter: " + (value.isSecured() ? "**Secured**" : value.toString()));
			secured = value.isSecured();
			return index + 1;
		}
	}

	/**
	 * A mapping that handles the results of a query.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class Mapping
	{
		/** The name of the column. */
		final String name;
		/** The type of the column. */
		final IDataType type;
		/** The value to compare against. */
		final String value;

		/**
		 * Creates a new Mapping.
		 * 
		 * @param configuration The configuration for this mapping.
		 */
		Mapping(DatabaseMappingConfiguration configuration)
		{
			name = configuration.getName();
			if (configuration.getType() == DatabaseMappingConfiguration.TYPE_COLUMN)
				type = columns.get(configuration.getValue());
			else
				type = null;
			this.value = configuration.getValue();
		}

		/**
		 * Appends the SQL this criteria represents.
		 * 
		 * @param sql The buffer to append SQL to.
		 */
		void appendSql(StringBuffer sql)
		{
			if (type == null)
				return;
			sql.append(' ').append(value);
		}

		/**
		 * Sets the value returned from the database on a variable.
		 * 
		 * @param statement The statement to configure.
		 * @throws SQLException If the results cannot be read.
		 */
		void setValue(IDataObject object, ResultSet resultSet) throws SQLException
		{
			IDataObject target = object;
			if (target.getType().isComplexType())
				target = target.getField(name);
			Object data = value;
			if (type != null)
				data = resultSet.getObject(value);
			if (target instanceof IBooleanObject)
				((IBooleanObject)target).setValue(data);
			else if (target instanceof IDateObject)
			{
				if (data instanceof java.sql.Date)
					((IDateObject)target).setValue(new Date(((java.sql.Date)data)
							.getTime()));
				else if (data instanceof Time)
					((IDateObject)target).setValue(new Date(((Time)data).getTime()));
				else if (data instanceof Timestamp)
					((IDateObject)target).setValue(new Date(((Timestamp)data).getTime()));
				else
					((IDateObject)target).setValue(data);
			}
			else if (target instanceof IDecimalObject)
				((IDecimalObject)target).setValue(data);
			else if (target instanceof INumberObject)
				((INumberObject)target).setValue(data);
			else if (target instanceof IStringObject)
				((IStringObject)target).setValue(data);
		}
	}
}
