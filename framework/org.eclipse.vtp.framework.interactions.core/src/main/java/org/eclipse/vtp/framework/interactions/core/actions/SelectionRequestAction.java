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

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.vtp.framework.common.ILastResult;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.interactions.core.configurations.SelectionRequestConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;
import org.eclipse.vtp.framework.util.Guid;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * An action that enqueues a selection request.
 * 
 * @author Lonnie Pryor
 */
public class SelectionRequestAction implements IAction
{
	/** The context to use. */
	private final IActionContext context;
	/** The conversation to use. */
	private final IConversation conversation;
	/** The configuration to use. */
	private final SelectionRequestConfiguration configuration;
	private final ILastResult lastResult;
	private final IVariableRegistry variableRegistry;
	private static String DEFAULT_DATA_NAME = "sel_" + Guid.createGUID();

	/**
	 * Creates a new SelectionRequestAction.
	 * 
	 * @param context The context to use.
	 * @param conversation The conversation to use.
	 * @param configuration The configuration to use.
	 */
	public SelectionRequestAction(IActionContext context,
			IConversation conversation, SelectionRequestConfiguration configuration,
			ILastResult lastResult, IVariableRegistry variableRegistry)
	{
		this.context = context;
		this.conversation = conversation;
		this.configuration = configuration;
		this.lastResult = lastResult;
		this.variableRegistry = variableRegistry;
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
			if("".equals(configuration.getDataName()))
				configuration.setDataName(DEFAULT_DATA_NAME);
			String result = context.getParameter(resultParameterName);
			context.clearParameter(resultParameterName);
			if (IConversation.RESULT_NAME_FILLED.equals(result))
			{
				String value = context.getParameter(configuration.getDataName());
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "selection.request.filled");
					props.put("event.key", "selection");
					props.put("event.value", value);
					context.report(IReporter.SEVERITY_INFO, "Got requested selection \""
						+ value + "\"", props);
				}
				lastResult.clear();
				String lastResultXML = context.getParameter("lastresult");
				if(lastResultXML != null && !lastResultXML.equals(""))
				{
					Document lastResultDocument = null;
					try
					{
						lastResultDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(lastResultXML.getBytes()));
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					NodeList nl = lastResultDocument.getDocumentElement().getElementsByTagName("result");
					for(int i = 0; i < nl.getLength(); i++)
					{
						Element resultElement = (Element)nl.item(i);
						Element confidenceElement = (Element)resultElement.getElementsByTagName("confidence").item(0);
						Element utteranceElement = (Element)resultElement.getElementsByTagName("utterance").item(0);
						Element inputModeElement = (Element)resultElement.getElementsByTagName("inputmode").item(0);
						Element interpretationElement = (Element)resultElement.getElementsByTagName("interpretation").item(0);
						lastResult.addResult(new BigDecimal(confidenceElement.getTextContent()).multiply(new BigDecimal(100)).intValue(), utteranceElement.getTextContent(), inputModeElement.getTextContent(), interpretationElement.getTextContent());
					}
				}
				if(!configuration.getDataName().equals(DEFAULT_DATA_NAME))
				{
					IStringObject variable = (IStringObject)variableRegistry
							.createVariable(IStringObject.TYPE_NAME);
					variable.setValue(value);
					variableRegistry.setVariable(configuration.getDataName(), variable);
				}
				return context.createResult(value);
			}
			else if (IConversation.RESULT_NAME_NO_INPUT.equals(result))
			{
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "selection.request.noinput");
					context.report(IReporter.SEVERITY_INFO,
						"Got no input for requested selection.", props);
				}
				return context.createResult(IConversation.RESULT_NAME_NO_INPUT);
			}
			else if (IConversation.RESULT_NAME_NO_MATCH.equals(result))
			{
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "selection.request.nomatch");
					context.report(IReporter.SEVERITY_INFO,
						"Got no match for requested selection.", props);
				}
				lastResult.clear();
				String lastResultXML = context.getParameter("lastresult");
				if(lastResultXML != null && !lastResultXML.equals(""))
				{
					Document lastResultDocument = null;
					try
					{
						lastResultDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(lastResultXML.getBytes()));
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					NodeList nl = lastResultDocument.getDocumentElement().getElementsByTagName("result");
					for(int i = 0; i < nl.getLength(); i++)
					{
						Element resultElement = (Element)nl.item(i);
						Element confidenceElement = (Element)resultElement.getElementsByTagName("confidence").item(0);
						Element utteranceElement = (Element)resultElement.getElementsByTagName("utterance").item(0);
						Element inputModeElement = (Element)resultElement.getElementsByTagName("inputmode").item(0);
						Element interpretationElement = (Element)resultElement.getElementsByTagName("interpretation").item(0);
						lastResult.addResult(new BigDecimal(confidenceElement.getTextContent()).multiply(new BigDecimal(100)).intValue(), utteranceElement.getTextContent(), inputModeElement.getTextContent(), interpretationElement.getTextContent());
					}
				}
				return context.createResult(IConversation.RESULT_NAME_NO_MATCH);
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
					props.put("event", "selection.request.before");
					context.report(IReporter.SEVERITY_INFO, "Requesting selection \""
						+ configuration.getDataName() + "\".", props);
				}
				conversation.createSelectionRequest(configuration,
						resultParameterName).enqueue();
				return context.createResult(IActionResult.RESULT_NAME_REPEAT);
			}
		}
		catch (RuntimeException e)
		{
			return context.createResult("error.selection.request", e); //$NON-NLS-1$
		}
	}
}
