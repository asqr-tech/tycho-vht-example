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
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;
import org.eclipse.vtp.framework.interactions.core.conversation.IMetaDataMessage;
import org.eclipse.vtp.framework.interactions.core.platforms.IPlatformSelector;
import org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform;

/**
 * An action that enqueues a meta-data message.
 * 
 * @author Lonnie Pryor
 */
public class MetaDataMessageAction implements IAction
{
	/** The context to use. */
	private final IActionContext context;
	/** The conversation to use. */
	private final IConversation conversation;
	/** The configuration to use. */
	private final MetaDataConfiguration configuration;
	private final IPlatformSelector platformSelector;

	/**
	 * Creates a new MetaDataMessageAction.
	 * 
	 * @param context The context to use.
	 * @param conversation The conversation to use.
	 * @param configuration The configuration to use.
	 */
	public MetaDataMessageAction(IActionContext context,
			IConversation conversation, MetaDataConfiguration configuration,
			IPlatformSelector platformSelector)
	{
		this.context = context;
		this.conversation = conversation;
		this.configuration = configuration;
		this.platformSelector = platformSelector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IAction#execute()
	 */
	public IActionResult execute()
	{
		String resultParameterName = ACTION_PREFIX + context.getActionID().replace(':', '_');
		String result = context.getParameter(resultParameterName);
		System.out.println("Meta-data Message Result: " + result);
		context.clearParameter(resultParameterName);
		if (IConversation.RESULT_NAME_FILLED.equals(result))
		{
			AbstractPlatform platform = (AbstractPlatform)platformSelector.getSelectedPlatform();
			if(platform.processMetaDataMessageResults(context))
			{
				return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
			}
			return context.createResult("error.meta-data.message"); //$NON-NLS-1$
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
			try
			{
				if(context.isDebugEnabled()) context.debug(getClass().getName().substring(
						getClass().getName().lastIndexOf('.') + 1));
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "metadata.message");
					context.report(IReporter.SEVERITY_INFO, "Sending meta-data.", props);
				}
				IMetaDataMessage createMetaDataMessage = conversation.createMetaDataMessage(configuration, resultParameterName);
				if (createMetaDataMessage.enqueue())
					return context.createResult(IActionResult.RESULT_NAME_REPEAT);
			}
			catch (RuntimeException e)
			{
				return context.createResult("error.meta-data.message", e); //$NON-NLS-1$
			}
			return context.createResult("error.meta-data.message"); //$NON-NLS-1$
		}
	}
}
