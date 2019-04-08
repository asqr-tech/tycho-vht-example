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

import org.eclipse.vtp.framework.common.IBrandSelection;
import org.eclipse.vtp.framework.common.IScriptingService;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.interactions.core.IInteractionTypeSelection;
import org.eclipse.vtp.framework.interactions.core.ILanguageSelection;
import org.eclipse.vtp.framework.interactions.core.configurations.BridgeMessageConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.PropertyConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IBridgeMessage;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;

/**
 * An action that enqueues a branch message.
 * 
 * @author Lonnie Pryor
 */
public class BridgeMessageAction implements IAction
{
	/** The context to use. */
	private final IActionContext context;
	/** The conversation to use. */
	private final IConversation conversation;
	/** The configuration to use. */
	private final BridgeMessageConfiguration configuration;
	private final IBrandSelection brandSelection;
	private final ILanguageSelection languageSelection;
	private final IInteractionTypeSelection interactionSelection;
	private final IVariableRegistry variableRegistry;
	private final IScriptingService scriptingService;

	/**
	 * Creates a new BranchMessageAction.
	 * 
	 * @param context The context to use.
	 * @param conversation The conversation to use.
	 * @param configuration The configuration to use.
	 */
	public BridgeMessageAction(IActionContext context,
			IConversation conversation, BridgeMessageConfiguration configuration, IBrandSelection brandSelection,
 ILanguageSelection languageSelection, IInteractionTypeSelection interactionSelection, IVariableRegistry variableRegistry,
 IScriptingService scriptingService)
	{
		this.context = context;
		this.conversation = conversation;
		this.configuration = configuration;
		this.brandSelection = brandSelection;
		this.languageSelection = languageSelection;
		this.interactionSelection = interactionSelection;
		this.variableRegistry = variableRegistry;
		this.scriptingService = scriptingService;
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
			MediaConfiguration mediaConfiguration = configuration.getMediaConfiguration();
			if(context.isDebugEnabled()) context.debug(getClass().getName().substring(
					getClass().getName().lastIndexOf('.') + 1));
			String result = context.getParameter(resultParameterName);
			context.clearParameter(resultParameterName);
			
			PropertyConfiguration transferTypePropertyConfig = mediaConfiguration.getPropertyConfiguration("transferType");
			PropertyConfiguration typePropertyConfig = mediaConfiguration.getPropertyConfiguration("type");
			PropertyConfiguration destinationPropertyConfig = mediaConfiguration.getPropertyConfiguration("destination");
			
			String value = conversation.resolveProperty(destinationPropertyConfig, true, true);
			if(value == null)
				value = conversation.resolveProperty(destinationPropertyConfig, true, false);
			if (value == null)
				return null;

			String type = conversation.resolveProperty(typePropertyConfig, true, true);
			if(type == null)
				type = conversation.resolveProperty(typePropertyConfig, true, false);
			if ("variable".equalsIgnoreCase(type))
				value = String.valueOf(variableRegistry.getVariable(value));
			else if ("expression".equalsIgnoreCase(type))
				value = String.valueOf(scriptingService.createScriptingEngine(
				"JavaScript").execute(value));
			
			String transferType = conversation.resolveProperty(transferTypePropertyConfig, true, true);
			if(transferType == null)
				transferType = conversation.resolveProperty(transferTypePropertyConfig, true, false);
			
			if (result != null)
			{
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "transfer.after");
					props.put("transfer.destination", value);
					props.put("transfer.type", transferType);
					props.put("transfer.result", result);
					context.report(IReporter.SEVERITY_INFO,
							"Ended transfer to destination \"" + value
							+ "\"", props);
				}
				if (IBridgeMessage.TRANSFERRED.equals(result))
					return context.createResult("Call Transfered");
				else if (IBridgeMessage.BUSY.equals(result))
					return context.createResult("Line Busy");
				else if (IBridgeMessage.UNAVAILABLE.equals(result))
					return context.createResult("No Answer");
				else if (IBridgeMessage.NOAUTH.equals(result))
					return context.createResult("error.connection.noauthorization");
				else if (IBridgeMessage.BADDEST.equals(result))
					return context.createResult("error.connection.baddestination");
				else if (IBridgeMessage.NOROUTE.equals(result))
					return context.createResult("error.connection.noroute");
				else if (IBridgeMessage.NORESOURCE.equals(result))
					return context.createResult("error.connection.noresource");
				else if (IBridgeMessage.PROTOCOL.equals(result))
					return context.createResult("error.connection.protocol");
				else if (IBridgeMessage.BADBRIDGE.equals(result))
					return context.createResult("error.unsupported.transfer.bridge");
				else if (IBridgeMessage.BADURI.equals(result))
					return context.createResult("error.unsupported.uri");
				else if (IConversation.RESULT_NAME_HANGUP.equals(result))
					return context.createResult(IConversation.RESULT_NAME_HANGUP);
				else
					return context.createResult(result);
			}
			if(context.isReportingEnabled())
			{
				Dictionary props = new Hashtable();
				props.put("event", "transfer.before");
				props.put("transfer.destination", value);
				props.put("transfer.type", transferType);
				context.report(IReporter.SEVERITY_INFO, "Transfering to destination \""
						+ value + "\"", props);
			}
			conversation.createBridgeMessage(configuration, resultParameterName)
					.enqueue();
			return context.createResult(IActionResult.RESULT_NAME_REPEAT);
		}
		catch (RuntimeException e)
		{
			return context.createResult("error.bridge.message", e); //$NON-NLS-1$
		}
	}
}
