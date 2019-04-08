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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Common base type for the runtime scopes (process, session, execution, and
 * action).
 * 
 * @author Lonnie Pryor
 */
public abstract class Scope
{
	/** The implicit service index. */
	private final Map implicitServices = new HashMap();
	/** The declared service index. */
	private final Map declaredServices = new HashMap();
	/** The current queue of builders to configure. */
	private List builderQueue = null;

	/**
	 * Creates a new Scope.
	 */
	protected Scope()
	{
	}
	
	protected void registerImplicitServices(String identifier, Object[] services)
	{
		implicitServices.put(identifier, Arrays.asList(services));
	}

	protected Object lookupInScope(String identifier)
	{
		Collection implicitServices = (Collection)this.implicitServices
				.get(identifier);
		if (implicitServices != null && !implicitServices.isEmpty())
			return implicitServices.iterator().next();
		Collection declaredServices = getServices(identifier);
		if (declaredServices != null && !declaredServices.isEmpty())
			return ((Service)declaredServices.iterator().next()).getInstance(this);
		return null;
	}

	protected Object[] lookupAllInScope(String identifier)
	{
		Collection implicitServices = (Collection)this.implicitServices
				.get(identifier);
		Collection declaredServices = getServices(identifier);
		List results = new ArrayList((implicitServices == null ? 0
				: implicitServices.size())
				+ (declaredServices == null ? 0 : declaredServices.size()));
		if (implicitServices != null && !implicitServices.isEmpty())
			results.addAll(implicitServices);
		if (declaredServices != null && !declaredServices.isEmpty())
		{
			for (Iterator i = declaredServices.iterator(); i.hasNext();)
			{
				Object instance = ((Service)i.next()).getInstance(this);
				if (instance != null)
					results.add(instance);
			}
		}
		return results.toArray();
	}

	protected Object getServiceInstance(Service service)
	{
		String id = service.getDescriptorID();
		synchronized (declaredServices)
		{
			Object instance = declaredServices.get(id);
			if (instance == null && !declaredServices.containsKey(id))
			{
				declaredServices.put(id, null);
				boolean flushBuilders = builderQueue == null;
				try
				{
					if (flushBuilders)
						builderQueue = new LinkedList();
					Builder builder = service.createBuilder(this);
					if ((instance = builder.create()) != null)
					{
						declaredServices.put(id, instance);
						builderQueue.add(builder);
					}
					if (flushBuilders)
					{
						List builders = builderQueue;
						builderQueue = null;
						RuntimeException re = null;
						for (Iterator i = builders.iterator(); i.hasNext();)
						{
							try
							{
								((Builder)i.next()).configure();
							}
							catch (RuntimeException e)
							{
								if (re == null)
									re = e;
							}
						}
						if (re != null)
							throw re;
					}
				}
				finally
				{
					if (flushBuilders && builderQueue != null)
						builderQueue = null;
					if (instance == null)
						declaredServices.remove(id);
				}
			}
			return instance;
		}
	}

	protected abstract Collection getServices(String identifier);
}
