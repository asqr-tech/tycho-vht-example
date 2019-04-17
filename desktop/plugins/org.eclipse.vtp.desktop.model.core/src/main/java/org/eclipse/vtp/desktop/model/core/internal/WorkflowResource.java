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
package org.eclipse.vtp.desktop.model.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IContributorResourceAdapter;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.event.IRefreshListener;
import org.eclipse.vtp.desktop.model.core.event.ObjectRefreshEvent;
import org.eclipse.vtp.desktop.model.core.internal.event.ObjectEvent;
import org.eclipse.vtp.desktop.model.core.internal.event.ObjectListener;

/**
 * This is a concrete implementation of <code>IVoiceToolsResource</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public abstract class WorkflowResource implements IWorkflowResource,
	ObjectListener, IContributorResourceAdapter
{
	/**
	 * The list of <code>IRefreshListener</code>s to be notified of
	 * refresh events.
	 */
	List<IRefreshListener> refreshListeners = new ArrayList<IRefreshListener>();

	/**
	 * Creates a new <code>WorkflowResource</code>.
	 */
	public WorkflowResource()
	{
		super();
	}

	public IWorkflowProject getProject()
	{
		return getParent().getProject();
	}

	/**
	 * Notifies the desktop core plugin to begin processing events for
	 * this object.
	 */
	protected void activateEvents()
	{
		WorkflowCore.getDefault()
						 .registerObjectListener(getObjectId(), this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable
	{
		WorkflowCore.getDefault()
						 .unregisterObjectListener(getObjectId(), this);
		super.finalize();
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
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#deferEvents()
	 */
	public void deferEvents()
	{
		WorkflowCore.getDefault().deferEvents(getObjectId());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#resumeEvents()
	 */
	public void resumeEvents()
	{
		WorkflowCore.getDefault().resumeEvents(getObjectId());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#addRefreshListener(org.eclipse.vtp.desktop.core.project.event.IRefreshListener)
	 */
	public void addRefreshListener(IRefreshListener l)
	{
		synchronized(refreshListeners)
		{
			refreshListeners.remove(l);
			refreshListeners.add(l);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#removeRefreshListener(org.eclipse.vtp.desktop.core.project.event.IRefreshListener)
	 */
	public void removeRefreshListener(IRefreshListener l)
	{
		synchronized(refreshListeners)
		{
			refreshListeners.remove(l);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.event.ObjectListener#processObjectEvent(org.eclipse.vtp.desktop.core.project.internals.event.ObjectEvent)
	 */
	public void processObjectEvent(ObjectEvent event)
	{
		if(event instanceof ObjectRefreshEvent)
		{
			synchronized(refreshListeners)
			{
				for(IRefreshListener l : refreshListeners)
				{
					l.refreshResource(this);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#refresh()
	 */
	public void refresh()
	{
		WorkflowCore.getDefault()
						 .postObjectEvent(new ObjectRefreshEvent(getObjectId()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		if(IContributorResourceAdapter.class.isAssignableFrom(adapter))
			return this;
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

    public IResource getAdaptedResource(IAdaptable adaptable)
    {
    	return (IResource)getAdapter(IResource.class);
    }
}
