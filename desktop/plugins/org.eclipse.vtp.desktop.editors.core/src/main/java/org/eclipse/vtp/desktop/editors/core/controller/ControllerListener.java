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
package org.eclipse.vtp.desktop.editors.core.controller;

import org.eclipse.vtp.desktop.editors.core.model.SelectionStructure;

public interface ControllerListener
{
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param inProgress
	 */
	public void graphicUpdate(int x, int y, int width, int height, boolean inProgress);
	
	/**
	 * @param selection
	 */
	public void selectionChanged(SelectionStructure selection);
}
