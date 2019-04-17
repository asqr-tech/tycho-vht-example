package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.legacysupport;

import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.XMLConverter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MediaBindingLegacyConverter implements XMLConverter
{

	public MediaBindingLegacyConverter()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.update.XMLConverter#convert(org.w3c.dom.Element)
	 */
	public void convert(Element element) throws ConversionException
	{
		Element parent = (Element)element.getParentNode();
		Element replacement = parent.getOwnerDocument().createElement("managed-config");
		replacement.setAttribute("type", "org.eclipse.vtp.configuration.media");
		NodeList children = element.getChildNodes();
		for(int i = 0; i < children.getLength(); i++)
		{
			Node clone = children.item(i).cloneNode(true);
			replacement.appendChild(clone);
		}
		parent.replaceChild(replacement, element);
	}

}
