package org.eclipse.vtp.desktop.model.elements.core.configuration;

import java.io.PrintStream;

import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;

public class InputItem
{
	public static final String NONE = "NONE";
	public static final String STATIC = "STATIC";
	public static final String VARIABLE = "VARIABLE";
	public static final String EXPRESSION = "EXPRESSION";
	
	/** The type of value being bound */
	private String type = NONE;
	/**	The value bound to the brand */
	private String value = null;
	
	public InputItem()
	{
		super();
	}
	
	public InputItem(String type, String value)
	{
		super();
		this.type = type;
		this.value = value;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
	
	/**
	 * Reads the configuration data stored in the given dom element into this
	 * brand binding instance.  Any previous information stored in this brand
	 * binding is lost.
	 * 
	 * @param brandBindingElement The dom element containing the configuration
	 */
	public void readConfiguration(Element brandBindingElement)
	{
		type = brandBindingElement.getAttribute("type");
		value = XMLUtilities.getElementTextDataNoEx(brandBindingElement, true);
	}
	
	/**
	 * Stores this brand binding's information into the given dom element.
	 * 
	 * @param brandBindingElement The dom element to hold this binding's data
	 */
	public void writeConfiguration(Element brandBindingElement)
	{
		brandBindingElement.setAttribute("type", type);
		brandBindingElement.setTextContent(value);
	}

	/**
	 * Prints this brand binding's information to the given print stream.  This
	 * is useful for logging and debugging.
	 * 
	 * @param out The print stream to write the information to
	 */
	public void dumpContents(PrintStream out)
	{
		out.println("[Input Binding] " + type + "(" + value + ")");
	}
}
