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
 * The <code>Action</code> class is an abstraction of
 * the group VXML elements that perform discrete operations
 * and have a common set of parent elements.
 * <br><br>
 * This class should be subclassed ONLY if the new element
 * can be contained by all of the following elements:
 * <ul>
 *                 <li><code>Block</code></li>
 *                 <li><code>If</code></li>
 *                 <li><code>EventHandler</code></li>
 * </ul>
 * To do otherwise, could cause the formation of an invalid
 * VXML document.
 *
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public abstract class Action extends Widget implements VXMLConstants
{
	/**
	 * Creates a new Action.
	 */
	protected Action()
	{
	}
}
