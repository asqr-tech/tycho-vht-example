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

import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.FileContent;
import org.w3c.dom.Element;

public class AudioContent extends FileContent
{
	public static final String ELEMENT_NAME = "audio-content"; //$NON-NLS-1$

	public AudioContent()
	{
	}

	public AudioContent(Element element)
	{
		super(element);
	}

	public String getFileTypeName()
	{
		return "AUDIO"; //$NON-NLS-1$
	}

	public Element store(Element element)
	{
		Element thisElement = element.getOwnerDocument().createElementNS(
				"http://www.eclipse.org/vtp/media/voice/content", ELEMENT_NAME); //$NON-NLS-1$
		element.appendChild(thisElement);
		super.storeBaseInfo(thisElement);
		return thisElement;
	}

	public String getContentType()
	{
		return "org.eclipse.vtp.framework.interactions.voice.media.content.audio"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.Content#createCopy()
	 */
	public Content createCopy()
	{
		AudioContent copy = new AudioContent();
		if (getPathType() == STATIC_PATH)
			copy.setStaticPath(getPath());
		else
			copy.setVariablePath(getPath());
		return copy;
	}
}
