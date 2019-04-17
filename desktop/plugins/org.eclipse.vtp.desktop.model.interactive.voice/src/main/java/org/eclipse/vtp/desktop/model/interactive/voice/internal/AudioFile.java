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
package org.eclipse.vtp.desktop.model.interactive.voice.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaContainer;
import org.eclipse.vtp.desktop.model.interactive.core.internal.MediaFile;
import org.eclipse.vtp.desktop.model.interactive.voice.IAudioFile;
import org.eclipse.vtp.desktop.model.interactive.voice.internal.helpers.AlternateTextManager;

/**
 * This is a concrete implementation of <code>IAudioFile</code> and
 * provides the default behavior of that interface.
 *
 * @author Trip Gilman
 *
 */
public class AudioFile extends MediaFile implements IAudioFile
{
	/**
	 * Creates a new <code>AudioFile</code> with the given parent container
	 * and eclipse file resource.
	 *
	 * @param container The parent container
	 * @param file The eclipse resource represented by this audio file
	 */
	public AudioFile(IMediaContainer container, IFile file)
	{
		super(container, file);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IAudioFile#getAlternateText()
	 */
	public String getAlternateText()
	{
		return AlternateTextManager.getInstance().getAlternateText(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IAudioFile#setAlternateText(java.lang.String)
	 */
	public void setAlternateText(String alternateText)
	{
		AlternateTextManager.getInstance().setAlternateText(this, alternateText);
	}
}
