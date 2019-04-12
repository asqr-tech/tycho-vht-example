package org.eclipse.vtp.desktop.editors.core.actions;

import org.eclipse.vtp.desktop.model.core.design.IDesignElement;

public class AllElementsFilter implements DesignElementActionFilter
{

	public AllElementsFilter()
	{
	}

	public boolean isApplicable(IDesignElement designElement)
	{
		return true;
	}

}
