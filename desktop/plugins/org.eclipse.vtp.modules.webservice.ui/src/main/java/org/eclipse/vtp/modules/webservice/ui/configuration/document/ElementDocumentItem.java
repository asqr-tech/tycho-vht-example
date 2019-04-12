package org.eclipse.vtp.modules.webservice.ui.configuration.document;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.framework.util.XMLUtilities;
import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceBindingManager;
import org.w3c.dom.Element;

public class ElementDocumentItem extends DocumentItemContainer
{
	private String name = null;
	private String namespace = null;
	private boolean simple = false;
	private List<ElementAttributeDocumentItem> attributes = new ArrayList<ElementAttributeDocumentItem>();

	public ElementDocumentItem(WebserviceBindingManager manager)
	{
		super(manager);
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public String getNamespace()
	{
		return namespace;
	}
	
	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}
	
	public boolean isSimple()
	{
		return simple;
	}
	
	public void setSimple(boolean simple)
	{
		this.simple = simple;
	}
	
	public List<ElementAttributeDocumentItem> getAttributes()
	{
		return attributes;
	}
	
	public void addAttribute(ElementAttributeDocumentItem attribute)
	{
		attributes.remove(attribute);
		attributes.add(attribute);
	}
	
	public void removeAttribute(ElementAttributeDocumentItem attribute)
	{
		attributes.remove(attribute);
	}
	
	public void clearAttributes()
	{
		attributes.clear();
	}

	public Element createConfigurationElement(Element parentElement)
	{
		Element elementItemElement = parentElement.getOwnerDocument().createElementNS(null, "element-item");
		parentElement.appendChild(elementItemElement);
		return elementItemElement;
	}

	public void readConfiguration(Element elementItemElement)
	{
		this.name = elementItemElement.getAttribute("name");
		this.namespace = elementItemElement.getAttribute("namespace");
		this.simple = elementItemElement.getAttribute("simple").equals("true");
		List<Element> attributeContainerElementList = XMLUtilities.getElementsByTagName(elementItemElement, "attributes", true);
		if(attributeContainerElementList.size() > 0)
		{
			Element attributeContainerElement = attributeContainerElementList.get(0);
			List<Element> attributeElementList = XMLUtilities.getElementsByTagName(attributeContainerElement, "attribute", true);
			for(Element attributeElement : attributeElementList)
			{
				ElementAttributeDocumentItem attribute = new ElementAttributeDocumentItem(getManager());
				attribute.readConfiguration(attributeElement);
				attributes.add(attribute);
			}
		}
		super.readConfiguration(elementItemElement);
	}

	public void writeConfiguration(Element elementItemElement)
	{
		elementItemElement.setAttribute("name", name);
		elementItemElement.setAttribute("namespace", namespace);
		elementItemElement.setAttribute("simple", simple ? "true" : "false");
		Element attributesElement = elementItemElement.getOwnerDocument().createElementNS(null, "attributes");
		elementItemElement.appendChild(attributesElement);
		for(ElementAttributeDocumentItem attribute : attributes)
		{
			Element attributeElement = attributesElement.getOwnerDocument().createElementNS(null, "attribute");
			attributesElement.appendChild(attributeElement);
			attribute.writeConfiguration(attributeElement);
		}
		super.writeConfiguration(elementItemElement);
	}

	public void dumpContents(PrintStream out)
	{
		out.println("[Element Item: " + name + "]");
		super.dumpContents(out);
	}
}
