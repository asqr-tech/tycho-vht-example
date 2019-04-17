package org.eclipse.vtp.desktop.model.elements.core.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.framework.common.configurations.DispatchConfiguration;
import org.eclipse.vtp.framework.common.configurations.VariableMappingConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IncludeConfigurationExporter implements IConfigurationExporter
{
	public IncludeConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
//		String uri = "http://www.eclipse.org/vtp/namespaces/config";//$NON-NLS-1$
		DispatchConfiguration config = new DispatchConfiguration();
		config.setTargetProcessURI(flowElement.getName()); //$NON-NLS-1$
		Element includeManagedConfig = null;
		NodeList managedConfigList = flowElement.getConfiguration().getElementsByTagName("managed-config");
		for (int i = 0; includeManagedConfig == null && i < managedConfigList.getLength(); ++i) {
			Element element = (Element)managedConfigList.item(i);
			if ("org.eclipse.vtp.configuration.include".equals(element.getAttribute("type")))
				includeManagedConfig = element;
		}
		if (includeManagedConfig != null) {
			// Input data.
			NodeList inputBindingList = includeManagedConfig.getElementsByTagName("input-binding");
			for (int i = 0; i < inputBindingList.getLength(); ++i) {
				Element inputBinding = (Element)inputBindingList.item(i);
				VariableMappingConfiguration vmc = new VariableMappingConfiguration();
				NodeList brandBindingList = inputBinding.getElementsByTagName("brand-binding");
				for (int j = 0; j < brandBindingList.getLength(); ++j) {
					Element brandBinding = (Element)brandBindingList.item(j);
					if (!"Default".equals(brandBinding.getAttribute("name")))
						continue;
					Element inputItem = (Element)brandBinding.getElementsByTagName("input-item").item(0);
					String type = inputItem.getAttribute("type");
					String value = inputItem.getTextContent().trim();
					if ("VARIABLE".equalsIgnoreCase(type)) //$NON-NLS-1$
						vmc.setVariableValue(value);
					else if ("EXPRESSION".equalsIgnoreCase(type)) //$NON-NLS-1$
						vmc.setExpressionValue(value, "JavaScript"); //$NON-NLS-1$
					else if ("STATIC".equalsIgnoreCase(type)) //$NON-NLS-1$
						vmc.setStaticValue(value);
					else
						vmc.setNoValue();
				}
				config.setVariableMapping(inputBinding.getAttribute("name"), vmc);
			}
			// Output data.
			NodeList exitBindingList = includeManagedConfig.getElementsByTagName("exit-binding");
			for (int i = 0; i < exitBindingList.getLength(); ++i) {
				Element exitBinding = (Element)exitBindingList.item(i);
				NodeList outputBindingList = exitBinding.getElementsByTagName("output-binding");
				for (int j = 0; j < outputBindingList.getLength(); ++j) {
					Element outputBinding = (Element)outputBindingList.item(j);
					NodeList brandBindingList = outputBinding.getElementsByTagName("brand-binding");
					for (int k = 0; k < brandBindingList.getLength(); ++k) {
						Element brandBinding = (Element)brandBindingList.item(k);
						if (!"Default".equals(brandBinding.getAttribute("name")))
							continue;
						Element outputItem = (Element)brandBinding.getElementsByTagName("output-item").item(0);
						config.setOutgoingDataValue(exitBinding.getAttribute("name"),
								outputBinding.getAttribute("name"), outputItem.getTextContent().trim());
					}
				}
				
			}
			
		}
		Element configElement = actionElement.getOwnerDocument().createElementNS(
				IDefinitionBuilder.NAMESPACE_URI_COMMON,
				"common:dispatch"); //$NON-NLS-1$
		config.save(configElement);
		actionElement.appendChild(configElement);
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.common.actions.include";
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
