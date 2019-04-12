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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.vtp.desktop.views.Activator;
import org.osgi.framework.Bundle;

public class PalletManager
{
	public static String palletExtensionPointId = "org.eclipse.vtp.desktop.views.pallet";
	public static String defaultPalletId = "org.eclipse.vtp.desktop.views.pallet.simple";
	private static PalletManager instance = new PalletManager();
	
	/**
	 * @return
	 */
	public static PalletManager getDefault()
	{
		return instance;
	}
	
	private Map<String, PalletRecord> primitiveTypes;
	
	public PalletManager()
	{
		super();
		primitiveTypes = new HashMap<String, PalletRecord>();
		IConfigurationElement[] primitiveExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(palletExtensionPointId);
		for(int i = 0; i < primitiveExtensions.length; i++)
		{
			String id = primitiveExtensions[i].getAttribute("id");
			String name = primitiveExtensions[i].getAttribute("name");
			String className = primitiveExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(primitiveExtensions[i].getContributor().getName());
			try
			{
				@SuppressWarnings("unchecked")
				Class<Pallet> providerClass = (Class<Pallet>) contributor.loadClass(className);
				PalletRecord palletRecord = new PalletRecord();
				palletRecord.id = id;
				palletRecord.name = name;
				palletRecord.clazz = providerClass;
				primitiveTypes.put(id, palletRecord);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return
	 */
	public List<PalletRecord> getInstalledPallets()
	{
		List<PalletRecord> ret = new LinkedList<PalletRecord>();
		ret.addAll(primitiveTypes.values());
		return ret;
	}
	
	/**
	 * @return
	 */
	public String getCurrentPallet()
	{
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        String palletId = store.getString("CurrentPallet");
        if(palletId == null)
        	return defaultPalletId;
        return palletId;
	}
	
	/**
	 * @return
	 */
	public Pallet createDefaultPallet()
	{
		try
        {
	        return primitiveTypes.get(defaultPalletId).clazz.newInstance();
        }
        catch(Exception e)
        {
	        e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * @return
	 */
	public Pallet createCurrentPallet()
	{
		try
        {
	        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	        String palletId = store.getString("CurrentPallet");
	        PalletRecord palletRecord = primitiveTypes.get(palletId);
	        if(palletRecord == null)
	        	return createDefaultPallet();
	        return palletRecord.clazz.newInstance();
        }
        catch(Exception e)
        {
	        e.printStackTrace();
        }
        return createDefaultPallet();
	}
	
	public class PalletRecord
	{
		String id;
		String name;
		Class<Pallet> clazz;
		
		/**
		 * @return
		 */
		public String getId()
        {
        	return id;
        }
		/**
		 * @return
		 */
		public String getName()
        {
        	return name;
        }
		/**
		 * @return
		 */
		public Class<Pallet> getPalletClass()
        {
        	return clazz;
        }
		
	}
}
