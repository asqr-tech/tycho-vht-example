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
package org.eclipse.vtp.framework.interactions.core.actions;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputMessageConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;

/**
 * An action that enqueues an output message.
 * 
 * @author Lonnie Pryor
 */
public class OutputMessageAction implements IAction
{
	/** The context to use. */
	private final IActionContext context;
	/** The conversation to use. */
	private final IConversation conversation;
	/** The configuration to use. */
	private final OutputMessageConfiguration configuration;

	/**
	 * Creates a new OutputMessageAction.
	 * 
	 * @param context The context to use.
	 * @param conversation The conversation to use.
	 * @param configuration The configuration to use.
	 */
	public OutputMessageAction(IActionContext context,
			IConversation conversation, OutputMessageConfiguration configuration)
	{
		this.context = context;
		this.conversation = conversation;
		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IAction#execute()
	 */
	public IActionResult execute()
	{
		String resultParameterName = ACTION_PREFIX + context.getActionID().replace(':', '_');
		try
		{
			String result = context.getParameter(resultParameterName);
			context.clearParameter(resultParameterName);
			if (IConversation.RESULT_NAME_FILLED.equals(result))
			{
				return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
			}
			else if (IConversation.RESULT_NAME_HANGUP.equals(result))
			{
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "connection.disconnect.hangup");
					context.report(IReporter.SEVERITY_INFO,
						"Got disconnect during output.", props);
				}
				return context.createResult(IConversation.RESULT_NAME_HANGUP);
			}
			else if(result != null)
			{
				return context.createResult(result);
			}
			else
			{
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "output.message");
					context.report(IReporter.SEVERITY_INFO, "Sending output.", props);
				}
				conversation.createOutputMessage(configuration, resultParameterName).enqueue();
				if(context.isInfoEnabled()) context.info("Output sent.");
				return context.createResult(IActionResult.RESULT_NAME_REPEAT);
			}
		}
		catch (RuntimeException e)
		{
			return context.createResult("error.output.message", e); //$NON-NLS-1$
		}
	}
}
