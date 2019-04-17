package org.eclipse.vtp.desktop.model.core.internal.design;

import org.eclipse.vtp.desktop.model.core.design.IExitBroadcastReceiver;

public class ExitBroadcastReceiver implements IExitBroadcastReceiver
{
	private String exitPattern = null;

	public ExitBroadcastReceiver(String exitPattern)
	{
		super();
		this.exitPattern = exitPattern;
	}

	@Override
	public String getExitPattern()
	{
		return exitPattern;
	}

}
