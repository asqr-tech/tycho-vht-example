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

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFile;

/**
 * This is a concrete implementation of <code>IMediaFile</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class MediaFile extends MediaResource implements IMediaFile
{
	private static final String HASHPREFIX = "MEDIAFILE";
	/**
	 * The eclipse file resource represented by this media file.
	 */
	IFile file;

	/**
	 * Creates a new <code>MediaFile</code> in the given container
	 * with the provided eclipse file resource.
	 *
	 * @param container The parent media container
	 * @param file The eclipse file resource this media file represents
	 */
	public MediaFile(IMediaContainer container, IFile file)
	{
		super(container, file);
		this.file = file;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IMediaFile#open()
	 */
	public InputStream open() throws CoreException
	{
		return file.getContents();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IMediaFile#length()
	 */
	public long length() throws CoreException
	{
		return file.getLocation().toFile().length();
	}
	
	/**
	 * @return
	 */
	public IFile getUnderlyingFile()
	{
		return file;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass)
	{
		if(adapterClass.isAssignableFrom(IFile.class))
			return file;
		return super.getAdapter(adapterClass);
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof MediaFile)
		{
			return file.equals(((MediaFile)obj).getUnderlyingFile());
		}
		return false;
	}
	
	public int hashCode()
	{
		return (HASHPREFIX + file.toString()).hashCode();
	}
}
