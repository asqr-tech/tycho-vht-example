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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.eclipse.vtp.framework.interactions.core.media.IContentFactory;
import org.eclipse.vtp.framework.interactions.core.media.IInputGrammarFactory;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Configuration for a selection request interaction.
 * 
 * @author Lonnie Pryor
 */
public class SelectionRequestConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** The content factory to use. */
	private final IContentFactory contentFactory;
	/** The input factory to use. */
	private final IInputGrammarFactory inputFactory;
	/** The name of the output item to play. */
	private String outputName = ""; //$NON-NLS-1$
	/** The name of the data collected by this request. */
	private String dataName = ""; //$NON-NLS-1$
	private boolean secured = false;
	/** Index of property configurations by name. */
	private MediaConfiguration mediaConfiguration = null;
	/** The choices in this interaction. */
	private final Set choices = new HashSet();
	/** The choice name lists to use by brand name. */
	private final Map brandedChoices = new HashMap();

	/**
	 * Creates a new SelectionRequestConfiguration.
	 * 
	 * @param contentFactory The content factory to use.
	 * @param inputFactory The input factory to use.
	 */
	public SelectionRequestConfiguration(IContentFactory contentFactory,
			IInputGrammarFactory inputFactory)
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
		return this.secured;
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

	/**
	 * Returns the choices in this interaction.
	 * 
	 * @return The choices in this interaction.
	 */
	public SelectionChoiceConfiguration[] getChoices()
	{
		return (SelectionChoiceConfiguration[])choices
				.toArray(new SelectionChoiceConfiguration[choices.size()]);
	}

	/**
	 * Adds a choice to this interaction.
	 * 
	 * @param choice The choice to add.
	 */
	public void addChoice(SelectionChoiceConfiguration choice)
	{
		if (choice != null)
			choices.add(choice);
	}

	/**
	 * Removes a choice from this interaction.
	 * 
	 * @param choice The choice to remove.
	 */
	public void removeChoice(SelectionChoiceConfiguration choice)
	{
		if (choice != null)
			choices.remove(choice);
	}

	/**
	 * Returns the choice names registered for the specified brand.
	 * 
	 * @param brandName The name of the brand to find the choice names for.
	 * @return The choice names registered for the specified brand.
	 */
	public String[] getBrandedChoices(String brandName)
	{
		return (String[])brandedChoices.get(brandName);
	}

	/**
	 * Sets the choice names registered for the specified brand.
	 * 
	 * @param brandName The name of the brand to set the choice names for.
	 * @param choiceNames The choice names to register for the specified brand.
	 */
	public void setBrandedChoices(String brandName, String[] choiceNames)
	{
		if (brandName == null)
			return;
		if (choiceNames == null)
			brandedChoices.remove(brandName);
		else
			brandedChoices.put(brandName, choiceNames);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		outputName = configurationElement.getAttribute(NAME_OUTPUT_NAME);
		dataName = configurationElement.getAttribute(NAME_DATA_NAME);
		secured = Boolean.parseBoolean(configurationElement.getAttribute(NAME_SECURED));
		NodeList elements = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, NAME_MEDIA);
		if (elements.getLength() == 0)
			mediaConfiguration = null;
		else
		{
			mediaConfiguration = new MediaConfiguration(contentFactory, inputFactory);
			mediaConfiguration.load((Element)elements.item(0));
		}
		choices.clear();
		elements = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_CHOICE);
		for (int i = 0; i < elements.getLength(); ++i)
		{
			SelectionChoiceConfiguration choice = new SelectionChoiceConfiguration(
					contentFactory, inputFactory);
			choice.load((Element)elements.item(i));
			choices.add(choice);
		}
		brandedChoices.clear();
		List choiceNames = new ArrayList();
		elements = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_CHOICES);
		for (int i = 0; i < elements.getLength(); ++i)
		{
			Element element = (Element)elements.item(i);
			String content = XMLUtilities.getElementTextDataNoEx(element, true);
			if (content != null)
				for (StringTokenizer st = new StringTokenizer(content, "\r\n"); //$NON-NLS-1$
				st.hasMoreTokens();)
					choiceNames.add(st.nextToken().trim());
			brandedChoices.put(element.getAttribute(NAME_KEY), (String[])choiceNames
					.toArray(new String[choiceNames.size()]));
			choiceNames.clear();
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
		configurationElement.setAttribute(NAME_DATA_NAME, dataName);
		configurationElement.setAttribute(NAME_SECURED, Boolean.toString(secured));
		String mediaName = NAME_MEDIA;
		String choiceName = NAME_CHOICE;
		String choicesName = NAME_CHOICES;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
		{
			mediaName = prefix + ":" + mediaName; //$NON-NLS-1$
			choiceName = prefix + ":" + choiceName; //$NON-NLS-1$
			choicesName = prefix + ":" + choicesName; //$NON-NLS-1$
		}
		if (mediaConfiguration != null)
		{
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, mediaName);
			mediaConfiguration.save(element);
			configurationElement.appendChild(element);
		}
		for (Iterator i = choices.iterator(); i.hasNext();)
		{
			SelectionChoiceConfiguration choice = (SelectionChoiceConfiguration)i
					.next();
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, choiceName);
			choice.save(element);
			configurationElement.appendChild(element);
		}
		for (Iterator i = brandedChoices.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			Element element = configurationElement.getOwnerDocument()
					.createElementNS(NAMESPACE_URI, choicesName);
			element.setAttribute(NAME_KEY, (String)entry.getKey());
			StringBuffer buffer = new StringBuffer();
			String[] choiceNames = (String[])entry.getValue();
			for (int j = 0; j < choiceNames.length; ++j)
			{
				if (choiceNames[j] == null)
					continue;
				if (buffer.length() > 0)
					buffer.append('\r').append('\n');
				buffer.append(choiceNames[j]);
			}
			element.appendChild(configurationElement.getOwnerDocument()
					.createTextNode(buffer.toString()));
			configurationElement.appendChild(element);
		}
	}
}
