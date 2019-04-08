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
import org.w3c.dom.Element;

/**
 * A configuration for a media provider binding.
 * 
 * @author Lonnie Pryor
 */
public class MediaProviderBindingConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** The key the binding is under. */
	private String key = ""; //$NON-NLS-1$
	/** The ID of the media provider. */
	private String mediaProviderID = ""; //$NON-NLS-1$

	/**
	 * Creates a new MediaProviderBindingConfiguration.
	 */
	public MediaProviderBindingConfiguration()
	{
	}

	/**
	 * Returns the key the binding is under.
	 * 
	 * @return The key the binding is under.
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * Sets the key the binding is under.
	 * 
	 * @param key The key the binding is under.
	 */
	public void setKey(String key)
	{
		this.key = key == null ? "" : key; //$NON-NLS-1$
	}

	/**
	 * Returns the ID of the media provider.
	 * 
	 * @return The ID of the media provider.
	 */
	public String getMediaProviderID()
	{
		return mediaProviderID;
	}

	/**
	 * Sets the ID of the media provider.
	 * 
	 * @param mediaProviderID The ID of the media provider.
	 */
	public void setMediaProviderID(String mediaProviderID)
	{
		this.mediaProviderID = mediaProviderID == null ? "" : mediaProviderID; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(
	 *      org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		key = configurationElement.getAttribute(NAME_KEY);
		mediaProviderID = configurationElement.getAttribute(NAME_MEDIA_PROVIDER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(
	 *      org.w3c.dom.Element)NAME_SHARED_CONTENT
	 */
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute(NAME_KEY, key);
		configurationElement.setAttribute(NAME_MEDIA_PROVIDER, mediaProviderID);
	}
}
