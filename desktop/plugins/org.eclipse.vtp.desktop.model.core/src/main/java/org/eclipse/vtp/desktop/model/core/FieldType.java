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

import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class encapsulates the possible data types of business object fields.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class FieldType
{
	public enum Primitive
	{
		ANY("ANYTYPE"), STRING("String"), NUMBER("Number"),
		DECIMAL("Decimal"), BOOLEAN("Boolean"), DATETIME("DateTime"),
		ARRAY("Array"), MAP("Map");
		
		private String val;
		
		private Primitive(String val)
		{
			this.val = val;
		}
		
		public boolean hasBaseType()
		{
			switch(this)
			{
				case ARRAY:
				case MAP:
					return true;
			}
			return false;
		}
		
		public boolean isMainType()
		{
			switch(this)
			{
				case ANY:
					return false;
			}
			return true;
		}
		
		public boolean isBaseType()
		{
			switch(this)
			{
				case ARRAY:
				case MAP:
					return false;
			}
			return true;
		}
		
		public String getName()
		{
			return val;
		}

		/**
		 * Determines whether or not the precision value is applicable to this
		 * field type.
		 *
		 * @return <code>true</code> if this field type has a precision, <code>false</code>
		 * otherwise
		 */
		public boolean hasPrecision()
		{
			switch(this)
			{
				case NUMBER:
				case DECIMAL:
					return true;
			}
			return false;
		}
		
		public boolean hasValue()
		{
			switch(this)
			{
				case STRING:
				case NUMBER:
				case DECIMAL:
				case BOOLEAN:
				case DATETIME:
					return true;
			}
			return false;
		}
		
		public static Primitive find(String name)
		{
			for(Primitive prim : Primitive.values())
			{
				if(prim.getName().equals(name))
					return prim;
			}
			return null;
		}
	}

	/**
	 * Declaration of a typical STRING field type.
	 */
	public static final FieldType STRING = new FieldType(Primitive.STRING);

	/**
	 * Declaration of a typical NUMBER field type.
	 */
	public static final FieldType NUMBER = new FieldType(Primitive.NUMBER);

	/**
	 * Declaration of a typical DECIMAL field type.
	 */
	public static final FieldType DECIMAL = new FieldType(Primitive.DECIMAL);

	/**
	 * Declaration of a typical BOOLEAN field type.
	 */
	public static final FieldType BOOLEAN = new FieldType(Primitive.BOOLEAN);

	/**
	 * Declaration of a typical DATETIME field type.
	 */
	public static final FieldType DATETIME = new FieldType(Primitive.DATETIME);
	
	/**
	 * A bitwise flag indicating this field type has single precision
	 */
	public static final int SINGLE = 0;

	/**
	 * A bitwise flag indicating this field type has double precision
	 */
	public static final int DOUBLE = 1;

	/**
	 * A bitwise flag indicating this field type has absolute precision
	 */
	public static final int ABSOLUTE = 2;
	
	/**
	 * The data type name of this field type
	 */
	private Primitive primitiveType;
	
	private IBusinessObject objectType;
	
	/**
	 * The base data type of this field if the primary type is Map or Array.
	 */
	private Primitive primitiveBaseType;
	
	private IBusinessObject objectBaseType;

	/**
	 * This is the precision of this field type.  (SINGLE | DOUBLE | ABSOLUTE)
	 */
	private int precision;

	/**
	 * Creates a new <code>FieldType</code> with the given primitive data type.
	 * Precision is defaulted to SINGLE.
	 *
	 * @param type The primitive data type of this field type
	 */
	public FieldType(Primitive type)
	{
		super();
		this.primitiveType = type;
		this.precision = SINGLE;
	}
	
	public FieldType(IBusinessObject type)
	{
		super();
		this.objectType = type;
	}
	
	public FieldType(Primitive type, Primitive baseType)
	{
		if(!type.isMainType())
			throw new IllegalArgumentException("Only arrays and maps can have a base type.");
		if(!baseType.isBaseType())
			throw new IllegalArgumentException("Arrays and Maps cannot explictly contain other arrays or maps.  Use the wildcard type as the base type instead.");
		this.primitiveType = type;
		this.primitiveBaseType = baseType;
	}
	
	public FieldType(Primitive type, IBusinessObject baseType)
	{
		if(!type.isMainType())
			throw new IllegalArgumentException("Only arrays and maps can have a base type.");
		this.primitiveType = type;
		this.objectBaseType = baseType;
	}

	public String getName()
	{
		if(primitiveType != null)
			return primitiveType.getName();
		return objectType.getName();
	}
	
	public String getBaseTypeName()
	{
		if(primitiveBaseType != null)
			return primitiveBaseType.getName();
		if(objectBaseType != null)
			return objectBaseType.getName();
		return null;
	}
	
	public boolean hasValue()
	{
		if(primitiveType != null)
			return primitiveType.hasValue();
		return false;
	}
	
	/**
	 * @return The current precision of this field type.
	 */
	public int getPrecision()
	{
		return precision;
	}
	
	public void setPrecision(int precision)
	{
		this.precision = precision;
	}

	/**
	 * Determines whether or not this field type represents a business object.
	 *
	 * @return <code>true</code> if this field type represents a business object,
	 * <code>false</code> otherwise
	 */
	public boolean isObject()
	{
		return primitiveType == null;
	}
	
	public Primitive getPrimitiveType()
	{
		return primitiveType;
	}
	
	public IBusinessObject getObjectType()
	{
		return objectType;
	}
	
	public boolean hasBaseType()
	{
		return primitiveType != null && primitiveType.hasBaseType();
	}
	
	public boolean isObjectBaseType()
	{
		return primitiveBaseType == null;
	}
	
	public Primitive getPrimitiveBaseType()
	{
		return primitiveBaseType;
	}
	
	public IBusinessObject getObjectBaseType()
	{
		return objectBaseType;
	}

	/**
	 * Generates the XML DOM elements and attributes required to save the information
	 * for this field type.
	 *
	 * @param parent The parent XML DOM element that will contain the field type information
	 */
	public void write(Element parent)
	{
		Element dt = parent.getOwnerDocument().createElement("data-type");
		parent.appendChild(dt);

		if(primitiveType != null)
		{
			dt.setAttribute("type", "primitive:" + primitiveType.getName());
			if(primitiveType.hasBaseType())
			{
				if(primitiveBaseType != null)
				{
					dt.setAttribute("base-type", "primitive:" + primitiveBaseType.getName());
					if(primitiveBaseType.hasPrecision())
						dt.setAttribute("base-precision", Integer.toString(getPrecision()));
				}
				else
					dt.setAttribute("base-type", "object:" + objectBaseType.getName());
			}
			if(primitiveType.hasPrecision())
			{
				dt.setAttribute("precision", Integer.toString(getPrecision()));
			}
		}
		else
		{
			dt.setAttribute("type", "object:" + objectType.getName());
		}
	}

	/**
	 * Constructs a new <code>FieldType</code> that represents a number with
	 * the given precision.
	 *
	 * @param precision The precision of the number represented by the new field type
	 * @return The new <code>FieldType</code>
	 */
	public static FieldType number(int precision)
	{
		FieldType ret = new FieldType(Primitive.NUMBER);
		ret.precision = precision;

		return ret;
	}

	/**
	 * Constructs a new <code>FieldType</code> that represents an array of numbers.
	 *
	 * @return The new <code>FieldType</code>
	 */
	public static FieldType numberArray()
	{
		FieldType ret = new FieldType(Primitive.ARRAY, Primitive.NUMBER);
		return ret;
	}

	/**
	 * Constructs a new <code>FieldType</code> that represents an array of numbers
	 * that will have the given precision.
	 *
	 * @param precision The precision of the numbers contained in the array
	 * represented by the new field type
	 * @return The new <code>FieldType</code>
	 */
	public static FieldType numberArray(int precision)
	{
		FieldType ret = new FieldType(Primitive.ARRAY, Primitive.NUMBER);
		ret.precision = precision;
		return ret;
	}

	/**
	 * Constructs a new <code>FieldType</code> that represents an array of strings.
	 *
	 * @return The new <code>FieldType</code>
	 */
	public static FieldType stringArray()
	{
		FieldType ret = new FieldType(Primitive.ARRAY, Primitive.STRING);
		return ret;
	}

	/**
	 * Constructs a new <code>FieldType</code> that represents an array of dates.
	 *
	 * @return The new <code>FieldType</code>
	 */
	public static FieldType dateTimeArray()
	{
		FieldType ret = new FieldType(Primitive.ARRAY, Primitive.DATETIME);
		return ret;
	}

	/**
	 * Constructs a new <code>FieldType</code> that represents a decimal number with
	 * the given precision.
	 *
	 * @param precision The precision of the decimal number represented by the new
	 * field type
	 * @return The new <code>FieldType</code>
	 */
	public static FieldType decimal(int precision)
	{
		FieldType ret = new FieldType(Primitive.DECIMAL);
		ret.precision = precision;
		return ret;
	}

	/**
	 * Constructs a new <code>FieldType</code> that represents an array of decimal
	 * numbers.
	 *
	 * @return The new <code>FieldType</code>
	 */
	public static FieldType decimalArray()
	{
		FieldType ret = new FieldType(Primitive.ARRAY, Primitive.DECIMAL);
		return ret;
	}

	/**
	 * Constructs a new <code>FieldType</code> that represents an array of decimal
	 * numbers that will have the given precision.
	 *
	 * @param precision The precision of the decimal numbers contained in the array
	 * represented by the new field type
	 * @return The new <code>FieldType</code>
	 */
	public static FieldType decimalArray(int precision)
	{
		FieldType ret = new FieldType(Primitive.ARRAY, Primitive.DECIMAL);
		ret.precision = precision;
		return ret;
	}

	/**
	 * Constructs a new <code>FieldType</code> that represents an array of boolean
	 * values.
	 *
	 * @return The new <code>FieldType</code>
	 */
	public static FieldType booleanArray()
	{
		FieldType ret = new FieldType(Primitive.ARRAY, Primitive.BOOLEAN);
		return ret;
	}

	/**
	 * Constructs a new <code>FieldType</code> from the information stored in the
	 * given XML DOM sturcture.  The information must be stored the same format
	 * produced by the write() function of this class.
	 *
	 * @param element The element that contains the formated data
	 * @return The new <code>FieldType</code> instance represented by the given
	 * field type information
	 */
	public static FieldType load(IBusinessObjectSet objectSet, Element element)
	{
		if(!element.getTagName().equals("data-type"))
		{
			throw new IllegalArgumentException("DesignElement Format Exception");
		}
		if(element.getAttributeNode("type") == null) //legacy support
		{
			String typeName = null;
			int style = -1;
			int precision = -1;
			NodeList nl = element.getChildNodes();
	
			for(int i = 0; i < nl.getLength(); i++)
			{
				if(nl.item(i).getNodeType() == Node.ELEMENT_NODE)
				{
					Element child = (Element)nl.item(i);
	
					if(child.getTagName().equals("type"))
					{
	                    typeName = XMLUtilities.getElementTextDataNoEx(child, true);
					}
					else if(child.getTagName().equals("style"))
					{
						try
						{
							style = Integer.parseInt(child.getAttribute("value"));
						}
						catch(NumberFormatException e)
						{
							e.printStackTrace();
						}
					}
					else if(child.getTagName().equals("precision"))
					{
						try
						{
							precision = Integer.parseInt(child.getAttribute("value"));
						}
						catch(NumberFormatException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
	
			if((typeName == null) || (style == -1) || (precision == -1))
			{
				throw new IllegalArgumentException("DesignElement Format Exception");
			}
			FieldType ret = null;
			System.err.println("**************************" + typeName + "***************************");
			Primitive type = Primitive.find(typeName);
			if(type != null)
			{
				System.err.println("Primitive!");
				if((style & 1) == 1) //array designation
				{
					ret = new FieldType(Primitive.ARRAY, type);
				}
				else
					ret = new FieldType(type);
				if(type.hasPrecision())
					ret.precision = precision;
			}
			else
			{
				System.err.println("BusinessObject!");
				IBusinessObject bo = objectSet.getBusinessObject(typeName);
				if((style & 1) == 1)
					ret = new FieldType(Primitive.ARRAY, bo);
				else
					ret = new FieldType(bo);
			}
			return ret;
		}
		FieldType ret = null;
		int precision = 1;
		String typeName = element.getAttribute("type");
		if(typeName.startsWith("primitive:"))
		{
			typeName = typeName.substring(10);
			Primitive type = Primitive.find(typeName);
			if(type.hasBaseType())
			{
				String baseTypeName = element.getAttribute("base-type");
				if(baseTypeName.startsWith("primitive:"))
				{
					baseTypeName = baseTypeName.substring(10);
					Primitive baseType = Primitive.find(baseTypeName);
					ret = new FieldType(type, baseType);
					if(baseType.hasPrecision())
					{
						try
						{
							precision = Integer.parseInt(element.getAttribute("precision"));
						}
						catch(NumberFormatException nfe)
						{}
						ret.precision = precision;
					}
				}
				else //business object type
				{
					baseTypeName = baseTypeName.substring(7);
					IBusinessObject bo = objectSet.getBusinessObject(baseTypeName);
					if(bo == null)
						throw new RuntimeException("Missing business object definition: " + baseTypeName);
					ret = new FieldType(type, bo);
				}
			}
			else
			{
				ret = new FieldType(type);
			}
			if(type.hasPrecision())
			{
				try
				{
					precision = Integer.parseInt(element.getAttribute("precision"));
				}
				catch(NumberFormatException nfe)
				{}
				ret.precision = precision;
			}
		}
		else //business object type
		{
			typeName = typeName.substring(7);
			IBusinessObject bo = objectSet.getBusinessObject(typeName);
			if(bo == null)
				throw new RuntimeException("Missing business object definition: " + typeName);
			ret = new FieldType(bo);
		}
		return ret;
	}
	
	public boolean equals(Object obj)
	{
		if(!(obj instanceof FieldType))
			return false;
		FieldType type = (FieldType)obj;
		return type.primitiveType == primitiveType && type.objectType == objectType && type.primitiveBaseType == primitiveBaseType && type.objectBaseType == objectBaseType;
	}
}
