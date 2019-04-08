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

import org.eclipse.vtp.framework.core.IConfiguration;
import org.eclipse.vtp.framework.interactions.core.media.IContentFactory;
import org.eclipse.vtp.framework.interactions.core.media.IInputGrammarFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Configuration for an data request interaction.
 * 
 * @author Lonnie Pryor
 */
public class DataRequestConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** The content factory to use. */
	private final IContentFactory contentFactory;
	/** The input factory to use. */
	private final IInputGrammarFactory inputFactory;
	/** The name of the output item to play. */
	private String outputName = ""; //$NON-NLS-1$
	private String inputName = "";
	/** The name of the data collected by this request. */
	private String dataName = ""; //$NON-NLS-1$
	private boolean secured = false;
	/** Index of property configurations by name. */
	private MediaConfiguration mediaConfiguration = null;

	/**
	 * Creates a new DataRequestConfiguration.
	 * 
	 * @param contentFactory The content factory to use.
	 */
	public DataRequestConfiguration(IContentFactory contentFactory, IInputGrammarFactory inputFactory)
	{
		this.contentFactory = contentFactory;
		this.inputFactory = inputFactory;
	}

	/**
	 * Returns the name of the output item to play.
	 * 
	 * @return The name of the output item to play.
	 */
	public String getOutputName()
	{
		return outputName;
	}

	/**
	 * Sets the name of the output item to play.
	 * 
	 * @param outputName The name of the output item to play.
	 */
	public void setOutputName(String outputName)
	{
		this.outputName = outputName == null ? "" : outputName; //$NON-NLS-1$
	}
	
	public String getInputName()
	{
		return inputName;
	}
	
	public void setInputName(String inputName)
	{
		this.inputName = inputName == null	? "" : inputName;
	}

	/**
	 * Returns the name of the data collected by this request.
	 * 
	 * @return The name of the data collected by this request.
	 */
	public String getDataName()
	{
		return dataName;
	}

	/**
	 * Sets the name of the data collected by this request.
	 * 
	 * @param dataName The name of the data collected by this request.
	 */
	public void setDataName(String dataName)
	{
		this.dataName = dataName == null ? "" : dataName; //$NON-NLS-1$
	}
	
	public boolean isSecured()
	{
		return secured;
	}
	
	public void setSecured(boolean secured)
	{
		this.secured = secured;
	}

	/**
	 * Returns the media configuration for this message or <code>null</code> if
	 * no such configuration is registered.
	 * 
	 * @return The media configuration for this message or <code>null</code> if
	 *         no such configuration is registered.
	 */
	public MediaConfiguration getMediaConfiguration()
	{
		return mediaConfiguration;
	}

	/**
	 * Sets the media configuration for this message.
	 * 
	 * @param mediaConfiguration The media configuration for this message or
	 *          <code>null</code> to remove the configuration.
	 */
	public void setMediaConfiguration(MediaConfiguration mediaConfiguration)
	{
		this.mediaConfiguration = mediaConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		outputName = configurationElement.getAttribute(NAME_OUTPUT_NAME);
		inputName = configurationElement.getAttribute(NAME_INPUT_NAME);
		dataName = configurationElement.getAttribute(NAME_DATA_NAME);
		secured = Boolean.parseBoolean(configurationElement.getAttribute(NAME_SECURED));
		NodeList elements = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, NAME_MEDIA);
		mediaConfiguration = null;
		if (elements.getLength() == 0)
			mediaConfiguration = null;
		else
		{
			mediaConfiguration = new MediaConfiguration(contentFactory, inputFactory);
			mediaConfiguration.load((Element)elements.item(0));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute(NAME_OUTPUT_NAME, outputName);
		configurationElement.setAttribute(NAME_INPUT_NAME, inputName);
		configurationElement.setAttribute(NAME_DATA_NAME, dataName);
		configurationElement.setAttribute(NAME_SECURED, Boolean.toString(secured));
		if (mediaConfiguration == null)
			return;
		String mediaName = NAME_MEDIA;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
			mediaName = prefix + ":" + mediaName; //$NON-NLS-1$
		Element element = configurationElement.getOwnerDocument().createElementNS(
				NAMESPACE_URI, mediaName);
		mediaConfiguration.save(element);
		configurationElement.appendChild(element);
	}
}
