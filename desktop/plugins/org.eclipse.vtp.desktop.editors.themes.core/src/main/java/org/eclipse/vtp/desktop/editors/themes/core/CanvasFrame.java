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

import org.eclipse.swt.graphics.Rectangle;

/**
 * Represents the a themed frame for rendering the border area of a design
 * canvas.
 * 
 * @author trip
 */
public interface CanvasFrame extends ThematicFrame
{
	/**
	 * @return A Rectangle that indicates the amount of space on each side of
	 * the frame that is needed to render the frame border
	 */
	public Rectangle getInsets();
	
	/**
	 * Sets the boundaries of the canvas frame
	 * @param rect a Rectangle defining the bounds of the canvas frame
	 */
	public void setBounds(Rectangle rect);

}
