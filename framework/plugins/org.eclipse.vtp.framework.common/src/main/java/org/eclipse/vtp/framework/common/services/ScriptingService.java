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
package org.eclipse.vtp.framework.common.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.common.IScriptingContext;
import org.eclipse.vtp.framework.common.IScriptingEngine;
import org.eclipse.vtp.framework.common.IScriptingProvider;
import org.eclipse.vtp.framework.common.IScriptingService;
import org.eclipse.vtp.framework.core.IContext;

/**
 * Default implementation of {@link IScriptingService}.
 * 
 * @author Lonnie Pryor
 */
public final class ScriptingService implements IScriptingService
{
	/** The prefix to use when searching the service registry. */
	private static final String LOOKUP_PREFIX = IScriptingProvider.class
			.getName()
			+ ":SCRIPTING_LANGUAGE="; //$NON-NLS-1$

	/** The registry to search for scripting providers in. */
	private final IContext serviceRegistry;
	/** The scriptable objects in this service's scope. */
	private final Set<IScriptable> scriptables;
	/** The scripting service in the next highest scope or <code>null</code>. */
	private final ScriptingService parent;
	/** The contexts that have been created indexed by scripting language. */
	private final Map<String, IScriptingContext> contexts = new HashMap<String, IScriptingContext>();

	/**
	 * Creates a new SrciptingService.
	 * 
	 * @param serviceRegistry The registry to search for scripting providers in.
	 * @param scriptables The scriptable objects in this context and above.
	 */
	public ScriptingService(IContext serviceRegistry,
			IScriptable[] scriptables)
	{
		this(serviceRegistry, scriptables, null);
	}

	/**
	 * Creates a new SrciptingService.
	 * 
	 * @param serviceRegistry The registry to search for scripting providers in.
	 * @param scriptables The scriptable objects in this context and above.
	 * @param parent The scripting service in the next highest scope or
	 *          <code>null</code> if no such parent service exists.
	 */
	public ScriptingService(IContext serviceRegistry,
			IScriptable[] scriptables, ScriptingService parent)
	{
		this.serviceRegistry = serviceRegistry;
		Map<String, IScriptable> map = new LinkedHashMap<String, IScriptable>();
		for (int i = 0; i < scriptables.length; ++i)
			if (!map.containsKey(scriptables[i].getName())
					&& (parent == null || !parent.contains(scriptables[i])))
				map.put(scriptables[i].getName(), scriptables[i]);
		this.scriptables = Collections.unmodifiableSet(
				new LinkedHashSet<IScriptable>(map.values()));
		this.parent = parent;
	}

	/**
	 * Returns true if this service or its parent has the specified scriptable
	 * registered.
	 * 
	 * @param scriptable The scriptable to check for.
	 * @return True if this service or its parent has the specified scriptable
	 *         registered.
	 */
	private boolean contains(IScriptable scriptable)
	{
		return scriptables.contains(scriptable) || parent != null
				&& parent.contains(scriptable);
	}

	/**
	 * Returns the scripting context for the specified language.
	 * 
	 * @param scriptingLanuage The language to find the context for.
	 * @return The scripting context for the specified language.
	 */
	private IScriptingContext getScriptingContext(String scriptingLanuage)
	{
		synchronized (contexts)
		{
			if (contexts.containsKey(scriptingLanuage))
				return contexts.get(scriptingLanuage);
		}
		IScriptingContext context = null;
		if (parent != null)
		{
			IScriptingContext parentContext = parent
					.getScriptingContext(scriptingLanuage);
			if (parentContext != null)
				context = parentContext
						.createScriptingContext(scriptables
								.toArray(new IScriptable[scriptables.size()]));
		}
		else
		{
			Object obj = serviceRegistry.lookup(LOOKUP_PREFIX
					+ scriptingLanuage);
			if (obj instanceof IScriptingProvider)
				context = ((IScriptingProvider)obj).createScriptingContext(
						scriptingLanuage, scriptables
								.toArray(new IScriptable[scriptables.size()]));
		}
		synchronized (contexts)
		{
			if (contexts.containsKey(scriptingLanuage))
				context = contexts.get(scriptingLanuage);
			else
				contexts.put(scriptingLanuage, context);
		}
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptingService#
	 *      createScriptingEngine(java.lang.String)
	 */
	public IScriptingEngine createScriptingEngine(String scriptingLanuage)
	{
		if (scriptingLanuage == null)
			return null;
		IScriptingContext context = getScriptingContext(scriptingLanuage);
		if (context == null)
			return null;
		return context.createScriptingEngine();
	}
}
