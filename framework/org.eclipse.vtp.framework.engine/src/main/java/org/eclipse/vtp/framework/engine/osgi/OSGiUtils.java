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

import org.eclipse.core.runtime.IContributor;
import org.osgi.framework.Bundle;

/**
 * Utilities for the OSGi environment.
 *
 * @author Lonnie Pryor
 */
public class OSGiUtils
{
	/**
	 * Finds the bundle that matches the specified contributor.
	 * 
	 * @param contributor The contributor to match to a bundle.
	 * @param bundles The bundles to search for a match.
	 * @return The bundle that matched the specified contributor.
	 */
	public static Bundle findBundle(IContributor contributor, Bundle[] bundles)
	{
		String name = contributor.getName();
		for (int i = 0; i < bundles.length; ++i)
		{
			switch (bundles[i].getState())
			{
			case Bundle.INSTALLED:
			case Bundle.UNINSTALLED:
				break;
			default:
				if (name.equals(bundles[i].getSymbolicName()))
					return bundles[i];
			}
		}
		return null;
	}
}
