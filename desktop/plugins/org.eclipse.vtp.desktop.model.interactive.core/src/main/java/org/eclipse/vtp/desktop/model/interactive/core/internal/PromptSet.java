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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObjectContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;
import org.eclipse.vtp.desktop.model.interactive.core.IPromptSet;

/**
 * This is a concrete implementation of <code>IPromptSet</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class PromptSet extends MediaObject implements IPromptSet
{
	private static final String HASHPREFIX = "PROMPTSET";
	/**
	 * The eclipse file resource that contains the prompt
	 * definitions for a persona project or the global
	 * prompts of a brand.
	 */
	IFile file;

	/**
	 * The parent resource of this prompt set.
	 */
	IMediaProject parent;

	/**
	 * Creates a new <code>PromptSet</code> in the provided parent
	 * resource with the given eclipse file resource.
	 *
	 * @param file The eclipse file resource that contains the
	 * prompt definitions
	 * @param parent The parent resource
	 */
	public PromptSet(IFile file, IMediaProject parent)
	{
		super();
		this.file = file;
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getName()
	 */
	public String getName()
	{
		return file.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IPromptSet#read()
	 */
	public InputStream read() throws CoreException
	{
		return file.getContents();
	}

	public IMediaObjectContainer getParent()
	{
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IPromptSet#write(java.io.InputStream, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void write(InputStream source, IProgressMonitor monitor)
		throws CoreException
	{
		file.setContents(source, true, true, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.VoiceResource#getObjectId()
	 */
	protected String getObjectId()
	{
		return file.getFullPath().toPortableString();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.projects.core.IPromptSet#getUnderlyingFile()
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
		if(obj instanceof PromptSet)
		{
			return file.equals(((PromptSet)obj).getUnderlyingFile());
		}
		return false;
	}
	
	public int hashCode()
	{
		return (HASHPREFIX + file.toString()).hashCode();
	}
}
