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
 * This class represents and event that indicates that an object
 * has changed and any visual displays of its data should be updated.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class ObjectRefreshEvent extends ObjectEvent
{
	/**
	 * Creates a new object refresh event for the given object id.
	 *
	 * @param objectId An identifier of the object that has changed
	 */
	public ObjectRefreshEvent(String objectId)
	{
		super(objectId);
	}
}
