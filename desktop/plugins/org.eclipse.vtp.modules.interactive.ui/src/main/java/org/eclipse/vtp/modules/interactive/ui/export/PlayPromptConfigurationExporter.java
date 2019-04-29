package org.eclipse.vtp.modules.interactive.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.desktop.model.interactive.core.content.ContentLoadingManager;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputMessageConfiguration;
import org.w3c.dom.Element;

public class PlayPromptConfigurationExporter implements IConfigurationExporter
{
	private static final String ELEMENT_ID = "org.eclipse.vtp.modules.interactive.playPrompt";

	public PlayPromptConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		OutputMessageConfiguration config = new OutputMessageConfiguration(
				ContentLoadingManager.getInstance());
		Element customConfig = (Element)flowElement.getConfiguration().getElementsByTagNameNS(
				"http://www.eclipse.org/vtp/namespaces/config", //$NON-NLS-1$
				"custom-config").item(0); //$NON-NLS-1$
		config.setSecured(Boolean.parseBoolean(customConfig.getAttribute("secured")));
		config.setOutputName("Prompt"); //$NON-NLS-1$
		config.setMediaConfiguration(flowElement.loadMediaBindings(ELEMENT_ID));
		Element configElement = actionElement.getOwnerDocument().createElementNS(
				IDefinitionBuilder.NAMESPACE_URI_INTERACTIONS_CORE, "interactions:output-message"); //$NON-NLS-1$
		config.save(configElement);
		actionElement.appendChild(configElement);
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.interactions.core.actions.output-message";
	}

	public String getDefaultPath(IFlowElement flowElement)
	{
		return "Continue";
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
