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
 * Configuration for a choice in a selection request interaction.
 * 
 * @author Lonnie Pryor
 */
public class SelectionChoiceConfiguration implements IConfiguration,
		InteractionsConstants
{
	/** The content factory to use. */
	private final IContentFactory contentFactory;
	/** The input factory to use. */
	private final IInputGrammarFactory inputFactory;
	/** The name of this choice. */
	private String name = ""; //$NON-NLS-1$
	/** The name of the output item to play. */
	private String outputName = ""; //$NON-NLS-1$
	/** The name of the input item to collect. */
	private String inputName = ""; //$NON-NLS-1$
	/** The scripting language the script is in. */
	private String scriptingLanguage = null;
	/** The script to run. */
	private String script = null;
	/** Index of property configurations by name. */
	private MediaConfiguration mediaConfiguration = null;

	/**
	 * Creates a new SelectionChoiceConfiguration.
	 * 
	 * @param contentFactory The content factory to use.
	 * @param inputFactory The input factory to use.
	 */
	public SelectionChoiceConfiguration(IContentFactory contentFactory,
			IInputGrammarFactory inputFactory)
	{
		this.contentFactory = contentFactory;
		this.inputFactory = inputFactory;
	}

	/**
	 * Returns the name of this choice.
	 * 
	 * @return The name of this choice.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this choice.
	 * 
	 * @param name The name of this choice.
	 */
	public void setName(String name)
	{
		this.name = name == null ? "" : name; //$NON-NLS-1$
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
	 * Returns the name of the input item to collect.
	 * 
	 * @return The name of the input item to collect.
	 */
	public String getInputName()
	{
		return inputName;
	}

	/**
	 * Sets the name of the input item to collect.
	 * 
	 * @param inputName The name of the input item to collect.
	 */
	public void setInputName(String inputName)
	{
		this.inputName = inputName == null ? "" : inputName; //$NON-NLS-1$
	}

	/**
	 * Returns the scripting language the script is in.
	 * 
	 * @return The scripting language the script is in.
	 */
	public String getScriptingLanguage()
	{
		return scriptingLanguage;
	}

	/**
	 * Returns the script to run.
	 * 
	 * @return The script to run.
	 */
	public String getScript()
	{
		return script;
	}

	/**
	 * Sets the scripting language the script is in.
	 * 
	 * @param scriptingLanguage The scripting language the script is in.
	 */
	public void setScriptingLanguage(String scriptingLanguage)
	{
		this.scriptingLanguage = scriptingLanguage;
	}

	/**
	 * Sets the script to run.
	 * 
	 * @param script The script to run.
	 */
	public void setScript(String script)
	{
		this.script = script;
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
		name = configurationElement.getAttribute(NAME_NAME);
		outputName = configurationElement.getAttribute(NAME_OUTPUT_NAME);
		inputName = configurationElement.getAttribute(NAME_INPUT_NAME);
		if (configurationElement.hasAttribute(NAME_SCRIPTING_LANGUGAGE))
			this.scriptingLanguage = configurationElement
					.getAttribute(NAME_SCRIPTING_LANGUGAGE);
		else
			this.scriptingLanguage = null;
		if (configurationElement.hasAttribute(NAME_SCRIPT))
			this.script = configurationElement.getAttribute(NAME_SCRIPT);
		else
			this.script = null;
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
		configurationElement.setAttribute(NAME_NAME, name);
		configurationElement.setAttribute(NAME_OUTPUT_NAME, outputName);
		configurationElement.setAttribute(NAME_INPUT_NAME, inputName);
		if (scriptingLanguage != null)
			configurationElement.setAttribute(NAME_SCRIPTING_LANGUGAGE,
					scriptingLanguage);
		if (script != null)
			configurationElement.setAttribute(NAME_SCRIPT, script);
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
