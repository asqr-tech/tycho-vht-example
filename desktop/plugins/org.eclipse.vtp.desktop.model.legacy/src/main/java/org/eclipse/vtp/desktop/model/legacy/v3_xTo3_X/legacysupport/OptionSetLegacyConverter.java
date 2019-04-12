package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.legacysupport;

import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.XMLConverter;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class OptionSetLegacyConverter implements XMLConverter
{
	/**
	 * @param brandManager
	 */
	public OptionSetLegacyConverter()
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
				//It's a basic element of some sort
				NodeList propertyNodes = newModuleElement.getElementsByTagName("property");
				for(int c = 0; c < propertyNodes.getLength(); c++)
				{
					//Cycle through the property tags, searching for one in which attribute 'name' == "type"
					if(((Element)propertyNodes.item(c)).getAttribute("name").equals("type"))
					{
						//We have the right property tag. What type of of primitive is this?
						if(((Element)propertyNodes.item(c)).getAttribute("value").equals("org.eclipse.vtp.desktop.editors.core.optionSet"))
						{
							newModuleElement.setAttribute("type", "org.eclipse.vtp.desktop.model.elements.core.basic");
							((Element)propertyNodes.item(c)).setAttribute("value", "org.eclipse.vtp.modules.interactive.optionSet");
							Element managedConfig = (Element) newModuleElement.getElementsByTagName("managed-config").item(0);
							managedConfig.setAttribute("type", "org.eclipse.vtp.configuration.menuchoice");
							NodeList customConfigChildNodes = newModuleElement.getElementsByTagName("custom-config").item(0).getChildNodes();
							for(int d = 0; d < customConfigChildNodes.getLength(); d++)
							{
								managedConfig.insertBefore(customConfigChildNodes.item(d).cloneNode(true),null);
							}
						}
					}
				}
			}
		}
	}
}
