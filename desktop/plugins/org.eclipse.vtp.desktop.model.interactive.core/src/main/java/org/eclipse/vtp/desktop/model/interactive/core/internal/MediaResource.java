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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFolder;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObjectContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaResource;

/**
 * This is a concrete implementation of <code>IMediaResource</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public abstract class MediaResource extends MediaObject
	implements IMediaResource
{
	/**
	 * The eclipse file resource this media resource represents.
	 */
	IResource resource;

	/**
	 * The parent media container.
	 */
	IMediaContainer container;

	/**
	 * Craetes a new <code>MediaResource</code> in the provided media
	 * container with the given eclipse file resource.
	 *
	 * @param container The parent media container
	 * @param resource The eclipse file resource this media resource
	 * represnets
	 */
	public MediaResource(IMediaContainer container, IResource resource)
	{
		super();
		this.resource = resource;
		this.container = container;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IMediaResource#exists()
	 */
	public boolean exists()
	{
		return resource.exists();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IMediaResource#getParentMediaContainer()
	 */
	public IMediaContainer getParentMediaContainer()
	{
		return container;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getParent()
	 */
	public IMediaObjectContainer getParent()
	{
		return container;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getName()
	 */
	public String getName()
	{
		return resource.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.VoiceResource#getObjectId()
	 */
	protected String getObjectId()
	{
		return resource.getFullPath().toPortableString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IMediaResource#delete()
	 */
	public void delete() throws CoreException
	{
		resource.delete(false, null);
	}

/*        public String getLabel(Object o)
   {
           return resource.getName();
   }
   public Object getParent(Object o)
   {
           return getParent();
   }
 */

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IMediaResource#getMediaPath()
	 */
	public String getMediaPath()
	{
		String path = "";
		Object obj = getParent();

		if(obj instanceof IMediaFolder)
		{
			path = ((IMediaFolder)obj).getMediaPath();
		}

		path += ("/" + getName());

		return path;
	}
}
