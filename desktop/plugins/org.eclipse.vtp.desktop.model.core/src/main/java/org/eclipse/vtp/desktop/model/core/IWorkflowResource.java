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
package org.eclipse.vtp.desktop.model.core;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.vtp.desktop.model.core.event.IRefreshListener;

/**
 * This is the base interface for the OpenVXML project object
 * model.  It provides the baseline functions for all resource
 * objects.  It also provides a simple change notification
 * event model.
 *
 * @author Trip Gilman
 * @version 1.0
 *
 * @since 4.0
 */
public interface IWorkflowResource extends IAdaptable
{
	/**
	 * @return The name of the resource
	 */
	public String getName();

	/**
	 * Returns the parent of this resource.  If this is a project
	 * resource this function returns <code>this</code>.
	 *
	 * @return The parent of this resource resource
	 */
	public IWorkflowResource getParent();

	/**
	 * Adds the provided refresh listener to the list of listeners
	 * that will be notified if this resource should be refreshed.
	 *
	 * @param listener The listener to add
	 */
	public void addRefreshListener(IRefreshListener listener);

	/**
	 * Removes the provided refresh listener from the list of listeners
	 * that will be notified of refresh events.
	 *
	 * @param listener The listener to remove
	 */
	public void removeRefreshListener(IRefreshListener listener);

	/**
	 * Causes all refresh events for this resource to be ignored
	 * until resumeEvents() is called.
	 */
	public void deferEvents();

	/**
	 * Causes refresh events to once again be propagated to this
	 * resources listeners after deferEvents() has been called.
	 * This will automatically generate a refresh event to
	 * ensure any changes to this resource while events were
	 * deferred are propagated to its listeners.
	 */
	public void resumeEvents();

	/**
	 * Causes a refresh event to be propagated to all listeners for this
	 * resource.
	 */
	public void refresh();

	/**
	 * Returns the top level project object that contains this resource.
	 *
	 * @return The containing project
	 */
	public IWorkflowProject getProject();
	
}
