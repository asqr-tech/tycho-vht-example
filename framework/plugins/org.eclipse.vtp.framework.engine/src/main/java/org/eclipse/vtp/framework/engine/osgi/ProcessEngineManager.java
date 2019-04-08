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
package org.eclipse.vtp.framework.engine.osgi;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.engine.ActionDescriptor;
import org.eclipse.vtp.framework.engine.ConfigurationDescriptor;
import org.eclipse.vtp.framework.engine.IdentifierDescriptor;
import org.eclipse.vtp.framework.engine.ObserverDescriptor;
import org.eclipse.vtp.framework.engine.ServiceDescriptor;
import org.eclipse.vtp.framework.engine.main.ProcessEngine;
import org.eclipse.vtp.framework.spi.IProcessEngine;
import org.eclipse.vtp.framework.util.SingletonTracker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

/**
 * Maintains a registered instance of {@link IProcessEngine} and an instance of
 * {@link HttpConnectorManager} linked to the most desireable instance of
 * {@link IExtensionRegistry}.
 * 
 * @author Lonnie Pryor
 */
public final class ProcessEngineManager extends SingletonTracker
{
	/** The log to use. */
	private final LogService log;
	/** Comment for reporter. */
	private final IReporter reporter;
	/** The instance of the process engine. */
	private ProcessEngineInstance processEngineInstance = null;
	/** The HTTP connector manager. */
	private HttpConnectorManager httpConnectorManager = null;

	/**
	 * Creates a new ExtensionRegistryTracker.
	 * 
	 * @param context The context to operate under.
	 * @param log The log to use.
	 */
	public ProcessEngineManager(BundleContext context, LogService log,
			IReporter reporter)
	{
		super(context, IExtensionRegistry.class.getName(), null);
		this.log = log;
		this.reporter = reporter;
	}

	/**
	 * Creates a process engine with the supplied registry.
	 * 
	 * @param extensionRegistry The extension registry to use.
	 */
	private void createProcessEngine(IExtensionRegistry extensionRegistry)
	{
		log.log(LogService.LOG_DEBUG, "Creating process engine...");
		processEngineInstance = new ProcessEngineInstance(extensionRegistry);
		processEngineInstance.open();
		log.log(LogService.LOG_DEBUG, "Process engine created.");
		log.log(LogService.LOG_DEBUG, "Creating HTTP connector manager...");
		httpConnectorManager = new HttpConnectorManager(context, log,
				extensionRegistry, processEngineInstance.processEngine, reporter);
		httpConnectorManager.open();
		log.log(LogService.LOG_DEBUG, "HTTP connector manager created.");
	}

	/**
	 * Releases the current process engine.
	 */
	private void releaseProcessEngine()
	{
		try
		{
			if (httpConnectorManager != null)
			{
				log.log(LogService.LOG_DEBUG, "Releasing HTTP connector manager...");
				httpConnectorManager.close();
			}
		}
		finally
		{
			if (httpConnectorManager != null)
			{
				httpConnectorManager = null;
				log.log(LogService.LOG_DEBUG, "HTTP connector manager released.");
			}
			try
			{
				if (processEngineInstance != null)
				{
					log.log(LogService.LOG_DEBUG, "Releasing process engine...");
					processEngineInstance.close();
				}
			}
			finally
			{
				if (processEngineInstance != null)
				{
					processEngineInstance = null;
					log.log(LogService.LOG_DEBUG, "Process engine released.");
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.kernel.util.SingletonTrackerCustomizer#
	 *      selectingService(org.osgi.framework.ServiceReference)
	 */
	public Object selectingService(ServiceReference reference)
	{
		IExtensionRegistry service = (IExtensionRegistry)context
				.getService(reference);
		boolean failed = true;
		try
		{
			createProcessEngine(service);
			failed = false;
		}
		finally
		{
			if (failed)
			{
				service = null;
				context.ungetService(reference);
			}
		}
		return service;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.kernel.util.SingletonTrackerCustomizer#
	 *      changingSelectedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object, org.osgi.framework.ServiceReference)
	 */
	public Object changingSelectedService(ServiceReference oldReference,
			Object oldService, ServiceReference newReference)
	{
		IExtensionRegistry newService = (IExtensionRegistry)context
				.getService(newReference);
		releaseProcessEngine();
		boolean failed = true;
		try
		{
			createProcessEngine(newService);
			failed = false;
		}
		finally
		{
			if (failed)
			{
				newService = null;
				context.ungetService(newReference);
				createProcessEngine((IExtensionRegistry)oldService);
			}
		}
		context.ungetService(oldReference);
		return newService;
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
		try
		{
			releaseProcessEngine();
		}
		finally
		{
			context.ungetService(reference);
		}
	}

	/**
	 * Manages an instance of the process engine.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ProcessEngineInstance implements IExtensionChangeHandler
	{
		/** The extension registry to use. */
		final IExtensionRegistry extensionRegistry;
		/** The action extension handler. */
		final IExtensionPoint actions;
		/** The configuration extension handler. */
		final IExtensionPoint configurations;
		/** The observer extension handler. */
		final IExtensionPoint observers;
		/** The service extension handler. */
		final IExtensionPoint services;
		/** The process engine to use. */
		final ProcessEngine processEngine;
		/** The extension registry tracker. */
		IExtensionTracker extensionTracker = null;
		/** The process engine service registration. */
		ServiceRegistration registration = null;

		/**
		 * Creates a new ProcessEngineInstance.
		 * 
		 * @param extensionRegistry The extension registry to use.
		 */
		ProcessEngineInstance(IExtensionRegistry extensionRegistry)
		{
			this.extensionRegistry = extensionRegistry;
			this.actions = extensionRegistry
					.getExtensionPoint("org.eclipse.vtp.framework.core.actions"); //$NON-NLS-1$
			this.configurations = extensionRegistry
					.getExtensionPoint("org.eclipse.vtp.framework.core.configurations"); //$NON-NLS-1$
			this.observers = extensionRegistry
					.getExtensionPoint("org.eclipse.vtp.framework.core.observers"); //$NON-NLS-1$
			this.services = extensionRegistry
					.getExtensionPoint("org.eclipse.vtp.framework.core.services"); //$NON-NLS-1$
			this.processEngine = new ProcessEngine(extensionRegistry);
		}

		/**
		 * Activates this process engine instance.
		 */
		void open()
		{
			extensionTracker = new ExtensionTracker(extensionRegistry);
			synchronized (this)
			{
				IExtensionPoint[] extensionPoints = new IExtensionPoint[] { actions,
						configurations, observers, services };
				extensionTracker.registerHandler(this, ExtensionTracker
						.createExtensionPointFilter(extensionPoints));
				for (int i = 0; i < extensionPoints.length; ++i)
				{
					IExtension[] extensions = extensionPoints[i].getExtensions();
					for (int j = 0; j < extensions.length; ++j)
						addExtension(extensionTracker, extensions[j]);
				}
			}
			registration = context.registerService(IProcessEngine.class.getName(),
					processEngine, null);
		}

		/**
		 * Deactivates this process engine instance.
		 */
		void close()
		{
			try
			{
				if (registration != null)
					registration.unregister();
			}
			finally
			{
				registration = null;
				try
				{
					if (extensionTracker != null)
						extensionTracker.unregisterHandler(this);
				}
				finally
				{
					try
					{
						if (extensionTracker != null)
							extensionTracker.close();
					}
					finally
					{
						extensionTracker = null;
					}
				}
			}
		}

		/**
		 * Loads an action descriptor.
		 * 
		 * @param contributor The bundle that contributed the element.
		 * @param element The configuration element to load from.
		 * @return A new action descriptor.
		 */
		ActionDescriptor loadAction(Bundle contributor,
				IConfigurationElement element)
		{
			System.out.println("Loading action class: " + element.getAttribute("type"));
			return new ActionDescriptor(element.getAttribute("id"), //$NON-NLS-1$
					element.getAttribute("name"), //$NON-NLS-1$
					loadClass(contributor, element.getAttribute("type")), //$NON-NLS-1$
					Boolean.TRUE.toString().equalsIgnoreCase(
							element.getAttribute("blocking")) //$NON-NLS-1$
			);
		}

		/**
		 * Loads a configuration descriptor.
		 * 
		 * @param contributor The bundle that contributed the element.
		 * @param element The configuration element to load from.
		 * @return A new configuration descriptor.
		 */
		ConfigurationDescriptor loadConfiguration(Bundle contributor,
				IConfigurationElement element)
		{
			return new ConfigurationDescriptor(element.getAttribute("id"), //$NON-NLS-1$
					element.getAttribute("name"), //$NON-NLS-1$
					element.getAttribute("xml-namespace"), //$NON-NLS-1$
					element.getAttribute("xml-tag"), //$NON-NLS-1$
					loadClass(contributor, element.getAttribute("type")) //$NON-NLS-1$
			);
		}

		/**
		 * Loads an observer descriptor.
		 * 
		 * @param contributor The bundle that contributed the element.
		 * @param element The configuration element to load from.
		 * @return A new observer descriptor.
		 */
		ObserverDescriptor loadObserver(Bundle contributor,
				IConfigurationElement element)
		{
			return new ObserverDescriptor(element.getAttribute("id"), //$NON-NLS-1$
					element.getAttribute("name"), //$NON-NLS-1$
					loadClass(contributor, element.getAttribute("type")), //$NON-NLS-1$
					Boolean.TRUE.toString().equalsIgnoreCase(
							element.getAttribute("blocking")) //$NON-NLS-1$
			);
		}

		/**
		 * Loads a service descriptor.
		 * 
		 * @param contributor The bundle that contributed the element.
		 * @param element The configuration element to load from.
		 * @return A new service descriptor.
		 */
		ServiceDescriptor loadService(Bundle contributor,
				IConfigurationElement element)
		{
			IConfigurationElement[] identifierElements = element
					.getChildren("identifier"); //$NON-NLS-1$
			IdentifierDescriptor[] identifiers = new IdentifierDescriptor[identifierElements.length];
			for (int i = 0; i < identifierElements.length; ++i)
			{
				IConfigurationElement[] qualifierElements = identifierElements[i]
						.getChildren("qualifier"); //$NON-NLS-1$
				String[] qualifiers = new String[qualifierElements.length];
				for (int j = 0; j < qualifierElements.length; ++j)
					qualifiers[j] = qualifierElements[j].getAttribute("name"); //$NON-NLS-1$
				identifiers[i] = new IdentifierDescriptor(identifierElements[i]
						.getAttribute("name"), qualifiers); //$NON-NLS-1$
			}
			return new ServiceDescriptor(element.getAttribute("id"), //$NON-NLS-1$
					element.getAttribute("name"), //$NON-NLS-1$
					element.getAttribute("scope"), //$NON-NLS-1$
					loadClass(contributor, element.getAttribute("type")), //$NON-NLS-1$
					identifiers);
		}

		/**
		 * Loads a class defined in the specified contributor.
		 * 
		 * @param contributor The bundle that contributed the element.
		 * @param className The name of the class to load.
		 * @return A class defined in the specified contributor.
		 * @throws IllegalStateException If the specified class is not found.
		 */
		Class loadClass(Bundle contributor, String className)
				throws IllegalStateException
		{
			try
			{
				return contributor.loadClass(className);
			}
			catch (ClassNotFoundException e)
			{
				throw new IllegalStateException(e);
			}
		}

		/**
		 * Returns our reference of the specified exetension's point..
		 * 
		 * @param extension The extension to find the point of.
		 * @return Our reference of the specified exetension's point.
		 */
		private IExtensionPoint getExtensionPoint(IExtension extension)
		{
			String epuid = extension.getExtensionPointUniqueIdentifier();
			if (actions.getUniqueIdentifier().equals(epuid))
				return actions;
			else if (configurations.getUniqueIdentifier().equals(epuid))
				return configurations;
			else if (observers.getUniqueIdentifier().equals(epuid))
				return observers;
			else if (services.getUniqueIdentifier().equals(epuid))
				return services;
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler#
		 *      addExtension(
		 *      org.eclipse.core.runtime.dynamichelpers.IExtensionTracker,
		 *      org.eclipse.core.runtime.IExtension)
		 */
		public void addExtension(IExtensionTracker tracker, IExtension extension)
		{
			try
            {
	            Bundle contributor = OSGiUtils.findBundle(extension.getContributor(),
	            		context.getBundles());
	            if (contributor == null)
	            	return;
	            synchronized (this)
	            {
	            	Object[] objects = tracker.getObjects(extension);
	            	if (objects != null && objects.length > 0)
	            		return;
	            	IExtensionPoint point = getExtensionPoint(extension);
	            	if (point == null)
	            		return;
	            	IConfigurationElement[] elements = extension.getConfigurationElements();
	            	for (int i = 0; i < elements.length; ++i)
	            	{
	            		Object descriptor = null;
	            		if (actions == point)
	            			descriptor = loadAction(contributor, elements[i]);
	            		else if (configurations == point)
	            			descriptor = loadConfiguration(contributor, elements[i]);
	            		else if (observers == point)
	            			descriptor = loadObserver(contributor, elements[i]);
	            		else if (services == point)
	            			descriptor = loadService(contributor, elements[i]);
	            		if (descriptor == null)
	            			continue;
	            		tracker.registerObject(extension, descriptor,
	            				IExtensionTracker.REF_STRONG);
	            		if (actions == point)
	            			processEngine.registerAction((ActionDescriptor)descriptor);
	            		else if (configurations == point)
	            			processEngine
	            					.registerConfiguration((ConfigurationDescriptor)descriptor);
	            		else if (observers == point)
	            			processEngine.registerObserver((ObserverDescriptor)descriptor);
	            		else if (services == point)
	            			processEngine.registerService((ServiceDescriptor)descriptor);
	            	}
	            }
            }
            catch(Exception e)
            {
	            e.printStackTrace();
            }
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler#
		 *      removeExtension(org.eclipse.core.runtime.IExtension,
		 *      java.lang.Object[])
		 */
		public void removeExtension(IExtension extension, Object[] objects)
		{
			synchronized (this)
			{
				if (objects == null || objects.length <= 0)
					return;
				IExtensionPoint point = getExtensionPoint(extension);
				if (point == null)
					return;
				for (int i = 0; i < objects.length; ++i)
				{
					Object descriptor = objects[i];
					if (actions == point)
						processEngine.unregisterAction((ActionDescriptor)descriptor);
					else if (configurations == point)
						processEngine
								.unregisterConfiguration((ConfigurationDescriptor)descriptor);
					else if (observers == point)
						processEngine.unregisterObserver((ObserverDescriptor)descriptor);
					else if (services == point)
						processEngine.unregisterService((ServiceDescriptor)descriptor);
				}
			}
		}
	}
}
