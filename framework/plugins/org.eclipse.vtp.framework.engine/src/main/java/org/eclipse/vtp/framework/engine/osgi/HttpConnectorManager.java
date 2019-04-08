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

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.engine.DeploymentAdmin;
import org.eclipse.vtp.framework.engine.ProcessDefinition;
import org.eclipse.vtp.framework.engine.ResourceGroup;
import org.eclipse.vtp.framework.engine.http.HttpConnector;
import org.eclipse.vtp.framework.spi.IProcessEngine;
import org.eclipse.vtp.framework.util.Guid;
import org.eclipse.vtp.framework.util.SingletonTracker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;

/**
 * Maintains an instance of {@link HttpConnector} linked to the currently
 * registered instance of {@link IProcessEngine} and the most desireable
 * instance of {@link HttpService}.
 * 
 * @author Lonnie Pryor
 */
public final class HttpConnectorManager extends SingletonTracker
{
	/** The log to use. */
	private final LogService log;
	/** The extension registry to use. */
	private final IExtensionRegistry extensionRegistry;
	/** The process engine to use. */
	private final IProcessEngine processEngine;
	/** Comment for reporter. */
	private final IReporter reporter;
	/** The instance of the HTTP connector. */
	private HttpConnectorInstance httpConnectorInstance = null;

	/**
	 * createHttpFilter.
	 * 
	 * @param context TODO
	 * @return
	 */
	private static Filter createHttpFilter(BundleContext context)
	{
		String config = context
				.getProperty("org.eclipse.vtp.framework.engine.http.service");
		try
		{
			if (config == null)
				return context.createFilter(String.format("(OBJECTCLASS=%s)",
						HttpService.class.getName()));
			else
				return context
						.createFilter(String
								.format(
										"(&(OBJECTCLASS=%s)(org.eclipse.vtp.framework.engine.http.service=%s))",
										HttpService.class.getName(), config));
		}
		catch (InvalidSyntaxException e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Creates a new HttpConnectorManager.
	 * 
	 * @param context The context to operate under.
	 * @param log The log to use.
	 * @param log The extension registry to use.
	 * @param log The process engine to use.
	 */
	public HttpConnectorManager(BundleContext context, LogService log,
			IExtensionRegistry extensionRegistry, IProcessEngine processEngine,
			IReporter reporter)
	{
		super(context, createHttpFilter(context), null);
		this.log = log;
		this.extensionRegistry = extensionRegistry;
		this.processEngine = processEngine;
		this.reporter = reporter;
	}

	/**
	 * Creates a HTTP connector with the supplied HTTP service.
	 * 
	 * @param httpService The HTTP service to use.
	 */
	private void createHttpConnector(HttpService httpService)
	{
		log.log(LogService.LOG_DEBUG, "Creating HTTP connector...");
		httpConnectorInstance = new HttpConnectorInstance(httpService);
		httpConnectorInstance.open();
		log.log(LogService.LOG_DEBUG, "HTTP connector created.");
	}

	/**
	 * Releases the current HTTP connector.
	 */
	private void releaseHttpConnector()
	{
		try
		{
			if (httpConnectorInstance != null)
			{
				log.log(LogService.LOG_DEBUG, "Releasing HTTP connector...");
				httpConnectorInstance.close();
			}
		}
		finally
		{
			if (httpConnectorInstance != null)
			{
				httpConnectorInstance = null;
				log.log(LogService.LOG_DEBUG, "HTTP connector released.");
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
		HttpService service = (HttpService)context.getService(reference);
		boolean failed = true;
		try
		{
			createHttpConnector(service);
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
		HttpService newService = (HttpService)context.getService(newReference);
		releaseHttpConnector();
		boolean failed = true;
		try
		{
			createHttpConnector(newService);
			failed = false;
		}
		finally
		{
			if (failed)
			{
				newService = null;
				context.ungetService(newReference);
				createHttpConnector((HttpService)oldService);
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
			releaseHttpConnector();
		}
		finally
		{
			context.ungetService(reference);
		}
	}

	/**
	 * Manages an instance of the HTTP connector.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class HttpConnectorInstance implements IExtensionChangeHandler
	{
		/** The definition extension handler. */
		final IExtensionPoint definitions;
		/** The definition extension handler. */
		final IExtensionPoint resources;
		/** The HTTP connector to use. */
		final HttpConnector httpConnector;
		/** The document builder to use. */
		private DocumentBuilder documentBuilder;
		/** The extension registry tracker. */
		IExtensionTracker extensionTracker = null;
		/** The basic configuration registration. */
		ServiceRegistration configRegistration = null;
		/** The deployment configuration registration. */
		ServiceRegistration deploymentsRegistration = null;

		/**
		 * Creates a new HttpConnectorInstance.
		 * 
		 * @param httpService The HTTP connector to use.
		 */
		HttpConnectorInstance(HttpService httpService)
		{
			this.definitions = extensionRegistry
					.getExtensionPoint("org.eclipse.vtp.framework.engine.definitions"); //$NON-NLS-1$
			this.resources = extensionRegistry
					.getExtensionPoint("org.eclipse.vtp.framework.engine.resources"); //$NON-NLS-1$
			this.httpConnector = new HttpConnector(log, processEngine, httpService,
					reporter);
			httpConnector.configure(null);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);
			try
			{
				documentBuilder = factory.newDocumentBuilder();
			}
			catch (Exception e)
			{
				log.log(LogService.LOG_ERROR, e.getMessage(), e);
				throw new IllegalStateException(e);
			}
		}

		/**
		 * Activates this HTTP connector instance.
		 */
		void open()
		{
			extensionTracker = new ExtensionTracker(extensionRegistry);
			synchronized (this)
			{
				IExtensionPoint[] extensionPoints = new IExtensionPoint[] {
						definitions, resources };
				extensionTracker.registerHandler(this, ExtensionTracker
						.createExtensionPointFilter(extensionPoints));
				for (int i = 0; i < extensionPoints.length; ++i)
				{
					IExtension[] extensions = extensionPoints[i].getExtensions();
					for (int j = 0; j < extensions.length; ++j)
						addExtension(extensionTracker, extensions[j]);
				}
			}
			Hashtable properties = new Hashtable();
			properties.put(Constants.SERVICE_PID,
					"org.eclipse.vtp.framework.engine.http"); //$NON-NLS-1$
			configRegistration = context.registerService(ManagedService.class
					.getName(), new HttpConnectorConfig(httpConnector), properties);
			properties = new Hashtable();
			properties.put(Constants.SERVICE_PID,
					"org.eclipse.vtp.framework.engine.http.deployments"); //$NON-NLS-1$
			deploymentsRegistration = context.registerService(new String[] {
					ManagedServiceFactory.class.getName(),
					DeploymentAdmin.class.getName() }, new HttpConnectorDeployments(
					httpConnector), properties);
		}

		/**
		 * Deactivates this HTTP connector instance.
		 */
		void close()
		{
			try
			{
				if (deploymentsRegistration != null)
					deploymentsRegistration.unregister();
			}
			finally
			{
				deploymentsRegistration = null;
				try
				{
					if (configRegistration != null)
						configRegistration.unregister();
				}
				finally
				{
					configRegistration = null;
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
			Bundle contributor = OSGiUtils.findBundle(extension.getContributor(),
					context.getBundles());
			if (contributor == null)
				return;
			synchronized (this)
			{
				Object[] objects = tracker.getObjects(extension);
				if (objects != null && objects.length > 0)
					return;
				IConfigurationElement[] elements = extension.getConfigurationElements();
				for (int i = 0; i < elements.length; ++i)
				{
					String id = elements[i].getAttribute("id"); //$NON-NLS-1$
					if (id == null)
						continue;
					String path = elements[i].getAttribute("path"); //$NON-NLS-1$
					if (path == null)
						continue;
					if (definitions.getUniqueIdentifier().equals(
							extension.getExtensionPointUniqueIdentifier()))
					{
						InputStream input = null;
						try
						{
							httpConnector.registerDefinition(id, new ProcessDefinition(
									documentBuilder.parse(input = contributor.getEntry(path)
											.openStream())), contributor);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							log.log(LogService.LOG_ERROR, e.getMessage(), e);
							continue;
						}
						finally
						{
							try
							{
								if (input != null)
									input.close();
							}
							catch (IOException e)
							{
							}
						}
					}
					else
						httpConnector.registerResouces(id, new ResourceGroup(contributor,
								path));
					tracker.registerObject(extension, id, IExtensionTracker.REF_STRONG);
				}
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
				if (objects == null || objects.length == 0)
					return;
				for (int i = 0; i < objects.length; ++i)
				{
					if (definitions.getUniqueIdentifier().equals(
							extension.getExtensionPointUniqueIdentifier()))
						httpConnector.releaseDefinition((String)objects[i]);
					else
						httpConnector.releaseResouces((String)objects[i]);
				}
			}
		}
	}

	/**
	 * A proxy for the basic HTTP connector configuration.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class HttpConnectorConfig implements ManagedService
	{
		/** The connector to configure. */
		final HttpConnector connector;

		/**
		 * Creates a new HttpConnectorConfig.
		 * 
		 * @param connector The connector to configure.
		 */
		HttpConnectorConfig(HttpConnector connector)
		{
			this.connector = connector;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.osgi.service.cm.ManagedService#updated(java.util.Dictionary)
		 */
		public void updated(Dictionary properties) throws ConfigurationException
		{
			connector.configure(properties);
		}
	}

	/**
	 * A proxy for the HTTP connector deployment configuration.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class HttpConnectorDeployments implements
			ManagedServiceFactory, DeploymentAdmin
	{
		/** The connector to deploy to. */
		final HttpConnector connector;

		/**
		 * Creates a new HttpConnectorDeployments.
		 * 
		 * @param connector The connector to deploy to.
		 */
		HttpConnectorDeployments(HttpConnector connector)
		{
			this.connector = connector;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.osgi.service.cm.ManagedServiceFactory#getName()
		 */
		public String getName()
		{
			return connector.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.osgi.service.cm.ManagedServiceFactory#updated(java.lang.String,
		 *      java.util.Dictionary)
		 */
		public void updated(String pid, Dictionary properties)
				throws ConfigurationException
		{
			connector.deploy(pid, properties);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.osgi.service.cm.ManagedServiceFactory#deleted(java.lang.String)
		 */
		public void deleted(String pid)
		{
			connector.undeploy(pid);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.engine.DeplymentAdmin#deploy(
		 *      java.util.Dictionary)
		 */
		public String deploy(Dictionary properties)
		{
			String id = Guid.createGUID();
			try
			{
				updated(id, properties);
			}
			catch (ConfigurationException e)
			{
				throw new IllegalArgumentException(e);
			}
			return id;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.engine.DeplymentAdmin#update(
		 *      java.lang.String, java.util.Dictionary)
		 */
		public boolean update(String id, Dictionary properties)
		{
			try
			{
				updated(id, properties);
			}
			catch (ConfigurationException e)
			{
				throw new IllegalArgumentException(e);
			}
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.engine.DeplymentAdmin#undelpoy(
		 *      java.lang.String)
		 */
		public boolean undelpoy(String id)
		{
			deleted(id);
			return true;
		}
	}
}
