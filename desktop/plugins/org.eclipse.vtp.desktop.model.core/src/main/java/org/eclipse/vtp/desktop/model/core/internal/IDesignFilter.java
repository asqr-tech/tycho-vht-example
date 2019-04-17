package org.eclipse.vtp.desktop.model.core.internal;

import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;

public interface IDesignFilter
{
	public boolean matches(IDesignComponent component);
}
