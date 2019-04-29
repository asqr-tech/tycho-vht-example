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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class contains the named bindings associated with an interaction type.
 * Each interaction type will likely have its own unique set of named bindings.
 * There may be similarly named bindings present in multiple interaction
 * bindings but there is no guarantee that they are related in any way.
 * 
 * @author trip
 */
public class InteractionBinding
{
	/**	The unique identifier of the interaction type represented */
	private String interactionType = null;
	/**	The manager that contains this binding */
	private GenericBindingManager manager = null;
	/**	An index of the named bindings contained by this binding based on the binding name */
	private Map<String, NamedBinding> namedBindings = new TreeMap<String, NamedBinding>();

	/**
	 * Creates a new interaction binding instance that is contained by the
	 * provided manager and associated with the interaction type with the given
	 * identifier.
	 * 
	 * @param manager The manager that contains this binding
	 * @param interactionType The identifier for the associated interaciton type
	 */
	public InteractionBinding(GenericBindingManager manager, String interactionType)
	{
		super();
		this.manager = manager;
		this.interactionType = interactionType;
	}

	/**
	 * @return The identifier of the interaction type associated with this binding
	 */
	public String getInteractionType()
	{
		return interactionType;
	}
	
	public List<NamedBinding> getNamedBindings()
	{
		List<NamedBinding> ret = new ArrayList<NamedBinding>();
		ret.addAll(namedBindings.values());
		return ret;
	}
	
	/**
	 * Retrieves the named binding with the given name.  If no named binding is
	 * located, a new named binding instance is created with the given name and
	 * added to this interaction binding.
	 * 
	 * @param bindingName The name of the requested binding
	 * @return The binding object with the given name
	 */
	public NamedBinding getNamedBinding(String bindingName)
	{
		NamedBinding namedBinding = namedBindings.get(bindingName);
		if(namedBinding == null) //auto generate the named binding
		{
			namedBinding = new NamedBinding(manager, bindingName);
			namedBindings.put(bindingName, namedBinding);
		}
		return namedBinding;
	}
	
	/**
	 * Reads the configuration data stored in the given dom element into this
	 * interaction binding instance.  Any previous information stored in this
	 * interaction binding is lost.
	 * 
	 * @param interactionElement The dom element containing the configuration
	 */
	public void readConfiguration(Element interactionElement)
	{
		NodeList namedBindingElementList = interactionElement.getElementsByTagName("named-binding");
		for(int i = 0; i < namedBindingElementList.getLength(); i++)
		{
			Element namedBindingElement = (Element)namedBindingElementList.item(i);
			String bindingName = namedBindingElement.getAttribute("name");
			NamedBinding namedBinding = new NamedBinding(manager, bindingName);
			namedBinding.readConfiguration(namedBindingElement);
			namedBindings.put(bindingName, namedBinding);
		}
	}
	
	/**
	 * Stores this interaction binding's information into the given dom element.
	 * 
	 * @param interactionElement The dom element to hold this binding's data
	 */
	public void writeConfiguration(Element interactionElement)
	{
		Iterator<NamedBinding> iterator = namedBindings.values().iterator();
		while(iterator.hasNext())
		{
			NamedBinding namedBinding = iterator.next();
			Element namedBindingElement = interactionElement.getOwnerDocument().createElement("named-binding");
			interactionElement.appendChild(namedBindingElement);
			namedBindingElement.setAttribute("name", namedBinding.getName());
			namedBinding.writeConfiguration(namedBindingElement);
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
		out.println("[Interaction Binding] " + interactionType);
		out.println("Named Bindings");
		Iterator<NamedBinding> iterator = namedBindings.values().iterator();
		while(iterator.hasNext())
		{
			NamedBinding namedBinding = iterator.next();
			namedBinding.dumpContents(out);
		}
	}

	public void renameNamedBinding(String oldName, String newName)
	{
		NamedBinding nb = namedBindings.get(oldName);
		nb.setName(newName);
		namedBindings.put(newName, nb);
		namedBindings.remove(oldName);
	}
}
