package org.eclipse.vtp.modules.webservice.ui.configuration;

import org.eclipse.vtp.modules.webservice.ui.configuration.document.DocumentItemContainer;
import org.w3c.dom.Element;

public class InputDocumentStructure extends DocumentItemContainer
{

	public InputDocumentStructure(WebserviceBindingManager manager)
	{
		super(manager);
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
