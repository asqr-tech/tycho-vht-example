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

import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.interactions.core.platforms.IPlatform;
import org.eclipse.vtp.framework.interactions.core.platforms.IPlatformSelector;

/**
 * Implementation of the platform selector.
 * 
 * @author Lonnie Pryor
 */
public class PlatformSelector implements IPlatformSelector
{
	/** The parameters that control platform selection. */
	private static final String[] PARAMETERS = { "USER_AGENT", //$NON-NLS-1$
			"ACCEPT", "MODE" }; //$NON-NLS-1$ //$NON-NLS-2$
	/** The qualifier prefixes generated for each parameter name. */
	private static final String[] PREFIXES;

	/** Generate the qualifier prefixes. */
	static
	{
		String[] prefixes = new String[PARAMETERS.length];
		StringBuffer buffer = new StringBuffer(IPlatform.class.getName())
				.append(':');
		int resetLength = buffer.length();
		for (int i = 0; i < prefixes.length; ++i)
		{
			if (i > 0)
				buffer.setLength(resetLength);
			prefixes[i] = buffer.append(PARAMETERS[i]).append('=').toString();
		}
		PREFIXES = prefixes;
	}

	/** The context to search in. */
	private final IExecutionContext context;
	private IPlatform selectedPlatform = null;

	/**
	 * Creates a new PlatformSelector.
	 * 
	 * @param context The context to search in.
	 */
	public PlatformSelector(IExecutionContext context)
	{
		this.context = context;
		selectedPlatform = selectPlatform();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.
	 *      IPlatformSelector#selectPlatform()
	 */
	public IPlatform selectPlatform()
	{
		for (int i = 0; i < PARAMETERS.length; ++i)
		{
			String[] values = context.getParameters(PARAMETERS[i]);
			if (values == null)
				continue;
			for (int j = 0; j < values.length; ++j)
			{
				if (values[j] == null)
					continue;
				System.out.println("Looking up platform: " + PREFIXES[i] + values[j]);
				Object platform = context.lookup(PREFIXES[i] + values[j]);
				System.out.println(platform);
				if (platform instanceof IPlatform)
					return (IPlatform)platform;
			}
		}
		Object platform = context.lookup(IPlatform.class.getName());
		if (platform instanceof IPlatform)
			return (IPlatform)platform;
		return null;
	}
	
	public IPlatform getSelectedPlatform()
	{
		return this.selectedPlatform;
	}
}
