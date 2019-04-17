package org.eclipse.vtp.modules.webservice.ui.configuration.document;

import java.io.PrintStream;

import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceBindingManager;
import org.w3c.dom.Element;

public class ElseDocumentItem extends DocumentItemContainer
{

	public ElseDocumentItem(WebserviceBindingManager manager)
	{
		super(manager);
	}
	
	public Element createConfigurationElement(Element parentElement)
	{
		Element elseItemElement = parentElement.getOwnerDocument().createElementNS(null, "else-item");
		parentElement.appendChild(elseItemElement);
		return elseItemElement;
	}

	public void dumpContents(PrintStream out)
	{
		out.println("[Else Item]");
		super.dumpContents(out);
	}
}
