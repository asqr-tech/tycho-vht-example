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

import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;

/**
 * This interface provides the basic functions that each theme has to implement.
 * 
 * @author trip
 */
public interface Theme
{
	/**	Indicates that frame borders should not be rendered */
	public static final int RENDER_FLAG_NO_BORDER = 1;
	/**	Indicates that frame selection states should not be rendered */
	public static final int RENDER_FLAG_NO_SELECTION = 2;
	/**	Indicates that frame icons should not be rendered */
	public static final int RENDER_FLAG_NO_ICONS = 4;
	/**	Indicates that frame informational markers should not be rendered */
	public static final int RENDER_FLAG_NO_MARKERS = 8;
	/**	Indicates that the render request is for printing purposes */
	public static final int RENDER_FLAG_PRINTING = 16;
	
	/**
	 * @return The unique identifier for this theme
	 */
	public String getId();
	
	/**
	 * @return The human readable name for this theme
	 */
	public String getName();
	
	/**
	 * Requests that a canvas frame for the given canvas be created by this
	 * theme.
	 * 
	 * @param canvas The canvas to be themed
	 * @return The canvas frame created
	 */
	public CanvasFrame createCanvasFrame(IDesign canvas);

	/**
	 * Requests that an element frame for the given element be created by this
	 * theme.
	 * 
	 * @param element The element to be themed
	 * @return The element frame created
	 */
	public ElementFrame createElementFrame(IDesignElement element);

	/**
	 * Requests that a connector frame for the given connector be created that
	 * connects the given element frames together.
	 * 
	 * @param source The origin element frame for the connector
	 * @param destination The destination element frame for the connector
	 * @param connector The connector to be themed
	 * @return The connector frame created
	 */
	public ConnectorFrame createConnectorFrame(ElementFrame source, ElementFrame destination, IDesignConnector connector);
}
