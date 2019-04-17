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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class represents a set of language bindings associated with a name.
 * 
 * @author trip
 */
public class NamedBinding
{
	/**	The name of this binding */
	private String name = null;
	/**	The binding manager that contains this binding */
	private GenericBindingManager manager = null;
	/**	An index of the language bindings contained by this binding, based on the language name */
	private Map<String, LanguageBinding> languageBindings = new TreeMap<String, LanguageBinding>();

	/**
	 * Creates a new named binding instance that is contained by the provided
	 * manager with the given name.
	 * 
	 * @param manager The binding manager that contains this binding
	 * @param name The name of this binding
	 */
	public NamedBinding(GenericBindingManager manager, String name)
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
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void duplicateLanguageBinding(String sourceLanguage, String destinationLanguage, boolean force) throws IllegalStateException
	{
		if(languageBindings.get(destinationLanguage) != null && !force)
			throw new IllegalStateException("That langauge already has configuration present");
		LanguageBinding sourceBinding = languageBindings.get(sourceLanguage);
		if(sourceBinding == null)
			return;
		languageBindings.put(destinationLanguage, sourceBinding.replicate(destinationLanguage));
	}

	/**
	 * Retrieves the language binding associated with the given language name.
	 * 
	 * @param languageName The name of the language associated with the desired binding
	 * @return The language binding associated with the language with the given name
	 */
	public LanguageBinding getLanguageBinding(String languageName)
	{
		LanguageBinding languageBinding = languageBindings.get(languageName);
		if(languageBinding == null)
		{
			languageBinding = new LanguageBinding(manager, languageName);
			languageBindings.put(languageName, languageBinding);
		}
		return languageBinding;
	}
	
	/**
	 * Reads the configuration data stored in the given dom element into this
	 * named binding instance.  Any previous information stored in this named
	 * binding is lost.
	 * 
	 * @param namedBindingElement The dom element containing the configuration
	 */
	public void readConfiguration(Element namedBindingElement)
	{
		NodeList languageBindingElementList = namedBindingElement.getElementsByTagName("language-binding");
		for(int i = 0; i < languageBindingElementList.getLength(); i++)
		{
			Element languageBindingElement = (Element)languageBindingElementList.item(i);
			String language = languageBindingElement.getAttribute("language");
			LanguageBinding languageBinding = new LanguageBinding(manager, language);
			languageBinding.readConfiguration(languageBindingElement);
			languageBindings.put(language, languageBinding);
		}
	}
	
	/**
	 * Stores this named binding's information into the given dom element.
	 * 
	 * @param namedBindingElement The dom element to hold this binding's data
	 */
	public void writeConfiguration(Element namedBindingElement)
	{
		Iterator<LanguageBinding> iterator = languageBindings.values().iterator();
		while(iterator.hasNext())
		{
			LanguageBinding languageBinding = iterator.next();
			Element languageBindingElement = namedBindingElement.getOwnerDocument().createElement("language-binding");
			namedBindingElement.appendChild(languageBindingElement);
			languageBindingElement.setAttribute("language", languageBinding.getLanguage());
			languageBinding.writeConfiguration(languageBindingElement);
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
		out.println("[Named Binding] " + name);
		out.println("Language Bindings");
		Iterator<LanguageBinding> iterator = languageBindings.values().iterator();
		while(iterator.hasNext())
		{
			LanguageBinding languageBinding = iterator.next();
			languageBinding.dumpContents(out);
		}
	}
}
