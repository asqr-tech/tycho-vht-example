/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package com.openmethods.openvxml.platforms.avp.services;

import java.math.BigDecimal;

import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.interactions.core.commands.InputRequestCommand;
import org.eclipse.vtp.framework.interactions.core.commands.SelectionRequestCommand;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.interactions.core.platforms.ILink;
import org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory;
import org.eclipse.vtp.framework.interactions.core.platforms.IRenderingQueue;
import org.eclipse.vtp.framework.interactions.voice.services.VoicePlatform;
import org.eclipse.vtp.framework.interactions.voice.vxml.AudioOutput;
import org.eclipse.vtp.framework.interactions.voice.vxml.Catch;
import org.eclipse.vtp.framework.interactions.voice.vxml.Dialog;
import org.eclipse.vtp.framework.interactions.voice.vxml.Else;
import org.eclipse.vtp.framework.interactions.voice.vxml.ElseIf;
import org.eclipse.vtp.framework.interactions.voice.vxml.ExternalGrammar;
import org.eclipse.vtp.framework.interactions.voice.vxml.Field;
import org.eclipse.vtp.framework.interactions.voice.vxml.Filled;
import org.eclipse.vtp.framework.interactions.voice.vxml.Form;
import org.eclipse.vtp.framework.interactions.voice.vxml.Goto;
import org.eclipse.vtp.framework.interactions.voice.vxml.If;
import org.eclipse.vtp.framework.interactions.voice.vxml.InlineGrammar;
import org.eclipse.vtp.framework.interactions.voice.vxml.Item;
import org.eclipse.vtp.framework.interactions.voice.vxml.NoInput;
import org.eclipse.vtp.framework.interactions.voice.vxml.NoMatch;
import org.eclipse.vtp.framework.interactions.voice.vxml.OneOf;
import org.eclipse.vtp.framework.interactions.voice.vxml.OutputSet;
import org.eclipse.vtp.framework.interactions.voice.vxml.Prompt;
import org.eclipse.vtp.framework.interactions.voice.vxml.Rule;
import org.eclipse.vtp.framework.interactions.voice.vxml.SSMLMarkOutput;
import org.eclipse.vtp.framework.interactions.voice.vxml.Script;
import org.eclipse.vtp.framework.interactions.voice.vxml.Submit;
import org.eclipse.vtp.framework.interactions.voice.vxml.TextOutput;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLDocument;
import org.eclipse.vtp.framework.interactions.voice.vxml.Variable;
import org.eclipse.vtp.framework.util.Guid;

/**
 * A generic implementation of a AVP-specific VXML platform.
 * 
 * @author Lonnie Pryor
 */
public class Avp5Platform extends VoicePlatform
{
	/**
	 * Creates a new AvpPlatform.
	 */
	public Avp5Platform(IExecutionContext context)
	{
		super(context);
	}
 
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      createDocument(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.platforms.IRenderingQueue)
	 */
	public IDocument createDocument(ILinkFactory links,
			IRenderingQueue renderingQueue)
	{
		links.setUrlEncoded(false);
		return super.createDocument(links, renderingQueue);
	}

	@Override
    protected VXMLDocument createVXMLDocument(ILinkFactory links, Dialog dialog)
    {
		VXMLDocument document = super.createVXMLDocument(links, dialog);
		document.setProperty("documentmaxage", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("documentmaxstale", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		return document;
    }
	
	protected IDocument renderInputRequest(ILinkFactory links,
			InputRequestCommand inputRequestCommand)
	{
		Form form = new Form("InputRequestForm"); //$NON-NLS-1$
		String bargeIn = inputRequestCommand.getPropertyValue("barge-in"); //$NON-NLS-1$
		if (Boolean.TRUE.toString().equalsIgnoreCase(bargeIn))
			bargeIn = Boolean.TRUE.toString();
		else if (Boolean.FALSE.toString().equalsIgnoreCase(bargeIn))
			bargeIn = Boolean.FALSE.toString();
		else
			bargeIn = null;
		String timeout = inputRequestCommand.getPropertyValue("initial-timeout"); //$NON-NLS-1$
		String inputMode = inputRequestCommand.getPropertyValue("input-mode"); //$NON-NLS-1$
		if (inputMode == null || inputMode.length() == 0)
			inputMode = "dtmf only"; //$NON-NLS-1$
		String confidenceLevel = inputRequestCommand
				.getPropertyValue("confidence-level"); //$NON-NLS-1$
		String sensitivity = inputRequestCommand
				.getPropertyValue("sensitivity-level"); //$NON-NLS-1$
		String speedVsAccuracy = inputRequestCommand
				.getPropertyValue("speed-vs-accuracy"); //$NON-NLS-1$
		String speechCompletionTimeout = inputRequestCommand
				.getPropertyValue("speech-complete-timeout"); //$NON-NLS-1$
		String speechIncompleteTimeout = inputRequestCommand
				.getPropertyValue("speech-incomplete-timeout"); //$NON-NLS-1$
		String maxSpeechLength = inputRequestCommand
				.getPropertyValue("max-speech-timeout"); //$NON-NLS-1$
		String maxNBest = inputRequestCommand.getPropertyValue("max-n-best"); //$NON-NLS-1$
		String interDigitTimeout = inputRequestCommand
				.getPropertyValue("interdigit-timeout"); //$NON-NLS-1$
		String terminationTimeout = inputRequestCommand
				.getPropertyValue("termination-timeout"); //$NON-NLS-1$
		String terminationCharacter = inputRequestCommand
				.getPropertyValue("termination-character"); //$NON-NLS-1$
		Field field = new Field(inputRequestCommand.getDataName());
		if (bargeIn != null)
			field.setProperty(NAME_BARGEIN, bargeIn);
		if (timeout != null && timeout.length() > 0)
			field.setProperty(NAME_TIMEOUT, timeout + "s"); //$NON-NLS-1$
		if ("hybrid".equalsIgnoreCase(inputMode))
		{
			field.setProperty(NAME_INPUTMODES, "dtmf voice"); //$NON-NLS-1$
			field.setProperty("com.telera.speechenabled", Boolean.TRUE.toString()); //$NON-NLS-1$
		}
		if ("dtmf only".equalsIgnoreCase(inputMode)) //$NON-NLS-1$
		{
			field.setProperty(NAME_INPUTMODES, "dtmf"); //$NON-NLS-1$
			field.setProperty("com.telera.speechenabled", Boolean.FALSE.toString()); //$NON-NLS-1$
		}
		else
		{
			try
			{
				if (confidenceLevel != null && confidenceLevel.length() > 0)
					field.setProperty(NAME_CONFIDENCELEVEL, new BigDecimal(
							confidenceLevel).divide(ONE_HUNDRED).toString());
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			try
			{
				if (sensitivity != null && sensitivity.length() > 0)
					field.setProperty(NAME_SENSITIVITY, new BigDecimal(sensitivity)
							.divide(ONE_HUNDRED).toString());
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			try
			{
				if (speedVsAccuracy != null && speedVsAccuracy.length() > 0)
					field.setProperty(NAME_SPEEDVSACCURACY, new BigDecimal(
							speedVsAccuracy).divide(ONE_HUNDRED).toString());
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			if (speechCompletionTimeout != null
					&& speechCompletionTimeout.length() > 0)
				field.setProperty(NAME_COMPLETETIMEOUT, speechCompletionTimeout + "s"); //$NON-NLS-1$
			if (speechIncompleteTimeout != null
					&& speechIncompleteTimeout.length() > 0)
				field
						.setProperty(NAME_INCOMPLETETIMEOUT, speechIncompleteTimeout + "s"); //$NON-NLS-1$
			if (maxSpeechLength != null && maxSpeechLength.length() > 0)
				field.setProperty(NAME_MAXSPEECHTIMEOUT, maxSpeechLength + "s"); //$NON-NLS-1$
			else
				field.setProperty(NAME_MAXSPEECHTIMEOUT, "10s"); //$NON-NLS-1$
			if (maxNBest != null && maxNBest.length() > 0)
				field.setProperty(NAME_MAXNBEST, maxNBest);
		}
		if ("voice only".equalsIgnoreCase(inputMode)) //$NON-NLS-1$
		{
			field.setProperty(NAME_INPUTMODES, "voice"); //$NON-NLS-1$
			field.setProperty("com.telera.speechenabled", Boolean.TRUE.toString()); //$NON-NLS-1$
		}
		else
		{
			if (interDigitTimeout != null && interDigitTimeout.length() > 0)
				field.setProperty(NAME_INTERDIGITTIMEOUT, interDigitTimeout + "s"); //$NON-NLS-1$
			if (terminationTimeout != null && terminationTimeout.length() > 0)
				field.setProperty(NAME_TERMTIMEOUT, terminationTimeout + "s"); //$NON-NLS-1$
			if (terminationCharacter != null && terminationCharacter.length() > 0
					&& !"none".equalsIgnoreCase(terminationCharacter)) //$NON-NLS-1$
				field.setProperty(NAME_TERMCHAR, terminationCharacter);
			else
				field.setProperty(NAME_TERMCHAR, String.valueOf('D'));
		}
		OutputSet outputs = new OutputSet();
		for (int i = 0; i < inputRequestCommand.getOutputCount(); ++i)
		{
			String outputValue = inputRequestCommand.getOutputValue(i);
			switch (inputRequestCommand.getOutputType(i))
			{
			case InputRequestCommand.OUTPUT_TYPE_FILE:
				outputs.addOutput(new AudioOutput(links.createResourceLink(
						outputValue).toString()));
				break;
			case InputRequestCommand.OUTPUT_TYPE_TEXT:
				if(outputValue.startsWith("@@mark "))
				{
					outputs.addOutput(new SSMLMarkOutput(outputValue.substring(7)));
				}
				else
				{
					outputs
						.addOutput(new TextOutput(outputValue));
				}
				break;
			}
		}
		Prompt prompt = new Prompt(outputs);
		if (bargeIn != null)
			prompt.setBargeInEnabled(Boolean.valueOf(bargeIn).booleanValue());
		prompt.setLanguage(getCurrentLocale());
		field.setPrompt(prompt);
		if (inputRequestCommand.getInputValue() != null)
		{
			switch (inputRequestCommand.getInputType())
			{
			case InputRequestCommand.INPUT_TYPE_FILE:
				field
						.addGrammar(new ExternalGrammar("dtmf", links
								.createResourceLink(inputRequestCommand.getInputValue())
								.toString()));
				break;
			case InputRequestCommand.INPUT_TYPE_CUSTOM:
				String customData = inputRequestCommand.getInputValue();
				if (customData != null && customData.startsWith(VXML_BUILTIN_PREFIX))
				{
					//field.setType(customData.substring(VXML_BUILTIN_PREFIX.length()));
					field.addGrammar(new ExternalGrammar("dtmf", customData));
				}
				break;
			}
		}
		if (inputRequestCommand.getInput2Value() != null)
		{
			switch (inputRequestCommand.getInput2Type())
			{
			case InputRequestCommand.INPUT_TYPE_FILE:
				field.addGrammar(new ExternalGrammar("voice", links
						.createResourceLink(inputRequestCommand.getInput2Value())
						.toString()));
				break;
			case InputRequestCommand.INPUT_TYPE_CUSTOM:
				String customData = inputRequestCommand.getInput2Value();
				if (customData != null && customData.startsWith(VXML_BUILTIN_PREFIX))
				{
					//field.setType(customData.substring(VXML_BUILTIN_PREFIX.length()));
					field.addGrammar(new ExternalGrammar("dtmf", customData));
				}
				break;
			}
		}
		String[] parameterNames = inputRequestCommand.getParameterNames();
		ILink filledLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
			filledLink.setParameters(parameterNames[i], inputRequestCommand
					.getParameterValues(parameterNames[i]));
		filledLink.setParameter(inputRequestCommand.getResultName(),
				inputRequestCommand.getFilledResultValue());
		Filled filled = new Filled();
		filled.addVariable(new Variable("lastresult", "'<lastresult>'"));
		Script script = new Script();
		script.setText( "		lastresult = lastresult + '<mark name=\"' + application.lastresult$.markname + '\" offset=\"' + application.lastresult$.marktime + '\"/>';\r\n" +
						"		for(var i = 0; i < application.lastresult$.length; i++)\r\n" +
						"		{\r\n" +
						"			lastresult = lastresult + '<result>';\r\n" +
						"			lastresult = lastresult + '<confidence>' + application.lastresult$[i].confidence + '</confidence>';\r\n" +
						"			lastresult = lastresult + '<utterance><![CDATA[' + application.lastresult$[i].utterance + ']]></utterance>';\r\n" +
						"			lastresult = lastresult + '<inputmode><![CDATA[' + application.lastresult$[i].inputmode + ']]></inputmode>';\r\n" +
						"			lastresult = lastresult + '<interpretation><![CDATA[' + application.lastresult$[i].interpretation + ']]></interpretation>';\r\n" +
						"			lastresult = lastresult + '</result>';\r\n" +
						"		}\r\n" +
						"		lastresult = lastresult + '</lastresult>';\r\n");
		filled.addScript(script);
		filled.addAction(new Submit(filledLink.toString(),
				new String[] { inputRequestCommand.getDataName(), "lastresult" }));
		field.addFilledHandler(filled);
		ILink noInputLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
			noInputLink.setParameters(parameterNames[i], inputRequestCommand
					.getParameterValues(parameterNames[i]));
		noInputLink.setParameter(inputRequestCommand.getResultName(),
				inputRequestCommand.getNoInputResultValue());
		NoInput noInput = new NoInput();
		noInput.addAction(new Submit(noInputLink.toString(),
				new String[] { inputRequestCommand.getDataName() }));
		field.addEventHandler(noInput);
		ILink noMatchLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
			noMatchLink.setParameters(parameterNames[i], inputRequestCommand
					.getParameterValues(parameterNames[i]));
		noMatchLink.setParameter(inputRequestCommand.getResultName(),
				inputRequestCommand.getNoMatchResultValue());
		NoMatch noMatch = new NoMatch();
		noMatch.addVariable(new Variable("lastresult", "'<lastresult>'"));
		If ifTag = new If("typeof(application.lastresult$.markname) == 'string'");
		Script markScript = new Script();
		markScript.setText( "		lastresult = lastresult + '<mark name=\"' + application.lastresult$.markname + '\" offset=\"' + application.lastresult$.marktime + '\"/>';\r\n" +
			"		for(var i = 0; i < application.lastresult$.length; i++)\r\n" +
			"		{\r\n" +
			"			lastresult = lastresult + '<result>';\r\n" +
			"			lastresult = lastresult + '<confidence>' + application.lastresult$[i].confidence + '</confidence>';\r\n" +
			"			lastresult = lastresult + '<utterance><![CDATA[' + application.lastresult$[i].utterance + ']]></utterance>';\r\n" +
			"			lastresult = lastresult + '<inputmode><![CDATA[' + application.lastresult$[i].inputmode + ']]></inputmode>';\r\n" +
			"			lastresult = lastresult + '<interpretation><![CDATA[' + application.lastresult$[i].interpretation + ']]></interpretation>';\r\n" +
			"			lastresult = lastresult + '</result>';\r\n" +
			"		}\r\n" +
			"		lastresult = lastresult + '</lastresult>';\r\n");
		ifTag.addScript(markScript);
		Else elseTag = new Else();
		Script noMarkScript = new Script();
		noMarkScript.setText( "		lastresult = lastresult + '<mark name=\"NONE\" offset=\"0\"/>';\r\n" +
			"			lastresult = lastresult + '<result>';\r\n" +
			"			lastresult = lastresult + '<confidence>undefined</confidence>';\r\n" +
			"			lastresult = lastresult + '<utterance>undefined</utterance>';\r\n" +
			"			lastresult = lastresult + '<inputmode>undefined</inputmode>';\r\n" +
			"			lastresult = lastresult + '<interpretation>undefined</interpretation>';\r\n" +
			"			lastresult = lastresult + '</result>';\r\n" +
			"		lastresult = lastresult + '</lastresult>';\r\n");
		elseTag.addScript(noMarkScript);
		ifTag.setElse(elseTag);
		noMatch.addIfClause(ifTag);
//		script = new Script();
//		script.setText( "		lastresult = lastresult + '<mark name=\"' + application.lastresult$.markname + '\" offset=\"' + application.lastresult$.marktime + '\"/>';\r\n" +
//				"		for(var i = 0; i < application.lastresult$.length; i++)\r\n" +
//				"		{\r\n" +
//				"			lastresult = lastresult + '<result>';\r\n" +
//				"			lastresult = lastresult + '<confidence>' + application.lastresult$[i].confidence + '</confidence>';\r\n" +
//				"			lastresult = lastresult + '<utterance><![CDATA[' + application.lastresult$[i].utterance + ']]></utterance>';\r\n" +
//				"			lastresult = lastresult + '<inputmode><![CDATA[' + application.lastresult$[i].inputmode + ']]></inputmode>';\r\n" +
//				"			lastresult = lastresult + '<interpretation><![CDATA[' + application.lastresult$[i].interpretation + ']]></interpretation>';\r\n" +
//				"			lastresult = lastresult + '</result>';\r\n" +
//				"		}\r\n" +
//				"		lastresult = lastresult + '</lastresult>';\r\n");
		noMatch.addAction(new Submit(noMatchLink.toString(),
				new String[] { inputRequestCommand.getDataName(), "lastresult" }));
		field.addEventHandler(noMatch);
		ILink hangupLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
			hangupLink.setParameters(parameterNames[i], inputRequestCommand
					.getParameterValues(parameterNames[i]));
		hangupLink.setParameter(inputRequestCommand.getResultName(),
				inputRequestCommand.getHangupResultValue());
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		field.addEventHandler(disconnectCatch);
		form.addFormElement(field);
		return createVXMLDocument(links, form);
	}
	
	protected IDocument renderSelectionRequest(ILinkFactory links,
			SelectionRequestCommand selectionRequestCommand)
	{
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&   AVP 5   &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		String inputMode = selectionRequestCommand.getPropertyValue("input-mode"); //$NON-NLS-1$
		if (inputMode == null || inputMode.length() == 0)
			inputMode = "dtmf only"; //$NON-NLS-1$
		if (!"dtmf only".equalsIgnoreCase(inputMode)) //$NON-NLS-1$
		{
			return super.renderSelectionRequest(links, selectionRequestCommand);
		}
		String bargeIn = getNormalizedBoolean(selectionRequestCommand
				.getPropertyValue("barge-in")); //$NON-NLS-1$
		String timeout = selectionRequestCommand
				.getPropertyValue("initial-timeout"); //$NON-NLS-1$
		String confidenceLevel = selectionRequestCommand
				.getPropertyValue("confidence-level"); //$NON-NLS-1$
		String sensitivity = selectionRequestCommand
				.getPropertyValue("sensitivity-level"); //$NON-NLS-1$
		String speedVsAccuracy = selectionRequestCommand
				.getPropertyValue("speed-vs-accuracy"); //$NON-NLS-1$
		String speechCompletionTimeout = selectionRequestCommand
				.getPropertyValue("speech-complete-timeout"); //$NON-NLS-1$
		String speechIncompleteTimeout = selectionRequestCommand
				.getPropertyValue("speech-incomplete-timeout"); //$NON-NLS-1$
		String maxSpeechLength = selectionRequestCommand
				.getPropertyValue("max-speech-timeout"); //$NON-NLS-1$
		String maxNBest = selectionRequestCommand.getPropertyValue("max-n-best"); //$NON-NLS-1$
		String interDigitTimeout = selectionRequestCommand
				.getPropertyValue("interdigit-timeout"); //$NON-NLS-1$
		String terminationTimeout = selectionRequestCommand
				.getPropertyValue("termination-timeout"); //$NON-NLS-1$
		String terminationCharacter = selectionRequestCommand
				.getPropertyValue("termination-character"); //$NON-NLS-1$
		String grammarMode = null;
		OutputSet outputs = new OutputSet();
		for (int i = 0; i < selectionRequestCommand.getOutputCount(); ++i)
		{
			String outputValue = selectionRequestCommand
            	.getOutputValue(i);
			switch (selectionRequestCommand.getOutputType(i))
			{
			case InputRequestCommand.OUTPUT_TYPE_FILE:
				outputs.addOutput(new AudioOutput(links.createResourceLink(
						outputValue).toString()));
				break;
			case InputRequestCommand.OUTPUT_TYPE_TEXT:
				if(outputValue.startsWith("@@mark "))
				{
					outputs.addOutput(new SSMLMarkOutput(outputValue.substring(7)));
				}
				else
				{
					outputs.addOutput(new TextOutput(outputValue));
				}
				break;
			}
		}
		Prompt prompt = new Prompt(outputs);
		if (bargeIn != null)
			prompt.setBargeInEnabled(Boolean.valueOf(bargeIn).booleanValue());
		prompt.setLanguage(getCurrentLocale());
//		Menu menu = new Menu(selectionRequestCommand.getSelectionName(), prompt);
		Field field = new Field(selectionRequestCommand.getSelectionName(), prompt);
		
		
		if (bargeIn != null)
			field.setProperty(NAME_BARGEIN, bargeIn);
		if (timeout != null && timeout.length() > 0)
			field.setProperty(NAME_TIMEOUT, timeout + "s"); //$NON-NLS-1$
		if ("hybrid".equalsIgnoreCase(inputMode))
		{
			field.setProperty(NAME_INPUTMODES, "dtmf voice"); //$NON-NLS-1$
			field.setProperty("com.telera.speechenabled", Boolean.TRUE.toString()); //$NON-NLS-1$
		}
		if ("dtmf only".equalsIgnoreCase(inputMode)) //$NON-NLS-1$
		{
			grammarMode = GRAMMAR_MODE_DTMF;
			field.setProperty(NAME_INPUTMODES, "dtmf"); //$NON-NLS-1$
		}
		else
		{
			try
			{
				if (confidenceLevel != null && confidenceLevel.length() > 0)
					field.setProperty(NAME_CONFIDENCELEVEL,
							new BigDecimal(confidenceLevel).divide(ONE_HUNDRED).toString());
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			try
			{
				if (sensitivity != null && sensitivity.length() > 0)
					field.setProperty(NAME_SENSITIVITY, new BigDecimal(sensitivity)
							.divide(ONE_HUNDRED).toString());
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			try
			{
				if (speedVsAccuracy != null && speedVsAccuracy.length() > 0)
					field.setProperty(NAME_SPEEDVSACCURACY,
							new BigDecimal(speedVsAccuracy).divide(ONE_HUNDRED).toString());
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			if (speechCompletionTimeout != null
					&& speechCompletionTimeout.length() > 0)
				field.setProperty(NAME_COMPLETETIMEOUT, speechCompletionTimeout + "s"); //$NON-NLS-1$
			if (speechIncompleteTimeout != null
					&& speechIncompleteTimeout.length() > 0)
				field.setProperty(NAME_INCOMPLETETIMEOUT, speechIncompleteTimeout + "s"); //$NON-NLS-1$
			if (maxSpeechLength != null && maxSpeechLength.length() > 0)
				field.setProperty(NAME_MAXSPEECHTIMEOUT, maxSpeechLength + "s"); //$NON-NLS-1$
			else
				field.setProperty(NAME_MAXSPEECHTIMEOUT, "10s"); //$NON-NLS-1$
			if (maxNBest != null && maxNBest.length() > 0)
				field.setProperty(NAME_MAXNBEST, maxNBest);
		}
		if ("voice only".equalsIgnoreCase(inputMode)) //$NON-NLS-1$
		{
			grammarMode = GRAMMAR_MODE_VOICE;
			field.setProperty(NAME_INPUTMODES, "voice"); //$NON-NLS-1$
		}
		else
		{
			if (interDigitTimeout != null && interDigitTimeout.length() > 0)
				field.setProperty(NAME_INTERDIGITTIMEOUT, interDigitTimeout + "s"); //$NON-NLS-1$
			if (terminationTimeout != null && terminationTimeout.length() > 0)
				field.setProperty(NAME_TERMTIMEOUT, terminationTimeout + "s"); //$NON-NLS-1$
			if (terminationCharacter != null && terminationCharacter.length() > 0
					&& !"none".equalsIgnoreCase(terminationCharacter)) //$NON-NLS-1$
				field.setProperty(NAME_TERMCHAR, terminationCharacter);
			else
				field.setProperty(NAME_TERMCHAR, ""); //$NON-NLS-1$
		}
		OneOf optionGroup = new OneOf();
		ILink nextLink = links.createNextLink();
		String[] parameterNames = selectionRequestCommand.getParameterNames();
		for (int i = 0; i < parameterNames.length; ++i)
			nextLink.setParameters(parameterNames[i], selectionRequestCommand
					.getParameterValues(parameterNames[i]));
		nextLink.setParameter(selectionRequestCommand.getResultName(),
				selectionRequestCommand.getFilledResultValue());
		If ifElement = null;
		for (int i = 0; i < selectionRequestCommand.getOptionCount(); ++i)
		{
			nextLink.setParameter(selectionRequestCommand.getSelectionName(),
					selectionRequestCommand.getOption(i));
			String dtmf = selectionRequestCommand.getOptionProperty(i, "dtmf"); //$NON-NLS-1$
			if(i == 0)
			{
				ifElement = new If(selectionRequestCommand.getSelectionName() + " == " + dtmf);
				ifElement.addAction(new Goto(nextLink.toString()));
			}
			else if(i == selectionRequestCommand.getOptionCount() - 1)
			{
				Else elseElement = new Else();
				ifElement.setElse(elseElement);
				elseElement.addAction(new Goto(nextLink.toString()));
			}
			else
			{
				ElseIf elseIfElement = new ElseIf(selectionRequestCommand.getSelectionName() + " == " + dtmf);
				ifElement.addElseIf(elseIfElement);
				elseIfElement.addAction(new Goto(nextLink.toString()));
			}
			String silent = selectionRequestCommand.getOptionProperty(i, "silent"); //$NON-NLS-1$
			if (Boolean.TRUE.toString().equalsIgnoreCase(silent))
				silent = Boolean.TRUE.toString();
			else if (Boolean.FALSE.toString().equalsIgnoreCase(silent))
				silent = Boolean.FALSE.toString();
			else
				silent = null;
			if (!Boolean.TRUE.toString().equals(silent))
			{
				for (int j = 0; j < selectionRequestCommand.getOptionOutputCount(i); ++j)
				{
					String optionOutputValue = selectionRequestCommand
                    		.getOptionOutputValue(i, j);
					switch (selectionRequestCommand.getOptionOutputType(i, j))
					{
					case SelectionRequestCommand.OUTPUT_TYPE_FILE:
						outputs
								.addOutput(new AudioOutput(links.createResourceLink(
										optionOutputValue)
										.toString()));
						break;
					case SelectionRequestCommand.OUTPUT_TYPE_TEXT:
						if(optionOutputValue.startsWith("@@mark "))
						{
							outputs.addOutput(new SSMLMarkOutput(optionOutputValue.substring(7)));
						}
						else
						{
						outputs.addOutput(new TextOutput(optionOutputValue));
						}
						break;
					}
				}
			}
			if (!GRAMMAR_MODE_VOICE.equals(grammarMode))
			{
				Item item = new Item(dtmf);
				item.setMinimumRepeat(1);
				optionGroup.addItem(item);
			}
		}
		Filled filled = new Filled();
		filled.addIfClause(ifElement);
		field.addFilledHandler(filled);
		Rule rule = new Rule(Guid.createGUID());
		rule.addItem(optionGroup);
		InlineGrammar gram = new InlineGrammar(GRAMMAR_MODE_DTMF);
		gram.addRule(rule);
		field.addGrammar(gram);
		NoInput noInput = new NoInput();
		nextLink.setParameter(selectionRequestCommand.getResultName(),
				selectionRequestCommand.getNoInputResultValue());
		nextLink.setParameter(selectionRequestCommand.getSelectionName(), null);
		noInput.addAction(new Goto(nextLink.toString()));
		field.addEventHandler(noInput);
		NoMatch noMatch = new NoMatch();
		nextLink.setParameter(selectionRequestCommand.getResultName(),
				selectionRequestCommand.getNoMatchResultValue());
		nextLink.setParameter(selectionRequestCommand.getSelectionName(), null);
		noMatch.addAction(new Goto(nextLink.toString()));
		field.addEventHandler(noMatch);
		ILink hangupLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
			hangupLink.setParameters(parameterNames[i], selectionRequestCommand
					.getParameterValues(parameterNames[i]));
		hangupLink.setParameter(selectionRequestCommand.getResultName(),
				selectionRequestCommand.getHangupResultValue());
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		field.addEventHandler(disconnectCatch);
		Form form = new Form("MenuRewriteForm"); //$NON-NLS-1$
		form.addFormElement(field);
		return createVXMLDocument(links, form);
	}
}
