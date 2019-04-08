package org.eclipse.vtp.framework.webservices.configurations.document;

import org.w3c.dom.Element;

public class ForLoopDocumentItem extends DocumentItemContainer
{
	public static final String TRADITIONAL = "traditional";
	public static final String FOREACH = "foreach";
	
	private String type = FOREACH;
	private String varName = "";
	private String conditional = "";
	private String transform = "";

	public ForLoopDocumentItem()
	{
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getVariableName()
	{
		return varName;
	}
	
	public String getConditional()
	{
		return conditional;
	}
	
	public String getTransform()
	{
		return transform;
	}
	
	public void setForEach(String varName, String sourceName)
	{
		type = FOREACH;
		this.varName = varName;
		this.transform = sourceName;
		this.conditional = "";
	}
	
	public void setTraditional(String varName, String conditional, String transform)
	{
		type = TRADITIONAL;
		this.varName = varName;
		this.conditional = conditional;
		this.transform = transform;
	}

	public Element createConfigurationElement(Element parentElement)
	{
		Element conditionalItemElement = parentElement.getOwnerDocument().createElementNS(null, "for-loop-item");
		parentElement.appendChild(conditionalItemElement);
		return conditionalItemElement;
	}

	public void readConfiguration(Element conditionalItemElement)
	{
		type = conditionalItemElement.getAttribute("type");
		if(type.equals(""))
			type = FOREACH;
		varName = conditionalItemElement.getAttribute("variable-name");
		conditional = conditionalItemElement.getAttribute("conditional");
		transform = conditionalItemElement.getAttribute("transform");
		super.readConfiguration(conditionalItemElement);
	}

	public void writeConfiguration(Element conditionalItemElement)
	{
		conditionalItemElement.setAttribute("type", type);
		conditionalItemElement.setAttribute("variable-name", varName);
		conditionalItemElement.setAttribute("conditional", conditional);
		conditionalItemElement.setAttribute("transform", transform);
		super.writeConfiguration(conditionalItemElement);
	}

}
