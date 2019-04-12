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
package org.eclipse.vtp.modules.database.ui.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.IBusinessObject;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectSet;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DatabaseQuerySettingsStructure implements Cloneable
{
	IBusinessObjectSet objectSet;
	List<DatabaseQuerySettingsListener> listeners = new ArrayList<DatabaseQuerySettingsListener>();

	//target settings
	boolean targetVariableExists = true;
	String targetVariableName = null;
	FieldType targetVariableType = null;
	boolean targetVariableSecure = false;

	//source settings
	String sourceDatabase = null;
	String sourceDatabaseTable = null;

	//data mapping
	List<DataMapping> dataMapping = new ArrayList<DataMapping>();

	//selection criteria
	List<SelectionCriteria> criteria = new ArrayList<SelectionCriteria>();
	int resultLimit = -1;
	
	public DatabaseQuerySettingsStructure(IBusinessObjectSet objectSet)
	{
		super();
		this.objectSet = objectSet;
	}

	public boolean isTargetVariableExists()
    {
    	return targetVariableExists;
    }

	public void setTargetVariableExists(boolean targetVariableExists)
    {
    	this.targetVariableExists = targetVariableExists;
    }
	
	public boolean isTargetVariableSecure()
	{
		return this.targetVariableSecure;
	}
	
	public void setTargetVariableSecure(boolean secure)
	{
		this.targetVariableSecure = secure;
	}

	public String getTargetVariableName()
    {
    	return targetVariableName;
    }

	public void setTargetVariableName(String targetVariableName)
    {
    	this.targetVariableName = targetVariableName;
    }

	public FieldType getTargetVariableType()
    {
    	return targetVariableType;
    }

	public void setTargetVariableType(FieldType targetVariableType)
    {
    	this.targetVariableType = targetVariableType;
    }

	public boolean isComplete()
	{
		if((targetVariableName == null) || targetVariableName.equals(""))
		{
			return false;
		}

		if((targetVariableType == null) || targetVariableType.equals(""))
		{
			return false;
		}

		if((sourceDatabase == null) || sourceDatabase.equals(""))
		{
			return false;
		}

		if((sourceDatabaseTable == null) || sourceDatabaseTable.equals(""))
		{
			return false;
		}

		return true;
	}

	public Object clone()
	{
		DatabaseQuerySettingsStructure copy =
			new DatabaseQuerySettingsStructure(objectSet);
		copy.targetVariableExists = targetVariableExists;
		copy.targetVariableName = targetVariableName;
		copy.targetVariableType = targetVariableType;
		copy.targetVariableSecure = targetVariableSecure;
		copy.sourceDatabase = sourceDatabase;
		copy.sourceDatabaseTable = sourceDatabaseTable;

		Iterator<DataMapping> iterator = dataMapping.iterator();

		while(iterator.hasNext())
		{
			copy.dataMapping.add((DataMapping)iterator.next().clone());
		}

		Iterator<SelectionCriteria> iterator2 = criteria.iterator();

		while(iterator2.hasNext())
		{
			copy.criteria.add((SelectionCriteria)iterator2.next().clone());
		}

		copy.resultLimit = resultLimit;

		return copy;
	}

	public void read(Element element)
	{
		targetVariableExists = Boolean.valueOf(element.getAttribute(
					"var-exists")).booleanValue();
		targetVariableName = element.getAttribute("var-name");
		if(element.getAttributeNode("var-type") != null) //legacy support
		{
			String type = element.getAttribute("var-type");
			boolean array = Integer.parseInt(element.getAttribute("var-multi")) > 0;
			if(array)
			{
				Primitive prim = Primitive.find(type);
				if(prim != null)
				{
					targetVariableType = new FieldType(Primitive.ARRAY, prim);
				}
				else
				{
					IBusinessObject bo = objectSet.getBusinessObject(type);
					targetVariableType = new FieldType(Primitive.ARRAY, bo);
				}
			}
			else
			{
				Primitive prim = Primitive.find(type);
				if(prim != null)
				{
					targetVariableType = new FieldType(prim);
				}
				else
				{
					IBusinessObject bo = objectSet.getBusinessObject(type);
					targetVariableType = new FieldType(bo);
				}
			}
		}
		else
		{
			List<Element> typeElements = XMLUtilities.getElementsByTagName(element, "data-type", true);
			if(typeElements.size() > 0)
			{
				targetVariableType = FieldType.load(objectSet, typeElements.get(0));
			}
		}
		targetVariableSecure = Boolean.parseBoolean(element.getAttribute("var-secured"));
		sourceDatabase = element.getAttribute("db-name");
		sourceDatabaseTable = element.getAttribute("db-table");
		resultLimit = Integer.parseInt(element.getAttribute("db-result-limit"));

		NodeList nl = element.getElementsByTagName("mapping");

		for(int i = 0; i < nl.getLength(); i++)
		{
			Element dmElement = (Element)nl.item(i);
			DataMapping dm =
				new DataMapping(dmElement.getAttribute("name"),
					Integer.parseInt(dmElement.getAttribute("type")),
					dmElement.getAttribute("value"));
			dataMapping.add(dm);
		}

		nl = element.getElementsByTagName("criterium");

		for(int i = 0; i < nl.getLength(); i++)
		{
			Element scElement = (Element)nl.item(i);
			SelectionCriteria sc =
				new SelectionCriteria(scElement.getAttribute("name"));
			sc.comparison = Integer.parseInt(scElement.getAttribute("comp"));
			sc.type = Integer.parseInt(scElement.getAttribute("type"));
			sc.value = scElement.getAttribute("value");
			criteria.add(sc);
		}
	}

	public void write(Element element)
	{
		element.setAttribute("var-exists",
			Boolean.toString(targetVariableExists));
		element.setAttribute("var-name",
			(targetVariableName == null) ? "" : targetVariableName);
		if(targetVariableType != null)
			targetVariableType.write(element);
		element.setAttribute("var-secured", Boolean.toString(targetVariableSecure));
		element.setAttribute("db-name",
			(sourceDatabase == null) ? "" : sourceDatabase);
		element.setAttribute("db-table",
			(sourceDatabaseTable == null) ? "" : sourceDatabaseTable);
		element.setAttribute("db-result-limit", Integer.toString(resultLimit));

		Element mappingsElement =
			element.getOwnerDocument().createElement("mappings");
		element.appendChild(mappingsElement);

		for(int i = 0; i < dataMapping.size(); i++)
		{
			DataMapping dm = dataMapping.get(i);
			Element dmElement =
				mappingsElement.getOwnerDocument().createElement("mapping");
			mappingsElement.appendChild(dmElement);
			dmElement.setAttribute("name",
				(dm.fieldName == null) ? "" : dm.fieldName);
			dmElement.setAttribute("type", Integer.toString(dm.mappingType));
			dmElement.setAttribute("value",
				(dm.mappingValue == null) ? "" : dm.mappingValue);
		}

		Element criteriaElement =
			element.getOwnerDocument().createElement("criteria");
		element.appendChild(criteriaElement);

		for(int i = 0; i < criteria.size(); i++)
		{
			SelectionCriteria sc = criteria.get(i);
			Element scElement =
				criteriaElement.getOwnerDocument().createElement("criterium");
			criteriaElement.appendChild(scElement);
			scElement.setAttribute("name",
				(sc.columnName == null) ? "" : sc.columnName);
			scElement.setAttribute("comp", Integer.toString(sc.comparison));
			scElement.setAttribute("type", Integer.toString(sc.type));
			scElement.setAttribute("value", (sc.value == null) ? "" : sc.value);
		}
	}

	public void addSettingsListener(DatabaseQuerySettingsListener l)
	{
		listeners.remove(l);
		listeners.add(l);
	}

	public void fireTargetChanged()
	{
		Iterator<DatabaseQuerySettingsListener> iterator = listeners.iterator();

		while(iterator.hasNext())
		{
			iterator.next().targetVariableChanged();
		}
	}

	public void fireSourceChanged()
	{
		Iterator<DatabaseQuerySettingsListener> iterator = listeners.iterator();

		while(iterator.hasNext())
		{
			iterator.next().sourceDatabaseChanged();
		}
	}

	public void fireDataMappingChanged()
	{
		Iterator<DatabaseQuerySettingsListener> iterator = listeners.iterator();

		while(iterator.hasNext())
		{
			iterator.next().dataMappingChanged();
		}
	}

	public void fireSearchCriteriaChanged()
	{
		Iterator<DatabaseQuerySettingsListener> iterator = listeners.iterator();

		while(iterator.hasNext())
		{
			iterator.next().searchCriteriaChanged();
		}
	}

	public class DataMapping implements Cloneable
	{
		String fieldName;
		int mappingType = 0;
		String mappingValue;

		public DataMapping(String fieldName, int mappingType,
			String mappingValue)
		{
			super();
			this.fieldName = fieldName;
			this.mappingType = mappingType;
			this.mappingValue = mappingValue;
		}

		public Object clone()
		{
			DataMapping copy =
				new DataMapping(fieldName, mappingType, mappingValue);

			return copy;
		}
	}

	public class SelectionCriteria implements Cloneable
	{
		String columnName = null;
		int comparison = -1;
		int type = -1;
		String value = null;

		public SelectionCriteria(String columnName)
		{
			super();
			this.columnName = columnName;
		}

		public Object clone()
		{
			SelectionCriteria copy = new SelectionCriteria(columnName);
			copy.comparison = comparison;
			copy.type = type;
			copy.value = value;

			return copy;
		}
	}
}
