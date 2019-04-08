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
import org.eclipse.vtp.framework.interactions.core.configurations.TransferMessageConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;

/**
 * An action that enqueues a transfer message.
 * 
 * @author Lonnie Pryor
 */
public class TransferMessageAction implements IAction
{
	/** The context to use. */
	private final IActionContext context;
	/** The conversation to use. */
	private final IConversation conversation;
	/** The configuration to use. */
	private final TransferMessageConfiguration configuration;

	/**
	 * Creates a new TransferMessageAction.
	 * 
	 * @param context The context to use.
	 * @param conversation The conversation to use.
	 * @param configuration The configuration to use.
	 */
	public TransferMessageAction(IActionContext context,
			IConversation conversation, TransferMessageConfiguration configuration)
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
		try
		{
			if(context.isDebugEnabled()) context.debug(getClass().getName().substring(
					getClass().getName().lastIndexOf('.') + 1));
			if(context.isReportingEnabled())
			{
				Dictionary props = new Hashtable();
				props.put("event", "transfer");
				context.report(IReporter.SEVERITY_INFO, "Transfering session.", props);
			}
			conversation.createTransferMessage(configuration).enqueue();
			return context.createResult(IActionResult.RESULT_NAME_ABORT);
		}
		catch (RuntimeException e)
		{
			return context.createResult("error.transfer.message", e); //$NON-NLS-1$
		}
	}
}
