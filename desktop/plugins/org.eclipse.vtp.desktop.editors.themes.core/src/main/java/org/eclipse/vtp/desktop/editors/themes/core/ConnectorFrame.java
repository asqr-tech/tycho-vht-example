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

import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;

/**
 * Defines the basic functions available from themed frames that represent
 * design connectors.
 * 
 * @author trip
 */
public interface ConnectorFrame extends ComponentFrame
{
	/**
	 * @return The design connector rendered by this frame
	 */
	public IDesignConnector getDesignConnector();
	
	/**
	 * If no mid point is present at the given x,y location, a new mid point is
	 * created on the design connector rendered by this frame.  If a mid point
	 * already exists at the location, the mid point is removed from the design
	 * connector.
	 * 
	 * @param x The x coordinate for the mid point
	 * @param y The y coordinate for the mid point
	 */
	public void toggleMidPoint(int x, int y);
}
