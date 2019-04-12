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
package org.eclipse.vtp.desktop.model.interactive.core.internal.mediadefaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class DefaultSettingsRegistry
{
	public static final String configurationManagerExtensionId = "org.eclipse.vtp.desktop.model.interactive.core.defaultSettings";
	private static final DefaultSettingsRegistry instance = new DefaultSettingsRegistry();
	
	public static DefaultSettingsRegistry getInstance()
	{
		return instance;
	}

	List<DefaultSettingGroupRecord> managerRecords = new ArrayList<DefaultSettingGroupRecord>();
	
	public DefaultSettingsRegistry()
	{
		super();
		IConfigurationElement[] managerExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(configurationManagerExtensionId);
		for(int i = 0; i < managerExtensions.length; i++)
		{
			String elementType = managerExtensions[i].getAttribute("element-type");
			String interactionType = managerExtensions[i].getAttribute("interaction-type");
			DefaultSettingGroupRecord cmr = new DefaultSettingGroupRecord(elementType, interactionType);
			managerRecords.add(cmr);
			IConfigurationElement[] settings = managerExtensions[i].getChildren("default-setting");
			for(int s = 0; s < settings.length; s++)
			{
				for(String an : settings[s].getAttributeNames())
					System.out.println(settings[s].getName() + " " + an + " " + settings[s].getAttribute(an));
				cmr.defaultSettings.put(settings[s].getAttribute("name"), settings[s].getAttribute("default-value"));
			}
		}
	}
	
	public List<String> getDefaultSettingNames(String elementType, String interactionType)
	{
		System.err.println("Getting default settings names for: " + elementType + " " + interactionType);
		for(DefaultSettingGroupRecord dsgr : managerRecords)
		{
			System.err.println("Checking record: " + dsgr.elementType + " " + dsgr.interactionType);
			if(dsgr.elementType.equals(elementType) && dsgr.interactionType.equals(interactionType))
			{
				return new ArrayList<String>(dsgr.defaultSettings.keySet());
			}
		}
		return Collections.emptyList();
	}
	
	public List<DefaultSettingGroupRecord> getDefaultRecords()
	{
		return managerRecords;
	}
	
	public class DefaultSettingGroupRecord
	{
		public String elementType;
		public String interactionType;
		public Map<String, String> defaultSettings = new HashMap<String, String>();
		
		public DefaultSettingGroupRecord(String elementType, String interactionType)
		{
			super();
			this.elementType = elementType;
			this.interactionType = interactionType;
		}
		
	}
}
