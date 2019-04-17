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
package org.eclipse.vtp.desktop.views.pallet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class PallateProviderManager
{
	public static String PALLET_PROVIDER_EXTENSION_ID = "org.eclipse.vtp.desktop.views.palletProvider";
	
	public static List<PalletItemProvider> getPallateProviders()
	{
		IConfigurationElement[] primitiveExtensions = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						PALLET_PROVIDER_EXTENSION_ID);
		List<PalletItemProvider> providers = new ArrayList<PalletItemProvider>(primitiveExtensions.length);
		for (int i = 0; i < primitiveExtensions.length; i++)
		{
			String className = primitiveExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(primitiveExtensions[i]
					.getContributor().getName());
			try
			{
				@SuppressWarnings("rawtypes")
				Class providerClass = contributor.loadClass(className);
				if (!PalletItemProvider.class.isAssignableFrom(providerClass))
					throw new IllegalArgumentException(
							"The provided class is not a PalletItemProvider: "
									+ providerClass);
				PalletItemProvider providerObject = (PalletItemProvider)providerClass
						.newInstance();
				providers.add(providerObject);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
		return providers;
	}
}
