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
package org.eclipse.vtp.desktop.model.core.design;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.FieldType;

/**
 * @author Trip
 *
 */
public class ObjectDefinition implements PropertyChangeListener
{
	public static final String PROP_NAME = "name";
	public static final String PROP_TYPE = "type";
	public static final String PROP_FIELDS = "fields";
	
	String name;
	FieldType type;
	List<ObjectField> fields;
	String defaultValue;
	boolean secure = false;
	List<PropertyChangeListener> listeners = new LinkedList<PropertyChangeListener>();

	/**
	 * @param name
	 * @param type
	 */
	public ObjectDefinition(String name, FieldType type)
	{
		super();
		this.name = name;
		this.type = type;
		fields = new ArrayList<ObjectField>();
	}

	/**
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		String oldName = this.name;
		this.name = name;
		postChange(PROP_NAME, oldName, name);
	}

	/**
	 * @return
	 */
	public FieldType getType()
	{
		return type;
	}
	
	public void setType(FieldType type)
	{
		FieldType oldType = this.type;
		this.type = type;
		fields = new ArrayList<ObjectField>();
		postChange(PROP_TYPE, oldType, type);
	}

	/**
	 * @return
	 */
	public List<ObjectField> getFields()
	{
		return fields;
	}

	/**
	 * @return
	 */
	public String getPath()
	{
		return getName();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if(obj instanceof ObjectDefinition)
		{
			ObjectDefinition od = (ObjectDefinition)obj;

			return od.getPath().equals(getPath());
		}
		else
		{
			return false;
		}
	}

	/**
	 * @return
	 */
	public String getDefaultValue()
    {
    	return defaultValue;
    }

	/**
	 * @param defaultValue
	 */
	public void setDefaultValue(String defaultValue)
    {
    	this.defaultValue = defaultValue;
    }

	/**
	 * @return
	 */
	public boolean isSecure()
	{
		return secure;
	}
	
	/**
	 * @param secure
	 */
	public void setSecure(boolean secure)
	{
		this.secure = secure;
	}

	/**
	 * @param field
	 * @return
	 */
	public ObjectField addField(ObjectField field)
	{
		List<ObjectField> oldFields = new ArrayList<ObjectField>(fields);
		boolean inserted = false;
		for(int i = 0; i < fields.size(); i++)
		{
			if(fields.get(i).getName().compareToIgnoreCase(field.getName()) > 0)
			{
				fields.add(i, field);
				inserted = true;
				break;
			}
		}
		if(!inserted)
			fields.add(field);
		field.setParent(this);
		postChange(PROP_FIELDS, oldFields, fields);
		return field;
	}
	
	public void clearFields()
	{
		List<ObjectField> oldFields = fields;
		fields = new ArrayList<ObjectField>();
		for(ObjectField field : oldFields)
		{
			field.removePropertyChangeListener(this);
		}
		postChange(PROP_FIELDS, oldFields, fields);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.remove(listener);
	}
	
	private void postChange(String property, Object oldValue, Object newValue)
	{
		PropertyChangeEvent evt = new PropertyChangeEvent(this, property, oldValue, newValue);
		for(PropertyChangeListener listener : listeners)
		{
			listener.propertyChange(evt);
		}
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		if(PROP_NAME.equals(evt.getPropertyName()))
		{
			List<ObjectField> oldFields = new ArrayList<ObjectField>(fields);
			ObjectField field = (ObjectField)evt.getSource();
			for(int i = 0; i < fields.size(); i++)
			{
				if(fields.get(i).getName().equals(evt.getOldValue()))
				{
					fields.remove(i);
					break;
				}
			}
			boolean inserted = false;
			for(int i = 0; i < fields.size(); i++)
			{
				if(fields.get(i).getName().compareToIgnoreCase(field.getName()) > 0)
				{
					fields.add(i, field);
					inserted = true;
					break;
				}
			}
			if(!inserted)
				fields.add(field);
			postChange(PROP_FIELDS, oldFields, fields);
		}
	}
}
