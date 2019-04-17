/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.interactive.core.configuration.generic;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * This class tracks the types of bindings registered.  The registry is a
 * singleton accessed with the getInstance() method.
 * 
 * @author trip
 */
public class BindingRegistry
{
	/**	The extension point id being tracked */
	public static final String bindingTypesExtensionId = "org.eclipse.vtp.desktop.model.interactive.core.bindingTypes";
	/**	The singleton instance */
	private static final BindingRegistry instance = new BindingRegistry();
	
	/**
	 * Provides access to the singleton object.
	 * 
	 * @return The singleton instance
	 */
	public static BindingRegistry getInstance()
	{
		return instance;
	}

	/**	A map of the binding types registered */
	private Map<String, BindingType> bindingTypes = new HashMap<String, BindingType>();
	
	/**
	 * Constructs a new registry.  The registry begins by loading the registered
	 * binding types.  The binding implementation class is stored in the map,
	 * indexed by its binding type id.
	 */
	public BindingRegistry()
	{
		super();
		IConfigurationElement[] bindingTypeExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(bindingTypesExtensionId);
		for(int i = 0; i < bindingTypeExtensions.length; i++)
		{
			String bindingTypeId = bindingTypeExtensions[i].getAttribute("id");
			String className = bindingTypeExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(bindingTypeExtensions[i].getContributor().getName());
			try
			{
				@SuppressWarnings("unchecked")
				Class<BindingItem> bindingClass = (Class<BindingItem>) contributor.loadClass(className);
				BindingType bt = new BindingType(bindingTypeId, bindingClass);
				bindingTypes.put(bindingTypeId, bt);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}

	/**
	 * Creates a new binding item instance using the class that was registered.
	 * 
	 * @param bindingType The id of the binding type
	 * @return A new binding item instance
	 */
	public BindingItem getBindingItem(String bindingType)
	{
		try
        {
	        return bindingTypes.get(bindingType).bindingItemClass.newInstance();
        }
        catch(Exception e)
        {
	        e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * A data class that stores the binding type id and the implementation
	 * class.
	 * 
	 * @author trip
	 */
	private class BindingType
	{
		/**	The id of this binding type */
		@SuppressWarnings("unused")
		String id;
		/**	The implementation class of this binding item type */
		Class<BindingItem> bindingItemClass;
		
		/**
		 * Creates a new binding type with the given id and implementation class.
		 * 
		 * @param id The id of this binding type
		 * @param bindingItemClass The implementation class of this binding type
		 */
		public BindingType(String id, Class<BindingItem> bindingItemClass)
		{
			super();
			this.id = id;
			this.bindingItemClass = bindingItemClass;
		}
	}
}
