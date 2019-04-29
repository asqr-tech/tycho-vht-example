/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.modules.attacheddata.ui.configuration.post;

/**
 * This class records the details of which attached data point is being
 * submitted.  It contains the name of the data point as well as the location
 * the value submitted should be retrieved from.
 * 
 * @author trip
 */
public class AttachedDataItemEntry
{
	/** Constant to indicate the value is hard-coded */
	public static final int TYPE_STATIC = 0;
	/** Constant to indicate the value is a script expression */
	public static final int TYPE_EXPRESSION = 1;
	/** Constant to indicate the value is stored in a variable */
	public static final int TYPE_VARIABLE = 2;
	public static final int TYPE_MAP = 4;
	
	/** The name of the attached data being submitted */
	private String name = null;
	/** The type of value being submitted */
	private int dataType = 0;
	/** The value, expression, or variable name if the data */
	private String value = null;

	/**
	 * Constructs a new <code>AttachedDataItemEntry</code>.  The name and value
	 * fields are initially null and need to be assigned using the available
	 * setters.
	 */
	public AttachedDataItemEntry()
	{
		super();
	}
	
	/**
	 * Sets the static value, expression text, or variable name the value of
	 * this data entry is stored in.
	 * 
	 * @param value The value used to derive the data being submitted
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @return The data used to derive the value of this entry
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Retrieves the name of the attached data entry being submitted.
	 * 
	 * @return The name of the attached data entry
	 */
	public String getName()
    {
    	return name;
    }

	/**
	 * Set the name of the attached data entry being submitted.
	 * 
	 * @param name The name of the attached data entry
	 */
	public void setName(String name)
    {
    	this.name = name;
    }

	/**
	 * @return The data type of the value being submitted
	 */
	public int getDataType()
    {
    	return dataType;
    }

	/**
	 * Sets the data type of the value being submitted.
	 * 
	 * @param dataType The data type of the value
	 */
	public void setDataType(int dataType)
    {
    	this.dataType = dataType;
    }
}
