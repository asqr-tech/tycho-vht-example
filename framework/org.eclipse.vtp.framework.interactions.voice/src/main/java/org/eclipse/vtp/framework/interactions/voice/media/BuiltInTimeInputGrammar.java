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
public class BuiltInTimeInputGrammar extends BuiltInInputGrammar
{
	public static final String ELEMENT_NAME = "builtin-time-input-grammar"; //$NON-NLS-1$
	public static final String DATA_IDENTIFIER = "VXML:Builtin:time"; //$NON-NLS-1$
	
	public BuiltInTimeInputGrammar()
	{
		super();
	}

	public BuiltInTimeInputGrammar(Element element)
	{
		super();
	}
	
	public Element store(Element element)
	{
		Element thisElement = element.getOwnerDocument().createElementNS(
				"http://www.eclipse.org/vtp/media/voice/input", ELEMENT_NAME); //$NON-NLS-1$
		element.appendChild(thisElement);
		return thisElement;
	}

	public String getInputGrammarType()
    {
	    return "org.eclipse.vtp.framework.interactions.voice.media.input.builtin.time";
    }
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.CustomInputGrammar#getCustomInputData()
	 */
	public String getBuiltInInputURI()
	{
		return DATA_IDENTIFIER;
	}

	public String toString()
	{
		return "BuiltIn:Time";
	}
}
