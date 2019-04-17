package org.eclipse.vtp.modules.standard.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.w3c.dom.Element;

public class BroadcastReceiverConfigurationExporter implements IConfigurationExporter
{
	public BroadcastReceiverConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.common.actions.broadcastreceiver";
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
