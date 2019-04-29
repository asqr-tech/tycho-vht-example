package org.eclipse.vtp.modules.interactive.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.desktop.model.interactive.core.content.ContentLoadingManager;
import org.eclipse.vtp.framework.interactions.core.configurations.BridgeMessageConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaConfiguration;
import org.w3c.dom.Element;

public class AdvancedTransferConfigurationExporter implements IConfigurationExporter
{
	public AdvancedTransferConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		BridgeMessageConfiguration config = new BridgeMessageConfiguration(ContentLoadingManager.getInstance());
		MediaConfiguration mediaBindings = flowElement.loadMediaBindings("");
		config.setMediaConfiguration(mediaBindings);
		Element configElement = actionElement.getOwnerDocument().createElementNS(
				IDefinitionBuilder.NAMESPACE_URI_INTERACTIONS_CORE, "interactions:bridge-message"); //$NON-NLS-1$
		config.save(configElement);
		actionElement.appendChild(configElement);
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.interactions.core.actions.transfer-message";
	}

	public String getDefaultPath(IFlowElement flowElement)
	{
		return "Call Transfered";
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
