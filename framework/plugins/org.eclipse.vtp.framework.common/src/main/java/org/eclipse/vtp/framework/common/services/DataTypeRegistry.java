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
package org.eclipse.vtp.framework.common.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.framework.common.IArrayObject;
import org.eclipse.vtp.framework.common.IBooleanObject;
import org.eclipse.vtp.framework.common.IDataType;
import org.eclipse.vtp.framework.common.IDataTypeRegistry;
import org.eclipse.vtp.framework.common.IDateObject;
import org.eclipse.vtp.framework.common.IDecimalObject;
import org.eclipse.vtp.framework.common.IExternalDataType;
import org.eclipse.vtp.framework.common.IMapObject;
import org.eclipse.vtp.framework.common.INumberObject;
import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.configurations.DataTypeConfiguration;
import org.eclipse.vtp.framework.common.configurations.FieldConfiguration;
import org.eclipse.vtp.framework.common.support.ScriptableArray;
import org.osgi.framework.Bundle;

/**
 * Implementation of {@link IDataTypeRegistry}.
 * 
 * @author Lonnie Pryor
 */
public class DataTypeRegistry implements IDataTypeRegistry, IScriptable
{
	/** The index of data type instances. */
	private final Map<String, IDataType> dataTypes;

	/**
	 * Creates a new DataTypeRegistry.
	 *
	 * @param extensionRegistry The extension registry to use.
	 * @param configurations The configurations to use.
	 */
	@SuppressWarnings("unchecked")
	public DataTypeRegistry(IExtensionRegistry extensionRegistry,
			DataTypeConfiguration[] configurations)
	{
		Map<String, IDataType> dataTypes = new HashMap<String, IDataType>();
		// Create the registered types.
		IConfigurationElement[] elements = extensionRegistry
				.getConfigurationElementsFor(//
				"org.eclipse.vtp.framework.common.dataTypes"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; ++i)
		{
			DataType type = new DataType(elements[i]);
			dataTypes.put(type.getName(), type);
			if(elements[i].getName().equals("object-implementation"))
			{
				String typeClassName = elements[i].getAttribute("factory-class");
				Bundle contributor = Platform.getBundle(elements[i].getContributor().getName());
				IExternalDataType factory = null;
				try
				{
					factory = ((Class<IExternalDataType>)contributor.loadClass(typeClassName)).newInstance();
					type.setExternalFactory(factory);
					type.setExternal(true);
				}
				catch (Exception e)
				{
					dataTypes.remove(type.getName());
					e.printStackTrace();
					continue;
				}
			}
		}
		// Create the configured types.
		for (int i = 0; i < configurations.length; ++i)
		{
			DataType type = new DataType(configurations[i]);
			dataTypes.put(type.getName(), type);
		}
		// Create the implicit types.
		String zero = String.valueOf(0);
		dataTypes.put(IArrayObject.TYPE_NAME, new DataType(IArrayObject.TYPE_NAME,
				IArrayObject.FIELD_NAME_LENGTH, INumberObject.TYPE_NAME, zero));
		dataTypes.put(IMapObject.TYPE_NAME, new DataType(IMapObject.TYPE_NAME));
		dataTypes.put(IBooleanObject.TYPE_NAME, new DataType(
				IBooleanObject.TYPE_NAME));
		dataTypes.put(IDateObject.TYPE_NAME, new DataType(IDateObject.TYPE_NAME));
		dataTypes.put(IDecimalObject.TYPE_NAME, new DataType(
				IDecimalObject.TYPE_NAME));
		dataTypes.put(INumberObject.TYPE_NAME,
				new DataType(INumberObject.TYPE_NAME));
		dataTypes.put(IStringObject.TYPE_NAME, new DataType(
				IStringObject.TYPE_NAME, IStringObject.FIELD_NAME_LENGTH,
				INumberObject.TYPE_NAME, zero));
		this.dataTypes = Collections.unmodifiableMap(new HashMap<String, IDataType>(dataTypes));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IDataTypeRegistry#getDataType(
	 *      java.lang.String)
	 */
	public final IDataType getDataType(String typeName)
			throws NullPointerException
	{
		if (typeName == null)
			throw new NullPointerException("typeName"); //$NON-NLS-1$
		return dataTypes.get(typeName);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
	 */
	public final String getName()
	{
		return "DataTypes"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
	 */
	public boolean hasValue()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
	 */
	public Object toValue()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getFunctionNames()
	 */
	public final String[] getFunctionNames()
	{
		return new String[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
	 *      java.lang.String, java.lang.Object[])
	 */
	public final Object invokeFunction(String name, Object[] arguments)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
	 */
	public final boolean hasItem(int index)
	{
		return false;
	}

	public String[] getPropertyNames()
	{
		List<String> propNames = new ArrayList<String>();
		for(Map.Entry<String, IDataType> entry : dataTypes.entrySet())
		{
			propNames.add(entry.getKey());
		}
		return propNames.toArray(new String[propNames.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasProperty(
	 *      java.lang.String)
	 */
	public final boolean hasEntry(String name)
	{
		return getDataType(name) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
	 */
	public final Object getItem(int index)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getProperty(
	 *      java.lang.String)
	 */
	public final Object getEntry(String name)
	{
		return getDataType(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
	 *      java.lang.Object)
	 */
	public final boolean setItem(int index, Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setProperty(
	 *      java.lang.String, java.lang.Object)
	 */
	public final boolean setEntry(String name, Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
	 */
	public final boolean clearItem(int index)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearProperty(
	 *      java.lang.String)
	 */
	public final boolean clearEntry(String name)
	{
		return false;
	}

	/**
	 * Implementation of {@link IDataType}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class DataType implements IDataType, IScriptable
	{
		/** The name of this type. */
		private final String name;
		/** The fields of this type. */
		private final Field[] fields;
		/** The primary fields of this type. */
		private final Field primaryField;
		/** The children of this brand as a scriptable array. */
		private final ScriptableArray fieldArray;
		private boolean external = false;
		private IExternalDataType factory;

		/**
		 * Creates a new ScriptableDataType.
		 * 
		 * @param name The name of this type.
		 */
		DataType(String name)
		{
			this.name = name;
			this.fields = new Field[0];
			this.primaryField = null;
			this.fieldArray = new ScriptableArray("fields", fields); //$NON-NLS-1$
		}

		/**
		 * Creates a new ScriptableDataType.
		 * 
		 * @param name The name of this type.
		 * @param fieldName The name of the single derived field.
		 * @param fieldTypeName The name of the type of the single derived field.
		 * @param fieldInitialValue The initial value of the single derived field.
		 */
		DataType(String name, String fieldName, String fieldTypeName,
				String fieldInitialValue)
		{
			this(name, fieldName, fieldTypeName, fieldInitialValue, false);
		}
		
		/**
		 * Creates a new ScriptableDataType.
		 * 
		 * @param name The name of this type.
		 * @param fieldName The name of the single derived field.
		 * @param fieldTypeName The name of the type of the single derived field.
		 * @param fieldInitialValue The initial value of the single derived field.
		 * @param fieldSecured Whether or not the field is secured.
		 */
		DataType(String name, String fieldName, String fieldTypeName,
				String fieldInitialValue, boolean fieldSecured)
		{
			this.name = name;
			this.fields = new Field[] { new Field(fieldName, fieldTypeName,
					fieldInitialValue, fieldSecured) };
			this.primaryField = null;
			this.fieldArray = new ScriptableArray("fields", fields); //$NON-NLS-1$
		}

		/**
		 * Creates a new DataType.
		 * 
		 * @param configuration The configuration for this type.
		 */
		DataType(DataTypeConfiguration configuration)
		{
			this.name = configuration.getName();
			FieldConfiguration[] fieldConfigs = configuration.getFields();
			this.fields = new Field[fieldConfigs.length];
			String primaryFieldName = configuration.getPrimaryField();
			Field primaryField = null;
			for (int i = 0; i < fieldConfigs.length; ++i)
			{
				this.fields[i] = new Field(fieldConfigs[i].getName(), fieldConfigs[i]
						.getType(), fieldConfigs[i].getInitialValue(), fieldConfigs[i].isSecured());
				if (this.fields[i].getName().equals(primaryFieldName))
					primaryField = this.fields[i];
			}
			this.primaryField = primaryField;
			this.fieldArray = new ScriptableArray("fields", fields); //$NON-NLS-1$
		}

		/**
		 * Creates a new DataType.
		 * 
		 * @param configuration The configuration for this type.
		 */
		DataType(IConfigurationElement configuration)
		{
			this.name = configuration.getAttribute("name"); //$NON-NLS-1$
			IConfigurationElement[] fieldConfigs = configuration.getChildren("field"); //$NON-NLS-1$
			this.fields = new Field[fieldConfigs.length];
			String primaryFieldName = configuration.getAttribute("primary-field"); //$NON-NLS-1$
			Field primaryField = null;
			for (int i = 0; i < fieldConfigs.length; ++i)
			{
				this.fields[i] = new Field(fieldConfigs[i].getAttribute("name"), //$NON-NLS-1$ 
						fieldConfigs[i].getAttribute("type"), //$NON-NLS-1$
						fieldConfigs[i].getAttribute("initial-value"),
						fieldConfigs[i].getAttribute("secured") != null ? 
								Boolean.parseBoolean(fieldConfigs[i].getAttribute("secured")) :
								false);
				if (this.fields[i].getName().equals(primaryFieldName))
					primaryField = this.fields[i];
			}
			this.primaryField = primaryField;
			this.fieldArray = new ScriptableArray("fields", fields); //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDataType#getName()
		 */
		public String getName()
		{
			return name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDataType#getFieldNames()
		 */
		public String[] getFieldNames()
		{
			String[] fieldNames = new String[fields.length];
			for (int i = 0; i < fields.length; ++i)
				fieldNames[i] = fields[i].name;
			return fieldNames;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDataType#getPrimaryFieldName()
		 */
		public String getPrimaryFieldName()
		{
			return primaryField == null ? null : primaryField.name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDataType#getFieldType(
		 *      java.lang.String)
		 */
		public IDataType getFieldType(String fieldName)
		{
			for (int i = 0; i < fields.length; ++i)
				if (fields[i].name.equals(fieldName))
					return fields[i].getType();
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDataType#getFieldInitialValue(
		 *      java.lang.String)
		 */
		public String getFieldInitialValue(String fieldName)
		{
			for (int i = 0; i < fields.length; ++i)
				if (fields[i].name.equals(fieldName))
					return fields[i].initalValue;
			return null;
		}
		
		public boolean isFieldSecured(String fieldName)
		{
			for (int i = 0; i < fields.length; ++i)
				if (fields[i].name.equals(fieldName))
					return fields[i].secured;
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDataType#isArrayType()
		 */
		public boolean isArrayType()
		{
			return IArrayObject.TYPE_NAME.equals(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDataType#isArrayType()
		 */
		public boolean isMapType()
		{
			return IMapObject.TYPE_NAME.equals(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDataType#isComplexType()
		 */
		public boolean isComplexType()
		{
			return !isArrayType() && !isMapType() && !isSimpleType();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDataType#isSimpleType()
		 */
		public boolean isSimpleType()
		{
			return IBooleanObject.TYPE_NAME.equals(name)
					|| IDateObject.TYPE_NAME.equals(name)
					|| IDecimalObject.TYPE_NAME.equals(name)
					|| INumberObject.TYPE_NAME.equals(name)
					|| IStringObject.TYPE_NAME.equals(name);
		}
		
		public boolean isExternalType()
		{
			return external;
		}
		
		public void setExternal(boolean external)
		{
			this.external = external;
		}

		public final IExternalDataType getExternalFactory()
		{
			return factory;
		}
		
		public void setExternalFactory(IExternalDataType factory)
		{
			this.factory = factory;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
		 */
		public boolean hasValue()
		{
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
		 */
		public Object toValue()
		{
			return name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getFunctionNames()
		 */
		public String[] getFunctionNames()
		{
			return new String[] { "isArray", //$NON-NLS-1$
					"isComplex", //$NON-NLS-1$
					"isSimple",
					"isMap"}; //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
		 *      java.lang.String, java.lang.Object[])
		 */
		public Object invokeFunction(String name, Object[] arguments)
		{
			if ("isArray".equals(name)) //$NON-NLS-1$
				return isArrayType() ? Boolean.TRUE : Boolean.FALSE;
			if ("isMap".equals(name)) //$NON-NLS-1$
				return isMapType() ? Boolean.TRUE : Boolean.FALSE;
			if ("isComplex".equals(name)) //$NON-NLS-1$
				return isComplexType() ? Boolean.TRUE : Boolean.FALSE;
			if ("isSimple".equals(name)) //$NON-NLS-1$
				return isSimpleType() ? Boolean.TRUE : Boolean.FALSE;
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
		 */
		public boolean hasItem(int index)
		{
			return false;
		}

		public String[] getPropertyNames()
		{
			return new String[] {"name", "fields"};
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasProperty(
		 *      java.lang.String)
		 */
		public boolean hasEntry(String name)
		{
			return "name".equals(name) || //$NON-NLS-1$
				   "fields".equals(name); //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
		 */
		public Object getItem(int index)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getProperty(
		 *      java.lang.String)
		 */
		public Object getEntry(String name)
		{
			if ("name".equals(name)) //$NON-NLS-1$
				return this.name;
			if ("fields".equals(name)) //$NON-NLS-1$
				return fieldArray;
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
		 *      java.lang.Object)
		 */
		public boolean setItem(int index, Object value)
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setProperty(
		 *      java.lang.String, java.lang.Object)
		 */
		public boolean setEntry(String name, Object value)
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
		 */
		public boolean clearItem(int index)
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearProperty(
		 *      java.lang.String)
		 */
		public boolean clearEntry(String name)
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return name;
		}

		/**
		 * A field of a data type.
		 * 
		 * @author Lonnie Pryor
		 */
		private final class Field implements IScriptable
		{
			/** The name of this field. */
			final String name;
			/** The name of the type of this field. */
			final String typeName;
			/** The initial value of this field. */
			final String initalValue;
			final boolean secured;

			/**
			 * Creates a new Field.
			 * 
			 * @param name The name of this field.
			 * @param typeName The name of the type of this field.
			 * @param initalValue The initial value of this field.
			 */
			Field(String name, String typeName, String initalValue, boolean secured)
			{
				this.name = name;
				this.typeName = typeName;
				this.initalValue = initalValue;
				this.secured = secured;
			}

			/**
			 * Resolves and returns the type of this field.
			 * 
			 * @return The type of this field.
			 */
			IDataType getType()
			{
				return DataTypeRegistry.this.getDataType(typeName);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
			 */
			public String getName()
			{
				return name;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
			 */
			public boolean hasValue()
			{
				return true;
			}
			
			/* FIXME This method is never used and should be deleted.
			public boolean isSecured()
			{
				return secured;
			}
			*/

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
			 */
			public Object toValue()
			{
				return name;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getFunctionNames()
			 */
			public String[] getFunctionNames()
			{
				return new String[] {}; //$NON-NLS-1$
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
			 *      java.lang.String, java.lang.Object[])
			 */
			public Object invokeFunction(String name, Object[] arguments)
			{
				return null;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
			 */
			public boolean hasItem(int index)
			{
				return false;
			}

			public String[] getPropertyNames()
			{
				return new String[] {"name", "type", "initialValue", "secured"};
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasProperty(
			 *      java.lang.String)
			 */
			public boolean hasEntry(String name)
			{
				return "name".equals(name) //$NON-NLS-1$
						|| "type".equals(name) //$NON-NLS-1$
						|| "initalValue".equals(name) //$NON-NLS-1$
						|| "secured".equals(name); //$NON-NLS-1$
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
			 */
			public Object getItem(int index)
			{
				return null;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getProperty(
			 *      java.lang.String)
			 */
			public Object getEntry(String name)
			{
				if ("name".equals(name)) //$NON-NLS-1$
					return this.name;
				if ("type".equals(name)) //$NON-NLS-1$
					return getType();
				if ("initalValue".equals(name)) //$NON-NLS-1$
					return initalValue;
				if ("secured".equals(name)) //$NON-NLS-1$
					return new Boolean(secured);
				return null;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
			 *      java.lang.Object)
			 */
			public boolean setItem(int index, Object value)
			{
				return false;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setProperty(
			 *      java.lang.String, java.lang.Object)
			 */
			public boolean setEntry(String name, Object value)
			{
				return false;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
			 */
			public boolean clearItem(int index)
			{
				return false;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearProperty(
			 *      java.lang.String)
			 */
			public boolean clearEntry(String name)
			{
				return false;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Object#toString()
			 */
			public String toString()
			{
				return name;
			}

			@Override
			public boolean isMutable()
			{
				// TODO Auto-generated method stub
				return false;
			}
		}

		@Override
		public boolean isMutable()
		{
			// TODO Auto-generated method stub
			return false;
		}
	}

	@Override
	public boolean isMutable()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
