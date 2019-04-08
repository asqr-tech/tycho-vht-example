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
import org.eclipse.vtp.framework.interactions.core.configurations.DataRequestConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * An action that enqueues a data request.
 * 
 * @author Lonnie Pryor
 */
public class DataRequestAction implements IAction
{
	/** The context to use. */
	private final IActionContext context;
	/** The variable registry to use. */
	private final IVariableRegistry variableRegistry;
	/** The conversation to use. */
	private final IConversation conversation;
	/** The configuration to use. */
	private final DataRequestConfiguration configuration;
	private final ILastResult lastResult;

	/**
	 * Creates a new DataRequestAction.
	 * 
	 * @param context The context to use.
	 * @param variableRegistry The variable registry to use.
	 * @param conversation The conversation to use.
	 * @param configuration The configuration to use.
	 */
	public DataRequestAction(IActionContext context,
			IVariableRegistry variableRegistry, IConversation conversation,
			DataRequestConfiguration configuration, ILastResult lastResult)
	{
		this.context = context;
		this.variableRegistry = variableRegistry;
		this.conversation = conversation;
		this.configuration = configuration;
		this.lastResult = lastResult;
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
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "data.request.filled");
					context.report(IReporter.SEVERITY_INFO, "Got requested data \""
						+ context.getParameter(configuration.getDataName()) + "\"", props);
				}
				IStringObject variable = (IStringObject)variableRegistry
						.createVariable(IStringObject.TYPE_NAME);
				variable.setValue(context.getParameter(configuration.getDataName()));
				variableRegistry.setVariable(configuration.getDataName(), variable);
				IStringObject dtmfVar = (IStringObject)variableRegistry.createVariable(IStringObject.TYPE_NAME);
				dtmfVar.setValue(context.getParameter(configuration.getDataName() + "_termchar"));
				variableRegistry.setVariable("RecordDTMF", dtmfVar);
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
						lastResult.addResult(new BigDecimal(confidenceElement.getTextContent()).multiply(new BigDecimal(100)).intValue(), utteranceElement.getTextContent(), inputModeElement.getTextContent(), interpretationElement.getTextContent());
					}
				}
				return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
			}
			else if (IConversation.RESULT_NAME_NO_INPUT.equals(result))
			{
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "data.request.noinput");
					context.report(IReporter.SEVERITY_INFO,
						"Got no input for requested data.", props);
				}
				return context.createResult(IConversation.RESULT_NAME_NO_INPUT);
			}
			else if (IConversation.RESULT_NAME_HANGUP.equals(result))
			{
				if(context.isReportingEnabled())
				{
					Dictionary props = new Hashtable();
					props.put("event", "data.request.filled");
					context.report(IReporter.SEVERITY_INFO, "Got requested data \""
						+ context.getParameter(configuration.getDataName()) + "\"", props);
				}
				IStringObject variable = (IStringObject)variableRegistry
						.createVariable(IStringObject.TYPE_NAME);
				variable.setValue(context.getParameter(configuration.getDataName()));
				variableRegistry.setVariable(configuration.getDataName(), variable);
				lastResult.clear();
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
					props.put("event", "data.request.nomatch");
					context.report(IReporter.SEVERITY_INFO,
						"Got no match for requested data.", props);
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
						lastResult.addResult(new BigDecimal(confidenceElement.getTextContent()).multiply(new BigDecimal(100)).intValue(), utteranceElement.getTextContent(), inputModeElement.getTextContent(), interpretationElement.getTextContent());
					}
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
					props.put("event", "data.request.before");
					context.report(IReporter.SEVERITY_INFO, "Requesting data \""
						+ configuration.getDataName() + "\".", props);
				}
				conversation.createDataRequest(configuration, resultParameterName)
						.enqueue();
				return context.createResult(IActionResult.RESULT_NAME_REPEAT);
			}
		}
		catch (RuntimeException e)
		{
			return context.createResult("error.data.request", e); //$NON-NLS-1$
		}
	}
}
