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
package org.eclipse.vtp.framework.engine.main;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.vtp.framework.engine.ActionDescriptor;
import org.eclipse.vtp.framework.engine.ConfigurationDescriptor;
import org.eclipse.vtp.framework.engine.ObserverDescriptor;
import org.eclipse.vtp.framework.engine.ServiceDescriptor;
import org.eclipse.vtp.framework.engine.runtime.Blueprint;
import org.eclipse.vtp.framework.engine.runtime.Process;
import org.eclipse.vtp.framework.spi.IProcess;
import org.eclipse.vtp.framework.spi.IProcessDefinition;
import org.eclipse.vtp.framework.spi.IProcessDescriptor;
import org.eclipse.vtp.framework.spi.IProcessEngine;
import org.eclipse.vtp.framework.spi.IProcessEngineObserver;

/**
 * ProcessEngine.
 * 
 * @author Lonnie Pryor
 */
public class ProcessEngine implements IProcessEngine
{
	/** An empty array of observers. */
	private static final IProcessEngineObserver[] NO_OBSERVERS = new IProcessEngineObserver[0];

	/** The extension registry to use. */
	private final IExtensionRegistry extensionRegistry;
	/** The action component registry. */
	private final Map actionRegistry = new HashMap();
	/** The configuration component registry. */
	private final Map configurationRegistry = new HashMap();
	/** The observer component registry. */
	private final Map observerRegistry = new HashMap();
	/** The service component registry. */
	private final Map serviceRegistry = new HashMap();
	/** The set of registered observers. */
	private final Set<IProcessEngineObserver> observers = Collections.synchronizedSet(new HashSet<IProcessEngineObserver>());

	/**
	 * Creates a new ProcessEngine.
	 */
	public ProcessEngine(IExtensionRegistry extensionRegistry)
	{
		this.extensionRegistry = extensionRegistry;
	}

	/**
	 * Registers an action descriptor with this process engine.
	 * 
	 * @param descriptor The descriptor to register.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public void registerAction(ActionDescriptor descriptor)
			throws NullPointerException
	{
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		register(actionRegistry, IProcessEngineObserver.COMPONENT_TYPE_ACTION,
				descriptor.getId(), descriptor);
	}

	/**
	 * Removes an action descriptor registration from this process engine.
	 * 
	 * @param descriptor The descriptor to remove.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public void unregisterAction(ActionDescriptor descriptor)
			throws NullPointerException
	{
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		unregister(actionRegistry, IProcessEngineObserver.COMPONENT_TYPE_ACTION,
				descriptor.getId(), descriptor);
	}

	/**
	 * Registers a configuration descriptor with this process engine.
	 * 
	 * @param descriptor The descriptor to register.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public void registerConfiguration(ConfigurationDescriptor descriptor)
			throws NullPointerException
	{
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		register(configurationRegistry,
				IProcessEngineObserver.COMPONENT_TYPE_CONFIGURATION,
				descriptor.getId(), descriptor);
	}

	/**
	 * Removes a configuration descriptor registration from this process engine.
	 * 
	 * @param descriptor The descriptor to remove.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public void unregisterConfiguration(ConfigurationDescriptor descriptor)
			throws NullPointerException
	{
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		unregister(configurationRegistry,
				IProcessEngineObserver.COMPONENT_TYPE_CONFIGURATION,
				descriptor.getId(), descriptor);
	}

	/**
	 * Registers an observer descriptor with this process engine.
	 * 
	 * @param descriptor The descriptor to register.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public void registerObserver(ObserverDescriptor descriptor)
			throws NullPointerException
	{
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		register(observerRegistry, IProcessEngineObserver.COMPONENT_TYPE_OBSERVER,
				descriptor.getId(), descriptor);
	}

	/**
	 * Removes an observer descriptor registration from this process engine.
	 * 
	 * @param descriptor The descriptor to remove.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public void unregisterObserver(ObserverDescriptor descriptor)
			throws NullPointerException
	{
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		unregister(observerRegistry,
				IProcessEngineObserver.COMPONENT_TYPE_OBSERVER, descriptor.getId(),
				descriptor);
	}

	/**
	 * Registers a service descriptor with this process engine.
	 * 
	 * @param descriptor The descriptor to register.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public void registerService(ServiceDescriptor descriptor)
			throws NullPointerException
	{
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		register(serviceRegistry, IProcessEngineObserver.COMPONENT_TYPE_SERVICE,
				descriptor.getId(), descriptor);
	}

	/**
	 * Removes a service descriptor registration from this process engine.
	 * 
	 * @param descriptor The descriptor to remove.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public void unregisterService(ServiceDescriptor descriptor)
			throws NullPointerException
	{
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		unregister(serviceRegistry, IProcessEngineObserver.COMPONENT_TYPE_SERVICE,
				descriptor.getId(), descriptor);
	}

	/**
	 * Registers a descriptor under the specified ID in the supplied registry.
	 * 
	 * @param registry The registry to modify.
	 * @param identifier The identifier of the descriptor.
	 * @param descriptor The descriptor object to register.
	 */
	private void register(Map registry, int componentType, String identifier,
			Object descriptor)
	{
		int eventType = 0;
		synchronized (this)
		{
			Object value = registry.get(identifier);
			if (value == null)
			{
				registry.put(identifier, descriptor);
				eventType = IProcessEngineObserver.EVENT_TYPE_SELECTED;
			}
			else if (value instanceof LinkedList)
				((LinkedList)value).addLast(descriptor);
			else
			{
				LinkedList list = new LinkedList();
				list.addLast(value);
				list.addLast(descriptor);
				registry.put(identifier, list);
			}
		}
		if (eventType > 0)
			fireEvent(eventType, componentType, identifier);
	}

	/**
	 * Removes a descriptor registration with the specified ID in the supplied
	 * registry.
	 * 
	 * @param registry The registry to modify.
	 * @param identifier The identifier of the descriptor.
	 * @param descriptor The descriptor object to remove from the registry.
	 */
	private void unregister(Map registry, int componentType, String identifier,
			Object descriptor)
	{
		int eventType = 0;
		synchronized (this)
		{
			Object value = registry.get(identifier);
			if (value instanceof LinkedList)
			{
				LinkedList list = (LinkedList)value;
				if (descriptor.equals(list.getFirst()))
					eventType = IProcessEngineObserver.EVENT_TYPE_CHANGED;
				if (list.remove(descriptor))
					if (list.size() == 1)
						registry.put(identifier, list.removeFirst());
			}
			else if (descriptor.equals(value))
			{
				registry.remove(identifier);
				eventType = IProcessEngineObserver.EVENT_TYPE_RELEASED;
			}
		}
		if (eventType > 0)
			fireEvent(eventType, componentType, identifier);
	}

	/**
	 * Fires an event to all registered observers.
	 * 
	 * @param eventType The type of event to fire.
	 * @param componentType The type of component that changed.
	 * @param componentIdentifier The identifier of the component that changed.
	 */
	private void fireEvent(int eventType, int componentType,
			String componentIdentifier)
	{
		IProcessEngineObserver[] array = observers.toArray(NO_OBSERVERS);
		for (int i = 0; i < array.length; ++i)
			if (observers.contains(array[i]))
				array[i].processEngineUpdated(eventType, componentType,
						componentIdentifier);
	}

	/**
	 * Exports the selected descriptors for each identifier to the supplied array.
	 * 
	 * @param registry The registry to export from.
	 * @param results The results array to populate.
	 */
	private void exportDescriptors(Map registry, Object[] results)
	{
		int i = 0;
		for (Iterator j = registry.values().iterator(); j.hasNext(); ++i)
		{
			Object value = (Object)j.next();
			if (value instanceof LinkedList)
				results[i] = ((LinkedList)value).getFirst();
			else
				results[i] = value;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessEngine#createProcess(
	 *      org.eclipse.vtp.framework.spi.IProcessDefinition,
	 *      org.eclipse.vtp.framework.spi.IProcessDescriptor)
	 */
	public IProcess createProcess(IProcessDefinition definition,
			IProcessDescriptor descriptor) throws NullPointerException
	{
		System.out.println("Creating process for " + descriptor.getProcessID());
		ActionDescriptor[] actions = null;
		ConfigurationDescriptor[] configurations = null;
		ObserverDescriptor[] observers = null;
		ServiceDescriptor[] services = null;
		synchronized (this)
		{
			exportDescriptors(actionRegistry,
					actions = new ActionDescriptor[actionRegistry.size()]);
			exportDescriptors(configurationRegistry,
					configurations = new ConfigurationDescriptor[configurationRegistry
							.size()]);
			exportDescriptors(observerRegistry,
					observers = new ObserverDescriptor[observerRegistry.size()]);
			exportDescriptors(serviceRegistry,
					services = new ServiceDescriptor[serviceRegistry.size()]);
		}
		return new Process(new Blueprint(definition, configurations,
				actions, observers, services, extensionRegistry), descriptor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.process.IProcessEngine#
	 *      addProcessEngineObserver(
	 *      org.eclipse.vtp.framework.spi.process.IProcessEngineObserver)
	 */
	public void addProcessEngineObserver(IProcessEngineObserver observer)
			throws NullPointerException
	{
		if (observer == null)
			throw new NullPointerException("observer"); //$NON-NLS-1$
		observers.add(observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.process.IProcessEngine#
	 *      removeProcessEngineObserver(
	 *      org.eclipse.vtp.framework.spi.process.IProcessEngineObserver)
	 */
	public void removeProcessEngineObserver(IProcessEngineObserver observer)
			throws NullPointerException
	{
		if (observer == null)
			throw new NullPointerException("observer"); //$NON-NLS-1$
		observers.remove(observer);
	}
}
