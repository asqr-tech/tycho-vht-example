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
package org.eclipse.vtp.desktop.editors.themes.core;

import java.util.Map;

import org.eclipse.swt.graphics.GC;

/**
 * This interface represents the base functions provided by all themed frames.
 * 
 * @author trip
 */
public interface ThematicFrame
{
	/**
	 * Requests this frame render itself to the given graphics context.  The
	 * stage parameter indicates what phase of rendering is being performed.
	 * The renderFlags parameter provides special restrictions on how the
	 * rendering should be carried out.  The resourceMap parameter is an index
	 * and cache of graphical resources that are shared by all frames such as
	 * colors and fonts.
	 * 
	 * @param graphicsContext The graphics context to render to
	 * @param stage The phase of rendering to perform
	 * @param renderFlags Restrictions on what to render
	 * @param resourceMap Cache of re-usable graphical resources
	 */
	public void renderFrame(GC graphicsContext, int stage, int renderFlags, Map<String, Object> resourceMap);
}
