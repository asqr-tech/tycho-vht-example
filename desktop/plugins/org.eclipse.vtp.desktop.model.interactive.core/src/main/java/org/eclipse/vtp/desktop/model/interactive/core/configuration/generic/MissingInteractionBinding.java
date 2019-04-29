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

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class represents an interaction binding that is associated with an
 * interaction type that is not supported by the current OpenVXML installation.
 * The original dom element that contains the configuration data is saved and
 * re-written to the call design to preserve the data.
 * 
 * @author trip
 */
public class MissingInteractionBinding
{
	/**	The original dom element that contains the binding information */
	private Element sourceElement = null;
	
	/**
	 * Creates a new placeholder for the interaction binding with the
	 * configuration data stored in the given dom element.
	 * 
	 * @param sourceElement The original dom element
	 */
	public MissingInteractionBinding(Element sourceElement)
	{
		super();
		this.sourceElement = sourceElement;
	}

	/**
	 * Stores the original configuration data into the given dom element.  All
	 * data will be preserved as it was when read in.
	 * 
	 * @param parent The parent element to hold the configuration data
	 */
	public void writeConfiguration(Element parent)
	{
		try
        {
	        Node copy = parent.getOwnerDocument().importNode(sourceElement, true);
	        parent.appendChild(copy);
        }
        catch(DOMException e)
        {
	        e.printStackTrace();
        }
	}
}
