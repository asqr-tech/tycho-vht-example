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
package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LegacyBrandManager
{
	private LegacyBrand defaultBrand;
	private Map<String, LegacyBrand> brandMap = new HashMap<String, LegacyBrand>();
	private Map<String, List<String>> supportedLanguages = new HashMap<String, List<String>>();
	private List<String> supportedInteractions = new ArrayList<String>();

	public LegacyBrandManager()
	{
		super();
	}

	public LegacyBrand getDefaultBrand()
	{
		return defaultBrand;
	}
	
	public void setDefaultBrand(LegacyBrand defaultBrand)
	{
		if(this.defaultBrand != null)
			brandMap.remove(this.defaultBrand.getName());
		this.defaultBrand = defaultBrand;
		defaultBrand.setManager(this);
		brandMap.put(defaultBrand.getName(), defaultBrand);
	}
	
	public boolean checkBrandName(String name)
	{
		return brandMap.get(name) == null;
	}

	public LegacyBrand getBrand(String name)
	{
		return brandMap.get(name);
	}

	public List<String> getSupportedInteractionTypes()
	{
		return supportedInteractions;
	}
	
	public void addInteractionSupport(String interactionType)
	{
		if(!supportedInteractions.contains(interactionType))
			supportedInteractions.add(interactionType);
	}

	public List<String> getSupportedLanguages(String interactionType)
	{
		List<String> ret = supportedLanguages.get(interactionType);
		if(ret == null)
			ret = new ArrayList<String>();
		return ret;
	}

	public void addSupportedLanguage(String interactionType, String language)
	{
		List<String> ret = supportedLanguages.get(interactionType);
		if(ret == null)
		{
			ret = new ArrayList<String>();
			supportedLanguages.put(interactionType, ret);
		}
		ret.remove(language);
		ret.add(language);
	}
}
