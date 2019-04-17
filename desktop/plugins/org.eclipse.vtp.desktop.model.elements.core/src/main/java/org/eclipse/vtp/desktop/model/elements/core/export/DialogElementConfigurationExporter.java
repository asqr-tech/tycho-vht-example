package org.eclipse.vtp.desktop.model.elements.core.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.desktop.export.IFlowModel;
import org.w3c.dom.Element;

public class DialogElementConfigurationExporter implements IConfigurationExporter
{
	public DialogElementConfigurationExporter()
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
		return uiPath;
	}

	public String getTargetId(IFlowElement flowElement, Element afterTransitionElement)
	{
		IFlowModel dialogModel = flowElement.getModel().getDefinitionBuilder().getDialogModel(flowElement.getId());
		return dialogModel == null ? null : dialogModel.getEntries().isEmpty() ? null : dialogModel.getEntries().iterator().next().getTargetID(afterTransitionElement);
	}
	
	public boolean isEntryPoint(IFlowElement flowElement)
	{
		return false;
	}
}
