/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.interactive.core.configuration.generic;

import org.w3c.dom.Element;

/**
 * The <code>BindingItem</code> interface defines the common functions each
 * bind item implementation must provide.
 * 
 * @author trip
 */
public interface BindingItem extends Cloneable
{
	/**
	 * Returns a unique identifier that specifies the type of binding item this
	 * is.  The identifiers usually follow the java package nameing convensions.
	 * 
	 * @return Identifier of this binding item's type
	 */
	public String getType();
	
	/**
	 * Loads this binding item's data from the provided dom element.
	 * 
	 * @param configuration The dom element contianing this item's data
	 */
	public void readConfiguration(Element configuration);
	
	/**
	 * Indicates this item should store its data into the provided dom element.
	 * 
	 * @param configuration The dom element to hold this item's data
	 */
	public void writeConfiguration(Element configuration);
	
	/**
	 * Creates a copy of this binding item.  Implementers should decide and then
	 * document whether this is a deep or shallow copy.
	 * 
	 * @return The copy of this binding item
	 */
	public Object clone();
}
