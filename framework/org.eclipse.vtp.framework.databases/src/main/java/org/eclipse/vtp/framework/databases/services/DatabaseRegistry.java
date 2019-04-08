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
package org.eclipse.vtp.framework.databases.services;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.eclipse.vtp.framework.common.IBooleanObject;
import org.eclipse.vtp.framework.common.IDataType;
import org.eclipse.vtp.framework.common.IDataTypeRegistry;
import org.eclipse.vtp.framework.common.IDecimalObject;
import org.eclipse.vtp.framework.common.IEncryptionEngine;
import org.eclipse.vtp.framework.common.INumberObject;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.core.IProcessContext;
import org.eclipse.vtp.framework.databases.IDatabase;
import org.eclipse.vtp.framework.databases.IDatabaseRegistry;
import org.eclipse.vtp.framework.databases.configurations.DatabaseColumnConfiguration;
import org.eclipse.vtp.framework.databases.configurations.DatabaseConfiguration;
import org.eclipse.vtp.framework.databases.configurations.DatabaseTableConfiguration;
import org.eclipse.vtp.framework.databases.configurations.JdbcDatabaseConfiguration;
import org.eclipse.vtp.framework.databases.configurations.JndiDatabaseConfiguration;

/**
 * Implementation of {@link IDatabaseRegistry}.
 * 
 * @author Lonnie Pryor
 */
public class DatabaseRegistry implements IDatabaseRegistry
{
	/** The context to use. */
	private final IProcessContext context;
	/** The encryption engine to use or <code>null</code>. */
	private final IEncryptionEngine encryption;
	/** Comment for factories. */
	private final Map<String, IDatabase> databases;

	/**
	 * Creates a new DatabaseRegistry.
	 * 
	 * @param context The context to use.
	 * @param types The data type registry to use.
	 * @param configurations The database configuration information.
	 */
	public DatabaseRegistry(IProcessContext context, IDataTypeRegistry types,
			DatabaseConfiguration[] configurations)
	{
		this.context = context;
		Object encryption = context.lookup(IEncryptionEngine.class.getName());
		if (encryption instanceof IEncryptionEngine)
			this.encryption = (IEncryptionEngine)encryption;
		else
			this.encryption = null;
		Map<String, IDatabase> databases = new HashMap<String, IDatabase>(configurations.length);
		for (int i = 0; i < configurations.length; ++i)
		{
			AbstractDatabase factory = null;
			if (configurations[i] instanceof JdbcDatabaseConfiguration)
				factory = new JdbcDatabase(types,
						(JdbcDatabaseConfiguration)configurations[i]);
			else if (configurations[i] instanceof JndiDatabaseConfiguration)
				factory = new JndiDatabase(types,
						(JndiDatabaseConfiguration)configurations[i]);
			else
				continue;
			databases.put(configurations[i].getName(), factory);
		}
		this.databases = Collections.unmodifiableMap(databases);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.databases.IDatabaseRegistry#
	 *      getDatabaseNames()
	 */
	public String[] getDatabaseNames()
	{
		return databases.keySet().toArray(new String[databases.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.databases.IDatabaseRegistry#getDatabase(
	 *      java.lang.String)
	 */
	public IDatabase getDatabase(String databaseName)
	{
		return databases.get(databaseName);
	}

	/**
	 * The basic database implementation.
	 * 
	 * @author Lonnie Pryor
	 */
	private abstract class AbstractDatabase implements IDatabase
	{
		/** The user name to use. */
		final String name;
		/** The user name to use. */
		final String username;
		/** The password to use. */
		final String password;
		/** The schema of this database. */
		final Map<String, Map<String, IDataType>> schema;

		/**
		 * Creates a new Database.
		 * 
		 * @param types The data type registry to use.
		 * @param configuration The configuration of this database.
		 */
		AbstractDatabase(IDataTypeRegistry types,
				DatabaseConfiguration configuration)
		{
			this.name = configuration.getName();
			this.username = configuration.getUsername();
			String password = configuration.getPassword();
			if (encryption == null || password == null)
				this.password = password;
			else
				this.password = new String(encryption.decrypt(password.toCharArray()));
			DatabaseTableConfiguration[] tableConfigs = configuration.getTables();
			Map<String, Map<String, IDataType>> schema = new LinkedHashMap<String, Map<String, IDataType>>(tableConfigs.length);
			for (int i = 0; i < tableConfigs.length; ++i)
			{
				DatabaseColumnConfiguration[] columnConfigs = tableConfigs[i]
						.getColumns();
				Map<String, IDataType> table = new LinkedHashMap<String, IDataType>(columnConfigs.length);
				for (int j = 0; j < columnConfigs.length; ++j)
				{
					String type = null;
					switch (columnConfigs[j].getType())
					{
					case DatabaseColumnConfiguration.TYPE_BIG_DECIMAL:
					case DatabaseColumnConfiguration.TYPE_BIG_NUMBER:
					case DatabaseColumnConfiguration.TYPE_DECIMAL:
						type = IDecimalObject.TYPE_NAME;
						break;
					case DatabaseColumnConfiguration.TYPE_BOOLEAN:
						type = IBooleanObject.TYPE_NAME;
						break;
					case DatabaseColumnConfiguration.TYPE_DATETIME:
						type = IBooleanObject.TYPE_NAME;
						break;
					case DatabaseColumnConfiguration.TYPE_NUMBER:
						type = INumberObject.TYPE_NAME;
						break;
					case DatabaseColumnConfiguration.TYPE_TEXT:
					case DatabaseColumnConfiguration.TYPE_VARCHAR:
						type = IStringObject.TYPE_NAME;
						break;
					}
					if (type != null)
						table.put(columnConfigs[j].getName(), types.getDataType(type));
				}
				schema.put(tableConfigs[i].getName(), Collections
						.unmodifiableMap(table));
			}
			this.schema = Collections.unmodifiableMap(schema);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.databases.IDatabase#getName()
		 */
		public String getName()
		{
			return name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.databases.IDatabase#getTableNames()
		 */
		public String[] getTableNames()
		{
			return schema.keySet().toArray(new String[schema.size()]);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.databases.IDatabase#getColumnNames(java.lang.String)
		 */
		public String[] getColumnNames(String tableName)
		{
			Map<String, IDataType> table = schema.get(tableName);
			if (table == null)
				return null;
			return table.keySet().toArray(new String[table.size()]);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.databases.IDatabase#getColumnType(java.lang.String,
		 *      java.lang.String)
		 */
		public IDataType getColumnType(String tableName, String columnName)
		{
			Map<String, IDataType> table = schema.get(tableName);
			if (table == null)
				return null;
			return table.get(columnName);
		}
	}

	/**
	 * The JDBC database implementation.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class JdbcDatabase extends AbstractDatabase
	{
		/** The driver to use. */
		final Driver driver;
		/** The URL to use. */
		final String url;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		/**
		 * Creates a new JdbcDatabase.
		 * 
		 * @param types The data type registry to use.
		 * @param configuration The configuration to use.
		 */
		JdbcDatabase(IDataTypeRegistry types,
				JdbcDatabaseConfiguration configuration)
		{
			super(types, configuration);
			Driver driver = null;
			try
			{
				driver = (Driver)context.loadClass(configuration.getDriver())
						.newInstance();
			}
			catch (Exception e)
			{
				Hashtable properties = new Hashtable();
				properties.put("cause", e); //$NON-NLS-1$
				context.error(e.getMessage(), properties);
			}
			this.driver = driver;
			this.url = configuration.getUrl();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.databases.IDatabase#getConnection()
		 */
		public Connection getConnection() throws SQLException
		{
			if (driver == null)
				return null;
			Properties info = new Properties();
			if (username != null)
				info.setProperty("user", username); //$NON-NLS-1$
			if (password != null)
				info.setProperty("password", password); //$NON-NLS-1$
			return driver.connect(url, info);
		}
	}

	/**
	 * The JNDI database implementation.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class JndiDatabase extends AbstractDatabase
	{
		/** The URL to use. */
		final DataSource dataSource;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		/**
		 * Creates a new JndiDatabase.
		 * 
		 * @param types The data type registry to use.
		 * @param configuration The configuration to use.
		 */
		JndiDatabase(IDataTypeRegistry types,
				JndiDatabaseConfiguration configuration)
		{
			super(types, configuration);
			DataSource dataSource = null;
			try
			{
				dataSource = (DataSource)new InitialContext().lookup(configuration
						.getUri());
			}
			catch (Exception e)
			{
				Hashtable properties = new Hashtable();
				properties.put("cause", e); //$NON-NLS-1$
				context.error(e.getMessage(), properties);
			}
			this.dataSource = dataSource;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.databases.services.DatabaseRegistry.
		 *      ConnectionFactory#getConnection()
		 */
		public Connection getConnection() throws SQLException
		{
			if (dataSource == null)
				return null;
			if (username != null && password != null)
				return dataSource.getConnection(username, password);
			return dataSource.getConnection();
		}
	}
}
