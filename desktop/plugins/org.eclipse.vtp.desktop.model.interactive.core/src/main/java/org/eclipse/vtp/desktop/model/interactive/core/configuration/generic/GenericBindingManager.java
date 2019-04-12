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
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationException;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManager;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowProject;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionTypeManager;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSettings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class implements the <code>ConfigurationManager</code> interface and
 * is used to manage a generic set of information bindings.  The data is held
 * in a hierarchy with interaction type as the root and then progressing through
 * binding name, language, and ending with the brand structure. Only the brand
 * structure supports inheritance in this tree.
 * 
 * This manager also provides access to the media defaults for the workspace and
 * project.  The defaults are plugged in at the base of the brand structure to
 * provide default values for each named binding.
 * 
 * @author trip
 */
public class GenericBindingManager implements ConfigurationManager
{
	/**	The unique identifier for this manager type */
	public static final String TYPE_ID = "org.eclipse.vtp.configuration.generic";
	/**	The current XML structure version used by this manager */
	public static final String XML_VERSION = "1.0.0";
	
	/**	The brand manager to use when resolving the brand hierarchy */
	private BrandManager brandManager = null;
	/**	The current set of media defaults available to this manager */
	private IMediaDefaultSettings mediaDefaults = null;
	/**	An index of objects representing configuration data for interaction
	 * types not supported by this installation */
	private Map<String, MissingInteractionBinding> missingInteractionBindings =
		new TreeMap<String, MissingInteractionBinding>();
	/**	An index of interaction bindings based on interaction type */
	private Map<String, InteractionBinding> interactionBindings = new TreeMap<String, InteractionBinding>();
	private IDesign hostDesign = null;

	/**
	 * Creates a new instance of this manager that will use the given brand
	 * manager to resolve the brand structure and have the provided media
	 * default values.
	 * 
	 */
	public GenericBindingManager(IDesign design)
	{
		super();
		this.hostDesign = design;
		this.brandManager = design.getDocument().getProject().getBrandManager();
		this.mediaDefaults = ((IInteractiveWorkflowProject)design.getDocument().getProject()).getMediaDefaultSettings();
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
	 * Returns the interaction binding object for the given interaction type
	 * 
	 * @param interactionType The id of the interaction type requested
	 * @return the binding associated with the provided interaction type
	 */
	public InteractionBinding getInteractionBinding(String interactionType)
	{
		InteractionBinding interactionBinding = interactionBindings.get(interactionType);
		if(interactionBinding == null) //auto generate the named binding
		{
			interactionBinding = new InteractionBinding(this, interactionType);
			interactionBindings.put(interactionType, interactionBinding);
		}
		return interactionBinding;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.ConfigurationManager#readConfiguration(org.w3c.dom.Element)
	 */
	public void readConfiguration(Element configuration) throws ConfigurationException
	{
		NodeList interactionElementList = configuration.getElementsByTagName("interaction-binding");
		for(int i = 0; i < interactionElementList.getLength(); i++)
		{
			Element interactionElement = (Element)interactionElementList.item(i);
			String interactionTypeId = interactionElement.getAttribute("type");
			InteractionType interactionType = InteractionTypeManager.getInstance().getType(interactionTypeId);
			if(interactionType != null)
			{
				InteractionBinding interactionBinding = new InteractionBinding(this, interactionTypeId);
				interactionBinding.readConfiguration(interactionElement);
				interactionBindings.put(interactionTypeId, interactionBinding);
			}
			else
			{
				MissingInteractionBinding missingInteractionBinding = new MissingInteractionBinding(interactionElement);
				missingInteractionBindings.put(interactionTypeId, missingInteractionBinding);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.ConfigurationManager#writeConfiguration(org.w3c.dom.Element)
	 */
	public void writeConfiguration(Element configuration)
	{
		Map<String, Object> tempMap = new TreeMap<String, Object>();
		tempMap.putAll(interactionBindings);
		tempMap.putAll(missingInteractionBindings);
		for(Object obj : tempMap.values())
		{
			if(obj instanceof InteractionBinding)
			{
				InteractionBinding interactionBinding = (InteractionBinding)obj;
				Element interactionElement = configuration.getOwnerDocument().createElement("interaction-binding");
				configuration.appendChild(interactionElement);
				interactionElement.setAttribute("type", interactionBinding.getInteractionType());
				interactionBinding.writeConfiguration(interactionElement);
			}
			else
			{
				MissingInteractionBinding missingBinding = (MissingInteractionBinding)obj;
				missingBinding.writeConfiguration(configuration);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		GenericBindingManager copy = new GenericBindingManager(hostDesign);
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

	/**
	 * @return the brand manager this manager uses to resolve brand structure
	 */
	public BrandManager getBrandManager()
    {
    	return brandManager;
    }

	/**
	 * @return The media default values available to this manager
	 */
	public IMediaDefaultSettings getMediaDefaults()
    {
    	return mediaDefaults;
    }
	
	/**
	 * Prints this binding's information to the given print stream.  This is
	 * useful for logging and debugging.
	 * 
	 * @param out The print stream to write the information to
	 */
	public void dumpContents(PrintStream out)
	{
		out.println("Generic Binding Manager\r\n");
		out.println("Interaction Bindings");
		Iterator<InteractionBinding> iterator = interactionBindings.values().iterator();
		while(iterator.hasNext())
		{
			InteractionBinding interactionBinding = iterator.next();
			interactionBinding.dumpContents(out);
		}
	}
	
	public void renameNamedBinding(String interactionType, String oldName, String newName)
	{
		InteractionBinding ib = interactionBindings.get(interactionType);
		ib.renameNamedBinding(oldName, newName);
	}
}
