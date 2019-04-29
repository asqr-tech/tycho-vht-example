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
 * Indicates that a particular design element should be revealed in an editor or
 * a new editor should be created to display the design canvas which contains
 * the desired element.
 * 
 * @author trip
 */
public class LocateElement extends Command
{
	/**	The unique identifier of the element to display */
	private String elementId;
	
	/**
	 * Creates a new instance of this command with the given element identifier.
	 * 
	 * @param elementId The identifier of the element to display
	 */
	public LocateElement(String elementId)
	{
		super();
		this.elementId = elementId;
	}
	
	/**
	 * @return The identifier of the element to display
	 */
	public String getElementId()
	{
		return elementId;
	}
}
