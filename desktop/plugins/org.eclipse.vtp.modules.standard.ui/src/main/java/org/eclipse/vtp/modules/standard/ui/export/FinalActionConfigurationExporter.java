package org.eclipse.vtp.modules.standard.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.eclipse.vtp.framework.common.configurations.ExitConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FinalActionConfigurationExporter implements IConfigurationExporter
{
	public FinalActionConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		ExitConfiguration config = new ExitConfiguration();
		config.setValue(flowElement.getName());//$NON-NLS-1$
		Element configElement = actionElement.getOwnerDocument().createElementNS(
				IDefinitionBuilder.NAMESPACE_URI_COMMON,
				"common:exit"); //$NON-NLS-1$
		config.save(configElement);
		actionElement.appendChild(configElement);
		NodeList list = flowElement.getConfiguration().getElementsByTagNameNS(
				"http://www.eclipse.org/vtp/namespaces/config", "custom-config");
		if (list.getLength() > 0)
		{
			Element custom = (Element)list.item(0);
			list = custom.getElementsByTagName("export");
			for (int i = 0; i < list.getLength(); ++i)
			{
				String name = ((Element)list.item(i)).getAttribute("name");
				if (name != null && name.length() > 0)
				{
					AssignmentConfiguration assign = new AssignmentConfiguration();
					assign.setName(name);
					configElement = actionElement.getOwnerDocument().createElementNS(
							IDefinitionBuilder.NAMESPACE_URI_COMMON,
							"common:assignment"); //$NON-NLS-1$
					assign.save(configElement);
					actionElement.appendChild(configElement);
				}
			}
		}
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.interactions.core.actions.final";
	}

	public String getDefaultPath(IFlowElement flowElement)
	{
		return null;
	}
	
	public String translatePath(IFlowElement flowElement, String uiPath)
	{
		return null;
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
