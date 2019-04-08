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

import org.eclipse.vtp.framework.interactions.core.media.InlineInputGrammar;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;

/**
 * @author lonnie
 * 
 */
public class TextInputGrammar extends InlineInputGrammar
{
	public static final String ELEMENT_NAME = "text-input-grammar"; //$NON-NLS-1$
	
	private String text = null;

	public TextInputGrammar()
	{
		super();
	}
	
	public TextInputGrammar(Element element)
	{
		super();
		text = XMLUtilities.getElementTextDataNoEx(element, true);
		if(text == null)
			text = "";
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public String getGrammarText()
	{
		return text;
	}

	public Element store(Element element)
	{
		Element thisElement = element.getOwnerDocument().createElementNS(
				"http://www.eclipse.org/vtp/media/voice/input", ELEMENT_NAME); //$NON-NLS-1$
		element.appendChild(thisElement);
		thisElement.setTextContent(text);
		return thisElement;
	}

	public String toString()
	{
		return "SIMPLE INLINE GRAMMAR";
	}

	public String getInputGrammarType()
    {
	    return "org.eclipse.vtp.framework.interactions.voice.media.input.text";
    }
}
