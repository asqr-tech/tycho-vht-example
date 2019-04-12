package org.eclipse.vtp.desktop.model.interactive.core.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InteractionTypeSupport implements Cloneable
{
	private String interactionType = null;
	private String interactionTypeName = null;
	private Map<String, LanguageSupport> languageMappings =
		new HashMap<String, LanguageSupport>();
	
	public InteractionTypeSupport(String interactionType, String interactionTypeName)
	{
		super();
		this.interactionType = interactionType;
		this.interactionTypeName = interactionTypeName;
	}
	
	public String getInteractionType()
	{
		return interactionType;
	}
	
	public String getInteractionTypeName()
	{
		return interactionTypeName;
	}
	
	public LanguageSupport addLanguageSupport(String language)
	{
		LanguageSupport ls = languageMappings.get(language);
		if(ls == null) //language not already registered
		{
			languageMappings.put(language, ls = new LanguageSupport(language));
		}
		return ls;
	}
	
	private void addLanguageSupport0(String language, LanguageSupport languageMapping)
	{
		languageMappings.put(language, languageMapping);
	}
	
	public void removeLanguageSupport(String language)
	{
		languageMappings.remove(language);
	}
	
	public LanguageSupport getLanguageSupport(String language)
	{
		return languageMappings.get(language);
	}
	
	public List<LanguageSupport> getSupportedLanguages()
	{
		return new LinkedList<LanguageSupport>(languageMappings.values());
	}
	
	public Object clone()
	{
		InteractionTypeSupport copy = new InteractionTypeSupport(interactionType, interactionTypeName);
		for(Map.Entry<String, LanguageSupport> entry : languageMappings.entrySet())
		{
			copy.addLanguageSupport0(entry.getKey(), (LanguageSupport)entry.getValue().clone());
		}
		return copy;
	}
}
