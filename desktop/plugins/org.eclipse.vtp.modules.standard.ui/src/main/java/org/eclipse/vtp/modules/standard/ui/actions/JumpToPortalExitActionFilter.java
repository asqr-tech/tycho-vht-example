package org.eclipse.vtp.modules.standard.ui.actions;

import org.eclipse.vtp.desktop.editors.core.actions.DesignElementActionFilter;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;

public class JumpToPortalExitActionFilter implements DesignElementActionFilter
{

	public JumpToPortalExitActionFilter()
	{
	}

	public boolean isApplicable(IDesignElement designElement)
	{
		if(designElement instanceof PrimitiveElement)
		{
			PrimitiveElement element = (PrimitiveElement)designElement;
//			System.out.println(element.getType());
			return element.getSubTypeId().equals("org.eclipse.vtp.modules.standard.ui.portalEntry");
		}
		return false;
	}

}
