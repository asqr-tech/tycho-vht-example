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
package org.eclipse.vtp.framework.engine.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.vtp.framework.core.IContext;
import org.eclipse.vtp.framework.engine.ActionDescriptor;
import org.eclipse.vtp.framework.engine.ConfigurationDescriptor;
import org.eclipse.vtp.framework.engine.ObserverDescriptor;
import org.eclipse.vtp.framework.engine.ServiceDescriptor;
import org.eclipse.vtp.framework.spi.IProcessDefinition;
import org.w3c.dom.Element;

/**
 * The representation of the architecture of the process.
 * 
 * @author Lonnie Pryor
 */
public class Blueprint
{
	private final Map configurationIndex;
	private final Map processServiceIndex;
	private final Map sessionServiceIndex;
	private final Map executionServiceIndex;
	private final Map actionServiceIndex;
	private final Map executableIndex;
	private final Map<String, Executable> entryPointIndex;
	private final IExtensionRegistry registry;

	public Blueprint(IProcessDefinition definition,
			ConfigurationDescriptor[] configurationDescriptors,
			ActionDescriptor[] actionDescriptors,
			ObserverDescriptor[] observerDescriptors,
			ServiceDescriptor[] serviceDescriptors, IExtensionRegistry registry)
	{
		this.configurationIndex = indexConfigurations(configurationDescriptors);
		List processServiceDescriptors = new ArrayList(serviceDescriptors.length);
		List sessionServiceDescriptors = new ArrayList(serviceDescriptors.length);
		List executionServiceDescriptors = new ArrayList(serviceDescriptors.length);
		List actionServiceDescriptors = new ArrayList(serviceDescriptors.length);
		for (int i = 0; i < serviceDescriptors.length; ++i)
		{
			if (ServiceDescriptor.SCOPE_PROCESS.equals(serviceDescriptors[i]
					.getScope()))
				processServiceDescriptors.add(serviceDescriptors[i]);
			else if (ServiceDescriptor.SCOPE_SESSION.equals(serviceDescriptors[i]
					.getScope()))
				sessionServiceDescriptors.add(serviceDescriptors[i]);
			else if (ServiceDescriptor.SCOPE_EXECUTION.equals(serviceDescriptors[i]
					.getScope()))
				executionServiceDescriptors.add(serviceDescriptors[i]);
			else if (ServiceDescriptor.SCOPE_ACTION.equals(serviceDescriptors[i]
					.getScope()))
				actionServiceDescriptors.add(serviceDescriptors[i]);
		}
		this.processServiceIndex = buildServices(definition,
				processServiceDescriptors);
		this.sessionServiceIndex = buildServices(definition,
				sessionServiceDescriptors);
		this.executionServiceIndex = buildServices(definition,
				executionServiceDescriptors);
		this.actionServiceIndex = buildServices(definition,
				actionServiceDescriptors);
		this.executableIndex = buildExecutables(definition, actionDescriptors,
				observerDescriptors);
		this.entryPointIndex = linkExecutables(definition);
		this.registry = registry;
	}

	public Collection createConfigurations(Element data)
	{
		String xmlIdentifier = RuntimeUtils.getXMLIdentifier(data);
		List descriptors = (List)configurationIndex.get(xmlIdentifier);
		if (descriptors == null || descriptors.isEmpty())
			return Collections.EMPTY_LIST;
		List results = new ArrayList(descriptors.size());
		for (Iterator i = descriptors.iterator(); i.hasNext();)
			results.add(new Configuration(this, ((ConfigurationDescriptor)i.next()),
					data));
		return results;
	}
	
	public void solidifyConfigurations(IContext serviceRegistry)
	{
		Iterator i = processServiceIndex.values().iterator();
		while(i.hasNext())
		{
			List<Configurable> list = (List<Configurable>)i.next();
			for(Configurable configurable : list)
				configurable.solidifyConfigurations(serviceRegistry);
		}
		i = sessionServiceIndex.values().iterator();
		while(i.hasNext())
		{
			List<Configurable> list = (List<Configurable>)i.next();
			for(Configurable configurable : list)
				configurable.solidifyConfigurations(serviceRegistry);
		}
		i = executionServiceIndex.values().iterator();
		while(i.hasNext())
		{
			List<Configurable> list = (List<Configurable>)i.next();
			for(Configurable configurable : list)
				configurable.solidifyConfigurations(serviceRegistry);
		}
		i = actionServiceIndex.values().iterator();
		while(i.hasNext())
		{
			List<Configurable> list = (List<Configurable>)i.next();
			for(Configurable configurable : list)
				configurable.solidifyConfigurations(serviceRegistry);
		}
		i = executableIndex.values().iterator();
		while(i.hasNext())
		{
			Configurable configurable = (Configurable)i.next();
			configurable.solidifyConfigurations(serviceRegistry);
		}
	}

	public Collection getProcessServices(String identifier)
	{
		return (List)processServiceIndex.get(identifier);
	}

	public Collection getSessionServices(String identifier)
	{
		return (List)sessionServiceIndex.get(identifier);
	}

	public Collection getExecutionServices(String identifier)
	{
		return (List)executionServiceIndex.get(identifier);
	}

	public Collection getActionServices(String identifier)
	{
		return (List)actionServiceIndex.get(identifier);
	}

	public Executable getExecutable(String instanceID)
	{
		return (Executable)executableIndex.get(instanceID);
	}

	public Executable getEntryPoint(String entryName)
	{
		return entryPointIndex.get(entryName);
	}

	public IExtensionRegistry getRegistry()
	{
		return registry;
	}

	/**
	 * Builds a map of configuration descriptor lists indexed by XML identifiers.
	 * 
	 * @param definition The process definition.
	 * @param configurationDescriptors The environment configuration descriptors.
	 * @return A Map of configuration descriptor lists indexed by XML identifiers.
	 */
	private Map indexConfigurations(
			ConfigurationDescriptor[] configurationDescriptors)
	{
		Map configurationDescriptorIndex = new HashMap(
				configurationDescriptors.length);
		for (int i = 0; i < configurationDescriptors.length; ++i)
		{
			String xmlIdentifier = RuntimeUtils.getQualifiedIdentifier(
					configurationDescriptors[i].getXmlTag(), configurationDescriptors[i]
							.getXmlNamespace());
			LinkedList list = (LinkedList)configurationDescriptorIndex
					.get(xmlIdentifier);
			if (list == null)
				configurationDescriptorIndex
						.put(xmlIdentifier, list = new LinkedList());
			list.addLast(configurationDescriptors[i]);
		}
		for (Iterator i = configurationDescriptorIndex.entrySet().iterator(); i
				.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			entry.setValue(Collections.unmodifiableList(new ArrayList(
					(LinkedList)entry.getValue())));
		}
		return Collections
				.unmodifiableMap(new HashMap(configurationDescriptorIndex));
	}

	/**
	 * Builds a map of service lists indexed by identifiers.
	 * 
	 * @param definition The process definition.
	 * @param serviceDescriptors The environment service descriptors.
	 * @return A Map of service lists indexed by identifiers.
	 */
	private Map buildServices(IProcessDefinition definition,
			List serviceDescriptors)
	{
		Map serviceIndex = new HashMap(serviceDescriptors.size());
		for (Iterator i = serviceDescriptors.iterator(); i.hasNext();)
		{
			ServiceDescriptor descriptor = (ServiceDescriptor)i.next();
			Service service = new Service(this, definition
					.getServiceConfiguration(descriptor.getId()), descriptor);
			for (Iterator j = service.getIdentifiers().iterator(); j.hasNext();)
			{
				String identifier = (String)j.next();
				LinkedList list = (LinkedList)serviceIndex.get(identifier);
				if (list == null)
					serviceIndex.put(identifier, list = new LinkedList());
				list.addLast(service);
			}
		}
		return Collections.unmodifiableMap(new HashMap(serviceIndex));
	}

	/**
	 * Builds a map of executable objects indexed by instance ID.
	 * 
	 * @param definition The process definition.
	 * @param actionDescriptors The environment action descriptors.
	 * @param observerDescriptors The environment observer descriptors.
	 * @return A Map of service lists indexed by identifiers.
	 */
	private Map buildExecutables(IProcessDefinition definition,
			ActionDescriptor[] actionDescriptors,
			ObserverDescriptor[] observerDescriptors)
	{
		// Index the action descriptors.
		Map actionIndex = new HashMap();
		for (int i = 0; i < actionDescriptors.length; ++i)
			actionIndex.put(actionDescriptors[i].getId(), actionDescriptors[i]);
		// Index the observer descriptors.
		Map observerIndex = new HashMap();
		for (int i = 0; i < observerDescriptors.length; ++i)
			observerIndex.put(observerDescriptors[i].getId(), observerDescriptors[i]);
		// Index the executable instances.
		Map executableIndex = new HashMap();
		// Index each action instance.
		String[] actionIDs = definition.getActionInstanceIDs();
		for (int i = 0; i < actionIDs.length; ++i)
		{
			String actionID = actionIDs[i];
			Action action = new Action(this, definition.getActionName(actionID),
					definition.getActionConfiguration(actionID), actionID,
					((ActionDescriptor)actionIndex.get(definition
							.getActionDescriptorID(actionID))));
			executableIndex.put(actionID, action);
			// Index the action's before observers.
			String[] observerIDs = definition.getBeforeObserverInstanceIDs(actionID);
			for (int j = 0; j < observerIDs.length; ++j)
			{
				String observerID = observerIDs[j];
				Observer observer = new Observer(this, definition
						.getObserverConfiguration(observerID), observerID,
						((ObserverDescriptor)observerIndex.get(definition
								.getObserverDescriptorID(observerID))), action);
				executableIndex.put(observerID, observer);
			}
			// Index the action's after observers.
			String[] resultIDs = definition.getActionResultIDs(actionID);
			for (int j = 0; j < resultIDs.length; ++j)
			{
				observerIDs = definition.getAfterObserverInstanceIDs(actionID,
						resultIDs[j]);
				for (int k = 0; k < observerIDs.length; ++k)
				{
					String observerID = observerIDs[k];
					Observer observer = new Observer(this, definition
							.getObserverConfiguration(observerID), observerID,
							((ObserverDescriptor)observerIndex.get(definition
									.getObserverDescriptorID(observerID))), action);
					executableIndex.put(observerID, observer);
				}
			}
		}
		return Collections.unmodifiableMap(new HashMap(executableIndex));
	}

	/**
	 * Links the flow of the entire process and returns the entry point
	 * executable.
	 * 
	 * @param definition The process definition.
	 * @return The entry point executable.
	 */
	private Map<String, Executable> linkExecutables(IProcessDefinition definition)
	{
		Map<String, Executable> index = new HashMap<String, Executable>();
		// Link each action instance's before observers to determine target IDs.
		Map targetsByActionID = new HashMap();
		String[] actionIDs = definition.getActionInstanceIDs();
		for (int i = 0; i < actionIDs.length; ++i)
		{
			String actionID = actionIDs[i];
			Action action = (Action)getExecutable(actionID);
			// Link the action's before observers.
			String[] observerIDs = definition.getBeforeObserverInstanceIDs(actionID);
			Observer previous = null;
			for (int j = 0; j < observerIDs.length; ++j)
			{
				String observerID = observerIDs[j];
				Observer observer = (Observer)getExecutable(observerID);
				if (previous == null)
					targetsByActionID.put(actionID, observer);
				else
					previous.configure(observer);
				previous = observer;
			}
			if (previous == null)
				targetsByActionID.put(actionID, action);
			else
				previous.configure(action);
		}
		// Link the action's results and after observers.
		for (int i = 0; i < actionIDs.length; ++i)
		{
			Action action = (Action)getExecutable(actionIDs[i]);
			String[] resultIDs = definition.getActionResultIDs(actionIDs[i]);
			for (int j = 0; j < resultIDs.length; ++j)
			{
				Executable result = null;
				String[] observerIDs = definition.getAfterObserverInstanceIDs(
						actionIDs[i], resultIDs[j]);
				Observer previous = null;
				for (int k = 0; k < observerIDs.length; ++k)
				{
					Observer observer = (Observer)getExecutable(observerIDs[k]);
					if (previous == null)
						result = observer;
					else
						previous.configure(observer);
					previous = observer;
				}
				Executable target = (Executable)targetsByActionID.get(definition
						.getActionResultTargetInstanceID(actionIDs[i], resultIDs[j]));
				if (previous == null)
					result = target;
				else
					previous.configure(target);
				action.configure(resultIDs[j], result);
			}
		}
		String[] startActionIds = definition.getStartActionInstanceIDs();
		index.put("Default", (Executable)targetsByActionID.get(startActionIds[0]));
		for(String startActionId : startActionIds)
		{
			System.out.println("Indexing start action: " + definition.getActionName(startActionId) + "[" + startActionId + "]");
			index.put(definition.getActionName(startActionId), (Executable)targetsByActionID.get(startActionId));
		}
		return index;
	}
}
