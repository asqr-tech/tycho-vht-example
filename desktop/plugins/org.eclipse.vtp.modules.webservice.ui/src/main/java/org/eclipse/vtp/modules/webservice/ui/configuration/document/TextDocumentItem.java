package org.eclipse.vtp.modules.webservice.ui.configuration.document;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.modules.webservice.ui.configuration.BrandBinding;
import org.eclipse.vtp.modules.webservice.ui.configuration.BrandedBinding;
import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceBindingManager;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TextDocumentItem extends DocumentItem implements BrandedBinding
{
	private Map<String, BrandBinding> brandBindings = new TreeMap<String, BrandBinding>();

	public TextDocumentItem(WebserviceBindingManager manager)
	{
		super(manager);
		BrandManager brandManager = manager.getBrandManager();
		IBrand defaultBrand = brandManager.getDefaultBrand();
		createBrandBinding(defaultBrand);
	}

	/**
	 * Retrieves the brand binding associated with the given brand.  If no
	 * binding is associated with the brand, null is returned.  This should not
	 * happen as a binding is created for every brand during instantiation.
	 * 
	 * @param brand The brand associated with the desired binding.
	 * @return The binding associated with the given brand
	 */
	public BrandBinding getBrandBinding(IBrand brand)
	{
		return brandBindings.get(brand.getId());
	}
	
	/**
	 * Reads the configuration data stored in the given dom element into this
	 * language binding instance.  Any previous information stored in this
	 * language binding is lost.
	 * 
	 * @param textItemElement The dom element containing the configuration
	 */
	public void readConfiguration(Element textItemElement)
	{
		NodeList brandBindingElementList = textItemElement.getElementsByTagName("brand-binding");
		for(int i = 0; i < brandBindingElementList.getLength(); i++)
		{
			Element brandBindingElement = (Element)brandBindingElementList.item(i);
			String brandId = brandBindingElement.getAttribute("id");
			BrandBinding brandBinding = brandBindings.get(brandId);
			if(brandBinding != null)
			{
				brandBinding.readConfiguration(brandBindingElement);
			}
		}
	}
	
	/**
	 * Stores this language binding's information into the given dom element.
	 * 
	 * @param textItemElement The dom element to hold this binding's data
	 */
	public void writeConfiguration(Element textItemElement)
	{
		Iterator<BrandBinding> iterator = brandBindings.values().iterator();
		while(iterator.hasNext())
		{
			BrandBinding brandBinding = iterator.next();
			if(!brandBinding.isInherited())
			{
				Element brandBindingElement = textItemElement.getOwnerDocument().createElementNS(null, "brand-binding");
				textItemElement.appendChild(brandBindingElement);
				brandBindingElement.setAttribute("id", brandBinding.getBrand().getId());
				brandBindingElement.setAttribute("name", brandBinding.getBrand().getName());
				brandBinding.writeConfiguration(brandBindingElement);
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
		out.println("[Text Item]");
		out.println("IBrand Bindings");
		Iterator<BrandBinding> iterator = brandBindings.values().iterator();
		while(iterator.hasNext())
		{
			BrandBinding brandBinding = iterator.next();
			brandBinding.dumpContents(out);
		}
	}
	
	/**
	 * Recursively creates the brand binding structure.
	 * 
	 * @param brand The brand to bind
	 * @return The binding for the given brand
	 */
	private BrandBinding createBrandBinding(IBrand brand)
	{
		BrandBinding brandBinding = new BrandBinding(getManager(), brand);
		brandBindings.put(brand.getId(), brandBinding);
		List<IBrand> children = brand.getChildBrands();
		for(IBrand child : children)
		{
			BrandBinding bindingChild = createBrandBinding(child);
			bindingChild.setParent(brandBinding);
		}
		return brandBinding;
	}

	@Override
	public Element createConfigurationElement(Element parentElement)
	{
		Element textElement = parentElement.getOwnerDocument().createElementNS(null, "text-item");
		parentElement.appendChild(textElement);
		return textElement;
	}
}
