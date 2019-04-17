package org.eclipse.vtp.desktop.editors.core.configuration;

import java.util.List;

import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;

public interface ComponentPropertiesPanelProvider
{
	public boolean isApplicableFor(IDesignComponent designComponent);
	
	public List<ComponentPropertiesPanel> getPropertiesPanels(IDesignComponent designComponent);
}
