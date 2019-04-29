package org.eclipse.vtp.modules.webservice.ui.configuration.document;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.framework.util.XMLUtilities;
import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceBindingManager;
import org.w3c.dom.Element;

public abstract class DocumentItemContainer extends DocumentItem
{
	protected List<DocumentItem> items = new ArrayList<DocumentItem>();

	public DocumentItemContainer(WebserviceBindingManager manager)
	{
		super(manager);
	}

	public List<DocumentItem> getItems()
	{
		return items;
	}
	
	public void addItem(DocumentItem item)
	{
		items.add(item);
		item.setParent(this);
	}
	
	public void insertItem(DocumentItem item, int index)
	{
		items.add(index, item);
		item.setParent(this);
	}
	
	public void removeItem(DocumentItem item)
	{
		items.remove(item);
		item.setParent(null);
	}
	
	public void insertItem(DocumentItem item, DocumentItem insertionPoint)
	{
		if(insertionPoint == null)
			items.add(0, item);
		else
		{
			for(int i = 0; i < items.size(); i++)
			{
				if(items.get(i) == insertionPoint)
				{
					items.add(i + 1, item);
					item.setParent(this);
					return;
				}
			}
			items.add(item);
		}
		item.setParent(this);
	}
	
	public void readConfiguration(Element documentItemContainerElement)
	{
		List<Element> children = XMLUtilities.getChildElements(documentItemContainerElement);
		for(Element child : children)
		{
			System.out.println("reading child element: " + child.getLocalName());
			System.out.println("ns: " + child.getNamespaceURI());
			System.out.println("tag name: " + child.getTagName());
			DocumentItem item = null;
			if(child.getLocalName().equals("conditional-container"))
			{
				item = new ConditionalContainerSet(getManager());
			}
			else if(child.getLocalName().equals("element-item"))
			{
				item = new ElementDocumentItem(getManager());
			}
			else if(child.getLocalName().equals("for-loop-item"))
			{
				item = new ForLoopDocumentItem(getManager());
			}
			else if(child.getLocalName().equals("text-item"))
			{
				item = new TextDocumentItem(getManager());
			}
			System.out.println("produced item: " + item);
			if(item != null)
			{
				item.readConfiguration(child);
				items.add(item);
				item.setParent(this);
			}
		}
	}
	
	public void writeConfiguration(Element documentItemContainerElement)
	{
		for(DocumentItem item : items)
		{
			Element documentItemElement = item.createConfigurationElement(documentItemContainerElement);
			item.writeConfiguration(documentItemElement);
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
		out.println("Children:");
		for(DocumentItem item : items)
		{
			item.dumpContents(out);
		}
	}
}
