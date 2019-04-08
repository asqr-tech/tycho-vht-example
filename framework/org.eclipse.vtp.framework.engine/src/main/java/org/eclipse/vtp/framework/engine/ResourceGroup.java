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
package org.eclipse.vtp.framework.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.vtp.framework.interactions.core.media.IResourceManager;
import org.osgi.framework.Bundle;

/**
 * A group of public resources.
 * 
 * @author Lonnie Pryor
 */
public class ResourceGroup implements IResourceManager
{
	/** The bundle to load from. */
	private final Bundle bundle;
	/** The base path to publish. */
	private final String path;
	private final HashSet<String> index = new HashSet<String>();

	/**
	 * Creates a new ResourceGroup.
	 * 
	 * @param bundle The bundle to load from.
	 * @param path The base path to publish.
	 */
	public ResourceGroup(Bundle bundle, String path)
	{
		this.bundle = bundle;
		if (!path.startsWith("/")) //$NON-NLS-1$
			path = "/" + path; //$NON-NLS-2$
		if (!path.endsWith("/")) //$NON-NLS-1$
			path = path + "/"; //$NON-NLS-2$
		this.path = path;
		URL indexURL = bundle.getResource("files.index");
		if(indexURL != null)
		{
			try
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(indexURL.openConnection().getInputStream()));
				String line = br.readLine();
				while(line != null)
				{
					index.add(line);
					line = br.readLine();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the requested resource.
	 * 
	 * @param fullResourcePath The path of the resource to return.
	 * @return The requested resource.
	 */
	public URL getResource(String fullResourcePath)
	{
		System.out.println("resolving resource: " + path + fullResourcePath);
		URL ret = bundle.getEntry(path + fullResourcePath);
//		System.out.println("location: " + ret);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.IResourceManager#
	 *      listResources(java.lang.String)
	 */
	public String[] listResources(String fullDirectoryPath)
	{
		LinkedList list = new LinkedList();
		for (Enumeration e = bundle.getEntryPaths(path + fullDirectoryPath); e != null
				&& e.hasMoreElements();)
			list.add(e.nextElement());
		return (String[])list.toArray(new String[list.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.IResourceManager#
	 *      isDirectoryResource(java.lang.String)
	 */
	public boolean isDirectoryResource(String fullDirectoryPath)
	{
		return fullDirectoryPath.endsWith("/"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.IResourceManager#
	 *      isFileResource(java.lang.String)
	 */
	public boolean isFileResource(String fullFilePath)
	{
		return !isDirectoryResource(fullFilePath)
		&& (index.contains(fullFilePath) || getResource(fullFilePath) != null);
	}
}
