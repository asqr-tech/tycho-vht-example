package org.eclipse.vtp.desktop.model.interactive.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.internal.WorkflowProject;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowProject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProviderManager;
import org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.core.internal.mediadefaults.WorkspaceMediaDefaultSettings;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSetting;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSettings;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class InteractiveWorkflowProject extends WorkflowProject implements IInteractiveWorkflowProject, IMediaProviderManager
{
	private Map<String, InteractionTypeSupport> supportedInteractionTypes =
		new HashMap<String, InteractionTypeSupport>();
	private MediaDefaultSettings mediaDefaultSettings =
		new MediaDefaultSettings();
	
	/**
	 * Creates a new <code>InteractiveWorkflowProject</code> for the given eclipse
	 * project resource.
	 *
	 * @param project The underlying eclipse project resource
	 */
	public InteractiveWorkflowProject(IProject project)
	{
		super(project, false);
		loadBuildPath();
	}

	public List<String> getSupportedInteractionTypes()
	{
		return new ArrayList<String>(supportedInteractionTypes.keySet());
	}
	
	public List<String> getSupportedLanguages(String interactionType)
	{
		List<String> languages = new LinkedList<String>();
		InteractionTypeSupport interactionTypeSupport = supportedInteractionTypes.get(interactionType);
		if(interactionTypeSupport != null)
		{
			List<LanguageSupport> supportedLanguages = interactionTypeSupport.getSupportedLanguages();
			for(LanguageSupport language : supportedLanguages)
			{
				languages.add(language.getLanguage());
			}
		}
		return languages;
	}
	
	public List<InteractionTypeSupport> getInteractionTypeSupport()
	{
		List<InteractionTypeSupport> ret = new ArrayList<InteractionTypeSupport>();
		for(Map.Entry<String, InteractionTypeSupport> entry : supportedInteractionTypes.entrySet())
		{
			ret.add((InteractionTypeSupport)entry.getValue().clone());
		}
		return ret;
	}
	
	public void setInteractionTypeSupport(List<InteractionTypeSupport> newSupport)
	{
		supportedInteractionTypes.clear();
		for(InteractionTypeSupport its : newSupport)
		{
			supportedInteractionTypes.put(its.getInteractionType(), its);
		}
		this.storeBuildPath();
	}
	
	public IMediaDefaultSettings getMediaDefaultSettings()
    {
	    return mediaDefaultSettings;
    }
	
	public IMediaProviderManager getMediaProviderManager()
	{
		return this;
	}

	public IInteractiveWorkflowProject getInteractiveWorkflowProject()
	{
		return this;
	}

	public IMediaProject getMediaProject(String interactionType, IBrand brand,
			String language)
	{
		InteractionTypeSupport typeSupport = supportedInteractionTypes.get(interactionType);
		if(typeSupport != null)
		{
			LanguageSupport languageSupport = typeSupport.getLanguageSupport(language);
			if(languageSupport != null)
			{
				String mediaProjectId = languageSupport.getMediaProjectId(brand, true);
				if(mediaProjectId != null)
				{
					IMediaProject mediaProject = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().getMediaProject(mediaProjectId);
					if(mediaProject != null)
					{
						return mediaProject;
					}
				}
			}
		}
		return null;
	}

	public IMediaProvider getMediaProvider(String interactionType, IBrand brand,
			String language)
	{
		IMediaProject mediaProject = getMediaProject(interactionType, brand, language);
		if (mediaProject != null)
			return mediaProject.getMediaProvider();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowProject#loadBuildPathSection(org.w3c.dom.Element)
	 */
	protected void loadBuildPathSection(Element sectionElement)
	{
		if(sectionElement.getNodeName().equals("interaction-type-support"))
		{
			NodeList itList = sectionElement.getElementsByTagName("interaction-type");
			for(int i = 0; i < itList.getLength(); i++)
			{
				Element it = (Element)itList.item(i);
				String interactionType = it.getAttribute("id");
				String interactionTypeName = it.getAttribute("name");
				InteractionTypeSupport interactionTypeSupport = 
					supportedInteractionTypes.get(interactionType);
				if(interactionTypeSupport == null)
				{
					supportedInteractionTypes.put(interactionType, interactionTypeSupport = new InteractionTypeSupport(interactionType, interactionTypeName));
				}
				NodeList lmList = it.getElementsByTagName("language-mapping");
				for(int l = 0; l < lmList.getLength(); l++)
				{
					Element lm = (Element)lmList.item(l);
					String language = lm.getAttribute("language");
					LanguageSupport languageSupport = interactionTypeSupport.getLanguageSupport(language);
					if(languageSupport == null)
						languageSupport = interactionTypeSupport.addLanguageSupport(language);
					NodeList bmList = lm.getElementsByTagName("brand-mapping");
					for(int b = 0; b < bmList.getLength(); b++)
					{
						Element bm = (Element)bmList.item(b);
						String brandId = bm.getAttribute("brand-id");
						String mediaProjectId = bm.getAttribute("media-project-id");
						languageSupport.assignMediaProject(brandId, mediaProjectId);
					}
				}
			}
		}
		else if(sectionElement.getNodeName().equals("media-defaults"))
		{
			NodeList nl = sectionElement.getElementsByTagName("media-default");
			for(int i = 0; i < nl.getLength(); i++)
			{
				Element defaultElement = (Element)nl.item(i);
				String interactionType = defaultElement.getAttribute("interaction-type");
				String elementType = defaultElement.getAttribute("element-type");
				String settingName = defaultElement.getAttribute("name");
				String value = defaultElement.getAttribute("value");
				if(value != null && !value.equals(""))
					mediaDefaultSettings.addDefaultSetting(interactionType, elementType, settingName, value);
			}
		}
		else
			super.loadBuildPathSection(sectionElement);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowProject#writeBuildPathSections(org.w3c.dom.Element)
	 */
	protected void writeBuildPathSections(Element rootElement)
	{
		super.writeBuildPathSections(rootElement);
		Document document = rootElement.getOwnerDocument();
		Element interactionTypeSupportElement = document.createElement("interaction-type-support");
		rootElement.appendChild(interactionTypeSupportElement);
		for(InteractionTypeSupport interactionTypeSupport : supportedInteractionTypes.values())
		{
			Element interactionTypeElement = document.createElement("interaction-type");
			interactionTypeSupportElement.appendChild(interactionTypeElement);
			interactionTypeElement.setAttribute("id", interactionTypeSupport.getInteractionType());
			interactionTypeElement.setAttribute("name", interactionTypeSupport.getInteractionTypeName());
			List<LanguageSupport> supportedLanguages = interactionTypeSupport.getSupportedLanguages();
			for(LanguageSupport languageSupport : supportedLanguages)
			{
				Element languageSupportElement = document.createElement("language-mapping");
				interactionTypeElement.appendChild(languageSupportElement);
				languageSupportElement.setAttribute("language", languageSupport.getLanguage());
				for(IBrand brand : flattenBrands())
				{
					String mediaProjectId = languageSupport.getMediaProjectId(brand, false);
					if(mediaProjectId != null)
					{
						Element brandMappingElement = document.createElement("brand-mapping");
						languageSupportElement.appendChild(brandMappingElement);
						brandMappingElement.setAttribute("brand-id", brand.getId());
						brandMappingElement.setAttribute("media-project-id", mediaProjectId);
					}
				}
			}
		}
		Element mediaDefaultsElement = document.createElement("media-defaults");
		rootElement.appendChild(mediaDefaultsElement);
		for(MediaDefaultSetting mediaDefaultSetting : mediaDefaultSettings.settings.values())
		{
			if(!mediaDefaultSetting.isValueInherited() && mediaDefaultSetting.getValue() != null && !(mediaDefaultSetting.getValue().equals("")))
			{
				Element mediaDefaultElement = document.createElement("media-default");
				mediaDefaultsElement.appendChild(mediaDefaultElement);
				mediaDefaultElement.setAttribute("interaction-type", mediaDefaultSetting.getInteractionType());
				mediaDefaultElement.setAttribute("element-type", mediaDefaultSetting.getElementType());
				mediaDefaultElement.setAttribute("name", mediaDefaultSetting.getName());
				mediaDefaultElement.setAttribute("value", mediaDefaultSetting.getValue());
			}
		}
	}
	
	private List<IBrand> flattenBrands()
	{
		List<IBrand> ret = new LinkedList<IBrand>();
		BrandManager brandManager = this.getBrandManager();
		IBrand defaultBrand = brandManager.getDefaultBrand();
		flattenBrand(ret, defaultBrand);
		return ret;
	}
	
	private void flattenBrand(List<IBrand> list, IBrand brand)
	{
		list.add(brand);
		for(IBrand child : brand.getChildBrands())
		{
			flattenBrand(list, child);
		}
	}
	
	protected void initializeBuildPath()
	{
		super.initializeBuildPath();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.projects.core.IVoiceToolsDesignProject#storeMediaDefaultSettings()
	 */
	public void storeMediaDefaultSettings()
	{
		this.storeBuildPath();
	}

	
	public class MediaDefaultSettings implements IMediaDefaultSettings
	{
		public Map<String, MediaDefaultSetting> settings =
			new HashMap<String, MediaDefaultSetting>();

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSettings#getDefaultSetting(java.lang.String, java.lang.String, java.lang.String)
		 */
		public IMediaDefaultSetting getDefaultSetting(String interactionType,
                String elementType, String setting)
        {
			String key = interactionType + ":" + elementType + ":" + setting;
	        MediaDefaultSetting defaultSetting = settings.get(key);
	        if(defaultSetting == null)
	        {
	        	defaultSetting = new MediaDefaultSetting(interactionType, elementType, setting);
	        	settings.put(key, defaultSetting);
	        }
			return defaultSetting;
        }
		
		void addDefaultSetting(String interactionType, String elementType, String settingName, String value)
		{
			String key = interactionType + ":" + elementType + ":" + settingName;
			MediaDefaultSetting setting = settings.get(key);
			if(setting == null)
			{
				setting = new MediaDefaultSetting(interactionType, elementType, settingName);
				settings.put(key, setting);
			}
			setting.setValue(value);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSettings#inheritanceSupported()
		 */
		public boolean inheritanceSupported()
        {
	        return true;
        }
	}
	
	private class MediaDefaultSetting implements IMediaDefaultSetting
	{
		String interactionType = "";
		String elementType = "";
		String setting = "";
		String settingValue = "";
		boolean inherited = false;
		
		/**
		 * @param interactionType
		 * @param elementType
		 * @param setting
		 */
		public MediaDefaultSetting(String interactionType, String elementType, String setting)
		{
			super();
			this.interactionType = interactionType;
			this.elementType = elementType;
			this.setting = setting;
        	inherited = true;
        	settingValue = WorkspaceMediaDefaultSettings.getInstance().getDefaultSetting(interactionType, elementType, setting).getValue();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSetting#getElementType()
		 */
		public String getElementType()
        {
	        return elementType;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSetting#getInteractionType()
		 */
		public String getInteractionType()
        {
	        return interactionType;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSetting#getName()
		 */
		public String getName()
        {
	        return setting;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSetting#getValue()
		 */
		public String getValue()
        {
	        return settingValue;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSetting#isValueInherited()
		 */
		public boolean isValueInherited()
        {
	        return inherited;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSetting#setValue(java.lang.String)
		 */
		public void setValue(String value)
        {
			if(inherited)
			{
				if(value != null && !value.equals(""))
				{
					inherited = false;
					settingValue = value;
				}
			}
			else
			{
				if(value == null || value.equals(""))
				{
					inherited = true;
					settingValue = WorkspaceMediaDefaultSettings.getInstance().getDefaultSetting(interactionType, elementType, setting).getValue();
				}
				else
				{
					settingValue = value;
				}
			}
        }
		
	}

}
