package org.eclipse.vtp.modules.attacheddata.ui.configuration;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel;
import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProvider;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowProject;

public class ConnectorPropertiesPanelProvider implements
	ComponentPropertiesPanelProvider
{

	public ConnectorPropertiesPanelProvider()
	{
	}

	public boolean isApplicableFor(IDesignComponent designComponent)
	{
		return (designComponent instanceof IDesignConnector) && (designComponent.getDesign().getDocument().getProject() instanceof IInteractiveWorkflowProject);
	}
	
	public List<ComponentPropertiesPanel> getPropertiesPanels(
		IDesignComponent designComponent)
	{
		List<ComponentPropertiesPanel> ret = new LinkedList<ComponentPropertiesPanel>();
		IDesignConnector connector = (IDesignConnector)designComponent;
        AttachedDataPropertiesPanel attachedDataPropertiesPanel = new AttachedDataPropertiesPanel(connector);
        ret.add(attachedDataPropertiesPanel);
		return ret;
	}

}
