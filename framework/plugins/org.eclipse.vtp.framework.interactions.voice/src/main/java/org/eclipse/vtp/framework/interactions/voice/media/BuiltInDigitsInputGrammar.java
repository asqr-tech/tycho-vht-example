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
public class BuiltInDigitsInputGrammar extends BuiltInInputGrammar
{
	public static final String ELEMENT_NAME = "builtin-digits-input-grammar"; //$NON-NLS-1$
	public static final String DATA_IDENTIFIER = "VXML:Builtin:digits"; //$NON-NLS-1$
	
	private String minDigits = "1";
	private String maxDigits = "6";

	public BuiltInDigitsInputGrammar()
	{
		super();
	}

	public BuiltInDigitsInputGrammar(Element element)
	{
		super();
		if(!element.getAttribute("min-digits").equals(""))
		{
			minDigits = element.getAttribute("min-digits");
		}
		if(!element.getAttribute("max-digits").equals(""))
		{
			maxDigits = element.getAttribute("max-digits");
		}
	}
	
	public String getMinDigits()
	{
		return minDigits;
	}
	
	public void setMinDigits(String minDigits)
	{
		this.minDigits = minDigits;
	}
	
	public String getMaxDigits()
	{
		return maxDigits;
	}
	
	public void setMaxDigits(String maxDigits)
	{
		this.maxDigits = maxDigits;
	}

	public Element store(Element element)
	{
		Element thisElement = element.getOwnerDocument().createElementNS(
				"http://www.eclipse.org/vtp/media/voice/input", ELEMENT_NAME); //$NON-NLS-1$
		element.appendChild(thisElement);
		thisElement.setAttribute("min-digits", minDigits);
		thisElement.setAttribute("max-digits", maxDigits);
		return thisElement;
	}

	public String getInputGrammarType()
    {
	    return "org.eclipse.vtp.framework.interactions.voice.media.input.builtin.digits";
    }
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.CustomInputGrammar#getCustomInputData()
	 */
	public String getBuiltInInputURI()
	{
		return DATA_IDENTIFIER + "?minlength=" + minDigits +";maxlength=" + maxDigits;
	}

	public String toString()
	{
		return "BuiltIn:Digits [minimum digits=" + this.minDigits + ", maximum digits=" + this.maxDigits + "]";
	}
}
