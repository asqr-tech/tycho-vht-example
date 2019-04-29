package org.eclipse.vtp.desktop.editors.core.actions;

import org.eclipse.vtp.desktop.model.core.design.IDesignElement;

public interface DesignElementActionFilter
{
	public boolean isApplicable(IDesignElement designElement);
}
