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
package org.eclipse.vtp.desktop.model.interactive.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class InteractionTypeManager
{
	public static final String interactionTypeExtensionId = "org.eclipse.vtp.framework.interactions.core.interactionTypes";
	private static final InteractionTypeManager INSTANCE = new InteractionTypeManager();
	
	public static InteractionTypeManager getInstance()
	{
		return INSTANCE;
	}

	private List<InteractionType> interactionTypes = new ArrayList<InteractionType>();
	private Map<String, InteractionType> interactionTypesById = new HashMap<String, InteractionType>();

	public InteractionTypeManager()
	{
		super();
		IConfigurationElement[] formatterExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(interactionTypeExtensionId);
		for(int i = 0; i < formatterExtensions.length; i++)
		{
			InteractionType it = new InteractionType(formatterExtensions[i].getAttribute("id"), formatterExtensions[i].getAttribute("name"));
			interactionTypes.add(it);
			interactionTypesById.put(it.getId(), it);
		}
	}

	public List<InteractionType> getInteractionTypes()
	{
		return Collections.unmodifiableList(interactionTypes);
	}
	
	public InteractionType getType(String typeId)
	{
		return interactionTypesById.get(typeId);
	}
}
