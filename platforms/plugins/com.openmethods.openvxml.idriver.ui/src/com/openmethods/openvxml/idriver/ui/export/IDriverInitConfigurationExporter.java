package com.openmethods.openvxml.idriver.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.w3c.dom.Element;

import com.openmethods.openvxml.idriver.runtime.configuration.IDriverConfiguration;

public class IDriverInitConfigurationExporter implements IConfigurationExporter
{
	public IDriverInitConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		String uri = "http://www.eclipse.org/vtp/namespaces/config";//$NON-NLS-1$
		IDriverConfiguration config = new IDriverConfiguration();
		Element customConfig = (Element)flowElement.getConfiguration().getElementsByTagNameNS(
				"http://www.eclipse.org/vtp/namespaces/config", //$NON-NLS-1$
				"custom-config").item(0); //$NON-NLS-1$
		Element subElement = (Element)customConfig.getElementsByTagNameNS(
				uri, "idriver-init").item(0);
		config.setCallIdVariable(subElement.getAttribute("call-id-variable"));//$NON-NLS-1$
		config.setConnIdVariable(subElement.getAttribute("conn-id-variable"));//$NON-NLS-1$
		config.setPortVariable(subElement.getAttribute("port-variable"));//$NON-NLS-1$
		Element configElement = actionElement.getOwnerDocument().createElementNS(
				IDriverConfiguration.NAMESPACE_URI,	"idriver:idriver-config"); //$NON-NLS-1$
		config.save(configElement);
		actionElement.appendChild(configElement);
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "com.openmethods.openvxml.idriver.actions.idriverinit";
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
