/**
 * 
 */
package org.eclipse.vtp.modules.webservice.ui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel;
import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProvider;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;

/**
 * @author trip
 *
 */
public class SoapWebservicePropertiesPanelProvider implements
	ComponentPropertiesPanelProvider
{

	/**
	 * 
	 */
	public SoapWebservicePropertiesPanelProvider()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProvider#getPropertiesPanels(org.eclipse.vtp.desktop.model.core.design.IDesignComponent)
	 */
	public List<ComponentPropertiesPanel> getPropertiesPanels(
		IDesignComponent designComponent)
	{
		PrimitiveElement pe = (PrimitiveElement)designComponent;
		List<ComponentPropertiesPanel> ret = new ArrayList<ComponentPropertiesPanel>();
		ret.add(new SoapServiceSelectionPropertiesPanel("Service", pe));
		ret.add(new SoapInputDocumentPropertiesPanel("Input Document", pe));
		ret.add(new SoapOutputPropertiesPanel("Output Processing", pe));
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
		return pe.getSubTypeId().equals("org.eclipse.vtp.modules.webservice.soap");
		
	}

}
