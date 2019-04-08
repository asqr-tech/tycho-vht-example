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
package org.eclipse.vtp.framework.interactions.voice.media;

import org.eclipse.vtp.framework.interactions.core.media.BuiltInInputGrammar;
import org.w3c.dom.Element;

/**
 * @author lonnie
 * 
 */
public class BuiltInBooleanInputGrammar extends BuiltInInputGrammar
{
	public static final String ELEMENT_NAME = "builtin-boolean-input-grammar"; //$NON-NLS-1$
	public static final String DATA_IDENTIFIER = "VXML:Builtin:boolean"; //$NON-NLS-1$
	
	
	private String yesDigit = "1";
	private String noDigit = "2";

	public BuiltInBooleanInputGrammar()
	{
		super();
	}

	public BuiltInBooleanInputGrammar(Element element)
	{
		super(element);
		if(!element.getAttribute("yes-digit").equals(""))
		{
			yesDigit = element.getAttribute("yes-digit");
		}
		if(!element.getAttribute("no-digit").equals(""))
		{
			noDigit = element.getAttribute("no-digit");
		}
	}
	
	public String getYesDigit()
	{
		return yesDigit;
	}
	
	public void setYesDigit(String yesDigit)
	{
		this.yesDigit = yesDigit;
	}
	
	public String getNoDigit()
	{
		return noDigit;
	}
	
	public void setNoDigit(String noDigit)
	{
		this.noDigit = noDigit;
	}

	public Element store(Element element)
	{
		Element thisElement = element.getOwnerDocument().createElementNS(
				"http://www.eclipse.org/vtp/media/voice/input", ELEMENT_NAME); //$NON-NLS-1$
		element.appendChild(thisElement);
		thisElement.setAttribute("yes-digit", yesDigit);
		thisElement.setAttribute("no-digit", noDigit);
		return thisElement;
	}

	public String getInputGrammarType()
    {
	    return "org.eclipse.vtp.framework.interactions.voice.media.input.builtin.boolean";
    }

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.CustomInputGrammar#getCustomInputData()
	 */
	public String getBuiltInInputURI()
	{
		return DATA_IDENTIFIER + "?y=" + yesDigit +";n=" + noDigit;
	}

	public String toString()
	{
		return "BuiltIn:Boolean [yes key=" + this.yesDigit + ", no key=" + this.noDigit + "]";
	}
}
