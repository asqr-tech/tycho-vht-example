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
package org.eclipse.vtp.framework.common.configurations;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;

/**
 * A single of branded configuration for executing a script block.
 * 
 * @author Lonnie Pryor
 */
public class ScriptConfiguration implements IConfiguration, CommonConstants
{
	/** The scripting language the script is in. */
	private String scriptingLanguage = ""; //$NON-NLS-1$
	/** The script to run. */
	private String script = ""; //$NON-NLS-1$
	private boolean secured = false;

	/**
	 * Creates a new ScriptItemConfiguration.
	 */
	public ScriptConfiguration()
	{
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
		this.scriptingLanguage = scriptingLanguage == null ? "" //$NON-NLS-1$
				: scriptingLanguage;
	}

	/**
	 * Sets the script to run.
	 * 
	 * @param script The script to run.
	 */
	public void setScript(String script)
	{
		this.script = script == null ? "" //$NON-NLS-1$
				: script;
	}
	
	public boolean isSecured()
	{
		return secured;
	}
	
	public void setSecured(boolean secured)
	{
		this.secured = secured;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		this.scriptingLanguage = configurationElement
				.getAttribute(NAME_SCRIPTING_LANGUGAGE);
		secured = Boolean.parseBoolean(configurationElement.getAttribute(NAME_SECURED));
		this.script = XMLUtilities.getElementTextDataNoEx(configurationElement, true);
		if(script == null)
			script = "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute(NAME_SCRIPTING_LANGUGAGE,
				scriptingLanguage);
		configurationElement.appendChild(configurationElement.getOwnerDocument()
				.createTextNode(script));
		configurationElement.setAttribute(NAME_SECURED, Boolean.toString(secured));
	}
}
