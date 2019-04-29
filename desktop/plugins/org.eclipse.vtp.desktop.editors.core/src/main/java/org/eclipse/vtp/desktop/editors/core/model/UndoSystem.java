package org.eclipse.vtp.desktop.editors.core.model;

import org.eclipse.core.commands.operations.IOperationHistory;

public interface UndoSystem
{
	public IOperationHistory getOperationHistory();
	
	public void disableUndo();
	
	public void enableUndo();

}
