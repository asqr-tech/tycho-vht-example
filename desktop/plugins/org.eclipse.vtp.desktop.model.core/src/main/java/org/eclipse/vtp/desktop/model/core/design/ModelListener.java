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
package org.eclipse.vtp.desktop.model.core.design;


public interface ModelListener
{
	/**
	 * @param model
	 * @param component
	 */
	public void componentAdded(IDesign model, IDesignComponent component);
	
	/**
	 * @param model
	 * @param component
	 */
	public void componentRemoved(IDesign model, IDesignComponent component);
	
	public void nameChanged(IDesign model);

	public void orientationChanged(IDesign model);
	
	public void paperSizeChanged(IDesign model);
}
