package org.eclipse.vtp.framework.webservices.configurations.document;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.vtp.framework.common.IBrand;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TextDocumentItem extends DocumentItem
{
	private Map<String, BindingValue> brandBindings = new TreeMap<String, BindingValue>();

	public TextDocumentItem()
	{
		super();
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
	 * @param textItemElement The dom element containing the configuration
	 */
	public void readConfiguration(Element textItemElement)
	{
		NodeList brandBindingElementList = textItemElement.getElementsByTagName("brand-binding");
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
	 * @param textItemElement The dom element to hold this binding's data
	 */
	public void writeConfiguration(Element textItemElement)
	{
		for(Map.Entry<String, BindingValue> entry : brandBindings.entrySet())
		{
			String brandId = entry.getKey();
			BindingValue brandBinding = entry.getValue();
			Element brandBindingElement = textItemElement.getOwnerDocument().createElementNS(null, "brand-binding");
			textItemElement.appendChild(brandBindingElement);
			brandBindingElement.setAttribute("id", brandId);
			brandBinding.writeConfiguration(brandBindingElement);
		}
	}

	@Override
	public Element createConfigurationElement(Element parentElement)
	{
		Element textElement = parentElement.getOwnerDocument().createElementNS(null, "text-item");
		parentElement.appendChild(textElement);
		return textElement;
	}
}
