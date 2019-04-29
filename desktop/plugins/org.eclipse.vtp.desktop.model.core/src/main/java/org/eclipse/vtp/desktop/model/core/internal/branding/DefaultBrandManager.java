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
package org.eclipse.vtp.desktop.model.core.internal.branding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.branding.BrandManagerListener;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;

public class DefaultBrandManager implements BrandManager
{
	private Brand defaultBrand;
	private List<BrandManagerListener> listeners = new LinkedList<BrandManagerListener>();
	private Map<String, IBrand> brandMap = new HashMap<String, IBrand>();
	private Map<String, List<String>> supportedLanguages = new HashMap<String, List<String>>();
	private List<String> supportedInteractions = new ArrayList<String>();

	public DefaultBrandManager()
	{
		super();
	}

	public void fireBrandAdded(IBrand brand)
	{
		brandMap.put(brand.getName(), brand);
		for(BrandManagerListener listener : listeners)
		{
			try
			{
				listener.brandAdded(brand);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void fireBrandNameChanged(IBrand brand, String oldName)
	{
		for(BrandManagerListener listener : listeners)
		{
			try
			{
				listener.brandNameChanged(brand, oldName);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void fireBrandParentChanged(IBrand brand, IBrand oldParent)
	{
		for(BrandManagerListener listener : listeners)
		{
			try
			{
				listener.brandParentChanged(brand, oldParent);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void fireBrandRemoved(IBrand brand)
	{
		brandMap.remove(brand.getName());
		for(BrandManagerListener listener : listeners)
		{
			try
			{
				listener.brandRemoved(brand);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public IBrand getDefaultBrand()
	{
		return defaultBrand;
	}
	
	public void setDefaultBrand(IBrand defaultBrand)
	{
		if(this.defaultBrand != null)
			brandMap.remove(this.defaultBrand.getName());
		this.defaultBrand = (Brand)defaultBrand;
		this.defaultBrand.setManager(this);
		brandMap.put(defaultBrand.getName(), defaultBrand);
	}
	
	public void addListener(BrandManagerListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}

	public void removeListener(BrandManagerListener listener)
	{
		listeners.remove(listener);
	}

	public boolean checkBrandName(String name)
	{
		return brandMap.get(name) == null;
	}

	public Brand getBrand(String name)
	{
		return (Brand)brandMap.get(name);
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
