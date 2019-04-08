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
package org.eclipse.vtp.framework.interactions.core.services;

import java.util.Locale;

import org.eclipse.vtp.framework.common.IBrandSelection;
import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.core.ISessionContext;
import org.eclipse.vtp.framework.interactions.core.ILanguage;
import org.eclipse.vtp.framework.interactions.core.ILanguageRegistry;
import org.eclipse.vtp.framework.interactions.core.ILanguageSelection;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProviderRegistry;

/**
 * Implementation of {@link ILanguageSelection}.
 * 
 * @author Lonnie Pryor
 */
public class LanguageSelection implements ILanguageSelection, IScriptable
{
	/** The session context. */
	private final ISessionContext context;
	/** The language registry. */
	private final ILanguageRegistry languageRegistry;

	/**
	 * Creates a new LanguageSelection.
	 * 
	 * @param context The session context.
	 * @param languageRegistry The language registry.
	 */
	public LanguageSelection(ISessionContext context,
			ILanguageRegistry languageRegistry, IBrandSelection brandSelection,
			IMediaProviderRegistry mediaProviderRegistry)
	{
		this.context = context;
		this.languageRegistry = languageRegistry;
		String defaultLanguage = null;
		if(context.isDebugEnabled())
			context.debug("Selected Brand: " + brandSelection.getSelectedBrand().getId());
		String[] ids = languageRegistry.getLanguageIDs("org.eclipse.vtp.framework.interactions.voice.interaction");
		for(String id : ids)
		{
			if(context.isDebugEnabled())
				context.debug("Language: " + id);
			String providerId = mediaProviderRegistry.lookupMediaProviderID(brandSelection.getSelectedBrand().getId(), "org.eclipse.vtp.framework.interactions.voice.interaction", id);
			if(context.isDebugEnabled())
			{
				context.debug("Media Provider Id: " + providerId);
				context.debug(mediaProviderRegistry.getMediaProvider(providerId).getFormatter().toString());
			}
		}
		Locale locale = Locale.getDefault();
		for (int i = 0; defaultLanguage == null && i < ids.length; ++i)
			if (locale.equals(mediaProviderRegistry.getMediaProvider(mediaProviderRegistry.lookupMediaProviderID(brandSelection.getSelectedBrand().getId(), "org.eclipse.vtp.framework.interactions.voice.interaction", ids[i])).getFormatter().getLanguageCode()))
				defaultLanguage = ids[i];
		if (defaultLanguage == null)
		{
			locale = new Locale(locale.getLanguage(), locale.getCountry());
			for (int i = 0; defaultLanguage == null && i < ids.length; ++i)
			{
				if (locale.equals(mediaProviderRegistry.getMediaProvider(mediaProviderRegistry.lookupMediaProviderID(brandSelection.getSelectedBrand().getId(), "org.eclipse.vtp.framework.interactions.voice.interaction", ids[i])).getFormatter().getLanguageCode()))
					defaultLanguage = ids[i];
			}
			if (defaultLanguage == null)
			{
				locale = new Locale(locale.getLanguage());
				for (int i = 0; defaultLanguage == null && i < ids.length; ++i)
				{
					if (locale.equals(mediaProviderRegistry.getMediaProvider(mediaProviderRegistry.lookupMediaProviderID(brandSelection.getSelectedBrand().getId(), "org.eclipse.vtp.framework.interactions.voice.interaction", ids[i])).getFormatter().getLanguageCode()))
						defaultLanguage = ids[i];
				}
			}
		}
		if (defaultLanguage == null && ids.length > 0)
		{
		    defaultLanguage = ids[0];
		}
		context.setAttribute("language.default.locale", defaultLanguage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.ILanguageSelection#
	 *      getSelectedLanguage()
	 */
	public String getSelectedLanguage()
	{
		String id = (String)context.getAttribute("language.selection"); //$NON-NLS-1$
		if (id == null || id.equals(""))
		{
			String inherited = (String)context.getInheritedAttribute("language.selection");
			String[] ids = languageRegistry.getLanguageIDs("org.eclipse.vtp.framework.interactions.voice.interaction");
			for(String lid : ids)
			{
				if(lid.equals(inherited))
				{
					id = inherited;
					break;
				}
			}
		}
		if (id == null || id.equals(""))
			id = (String)context.getAttribute("language.default.user");
		if (id == null || id.equals(""))
			id = (String)context.getAttribute("language.default.locale");
		return id;
	}
	
	public void setDefaultLanguage(String languageId)
	{
		if(languageId == null)
		{
			context.clearAttribute("language.default.user");
			return;
		}
		String[] ids = languageRegistry.getLanguageIDs("org.eclipse.vtp.framework.interactions.voice.interaction");
		for(String id : ids)
		{
			if(id.equals(languageId))
			{
				context.setAttribute("language.default.user", languageId);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.ILanguageSelection#
	 *      setSelectedLanguage(java.lang.String)
	 */
	public boolean setSelectedLanguage(String languageId)
	{
		context.info("Setting language to: " + languageId);
		if (languageId == null)
		{
			context.clearAttribute("language.selection"); //$NON-NLS-1$
			return true;
		}
		String[] ids = languageRegistry.getLanguageIDs("org.eclipse.vtp.framework.interactions.voice.interaction");
		for(String id : ids)
		{
			context.info("Comparing to: " + id);
			if(id.equals(languageId))
			{
				context.setAttribute("language.selection", languageId);
				return true;
			}
		}
		return false;
	}

	public boolean clearEntry(String name)
	{
		if("value".equals(name))
		{
			setSelectedLanguage(null);
			return true;
		}
		return false;
	}

	public boolean clearItem(int index)
	{
		return false;
	}

	public Object getEntry(String name)
	{
		if("value".equals(name))
			return getSelectedLanguage();
		return null;
	}

	public String[] getFunctionNames()
	{
		return new String[] {};
	}

	public Object getItem(int index)
	{
		return null;
	}

	public String getName()
	{
		return "SelectedLanguage";
	}

	public String[] getPropertyNames()
	{
		return new String[] {"value"};
	}

	public boolean hasEntry(String name)
	{
		return "value".equals(name);
	}

	public boolean hasItem(int index)
	{
		return false;
	}

	public boolean hasValue()
	{
		return true;
	}

	public Object invokeFunction(String name, Object[] arguments)
	{
		return null;
	}

	public boolean setEntry(String name, Object value)
	{
		if("value".equals(name))
		{
			String val = "";
			if(value instanceof ILanguage)
			{
				val = ((ILanguage)value).getID();
			}
			else if(value instanceof String)
			{
				val = (String)value;
			}
			else if(value instanceof IStringObject)
			{
				val = ((IStringObject)value).getValue();
			}
			return setSelectedLanguage(val);
		}
		return false;
	}

	public boolean setItem(int index, Object value)
	{
		return false;
	}

	public Object toValue()
	{
		return getSelectedLanguage();
	}

	@Override
	public boolean isMutable()
	{
		return false;
	}
}
