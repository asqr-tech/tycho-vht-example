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

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class represents a set of output bindings associated with a name.
 * 
 * @author trip
 */
public class ExitBinding
{
	/**	The name of this binding */
	private String name = null;
	/**	The binding manager that contains this binding */
	private FragmentConfigurationManager manager = null;
	/**	An index of the output bindings contained by this binding, based on the output name */
	private Map<String, OutputBinding> outputBindings = new TreeMap<String, OutputBinding>();

	/**
	 * Creates a new exit binding instance that is contained by the provided
	 * manager with the given name.
	 * 
	 * @param manager The binding manager that contains this binding
	 * @param name The name of this binding
	 */
	public ExitBinding(FragmentConfigurationManager manager, String name)
	{
		super();
		this.manager = manager;
		this.name = name;
	}
	
	/**
	 * @return the name of this binding
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Retrieves the output binding associated with the given name.
	 * 
	 * @param outputName The name of the output associated with the desired binding
	 * @return The output binding associated with the output with the given name
	 */
	public OutputBinding getOutputBinding(String outputName)
	{
		return outputBindings.get(outputName);
	}
	
	public List<OutputBinding> getOutputBindings()
	{
		return new LinkedList<OutputBinding>(outputBindings.values());
	}
	
	public OutputBinding addOutputBinding(String outputName)
	{
		OutputBinding ret = outputBindings.get(outputName);
		if(ret == null)
			outputBindings.put(outputName, ret = new OutputBinding(manager, outputName));
		return ret;
	}
	
	public void removeOutputBinding(OutputBinding binding)
	{
		outputBindings.remove(binding.getOutput());
	}
	
	public void clearOutputBindings()
	{
		outputBindings.clear();
	}
	
	/**
	 * Reads the configuration data stored in the given DOM element into this
	 * exit binding instance.  Any previous information stored in this exit
	 * binding is lost.
	 * 
	 * @param exitBindingElement The DOM element containing the configuration
	 */
	public void readConfiguration(Element exitBindingElement)
	{
		NodeList outputBindingElementList = exitBindingElement.getElementsByTagName("output-binding");
		for(int i = 0; i < outputBindingElementList.getLength(); i++)
		{
			Element outputBindingElement = (Element)outputBindingElementList.item(i);
			String outputName = outputBindingElement.getAttribute("name");
			OutputBinding outputBinding = new OutputBinding(manager, outputName);
			outputBinding.readConfiguration(outputBindingElement);
			outputBindings.put(outputName, outputBinding);
		}
	}
	
	/**
	 * Stores this exit binding's information into the given DOM element.
	 * 
	 * @param exitBindingElement The DOM element to hold this binding's data
	 */
	public void writeConfiguration(Element exitBindingElement)
	{
		Iterator<OutputBinding> iterator = outputBindings.values().iterator();
		while(iterator.hasNext())
		{
			OutputBinding outputBinding = iterator.next();
			Element outputBindingElement = exitBindingElement.getOwnerDocument().createElement("output-binding");
			exitBindingElement.appendChild(outputBindingElement);
			outputBindingElement.setAttribute("name", outputBinding.getOutput());
			outputBinding.writeConfiguration(outputBindingElement);
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
		out.println("[Exit Binding] " + name);
		out.println("Output Bindings");
		Iterator<OutputBinding> iterator = outputBindings.values().iterator();
		while(iterator.hasNext())
		{
			OutputBinding outputBinding = iterator.next();
			outputBinding.dumpContents(out);
		}
	}
}
