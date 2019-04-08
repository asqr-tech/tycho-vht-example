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
package org.eclipse.vtp.framework.util;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A customized {@link ServiceTracker} that tracks only the single most
 * desirable instance of a service.
 * 
 * @author Lonnie Pryor
 * @version 1.0
 * @since 3.0
 */
@SuppressWarnings("rawtypes")
public class SingletonTracker extends ServiceTracker implements
		SingletonTrackerCustomizer
{
	/** A comparator that sorts service references by ID. */
	private static final Comparator SERVICE_SORT_BY_ID = new Comparator()
	{
		public int compare(Object leftService, Object rightService)
		{
			long difference = ((Long)((ServiceReference)leftService)
					.getProperty(Constants.SERVICE_ID)).longValue()
					- ((Long)((ServiceReference)rightService)
							.getProperty(Constants.SERVICE_ID)).longValue();
			return difference == 0 ? 0 : (int)(difference / Math.abs(difference));
		}
	};

	/** The customizer to inform of changes to the singleton. */
	private final SingletonTrackerCustomizer customizer;
	/** The candidate references for selection as a singleton. */
	@SuppressWarnings("unchecked")
	private final SortedMap<ServiceReference, Boolean> candidates = new TreeMap<ServiceReference, Boolean>(SERVICE_SORT_BY_ID);
	/** True if a thread is currently selecting a singleton. */
	private boolean selecting = false;
	/** The currently selected singleton reference. */
	private volatile ServiceReference selectedReference = null;
	/** The currently selected singleton object. */
	private volatile Object selectedService = null;

	/*
	 * Constructors.
	 */

	/**
	 * Creates a new SingletonTracker.
	 * 
	 * @param context The context to operate under.
	 * @param reference The reference to the service to track.
	 * @param customizer The customizer for this tracker or <code>null</code> to
	 *          use this tracker as the customizer also.
	 */
	@SuppressWarnings("unchecked")
	public SingletonTracker(BundleContext context, ServiceReference reference,
			SingletonTrackerCustomizer customizer)
	{
		super(context, reference, null);
		this.customizer = customizer == null ? this : customizer;
	}

	/**
	 * Creates a new SingletonTracker.
	 * 
	 * @param context The context to operate under.
	 * @param clazz The type of service to track.
	 * @param customizer The customizer for this tracker or <code>null</code> to
	 *          use this tracker as the customizer also.
	 */
	@SuppressWarnings("unchecked")
	public SingletonTracker(BundleContext context, String clazz,
			SingletonTrackerCustomizer customizer)
	{
		super(context, clazz, null);
		this.customizer = customizer == null ? this : customizer;
	}

	/**
	 * Creates a new SingletonTracker.
	 * 
	 * @param context The context to operate under.
	 * @param filter A filter that identifies the services to track.
	 * @param customizer The customizer for this tracker or <code>null</code> to
	 *          use this tracker as the customizer also.
	 */
	@SuppressWarnings("unchecked")
	public SingletonTracker(BundleContext context, Filter filter,
			SingletonTrackerCustomizer customizer)
	{
		super(context, filter, null);
		this.customizer = customizer == null ? this : customizer;
	}

	/**
	 * Called when a method on the customizer fails to complete normally. By
	 * default the stack trace is printed and the throwable is ignored.
	 * 
	 * @param t The throwable that was caught.
	 */
	protected void throwableCaught(Throwable t)
	{
		t.printStackTrace();
	}

	/*
	 * Selection accessors.
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTracker#getServiceReference()
	 */
	public ServiceReference getServiceReference()
	{
		return selectedReference;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTracker#getService()
	 */
	public Object getService()
	{
		return selectedService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTracker#getServiceReferences()
	 */
	public ServiceReference[] getServiceReferences()
	{
		synchronized (candidates)
		{
			return selectedReference == null ? null
					: new ServiceReference[] { selectedReference };
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTracker#getService(
	 *      org.osgi.framework.ServiceReference)
	 */
	public Object getService(ServiceReference ref)
	{
		synchronized (candidates)
		{
			return ref != null && ref.equals(selectedReference) ? selectedService
					: null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTracker#getServices()
	 */
	public Object[] getServices()
	{
		synchronized (candidates)
		{
			return selectedService == null ? null : new Object[] { selectedService };
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTracker#size()
	 */
	public int size()
	{
		synchronized (candidates)
		{
			return selectedService == null ? 0 : 1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTracker#waitForService(long)
	 */
	public Object waitForService(long timeout) throws InterruptedException
	{
		if (timeout < 0)
			throw new IllegalArgumentException("timeout"); //$NON-NLS-1$
		synchronized (candidates)
		{
			do
			{
				if (selectedService == null)
					candidates.wait(timeout);
				else
					break;
			}
			while (timeout == 0);
			return selectedService;
		}
	}

	/*
	 * ServiceTrackerCustomizer methods.
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTracker#addingService(
	 *      org.osgi.framework.ServiceReference)
	 */
	public final Object addingService(ServiceReference reference)
	{
		synchronized (candidates)
		{
			candidates.put(reference, Boolean.FALSE);
			if (selecting)
				return reference;
			selecting = true;
		}
		try
		{
			selectSingleton();
		}
		catch (Throwable t)
		{
			throwableCaught(t);
		}
		return reference;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTracker#modifiedService(
	 *      org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public final void modifiedService(ServiceReference reference, Object service)
	{
		synchronized (candidates)
		{
			if (!Boolean.FALSE.equals(candidates.get(reference)))
				return;
			candidates.put(reference, Boolean.TRUE);
			if (selecting)
				return;
			selecting = true;
		}
		try
		{
			selectSingleton();
		}
		catch (Throwable t)
		{
			throwableCaught(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTracker#removedService(
	 *      org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public final void removedService(ServiceReference reference, Object service)
	{
		synchronized (candidates)
		{
			if (candidates.remove(reference) == null)
				return;
			if (selecting)
				return;
			selecting = true;
		}
		try
		{
			selectSingleton();
		}
		catch (Throwable t)
		{
			throwableCaught(t);
		}
	}

	/*
	 * SingletonTrackerCustomizer default implementations.
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.kernel.util.SingletonTrackerCustomizer#
	 *      selectingService(org.osgi.framework.ServiceReference)
	 */
	@SuppressWarnings("unchecked")
	public Object selectingService(ServiceReference reference)
	{
		return context.getService(reference);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.kernel.util.SingletonTrackerCustomizer#
	 *      changingSelectedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object, org.osgi.framework.ServiceReference)
	 */
	@SuppressWarnings("unchecked")
	public Object changingSelectedService(ServiceReference oldReference,
			Object oldService, ServiceReference newReference)
	{
		Object newService = null;
		try
		{
			newService = context.getService(newReference);
		}
		finally
		{
			if (newService != null)
				context.ungetService(oldReference);
		}
		return newService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.kernel.util.SingletonTrackerCustomizer#
	 *      selectedServiceModified(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void selectedServiceModified(ServiceReference reference, Object service)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.kernel.util.SingletonTrackerCustomizer#
	 *      releasedSelectedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void releasedSelectedService(ServiceReference reference, Object service)
	{
		context.ungetService(reference);
	}

	/*
	 * Selection algorithm.
	 */

	private void selectSingleton()
	{
		RuntimeException re = null;
		while (true)
		{
			ServiceReference bestReference = null;
			int bestRanking = 0;
			boolean bestModified = false;
			synchronized (candidates)
			{
				for (Map.Entry<ServiceReference, Boolean> entry : candidates.entrySet())
				{
					ServiceReference reference = entry.getKey();
					Object rankingObj = reference.getProperty(Constants.SERVICE_RANKING);
					int ranking = rankingObj instanceof Integer ? ((Integer)rankingObj)
							.intValue() : 0;
					if (bestReference == null || ranking > bestRanking)
					{
						bestReference = reference;
						bestRanking = ranking;
						bestModified = Boolean.TRUE.equals(entry.getValue());
					}
					entry.setValue(Boolean.FALSE);
				}
				if (selectedReference == null && bestReference == null
						|| selectedReference != null
						&& selectedReference.equals(bestReference))
				{
					if (!bestModified)
					{
						selecting = false;
						if (re != null)
							throw re;
						return;
					}
				}
				else
					bestModified = false;
			}
			try
			{
				if (bestModified)
					customizer
							.selectedServiceModified(selectedReference, selectedService);
				else if (selectedReference == null)
				{
					Object service = null;
					try
					{
						service = customizer.selectingService(bestReference);
					}
					finally
					{
						synchronized (candidates)
						{
							if (service == null)
								candidates.remove(bestReference);
							else
							{
								selectedReference = bestReference;
								selectedService = service;
								candidates.notifyAll();
							}
						}
					}
				}
				else if (bestReference == null)
				{
					try
					{
						customizer.releasedSelectedService(selectedReference,
								selectedService);
					}
					finally
					{
						synchronized (candidates)
						{
							selectedReference = null;
							selectedService = null;
						}
					}
				}
				else
				{
					Object service = null;
					try
					{
						service = customizer.changingSelectedService(selectedReference,
								selectedService, bestReference);
					}
					finally
					{
						synchronized (candidates)
						{
							if (service == null)
								candidates.remove(bestReference);
							else
							{
								selectedReference = bestReference;
								selectedService = service;
							}
						}
					}
				}
			}
			catch (RuntimeException e)
			{
				if (re == null)
					re = e;
			}
		}
	}
}
