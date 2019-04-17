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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;

/**
 * This interface represents a themed graphical frame for a design element.  It
 * identifies the basic functions that an element frame must support.
 * 
 * @author trip
 */
public interface ElementFrame extends ComponentFrame
{
	/**
	 * Moves the element on the canvas such that the specified edge is aligned
	 * with the specified x or y coordinate
	 * 
	 * @param edge - the edge to align (<code>SWT.LEFT</code>,
	 * <code>SWT.TOP</code>, <code>SWT.RIGHT</code>, <code>SWT.BOTTOM)
	 * @param xy - the x or y coordinate to which to align
	 */
	public void alignEdge(int edge, int xy);
	
	/**
	 * Moves the element on the canvas such that the center is aligned with the
	 * specified x or y coordinate
	 * 
	 * @param orientation - the orientation of the alignment of blocks
	 * (<code>SWT.HORIZONTAL</code> moves the blocks along the y axis,
	 * <code>SWT.VERTICAL</code> moves the blocks along the x axis)
	 * @param xy - the x or y coordinate to which to align
	 */
	public void alignCenter(int orientation, int xy);

	/**
	 * Sets the size of this element frame to the given width and height.
	 * 
	 * @param width The new width of the element frame
	 * @param height The new height of the element frame
	 */
	public void setSize(int width, int height);

	/**
	 * @return The design element themed by this element frame
	 */
	public IDesignElement getDesignElement();
	
	/**
	 * Determines whether the specified Rectangle intersects this frame
	 * 
	 * @param rect - the Rectangle to be checked against this frame for
	 * intersection
	 * @return <code>true</code> if the Rectangle intersects this frame;
	 * otherwise <code>false</code>
	 */
	public boolean touchesElement(Rectangle rect);

	/**
	 * Determines the point of intersection of the borders of this frame and the
	 * line defined by the center point of the frame in combination the point
	 * defined by the specified coordinates
	 * 
	 * @param x - the x coordinate
	 * @param y - the y coordinate
	 * @return the Point of intersection
	 */
	public Point getIncursionPoint(int x, int y);
}
