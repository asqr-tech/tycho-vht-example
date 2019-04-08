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

import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.interactions.core.configurations.ExternalReferenceConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;

/**
 * An action that enqueues an external reference.
 * 
 * @author Lonnie Pryor
 */
public class ExternalReferenceAction implements IAction
{
	/** The context to use. */
	private final IActionContext context;
	/** The conversation to use. */
	private final IConversation conversation;
	/** Comment for variables. */
	private final IVariableRegistry variables;
	/** Comment for configuration. */
	private final ExternalReferenceConfiguration configuration;

	/**
	 * Creates a new ExternalReferenceAction.
	 * 
	 * @param context The context to use.
	 * @param conversation The conversation to use.
	 */
	public ExternalReferenceAction(IActionContext context,
			IConversation conversation, IVariableRegistry variables,
			ExternalReferenceConfiguration configuration)
	{
		this.context = context;
		this.conversation = conversation;
		this.variables = variables;
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
			if(context.isDebugEnabled()) context.debug(getClass().getName().substring(
					getClass().getName().lastIndexOf('.') + 1));
			String result = context.getParameter(resultParameterName);
			context.clearParameter(resultParameterName);
			if (IConversation.RESULT_NAME_FILLED.equals(result))
			{
				String[] keys = configuration.getOutputNames();
				for (int i = 0; i < keys.length; ++i)
				{
					IStringObject obj = (IStringObject)variables
							.createVariable(IStringObject.TYPE_NAME);
					obj.setValue(context.getParameter(keys[i]));
					variables.setVariable(keys[i], obj);
				}
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "external.reference.after");
					context.report(IReporter.SEVERITY_INFO,
							"External reference complete.",
							props);
				}
				return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
			}
			else if (IConversation.RESULT_NAME_HANGUP.equals(result))
			{
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "error.disconnect.hangup");
					context.report(IReporter.SEVERITY_INFO,
						"Got disconnect during interaction.", props);
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
					props.put("event", "external.reference.before");
					context.report(IReporter.SEVERITY_INFO,
						"Invoking external reference \"" + configuration.getName() + "\".",
						props);
				}
				conversation.createExternalReference(configuration,
						resultParameterName).enqueue();
				return context.createResult(IActionResult.RESULT_NAME_REPEAT);
			}
		}
		catch (RuntimeException e)
		{
			return context.createResult("error.external.reference", e); //$NON-NLS-1$
		}
	}
}
