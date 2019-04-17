package org.eclipse.vtp.desktop.model.elements.core.configuration;

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
 * This class binds the brand structure to a specific output.  The brand
 * structure contained by this output binding is automatically created during
 * instantiation.
 * 
 * @author trip
 */
public class OutputBinding implements BrandManagerListener
{
	/** The configuration manager that contains this binding */
	private FragmentConfigurationManager manager = null;
	/**	The name of this binding's associated output */
	private String name = null;
	/**	An index of brand bindings based on the brand id */
	private Map<String, OutputBrandBinding> brandBindings = new TreeMap<String, OutputBrandBinding>();

	/**
	 * Constructs a new output binding instance that is contained by the
	 * provided binding manager and is associated with the output with the
	 * given name.  The brand structure is automatically created.
	 * 
	 * @param manager The binding manager that contains this binding
	 * @param name The name of the output associated with this binding
	 */
	public OutputBinding(FragmentConfigurationManager manager, String name)
	{
		super();
		this.manager = manager;
		this.name = name;
		BrandManager brandManager = manager.getBrandManager();
		IBrand defaultBrand = brandManager.getDefaultBrand();
		createBrandBinding(defaultBrand);
		brandManager.addListener(this);
	}

	/**
	 * @return The name of the output associated with this binding
	 */
	public String getOutput()
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
	public OutputBrandBinding getBrandBinding(IBrand brand)
	{
		return brandBindings.get(brand.getId());
	}
	
	/**
	 * Reads the configuration data stored in the given DOM element into this
	 * output binding instance.  Any previous information stored in this output
	 * binding is lost.
	 * 
	 * @param inputBindingElement The DOM element containing the configuration
	 */
	public void readConfiguration(Element inputBindingElement)
	{
		NodeList brandBindingElementList = inputBindingElement.getElementsByTagName("brand-binding");
		for(int i = 0; i < brandBindingElementList.getLength(); i++)
		{
			Element brandBindingElement = (Element)brandBindingElementList.item(i);
			String brandId = brandBindingElement.getAttribute("id");
			OutputBrandBinding brandBinding = brandBindings.get(brandId);
			if(brandBinding != null)
			{
				brandBinding.readConfiguration(brandBindingElement);
			}
		}
	}
	
	/**
	 * Stores this input binding's information into the given DOM element.
	 * 
	 * @param inputBindingElement The DOM element to hold this binding's data
	 */
	public void writeConfiguration(Element inputBindingElement)
	{
		Iterator<OutputBrandBinding> iterator = brandBindings.values().iterator();
		while(iterator.hasNext())
		{
			OutputBrandBinding brandBinding = iterator.next();
			if(!brandBinding.isInherited())
			{
				Element brandBindingElement = inputBindingElement.getOwnerDocument().createElement("brand-binding");
				inputBindingElement.appendChild(brandBindingElement);
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
		out.println("[Output Binding] " + name);
		out.println("IBrand Bindings");
		Iterator<OutputBrandBinding> iterator = brandBindings.values().iterator();
		while(iterator.hasNext())
		{
			OutputBrandBinding brandBinding = iterator.next();
			brandBinding.dumpContents(out);
		}
	}
	
	/**
	 * Recursively creates the brand binding structure.
	 * 
	 * @param brand The brand to bind
	 * @return The binding for the given brand
	 */
	private OutputBrandBinding createBrandBinding(IBrand brand)
	{
		OutputBrandBinding brandBinding = new OutputBrandBinding(manager, brand);
		brandBindings.put(brand.getId(), brandBinding);
		List<IBrand> children = brand.getChildBrands();
		for(IBrand child : children)
		{
			OutputBrandBinding bindingChild = createBrandBinding(child);
			bindingChild.setParent(brandBinding);
		}
		return brandBinding;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.BrandManagerListener#brandAdded(org.eclipse.vtp.desktop.core.configuration.Brand)
	 */
	public void brandAdded(IBrand brand)
    {
		OutputBrandBinding parentBinding = brandBindings.get(brand.getParent().getId());
		OutputBrandBinding brandBinding = createBrandBinding(brand);
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
