package org.eclipse.vtp.modules.webservice.ui.configuration.document;

import java.io.PrintStream;

import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceBindingManager;
import org.w3c.dom.Element;

public class ConditionalDocumentItem extends DocumentItemContainer
{
	private String condition = null;

	public ConditionalDocumentItem(WebserviceBindingManager manager)
	{
		super(manager);
	}

	public String getCondition()
	{
		return condition;
	}
	
	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	public Element createConfigurationElement(Element parentElement)
	{
		Element conditionalItemElement = parentElement.getOwnerDocument().createElementNS(null, "conditional-item");
		parentElement.appendChild(conditionalItemElement);
		return conditionalItemElement;
	}

	public void readConfiguration(Element conditionalItemElement)
	{
		String condition = conditionalItemElement.getAttribute("condition");
		this.condition = condition;
		super.readConfiguration(conditionalItemElement);
	}

	public void writeConfiguration(Element conditionalItemElement)
	{
		conditionalItemElement.setAttribute("condition", condition);
		super.writeConfiguration(conditionalItemElement);
	}

	public void dumpContents(PrintStream out)
	{
		out.println("[Conditional Item: " + condition + "]");
		super.dumpContents(out);
	}
}
