/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.interactive.core.configuration.generic;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.branding.BrandManagerListener;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class binds the brand structure to a specific language.  The brand
 * structure contained by this language binding is automatically created during
 * instantiation.
 * 
 * @author trip
 */
public class LanguageBinding implements BrandManagerListener
{
	/**	The binding manager that contains this binding */
	private GenericBindingManager manager = null;
	/**	The name of this binding's associated language */
	private String name = null;
	/**	An index of brand bindings based on the brand id */
	private Map<String, BrandBinding> brandBindings = new TreeMap<String, BrandBinding>();

	/**
	 * Constructs a new language binding instance that is contained by the
	 * provided binding manager and is associated with the language with the
	 * given name.  The brand structure is automatically created.
	 * 
	 * @param manager The binding manager that contains this binding
	 * @param name The name of the language associated with this binding
	 */
	public LanguageBinding(GenericBindingManager manager, String name)
	{
		super();
		this.manager = manager;
		this.name = name;
		BrandManager brandManager = manager.getBrandManager();
		IBrand defaultBrand = brandManager.getDefaultBrand();
		createBrandBinding(defaultBrand);
		brandManager.addListener(this);
	}
	
	public LanguageBinding replicate(String replicationName)
	{
		LanguageBinding copy = new LanguageBinding(manager, replicationName);
		for(Map.Entry<String, BrandBinding> entry : brandBindings.entrySet())
		{
			BrandBinding brandCopy = (BrandBinding)entry.getValue().clone();
			copy.brandBindings.put(brandCopy.getBrand().getId(), brandCopy);
		}
		return copy;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	public void finalize()
	{
		manager.getBrandManager().removeListener(this);
	}
	
	/**
	 * @return The name of the language associated with this binding
	 */
	public String getLanguage()
	{
		return name;
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
	 * @param languageBindingElement The dom element containing the configuration
	 */
	public void readConfiguration(Element languageBindingElement)
	{
		NodeList brandBindingElementList = languageBindingElement.getElementsByTagName("brand-binding");
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
	 * @param languageBindingElement The dom element to hold this binding's data
	 */
	public void writeConfiguration(Element languageBindingElement)
	{
		Iterator<BrandBinding> iterator = brandBindings.values().iterator();
		while(iterator.hasNext())
		{
			BrandBinding brandBinding = iterator.next();
			if(!brandBinding.isInherited())
			{
				Element brandBindingElement = languageBindingElement.getOwnerDocument().createElement("brand-binding");
				languageBindingElement.appendChild(brandBindingElement);
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
		out.println("[Language Binding] " + name);
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
		BrandBinding brandBinding = new BrandBinding(manager, brand);
		brandBindings.put(brand.getId(), brandBinding);
		List<IBrand> children = brand.getChildBrands();
		for(IBrand child : children)
		{
			BrandBinding bindingChild = createBrandBinding(child);
			bindingChild.setParent(brandBinding);
		}
		return brandBinding;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.BrandManagerListener#brandAdded(org.eclipse.vtp.desktop.core.configuration.Brand)
	 */
	public void brandAdded(IBrand brand)
    {
		BrandBinding parentBinding = brandBindings.get(brand.getParent().getId());
		BrandBinding brandBinding = createBrandBinding(brand);
		brandBinding.setParent(parentBinding);
    }

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.BrandManagerListener#brandNameChanged(org.eclipse.vtp.desktop.core.configuration.Brand, java.lang.String)
	 */
	public void brandNameChanged(IBrand brand, String oldName)
    {
    }

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.BrandManagerListener#brandParentChanged(org.eclipse.vtp.desktop.core.configuration.Brand, org.eclipse.vtp.desktop.core.configuration.Brand)
	 */
	public void brandParentChanged(IBrand brand, IBrand oldParent)
    {
    }

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.BrandManagerListener#brandRemoved(org.eclipse.vtp.desktop.core.configuration.Brand)
	 */
	public void brandRemoved(IBrand brand)
    {
		brandBindings.remove(brand.getId());
    }
}
