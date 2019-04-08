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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * ExternalReferenceConfiguration.
 * 
 * @author Lonnie Pryor
 */
public class ExternalReferenceConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** Comment for name. */
	private String name = ""; //$NON-NLS-1$
	/** Comment for url. */
	private String url = ""; //$NON-NLS-1$
	/** Comment for outputs. */
	private final Map inputs = new HashMap();
	/** Comment for outputs. */
	private final Map outputs = new HashMap();
	private final Map urlParameters = new HashMap();

	/**
	 * Creates a new ExternalReferenceConfiguration.
	 */
	public ExternalReferenceConfiguration()
	{
	}

	/**
	 * Returns the name.
	 * 
	 * @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the url.
	 * 
	 * @return The url.
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name == null ? "" : name;
	}

	/**
	 * Sets the url.
	 * 
	 * @param url The url to set.
	 */
	public void setUrl(String url)
	{
		this.url = url == null ? "" : url;
	}

	/**
	 * getInputNames.
	 * 
	 * @return
	 */
	public String[] getInputNames()
	{
		return (String[])inputs.keySet().toArray(new String[inputs.size()]);
	}

	/**
	 * isInputVariable.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isInputVariable(String name)
	{
		Input input = (Input)inputs.get(name);
		return input != null && input.variable;
	}

	/**
	 * getInputValue.
	 * 
	 * @param name
	 * @return
	 */
	public String getInputValue(String name)
	{
		Input input = (Input)inputs.get(name);
		return input == null ? null : input.value;
	}

	/**
	 * setInput.
	 * 
	 * @param name
	 * @param variable
	 * @param value
	 */
	public void setInput(String name, boolean variable, String value)
	{
		inputs.put(name, new Input(variable, value));
	}

	/**
	 * removeInput.
	 * 
	 * @param name
	 */
	public void removeInput(String name)
	{
		inputs.remove(name);
	}

	public String[] getURLParameterNames()
	{
		return (String[])urlParameters.keySet().toArray(new String[urlParameters.size()]);
	}

	/**
	 * isInputVariable.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isURLParameterVariable(String name)
	{
		URLParameter input = (URLParameter)urlParameters.get(name);
		return input != null && input.variable;
	}

	/**
	 * getInputValue.
	 * 
	 * @param name
	 * @return
	 */
	public String getURLParameterValue(String name)
	{
		URLParameter input = (URLParameter)urlParameters.get(name);
		return input == null ? null : input.value;
	}

	/**
	 * setInput.
	 * 
	 * @param name
	 * @param variable
	 * @param value
	 */
	public void setURLParameter(String name, boolean variable, String value)
	{
		urlParameters.put(name, new URLParameter(variable, value));
	}

	/**
	 * removeInput.
	 * 
	 * @param name
	 */
	public void removeURLParameter(String name)
	{
		urlParameters.remove(name);
	}

	/**
	 * getOutputNames.
	 * 
	 * @return
	 */
	public String[] getOutputNames()
	{
		return (String[])outputs.keySet().toArray(new String[outputs.size()]);
	}

	/**
	 * getOutputValue.
	 * 
	 * @param name
	 * @return
	 */
	public String getOutputValue(String name)
	{
		return (String)outputs.get(name);
	}

	/**
	 * setOutput.
	 * 
	 * @param name
	 * @param value
	 */
	public void setOutput(String name, String value)
	{
		outputs.put(name, value);
	}

	/**
	 * removeOutput.
	 * 
	 * @param name
	 */
	public void removeOutput(String name)
	{
		outputs.remove(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		name = configurationElement.getAttribute(NAME_NAME);
		url = configurationElement.getAttribute(NAME_VALUE);
		inputs.clear();
		NodeList itemElements = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, NAME_INPUT_ITEM);
		for (int i = 0; i < itemElements.getLength(); ++i)
		{
			Element itemElement = (Element)itemElements.item(i);
			String key = itemElement.getAttribute(NAME_KEY);
			String type = itemElement.getAttribute(NAME_TYPE);
			String value = itemElement.getAttribute(NAME_VALUE);
			inputs.put(key, new Input("variable".equals(type), value)); //$NON-NLS-1$
		}
		urlParameters.clear();
		itemElements = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, NAME_URL_PARAMETER_ITEM);
		for (int i = 0; i < itemElements.getLength(); ++i)
		{
			Element itemElement = (Element)itemElements.item(i);
			String key = itemElement.getAttribute(NAME_KEY);
			String type = itemElement.getAttribute(NAME_TYPE);
			String value = itemElement.getAttribute(NAME_VALUE);
			urlParameters.put(key, new URLParameter("variable".equals(type), value)); //$NON-NLS-1$
		}
		outputs.clear();
		itemElements = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_OUTPUT_ITEM);
		for (int i = 0; i < itemElements.getLength(); ++i)
		{
			Element itemElement = (Element)itemElements.item(i);
			String key = itemElement.getAttribute(NAME_KEY);
			String value = itemElement.getAttribute(NAME_VALUE);
			outputs.put(key, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute(NAME_NAME, name);
		configurationElement.setAttribute(NAME_VALUE, url);
		String inputItemName = NAME_INPUT_ITEM;
		String outputItemName = NAME_OUTPUT_ITEM;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
		{
			inputItemName = prefix + ":" + inputItemName; //$NON-NLS-1$
			outputItemName = prefix + ":" + outputItemName; //$NON-NLS-1$
		}
		for (Iterator i = inputs.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, inputItemName);
			element.setAttribute(NAME_KEY, (String)entry.getKey());
			Input input = (Input)entry.getValue();
			element.setAttribute(NAME_TYPE, input.variable ? "variable" : "static"); //$NON-NLS-1$ //$NON-NLS-2$
			element.setAttribute(NAME_VALUE, input.value);
			configurationElement.appendChild(element);
		}
		for (Iterator i = urlParameters.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, NAME_URL_PARAMETER_ITEM);
			element.setAttribute(NAME_KEY, (String)entry.getKey());
			URLParameter input = (URLParameter)entry.getValue();
			element.setAttribute(NAME_TYPE, input.variable ? "variable" : "static"); //$NON-NLS-1$ //$NON-NLS-2$
			element.setAttribute(NAME_VALUE, input.value);
			configurationElement.appendChild(element);
		}
		for (Iterator i = outputs.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, outputItemName);
			element.setAttribute(NAME_KEY, (String)entry.getKey());
			element.setAttribute(NAME_VALUE, (String)entry.getValue());
			configurationElement.appendChild(element);
		}
	}

	/**
	 * Input.
	 * 
	 * @author Lonnie Pryor
	 */
	private static final class Input
	{
		/** Comment for variable. */
		final boolean variable;
		/** Comment for value. */
		final String value;

		/**
		 * Creates a new Input.
		 * 
		 * @param variable
		 * @param value
		 */
		Input(boolean variable, String value)
		{
			this.variable = variable;
			this.value = value;
		}
	}
	
	private static final class URLParameter
	{
		/** Comment for variable. */
		final boolean variable;
		/** Comment for value. */
		final String value;

		/**
		 * Creates a new Input.
		 * 
		 * @param variable
		 * @param value
		 */
		URLParameter(boolean variable, String value)
		{
			this.variable = variable;
			this.value = value;
		}
	}

}
