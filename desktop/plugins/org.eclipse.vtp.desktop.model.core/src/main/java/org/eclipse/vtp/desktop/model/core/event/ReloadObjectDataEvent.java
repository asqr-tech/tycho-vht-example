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

import org.eclipse.vtp.desktop.model.core.internal.event.ObjectEvent;

/**
 * This class represents an event that indicates that an object's
 * data may have been changed by directly accessing the storage
 * locations for that information.  The object should reinitialize
 * it's members with the most current information from storage.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class ReloadObjectDataEvent extends ObjectEvent
{
	/**
	 * Creates a new reload data event.
	 *
	 * @param objectId The unique identifier of the object whose
	 * data may have been changed
	 */
	public ReloadObjectDataEvent(String objectId)
	{
		super(objectId);
	}
}
