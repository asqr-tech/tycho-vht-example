package org.eclipse.vtp.framework.webservices.configurations.document;

import org.w3c.dom.Element;

public class InputDocumentStructure extends DocumentItemContainer
{

	public InputDocumentStructure()
	{
		super();
	}

	public void clearStructure()
	{
		items.clear();
	}
	
	public Element createConfigurationElement(Element parentElement)
	{
		Element conditionalContainerElement = parentElement.getOwnerDocument().createElement("input-document-structure");
		parentElement.appendChild(conditionalContainerElement);
		return conditionalContainerElement;
	}

}
