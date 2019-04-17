package org.eclipse.vtp.modules.standard.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.desktop.export.IFlowModel;
import org.w3c.dom.Element;

public class DialogExitConfigurationExporter implements IConfigurationExporter
{
	public DialogExitConfigurationExporter()
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
		IFlowModel mainModel = flowElement.getModel().getDefinitionBuilder().getMainModel();
		if(mainModel == null)
			return null;
		IFlowElement dialog = mainModel.getElementsById().get(flowElement.getProperties().getProperty("DIALOG_ID"));
		if (dialog == null)
			return null;
		String path = flowElement.getName(); //$NON-NLS-1$
		IFlowElement next = dialog.getResultPath(path);
		if (next == null)
			return null;
		dialog.buildObservers(path, afterTransitionElement);
		return next.getTargetID(afterTransitionElement);
	}
	
	public boolean isEntryPoint(IFlowElement flowElement)
	{
		return false;
	}
}
