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
package org.eclipse.vtp.desktop.model.elements.core.configuration;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
 * This class implements the <code>ConfigurationManager</code> interface and
 * is used to manage the input and output bindings for a fragment element.  The
 * data is held in two hierarchies one for inputs and one for outputs. Only the
 * brand structure supports inheritance in this tree.
 * 
 * @author trip
 */
public class FragmentConfigurationManager implements ConfigurationManager
{
	/**	The unique identifier for this manager type */
	public static final String TYPE_ID = "org.eclipse.vtp.configuration.include";
	/**	The current XML structure version used by this manager */
	public static final String XML_VERSION = "1.0.0";
	
	/**	The brand manager to use when resolving the brand hierarchy */
	private BrandManager brandManager = null;
	/** The id of the entry point being referenced by this fragment element */
	private String entryId = null;
	/**	An index of input bindings based on input name */
	private Map<String, InputBinding> inputBindings = new TreeMap<String, InputBinding>();
	/** An index of exit bindings based on the exit name */
	private Map<String, ExitBinding> exitBindings = new TreeMap<String, ExitBinding>();
	/** A list of change listeners */
	private List<FragmentConfigurationListener> listeners = new LinkedList<FragmentConfigurationListener>();

	/**
	 * 
	 */
	public FragmentConfigurationManager(IDesign design)
	{
		super();
		brandManager = design.getDocument().getProject().getBrandManager();
	}
	
	private FragmentConfigurationManager(BrandManager brandManager)
	{
		super();
		this.brandManager = brandManager;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.ConfigurationManager#getType()
	 */
	public String getType()
	{
		return TYPE_ID;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.ConfigurationManager#getXMLVersion()
	 */
	public String getXMLVersion()
	{
		return XML_VERSION;
	}
	
	/**
	 * @return the brand manager this manager uses to resolve brand structure
	 */
	public BrandManager getBrandManager()
    {
    	return brandManager;
    }
	
	public String getEntryId()
	{
		return this.entryId;
	}
	
	public void setEntryId(String entryId)
	{
		this.entryId = entryId;
		for(FragmentConfigurationListener listener : listeners)
		{
			listener.entryChanged(this);
		}
	}
	
	public List<InputBinding> getInputBindings()
	{
		return new LinkedList<InputBinding>(inputBindings.values());
	}
	
	public InputBinding addInputBinding(String input)
	{
		InputBinding ret = inputBindings.get(input);
		if(ret == null)
		{
			ret = new InputBinding(this, input);
			inputBindings.put(input, ret);
		}
		return ret;
	}
	
	public void removeInputBinding(String input)
	{
		inputBindings.remove(input);
	}
	
	public void clearInputBindings()
	{
		inputBindings.clear();
	}
	
	public ExitBinding getExitBinding(String exit)
	{
		return exitBindings.get(exit);
	}
	
	public List<ExitBinding> getExitBindings()
	{
		return new LinkedList<ExitBinding>(exitBindings.values());
	}
	
	public ExitBinding addExitBinding(String exit)
	{
		ExitBinding ret = exitBindings.get(exit);
		if(ret == null)
		{
			ret = new ExitBinding(this, exit);
			exitBindings.put(exit, ret);
		}
		return ret;
	}
	
	public void removeExitBinding(String exit)
	{
		exitBindings.remove(exit);
	}
	
	public void clearExitBindings()
	{
		exitBindings.clear();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManager#readConfiguration(org.w3c.dom.Element)
	 */
	public void readConfiguration(Element configuration)
		throws ConfigurationException
	{
		entryId = configuration.getAttribute("entry-id");
		NodeList inputElementList = configuration.getElementsByTagName("input-binding");
		for(int i = 0; i < inputElementList.getLength(); i++)
		{
			Element inputElement = (Element)inputElementList.item(i);
			String inputName = inputElement.getAttribute("name");
			InputBinding inputBinding = new InputBinding(this, inputName);
			inputBinding.readConfiguration(inputElement);
			inputBindings.put(inputName, inputBinding);
		}
		NodeList exitElementList = configuration.getElementsByTagName("exit-binding");
		for(int i = 0; i < exitElementList.getLength(); i++)
		{
			Element exitElement = (Element)exitElementList.item(i);
			String exitName = exitElement.getAttribute("name");
			ExitBinding exitBinding = new ExitBinding(this, exitName);
			exitBinding.readConfiguration(exitElement);
			exitBindings.put(exitName, exitBinding);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManager#writeConfiguration(org.w3c.dom.Element)
	 */
	public void writeConfiguration(Element configuration)
	{
		configuration.setAttribute("entry-id", entryId);
		for(InputBinding inputBinding : inputBindings.values())
		{
			Element inputElement = configuration.getOwnerDocument().createElement("input-binding");
			configuration.appendChild(inputElement);
			inputElement.setAttribute("name", inputBinding.getInput());
			inputBinding.writeConfiguration(inputElement);
		}
		for(ExitBinding exitBinding : exitBindings.values())
		{
			Element exitElement = configuration.getOwnerDocument().createElement("exit-binding");
			configuration.appendChild(exitElement);
			exitElement.setAttribute("name", exitBinding.getName());
			exitBinding.writeConfiguration(exitElement);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		FragmentConfigurationManager copy = new FragmentConfigurationManager(brandManager);
		try
		{
			//build document contents
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.getDOMImplementation().createDocument(null, "temporary-document", null);
			org.w3c.dom.Element rootElement = document.getDocumentElement();
			rootElement.setAttribute("xml-version", XML_VERSION);
			writeConfiguration(rootElement);
			copy.readConfiguration(rootElement);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return copy;
	}
	
	public void addListener(FragmentConfigurationListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}
	
	public void removeListener(FragmentConfigurationListener listener)
	{
		listeners.remove(listener);
	}
}
