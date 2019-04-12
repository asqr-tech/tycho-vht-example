package org.eclipse.vtp.modules.standard.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.w3c.dom.Element;

public class DialogEntryConfigurationExporter implements IConfigurationExporter
{
	public DialogEntryConfigurationExporter()
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
		return "Continue";
	}
	
	public String translatePath(IFlowElement flowElement, String uiPath)
	{
		return uiPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.projects.core.export.DefinitionBuilder.
	 *      FlowElement#getTargetID(org.w3c.dom.Element)
	 */
	public String getTargetId(IFlowElement flowElement, Element afterTransitionElement)
	{
		flowElement.buildObservers("Continue", afterTransitionElement); //$NON-NLS-1$
		IFlowElement next = flowElement.getResultPath("Continue"); //$NON-NLS-1$
		return next == null ? null : next.getTargetID(afterTransitionElement);
	}
	
	public boolean isEntryPoint(IFlowElement flowElement)
	{
		return true;
	}
}
