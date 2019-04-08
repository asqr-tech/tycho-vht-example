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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Configuration for an output message interaction.
 * 
 * @author Lonnie Pryor
 */
public class OutputMessageConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** The content factory to use. */
	private final IContentFactory contentFactory;
	/** The name of the output item to play. */
	private String outputName = ""; //$NON-NLS-1$
	private boolean secured = false;
	/** Index of property configurations by name. */
	private MediaConfiguration mediaConfiguration = null;

	/**
	 * Creates a new OutputMessageConfiguration.
	 * 
	 * @param contentFactory The content factory to use.
	 */
	public OutputMessageConfiguration(IContentFactory contentFactory)
	{
		this.contentFactory = contentFactory;
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
		secured = Boolean.parseBoolean(configurationElement.getAttribute(NAME_SECURED));
		NodeList elements = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, NAME_MEDIA);
		mediaConfiguration = null;
		if (elements.getLength() == 0)
			mediaConfiguration = null;
		else
		{
			mediaConfiguration = new MediaConfiguration(contentFactory, null);
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
