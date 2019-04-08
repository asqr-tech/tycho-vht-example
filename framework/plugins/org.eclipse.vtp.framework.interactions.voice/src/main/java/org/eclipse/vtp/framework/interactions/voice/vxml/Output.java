/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods),
 *    T.D. Barnes (OpenMethods) - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.voice.vxml;

import org.eclipse.vtp.framework.interactions.core.support.Widget;


/**
 * The <code>Output</code> class is the base class for the family of
 * VXML elements that can be audibly rendered to the caller.  This
 * includes TTS and audio files.
 *
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public abstract class Output extends Widget implements VXMLConstants
{
	/**
	 * Creates a new instance of <code>Output</code>.
	 */
	protected Output()
	{
	}
}
