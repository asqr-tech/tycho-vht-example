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
package org.eclipse.vtp.desktop.model.core.event;

import org.eclipse.vtp.desktop.model.core.IWorkflowResource;

/**
 * This interface is used to notify listners when a resource should be
 * refreshed.  This is mainly used in conjunction with JFace widgets
 * so the data displayed in the viewers can be updated when a change
 * is made by some other entity.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public interface IRefreshListener
{
	/**
	 * Called when a resource has changed in some way and indicates any
	 * visual display of its data should be updated.
	 *
	 * @param resource The resource that changed
	 */
	public void refreshResource(IWorkflowResource resource);
}
