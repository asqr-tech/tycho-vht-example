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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.vtp.desktop.editors.themes.core.commands.CommandListener;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;

/**
 * This interface represents the basic functions that graphical frames that
 * render design components must support.
 * 
 * @author trip
 */
public interface ComponentFrame extends ThematicFrame
{
	public IDesignComponent getDesignComponent();
	
	/**
	 * Determines if the given coordinates "touch" this component frame.  Each
	 * theme defines what "touch" means.  It should generally mean the area
	 * rendered by an element frame or the area rendered by a connector frame or
	 * near by.
	 * 
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return <code>true</code> if the coordinate touches this frame,
	 * <code>false</code> otherwise
	 */
	public boolean touchesComponent(int x, int y);

	/**
	 * Notifies this component frame that the mouse has moved within its bounds.
	 * The x and y coordinates of the mouse cursor as well as any active
	 * keyboard modifiers are provided.  A command listener is also provided to
	 * receive any commands that may result from this event.
	 * 
	 * @param commandListener The command listener to deliver response commands
	 * to
	 * @param x The x coordinate of the mouse cursor
	 * @param y The y coordinate of the mouse cursor
	 * @param modifiers Any keyboard modifiers active during this event
	 */
	public void mouseMove(CommandListener commandListener, int x, int y, int modifiers);

	/**
	 * Notifies this component frame that a mouse button was pressed while the
	 * mouse cursor was within its bounds. The x and y coordinates of the mouse
	 * cursor as well as any active keyboard modifiers are provided.  A command
	 * listener is also provided to receive any commands that may result from
	 * this event.
	 * 
	 * @param commandListener The command listener to deliver response commands
	 * to
	 * @param x The x coordinate of the mouse cursor
	 * @param y The y coordinate of the mouse cursor
	 * @param modifiers Any keyboard modifiers active during this event
	 */
	public void mouseDown(CommandListener commandListener, int x, int y, int modifiers);

	/**
	 * Notifies this component frame that a mouse button was released while the
	 * mouse cursor was within its bounds. The x and y coordinates of the mouse
	 * cursor as well as any active keyboard modifiers are provided.  A command
	 * listener is also provided to receive any commands that may result from
	 * this event.
	 * 
	 * @param commandListener The command listener to deliver response commands
	 * to
	 * @param x The x coordinate of the mouse cursor
	 * @param y The y coordinate of the mouse cursor
	 * @param modifiers Any keyboard modifiers active during this event
	 */
	public void mouseUp(CommandListener commandListener, int x, int y, int modifiers);

	/**
	 * Notifies this component frame that a mouse button was double-clicked
	 * while the mouse cursor was within its bounds. The x and y coordinates of
	 * the mouse cursor as well as any active keyboard modifiers are provided.
	 * A command listener is also provided to receive any commands that may
	 * result from this event.
	 * 
	 * @param commandListener The command listener to deliver response commands
	 * to
	 * @param x The x coordinate of the mouse cursor
	 * @param y The y coordinate of the mouse cursor
	 * @param modifiers Any keyboard modifiers active during this event
	 */
	public void mouseDoubleClick(CommandListener commandListener, int x, int y, int modifiers);

	/**
	 * Sets the selected state of this component frame to the given value.
	 * 
	 * @param selected The new selected state of this component frame
	 */
	public void setSelected(boolean selected);

	/**
	 * Updates this component frame by moving it relative to its current
	 * position by the x and y offsets given.  This is pretty straight forward
	 * for element frames.  There is an additional requirement when called on
	 * a connector frame: all points must be updated simultaneously.  This
	 * covers all mid points and labels associated with the connector.
	 * 
	 * @param xoffset The distance to move this component frame on the X axis
	 * @param yoffset The distance to move this component frame on the Y axis
	 */
	public void adjustPosition(int xoffset, int yoffset);

	/**
	 * Returns the current rectangular bounds of this component frame.  The
	 * rectangle should be the smallest box that can contain all points rendered
	 * by this component frame.
	 * 
	 * @return The current rectangular bounds of this component frame
	 */
	public Rectangle getBounds();
	
	/**
	 * Registers the given listener with this component frame.  If the listener
	 * was already registered, it should be removed from the component frame and
	 * re-added at the end of the listener set.
	 * 
	 * @param listener The listener to register
	 */
	public void addListener(ComponentFrameListener listener);
	
	/**
	 * De-registers the given listener from this component frame.  If the
	 * listener was not already registered with this component frame, no action
	 * should be taken.
	 * 
	 * @param listener The listener to remove
	 */
	public void removeListener(ComponentFrameListener listener);
	
	/**
	 * This method is called so the component frame can perform any calculations
	 * it needs a graphics context for and also offers an opportunity to create
	 * and cache shared resources it will use later.
	 * 
	 * @param gc A graphics context to use in calculations
	 * @param resourceMap A cache of shared resources
	 */
	public void initializeGraphics(GC gc, Map<String, Object> resourceMap);
	
}
