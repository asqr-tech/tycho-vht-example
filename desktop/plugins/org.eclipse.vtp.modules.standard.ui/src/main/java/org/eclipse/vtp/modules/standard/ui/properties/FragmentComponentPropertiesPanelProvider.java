/**
 * 
 */
package org.eclipse.vtp.modules.standard.ui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel;
import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProvider;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.elements.core.internal.ApplicationFragmentElement;

/**
 * @author trip
 *
 */
public class FragmentComponentPropertiesPanelProvider implements
	ComponentPropertiesPanelProvider
{

	/**
	 * 
	 */
	public FragmentComponentPropertiesPanelProvider()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProvider#getPropertiesPanels(org.eclipse.vtp.desktop.model.core.design.IDesignComponent)
	 */
	public List<ComponentPropertiesPanel> getPropertiesPanels(
		IDesignComponent designComponent)
	{
		ApplicationFragmentElement pe = (ApplicationFragmentElement)designComponent;
		List<ComponentPropertiesPanel> ret = new ArrayList<ComponentPropertiesPanel>();
		if(pe.isModelPresent())
		{
			ret.add(new FragmentEntrySelectionPropertiesPanel("Workflow Entry", pe));
			ret.add(new FragmentVariableMappingPanel("Input Parameters", pe));
			ret.add(new FragmentOutputMappingPanel("Output Mapping", pe));
		}
		else
		{
			ret.add(new FragmentMissingModelPanel("Warning", pe));
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProvider#isApplicableFor(org.eclipse.vtp.desktop.model.core.design.IDesignComponent)
	 */
	public boolean isApplicableFor(IDesignComponent designComponent)
	{
		return designComponent instanceof ApplicationFragmentElement;
	}

}
