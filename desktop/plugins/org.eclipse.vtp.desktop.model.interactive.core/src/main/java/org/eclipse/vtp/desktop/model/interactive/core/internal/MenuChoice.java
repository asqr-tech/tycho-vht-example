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
package org.eclipse.vtp.desktop.model.interactive.core.internal;


/**
 * @author Trip
 * @version 1.0
 */
public class MenuChoice
{
	String optionName;
	String scriptText;

	public MenuChoice(String optionName, String scriptText)
	{
		super();
		this.optionName = optionName;
		this.scriptText = scriptText;
	}

	/**
	 * @return Returns the optionName.
	 */
	public String getOptionName()
	{
		return optionName;
	}

	/**
	 * @param optionName The optionName to set.
	 */
	public void setOptionName(String optionName)
	{
		this.optionName = optionName;
	}

	public String getScriptText()
    {
    	return scriptText;
    }

	public void setScriptText(String scriptText)
    {
    	this.scriptText = scriptText;
    }

}
