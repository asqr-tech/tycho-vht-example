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
package org.eclipse.vtp.modules.attacheddata.ui.configuration.request;

/**
 * This class records the details of which attached data point is being
 * requested.  It contains the name of the data point as well as the location
 * the value retrieved should be stored in.
 * 
 * @author trip
 */
public class AttachedDataItemEntry
{
	/** The name of this attached data item entry */
	private String name = null;
	/**	The name of the variable to store the value into */
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
	 * Sets the variable name the value of this attached data entry will be
	 * stored into.
	 * 
	 * @param value The name of the variable to place the value into
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Retrieves the name of the variable that will hold the attached data value.
	 * 
	 * @return The name of the variable that will hold the value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Retrieves the name of the attached data entry to be requested.
	 * 
	 * @return The name of the attached data entry
	 */
	public String getName()
    {
    	return name;
    }

	/**
	 * Set the name of the attached data entry to be requested.
	 * 
	 * @param name The name of the attached data entry
	 */
	public void setName(String name)
    {
    	this.name = name;
    }

}
