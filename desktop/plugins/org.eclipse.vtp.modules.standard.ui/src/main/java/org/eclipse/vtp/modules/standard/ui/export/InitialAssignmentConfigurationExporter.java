package org.eclipse.vtp.modules.standard.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.desktop.model.elements.core.export.ConfigurationExportHelper;
import org.eclipse.vtp.framework.common.configurations.InitialConfiguration;
import org.w3c.dom.Element;

public class InitialAssignmentConfigurationExporter implements IConfigurationExporter
{
	public InitialAssignmentConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		InitialConfiguration ic = new InitialConfiguration();
		String uri = "http://www.eclipse.org/vtp/namespaces/config";//$NON-NLS-1$
		Element configElement = (Element)flowElement.getConfiguration().getElementsByTagNameNS(uri, "custom-config").item(0);
		ic.setDefaultBrandId(configElement.getAttribute("default-brand"));
		ic.setDefaultLanguageName(configElement.getAttribute("default-language"));
		Element icElement = actionElement.getOwnerDocument().createElementNS(IDefinitionBuilder.NAMESPACE_URI_COMMON,
				"common:initial"); //$NON-NLS-1$
		ic.save(icElement);
		actionElement.appendChild(icElement);
		ConfigurationExportHelper.configureAssignmentAction(flowElement.getConfiguration(), actionElement);
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.interactions.core.actions.initial";
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
		return true;
	}
}
