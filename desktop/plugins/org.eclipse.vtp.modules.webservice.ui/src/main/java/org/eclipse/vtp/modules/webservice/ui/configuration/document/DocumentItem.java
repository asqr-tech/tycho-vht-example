package org.eclipse.vtp.modules.webservice.ui.configuration.document;

import java.io.PrintStream;

import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceBindingManager;
import org.w3c.dom.Element;

public abstract class DocumentItem
{
	/**	The binding manager that contains this brand binding */
	private WebserviceBindingManager manager = null;
	private DocumentItemContainer parent;

	public DocumentItem(WebserviceBindingManager manager)
	{
		super();
		this.manager = manager;
	}

	public WebserviceBindingManager getManager()
	{
		return manager;
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

	/**
	 * Prints this binding's information to the given print stream.  This is
	 * useful for logging and debugging.
	 * 
	 * @param out The print stream to write the information to
	 */
	public abstract void dumpContents(PrintStream out);
	
	public DocumentItemContainer getParent()
	{
		return parent;
	}
	
	void setParent(DocumentItemContainer parent)
	{
		this.parent = parent;
	}
}
