package org.eclipse.vtp.modules.webservice.ui.export;

import java.util.List;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.eclipse.vtp.framework.webservices.configurations.WebServiceConfiguration;
import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceBindingManager;
import org.w3c.dom.Element;

public class WebserviceConfigurationExporter implements IConfigurationExporter
{
	public WebserviceConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		Element baseConfigurationElement = flowElement.getConfiguration();
		WebServiceConfiguration wsconfig = new WebServiceConfiguration();
		List<Element> managedConfigurationElementList = XMLUtilities.getElementsByTagName(baseConfigurationElement, "managed-config", true);
		for(Element managedConfigurationElement : managedConfigurationElementList)
		{
			if(WebserviceBindingManager.TYPE_ID.equals(managedConfigurationElement.getAttribute("type")))
			{
				List<Element> serviceBindingElementList = XMLUtilities.getElementsByTagName(managedConfigurationElement, "service-binding", true);
				if(serviceBindingElementList.size() > 0)
				{
					wsconfig.setServiceType(WebServiceConfiguration.SOAP);
					Element serviceBindingElement = serviceBindingElementList.get(0);
					wsconfig.setURL("static", serviceBindingElement.getAttribute("url"));
					wsconfig.setSoapAction("static", serviceBindingElement.getAttribute("soap-action"));
				}
				List<Element> documentStructureElementList = XMLUtilities.getElementsByTagName(managedConfigurationElement, "input-document-structure", true);
				if(documentStructureElementList.size() > 0)
				{
					Element inputStructureElement = documentStructureElementList.get(0);
					wsconfig.getInputStructure().readConfiguration(inputStructureElement);
				}
				List<Element> outputElementList = XMLUtilities.getElementsByTagName(managedConfigurationElement, "output-binding", true);
				if(outputElementList.size() > 0)
				{
					Element outputElement = outputElementList.get(0);
					wsconfig.setVariableName(outputElement.getAttribute("variable-name"));
					wsconfig.setShouldProcess("true".equals(outputElement.getAttribute("should-process")));
					wsconfig.setOutputScriptText(XMLUtilities.getElementTextDataNoEx(outputElement, true));
				}
			}
		}
		Element configElement = actionElement.getOwnerDocument().createElementNS(
				IDefinitionBuilder.NAMESPACE_URI_WEBSERVICES, "webservice:call"); //$NON-NLS-1$
		wsconfig.save(configElement);
		actionElement.appendChild(configElement);
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.webservices.actions.webservice-call";
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
