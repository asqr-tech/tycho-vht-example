package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.legacysupport;

import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.XMLConverter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PrimitiveElementLegacyConverter implements XMLConverter
{
	public PrimitiveElementLegacyConverter()
	{
	}

	public void convert(Element element) throws ConversionException
	{
		System.err.println("Converting legacy Primitive element.");
		Element parent = (Element)element.getParentNode();
		Element replacement = parent.getOwnerDocument().createElement("element");
		String id = element.getAttribute("id");
		String name = element.getAttribute("name");
		String type = element.getAttribute("type");
		replacement.setAttribute("id", id);
		replacement.setAttribute("name", name);
		replacement.setAttribute("type", "org.eclipse.vtp.desktop.editors.core.elements.primitive");
		Element propertiesElement = parent.getOwnerDocument().createElement("properties");
		replacement.appendChild(propertiesElement);
		Element typePropertyElement = parent.getOwnerDocument().createElement("property");
		typePropertyElement.setAttribute("name", "type");
		typePropertyElement.setAttribute("value", type);
		propertiesElement.appendChild(typePropertyElement);
		Element configurationElement = parent.getOwnerDocument().createElement("configuration");
		replacement.appendChild(configurationElement);
		NodeList originalConfigNodes = element.getChildNodes();
		for(int i = 0; i < originalConfigNodes.getLength(); i++)
		{
			System.err.println("child node: " + originalConfigNodes.item(i));
			if(originalConfigNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				Element configSectionElement = (Element)originalConfigNodes.item(i);
				Element clone = (Element)configSectionElement.cloneNode(true);
				configurationElement.appendChild(clone);
			}
		}
		System.err.println("Converted xml:");
		System.err.println(element);
		System.err.println("to:");
		System.err.println(replacement);
		parent.replaceChild(replacement, element);
	}

}
