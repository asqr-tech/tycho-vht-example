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
package org.eclipse.vtp.framework.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.framework.spi.IProcessDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Implementation of {@link IProcessDefinition}.
 * 
 * @author Lonnie Pryor
 */
public class ProcessDefinition implements IProcessDefinition
{
	/** The definition name space URI. */
	private static final String URI = //
	"http://eclipse.org/vtp/xml/framework/engine/process-definition"; //$NON-NLS-1$

	/** The service configurations. */
	private final Map serviceConfigurations = new HashMap();
	/** The names of the actions. */
	private final Map actionNames = new HashMap();
	/** The sequence definitions. */
	private final Map sequenceDefinitions = new HashMap();
	/** The observer descriptor IDs by instance ID. */
	private final Map observerDescriptorIDs = new HashMap();
	/** The observer configurations by instance ID. */
	private final Map observerConfigurations = new HashMap();
	/** The start action for the process. */
	private final String[] startActionInstanceID;

	/**
	 * Creates a new ProccessDefinition.
	 * 
	 * @param document The document to load.
	 * @throws IllegalArgumentException If the specified document is invalid.
	 * @throws NullPointerException If the specified document is <code>null</code>.
	 */
	public ProcessDefinition(Document document) throws IllegalArgumentException,
			NullPointerException
	{
		if (document == null)
			throw new NullPointerException("document"); //$NON-NLS-1$
		Element definition = document.getDocumentElement();
		if (definition == null)
			throw new IllegalArgumentException();
		if (!URI.equals(definition.getNamespaceURI()))
			throw new IllegalArgumentException();
		if (!"definition".equals(definition.getLocalName())) //$NON-NLS-1$
			throw new IllegalArgumentException();
		// Load the services.
		Element services = null;
		NodeList list = definition.getElementsByTagNameNS(URI, "services"); //$NON-NLS-1$
		if (list.getLength() == 1)
			services = (Element)list.item(0);
		else
			throw new IllegalArgumentException();
		list = services.getElementsByTagNameNS(URI, "service"); //$NON-NLS-1$
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element service = (Element)list.item(i);
			String id = service.getAttribute("id"); //$NON-NLS-1$
			NodeList elements = service.getChildNodes();
			List configs = new ArrayList(elements.getLength());
			for (int j = 0; j < elements.getLength(); ++j)
				if (elements.item(j) instanceof Element)
					configs.add(elements.item(j));
			serviceConfigurations.put(id, configs);
		}
		// Load the actions.
		Element actions = null;
		list = definition.getElementsByTagNameNS(URI, "actions"); //$NON-NLS-1$
		if (list.getLength() == 1)
			actions = (Element)list.item(0);
		else
			throw new IllegalArgumentException();
		list = actions.getElementsByTagNameNS(URI, "action"); //$NON-NLS-1$
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element action = (Element)list.item(i);
			String id = action
					.getAttribute("id"); //$NON-NLS-1$
			actionNames.put(id, action.getAttribute("name"));
			SequenceDefinition sequence = new SequenceDefinition(id,
					action.getAttribute("descriptor-id"), //$NON-NLS-1$
					action.getChildNodes());
			sequenceDefinitions.put(sequence.actionInstanceID, sequence);
		}
		// Load the observers.
		Element observers = null;
		list = definition.getElementsByTagNameNS(URI, "observers"); //$NON-NLS-1$
		if (list.getLength() == 1)
			observers = (Element)list.item(0);
		else
			throw new IllegalArgumentException();
		list = observers.getElementsByTagNameNS(URI, "observer"); //$NON-NLS-1$
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element observer = (Element)list.item(i);
			String id = observer.getAttribute("id"); //$NON-NLS-1$
			observerDescriptorIDs.put(id, observer.getAttribute("descriptor-id")); //$NON-NLS-1$
			NodeList elements = observer.getChildNodes();
			List configurations = new ArrayList(elements.getLength());
			for (int j = 0; j < elements.getLength(); ++j)
				if (elements.item(j) instanceof Element)
					configurations.add(elements.item(j));
			observerConfigurations.put(id, configurations);
		}
		// Load the transitions.
		Element transitions = null;
		list = definition.getElementsByTagNameNS(URI, "transitions"); //$NON-NLS-1$
		if (list.getLength() == 1)
			transitions = (Element)list.item(0);
		else
			throw new IllegalArgumentException();
		list = transitions.getElementsByTagNameNS(URI, "before"); //$NON-NLS-1$
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element before = (Element)list.item(i);
			String action = before.getAttribute("action"); //$NON-NLS-1$
			SequenceDefinition sequence = (SequenceDefinition)sequenceDefinitions
					.get(action);
			if (sequence == null)
				throw new IllegalArgumentException();
			if (sequence.beforeObservers != null)
				throw new IllegalArgumentException();
			NodeList notify = before.getElementsByTagNameNS(URI, "notify"); //$NON-NLS-1$
			sequence.beforeObservers = new ArrayList(notify.getLength());
			for (int j = 0; j < notify.getLength(); ++j)
			{
				String observer = ((Element)notify.item(j)).getAttribute("observer"); //$NON-NLS-1$
				if (!observerDescriptorIDs.containsKey(observer))
					throw new IllegalArgumentException();
				sequence.beforeObservers.add(observer);
			}
		}
		list = transitions.getElementsByTagNameNS(URI, "after"); //$NON-NLS-1$
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element after = (Element)list.item(i);
			String action = after.getAttribute("action"); //$NON-NLS-1$
			String path = after.getAttribute("path"); //$NON-NLS-1$
			String target = after.getAttribute("target"); //$NON-NLS-1$
			SequenceDefinition sequence = (SequenceDefinition)sequenceDefinitions
					.get(action);
			if (sequence == null)
				throw new IllegalArgumentException();
			if (sequence.targets.containsKey(path))
				throw new IllegalArgumentException();
			SequenceDefinition targetSequence = (SequenceDefinition)sequenceDefinitions
					.get(target);
			if (targetSequence == null)
				throw new IllegalArgumentException();
			sequence.targets.put(path, target);
			NodeList notify = after.getElementsByTagNameNS(URI, "notify"); //$NON-NLS-1$
			List afterObservers = new ArrayList(notify.getLength());
			sequence.afterObservers.put(path, afterObservers);
			for (int j = 0; j < notify.getLength(); ++j)
			{
				String observer = ((Element)notify.item(j)).getAttribute("observer"); //$NON-NLS-1$
				if (!observerDescriptorIDs.containsKey(observer))
					throw new IllegalArgumentException();
				afterObservers.add(observer);
			}
		}
		// Finish up
		List<String> startIDs = new ArrayList<String>(Arrays.asList(
				definition.getAttribute("start").split(",")));
		for (Iterator<String> i = startIDs.iterator(); i.hasNext();)
			if (!sequenceDefinitions.containsKey(i.next()))
				i.remove();
		startActionInstanceID = startIDs.toArray(new String[startIDs.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDefinition#
	 *      getServiceConfiguration(java.lang.String)
	 */
	public Element[] getServiceConfiguration(String serviceDescriptorID)
	{
		List list = (List)serviceConfigurations.get(serviceDescriptorID);
		if (list == null)
			return new Element[0];
		return (Element[])list.toArray(new Element[list.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDefinition#
	 *      getStartActionInstanceID()
	 */
	public String[] getStartActionInstanceIDs()
	{
		return startActionInstanceID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDefinition#
	 *      getActionInstanceIDs()
	 */
	public String[] getActionInstanceIDs()
	{
		return (String[])sequenceDefinitions.keySet().toArray(
				new String[sequenceDefinitions.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.spi.IProcessDefinition#getActionName(java.lang.String)
	 */
	public String getActionName(String actionInstanceID)
	{
		return (String)actionNames.get(actionInstanceID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDefinition#
	 *      getActionDescriptorID(java.lang.String)
	 */
	public String getActionDescriptorID(String actionInstanceID)
	{
		SequenceDefinition sequence = (SequenceDefinition)sequenceDefinitions
				.get(actionInstanceID);
		if (sequence == null)
			return null;
		return sequence.actionDescriptorID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDefinition#
	 *      getActionConfiguration(java.lang.String)
	 */
	public Element[] getActionConfiguration(String actionInstanceID)
	{
		SequenceDefinition sequence = (SequenceDefinition)sequenceDefinitions
				.get(actionInstanceID);
		if (sequence == null)
			return new Element[0];
		return (Element[])sequence.configurations
				.toArray(new Element[sequence.configurations.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDefinition#getActionResultIDs(
	 *      java.lang.String)
	 */
	public String[] getActionResultIDs(String actionInstanceID)
	{
		SequenceDefinition sequence = (SequenceDefinition)sequenceDefinitions
				.get(actionInstanceID);
		if (sequence == null)
			return new String[0];
		return (String[])sequence.targets.keySet().toArray(
				new String[sequence.targets.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDefinition#
	 *      getActionResultTargetInstanceID(java.lang.String, java.lang.String)
	 */
	public String getActionResultTargetInstanceID(String actionInstanceID,
			String actionResultID)
	{
		SequenceDefinition sequence = (SequenceDefinition)sequenceDefinitions
				.get(actionInstanceID);
		if (sequence == null)
			return null;
		return (String)sequence.targets.get(actionResultID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDefinition#
	 *      getBeforeObserverInstanceIDs(java.lang.String)
	 */
	public String[] getBeforeObserverInstanceIDs(String actionInstanceID)
	{
		SequenceDefinition sequence = (SequenceDefinition)sequenceDefinitions
				.get(actionInstanceID);
		if (sequence == null)
			return new String[0];
		if (sequence.beforeObservers == null)
			return new String[0];
		return (String[])sequence.beforeObservers
				.toArray(new String[sequence.beforeObservers.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDefinition#
	 *      getAfterObserverInstanceIDs(java.lang.String, java.lang.String)
	 */
	public String[] getAfterObserverInstanceIDs(String actionInstanceID,
			String actionResultID)
	{
		SequenceDefinition sequence = (SequenceDefinition)sequenceDefinitions
				.get(actionInstanceID);
		if (sequence == null)
			return new String[0];
		List afterObservers = (List)sequence.afterObservers.get(actionResultID);
		if (afterObservers == null)
			return new String[0];
		return (String[])afterObservers.toArray(new String[afterObservers.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDefinition#
	 *      getObserverDescriptorID(java.lang.String)
	 */
	public String getObserverDescriptorID(String observerInstanceID)
	{
		return (String)observerDescriptorIDs.get(observerInstanceID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDefinition#
	 *      getObserverConfiguration(java.lang.String)
	 */
	public Element[] getObserverConfiguration(String observerInstanceID)
	{
		List elements = (List)observerConfigurations.get(observerInstanceID);
		if (elements == null)
			return new Element[0];
		return (Element[])elements.toArray(new Element[elements.size()]);
	}

	/**
	 * Definition of an action and its observers.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class SequenceDefinition
	{
		final String actionInstanceID;
		final String actionDescriptorID;
		final List configurations;
		final Map targets = new HashMap();
		final Map afterObservers = new HashMap();
		List beforeObservers = null;

		SequenceDefinition(String actionInstanceID, String actionDescriptorID,
				NodeList configurations)
		{
			this.actionInstanceID = actionInstanceID;
			this.actionDescriptorID = actionDescriptorID;
			this.configurations = new ArrayList(configurations.getLength());
			for (int j = 0; j < configurations.getLength(); ++j)
				if (configurations.item(j) instanceof Element)
					this.configurations.add(configurations.item(j));
		}
	}
}
