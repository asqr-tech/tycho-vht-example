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
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;

/**
 * An action that terminates the current process and forwards control to another
 * process.
 * 
 * @author Lonnie Pryor
 */
public class ForwardAction implements IAction
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
	public ForwardAction(IActionContext context, IController controller,
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
		try
		{
			if(context.isReportingEnabled())
			{
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put("event", "forward");
				props.put("forward.target", String.valueOf(configuration
					.getTargetProcessURI()));
				context.report(IReporter.SEVERITY_INFO, "Forwarding to \""
					+ configuration.getTargetProcessURI() + "\".", props);
			}
			if (controller.createForward(configuration).enqueue())
				return context.createResult(IActionResult.RESULT_NAME_ABORT);
		}
		catch (Exception e)
		{
			return context.createResult("error.forward", e); //$NON-NLS-1$
		}
		return context.createResult("error.forward"); //$NON-NLS-1$
	}
}
