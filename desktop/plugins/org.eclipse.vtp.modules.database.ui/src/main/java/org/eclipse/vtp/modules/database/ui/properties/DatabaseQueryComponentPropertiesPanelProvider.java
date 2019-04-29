/**
 * 
 */
package org.eclipse.vtp.modules.database.ui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel;
import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProvider;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.database.ui.DatabaseQueryInformationProvider;

/**
 * @author trip
 *
 */
public class DatabaseQueryComponentPropertiesPanelProvider implements
	ComponentPropertiesPanelProvider
{

	/**
	 * 
	 */
	public DatabaseQueryComponentPropertiesPanelProvider()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProvider#getPropertiesPanels(org.eclipse.vtp.desktop.model.core.design.IDesignComponent)
	 */
	public List<ComponentPropertiesPanel> getPropertiesPanels(
		IDesignComponent designComponent)
	{
		PrimitiveElement pe = (PrimitiveElement)designComponent;
		DatabaseQueryInformationProvider info = (DatabaseQueryInformationProvider)pe.getInformationProvider();
		DatabaseQuerySettingsStructure copy =
			(DatabaseQuerySettingsStructure)info.getSettings().clone();
		List<ComponentPropertiesPanel> ret = new ArrayList<ComponentPropertiesPanel>();
		ret.add(new DatabaseQueryTargetVariablePropertiesPanel(pe, copy));
		ret.add(new DatabaseQuerySourceDatabasePropertiesPanel(pe, copy));
		ret.add(new DatabaseQueryDataMappingPropertiesPanel(pe, copy));
		ret.add(new DatabaseQuerySearchCriteriaPropertiesPanel(pe, copy));

		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProvider#isApplicableFor(org.eclipse.vtp.desktop.model.core.design.IDesignComponent)
	 */
	public boolean isApplicableFor(IDesignComponent designComponent)
	{
		if(!(designComponent instanceof PrimitiveElement))
			return false;
		PrimitiveElement pe = (PrimitiveElement)designComponent;
		return pe.getSubTypeId().equals("org.eclipse.vtp.modules.database.simplequery");
		
	}

}
