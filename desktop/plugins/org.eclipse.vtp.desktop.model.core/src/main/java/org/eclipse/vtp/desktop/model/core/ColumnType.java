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
package org.eclipse.vtp.desktop.model.core;

import org.w3c.dom.Element;

/**
 * This class encapsulates the possible data types assigned to database
 * table columns.  Only the standard SQL-92 types are supported.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class ColumnType
{
	/**
	 * Definition of a typical VARCHAR column with 45 characters.
	 */
	public static final ColumnType VARCHAR = new ColumnType("String", 45);

	/**
	 * Definition of a typical 4 byte NUMBER column.
	 */
	public static final ColumnType NUMBER = new ColumnType("Number", 4);

	/**
	 * Definition of a typical 8 byte BIGNUMBER column.
	 */
	public static final ColumnType BIGNUMBER = new ColumnType("Big Number", 8);

	/**
	 * Definition of a typical 4 byte DECIMAL column.
	 */
	public static final ColumnType DECIMAL = new ColumnType("Decimal", 4);

	/**
	 * Definition of a typical 8 byte BIGDECIMAL column.
	 */
	public static final ColumnType BIGDECIMAL =
		new ColumnType("Big Decimal", 8);

	/**
	 * Definition of a typical BOOLEAN column.
	 */
	public static final ColumnType BOOLEAN = new ColumnType("Boolean", 1);

	/**
	 * Definition of a typical DATETIME column.
	 */
	public static final ColumnType DATETIME = new ColumnType("DateTime", 8);

	/**
	 * Definition of a typical TEXT column.
	 */
	public static final ColumnType TEXT = new ColumnType("Text", 16);

	/**
	 * The name of the data type.
	 */
	private String typeName;

	/**
	 * The number of bytes used for storage of the value
	 */
	private int length;

	/**
	 * A flag indicating wheather a NULL value is acceptable.  Default is <code>true</code>.
	 */
	private boolean nullable;

	/**
	 * A flag indicating wheather or not the column will automatically increase
	 * in value each time a new record is inserted into the table.  Default is
	 * <code>false</code>.
	 */
	private boolean autoIncrement;

	/**
	 * Creates a new <code>ColumnType</code> with the given type name and
	 * data storage length.
	 *
	 * @param typeName The name of the column data type
	 * @param length The number of bytes used to store the column values
	 */
	protected ColumnType(String typeName, int length)
	{
		super();
		this.typeName = typeName;
		this.length = length;
		nullable = true;
		autoIncrement = false;
	}

	/**
	 * Determines if the data type of the column can also be configured to
	 * auto increment.
	 *
	 * @return <code>true</code> if the column is a NUMBER | BIGNUMBER, <code>false</code>
	 * otherwise.
	 */
	public boolean canAutoIncrement()
	{
		return typeName.equals("Number") || typeName.equals("Big Number");
	}

	/**
	 * @return The number of bytes used to store the column data type values
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * Determines if the column can accept or contain NULL values.
	 *
	 * @return <code>true</code> if the column will accept or contain NULL values,
	 * <code>false</code> otherwise.
	 */
	public boolean isNullable()
	{
		return nullable;
	}

	/**
	 * @return <code>true</code> if the column is configured to auto increment, <code>false</code>
	 * otherwise.
	 */
	public boolean isAutoIncrement()
	{
		return autoIncrement;
	}

	/**
	 * @return The data type name of this column type
	 */
	public String getTypeName()
	{
		return typeName;
	}

	/**
	 * Generates the XML DOM elements and attributes required to save the information
	 * for this column type.
	 *
	 * @param parent The parent XML DOM element that will contain the column type information
	 */
	public void write(Element parent)
	{
		Element dt = parent.getOwnerDocument().createElement("column-type");
		parent.appendChild(dt);
		dt.setAttribute("type", typeName);
		dt.setAttribute("length", Integer.toString(length));
		dt.setAttribute("nullable", Boolean.toString(nullable));
		dt.setAttribute("autoIncrement", Boolean.toString(autoIncrement));
	}

	/**
	 * Constructs a new <code>ColumnType</code> from the given type name, data
	 * length, nullable flag, and auto increment flag.  This is the primary
	 * method of instanciating this class.
	 *
	 * @param typeName The data type name for the column type
	 * @param length The number of bytes used to store this column types values
	 * @param nullable A flag indicating whether this column type will accept or contain NULL values
	 * @param autoIncrement A flag indicating whether this column type will automatically
	 * increment when new records are added to its database table
	 * @return A <code>ColumnType</code> instance representing the given column type information
	 */
	public static ColumnType custom(String typeName, int length,
		boolean nullable, boolean autoIncrement)
	{
		ColumnType ret = new ColumnType(typeName, length);
		ret.nullable = nullable;
		ret.autoIncrement = autoIncrement;

		return ret;
	}

	/**
	 * Constructs a new <code>ColumnType</code> from the information stored in the
	 * given XML DOM sturcture.  The information must be stored the same format
	 * produced by the write() function of this class.
	 *
	 * @param element The element that contains the formated data
	 * @return The new <code>ColumnType</code> instance represented by the given
	 * column type information
	 */
	public static ColumnType load(Element element)
	{
		if(!element.getTagName().equals("column-type"))
		{
			throw new IllegalArgumentException("DesignElement Format Exception");
		}

		String typeName = element.getAttribute("type");
		int length = Integer.parseInt(element.getAttribute("length"));
		ColumnType ret = new ColumnType(typeName, length);
		ret.nullable = Boolean.valueOf(element.getAttribute("nullable"))
							  .booleanValue();
		ret.autoIncrement = Boolean.valueOf(element.getAttribute(
					"autoIncrement")).booleanValue();

		return ret;
	}
}
