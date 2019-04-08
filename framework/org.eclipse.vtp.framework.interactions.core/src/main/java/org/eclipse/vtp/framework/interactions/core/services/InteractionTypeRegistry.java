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
package org.eclipse.vtp.framework.interactions.core.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.vtp.framework.interactions.core.IInteractionType;
import org.eclipse.vtp.framework.interactions.core.IInteractionTypeRegistry;

/**
 * Implementation of {@link IInteractionTypeRegistry}.
 * 
 * @author Lonnie Pryor
 */
public class InteractionTypeRegistry implements IInteractionTypeRegistry
{
	/** The interaction types by ID. */
	private final Map interactionTypes;

	/**
	 * Creates a new InteractionTypeRegistry.
	 * 
	 * @param registry The extension registry to load from.
	 */
	public InteractionTypeRegistry(IExtensionRegistry registry)
	{
		IExtensionPoint point = registry.getExtensionPoint(//
				"org.eclipse.vtp.framework.interactions.core.interactionTypes"); //$NON-NLS-1$
		IExtension[] extensions = point.getExtensions();
		Map interactionTypes = new HashMap(extensions.length);
		for (int i = 0; i < extensions.length; ++i)
		{
			IConfigurationElement[] elements = extensions[i]
					.getConfigurationElements();
			for (int j = 0; j < elements.length; ++j)
			{
				InteractionType contentType = new InteractionType(elements[j]
						.getAttribute("id"), //$NON-NLS-1$
						elements[j].getAttribute("name")); //$NON-NLS-1$
				interactionTypes.put(contentType.getId(), contentType);
			}
		}
		this.interactionTypes = Collections.unmodifiableMap(interactionTypes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.IInteractionTypeRegistry#
	 *      getInteractionTypeIDs()
	 */
	public String[] getInteractionTypeIDs()
	{
		return (String[])interactionTypes.keySet().toArray(
				new String[interactionTypes.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.IInteractionTypeRegistry#
	 *      getInteractionType(java.lang.String)
	 */
	public IInteractionType getInteractionType(String interactionTypeID)
	{
		return (IInteractionType)interactionTypes.get(interactionTypeID);
	}

	/**
	 * Implementation of {@link IInteractionType}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class InteractionType implements IInteractionType
	{
		/** The ID of this interaction type. */
		private final String id;
		/** The name of this interaction type. */
		private final String name;

		/**
		 * Creates a new InteractionType.
		 * 
		 * @param id The ID of this interaction type.
		 * @param name The name of this interaction type.
		 */
		InteractionType(String id, String name)
		{
			this.id = id;
			this.name = name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.IInteractionType#getId()
		 */
		public String getId()
		{
			return id;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.IInteractionType#
		 *      getName()
		 */
		public String getName()
		{
			return name;
		}
	}
}
