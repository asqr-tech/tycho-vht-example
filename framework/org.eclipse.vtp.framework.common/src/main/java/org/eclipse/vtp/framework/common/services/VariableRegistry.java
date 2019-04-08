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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.vtp.framework.common.IArrayObject;
import org.eclipse.vtp.framework.common.IBooleanObject;
import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IDataType;
import org.eclipse.vtp.framework.common.IDataTypeRegistry;
import org.eclipse.vtp.framework.common.IDateObject;
import org.eclipse.vtp.framework.common.IDecimalObject;
import org.eclipse.vtp.framework.common.ILastResult;
import org.eclipse.vtp.framework.common.IMapObject;
import org.eclipse.vtp.framework.common.INumberObject;
import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.common.IVariableStorage;
import org.eclipse.vtp.framework.common.support.CustomDataField;
import org.eclipse.vtp.framework.common.support.CustomDataType;
import org.eclipse.vtp.framework.core.ILogger;
import org.eclipse.vtp.framework.core.ISessionContext;
import org.eclipse.vtp.framework.util.DateHelper;

/**
 * A support implementation of the {@link IVariableRegistry} interface.
 * 
 * @author Lonnie Pryor
 */
public class VariableRegistry implements IVariableRegistry, IScriptable, IVariableStorage
{

	/** The context to use. */
	private final ISessionContext context;
	/** The data type registry to use. */
	private final IDataTypeRegistry dataTypeRegistry;
	/** The variables in this registry by their internal ID. */
	private final Map<String, IDataObject> variables = new HashMap<String, IDataObject>();
	private final ILastResult lastResult;

	/**
	 * Creates a new VariableRegistry.
	 * 
	 * @param context The context to use.
	 * @param dataTypeRegistry The data type registry to use.
	 */
	public VariableRegistry(ISessionContext context,
			IDataTypeRegistry dataTypeRegistry, ILastResult lastResult)
	{
		this.context = context;
		this.dataTypeRegistry = dataTypeRegistry;
		this.lastResult = lastResult;
	}
	
	public ILogger getLogger()
	{
		return context;
	}

	/**
	 * Creates a new instance of an object with the default initial value.
	 * 
	 * @param type The type of object to create.
	 * @return The new object instance.
	 */
	private IDataObject createObject(IDataType type, boolean secured)
	{
		return createObject(type, null, secured);
	}

	private IDataObject createObject(IDataType type, String id)
	{
		IDataObject variable = null;
		if (type.equals(dataTypeRegistry.getDataType(type.getName())))
		{
			if(type.isExternalType())
			{
				variable = type.getExternalFactory().createInstance(this, type, null);
			}
			else if (IArrayObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new ArrayObject(id);
			}
			else if (IMapObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new MapObject(id);
			}
			else if (IBooleanObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new BooleanObject(id);
			}
			else if (IDateObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new DateObject(id);
			}
			else if (IDecimalObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new DecimalObject(id);
			}
			else if (INumberObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new NumberObject(id);
			}
			else if (IStringObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new StringObject(id);
			}
			else
				variable = new ComplexObject(type);
		}
		else
			variable = new ComplexObject(type);
		synchronized (variables)
		{
			variables.put(variable.getId(), variable);
		}
		return variable;
	}

	private IDataObject createObject(IDataType type, Object initialValue, boolean secured)
	{
		IDataObject variable = null;
		if (type.equals(dataTypeRegistry.getDataType(type.getName())))
		{
			if(type.isExternalType())
			{
				variable = type.getExternalFactory().createInstance(this, type, null);
			}
			else if (IArrayObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new ArrayObject();
			}
			else if (IMapObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new MapObject();
			}
			else if (IBooleanObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new BooleanObject(initialValue);
			}
			else if (IDateObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new DateObject(initialValue);
			}
			else if (IDecimalObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new DecimalObject(initialValue);
			}
			else if (INumberObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new NumberObject(initialValue);
			}
			else if (IStringObject.TYPE_NAME.equals(type.getName()))
			{
				variable = new StringObject(initialValue);
			}
			else
				variable = new ComplexObject(type);
		}
		else
			variable = new ComplexObject(type);
		variable.setSecured(secured);
		synchronized (variables)
		{
			variables.put(variable.getId(), variable);
		}
		return variable;
	}

	/**
	 * Loads an already-existing object rand returns it.
	 * 
	 * @param id The ID of the object to load.
	 * @return The requested object or <code>null</code> if it does not exist.
	 */
	public IDataObject loadObject(String id)
	{
		IDataObject variable = null;
		synchronized (variables)
		{
			variable = variables.get(id);
			if (variable == null)
			{
				Object[] record = getRecord(id);
				if (record != null)
				{
					IDataType type = (IDataType)record[0];
					if(type.isExternalType())
					{
						variable = type.getExternalFactory().createInstance(this, type, id);
					}
					else if (IArrayObject.TYPE_NAME.equals(type.getName()))
						variable = new ArrayObject(id);
					else if (IMapObject.TYPE_NAME.equals(type.getName()))
						variable = new MapObject(id);
					else if (IBooleanObject.TYPE_NAME.equals(type.getName()))
						variable = new BooleanObject(id);
					else if (IDateObject.TYPE_NAME.equals(type.getName()))
						variable = new DateObject(id);
					else if (IDecimalObject.TYPE_NAME.equals(type.getName()))
						variable = new DecimalObject(id);
					else if (INumberObject.TYPE_NAME.equals(type.getName()))
						variable = new NumberObject(id);
					else if (IStringObject.TYPE_NAME.equals(type.getName()))
						variable = new StringObject(id);
					else
						variable = new ComplexObject(id, type);
					variables.put(id, variable);
				}
			}
		}
		return variable;
	}

	/**
	 * Returns the variable record with the specified ID or <code>null</code> if
	 * no valid record can be found.
	 * 
	 * @param id The ID of the variable record to return.
	 * @return The variable record with the specified ID or <code>null</code> if
	 *         no valid record can be found.
	 */
	public Object[] getRecord(String id)
	{
		Object attribute = context.getAttribute(id);
		if (!(attribute instanceof Object[]))
			return null;
		Object[] record = (Object[])attribute;
		if (record.length != 2)
			return null;
		if (!(record[0] instanceof String))
			return null;
		String typeName = (String)record[0];
		if (typeName.startsWith("!"))
		{
			CustomDataType type = null;
			String[] array = typeName.substring(0).split(","); //$NON-NLS-1$
			CustomDataField[] fields = new CustomDataField[array.length - 2];
			for (int i = 2; i < array.length; ++i)
			{
				String[] field = array[i].split(":", -1); //$NON-NLS-1$
				fields[i - 2] = new CustomDataField(field[0], dataTypeRegistry
						.getDataType(field[1]), field[2], Boolean.parseBoolean(field[3]));
			}
			type = new CustomDataType(array[0], array[1].length() == 0 ? null
					: array[1], fields);
			return new Object[] { type, record[1] };
		}
		else
		{
			IDataType type = dataTypeRegistry.getDataType((typeName));
			if (type == null)
				return null;
			return new Object[] { type, record[1] };
		}
	}

	/**
	 * Sets the variable record information for the specified ID.
	 * 
	 * @param id The ID of the variable record to set.
	 * @param type The type of the variable.
	 * @param value The new value of the record.
	 */
	public void setRecord(String id, IDataType type, Object value)
	{
		if (type.equals(dataTypeRegistry.getDataType(type.getName())))
			context.setAttribute(id, (new Object[] { type.getName(), value }));
		else
		{
			StringBuffer b = new StringBuffer("!"); //$NON-NLS-1$
			b.append(type.getName());
			b.append(',');
			if (type.getPrimaryFieldName() != null)
			{
				b.append(type.getPrimaryFieldName());
			}
			String[] fields = type.getFieldNames();
			for (int i = 0; i < fields.length; ++i)
			{
				b.append(',');
				b.append(fields[i]);
				b.append(':');
				b.append(type.getFieldType(fields[i]).getName());
				b.append(':');
				b.append(type.getFieldInitialValue(fields[i]));
				b.append(':');
				b.append(Boolean.toString(type.isFieldSecured(fields[i])));
			}
			context.setAttribute(id, (new Object[] { b.toString(), value }));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IVariableRegistry#createVariable(
	 *      java.lang.String)
	 */
	public final IDataObject createVariable(String typeName)
			throws IllegalArgumentException, NullPointerException
	{
		return createVariable(typeName, false);
	}
	
	public final IDataObject createVariable(String typeName, boolean secured)
			throws IllegalArgumentException, NullPointerException
	{
		IDataType type = dataTypeRegistry.getDataType(typeName);
		if (type == null)
			throw new IllegalArgumentException("typeName: " + typeName); //$NON-NLS-1$
		return createVariable(type, secured);
	}

	public final IDataObject createVariable(String typeName, String id)
			throws IllegalArgumentException, NullPointerException
	{
		IDataType type = dataTypeRegistry.getDataType(typeName);
		if (type == null)
			throw new IllegalArgumentException("typeName: " + typeName); //$NON-NLS-1$
		return createVariable(type, id);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IVariableRegistry#createVariable(
	 *      org.eclipse.vtp.framework.core.IDataType)
	 */
	public final IDataObject createVariable(IDataType type)
			throws IllegalArgumentException, NullPointerException
	{
		return createVariable(type, false);
	}
	
	public final IDataObject createVariable(IDataType type, boolean secured)
			throws IllegalArgumentException, NullPointerException
	{
		if (type == null)
			throw new NullPointerException("type"); //$NON-NLS-1$
		return createObject(type, secured);
	}

	public final IDataObject createVariable(IDataType type, String id)
			throws IllegalArgumentException, NullPointerException
	{
		if (type == null)
			throw new NullPointerException("type"); //$NON-NLS-1$
		return createObject(type, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IVariableRegistry#getVariableNames()
	 */
	public final String[] getVariableNames()
	{
		String[] storageKeys = context.getAttributeNames();
		LinkedList<String> variableNames = new LinkedList<String>();
		for (int i = 0; i < storageKeys.length; ++i)
			if (storageKeys[i].startsWith(ENTRY_PREFIX))
				variableNames.add(storageKeys[i].substring(ENTRY_PREFIX.length()));
		return variableNames.toArray(new String[variableNames.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IVariableRegistry#getVariable(
	 *      java.lang.String)
	 */
	public final IDataObject getVariable(String name) throws NullPointerException
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		StringTokenizer st = new StringTokenizer(name, "."); //$NON-NLS-1$
		if (!st.hasMoreTokens())
			return null;
		String rootName = st.nextToken();
		String id = (String)context.getAttribute(ENTRY_PREFIX + rootName);
		if (id == null)
			return null;
		IDataObject variable = loadObject(id);
		while (variable != null && st.hasMoreTokens())
			variable = variable.getField(st.nextToken());
		return variable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IVariableRegistry#setVariable(
	 *      java.lang.String, org.eclipse.vtp.framework.core.IDataObject)
	 */
	public final void setVariable(String name, IDataObject variable)
			throws IllegalArgumentException, NullPointerException
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (variable == null)
			throw new NullPointerException("variable"); //$NON-NLS-1$
		IDataObject currentObject = getVariable(name);
		if(currentObject != null && currentObject.isSecured())
			variable.setSecured(true);
		StringTokenizer st = new StringTokenizer(name, ".");
		if (!st.hasMoreTokens())
			return;
		String rootName = st.nextToken();
		if (!st.hasMoreTokens())
		{
			context.setAttribute(ENTRY_PREFIX + rootName, variable.getId());
		}
		else
		{
			String id = (String)context.getAttribute(ENTRY_PREFIX + st.nextToken());
			if (id == null)
				return;
			IDataObject parent = loadObject(id);
			String childName = st.nextToken();
			for (; parent != null && st.hasMoreTokens(); childName = st.nextToken())
				parent = parent.getField(childName);
			if (parent != null)
				parent.setField(childName, variable);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IVariableRegistry#clearVariable(
	 *      java.lang.String)
	 */
	public final void clearVariable(String name) throws NullPointerException
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		context.clearAttribute(ENTRY_PREFIX + name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
	 */
	public final String getName()
	{
		return "Variables"; //$NON-NLS-1$
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

	public String[] getPropertyNames()
	{
		return new String[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#
	 *      getFunctionNames()
	 */
	public String[] getFunctionNames()
	{
		return new String[] { "create" }; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
	 *      java.lang.String, java.lang.Object[])
	 */
	public final Object invokeFunction(String name, Object[] arguments)
	{
		if ("create".equals(name)) //$NON-NLS-1$
		{
			if (arguments.length < 1)
				return null;
			IDataType type = null;
			if (arguments[0] instanceof IDataType)
				type = (IDataType)arguments[0];
			else
				type = dataTypeRegistry.getDataType(String.valueOf(arguments[0]));
			if (type == null)
				return null;
			return createVariable(type);
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasEntry(
	 *      java.lang.String)
	 */
	public final boolean hasEntry(String name)
	{
		return "LastResult".equals(name) || getVariable(name) != null;
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
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getEntry(
	 *      java.lang.String)
	 */
	public final Object getEntry(String name)
	{
		if("LastResult".equals(name))
			return lastResult;
		return getVariable(name);
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
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setEntry(
	 *      java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	public final boolean setEntry(String name, Object value)
	{
		if("LastResult".equals(name))
			return false;
		if (value == null)
			return clearEntry(name);
		if (value instanceof IDataObject)
		{
			setVariable(name, (IDataObject)value);
			return true;
		}
		IDataObject variable = getVariable(name);
		if (variable == null)
			setVariable(name, variable = createVariable(IStringObject.TYPE_NAME));
		else
		{
			setVariable(name, variable = createVariable(variable.getType()));
		}
		if (variable instanceof SimpleObject)
			((SimpleObject)variable).setEntry("value", value); //$NON-NLS-1$
		return true;
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
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearEntry(
	 *      java.lang.String)
	 */
	public final boolean clearEntry(String name)
	{
		clearVariable(name);
		return true;
	}

	/**
	 * Basic implementation of simple {@link IDataObject} types.
	 * 
	 * @author Lonnie Pryor
	 */
	private abstract class SimpleObject<T extends Comparable<T>> extends DataObject
	{
		/**
		 * Creates a new SimpleObject.
		 * 
		 * @param id The ID of this instance or <code>null</code> to generate a
		 *          new ID.
		 * @param type The type of this instance.
		 */
		SimpleObject(String id, IDataType type)
		{
			super(VariableRegistry.this, id, type);
		}

		/**
		 * Returns a comparable value derived from the supplied object or
		 * <code>null</code> if no comparable value can be derived.
		 * 
		 * @param toCoerce The value to coerce into a compatable type.
		 * @return A comparable value derived from the supplied object or
		 *         <code>null</code> if no comparable value can be derived.
		 */
		abstract T coerce(Object toCoerce);

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDataObject#isEqualTo(
		 *      java.lang.Object)
		 */
		public final boolean isEqualTo(Object object)
		{
			if (super.isEqualTo(object))
				return true;
			if (object == null)
				return false;
			Comparable<T> other = coerce(object);
			if (other == null)
				return false;
			return other.compareTo(toValue()) == 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractSessionScope.DataObject#
		 *      isGreaterThan(java.lang.Object)
		 */
		public final boolean isGreaterThan(Object object)
		{
			if (object == this)
				return false;
			if (object == null)
				return false;
			Comparable<T> other = coerce(object);
			if (other == null)
				return false;
			return other.compareTo(toValue()) < 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractSessionScope.DataObject#
		 *      isGreaterThanOrEqualTo(java.lang.Object)
		 */
		public final boolean isGreaterThanOrEqualTo(Object object)
		{
			if (object == this)
				return true;
			if (object == null)
				return false;
			Comparable<T> other = coerce(object);
			if (other == null)
				return false;
			return other.compareTo(toValue()) <= 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractSessionScope.DataObject#
		 *      isLessThan(java.lang.Object)
		 */
		public final boolean isLessThan(Object object)
		{
			if (object == this)
				return false;
			if (object == null)
				return false;
			Comparable<T> other = coerce(object);
			if (other == null)
				return false;
			return other.compareTo(toValue()) > 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractSessionScope.DataObject#
		 *      isLessThanOrEqualTo(java.lang.Object)
		 */
		public final boolean isLessThanOrEqualTo(Object object)
		{
			if (object == this)
				return true;
			if (object == null)
				return false;
			Comparable<T> other = coerce(object);
			if (other == null)
				return false;
			return other.compareTo(toValue()) >= 0;
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
		@SuppressWarnings("unchecked")
		public T toValue()
		{
			return (T)getEntry("value"); //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#hasEntry(java.lang.String)
		 */
		public final boolean hasEntry(String name)
		{
			return "value".equals(name) || super.hasEntry(name); //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#clearEntry(java.lang.String)
		 */
		public final boolean clearEntry(String name)
		{
			if ("value".equals(name)) //$NON-NLS-1$
				return setEntry(name, null);
			return super.clearEntry(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return String.valueOf(toValue());
		}
	}

	/**
	 * Implementation of array objects.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ArrayObject extends DataObject implements IArrayObject
	{
		/** The derived length field. */
		final NumberObject lengthField = new NumberObject()
		{
			public boolean isReadOnly()
			{
				return true;
			}

			public Integer getValue()
			{
				return new Integer(((String[])ArrayObject.this.load()).length);
			}
		};

		/**
		 * Creates a new ArrayObject.
		 * 
		 * @param id The id of the instance to load or <code>null</code> to create
		 *          a new object.
		 */
		ArrayObject()
		{
			super(VariableRegistry.this, null, dataTypeRegistry.getDataType(TYPE_NAME));
			save(new String[0]);
		}

		/**
		 * Creates a new ArrayObject.
		 * 
		 * @param id The id of the instance to load or <code>null</code> to create
		 *          a new object.
		 */
		ArrayObject(String id)
		{
			super(VariableRegistry.this, id, dataTypeRegistry.getDataType(TYPE_NAME));
			if (load() == null)
				save(new String[0]);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IArrayObject#getLength()
		 */
		public INumberObject getLength()
		{
			return lengthField;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IArrayObject#getItem(int)
		 */
		public IDataObject getElement(int index) throws IndexOutOfBoundsException
		{
			if (index < 0)
				throw new IndexOutOfBoundsException(String.valueOf(index));
			String[] itemIDs = (String[])load();
			if (index >= itemIDs.length)
				throw new IndexOutOfBoundsException(String.valueOf(index));
			return itemIDs[index] == null ? null : loadObject(itemIDs[index]);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IArrayObject#addItem(
		 *      org.eclipse.vtp.framework.core.IDataObject)
		 */
		public void addElement(IDataObject item) throws IllegalArgumentException,
				IllegalStateException
		{
			if (item != null && !(item instanceof DataObject))
				throw new IllegalArgumentException("item"); //$NON-NLS-1$
			String[] oldIDs = (String[])load();
			String[] newIDs = new String[oldIDs.length + 1];
			System.arraycopy(oldIDs, 0, newIDs, 0, oldIDs.length);
			newIDs[oldIDs.length] = item == null ? null : item.getId();
			save(newIDs);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IArrayObject#insertItem(int,
		 *      org.eclipse.vtp.framework.core.IDataObject)
		 */
		public void insertElement(int index, IDataObject item)
				throws IllegalArgumentException, IllegalStateException,
				IndexOutOfBoundsException
		{
			if (index < 0)
				throw new IndexOutOfBoundsException(String.valueOf(index));
			if (item != null && !(item instanceof DataObject))
				throw new IllegalArgumentException("item"); //$NON-NLS-1$
			String[] oldIDs = (String[])load();
			if (index > oldIDs.length)
				throw new IndexOutOfBoundsException(String.valueOf(index));
			String[] newIDs = new String[oldIDs.length + 1];
			if (index > 0)
				System.arraycopy(oldIDs, 0, newIDs, 0, index);
			newIDs[index] = item == null ? null : item.getId();
			if (index < oldIDs.length)
				System.arraycopy(oldIDs, index, newIDs, index + 1, oldIDs.length
						- index);
			save(newIDs);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IArrayObject#setItem(int,
		 *      org.eclipse.vtp.framework.core.IDataObject)
		 */
		public void setElement(int index, IDataObject item)
				throws IllegalArgumentException, IllegalStateException,
				IndexOutOfBoundsException
		{
			if (index < 0)
				throw new IndexOutOfBoundsException(String.valueOf(index));
			if (item != null && !(item instanceof DataObject))
				throw new IllegalArgumentException("item"); //$NON-NLS-1$
			String[] oldIDs = (String[])load();
			if (index >= oldIDs.length)
				throw new IndexOutOfBoundsException(String.valueOf(index));
			String[] newIDs = new String[oldIDs.length];
			System.arraycopy(oldIDs, 0, newIDs, 0, oldIDs.length);
			newIDs[index] = item == null ? null : item.getId();
			save(newIDs);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IArrayObject#removeItem(int)
		 */
		public void removeElement(int index) throws IllegalStateException,
				IndexOutOfBoundsException
		{
			if (index < 0)
				throw new IndexOutOfBoundsException(String.valueOf(index));
			String[] oldIDs = (String[])load();
			if (index >= oldIDs.length)
				throw new IndexOutOfBoundsException(String.valueOf(index));
			String[] newIDs = new String[oldIDs.length - 1];
			if (index > 0)
				System.arraycopy(oldIDs, 0, newIDs, 0, index);
			if (index < newIDs.length)
				System.arraycopy(oldIDs, index + 1, newIDs, index, newIDs.length
						- index);
			save(newIDs);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractVariableRegistry.DataObject#
		 *      getField(java.lang.String)
		 */
		public IDataObject getField(String fieldName)
		{
			return (FIELD_NAME_LENGTH.equals(fieldName) || "numberOfItems" //$NON-NLS-1$
			.equals(fieldName)) ? lengthField : super.getField(fieldName);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractVariableRegistry.DataObject#
		 *      isEqualTo(java.lang.Object)
		 */
		public boolean isEqualTo(Object object)
		{
			if (super.isEqualTo(object))
				return true;
			if (!(object instanceof IArrayObject))
				return false;
			IArrayObject other = (IArrayObject)object;
			String[] itemIDs = (String[])load();
			if (itemIDs.length != other.getLength().getValue().intValue())
				return false;
			for (int i = 0; i < itemIDs.length; ++i)
			{
				IDataObject ours = itemIDs[i] == null ? null : loadObject(itemIDs[i]);
				IDataObject theirs = other.getElement(i);
				if (ours == theirs)
					continue;
				if (ours == null || theirs == null)
					return false;
				if (!ours.isEqualTo(theirs))
					return false;
			}
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#hasItem(int)
		 */
		public boolean hasItem(int index)
		{
			return index >= 0 && index < ((String[])load()).length;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#getItem(int)
		 */
		public Object getItem(int index)
		{
			try
			{
				return getElement(index);
			}
			catch (IndexOutOfBoundsException e)
			{
				return null;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#setItem(int, java.lang.Object)
		 */
		public boolean setItem(int index, Object value)
		{
			if (!(value instanceof IDataObject))
			{
				try
				{
					IStringObject dataObject = (IStringObject)createVariable(IStringObject.TYPE_NAME);
					dataObject.setValue(value);
					value = dataObject;
				}
				catch (Exception e)
				{
					return false;
				}
			}
			if (index == ((String[])load()).length)
			{
				addElement((IDataObject)value);
				return true;
			}
			try
			{
				setElement(index, (IDataObject)value);
			}
			catch (IndexOutOfBoundsException e)
			{
				return false;
			}
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#clearItem(int)
		 */
		public boolean clearItem(int index)
		{
			try
			{
				removeElement(index);
			}
			catch (IndexOutOfBoundsException e)
			{
				return false;
			}
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			StringBuffer buffer = new StringBuffer().append('[');
			String[] itemIDs = (String[])load();
			for (int i = 0; i < itemIDs.length; ++i)
			{
				if (i > 0)
					buffer.append(',');
				buffer.append(itemIDs[i] == null ? null : loadObject(itemIDs[i]));
			}
			return buffer.append(']').toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#
		 *      getFunctionNames()
		 */
		public String[] getFunctionNames()
		{
			List<String> superFunctionNames = Arrays.asList(super.getFunctionNames());
			List<String> functionNames = new ArrayList<String>();
			functionNames.addAll(superFunctionNames);
			functionNames.add("add");
			functionNames.add("remove");
			functionNames.add("insert");
			functionNames.add("removeObject");

			String[] retArray = new String[functionNames.size()];
			System.arraycopy(functionNames.toArray(), 0, retArray, 0, functionNames.size());
			return retArray;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
		 *      java.lang.String, java.lang.Object[])
		 */
		public Object invokeFunction(String name, Object[] arguments) throws Exception
		{
			if("remove".equals(name))
			{
				try
				{
					return clearItem(Integer.parseInt((String)arguments[0]));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if("add".equals(name))
			{
				return setItem(lengthField.getValue(),arguments[0]);
			}
			else if("insert".equals(name))
			{
				try
				{
					IDataObject ido;
					if(arguments[1] instanceof IDataObject)
					{
						insertElement(Integer.parseInt((String)arguments[0]), (IDataObject)arguments[1]);
					}
					else
					{
						IStringObject dataObject = (IStringObject)createVariable(IStringObject.TYPE_NAME);
						dataObject.setValue(arguments[1]);
						ido = dataObject;
						insertElement(Integer.parseInt((String)arguments[0]), ido);
					}
					return true;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if("removeObject".equals(name))
			{
				try
				{
					return removeObject(arguments[0]);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			return super.invokeFunction(name, arguments);
		}

		public boolean removeObject(Object obj)
		{
			if(!(obj instanceof IDataObject))
			{
				return false;
			}

			String[] itemIDs = (String[])load();
			for(int b = 0; b < itemIDs.length; b++)
			{
				IDataObject ido = loadObject(itemIDs[b]);
				if(ido.isEqualTo(obj))
				{
					return clearItem(b);
				}
			}
			return false;
		}
	}
	
	/**
	 * Implementation of map objects.
	 */
	private final class MapObject extends DataObject implements IMapObject
	{
		/**
		 * Creates a new ArrayObject.
		 * 
		 * @param id The id of the instance to load or <code>null</code> to create
		 *          a new object.
		 */
		MapObject()
		{
			super(VariableRegistry.this, null, dataTypeRegistry.getDataType(TYPE_NAME));
			save(new String[0]);
		}

		/**
		 * Creates a new ArrayObject.
		 * 
		 * @param id The id of the instance to load or <code>null</code> to create
		 *          a new object.
		 */
		MapObject(String id)
		{
			super(VariableRegistry.this, id, dataTypeRegistry.getDataType(TYPE_NAME));
			if (load() == null)
				save(new String[0]);
		}

		public String[] getPropertyNames()
		{
			List<String> propNames = new ArrayList<String>();
			String[] oldIDs = (String[])load();
			for(int i = 0; i < oldIDs.length - 1; i+=2)
			{
				propNames.add(oldIDs[i]);
			}
			return propNames.toArray(new String[propNames.size()]);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IArrayObject#getLength()
		 */
		public INumberObject getSize()
		{
			INumberObject size = (INumberObject)createVariable(INumberObject.TYPE_NAME);
			size.setValue(new Integer(((String[])load()).length / 2));
			return size;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IMapObject#getElement(String)
		 */
		public IDataObject getField(String key)
		{
			if (key == null)
				throw new IllegalArgumentException("Key cannot be NULL.");
			String[] tuples = (String[])load();
			if (tuples.length % 2 != 0)
				throw new IllegalStateException("The map has become inconsistent.");
			for(int i = 0; i < tuples.length - 1; i += 2)
			{
				if(tuples[i].equals(key))
				{
					return loadObject(tuples[i + 1]);
				}
			}
			return super.getField(key);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IMapObject#putElement(String, 
		 *      org.eclipse.vtp.framework.core.IDataObject)
		 */
		public boolean setField(String key, IDataObject item)
		{
			if(key == null)
				throw new IllegalArgumentException("Key cannot be NULL.");
			if (item != null && !(item instanceof DataObject))
				throw new IllegalArgumentException("item"); //$NON-NLS-1$
			String[] oldIDs = (String[])load();
			for(int i = 0; i < oldIDs.length - 1; i+=2)
			{
				if(key.equals(oldIDs[i])) //already in map, overwrite
				{
					oldIDs[i+1] = item == null ? null : item.getId();
					save(oldIDs);
					return true;
				}
			}
			String[] newIDs = new String[oldIDs.length + 2];
			System.arraycopy(oldIDs, 0, newIDs, 0, oldIDs.length);
			newIDs[oldIDs.length] = key;
			newIDs[oldIDs.length + 1] = item == null ? null : item.getId();
			save(newIDs);
			return true;
		}
		
		@SuppressWarnings("rawtypes")
		public boolean setEntry(String name, Object value)
		{
			if (value instanceof IDataObject)
				return setField(name, (IDataObject)value);
			IDataObject variable = getField(name);
			if (variable == null)
				setField(name, variable = createVariable(IStringObject.TYPE_NAME));
			if (variable instanceof SimpleObject)
				((SimpleObject)variable).setEntry("value", value); //$NON-NLS-1$
			return true;
		}

		public Map<String, IDataObject> getValues()
		{
			Map<String, IDataObject> ret = new HashMap<String, IDataObject>();
			String[] oldIDs = (String[])load();
			for(int i = 0; i < oldIDs.length - 1; i+=2)
			{
				ret.put(oldIDs[i], loadObject(oldIDs[i + 1]));
			}
			return ret;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IMapObject#removeItem(String)
		 */
		public void removeElement(String key) throws IllegalStateException,
				IndexOutOfBoundsException
		{
			if (key == null)
				throw new IllegalArgumentException("Key cannot be NULL.");
			String[] oldIDs = (String[])load();
			int i = 0;
			for(; i < oldIDs.length - 1; i += 2)
			{
				if(oldIDs[i].equals(key))
				{
					String[] newIDs = new String[oldIDs.length - 2];
					if(i > 0)
						System.arraycopy(oldIDs, 0, newIDs, 0, i);
					if(oldIDs.length - 2 - i > 0)
						System.arraycopy(oldIDs, i + 2, newIDs, i, oldIDs.length - 2 - i);
					save(newIDs);
					return;
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractVariableRegistry.DataObject#
		 *      isEqualTo(java.lang.Object)
		 */
		public boolean isEqualTo(Object object)
		{
			if (super.isEqualTo(object))
				return true;
			if (!(object instanceof IMapObject))
				return false;
			IMapObject other = (IMapObject)object;
			return other == this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			StringBuffer buffer = new StringBuffer().append('[');
			String[] itemIDs = (String[])load();
			for (int i = 0; i < itemIDs.length; ++i)
			{
				if (i > 0)
					buffer.append(',');
				buffer.append(itemIDs[i] == null ? null : loadObject(itemIDs[i]));
			}
			return buffer.append(']').toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#
		 *      getFunctionNames()
		 */
		public String[] getFunctionNames()
		{
			List<String> superFunctionNames = Arrays.asList(super.getFunctionNames());
			List<String> functionNames = new ArrayList<String>();
			functionNames.addAll(superFunctionNames);
			functionNames.add("put");
			functionNames.add("get");
			functionNames.add("remove");
			functionNames.add("size");

			String[] retArray = new String[functionNames.size()];
			System.arraycopy(functionNames.toArray(), 0, retArray, 0, functionNames.size());
			return retArray;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
		 *      java.lang.String, java.lang.Object[])
		 */
		public Object invokeFunction(String name, Object[] arguments) throws Exception
		{
			if("remove".equals(name))
			{
				try
				{
					removeElement((String)arguments[0]);
					return true;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if("put".equals(name))
			{
				try
				{
					return setField((String)arguments[0], (IDataObject)arguments[1]);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if("get".equals(name))
			{
				try
				{
					return getField((String)arguments[0]);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if("size".equals(name))
			{
				return getSize();
			}
			return super.invokeFunction(name, arguments);
		}

	}

	/**
	 * Implementation of boolean objects.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class BooleanObject extends SimpleObject<Boolean> implements
			IBooleanObject
	{
		/**
		 * Creates a new BooleanObject.
		 */
		BooleanObject()
		{
			super(null, dataTypeRegistry.getDataType(TYPE_NAME));
		}

		/**
		 * Creates a new BooleanObject.
		 * 
		 * @param initialValue The initial value of this instance.
		 */
		BooleanObject(Object initialValue)
		{
			super(null, dataTypeRegistry.getDataType(TYPE_NAME));
			if (!setValue(initialValue))
				setValue(null);
		}

		/**
		 * Creates a new BooleanObject.
		 * 
		 * @param id The id of this instance.
		 */
		BooleanObject(String id)
		{
			super(id, dataTypeRegistry.getDataType(TYPE_NAME));
			if (getValue() == null)
				setValue(null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractVariableRegistry.SimpleObject#
		 *      coerce(java.lang.Object)
		 */
		Boolean coerce(Object obj)
		{
			if (obj == null)
				return Boolean.FALSE;
			if (obj instanceof Boolean)
				return (Boolean)obj;
			if (obj instanceof IBooleanObject)
				return ((IBooleanObject)obj).getValue();
			if (obj instanceof Number)
				return ((Number)obj).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
			if (obj instanceof INumberObject)
				return ((INumberObject)obj).getValue().intValue() == 0 ? Boolean.FALSE
						: Boolean.TRUE;
			String string = obj.toString();
			if ("true".equalsIgnoreCase(string)) //$NON-NLS-1$
				return Boolean.TRUE;
			if ("yes".equalsIgnoreCase(string)) //$NON-NLS-1$
				return Boolean.TRUE;
			if ("on".equalsIgnoreCase(string)) //$NON-NLS-1$
				return Boolean.TRUE;
			if ("1".equalsIgnoreCase(string)) //$NON-NLS-1$
				return Boolean.TRUE;
			if ("false".equalsIgnoreCase(string)) //$NON-NLS-1$
				return Boolean.FALSE;
			if ("no".equalsIgnoreCase(string)) //$NON-NLS-1$
				return Boolean.FALSE;
			if ("off".equalsIgnoreCase(string)) //$NON-NLS-1$
				return Boolean.FALSE;
			if ("0".equalsIgnoreCase(string)) //$NON-NLS-1$
				return Boolean.FALSE;
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IBooleanObject#getValue()
		 */
		public Boolean getValue()
		{
			return (Boolean)load();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IBooleanObject#setValue(
		 *      java.lang.Object)
		 */
		public boolean setValue(Object value) throws IllegalStateException
		{
			Boolean coerced = coerce(value);
			if (coerced == null)
				return false;
			save(coerced);
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#getEntry(java.lang.String)
		 */
		public Object getEntry(String name)
		{
			if ("value".equals(name)) //$NON-NLS-1$
				return getValue();
			return super.getEntry(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#setEntry(java.lang.String, java.lang.Object)
		 */
		public boolean setEntry(String name, Object value)
		{
			if ("value".equals(name)) //$NON-NLS-1$
				return setValue(value);
			return super.setEntry(name, value);
		}
	}

	/**
	 * Implementation of date objects.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class DateObject extends SimpleObject<Calendar> implements IDateObject
	{
		/**
		 * Creates a new DateObject.
		 */
		DateObject()
		{
			super(null, dataTypeRegistry.getDataType(TYPE_NAME));
		}

		/**
		 * Creates a new DateObject.
		 * 
		 * @param initialValue The initial value of this instance.
		 */
		DateObject(Object initialValue)
		{
			super(null, dataTypeRegistry.getDataType(TYPE_NAME));
			if (!setValue(initialValue))
				setValue(null);
		}

		/**
		 * Creates a new DateObject.
		 * 
		 * @param id The id of this instance.
		 */
		DateObject(String id)
		{
			super(id, dataTypeRegistry.getDataType(TYPE_NAME));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractVariableRegistry.SimpleObject#
		 *      coerce(java.lang.Object)
		 */
		Calendar coerce(Object obj)
		{
			Calendar cal = Calendar.getInstance();
			if (obj == null)
				return cal;
			if(obj instanceof Calendar)
				return (Calendar)obj;
			if (obj instanceof Date)
			{
				cal.setTime((Date)obj);
				return cal;
			}
			if (obj instanceof Long)
			{
				cal.setTimeInMillis((Long)obj);
				return cal;
			}
			if (obj instanceof IDateObject)
				return ((IDateObject)obj).getValue();
			if (obj instanceof String)
			{
				String inValue = (String)obj;
				context.debug("So the date is supposed to be: " + inValue);
				cal = DateHelper.parseDate(inValue);
				return cal;
			}
			return null;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDateObject#getValue()
		 */
		public Calendar getValue()
		{
			Calendar cal = Calendar.getInstance();
			String val = (String)load();
			cal = DateHelper.parseDate(val);
			return cal;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDateObject#
		 *      setValue(java.lang.Object)
		 */
		public boolean setValue(Object value) throws IllegalStateException
		{
			Calendar cal = coerce(value);
			if (cal == null)
				return false;
			save(DateHelper.toDateString(cal));
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#getEntry(java.lang.String)
		 */
		public Object getEntry(String name)
		{
			if ("value".equals(name)) //$NON-NLS-1$
				return getValue();
			return super.getEntry(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#setEntry(java.lang.String, java.lang.Object)
		 */
		public boolean setEntry(String name, Object value)
		{
			if ("value".equals(name)) //$NON-NLS-1$
				return setValue(value);
			return super.setEntry(name, value);
		}

		public String toString()
		{
			Calendar cal = getValue();
			return DateHelper.toDateString(cal);
		}
	}

	/**
	 * Implementation of decimal objects.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class DecimalObject extends SimpleObject<BigDecimal> implements
			IDecimalObject
	{
		/**
		 * Creates a new DecimalObject.
		 */
		DecimalObject()
		{
			super(null, dataTypeRegistry.getDataType(TYPE_NAME));
		}

		/**
		 * Creates a new DecimalObject.
		 * 
		 * @param initialValue The initial value of this instance.
		 */
		DecimalObject(Object initialValue)
		{
			super(null, dataTypeRegistry.getDataType(TYPE_NAME));
			if (!setValue(initialValue))
				setValue(null);
		}

		/**
		 * Creates a new DecimalObject.
		 * 
		 * @param id The id of this instance.
		 */
		DecimalObject(String id)
		{
			super(id, dataTypeRegistry.getDataType(TYPE_NAME));
			if (getValue() == null)
				setValue(null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractVariableRegistry.SimpleObject#
		 *      coerce(java.lang.Object)
		 */
		BigDecimal coerce(Object obj)
		{
			if (obj == null)
				return BigDecimal.valueOf(0L);
			if (obj instanceof BigDecimal)
				return (BigDecimal)obj;
			if (obj instanceof BigInteger)
				new BigDecimal(((BigInteger)obj));
			if (obj instanceof Float || obj instanceof Double)
				return BigDecimal.valueOf(((Number)obj).doubleValue());
			if (obj instanceof Number)
				return BigDecimal.valueOf(((Number)obj).longValue());
			if (obj instanceof IDecimalObject)
				return ((IDecimalObject)obj).getValue();
			if (obj instanceof INumberObject)
				return BigDecimal.valueOf(((INumberObject)obj).getValue().longValue());
			try
			{
				return new BigDecimal(obj.toString());
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDecimalObject#getValue()
		 */
		public BigDecimal getValue()
		{
			return (BigDecimal)load();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IDecimalObject#setValue(
		 *      java.lang.Object)
		 */
		public boolean setValue(Object value) throws IllegalStateException
		{
			BigDecimal coerced = coerce(value);
			if (coerced == null)
				return false;
			save(coerced);
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#getEntry(java.lang.String)
		 */
		public Object getEntry(String name)
		{
			if ("value".equals(name)) //$NON-NLS-1$
				return getValue();
			return super.getEntry(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#setEntry(java.lang.String, java.lang.Object)
		 */
		public boolean setEntry(String name, Object value)
		{
			if ("value".equals(name)) //$NON-NLS-1$
				return setValue(value);
			return super.setEntry(name, value);
		}
	}

	/**
	 * Implementation of number objects.
	 * 
	 * @author Lonnie Pryor
	 */
	private class NumberObject extends SimpleObject<Integer> implements INumberObject
	{
		/**
		 * Creates a new NumberObject.
		 */
		NumberObject()
		{
			super(null, dataTypeRegistry.getDataType(TYPE_NAME));
		}

		/**
		 * Creates a new NumberObject.
		 * 
		 * @param initialValue The initial value of this instance.
		 */
		NumberObject(Object initialValue)
		{
			super(null, dataTypeRegistry.getDataType(TYPE_NAME));
			if (!setValue(initialValue))
				setValue(null);
		}

		/**
		 * Creates a new NumberObject.
		 * 
		 * @param id The id of this instance.
		 */
		NumberObject(String id)
		{
			super(id, dataTypeRegistry.getDataType(TYPE_NAME));
			if (getValue() == null)
				setValue(null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractVariableRegistry.SimpleObject#
		 *      coerce(java.lang.Object)
		 */
		Integer coerce(Object obj)
		{
			if (obj == null)
				return new Integer(0);
			if (obj instanceof Integer)
				return (Integer)obj;
			if (obj instanceof Number)
				return new Integer(((Number)obj).intValue());
			if (obj instanceof INumberObject)
				return ((INumberObject)obj).getValue();
			try
			{
				return new Integer(obj.toString());
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				return null;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.INumberObject#getValue()
		 */
		public Integer getValue()
		{
			return (Integer)load();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.INumberObject#setValue(
		 *      java.lang.Object)
		 */
		public boolean setValue(Object value) throws IllegalStateException
		{
			Integer coerced = coerce(value);
			if (coerced == null)
				return false;
			save(coerced);
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#getEntry(java.lang.String)
		 */
		public Object getEntry(String name)
		{
			if ("value".equals(name)) //$NON-NLS-1$
				return getValue();
			return super.getEntry(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#setEntry(java.lang.String, java.lang.Object)
		 */
		public boolean setEntry(String name, Object value)
		{
			if ("value".equals(name)) //$NON-NLS-1$
				return setValue(value);
			return super.setEntry(name, value);
		}
	}

	/**
	 * Implementation of string objects.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class StringObject extends SimpleObject<String> implements
			IStringObject
	{
		/** The derived length field. */
		final NumberObject lengthField = new NumberObject()
		{
			public boolean isReadOnly()
			{
				return true;
			}

			public Integer getValue()
			{
				return new Integer(StringObject.this.getValue().length());
			}
		};

		/**
		 * Creates a new StringObject.
		 */
		StringObject()
		{
			super(null, dataTypeRegistry.getDataType(TYPE_NAME));
		}

		/**
		 * Creates a new StringObject.
		 * 
		 * @param initialValue The initial value of this instance.
		 */
		StringObject(Object initialValue)
		{
			super(null, dataTypeRegistry.getDataType(TYPE_NAME));
			if (!setValue(initialValue))
				setValue(null);
		}

		/**
		 * Creates a new StringObject.
		 * 
		 * @param id The id of this instance.
		 */
		StringObject(String id)
		{
			super(id, dataTypeRegistry.getDataType(TYPE_NAME));
			if (getValue() == null)
				setValue(null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractVariableRegistry.SimpleObject#
		 *      coerce(java.lang.Object)
		 */
		String coerce(Object obj)
		{
			if (obj == null)
				return new String();
			if (obj instanceof IStringObject)
				return ((IStringObject)obj).getValue();
			return obj.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IStringObject#getLength()
		 */
		public INumberObject getLength()
		{
			return lengthField;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IStringObject#getValue()
		 */
		public String getValue()
		{
			return (String)load();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IStringObject#setValue(
		 *      java.lang.Object)
		 */
		public boolean setValue(Object value) throws IllegalStateException
		{
			String coerced = coerce(value);
			if (coerced == null)
				return false;
			save(coerced);
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractVariableRegistry.DataObject#
		 *      getField(java.lang.String)
		 */
		public IDataObject getField(String fieldName)
		{
			return FIELD_NAME_LENGTH.equals(fieldName) ? lengthField : super
					.getField(fieldName);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#getEntry(java.lang.String)
		 */
		public Object getEntry(String name)
		{
			if ("value".equals(name)) //$NON-NLS-1$
				return getValue();
			return super.getEntry(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#setEntry(java.lang.String, java.lang.Object)
		 */
		public boolean setEntry(String name, Object value)
		{
			if ("value".equals(name)) //$NON-NLS-1$
				return setValue(value);
			return super.setEntry(name, value);
		}
	}

	/**
	 * Implementation of user-defined complex objects.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ComplexObject extends DataObject
	{
		/** The names of the fields of this instance. */
		private final String[] fieldNames;

		/**
		 * Creates a new ComplexObject.
		 * 
		 * @param type The type of this instance.
		 */
		ComplexObject(IDataType type)
		{
			super(VariableRegistry.this, null, type);
			this.fieldNames = type.getFieldNames();
			String[] fieldIDs = new String[fieldNames.length];
			for (int i = 0; i < fieldNames.length; ++i)
			{
				fieldIDs[i] = createObject(type.getFieldType(fieldNames[i]), type
						.getFieldInitialValue(fieldNames[i]), type.isFieldSecured(fieldNames[i])).getId();
			}
			save(fieldIDs);
		}

		/**
		 * Creates a new ComplexObject.
		 * 
		 * @param id The id of this instance.
		 * @param type The type of this instance.
		 */
		ComplexObject(String id, IDataType type)
		{
			super(VariableRegistry.this, id, type);
			this.fieldNames = type.getFieldNames();
			String[] fieldIDs = (String[])load();
			if (fieldIDs == null)
			{
				fieldIDs = new String[fieldNames.length];
				for (int i = 0; i < fieldNames.length; ++i)
					fieldIDs[i] = createObject(type.getFieldType(fieldNames[i]), type
							.getFieldInitialValue(fieldNames[i]), type.isFieldSecured(fieldNames[i])).getId();
				save(fieldIDs);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractVariableRegistry.DataObject#
		 *      getField(java.lang.String)
		 */
		public IDataObject getField(String fieldName)
		{
			for (int i = 0; i < fieldNames.length; ++i)
			{
				if (fieldNames[i].equals(fieldName))
				{
					String id = ((String[])load())[i];
					if (id == null)
						return null;
					return loadObject(id);
				}
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#setField(java.lang.String,
		 *      org.eclipse.vtp.framework.core.IDataObject)
		 */
		public boolean setField(String fieldName, IDataObject variable)
		{
			if (variable != null)
			{
				if (!(variable instanceof DataObject))
					return false;
				if (!variable.getType().equals(type.getFieldType(fieldName)))
					return false;
			}
			for (int i = 0; i < fieldNames.length; ++i)
			{
				if (fieldNames[i].equals(fieldName))
				{
					if(type.isFieldSecured(fieldName))
						variable.setSecured(type.isFieldSecured(fieldName));
					String[] oldIDs = (String[])load();
					String[] newIDs = new String[oldIDs.length];
					System.arraycopy(oldIDs, 0, newIDs, 0, oldIDs.length);
					newIDs[i] = variable == null ? null : variable.getId();
					save(newIDs);
					return true;
				}
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.AbstractVariableRegistry.DataObject#
		 *      isEqualTo(java.lang.Object)
		 */
		public boolean isEqualTo(Object object)
		{
			if (super.isEqualTo(object))
				return true;
			if (!(object instanceof IDataObject))
				return false;
			IDataObject other = (IDataObject)object;
			if (!type.equals(other.getType()))
				return false;
			String[] fieldIDs = (String[])load();
			for (int i = 0; i < fieldNames.length; ++i)
			{
				IDataObject ours = fieldIDs[i] == null ? null : loadObject(fieldIDs[i]);
				IDataObject theirs = other.getField(fieldNames[i]);
				if (ours == theirs)
					continue;
				if (ours == null || theirs == null)
					return false;
				if (!ours.isEqualTo(theirs))
					return false;
			}
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#hasValue()
		 */
		public boolean hasValue()
		{
			return type.getPrimaryFieldName() != null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.support.AbstractVariableRegistry.
		 *      DataObject#toValue()
		 */
		public Object toValue()
		{
			return getField(type.getPrimaryFieldName());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#
		 *      getFunctionNames()
		 */
		public String[] getFunctionNames()
		{
			String[] superFunctions = super.getFunctionNames();
			String[] result = new String[superFunctions.length + 3];
			System.arraycopy(superFunctions, 0, result, 0, superFunctions.length);
			result[superFunctions.length] = "getFieldCount"; //$NON-NLS-1$
			result[superFunctions.length + 1] = "getFieldValue"; //$NON-NLS-1$
			result[superFunctions.length + 2] = "setFieldValue"; //$NON-NLS-1$
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
		 *      java.lang.String, java.lang.Object[])
		 */
		public Object invokeFunction(String name, Object[] arguments) throws Exception
		{
			if ("getFieldCount".equals(name)) //$NON-NLS-1$
				return fieldNames.length;
			if (arguments.length == 1 && "getFieldValue".equals(name)) { //$NON-NLS-1$
				String fieldName = coerceFieldName(arguments[0]);
				return fieldName == null ? null : getEntry(fieldName);
			}
			if (arguments.length == 2 && "setFieldValue".equals(name)) { //$NON-NLS-1$
				String fieldName = coerceFieldName(arguments[0]);
				return fieldName == null ? null :  setEntry(fieldName, arguments[1]);
			}
			return super.invokeFunction(name, arguments);
		}
		
		/**
		 * Utility function to coerce a field name from an index value.
		 */
		private String coerceFieldName(Object obj) {
			int index = -1;
			if (obj instanceof Number)
				index = ((Number) obj).intValue();
			else if (obj instanceof INumberObject)
				index = ((INumberObject) obj).getValue();
			else {
				try {
					index = Integer.parseInt(obj.toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			return index < 0 || index >= fieldNames.length ? null : fieldNames[index];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			StringBuffer buffer = new StringBuffer(type.getName()).append('{');
			String[] fieldIDs = (String[])load();
			for (int i = 0; i < fieldNames.length; ++i)
			{
				if (i > 0)
					buffer.append(',');
				buffer.append(fieldNames[i]).append('=').append(
						fieldIDs[i] == null ? null : loadObject(fieldIDs[i]));
			}
			return buffer.append('}').toString();
		}
	}

	@Override
	public boolean isMutable()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
