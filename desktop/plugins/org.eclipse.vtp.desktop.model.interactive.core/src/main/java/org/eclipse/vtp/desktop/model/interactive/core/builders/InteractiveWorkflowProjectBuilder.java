/**
 * 
 */
package org.eclipse.vtp.desktop.model.interactive.core.builders;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author trip
 *
 */
public class InteractiveWorkflowProjectBuilder extends IncrementalProjectBuilder
{
	/**
	 * Constant string containing the builder id
	 */
	public static final String BUILDER_ID =
		"org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowProjectBuilder";

	/**
	 * 
	 */
	public InteractiveWorkflowProjectBuilder()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
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
		getProject().accept(new ResourceVisitor());
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
	delta.accept(new DeltaVisitor());
}

/**
 * This delta visitor is currently a NOOP.  Any resource delta analysis
 * needed by future incarnations of this builder will be performed here.
 */
private class DeltaVisitor implements IResourceDeltaVisitor
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
private class ResourceVisitor implements IResourceVisitor
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
