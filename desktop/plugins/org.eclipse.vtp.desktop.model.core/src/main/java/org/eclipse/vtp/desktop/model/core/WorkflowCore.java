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
package org.eclipse.vtp.desktop.model.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.vtp.desktop.model.core.event.ObjectRefreshEvent;
import org.eclipse.vtp.desktop.model.core.internal.WorkflowModel;
import org.eclipse.vtp.desktop.model.core.internal.event.ObjectEvent;
import org.eclipse.vtp.desktop.model.core.internal.event.ObjectListener;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class WorkflowCore extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.vtp.desktop.model.core";

	// The shared instance
	private static WorkflowCore plugin;
	
	private Map<String, List<WeakReference<ObjectListener>>> objectListeners = new HashMap<String, List<WeakReference<ObjectListener>>>();
	private Map<String, Object> deferredObjects = new HashMap<String, Object>();
	private List<ObjectEvent> eventBuffer = new LinkedList<ObjectEvent>();
	private boolean running = true;
	private IWorkflowModel model = null;

	/**
	 * The constructor
	 */
	public WorkflowCore()
	{
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		new Thread(new Runnable()
		{
			public void run()
			{
				while(running)
				{
					synchronized(eventBuffer)
					{
						if(!eventBuffer.isEmpty())
						{
							ObjectEvent oe = eventBuffer.remove(0);

							if(deferredObjects.get(oe.getObjectId()) == null)
							{
								List<WeakReference<ObjectListener>> ls =
									objectListeners.get(oe.getObjectId());

								if(ls != null)
								{
									Iterator<WeakReference<ObjectListener>> i =
										ls.iterator();
									while(i.hasNext())
									{
										WeakReference<ObjectListener> ref = i.next();
										if(ref.get() == null)
										{
											i.remove();
										}
										else
										{
											ref.get().processObjectEvent(oe);
										}
									}
								}
							}
						}
						else
						{
							try
							{
								eventBuffer.wait();
							}
							catch(Exception e)
							{
							}
						}
					}
				}
			}
		}).start();
		model = new WorkflowModel();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		running = false;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static WorkflowCore getDefault()
	{
		return plugin;
	}

	public IWorkflowModel getWorkflowModel()
	{
		return model;
	}

	/**
	 * @param objectId
	 * @param l
	 */
	public void registerObjectListener(String objectId, ObjectListener l)
	{
		synchronized(eventBuffer)
		{
			List<WeakReference<ObjectListener>> ls = objectListeners.get(objectId);

			if(ls == null)
			{
				ls = new ArrayList<WeakReference<ObjectListener>>();
				objectListeners.put(objectId, ls);
			}

			Iterator<WeakReference<ObjectListener>> i =
				ls.iterator();
			while(i.hasNext())
			{
				WeakReference<ObjectListener> ref = i.next();
				if(ref.get() == null)
				{
					i.remove();
				}
				else if(ref.get() == l)
				{
					i.remove();
				}
			}
			ls.add(new WeakReference<ObjectListener>(l));
		}
	}

	/**
	 * @param objectId
	 * @param l
	 */
	public void unregisterObjectListener(String objectId, ObjectListener l)
	{
		synchronized(eventBuffer)
		{
			List<WeakReference<ObjectListener>> ls = objectListeners.get(objectId);

			if(ls == null)
			{
				ls = new ArrayList<WeakReference<ObjectListener>>();
				objectListeners.put(objectId, ls);
			}

			Iterator<WeakReference<ObjectListener>> i =
				ls.iterator();
			while(i.hasNext())
			{
				WeakReference<ObjectListener> ref = i.next();
				if(ref.get() == null)
				{
					i.remove();
				}
				else if(ref.get() == l)
				{
					i.remove();
				}
			}
		}
	}

	/**
	 * @param event
	 */
	public void postObjectEvent(ObjectEvent event)
	{
		synchronized(eventBuffer)
		{
			eventBuffer.add(event);
			eventBuffer.notifyAll();
		}
	}

	/**
	 * @param objectId
	 */
	public void deferEvents(String objectId)
	{
		synchronized(eventBuffer)
		{
			deferredObjects.put(objectId, this);
		}
	}

	/**
	 * @param objectId
	 */
	public void resumeEvents(String objectId)
	{
		synchronized(eventBuffer)
		{
			deferredObjects.remove(objectId);
			postObjectEvent(new ObjectRefreshEvent(objectId));
		}
	}

	public String getTemplate(String name)
	{
		try
		{
			InputStream templateIn = this.getClass().getClassLoader().getResourceAsStream(name);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[10240];
			int len = templateIn.read(buf);
			while(len != -1)
			{
				baos.write(buf, 0, len);
				len = templateIn.read(buf);
			}
			templateIn.close();
			return baos.toString();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
