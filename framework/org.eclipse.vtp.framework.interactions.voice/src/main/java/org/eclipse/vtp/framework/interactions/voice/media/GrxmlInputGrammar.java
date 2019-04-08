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

import org.eclipse.vtp.framework.interactions.core.media.FileInputGrammar;
import org.w3c.dom.Element;

/**
 * @author lonnie
 * 
 */
public class GrxmlInputGrammar extends FileInputGrammar
{

	public static final String ELEMENT_NAME = "grxml-input-grammar"; //$NON-NLS-1$

	public GrxmlInputGrammar()
	{
		super();
	}

	public GrxmlInputGrammar(Element element)
	{
		super(element);
	}

	public String getFileTypeName()
	{
		return "GRXML"; //$NON-NLS-1$
	}

	public Element store(Element element)
	{
		Element thisElement = element.getOwnerDocument().createElementNS(
				"http://www.eclipse.org/vtp/media/voice/input", ELEMENT_NAME); //$NON-NLS-1$
		element.appendChild(thisElement);
		super.storeBaseInfo(thisElement);
		return thisElement;
	}

	public String toString()
	{
		return "FILE [" + this.getPath() + "]";
	}

	public String getInputGrammarType()
    {
	    return "org.eclipse.vtp.framework.interactions.voice.media.input.grxml";
    }
}
