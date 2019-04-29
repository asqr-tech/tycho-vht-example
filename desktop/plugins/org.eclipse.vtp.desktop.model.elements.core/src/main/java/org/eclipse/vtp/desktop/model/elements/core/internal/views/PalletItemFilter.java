package org.eclipse.vtp.desktop.model.elements.core.internal.views;

import org.eclipse.vtp.desktop.model.core.design.IDesign;

public interface PalletItemFilter
{
	public boolean canBeContainedBy(IDesign design);
}
