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

/**
 * <code>BasicOutput</code> is an abstract class that represents textual
 * outputs to be rendered by TTS. Subclasses of <code>
 * BasicOutput</code> can
 * be contained by all VXML elements that play sound to the caller.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public abstract class BasicOutput extends Output
{
	/**
	 * Creates a new instance of <code>BasicOutput</code>.
	 */
	protected BasicOutput()
	{
	}
}
