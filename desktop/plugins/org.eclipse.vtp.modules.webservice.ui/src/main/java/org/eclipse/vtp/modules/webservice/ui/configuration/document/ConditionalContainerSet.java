package org.eclipse.vtp.modules.webservice.ui.configuration.document;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.framework.util.XMLUtilities;
import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceBindingManager;
import org.w3c.dom.Element;

public class ConditionalContainerSet extends DocumentItemContainer
{
	private ConditionalDocumentItem ifItem = null;
	private List<ConditionalDocumentItem> elseIfItems = new ArrayList<ConditionalDocumentItem>();
	private ElseDocumentItem elseItem = null;

	public ConditionalContainerSet(WebserviceBindingManager manager)
	{
		super(manager);
	}

	public ConditionalDocumentItem getIf()
	{
		return ifItem;
	}
	
	public void setIf(ConditionalDocumentItem ifItem)
	{
		if(this.ifItem != null)
			this.ifItem.setParent(null);
		this.ifItem = ifItem;
		ifItem.setParent(this);
	}
	
	public List<ConditionalDocumentItem> getElseIfs()
	{
		return elseIfItems;
	}
	
	public void addItem(ConditionalDocumentItem item)
	{
		elseIfItems.add(item);
		item.setParent(this);
	}
	
	public void insertItem(ConditionalDocumentItem item, int index)
	{
		elseIfItems.add(index, item);
		item.setParent(this);
	}
	
	public void removeItem(ConditionalDocumentItem item)
	{
		elseIfItems.remove(item);
		item.setParent(null);
	}
	
	public ElseDocumentItem getElse()
	{
		return elseItem;
	}
	
	public void setElse(ElseDocumentItem elseItem)
	{
		if(this.elseItem != null)
			this.elseItem.setParent(null);
		this.elseItem = elseItem;
		if(elseItem != null)
			elseItem.setParent(this);
	}
	
	public Element createConfigurationElement(Element parentElement)
	{
		Element conditionalContainerElement = parentElement.getOwnerDocument().createElementNS(null, "conditional-container");
		parentElement.appendChild(conditionalContainerElement);
		return conditionalContainerElement;
	}

	/**
	 * Reads the configuration data stored in the given dom element into this
	 * language binding instance.  Any previous information stored in this
	 * language binding is lost.
	 * 
	 * @param conditionalContainerElement The dom element containing the configuration
	 */
	public void readConfiguration(Element conditionalContainerElement)
	{
		List<Element> conditionalElementList = XMLUtilities.getElementsByTagName(conditionalContainerElement, "conditional-item", true);
		for(Element conditionalElement : conditionalElementList)
		{
			ConditionalDocumentItem conditionalItem = new ConditionalDocumentItem(getManager());
			conditionalItem.readConfiguration(conditionalElement);
			if(ifItem == null) //first condition
			{
				ifItem = conditionalItem;
			}
			else
			{
				elseIfItems.add(conditionalItem);
			}
			conditionalItem.setParent(this);
		}
		List<Element> elseElementList = XMLUtilities.getElementsByTagName(conditionalContainerElement, "else-item", true);
		if(elseElementList.size() > 0)
		{
			ElseDocumentItem elseItem = new ElseDocumentItem(getManager());
			elseItem.readConfiguration(elseElementList.get(0));
			this.elseItem = elseItem;
			elseItem.setParent(this);
		}
	}
	
	/**
	 * Stores this language binding's information into the given dom element.
	 * 
	 * @param conditionalContainerElement The dom element to hold this binding's data
	 */
	public void writeConfiguration(Element conditionalContainerElement)
	{
		if(ifItem != null)
		{
			Element conditionalElement = conditionalContainerElement.getOwnerDocument().createElementNS(null, "conditional-item");
			conditionalContainerElement.appendChild(conditionalElement);
			ifItem.writeConfiguration(conditionalElement);
			for(ConditionalDocumentItem conditionalItem : elseIfItems)
			{
				conditionalElement = conditionalContainerElement.getOwnerDocument().createElementNS(null, "conditional-item");
				conditionalContainerElement.appendChild(conditionalElement);
				conditionalItem.writeConfiguration(conditionalElement);
			}
			if(elseItem != null)
			{
				conditionalElement = conditionalContainerElement.getOwnerDocument().createElementNS(null, "else-item");
				conditionalContainerElement.appendChild(conditionalElement);
				elseItem.writeConfiguration(conditionalElement);
			}
		}
	}

	/**
	 * Prints this binding's information to the given print stream.  This is
	 * useful for logging and debugging.
	 * 
	 * @param out The print stream to write the information to
	 */
	public void dumpContents(PrintStream out)
	{
		out.println("[Conditional Container]");
		out.println("Conditionals:");
		if(ifItem != null)
		{
			ifItem.dumpContents(out);
			for(ConditionalDocumentItem elseIfItem : elseIfItems)
			{
				elseIfItem.dumpContents(out);
			}
			if(elseItem != null)
			{
				elseItem.dumpContents(out);
			}
		}
	}
	
}
