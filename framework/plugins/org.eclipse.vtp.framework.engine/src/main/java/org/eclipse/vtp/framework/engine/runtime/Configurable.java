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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.vtp.framework.core.IContext;
import org.w3c.dom.Element;

/**
 * Base class for components that accept configuration data.
 * 
 * @author Lonnie Pryor
 */
public abstract class Configurable extends Component
{
	/** Index of objects returned by configuration queries. */
	private final Map configurationIndex;

	/**
	 * Creates a new Configurable.
	 * 
	 * @param blueprint The blueprint of the process.
	 * @param type The type of the configurable component.
	 * @param elements The configuration data or <code>null</code> for no
	 *          configuration data.
	 * @throws NullPointerException If the supplied blueprint is <code>null</code>.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 */
	protected Configurable(Blueprint blueprint, Class type, Element[] elements)
			throws NullPointerException
	{
		super(blueprint, type);
		HashMap configurationIndex = new HashMap();
		LinkedList elementList = new LinkedList();
		for (int i = 0; elements != null && i < elements.length; ++i)
		{
			if (elements[i] == null)
				continue;
			Element element = elements[i];
			elementList.addLast(element);
		}
		for (Iterator i = elementList.iterator(); i.hasNext();)
		{
			Element element = (Element)i.next();
			Collection configurationList = blueprint.createConfigurations(element);
			for (Iterator j = configurationList.iterator(); j.hasNext();)
			{
				Configuration configuration = (Configuration)j.next();
				Set identifierSet = configuration.getIdentifiers();
				for (Iterator k = identifierSet.iterator(); k.hasNext();)
				{
					String identifier = (String)k.next();
					LinkedList items = (LinkedList)configurationIndex.get(identifier);
					if (items == null)
						configurationIndex.put(identifier, items = new LinkedList());
					items.addLast(configuration);
				}
			}
		}
		for (Iterator i = configurationIndex.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			entry.setValue(Collections.unmodifiableList(new ArrayList(
					(LinkedList)entry.getValue())));
		}
		this.configurationIndex = Collections.unmodifiableMap(new HashMap(
				configurationIndex));
	}

	public void solidifyConfigurations(IContext serviceRegistry)
	{
		Iterator it = configurationIndex.values().iterator();
		while(it.hasNext())
		{
			List items = (List)it.next();
			for(int i = 0; i < items.size(); i++)
			{
				((Configuration)items.get(i)).solidify(serviceRegistry);
			}
		}
	}
	
	/**
	 * Looks up a single configuration object in the supplied scope with the
	 * specified identifier.
	 * 
	 * @param identifier The identifier of the configuration object to return.
	 * @param serviceRegistry The service registry that is bound to the
	 *          configurable component.
	 * @return A single configuration object or <code>null</code> if one is not
	 *         found.
	 */
	protected Object lookupConfiguration(String identifier,
			IContext serviceRegistry)
	{
		List items = (List)configurationIndex.get(identifier);
		if (items == null || items.isEmpty())
			return null;
		Object item = items.iterator().next();
		if (item instanceof Configuration)
			return ((Configuration)item).createInstance(serviceRegistry);
		else
			return item;
	}

	/**
	 * Returns all the configuration objects in the supplied scope with the
	 * specified identifier.
	 * 
	 * @param identifier The identifier of the configuration objects to return.
	 * @param serviceRegistry The service registry that is bound to the
	 *          configurable component.
	 * @return All the configuration objects or <code>null</code> if none are
	 *         found.
	 */
	protected Object[] lookupAllConfigurations(String identifier,
			IContext serviceRegistry)
	{
		List items = (List)configurationIndex.get(identifier);
		if (items == null || items.isEmpty())
			return null;
		Object[] results = new Object[items.size()];
		int i = 0;
		for (Iterator j = items.iterator(); j.hasNext(); ++i)
		{
			Object item = (Object)j.next();
			if (item instanceof Configuration)
				results[i] = ((Configuration)item).createInstance(serviceRegistry);
			else
			{
				results[i] = item;
			}
		}
		return results;
	}

	/**
	 * Creates a builder of this component for the specified scope.
	 * 
	 * @param scope The scope of the instance to create.
	 * @return A new builder of this component for the specified scope.
	 */
	protected Builder createBuilder(final Scope scope)
	{
		return new Builder()
		{
			protected Constructor[] getConstructors()
			{
				return constructors;
			}

			protected Method[] getMutators()
			{
				return mutators;
			}

			protected IContext createServiceRegistry()
			{
				return Configurable.this.createServiceRegistry(scope);
			}
		};
	}

	/**
	 * Creates a service registry for the specified scope.
	 * 
	 * @param scope The scope to create the registry for.
	 * @return A new service registry for the specified scope.
	 */
	protected abstract IContext createServiceRegistry(Scope scope);

	// public Object getInstance(Scope scope) throws IllegalStateException,
	// NullPointerException
	// {
	// if (scope == null)
	// throw new NullPointerException("scope"); //$NON-NLS-1$
	// Builder builder = createBuilder(scope);
	// Object instance = builder.create();
	// if (instance != null)
	// builder.configure();
	// return instance;
	// }
}
