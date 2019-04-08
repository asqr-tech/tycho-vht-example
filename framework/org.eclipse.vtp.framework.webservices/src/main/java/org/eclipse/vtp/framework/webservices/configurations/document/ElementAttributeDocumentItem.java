package org.eclipse.vtp.framework.webservices.configurations.document;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.vtp.framework.common.IBrand;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ElementAttributeDocumentItem
{
	private String name = null;
	/**	An index of brand bindings based on the brand id */
	private Map<String, BindingValue> brandBindings = new TreeMap<String, BindingValue>();

	public ElementAttributeDocumentItem()
	{
		super();
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Retrieves the brand binding associated with the given brand.  If no
	 * binding is associated with the brand, null is returned.  This should not
	 * happen as a binding is created for every brand during instantiation.
	 * 
	 * @param brand The brand associated with the desired binding.
	 * @return The binding associated with the given brand
	 */
	public BindingValue getBrandBinding(IBrand brand)
	{
		return brandBindings.get(brand.getId());
	}
	
	/**
	 * Reads the configuration data stored in the given dom element into this
	 * language binding instance.  Any previous information stored in this
	 * language binding is lost.
	 * 
	 * @param attributeItemElement The dom element containing the configuration
	 */
	public void readConfiguration(Element attributeItemElement)
	{
		NodeList brandBindingElementList = attributeItemElement.getElementsByTagName("brand-binding");
		for(int i = 0; i < brandBindingElementList.getLength(); i++)
		{
			Element brandBindingElement = (Element)brandBindingElementList.item(i);
			String brandId = brandBindingElement.getAttribute("id");
			BindingValue value = new BindingValue();
			value.readConfiguration(brandBindingElement);
			brandBindings.put(brandId, value);
		}
	}
	
	/**
	 * Stores this language binding's information into the given dom element.
	 * 
	 * @param attributeItemElement The dom element to hold this binding's data
	 */
	public void writeConfiguration(Element attributeItemElement)
	{
		for(Map.Entry<String, BindingValue> entry : brandBindings.entrySet())
		{
			String brandId = entry.getKey();
			BindingValue brandBinding = entry.getValue();
			Element brandBindingElement = attributeItemElement.getOwnerDocument().createElementNS(null, "brand-binding");
			attributeItemElement.appendChild(brandBindingElement);
			brandBindingElement.setAttribute("id", brandId);
			brandBinding.writeConfiguration(brandBindingElement);
		}
	}

}
