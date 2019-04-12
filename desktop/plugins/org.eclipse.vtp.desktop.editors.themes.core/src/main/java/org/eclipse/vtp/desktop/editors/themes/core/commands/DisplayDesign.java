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
package org.eclipse.vtp.desktop.editors.themes.core.commands;

/**
 * Indicates that a particular design canvas should be revealed in an editor or
 * a new editor should be created to display the contained design canvas.
 * 
 * @author trip
 */
public class DisplayDesign extends Command
{
	/**	The unique identifier for the design to display */
	private String designId;
	
	/**
	 * Creates a new <code>DisplayDesign</code> instance for the design canvas
	 * with the given identifier.
	 * 
	 * @param designId The identifier for the design to display
	 */
	public DisplayDesign(String designId)
	{
		super();
		this.designId = designId;
	}
	
	/**
	 * @return The identifier of the design to display
	 */
	public String getDesignId()
	{
		return designId;
	}
}
