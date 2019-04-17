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
package org.eclipse.vtp.desktop.model.interactive.core.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;

/**
 * This is a concrete implementation of <code>IVoiceToolsResource</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public abstract class MediaObject implements IMediaObject
{
	/**
	 * Creates a new <code>MediaObject</code>.
	 */
	public MediaObject()
	{
		super();
	}

	public IMediaProject getProject()
	{
		return getParent().getProject();
	}

	/**
	 * The object id is used by the event system to uniquely
	 * identify a project resource.  In this fashion, multiple
	 * references to the resource can be created and still be
	 * notified of events.
	 *
	 * @return A unique identifier for this resource
	 */
	protected abstract String getObjectId();

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
