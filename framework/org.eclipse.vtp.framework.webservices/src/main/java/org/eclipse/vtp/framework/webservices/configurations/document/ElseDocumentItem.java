package org.eclipse.vtp.framework.webservices.configurations.document;

import org.w3c.dom.Element;

public class ElseDocumentItem extends DocumentItemContainer
{

	public ElseDocumentItem()
	{
		super();
	}
	
	public Element createConfigurationElement(Element parentElement)
	{
		Element elseItemElement = parentElement.getOwnerDocument().createElementNS(null, "else-item");
		parentElement.appendChild(elseItemElement);
		return elseItemElement;
	}

}
