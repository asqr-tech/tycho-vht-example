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
package org.eclipse.vtp.desktop.core.custom;

/**
 * Event listener interface for the TextLinkViewer class.
 * 
 * @author Trip
 */
public interface LinkViewerSelectionListener
{
	/**
	 * Called when the current selection changes for the TextLinkViewer.
	 * 
	 * @param selection The new viewer selection
	 */
	public void selectionChanged(String selection);
}
