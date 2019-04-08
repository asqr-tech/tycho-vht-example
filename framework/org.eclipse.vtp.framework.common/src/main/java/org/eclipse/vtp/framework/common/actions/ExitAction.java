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

import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.eclipse.vtp.framework.common.configurations.ExitConfiguration;
import org.eclipse.vtp.framework.common.controller.IController;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;

/**
 * An action that terminates the current process and returns control to the
 * process that included it.
 * 
 * @author Lonnie Pryor
 */
public class ExitAction implements IAction
{
	/** The context that contains this action. */
	protected final IActionContext context;
	/** The controller to enqueue with. */
	protected final IController controller;
	/** The exit configuration. */
	protected final ExitConfiguration configuration;
	/** The exit configuration. */
	protected final AssignmentConfiguration[] assignments;

	/**
	 * Creates a new ForwardAction.
	 * 
	 * @param context The context that contains this action.
	 * @param controller The controller to enqueue with.
	 * @param configuration The configuration for this exit action.
	 */
	public ExitAction(IActionContext context, IController controller,
			ExitConfiguration configuration, AssignmentConfiguration[] assignments)
	{
		this.context = context;
		this.controller = controller;
		this.configuration = configuration;
		this.assignments = assignments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IAction#execute()
	 */
	public IActionResult execute()
	{
		try
		{
			if(context.isReportingEnabled())
			{
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put("event", "exit");
				props.put("exit.path", String.valueOf(configuration.getValue()));
				context.report(IReporter.SEVERITY_INFO, "Exiting via path \""
					+ configuration.getValue() + "\".", props);
			}
			if (controller.createExit(configuration, assignments).enqueue())
				return context.createResult(IActionResult.RESULT_NAME_ABORT);
		}
		catch (Exception e)
		{
			return context.createResult("error.exit", e); //$NON-NLS-1$
		}
		return context.createResult("error.exit"); //$NON-NLS-1$
	}
}
