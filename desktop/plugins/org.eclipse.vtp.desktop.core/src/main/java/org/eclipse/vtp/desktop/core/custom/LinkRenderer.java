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
package org.eclipse.vtp.desktop.core.custom;

import org.eclipse.swt.graphics.GC;

/**
 * This interface represents a drawing facility that can
 * render the contents of a text link to a display.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public interface LinkRenderer
{
	/**
	 * Requests the contents of the given text link be rendered
	 * to the provided graphical context.
	 *
	 * @param link The text link to render
	 * @param graphicalContext The graphical context of the display
	 * to be rendered on
	 */
	public void render(TextLink link, GC graphicalContext);
}
