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
 * A configuration for a language.
 * 
 * @author Lonnie Pryor
 */
public class LanguageConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** The ID of this language. */
	private String id = ""; //$NON-NLS-1$
	/** The id of the interaction type this language is associated with */
	private String interactionType = "";

	/**
	 * Creates a new LanguageConfiguration.
	 */
	public LanguageConfiguration()
	{
	}

	/**
	 * Returns the ID of this language.
	 * 
	 * @return The ID of this language.
	 */
	public String getID()
	{
		return id;
	}

	/**
	 * Sets the ID of this language.
	 * 
	 * @param id The ID of this language.
	 */
	public void setID(String id)
	{
		this.id = id == null ? "" : id; //$NON-NLS-1$
	}

	/**
	 * Returns the ID of the interaction type this language is associated with.
	 * 
	 * @return The interaction type id
	 */
	public String getInteractionType()
	{
		return interactionType;
	}

	/**
	 * Associates this language with the given interaction type
	 * 
	 * @param interactionType The ID of the interaction type
	 */
	public void setInteractionType(String interactionType)
	{
		this.interactionType = interactionType == null ? "" : interactionType; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(
	 *      org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		id = configurationElement.getAttribute(NAME_ID);
		interactionType = configurationElement.getAttribute("interaction-type");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(
	 *      org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute(NAME_ID, id);
		configurationElement.setAttribute("interaction-type", interactionType);
	}
}
