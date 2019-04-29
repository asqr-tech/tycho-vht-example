package org.eclipse.vtp.modules.interactive.ui.export;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.desktop.model.interactive.core.content.ContentLoadingManager;
import org.eclipse.vtp.desktop.model.interactive.core.input.InputLoadingManager;
import org.eclipse.vtp.framework.interactions.core.configurations.InputConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.PropertyConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.SelectionChoiceConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.SelectionRequestConfiguration;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class OptionSetConfigurationExporter implements IConfigurationExporter
{
	private static final String ELEMENT_ID = "org.eclipse.vtp.modules.interactive.optionSet";

	public OptionSetConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		SelectionRequestConfiguration config = new SelectionRequestConfiguration(
				ContentLoadingManager.getInstance(), InputLoadingManager
						.getInstance());
		config.setOutputName("Prompt"); //$NON-NLS-1$
		MediaConfiguration media = flowElement.loadMediaBindings(ELEMENT_ID);
		Element customConfig = (Element)flowElement.getConfiguration().getElementsByTagNameNS(
			"http://www.eclipse.org/vtp/namespaces/config", //$NON-NLS-1$
			"custom-config").item(0); //$NON-NLS-1$
		String variableName = customConfig.getAttribute("variable-name");
		config.setDataName(variableName); //$NON-NLS-1$
		config.setSecured(Boolean.parseBoolean(customConfig.getAttribute("secured")));
		List<Element> legacyChoicesElement = XMLUtilities.getElementsByTagName(customConfig, "choices", true);
		if(legacyChoicesElement.size() > 0) //legacy conversion
		{
			List<Element> choiceElements = XMLUtilities.getElementsByTagName(legacyChoicesElement.get(0), "choice", true);
			for (Element choiceElement : choiceElements)
			{
				String name = choiceElement.getAttribute("name"); //$NON-NLS-1$
				String script = null;
				if (choiceElement.hasAttribute("script")) //$NON-NLS-1$
					script = choiceElement.getAttribute("script"); //$NON-NLS-1$
				String prefix = name + "-";
				MediaConfiguration choiceMedia = new MediaConfiguration(
					ContentLoadingManager.getInstance(), InputLoadingManager
					.getInstance());
				String[] propertyNames = media.getPropertyConfigurationNames();
				for (int j = 0; j < propertyNames.length; ++j)
				{
					if (!propertyNames[j].startsWith(prefix))
						continue;
					PropertyConfiguration property = media
					.getPropertyConfiguration(propertyNames[j]);
					media.setPropertyConfiguration(propertyNames[j], null);
					choiceMedia.setPropertyConfiguration(propertyNames[j]
					                                                   .substring(prefix.length()), property);
				}
				String[] outputNames = media.getOutputConfigurationNames();
				for (int j = 0; j < outputNames.length; ++j)
				{
					if (!outputNames[j].startsWith(prefix))
						continue;
					OutputConfiguration output = media
					.getOutputConfiguration(outputNames[j]);
					media.setOutputConfiguration(outputNames[j], null);
					choiceMedia.setOutputConfiguration(outputNames[j].substring(prefix
						.length()), output);
				}
				String[] inputNames = media.getInputConfigurationNames();
				for (int j = 0; j < inputNames.length; ++j)
				{
					if (!inputNames[j].startsWith(prefix))
						continue;
					InputConfiguration input = media.getInputConfiguration(inputNames[j]);
					media.setInputConfiguration(inputNames[j], null);
					choiceMedia.setInputConfiguration(inputNames[j].substring(prefix
						.length()), input);
				}
				SelectionChoiceConfiguration choice = new SelectionChoiceConfiguration(
					ContentLoadingManager.getInstance(), InputLoadingManager
					.getInstance());
				choice.setName(name);
				choice.setOutputName("prompt"); //$NON-NLS-1$
				choice.setInputName("grammar"); //$NON-NLS-1$
				if (script != null)
				{
					choice.setScriptingLanguage("JavaScript"); //$NON-NLS-1$
					choice.setScript(script);
				}
				choice.setMediaConfiguration(choiceMedia);
				config.addChoice(choice);
			}
			config.setMediaConfiguration(media);
			List<Element> brandOrderElements = XMLUtilities.getElementsByTagName(customConfig, "brand-order", true);
			Map<Integer, String> entries = new TreeMap<Integer, String>();
			for (Element brandOrder : brandOrderElements)
			{
				String brand = brandOrder.getAttribute("brand"); //$NON-NLS-1$
				NodeList entryElements = brandOrder.getElementsByTagNameNS(brandOrder
					.getNamespaceURI(), "entry"); //$NON-NLS-1$
				for (int j = 0; j < entryElements.getLength(); ++j)
				{
					Element entryElement = (Element)entryElements.item(j);
					entries.put(new Integer(entryElement.getAttribute("spot")), //$NON-NLS-1$
						entryElement.getAttribute("name")); //$NON-NLS-1$
				}
				config.setBrandedChoices(brand, entries.values().toArray(
					new String[entries.size()]));
				entries.clear();
			}
		}
		else
		{
			List<Element> managedConfigs = XMLUtilities.getElementsByTagName(flowElement.getConfiguration(), "managed-config", true);
			System.err.println("# of managed configs: " + managedConfigs.size());
			for(Element element : managedConfigs)
			{
				System.err.println("type: " + element.getAttribute("type"));
				if(!"org.eclipse.vtp.configuration.menuchoice".equals(element.getAttribute("type")))
					continue;
				Element managedConfig = element;
				List<Element> choicesElements = XMLUtilities.getElementsByTagName(managedConfig, "choices", true);
				if(choicesElements.size() == 0)
					continue;
				List<Element> choiceElements = XMLUtilities.getElementsByTagName(choicesElements.get(0), "choice", true);
				for (Element choiceElement : choiceElements)
				{
					String name = choiceElement.getAttribute("name"); //$NON-NLS-1$
					String script = null;
					if (choiceElement.hasAttribute("script")) //$NON-NLS-1$
						script = choiceElement.getAttribute("script"); //$NON-NLS-1$
					String prefix = name + "-";
					MediaConfiguration choiceMedia = new MediaConfiguration(
						ContentLoadingManager.getInstance(), InputLoadingManager
						.getInstance());
					String[] propertyNames = media.getPropertyConfigurationNames();
					for (int j = 0; j < propertyNames.length; ++j)
					{
						if (!propertyNames[j].startsWith(prefix))
							continue;
						PropertyConfiguration property = media
						.getPropertyConfiguration(propertyNames[j]);
						media.setPropertyConfiguration(propertyNames[j], null);
						choiceMedia.setPropertyConfiguration(propertyNames[j]
						                                                   .substring(prefix.length()), property);
					}
					String[] outputNames = media.getOutputConfigurationNames();
					for (int j = 0; j < outputNames.length; ++j)
					{
						if (!outputNames[j].startsWith(prefix))
							continue;
						OutputConfiguration output = media
						.getOutputConfiguration(outputNames[j]);
						media.setOutputConfiguration(outputNames[j], null);
						choiceMedia.setOutputConfiguration(outputNames[j].substring(prefix
							.length()), output);
					}
					String[] inputNames = media.getInputConfigurationNames();
					for (int j = 0; j < inputNames.length; ++j)
					{
						if (!inputNames[j].startsWith(prefix))
							continue;
						InputConfiguration input = media.getInputConfiguration(inputNames[j]);
						media.setInputConfiguration(inputNames[j], null);
						choiceMedia.setInputConfiguration(inputNames[j].substring(prefix
							.length()), input);
					}
					SelectionChoiceConfiguration choice = new SelectionChoiceConfiguration(
						ContentLoadingManager.getInstance(), InputLoadingManager
						.getInstance());
					choice.setName(name);
					choice.setOutputName("prompt"); //$NON-NLS-1$
					choice.setInputName("grammar"); //$NON-NLS-1$
					if (script != null)
					{
						choice.setScriptingLanguage("JavaScript"); //$NON-NLS-1$
						choice.setScript(script);
					}
					choice.setMediaConfiguration(choiceMedia);
					config.addChoice(choice);
				}
				config.setMediaConfiguration(media);
				List<Element> brandOrderElements = XMLUtilities.getElementsByTagName(managedConfig, "brand-order", true);
				Map<Integer, String> entries = new TreeMap<Integer, String>();
				for (Element brandOrder : brandOrderElements)
				{
					String brand = brandOrder.getAttribute("brand"); //$NON-NLS-1$
					NodeList entryElements = brandOrder.getElementsByTagNameNS(brandOrder
						.getNamespaceURI(), "entry"); //$NON-NLS-1$
					for (int j = 0; j < entryElements.getLength(); ++j)
					{
						Element entryElement = (Element)entryElements.item(j);
						entries.put(new Integer(entryElement.getAttribute("spot")), //$NON-NLS-1$
							entryElement.getAttribute("name")); //$NON-NLS-1$
					}
					config.setBrandedChoices(brand, entries.values().toArray(
						new String[entries.size()]));
					entries.clear();
				}
			}
		}
		Element configElement = actionElement.getOwnerDocument().createElementNS(
				IDefinitionBuilder.NAMESPACE_URI_INTERACTIONS_CORE, "interactions:selection-request"); //$NON-NLS-1$
		config.save(configElement);
		actionElement.appendChild(configElement);
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.interactions.core.actions.selection-request";
	}

	public String getDefaultPath(IFlowElement flowElement)
	{
		return null;
	}
	
	public String translatePath(IFlowElement flowElement, String uiPath)
	{
		return uiPath;
	}

	public String getTargetId(IFlowElement flowElement, Element afterTransitionElement)
	{
		return flowElement.getDefaultTargetId(afterTransitionElement);
	}
	
	public boolean isEntryPoint(IFlowElement flowElement)
	{
		return false;
	}
}
