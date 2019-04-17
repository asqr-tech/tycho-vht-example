package org.eclipse.vtp.desktop.model.interactive.core.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.branding.IBrand;

public class LanguageSupport implements Cloneable
{
	private String language = null;
	private Map<String, String> projectMappings =
		new HashMap<String, String>();
	
	public LanguageSupport(String language)
	{
		this.language = language;
	}
	
	public String getLanguage()
	{
		return language;
	}

	public String getMediaProjectId(IBrand brand)
	{
		return getMediaProjectId(brand, true);
	}
	
	public String getMediaProjectId(IBrand brand, boolean inherit)
	{
		String key = brand.getId();
		String projectId = projectMappings.get(key);
		if(inherit && projectId == null)
		{
			if(brand.getParent() != null)
				return getMediaProjectId(brand.getParent());
		}
		return projectId;
	}
	
	public void assignMediaProject(String brandId, String mediaProjectId)
	{
		String key = brandId;
		projectMappings.put(key, mediaProjectId);
	}
	
	public Object clone()
	{
		LanguageSupport copy = new LanguageSupport(language);
		for(Map.Entry<String, String> entry : projectMappings.entrySet())
		{
			copy.assignMediaProject(entry.getKey(), entry.getValue());
		}
		return copy;
	}
}

