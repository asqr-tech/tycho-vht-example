package org.eclipse.vtp.desktop.editors.core.internal.configuration;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel;
import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProvider;
import org.eclipse.vtp.desktop.editors.core.configuration.ConnectorPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;

public class ConnectorPropertiesPanelProvider implements
	ComponentPropertiesPanelProvider
{

	public ConnectorPropertiesPanelProvider()
	{
	}

	public boolean isApplicableFor(IDesignComponent designComponent)
	{
		return designComponent instanceof IDesignConnector;
	}
	
	public List<ComponentPropertiesPanel> getPropertiesPanels(
		IDesignComponent designComponent)
	{
		List<ComponentPropertiesPanel> ret = new LinkedList<ComponentPropertiesPanel>();
		IDesignConnector connector = (IDesignConnector)designComponent;
        ConnectorPropertiesPanel connectorPropertiesPanel = new ConnectorPropertiesPanel(connector);
        ret.add(connectorPropertiesPanel);
		return ret;
	}

}
