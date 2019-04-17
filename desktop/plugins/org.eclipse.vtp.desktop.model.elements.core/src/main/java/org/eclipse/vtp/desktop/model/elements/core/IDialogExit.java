package org.eclipse.vtp.desktop.model.elements.core;

import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;

public interface IDialogExit
{
	public static final String PROP_EXIT_TYPE = "PROP_EXIT_TYPE";

	public IDesignElementConnectionPoint.ConnectionPointType getType();
}
