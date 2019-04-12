package org.eclipse.vtp.desktop.projects.interactive.core.view;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowProject;

public class InteractiveWorkflowTest extends PropertyTester
{

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue)
	{
		if(receiver instanceof IProject)
		{
			IWorkflowProject wp = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowProject((IProject)receiver);
			if(wp == null)
				return false;
			if(wp instanceof IInteractiveWorkflowProject)
				return true;
		}
		return false;
	}

}
