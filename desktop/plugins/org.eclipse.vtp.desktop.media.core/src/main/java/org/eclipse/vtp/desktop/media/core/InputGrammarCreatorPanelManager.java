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
import org.eclipse.vtp.desktop.model.interactive.core.input.InputType;
import org.osgi.framework.Bundle;

public class InputGrammarCreatorPanelManager
{
	public static final String inputCreatorPanelExtensionId = "org.eclipse.vtp.desktop.media.core.inputCreatorPanels";
	private static final InputGrammarCreatorPanelManager INSTANCE = new InputGrammarCreatorPanelManager();
	
	/**
	 * @return
	 */
	public static InputGrammarCreatorPanelManager getInstance()
	{
		return INSTANCE;
	}
	
	Map<String, ContentCreatorRecord> creatorPanels = new HashMap<String, ContentCreatorRecord>();
	List<ContentCreatorRecord> sortedPanels = new ArrayList<ContentCreatorRecord>();

	@SuppressWarnings("unchecked")
	public InputGrammarCreatorPanelManager()
	{
		IConfigurationElement[] creatorExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(inputCreatorPanelExtensionId);
		for(int i = 0; i < creatorExtensions.length; i++)
		{
			ContentCreatorRecord ccr = new ContentCreatorRecord();
			ccr.contentType = creatorExtensions[i].getAttribute("input-type");
			ccr.contentName = creatorExtensions[i].getAttribute("input-type-name");
			String className = creatorExtensions[i].getAttribute("class");
			Bundle contributor = Platform.getBundle(creatorExtensions[i].getContributor().getName());
			try
			{
				ccr.creatorClass = (Class<InputGrammarCreatorPanel>)contributor.loadClass(className);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
			creatorPanels.put(ccr.contentType, ccr);
			boolean inserted = false;
			for(int r = 0; r < sortedPanels.size(); r++)
			{
				ContentCreatorRecord record = sortedPanels.get(r);
				if(ccr.contentName.compareToIgnoreCase(record.contentName) < 0)
				{
					sortedPanels.add(r, ccr);
					inserted = true;
					break;
				}
			}
			if(!inserted)
				sortedPanels.add(ccr);
		}
	}
	
	/**
	 * @return
	 */
	public List<ContentCreatorRecord> getInputTypes()
	{
		return new ArrayList<ContentCreatorRecord>(sortedPanels);
	}
	
	/**
	 * @param contentType
	 * @return
	 */
	public InputGrammarCreatorPanel getCreatorPanel(InputType contentType)
	{
		ContentCreatorRecord ccr = creatorPanels.get(contentType.getId());
		if(ccr != null)
		{
			try
			{
				return ccr.creatorClass.newInstance();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public class ContentCreatorRecord
	{
		String contentType;
		String contentName;
		Class<InputGrammarCreatorPanel> creatorClass;
	}
}
