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
package org.eclipse.vtp.desktop.media.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.framework.interactions.core.media.IFormatter;
import org.osgi.framework.Bundle;

public class FormatterRegistrationManager
{
	public static final String formatterTypeExtensionId = "org.eclipse.vtp.framework.interactions.core.formatterTypes";
	private static final FormatterRegistrationManager INSTANCE = new FormatterRegistrationManager();
	
	public static FormatterRegistrationManager getInstance()
	{
		return INSTANCE;
	}
	
	private Map<String, List<FormatterRegistration>> formattersByInteractionType;
	private Map<String, FormatterRegistration> formattersById;

	@SuppressWarnings("unchecked")
	private FormatterRegistrationManager()
	{
		super();
		formattersByInteractionType = new HashMap<String, List<FormatterRegistration>>();
		formattersById = new HashMap<String, FormatterRegistration>();
		IConfigurationElement[] formatterExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(formatterTypeExtensionId);
		for(int i = 0; i < formatterExtensions.length; i++)
		{
			FormatterRegistration ftr = new FormatterRegistration();
			ftr.id = formatterExtensions[i].getAttribute("id");
			ftr.name = formatterExtensions[i].getAttribute("name");
			ftr.vendor = formatterExtensions[i].getAttribute("vendor-name");
			ftr.interactionType = formatterExtensions[i].getAttribute("interaction-type");
			String className = formatterExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(formatterExtensions[i].getContributor().getName());
			try
			{
				ftr.formatterClass = (Class<IFormatter>)contributor.loadClass(className);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
			List<FormatterRegistration> formatters = formattersByInteractionType.get(ftr.interactionType);
			if(formatters == null)
			{
				formatters = new ArrayList<FormatterRegistration>();
				formattersByInteractionType.put(ftr.interactionType, formatters);
			}
			formatters.add(ftr);
			formattersById.put(ftr.id, ftr);
		}
	}
	
	public List<FormatterRegistration> getFormattersForInteractionType(String interactionType)
	{
		List<FormatterRegistration> formatters = formattersByInteractionType.get(interactionType);
		if(formatters == null)
			formatters = new ArrayList<FormatterRegistration>();
		return formatters;
	}
	
	public FormatterRegistration getFormatter(String id)
	{
		FormatterRegistration ftr = formattersById.get(id);
		return ftr;
	}
}
