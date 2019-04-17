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
package org.eclipse.vtp.desktop.model.core.internal.design;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class ElementManager
{
	public static final String elementTypeExtensionId = "org.eclipse.vtp.desktop.model.core.elementType";
	private static final ElementManager INSTANCE = new ElementManager();
	
	/**
	 * @return
	 */
	public static ElementManager getInstance()
	{
		return INSTANCE;
	}
	
	private Map<String, ReaderRecord> elementTypes;

	@SuppressWarnings("unchecked")
	public ElementManager()
	{
		super();
		elementTypes = new HashMap<String, ReaderRecord>();
		IConfigurationElement[] primitiveExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(elementTypeExtensionId);
		for(int i = 0; i < primitiveExtensions.length; i++)
		{
			ReaderRecord rr = new ReaderRecord();
			rr.type = primitiveExtensions[i].getAttribute("id");
			String className = primitiveExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(primitiveExtensions[i].getContributor().getName());
			try
			{
				rr.elementClass = (Class<DesignElement>)contributor.loadClass(className);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
			elementTypes.put(rr.type, rr);
		}
	}
	
	/**
	 * @param elementTypeId
	 * @param id
	 * @param name
	 * @param properties
	 * @return
	 */
	public DesignElement loadElement(String elementTypeId, String id, String name, Properties properties)
	{
		ReaderRecord rr = elementTypes.get(elementTypeId);
		try
		{
			Constructor<DesignElement> constructor = rr.elementClass.getConstructor(new Class[] { String.class, String.class, Properties.class});
			return constructor.newInstance(new Object[] { id, name, properties});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private class ReaderRecord
	{
		String type;
		Class<DesignElement> elementClass;
	}
}
