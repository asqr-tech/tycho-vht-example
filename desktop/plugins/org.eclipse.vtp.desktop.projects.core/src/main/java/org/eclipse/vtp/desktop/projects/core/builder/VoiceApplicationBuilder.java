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
package org.eclipse.vtp.desktop.projects.core.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Map;

/**
 * This project builder is responsible for performing additional build
 * steps for OpenVXML voice application projects.  Currently, this
 * builder doesn't perform any actions, but exists to facilitate the
 * addition of such steps as they become needed.
 * 
 * Note: This builder and it's associated project nature have been deprecated.
 * They remain in this plug-in for now to support triggering conversion actions
 * in the navigator context menu.  They will be removed in the next major update
 * to the system or earlier if another trigger mechanism is constructed.
 *
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 * @deprecated
 */
public class VoiceApplicationBuilder extends IncrementalProjectBuilder
{
	/**
	 * Constant string containing the builder id
	 */
	public static final String BUILDER_ID =
		"org.eclipse.vtp.desktop.projects.core.VoiceApplicationBuilder";

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("rawtypes")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
		throws CoreException
	{
		if(kind == FULL_BUILD)
		{
			fullBuild(monitor);
		}
		else
		{
			IResourceDelta delta = getDelta(getProject());

			if(delta == null)
			{
				fullBuild(monitor);
			}
			else
			{
				incrementalBuild(delta, monitor);
			}
		}

		return null;
	}

	/**
	 * Performs all tasks required by a full build of the application project.
	 *
	 * @param monitor The progress monitor used to provide user feedback
	 * @throws CoreException If the build encounters an error during execution
	 */
	protected void fullBuild(final IProgressMonitor monitor)
		throws CoreException
	{
		try
		{
			getProject().accept(new ApplicationResourceVisitor());
		}
		catch(CoreException e)
		{
		}
	}

	/**
	 * Performs any build tasks required by the resource delta of the application project.
	 *
	 * @param delta The changes to the application project
	 * @param monitor The progress monitor used to provide user feedback
	 * @throws CoreException If the build encounters an error during execution
	 */
	protected void incrementalBuild(IResourceDelta delta,
		IProgressMonitor monitor) throws CoreException
	{
		delta.accept(new ApplicationDeltaVisitor());
	}

	/**
	 * This delta visitor is currently a NOOP.  Any resource delta analysis
	 * needed by future incarnations of this builder will be performed here.
	 */
	private class ApplicationDeltaVisitor implements IResourceDeltaVisitor
	{
		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException
		{
			switch(delta.getKind())
			{
				case IResourceDelta.ADDED:
					break;

				case IResourceDelta.REMOVED:
					break;

				case IResourceDelta.CHANGED:
					break;
			}

			// return true to continue visiting children.
			return true;
		}
	}

	/**
	 * This resource visitor is currently a NOOP.  Any resource analysis needed
	 * by future incarnations of this builder will be performed here.
	 */
	private class ApplicationResourceVisitor implements IResourceVisitor
	{
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
		 */
		public boolean visit(IResource resource)
		{
			return true;
		}
	}
}
