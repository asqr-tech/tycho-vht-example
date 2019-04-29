package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.legacysupport;

import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.XMLConverter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModelV0_V1Converter implements XMLConverter
{

	public ModelV0_V1Converter()
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.update.XMLConverter#convert(org.w3c.dom.Element)
	 */
	public void convert(Element element) throws ConversionException
	{
		System.err.println("Running converter: " + this.getClass());
		element.setAttribute("xml-version", "1.0.0");
		System.err.println("Updated xml-version to \"1.0.0\"");
		NodeList elementsElementList = element.getElementsByTagName("elements");
		if(elementsElementList.getLength() > 0)
		{
			Element elementsElement = (Element)elementsElementList.item(0);
			System.err.println("Converting legacy element xml sections...");
			NodeList elementsElementChildren = elementsElement.getChildNodes();
			for(int i = 0; i < elementsElementChildren.getLength(); i++)
			{
				if(elementsElementChildren.item(i).getNodeType() == Node.ELEMENT_NODE)
				{
					Element elementElement = (Element)elementsElementChildren.item(i);
					String tagName = elementElement.getNodeName();
					String namespace = elementElement.getNamespaceURI();
					System.err.println("Processing Element node: namespace=\"" + namespace + "\" xml-element=\"" + tagName + "\"");
					XMLConverter converter = LegacySupportManager.getInstance().getLegacyElementConverter(tagName, namespace);
					if(converter == null) //no converter for element type
					{
						System.err.println("Could not convert element: " + elementElement);
						throw new ConversionException("Could not convert element: " + elementElement);
					}
					System.err.println("Found coverter: " + converter);
					converter.convert(elementElement);
				}
			}
			System.err.println("Finished legacy elements.");
			NodeList elementList = elementsElement.getElementsByTagName("element");
			for(int i = 0; i < elementList.getLength(); i++)
			{
				Element elementElement = (Element)elementList.item(i);
				NodeList configurationList = elementElement.getElementsByTagName("configuration");
				if(configurationList.getLength() > 0)
				{
					org.w3c.dom.Element configurationElement = (org.w3c.dom.Element)configurationList.item(0);
					NodeList configSectionList = configurationElement.getChildNodes();
					for(int c = 0; c < configSectionList.getLength(); c++)
					{
						if(configSectionList.item(c).getNodeType() == Node.ELEMENT_NODE)
						{
							Element configSectionElement = (Element)configSectionList.item(c);
							if(!(configSectionElement.getNodeName().equals("custom-config") && configSectionElement.getNamespaceURI() != null && configSectionElement.getNamespaceURI().equals("http://www.eclipse.org/vtp/namespaces/config")))
							{
								String namespace = configSectionElement.getNamespaceURI();
								String tagName = configSectionElement.getNodeName();
								XMLConverter converter = LegacySupportManager.getInstance().getLegacyConfigurationManagerConverter(tagName, namespace);
								if(converter == null)
								{
									throw new ConversionException("Could not convert configuration manager: " + configSectionElement);
								}
								converter.convert(configSectionElement);
							}
						}
					}
				}
			}
		}
		System.err.println("Finished converter: " + this.getClass());
	}

}
