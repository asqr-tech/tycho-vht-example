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
import org.eclipse.vtp.framework.interactions.core.configurations.InputRequestConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * An action that enqueues an input request.
 * 
 * @author Lonnie Pryor
 */
public class InputRequestAction implements IAction
{
	private static final BigDecimal BD_100 = new BigDecimal(100);
	/** The context to use. */
	private final IActionContext context;
	/** The variable registry to use. */
	private final IVariableRegistry variableRegistry;
	/** The conversation to use. */
	private final IConversation conversation;
	private final ILastResult lastResult;
	/** The configuration to use. */
	private final InputRequestConfiguration configuration;

	/**
	 * Creates a new InputRequestAction.
	 * 
	 * @param context The context to use.
	 * @param variableRegistry The variable registry to use.
	 * @param conversation The conversation to use.
	 * @param configuration The configuration to use.
	 */
	public InputRequestAction(IActionContext context,
			IVariableRegistry variableRegistry, IConversation conversation,
			ILastResult lastResult, InputRequestConfiguration configuration)
	{
		this.context = context;
		this.variableRegistry = variableRegistry;
		this.conversation = conversation;
		this.lastResult = lastResult;
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
			context.info("Entered Action " + context.getActionName() + " [" + context.getActionID() + "] with result: " + result);
			context.clearParameter(resultParameterName);
			if (IConversation.RESULT_NAME_FILLED.equals(result))
			{
				String value = context.getParameter(configuration.getDataName());
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "input.request.filled");
					props.put("input.result", configuration.isSecured() ? "**Secured**" : String.valueOf(value));
					context.report(IReporter.SEVERITY_INFO, "Got requested input \""
						+ (configuration.isSecured() ? "**Secured**" : String.valueOf(value)) + "\"", props);
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
					NodeList markList = lastResultDocument.getDocumentElement().getElementsByTagName("mark");
					if(markList.getLength() > 0)
					{
						Element markElement = (Element)markList.item(0);
						lastResult.setMarkName(markElement.getAttribute("name"));
						lastResult.setMarkTime(markElement.getAttribute("offset"));
					}
					NodeList nl = lastResultDocument.getDocumentElement().getElementsByTagName("result");
					for(int i = 0; i < nl.getLength(); i++)
					{
						Element resultElement = (Element)nl.item(i);
						Element confidenceElement = (Element)resultElement.getElementsByTagName("confidence").item(0);
						Element utteranceElement = (Element)resultElement.getElementsByTagName("utterance").item(0);
						Element inputModeElement = (Element)resultElement.getElementsByTagName("inputmode").item(0);
						Element interpretationElement = (Element)resultElement.getElementsByTagName("interpretation").item(0);
						int confidenceValue = 0;
						try
						{
							BigDecimal confidenceBigDecimal = new BigDecimal(confidenceElement.getTextContent());
							confidenceValue = confidenceBigDecimal.multiply(BD_100).intValue();
						}
						catch(Exception ex)
						{
							context.info("Invalid confidence value: " + confidenceElement.getTextContent());
							context.info("Defaulting confidence to 0.");
						}
						lastResult.addResult(confidenceValue, utteranceElement.getTextContent(), inputModeElement.getTextContent(), interpretationElement.getTextContent());
//						lastResult.addResult(new BigDecimal(confidenceElement.getTextContent()).multiply(BD_100).intValue(), utteranceElement.getTextContent(), inputModeElement.getTextContent(), interpretationElement.getTextContent());
					}
				}
				IStringObject variable = (IStringObject)variableRegistry
						.createVariable(IStringObject.TYPE_NAME);
				variable.setValue(value);
				variableRegistry.setVariable(configuration.getDataName(), variable);
				return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
			}
			else if (IConversation.RESULT_NAME_NO_INPUT.equals(result))
			{
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "input.request.noinput");
					context.report(IReporter.SEVERITY_INFO,
						"Got no input for requested input.", props);
				}
				return context.createResult(IConversation.RESULT_NAME_NO_INPUT);
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
			else if (IConversation.RESULT_NAME_NO_MATCH.equals(result))
			{
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "input.request.nomatch");
					context.report(IReporter.SEVERITY_INFO,
						"Got no match for requested input.", props);
				}
				String lastResultXML = context.getParameter("lastresult");
				Document lastResultDocument = null;
				try
				{
					lastResultDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(lastResultXML.getBytes()));
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				lastResult.clear();
				NodeList markList = lastResultDocument.getDocumentElement().getElementsByTagName("mark");
				if(markList.getLength() > 0)
				{
					Element markElement = (Element)markList.item(0);
					lastResult.setMarkName(markElement.getAttribute("name"));
					lastResult.setMarkTime(markElement.getAttribute("offset"));
				}
				NodeList nl = lastResultDocument.getDocumentElement().getElementsByTagName("result");
				for(int i = 0; i < nl.getLength(); i++)
				{
					Element resultElement = (Element)nl.item(i);
					Element confidenceElement = (Element)resultElement.getElementsByTagName("confidence").item(0);
					Element utteranceElement = (Element)resultElement.getElementsByTagName("utterance").item(0);
					Element inputModeElement = (Element)resultElement.getElementsByTagName("inputmode").item(0);
					Element interpretationElement = (Element)resultElement.getElementsByTagName("interpretation").item(0);
					int confidenceValue = 0;
					try
					{
						BigDecimal confidenceBigDecimal = new BigDecimal(confidenceElement.getTextContent());
						confidenceValue = confidenceBigDecimal.multiply(BD_100).intValue();
					}
					catch(Exception ex)
					{
						context.info("Invalid confidence value: " + confidenceElement.getTextContent());
						context.info("Defaulting confidence to 0.");
					}
					lastResult.addResult(confidenceValue, utteranceElement.getTextContent(), inputModeElement.getTextContent(), interpretationElement.getTextContent());
				}
				return context.createResult(IConversation.RESULT_NAME_NO_MATCH);
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
					props.put("event", "input.request.before");
					context.report(IReporter.SEVERITY_INFO, "Requesting input \""
						+ configuration.getDataName() + "\".", props);
				}
				conversation.createInputRequest(configuration, resultParameterName)
						.enqueue();
				return context.createResult(IActionResult.RESULT_NAME_REPEAT);
			}
		}
		catch (RuntimeException e)
		{
			return context.createResult("error.input.request", e); //$NON-NLS-1$
		}
	}
}
