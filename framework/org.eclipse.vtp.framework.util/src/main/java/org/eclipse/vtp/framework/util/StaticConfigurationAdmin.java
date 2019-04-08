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

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.w3c.dom.Element;

/**
 * An immutable implementation of the configuration admin service.
 * 
 * @author Lonnie Pryor
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StaticConfigurationAdmin
{
	/**
	 * Creates a copy of the supplied dictionary.
	 * 
	 * @param input The dictionary to copy.
	 * @return The copy of the supplied dictionary.
	 */
	private static Dictionary copy(Dictionary input)
	{
		if (input == null)
			return null;
		Dictionary output = new Hashtable(input.size());
		for (Enumeration e = input.keys(); e.hasMoreElements();)
		{
			String key = (String)e.nextElement();
			output.put(key, input.get(key));
		}
		return output;
	}

	/**
	 * Returns the pid of the specified reference or <code>null</code> if it has
	 * no pid.
	 * 
	 * @param reference The reference to examine.
	 * @return The pid of the specified reference or <code>null</code> if it has
	 *         no pid.
	 */
	private static String pid(ServiceReference reference)
	{
		Object pid = reference.getProperty(Constants.SERVICE_PID);
		if (pid instanceof String)
			return (String)pid;
		return null;
	}

	/** The context to operate under. */
	private final BundleContext context;
	/** The log service to use. */
	private final LogService log;
	/** The tracker of all the managed service objects. */
	private final ConfigurationTargetTracker tracker;
	/** The object that dispatches configuration events asynchronously. */
	private volatile Dispatcher dispatcher = null;
	/** The non-factory configurations. */
	private final Map services;
	/** The factory configurations. */
	private final Map factories;
	/** The indexed of pids by the references that declare them. */
	private final Map pidsByReference = new HashMap();

	/**
	 * Creates a new StaticConfigurationAdmin.
	 * 
	 * @param context The context to operate under.
	 * @param log The log service to use.
	 * @param configurationsData The configuration data to load.
	 */
	public StaticConfigurationAdmin(BundleContext context, LogService log,
			Element configurationsData)
	{
		this.context = context;
		this.log = log;
		this.tracker = new ConfigurationTargetTracker(context);
		// Parse the XML configuration data.
		Map services = new HashMap();
		Map factories = new HashMap();
		ConfigurationDictionary[] dictionaries = ConfigurationDictionary
				.loadAll(configurationsData);
		for (int i = 0; i < dictionaries.length; ++i)
		{
			String pid = dictionaries[i].getPid();
			if (pid == null)
				continue;
			String factoryPid = dictionaries[i].getFactoryPid();
			if (factoryPid == null)
				services.put(pid, dictionaries[i]);
			else
			{
				Map factoryIndex = (Map)factories.get(factoryPid);
				if (factoryIndex == null)
					factories.put(factoryPid, factoryIndex = new HashMap());
				factoryIndex.put(pid, dictionaries[i]);
			}
		}
		this.services = Collections.unmodifiableMap(new HashMap(services));
		for (Iterator i = factories.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			Map factoryIndex = (Map)entry.getValue();
			entry.setValue(factoryIndex.values().toArray(
					new ConfigurationDictionary[factoryIndex.size()]));
		}
		this.factories = Collections.unmodifiableMap(new HashMap(factories));
	}

	/**
	 * Starts this service implementation.
	 */
	public synchronized void start()
	{
		if (dispatcher != null)
			return;
		dispatcher = new Dispatcher();
		boolean failed = true;
		try
		{
			tracker.open();
			dispatcher.start();
			failed = false;
		}
		finally
		{
			if (failed)
				stop();
		}
	}

	/**
	 * Stops this service implementation.
	 */
	public synchronized void stop()
	{
		if (dispatcher == null)
			return;
		try
		{
			dispatcher.stop();
		}
		finally
		{
			try
			{
				tracker.close();
			}
			finally
			{
				dispatcher = null;
				pidsByReference.clear();
			}
		}
	}

	/**
	 * Called on the dispatch thread after a service has been added.
	 * 
	 * @param reference The reference that was added.
	 */
	private void added(ServiceReference reference, boolean factory)
	{
		String pid = pid(reference);
		if (pid == null)
			return;
		pidsByReference.put(reference, pid);
		dispatch(reference, pid, factory);
	}

	/**
	 * Called on the dispatch thread after a service has been modified.
	 * 
	 * @param reference The reference that was modified.
	 */
	private void modified(ServiceReference reference, boolean factory)
	{
		String oldPid = (String)pidsByReference.get(reference);
		String newPid = pid(reference);
		if (oldPid == newPid)
			return;
		else if (newPid == null)
			pidsByReference.remove(reference);
		else if (newPid.equals(oldPid))
			return;
		else
		{
			pidsByReference.put(reference, newPid);
			dispatch(reference, newPid, factory);
		}
	}

	/**
	 * Called on the dispatch thread after a service has been removed.
	 * 
	 * @param reference The reference that was removed.
	 */
	private void removed(ServiceReference reference, boolean factory)
	{
		pidsByReference.remove(reference);
	}

	/**
	 * Configures the specified service.
	 * 
	 * @param reference The reference to the service to configure.
	 * @param pid The pid to configure the service with.
	 * @param factory Indicator of whether the configuration is provided by a
	 * factory.
	 */
	private void dispatch(ServiceReference reference, String pid, boolean factory)
	{
		Object instance = null;
		try
		{
			if (factory)
			{
				ConfigurationDictionary[] configs = (ConfigurationDictionary[])factories
						.get(pid);
				if (configs == null)
					return;
				instance = context.getService(reference);
				if (instance instanceof ManagedServiceFactory)
					configure(reference.getBundle().getLocation(),
							(ManagedServiceFactory)instance, configs);
			}
			else
			{
				ConfigurationDictionary config = (ConfigurationDictionary)services
						.get(pid);
				instance = context.getService(reference);
				if (instance instanceof ManagedService)
					configure(reference.getBundle().getLocation(),
							(ManagedService)instance, config);
			}
		}
		finally
		{
			if (instance != null)
				context.ungetService(reference);
		}
	}

	/**
	 * Configures a managed service.
	 * 
	 * @param bundleLocation The location of the bundle that registered the
	 *          service.
	 * @param service The service to configure.
	 * @param serviceConfiguration The configuration dictionary.
	 */
	private void configure(String bundleLocation, ManagedService service,
			ConfigurationDictionary serviceConfiguration)
	{
		if (serviceConfiguration != null)
		{
			if (serviceConfiguration.getBundleLocation() == null)
				serviceConfiguration.setBundleLocation(bundleLocation);
			else if (!serviceConfiguration.getBundleLocation().equals(bundleLocation))
				return;
		}
		Dictionary dictionary = copy(serviceConfiguration);
		if (dictionary != null)
			dictionary.remove(ConfigurationAdmin.SERVICE_BUNDLELOCATION);
		try
		{
			service.updated(dictionary);
		}
		catch (Exception e)
		{
			error(e);
		}
	}

	/**
	 * Configures a managed service factory.
	 * 
	 * @param bundleLocation The location of the bundle that registered the
	 *          service.
	 * @param serviceFactory The service factory to configure.
	 * @param serviceFactoryConfiguration The configuration dictionaries.
	 */
	private void configure(String bundleLocation,
			ManagedServiceFactory serviceFactory,
			ConfigurationDictionary[] serviceFactoryConfiguration)
	{
		for (int i = 0; i < serviceFactoryConfiguration.length; ++i)
		{
			if (serviceFactoryConfiguration[i].getBundleLocation() == null)
				serviceFactoryConfiguration[i].setBundleLocation(bundleLocation);
			else if (!serviceFactoryConfiguration[i].getBundleLocation().equals(
					bundleLocation))
				continue;
			Dictionary dictionary = copy(serviceFactoryConfiguration[i]);
			dictionary.remove(ConfigurationAdmin.SERVICE_BUNDLELOCATION);
			try
			{
				serviceFactory.updated(serviceFactoryConfiguration[i].getPid(),
						dictionary);
			}
			catch (Exception e)
			{
				error(e);
			}
		}
	}

	/**
	 * Logs an error to the log service.
	 * 
	 * @param e The error to log.
	 */
	private void error(Exception e)
	{
		log.log(LogService.LOG_ERROR, e.getMessage(), e);
	}

	/**
	 * A tracker for the configuration target services.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ConfigurationTargetTracker
	{
		/** The managed service tracker. */
		private final ServiceTracker serviceTracker;
		/** The managed service factory tracker. */
		private final ServiceTracker serviceFactoryTracker;

		/**
		 * Creates a new ConfigurationTargetTracker.
		 * 
		 * @param context The context to operate under.
		 */
		ConfigurationTargetTracker(BundleContext context)
		{
			serviceTracker = new ServiceTracker(context, ManagedService.class
					.getName(), null)
			{
				public Object addingService(ServiceReference reference)
				{
					doAddingService(reference, false);
					return reference;
				}

				public void modifiedService(ServiceReference reference, Object service)
				{
					doModifiedService(reference, false);
				}

				public void removedService(ServiceReference reference, Object service)
				{
					doRemovedService(reference, false);
				}
			};
			serviceFactoryTracker = new ServiceTracker(context,
					ManagedServiceFactory.class.getName(), null)
			{
				public Object addingService(ServiceReference reference)
				{
					doAddingService(reference, true);
					return reference;
				}

				public void modifiedService(ServiceReference reference, Object service)
				{
					doModifiedService(reference, true);
				}

				public void removedService(ServiceReference reference, Object service)
				{
					doRemovedService(reference, true);
				}
			};
		}

		/**
		 * Starts tracking configuration targets.
		 */
		void open()
		{
			serviceTracker.open();
			serviceFactoryTracker.open();
		}

		/**
		 * Stops tracking configuration targets.
		 */
		void close()
		{
			try
			{
				serviceFactoryTracker.close();
			}
			finally
			{
				serviceTracker.close();
			}
		}

		/**
		 * Schedules an added event.
		 * 
		 * @param reference The reference that caused the event.
		 * @param factory True if the reference is a managed service factory.
		 */
		private void doAddingService(final ServiceReference reference,
				final boolean factory)
		{
			Dispatcher dispatcher = StaticConfigurationAdmin.this.dispatcher;
			if (dispatcher != null)
				dispatcher.enqueue(new Runnable()
				{
					public void run()
					{
						added(reference, factory);
					}
				});
		}

		/**
		 * Schedules a modified event.
		 * 
		 * @param reference The reference that caused the event.
		 * @param factory True if the reference is a managed service factory.
		 */
		private void doModifiedService(final ServiceReference reference,
				final boolean factory)
		{
			Dispatcher dispatcher = StaticConfigurationAdmin.this.dispatcher;
			if (dispatcher != null)
				dispatcher.enqueue(new Runnable()
				{
					public void run()
					{
						modified(reference, factory);
					}
				});
		}

		/**
		 * Schedules a removed event.
		 * 
		 * @param reference The reference that caused the event.
		 * @param factory True if the reference is a managed service factory.
		 */
		private void doRemovedService(final ServiceReference reference,
				final boolean factory)
		{
			Dispatcher dispatcher = StaticConfigurationAdmin.this.dispatcher;
			if (dispatcher != null)
				dispatcher.enqueue(new Runnable()
				{
					public void run()
					{
						removed(reference, factory);
					}
				});
		}
	}

	/**
	 * The object that dispatches configuration events asynchronously.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class Dispatcher implements Runnable
	{
		/** The queue of actions to run. */
		private final LinkedList queue = new LinkedList();
		/** The state of this dispatcher. */
		private int state = 0;

		/**
		 * Starts the dispatcher thread.
		 */
		synchronized void start()
		{
			if (state != 0)
				return;
			state = 1;
			new Thread(this).start();
			while (state != 2)
			{
				try
				{
					wait();
				}
				catch (InterruptedException e)
				{
				}
			}
		}

		/**
		 * Enqueues the supplied task.
		 * 
		 * @param runnable The task to enqueue.
		 */
		synchronized void enqueue(Runnable runnable)
		{
			if (state > 2)
				return;
			queue.addLast(runnable);
			notify();
		}

		/**
		 * Stops the dispatcher thread.
		 */
		synchronized void stop()
		{
			if (state != 2)
				return;
			state = 3;
			queue.clear();
			notify();
			while (state != 4)
			{
				try
				{
					wait();
				}
				catch (InterruptedException e)
				{
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			synchronized (this)
			{
				if (state != 1)
					return;
				state = 2;
				notify();
			}
			while (true)
			{
				Runnable next = null;
				synchronized (this)
				{
					while (state == 2 && queue.isEmpty())
					{
						try
						{
							wait();
						}
						catch (InterruptedException e)
						{
						}
					}
					if (state == 2)
						next = (Runnable)queue.removeFirst();
				}
				if (next == null)
					break;
				else
				{
					try
					{
						next.run();
					}
					catch (Exception e)
					{
						error(e);
					}
				}
			}
			synchronized (this)
			{
				state = 4;
				notify();
			}
		}
	}
}
