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
package org.eclipse.vtp.desktop.model.elements.core.internal;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.model.elements.core.Activator;
import org.osgi.framework.Bundle;

public class DialogElementManager
{
	public static String dialogExtensionPointId = "org.eclipse.vtp.desktop.model.elements.core.dialogElement";
	private static DialogElementManager instance = new DialogElementManager();
	
	public static DialogElementManager getDefault()
	{
		return instance;
	}
	
	private Map<String, DialogElementRecord> dialogTypes;
	
	public DialogElementManager()
	{
		super();
		dialogTypes = new HashMap<String, DialogElementRecord>();
		IConfigurationElement[] primitiveExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(dialogExtensionPointId);
		for(int i = 0; i < primitiveExtensions.length; i++)
		{
			DialogElementRecord der = new DialogElementRecord();
			der.id = primitiveExtensions[i].getAttribute("id");
			der.name = primitiveExtensions[i].getAttribute("name");
			Bundle contributor = Platform.getBundle(primitiveExtensions[i].getContributor().getName());
			der.resourceURL = contributor.getResource(primitiveExtensions[i].getAttribute("template"));
			dialogTypes.put(der.id, der);
		}
	}
	
	public String getName(String id)
	{
		DialogElementRecord der = dialogTypes.get(id);
		if(der != null)
			return der.name;
		else
		{
			Activator.LocalDialogRecord record = Activator.getDefault().getLocalDialog(id);
			if(record != null)
				return record.getName();
		}
		return null;
	}
	
	public URL getTemplate(String id)
	{
		DialogElementRecord der = dialogTypes.get(id);
		if(der != null)
			return der.resourceURL;
		else
		{
			Activator.LocalDialogRecord record = Activator.getDefault().getLocalDialog(id);
			if(record != null)
				return record.getTemplateURL();
		}
		return null;
	}

	private class DialogElementRecord
	{
		String id;
		String name;
		URL resourceURL;
	}
}
