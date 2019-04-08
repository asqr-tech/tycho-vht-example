package org.eclipse.vtp.framework.common.configurations;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;

public class InitialConfiguration implements IConfiguration
{
	private String defaultBrandId;
	private String defaultLanguageName;

	public InitialConfiguration()
	{
	}
	
	public String getDefaultBrandId()
	{
		return defaultBrandId;
	}

	public void setDefaultBrandId(String defaultBrandId)
	{
		this.defaultBrandId = defaultBrandId;
	}

	public String getDefaultLanguageName()
	{
		return defaultLanguageName;
	}

	public void setDefaultLanguageName(String defaultLanguageName)
	{
		this.defaultLanguageName = defaultLanguageName;
	}

	@Override
	public void load(Element configurationElement)
	{
		defaultBrandId = configurationElement.getAttribute("default-brand-id");
		defaultLanguageName = configurationElement.getAttribute("default-language");
	}

	@Override
	public void save(Element configurationElement)
	{
		configurationElement.setAttribute("default-brand-id", defaultBrandId);
		configurationElement.setAttribute("default-language", defaultLanguageName);
	}

}
