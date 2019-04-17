package org.eclipse.vtp.modules.standard.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.w3c.dom.Element;

public class PortalEntryConfigurationExporter implements IConfigurationExporter
{
	public PortalEntryConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
	}

	public String getActionId(IFlowElement flowElement)
	{
		return null;
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
		String uri = "http://www.eclipse.org/vtp/namespaces/config";//$NON-NLS-1$
		IFlowElement exit = flowElement.getModel().getElementsById()
				.get(((Element)flowElement.getConfiguration().getElementsByTagNameNS(uri,
						"custom-config").item(0))//$NON-NLS-1$
						.getAttribute("exit-id"));//$NON-NLS-1$
		return exit == null ? null : exit.getTargetID(afterTransitionElement);
	}
	
	public boolean isEntryPoint(IFlowElement flowElement)
	{
		return false;
	}
}
