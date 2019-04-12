package org.eclipse.vtp.desktop.export;

import org.w3c.dom.Element;

public interface IConfigurationExporter
{
	public String getActionId(IFlowElement flowElement);
	
	public void exportConfiguration(IFlowElement flowElement, Element actionElement);
	
	public String getDefaultPath(IFlowElement flowElement);
	
	public String translatePath(IFlowElement flowElement, String uiPath);
	
	public String getTargetId(IFlowElement flowElement, Element afterTransitionElement);
	
	public boolean isEntryPoint(IFlowElement flowElement);
}
