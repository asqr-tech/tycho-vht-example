package org.eclipse.vtp.modules.interactive.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.framework.interactions.core.configurations.ExternalReferenceConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SubdialogConfigurationExporter implements IConfigurationExporter
{
	public SubdialogConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		ExternalReferenceConfiguration config = new ExternalReferenceConfiguration();
		config.setName(flowElement.getName());
		String uri = "http://www.eclipse.org/vtp/namespaces/config"; //$NON-NLS-1$
		Element custom = (Element)flowElement.getConfiguration().getElementsByTagNameNS(uri,
				"custom-config").item(0); //$NON-NLS-1$
		config.setUrl(custom.getAttribute("url"));
		NodeList list = ((Element)custom.getElementsByTagNameNS(uri, "inputs")
				.item(0)).getElementsByTagNameNS(uri, "input");
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element elem = (Element)list.item(i);
			config.setInput(elem.getAttribute("name"), "1".equals(elem
					.getAttribute("type")), elem.getAttribute("value"));
		}
		list = ((Element)custom.getElementsByTagNameNS(uri, "params")
				.item(0)).getElementsByTagNameNS(uri, "param");
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element elem = (Element)list.item(i);
			config.setURLParameter(elem.getAttribute("name"), "1".equals(elem
					.getAttribute("type")), elem.getAttribute("value"));
		}
		list = ((Element)custom.getElementsByTagNameNS(uri, "outputs").item(0))
				.getElementsByTagNameNS(uri, "output");
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element elem = (Element)list.item(i);
			config.setOutput(elem.getAttribute("name"), elem.getAttribute("value"));
		}
		Element configElement = actionElement.getOwnerDocument().createElementNS(
				IDefinitionBuilder.NAMESPACE_URI_INTERACTIONS_CORE, "interactions:external-reference"); //$NON-NLS-1$
		config.save(configElement);
		actionElement.appendChild(configElement);
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.interactions.core.actions.external-reference";
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
