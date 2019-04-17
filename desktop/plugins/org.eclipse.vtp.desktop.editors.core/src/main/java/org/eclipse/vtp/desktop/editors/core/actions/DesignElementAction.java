package org.eclipse.vtp.desktop.editors.core.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.vtp.desktop.editors.themes.core.commands.CommandListener;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;

public class DesignElementAction extends Action
{
	IDesignElement element = null;
	CommandListener commandListener = null;
	
	public DesignElementAction(IDesignElement element, CommandListener commandListener)
	{
		super();
		this.element = element;
		this.commandListener = commandListener;
	}
	
	public IDesignElement getElement()
	{
		return element;
	}
	
	public CommandListener getCommandListener()
	{
		return commandListener;
	}

}
