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
package org.eclipse.vtp.framework.core;

import org.w3c.dom.Element;

/**
 * Definition of configuration objects capable of being loaded and stored in an
 * XML document.
 * 
 * @author Lonnie Pryor
 */
public interface IConfiguration
{
	/**
	 * Loads the configuration information from the specified XML element.
	 *
	 * @param configurationElement The element to load the configuration from.
	 */
	void load(Element configurationElement);

	/**
	 * Saves the configuration information into the supplied XML element.
	 *
	 * @param configurationElement The element to save the configuration to.
	 */
	void save(Element configurationElement);
}
