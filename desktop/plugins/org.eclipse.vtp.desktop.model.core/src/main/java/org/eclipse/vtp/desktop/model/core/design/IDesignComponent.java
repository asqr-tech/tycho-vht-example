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
package org.eclipse.vtp.desktop.model.core.design;

import java.beans.PropertyChangeListener;

public interface IDesignComponent
{
	public String getId();
	
	public IDesign getDesign();

	/**
	 * @param listener
	 */
	public void addPropertyListener(PropertyChangeListener listener);
	
	/**
	 * @param listener
	 */
	public void removePropertyListener(PropertyChangeListener listener);
	
	/**
	 * @param listener
	 */
	public void addListener(IDesignComponentListener listener);
	
	/**
	 * @param listener
	 */
	public void removeListener(IDesignComponentListener listener);
	
}
