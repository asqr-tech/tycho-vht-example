package org.eclipse.vtp.framework.webservices.configurations.document;

import org.w3c.dom.Element;

public abstract class DocumentItem
{
	private DocumentItemContainer parent;

	public DocumentItem()
	{
		super();
	}

	public abstract Element createConfigurationElement(Element parentElement);

	/**
	 * Reads the configuration data stored in the given dom element into this
	 * language binding instance.  Any previous information stored in this
	 * language binding is lost.
	 * 
	 * @param documentItemElement The dom element containing the configuration
	 */
	public abstract void readConfiguration(Element documentItemElement);
	
	/**
	 * Stores this language binding's information into the given dom element.
	 * 
	 * @param documentItemElement The dom element to hold this binding's data
	 */
	public abstract void writeConfiguration(Element documentItemElement);

	public DocumentItemContainer getParent()
	{
		return parent;
	}
	
	void setParent(DocumentItemContainer parent)
	{
		this.parent = parent;
	}
}
