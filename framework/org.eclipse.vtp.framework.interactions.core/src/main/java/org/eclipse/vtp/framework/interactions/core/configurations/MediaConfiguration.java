/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.core.configurations;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.eclipse.vtp.framework.interactions.core.media.IContentFactory;
import org.eclipse.vtp.framework.interactions.core.media.IInputGrammarFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * MediaConfiguration.
 * 
 * @author Lonnie Pryor
 */
public class MediaConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** The content factory to use. */
	private final IContentFactory contentFactory;
	/** The input factory to use. */
	private final IInputGrammarFactory inputFactory;
	/** Index of output configurations by name. */
	private final Map outputConfigurations = new LinkedHashMap();
	/** Index of output configurations by name. */
	private final Map inputConfigurations = new LinkedHashMap();
	/** Index of property configurations by name. */
	private final Map propertyConfigurations = new LinkedHashMap();

	/**
	 * Creates a new MediaConfiguration.
	 * 
	 * @param contentFactory The content factory to use.
	 * @param inputFactory The input factory to use.
	 */
	public MediaConfiguration(IContentFactory contentFactory,
			IInputGrammarFactory inputFactory)
	{
		this.contentFactory = contentFactory;
		this.inputFactory = inputFactory;
	}

	/**
	 * Returns the names of the currently registered output configurations.
	 * 
	 * @return The names of the currently registered output configurations.
	 */
	public String[] getOutputConfigurationNames()
	{
		return (String[])outputConfigurations.keySet().toArray(
				new String[outputConfigurations.size()]);
	}

	/**
	 * Returns the output configuration registered under the specified name or
	 * <code>null</code> if no such configuration is registered.
	 * 
	 * @param name The name of the configuration to find.
	 * @return The output configuration registered under the specified name or
	 *         <code>null</code> if no such configuration is registered.
	 */
	public OutputConfiguration getOutputConfiguration(String name)
	{
		return (OutputConfiguration)outputConfigurations.get(name);
	}

	/**
	 * Sets the output configuration registered under the specified name.
	 * 
	 * @param name The name to register the configuration under.
	 * @param configuration The configuration to register or <code>null</code>
	 *          to remove the specified configuration.
	 */
	public void setOutputConfiguration(String name,
			OutputConfiguration configuration)
	{
		if (configuration == null)
			outputConfigurations.remove(name);
		else
			outputConfigurations.put(name, configuration);
	}

	/**
	 * Returns the names of the currently registered input configurations.
	 * 
	 * @return The names of the currently registered input configurations.
	 */
	public String[] getInputConfigurationNames()
	{
		return (String[])inputConfigurations.keySet().toArray(
				new String[inputConfigurations.size()]);
	}

	/**
	 * Returns the input configuration registered under the specified name or
	 * <code>null</code> if no such configuration is registered.
	 * 
	 * @param name The name of the configuration to find.
	 * @return The input configuration registered under the specified name or
	 *         <code>null</code> if no such configuration is registered.
	 */
	public InputConfiguration getInputConfiguration(String name)
	{
		return (InputConfiguration)inputConfigurations.get(name);
	}

	/**
	 * Sets the input configuration registered under the specified name.
	 * 
	 * @param name The name to register the configuration under.
	 * @param configuration The configuration to register or <code>null</code>
	 *          to remove the specified configuration.
	 */
	public void setInputConfiguration(String name,
			InputConfiguration configuration)
	{
		if (configuration == null)
			inputConfigurations.remove(name);
		else
			inputConfigurations.put(name, configuration);
	}

	/**
	 * Returns the names of the currently registered property configurations.
	 * 
	 * @return The names of the currently registered property configurations.
	 */
	public String[] getPropertyConfigurationNames()
	{
		return (String[])propertyConfigurations.keySet().toArray(
				new String[propertyConfigurations.size()]);
	}

	/**
	 * Returns the property configuration registered under the specified name or
	 * <code>null</code> if no such configuration is registered.
	 * 
	 * @param name The name of the configuration to find.
	 * @return The property configuration registered under the specified name or
	 *         <code>null</code> if no such configuration is registered.
	 */
	public PropertyConfiguration getPropertyConfiguration(String name)
	{
		return (PropertyConfiguration)propertyConfigurations.get(name);
	}

	/**
	 * Sets the property configuration registered under the specified name.
	 * 
	 * @param name The name to register the configuration under.
	 * @param configuration The configuration to register or <code>null</code>
	 *          to remove the specified configuration.
	 */
	public void setPropertyConfiguration(String name,
			PropertyConfiguration configuration)
	{
		if (configuration == null)
			propertyConfigurations.remove(name);
		else
			propertyConfigurations.put(name, configuration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		outputConfigurations.clear();
		inputConfigurations.clear();
		propertyConfigurations.clear();
		NodeList elements = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, NAME_OUTPUT);
		for (int i = 0; i < elements.getLength(); ++i)
		{
			Element element = (Element)elements.item(i);
			String name = element.getAttribute(NAME_NAME);
			OutputConfiguration configuration = new OutputConfiguration(
					contentFactory);
			configuration.load(element);
			outputConfigurations.put(name, configuration);
		}
		elements = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_INPUT);
		for (int i = 0; i < elements.getLength(); ++i)
		{
			Element element = (Element)elements.item(i);
			String name = element.getAttribute(NAME_NAME);
			InputConfiguration configuration = new InputConfiguration(inputFactory);
			configuration.load(element);
			inputConfigurations.put(name, configuration);
		}
		elements = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_PROPERTY);
		for (int i = 0; i < elements.getLength(); ++i)
		{
			Element element = (Element)elements.item(i);
			String name = element.getAttribute(NAME_NAME);
			PropertyConfiguration configuration = new PropertyConfiguration();
			configuration.load(element);
			propertyConfigurations.put(name, configuration);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		String outputName = NAME_OUTPUT;
		String inputName = NAME_INPUT;
		String propertyName = NAME_PROPERTY;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
		{
			outputName = prefix + ":" + outputName; //$NON-NLS-1$
			inputName = prefix + ":" + inputName; //$NON-NLS-1$
			propertyName = prefix + ":" + propertyName; //$NON-NLS-1$
		}
		for (Iterator i = outputConfigurations.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, outputName);
			((IConfiguration)entry.getValue()).save(element);
			element.setAttribute(NAME_NAME, (String)entry.getKey());
			configurationElement.appendChild(element);
		}
		for (Iterator i = inputConfigurations.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, inputName);
			((IConfiguration)entry.getValue()).save(element);
			element.setAttribute(NAME_NAME, (String)entry.getKey());
			configurationElement.appendChild(element);
		}
		for (Iterator i = propertyConfigurations.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, propertyName);
			((IConfiguration)entry.getValue()).save(element);
			element.setAttribute(NAME_NAME, (String)entry.getKey());
			configurationElement.appendChild(element);
		}
	}
}
