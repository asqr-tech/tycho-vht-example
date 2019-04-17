package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.legacysupport;

import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.XMLConverter;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AdvancedTransferLegacyConverter implements XMLConverter
{
	
	/**
	 * @param brandManager
	 */
	public AdvancedTransferLegacyConverter()
	{
		super();
	}

	public void convert(Element element) throws ConversionException
	{
		System.err.println("Running converter: " + this.getClass());
		element.setAttribute("xml-version", "1.0.1");
		System.err.println("Updated xml-version to \"1.0.1\"");		
		
		NodeList elementList = element.getElementsByTagName("element");
		for(int b = 0; b < elementList.getLength(); b++)
		{
			Element newModuleElement = (Element)elementList.item(b);
			if(newModuleElement.getAttribute("type").equals("org.eclipse.vtp.desktop.editors.core.elements.primitive"))
			{
				//It's a primitive of some sort
				NodeList propertyNodes = newModuleElement.getElementsByTagName("property");
				for(int c = 0; c < propertyNodes.getLength(); c++)
				{
					//Cycle through the property tags, searching for one in which attribute 'name' == "type"
					if(((Element)propertyNodes.item(c)).getAttribute("name").equals("type"))
					{
						//We have the right property tag. What type of of primitive is this?
						if(((Element)propertyNodes.item(c)).getAttribute("value").equals("org.eclipse.vtp.desktop.editors.core.transfer"))
						{
							NodeList namedBindingNodes = newModuleElement.getElementsByTagName("named-binding");
							for(int d = 0; d < namedBindingNodes.getLength(); d++)
							{
								if(((Element)namedBindingNodes.item(d)).getAttribute("name").equals("destination"))
								{
									Element newTransferTypeElement = (Element)namedBindingNodes.item(d).cloneNode(true);
									newTransferTypeElement.setAttribute("name", "transferType");

									NodeList propertyValueNodes = newTransferTypeElement.getElementsByTagName("property-value");
									((Element)propertyValueNodes.item(0)).setTextContent("blind");
									
									namedBindingNodes.item(d).getParentNode().insertBefore(newTransferTypeElement, namedBindingNodes.item(d));
									break;
								}
							}
						}
						else if(((Element)propertyNodes.item(c)).getAttribute("value").equals("org.eclipse.vtp.desktop.editors.core.advancedtransfer"))
						{
							NodeList namedBindingNodes = newModuleElement.getElementsByTagName("named-binding");
							for(int d = 0; d < namedBindingNodes.getLength(); d++)
							{
								if(((Element)namedBindingNodes.item(d)).getAttribute("name").equals("bridged"))
								{
									((Element)namedBindingNodes.item(d)).setAttribute("name", "transferType");
									NodeList propertyValueNodes = ((Element)namedBindingNodes.item(d)).getElementsByTagName("property-value");
									if(Boolean.parseBoolean(((Element)propertyValueNodes.item(0)).getTextContent().trim()))
									{
										((Element)propertyValueNodes.item(0)).setTextContent("bridged");
									}
									else
									{
										((Element)propertyValueNodes.item(0)).setTextContent("blind");
									}
								}
							}
						}
					}
				}
			}
		}

	}
}
