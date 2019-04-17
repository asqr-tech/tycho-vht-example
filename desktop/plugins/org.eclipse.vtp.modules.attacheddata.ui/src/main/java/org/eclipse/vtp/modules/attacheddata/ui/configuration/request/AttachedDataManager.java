/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.modules.attacheddata.ui.configuration.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationException;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManager;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This implementation of <code>ConfigurationManager</code> records configuration
 * data for the attached data request element.  The names of the attached data
 * items requested and the destination variable names are stored in this
 * configuration.
 * 
 * @author trip
 */
public class AttachedDataManager implements ConfigurationManager
{
	/**	Identifier for this configuration manager type */
	public static final String CONFIGURATION_TYPE_ID =
		"org.eclipse.vtp.configuration.attacheddata.request"; //$NON-NLS-1$
	/**	URI used as the namespace for xml elements */
	public static final String NAMESPACE_URI =
		"http://eclipse.org/vtp/xml/configuration/attacheddata/request"; //$NON-NLS-1$
	/**	The current version of this configuration's xml format */
	public static final String XML_VERSION = "1.0.0"; //$NON-NLS-1$
	
	/**	A map of the bindings indexed on the binding name */
	private Map<String, AttachedDataBinding> bindings;
	/**	A list of bindings contained by this manager */
	private List<AttachedDataBinding> dataBindings;
	/**	The brand manager used by this configuration manager to resolve brand information */
	private BrandManager brandManager = null;
	private IDesign design = null;

	/**
	 * Creates a new <code>AttachedDataManager</code> that will use the given
	 * <code>BrandManager</code> to resolve needed brand information.  There are
	 * no bindings in this instance initially.
	 * 
	 * @param brandManager The brand manager to use to resolve brand information
	 */
	public AttachedDataManager(IDesign design)
	{
		super();
		bindings = new HashMap<String, AttachedDataBinding>();
		dataBindings = new ArrayList<AttachedDataBinding>();
		this.design = design;
		this.brandManager = design.getDocument().getProject().getBrandManager();
	}

	/**
	 * Returns the brand manager instance used by this configuration manager to
	 * resolve brand information.
	 * 
	 * @return BrandManager instance used to resolve brand information
	 */
	public BrandManager getBrandManager()
	{
		return brandManager;
	}
	
	/**
	 * Returns the attached data binding with the given name.  If no binding
	 * is present with that name, a new binding will be created and returned.
	 * 
	 * @param name The name of the binding item
	 * @return The binding with the given name
	 */
	public AttachedDataBinding getAttachedDataBinding(String name)
	{
		AttachedDataBinding binding = bindings.get(name);
		if (binding == null)
		{
			binding = new AttachedDataBinding(this, name);
			bindings.put(name, binding);
			dataBindings.add(binding);
		}
		return binding;
	}

	/**
	 * Returns a list of the attached data bindings this configuration
	 * manager currently contains.
	 *  
	 * @return List of attached data bindings
	 */
	public List<AttachedDataBinding> listBindings()
	{
		List<AttachedDataBinding> ret = new ArrayList<AttachedDataBinding>(dataBindings.size());
		ret.addAll(dataBindings);
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.ConfigurationManager#getType()
	 */
	public String getType()
	{
		return CONFIGURATION_TYPE_ID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.ConfigurationManager#getXMLVersion()
	 */
	public String getXMLVersion()
	{
		return XML_VERSION;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.ConfigurationManager#readConfiguration(org.w3c.dom.Element)
	 */
	public void readConfiguration(Element configuration) throws ConfigurationException
	{
		// Data Bindings
		NodeList bindingList = configuration.getElementsByTagNameNS(
				NAMESPACE_URI, "attached-data-binding"); //$NON-NLS-1$
		for (int i = 0; i < bindingList.getLength(); i++)
		{
			Element bindingElement = (Element) bindingList.item(i);
			AttachedDataBinding binding = getAttachedDataBinding(bindingElement
					.getAttribute("name")); //$NON-NLS-1$
			binding.readBindingItems(bindingElement);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.ConfigurationManager#writeConfiguration(org.w3c.dom.Element)
	 */
	public void writeConfiguration(Element configuration)
	{
		// Data Bindings
		for (Iterator<AttachedDataBinding> i = dataBindings.iterator(); i.hasNext();)
		{
			AttachedDataBinding binding = i.next();
			Element bindingElement = configuration.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, "attached-data-binding"); //$NON-NLS-1$
			bindingElement.setAttribute("name", binding.getName()); //$NON-NLS-1$
			binding.writeBindingItems(bindingElement);
			configuration.appendChild(bindingElement);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		AttachedDataManager copy = new AttachedDataManager(design);
		try
		{
			//build document contents
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.getDOMImplementation().createDocument(null, "temporary-document", null); //$NON-NLS-1$
			org.w3c.dom.Element rootElement = document.getDocumentElement();
			rootElement.setAttribute("xml-version", XML_VERSION); //$NON-NLS-1$
			writeConfiguration(rootElement);
			copy.readConfiguration(rootElement);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return copy;
	}
}
