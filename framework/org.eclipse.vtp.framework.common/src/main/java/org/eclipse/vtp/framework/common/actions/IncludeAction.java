/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods), 
 *    Randy Childers (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.common.actions;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.vtp.framework.common.configurations.DispatchConfiguration;
import org.eclipse.vtp.framework.common.controller.IController;
import org.eclipse.vtp.framework.common.controller.IIncludeDispatcher;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;

/**
 * An action that suspends the current process, forwards control to another
 * process, and resumes when that process completes.
 * 
 * @author Lonnie Pryor
 */
public class IncludeAction implements IAction
{
	/** The context that contains this action. */
	private final IActionContext context;
	/** The controller to enqueue with. */
	private final IController controller;
	/** The forward configuration. */
	private final DispatchConfiguration configuration;

	/**
	 * Creates a new ForwardAction.
	 * 
	 * @param context The context that contains this action.
	 * @param controller The controller to enqueue with.
	 * @param configuration The configuration for this forward action.
	 */
	public IncludeAction(IActionContext context, IController controller,
			DispatchConfiguration configuration)
	{
		this.context = context;
		this.controller = controller;
		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IAction#execute()
	 */
	public IActionResult execute()
	{
		String cmd = context.getParameter(context.getActionID());
		if ("complete".equals(cmd)) //$NON-NLS-1$
		{
			if(context.isReportingEnabled())
			{
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put("include.target", String.valueOf(configuration
					.getTargetProcessURI()));
				props.put("event", "include.after");
				props.put("include.result", String.valueOf(context.getParameter("exit")));
				context.report(IReporter.SEVERITY_INFO, "Include returned with \""
					+ context.getParameter("exit") + "\".", props);
			}
			String result = context.getParameter("exit"); //$NON-NLS-1$
			if (result == null)
				result = IActionResult.RESULT_NAME_DEFAULT; //$NON-NLS-1$
			return context.createResult(result);
		}
		try
		{
			if(context.isReportingEnabled())
			{
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put("include.target", String.valueOf(configuration
					.getTargetProcessURI()));
				props.put("event", "include.before");
				context.report(IReporter.SEVERITY_INFO, "Including \""
					+ configuration.getTargetProcessURI() + "\".", props);
			}
			IIncludeDispatcher include = controller.createInclude(configuration);
			include.setParameterValue(context.getActionID(), "complete"); //$NON-NLS-1$
			if (include.enqueue())
				return context.createResult(IActionResult.RESULT_NAME_REPEAT); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			return context.createResult("error.include", e); //$NON-NLS-1$
		}
		return context.createResult("error.include"); //$NON-NLS-1$
	}
}
