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
package org.eclipse.vtp.desktop.model.interactive.voice;

import org.eclipse.vtp.desktop.model.interactive.core.IMediaFile;


/**
 * This interface represents an audio file resource of either an
 * application or persona project.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public interface IAudioFile extends IMediaFile
{
	/**
	 * @return The alternate text associated with this audio file resource
	 */
	public String getAlternateText();

	/**
	 * Sets the alternate text associated with this audio file to the given
	 * value.
	 *
	 * @param alternateText The new alternate text
	 */
	public void setAlternateText(String alternateText);
}
