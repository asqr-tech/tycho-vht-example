package org.eclipse.vtp.desktop.model.core.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;

public class IsWorkflowProjectPropertyTester extends PropertyTester
{

	public IsWorkflowProjectPropertyTester()
	{
	}

	public boolean test(Object receiver, String property, Object[] args,
		Object expectedValue)
	{
		if(receiver instanceof IProject)
		{
			return WorkflowCore.getDefault().getWorkflowModel().isWorkflowProject((IProject)receiver);
		}
		return false;
	}

}
