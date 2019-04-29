/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.modules.attacheddata.ui.configuration.request;

/**
 * The <code>GenericBindingItem</code> interface defines the common functions
 * each binding item implementation must provide.
 * 
 * @author trip
 */
public abstract class GenericBindingItem implements Cloneable
{
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public abstract Object clone();
}
