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
package org.eclipse.vtp.framework.interactions.voice.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.vtp.framework.common.IBrand;
import org.eclipse.vtp.framework.common.IBrandSelection;
import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.interactions.core.IInteractionTypeSelection;
import org.eclipse.vtp.framework.interactions.core.ILanguageSelection;
import org.eclipse.vtp.framework.interactions.core.commands.BridgeMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.DataRequestCommand;
import org.eclipse.vtp.framework.interactions.core.commands.EndMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.ExternalReferenceCommand;
import org.eclipse.vtp.framework.interactions.core.commands.FinalCommand;
import org.eclipse.vtp.framework.interactions.core.commands.InitialCommand;
import org.eclipse.vtp.framework.interactions.core.commands.InputRequestCommand;
import org.eclipse.vtp.framework.interactions.core.commands.OutputMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.SelectionRequestCommand;
import org.eclipse.vtp.framework.interactions.core.commands.TransferMessageCommand;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProviderRegistry;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.interactions.core.platforms.ILink;
import org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory;
import org.eclipse.vtp.framework.interactions.core.services.ExtendedActionEventManager;
import org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform;
import org.eclipse.vtp.framework.interactions.voice.vxml.Assignment;
import org.eclipse.vtp.framework.interactions.voice.vxml.AudioOutput;
import org.eclipse.vtp.framework.interactions.voice.vxml.Block;
import org.eclipse.vtp.framework.interactions.voice.vxml.Catch;
import org.eclipse.vtp.framework.interactions.voice.vxml.Choice;
import org.eclipse.vtp.framework.interactions.voice.vxml.Dialog;
import org.eclipse.vtp.framework.interactions.voice.vxml.Else;
import org.eclipse.vtp.framework.interactions.voice.vxml.ElseIf;
import org.eclipse.vtp.framework.interactions.voice.vxml.Exit;
import org.eclipse.vtp.framework.interactions.voice.vxml.ExternalGrammar;
import org.eclipse.vtp.framework.interactions.voice.vxml.Field;
import org.eclipse.vtp.framework.interactions.voice.vxml.Filled;
import org.eclipse.vtp.framework.interactions.voice.vxml.Form;
import org.eclipse.vtp.framework.interactions.voice.vxml.Goto;
import org.eclipse.vtp.framework.interactions.voice.vxml.If;
import org.eclipse.vtp.framework.interactions.voice.vxml.Menu;
import org.eclipse.vtp.framework.interactions.voice.vxml.NoInput;
import org.eclipse.vtp.framework.interactions.voice.vxml.NoMatch;
import org.eclipse.vtp.framework.interactions.voice.vxml.OutputSet;
import org.eclipse.vtp.framework.interactions.voice.vxml.Parameter;
import org.eclipse.vtp.framework.interactions.voice.vxml.Prompt;
import org.eclipse.vtp.framework.interactions.voice.vxml.RawInlineGrammar;
import org.eclipse.vtp.framework.interactions.voice.vxml.Recording;
import org.eclipse.vtp.framework.interactions.voice.vxml.Return;
import org.eclipse.vtp.framework.interactions.voice.vxml.SSMLMarkOutput;
import org.eclipse.vtp.framework.interactions.voice.vxml.Script;
import org.eclipse.vtp.framework.interactions.voice.vxml.Subdialog;
import org.eclipse.vtp.framework.interactions.voice.vxml.Submit;
import org.eclipse.vtp.framework.interactions.voice.vxml.TextOutput;
import org.eclipse.vtp.framework.interactions.voice.vxml.Transfer;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLConstants;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLDocument;
import org.eclipse.vtp.framework.interactions.voice.vxml.Variable;

/**
 * A generic implementation of a VXML platform.
 * 
 * @author Lonnie Pryor
 */
public class VoicePlatform extends AbstractPlatform implements VXMLConstants
{
	protected static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
	protected static final BigDecimal TEN = new BigDecimal("10");
	/** Comment for VXML_BUILTIN_PREFIX. */
	protected static final String VXML_BUILTIN_PREFIX = "VXML:Builtin:";

	/** The media provider. */
	private IMediaProvider mediaProvider;
	private final IExecutionContext context;

	/**
	 * Creates a new VoicePlatform.
	 */
	public VoicePlatform(IExecutionContext context)
	{
		this.context = context;
	}
	
	public Locale getCurrentLocale()
	{
		if(mediaProvider == null)
		{
			IInteractionTypeSelection interactionTypeSelection =
				(IInteractionTypeSelection)context.lookup(IInteractionTypeSelection.class.getName());
			System.out.println(interactionTypeSelection);
			System.out.println(interactionTypeSelection.getSelectedInteractionType());
			String interactionTypeID = interactionTypeSelection
					.getSelectedInteractionType().getId();
			ILanguageSelection languageSelection =
				(ILanguageSelection)context.lookup(ILanguageSelection.class.getName());
			String languageID = languageSelection.getSelectedLanguage();
			IBrandSelection brandSelection =
					(IBrandSelection)context.lookup(IBrandSelection.class.getName());
			IBrand brand = brandSelection.getSelectedBrand();
			IMediaProviderRegistry mediaProviderRegistry =
					(IMediaProviderRegistry)context.lookup(IMediaProviderRegistry.class.getName());
			String mediaProviderId = mediaProviderRegistry.lookupMediaProviderID(brand.getId(), interactionTypeID, languageID);
			this.mediaProvider = mediaProviderRegistry.getMediaProvider(mediaProviderId);
		}
		String languageCode = mediaProvider.getFormatter().getLanguageCode();
		Pattern p = Pattern.compile("^([a-zA-Z]+)([-_]([a-zA-Z]+)([-_](.+))?)?");
		Matcher matcher = p.matcher(languageCode);
		if(!matcher.find())
			return Locale.getDefault();
		String language = matcher.group(1);
		if(language == null)
			language = "";
		String country = matcher.group(3);
		if(country == null)
			country = "";
		String variant = matcher.group(5);
		if(variant == null)
			variant = "";
		return new Locale(language, country, variant);
	}
	
	public TimeValue getMinimumTimeValue(String property)
	{
		return new TimeValue(0);
	}
	
	public TimeValue getMaximumTimeValue(String property)
	{
		return new TimeValue(Long.MAX_VALUE);
	}
	
	public TimeValue resolveTimeValue(String property, String value)
	{
		if(value == null)
			return null;
		TimeValue setting = new TimeValue(value);
		TimeValue minimum = getMinimumTimeValue(property);
		TimeValue maximum = getMaximumTimeValue(property);
		if(minimum.compareTo(setting) > 0)
			return minimum;
		if(setting.compareTo(maximum) > 0)
			return maximum;
		return setting;
	}
	
	public AudioOutput generateAudioChain(ILinkFactory links, String path)
	{
		AudioOutput output = new AudioOutput(links.createResourceLink(path).toString());
		if(path.startsWith("http://"))
			return output;
		try
		{
			InitialContext jndiContext = new InitialContext();
			String mediaServerCountString = (String)jndiContext.lookup("java:comp/env/media-server-count");
			if(mediaServerCountString != null)
			{
				int count = 0;
				try
				{
					count = Integer.parseInt(mediaServerCountString);
				}
				catch (NumberFormatException e)
				{
				}
				AudioOutput current = output;
				for(int i = 1; i < count + 1; i++)
				{
					try
					{
						String serverPrefix = (String)jndiContext.lookup("java:comp/env/media-server-" + i);
						if(serverPrefix != null)
						{
							String fullPath = serverPrefix + path;
							if(!serverPrefix.endsWith("/") && !path.startsWith("/"))
								fullPath = serverPrefix + "/" + path;
							if(i == 1)
							{
								current.setAudioFileURI(fullPath); 
							}
							else
							{
								AudioOutput ao = new AudioOutput(fullPath);
								current.addOutput(ao);
								current = ao;
							}
						}
					}
					catch (NamingException e)
					{
					}
				}
			}
		}
		catch (NamingException e)
		{
		}
		return output;
	}

	/**
	 * Creates a new VXML document that contains the supplied dialog.
	 * 
	 * @param links The link factory.
	 * @param dialog The dialog to add to the new document.
	 * @return A new VXML document that contains the supplied dialog.
	 */
	protected VXMLDocument createVXMLDocument(ILinkFactory links, Dialog dialog)
	{
		VXMLDocument document = new VXMLDocument();
		document.addDialog(dialog);
		Catch hangupCatch = new Catch("connection.disconnect.hangup"); //$NON-NLS-1$
		hangupCatch.addAction(new Goto(links.createAbortLink().toString()));
		document.addEventHandler(hangupCatch);
		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      renderOutputMessage(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.commands.
	 *      OutputMessageCommand)
	 */
	protected IDocument renderOutputMessage(ILinkFactory links,
			OutputMessageCommand outputMessageCommand)
	{
		String bargeIn = outputMessageCommand.getPropertyValue("barge-in"); //$NON-NLS-1$
		if (Boolean.TRUE.toString().equalsIgnoreCase(bargeIn))
			bargeIn = Boolean.TRUE.toString();
		else if (Boolean.FALSE.toString().equalsIgnoreCase(bargeIn))
			bargeIn = Boolean.FALSE.toString();
		else
			bargeIn = null;
		Form form = new Form("OutputMessageForm"); //$NON-NLS-1$
		if (bargeIn != null)
			form.setProperty(NAME_BARGEIN, bargeIn);
		Block block = new Block("OutputMessageBlock"); //$NON-NLS-1$
		OutputSet outputs = new OutputSet();
		for (int i = 0; i < outputMessageCommand.getOutputCount(); ++i)
		{
			String outputValue = outputMessageCommand.getOutputValue(i);
			switch (outputMessageCommand.getOutputType(i))
			{
				case OutputMessageCommand.OUTPUT_TYPE_FILE:
					outputs.addOutput(generateAudioChain(links, outputValue));
					break;
				case OutputMessageCommand.OUTPUT_TYPE_TEXT:
				{
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
		}
		// if (outputs.getOutputs().length == 0)
		// return null;
		Prompt prompt = new Prompt(outputs);
		if (bargeIn != null)
			prompt.setBargeInEnabled(Boolean.valueOf(bargeIn).booleanValue());
		prompt.setLanguage(getCurrentLocale());
		block.addAction(prompt);
		ILink nextLink = links.createNextLink();
		String[] parameterNames = outputMessageCommand.getParameterNames();
		for (int i = 0; i < parameterNames.length; ++i)
			nextLink.setParameters(parameterNames[i], outputMessageCommand
					.getParameterValues(parameterNames[i]));
		nextLink.setParameter(outputMessageCommand.getResultName(), outputMessageCommand
				.getFilledResultValue());
		block.addAction(new Goto(nextLink.toString()));
		ILink hangupLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
			hangupLink.setParameters(parameterNames[i], outputMessageCommand
					.getParameterValues(parameterNames[i]));
		hangupLink.setParameter(outputMessageCommand.getResultName(),
				outputMessageCommand.getHangupResultValue());
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		form.addEventHandler(disconnectCatch);
		form.addFormElement(block);
		List<String> events = ExtendedActionEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			ILink eventLink = links.createNextLink();
			for (int i = 0; i < parameterNames.length; ++i)
				eventLink.setParameters(parameterNames[i], outputMessageCommand
						.getParameterValues(parameterNames[i]));
			eventLink.setParameter(outputMessageCommand.getResultName(), event);
			Catch eventCatch = new Catch(event);
			eventCatch.addAction(new Goto(eventLink.toString()));
			form.addEventHandler(eventCatch);
		}
		return createVXMLDocument(links, form);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      renderInitialDocument(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.commands.InitialCommand)
	 */
	protected IDocument renderInitialDocument(ILinkFactory links,
			InitialCommand initialCommand)
	{
		Form form = new Form("InitialForm"); //$NON-NLS-1$
		Map<String, String> varMap = new LinkedHashMap<String, String>();
		generateInitialVariableRequests(varMap);
		for (String key : varMap.keySet())
		{
			form.addVariable(new Variable(key, "''")); //$NON-NLS-1$
		}
		String[] variables = initialCommand.getVariableNames();
		for (int i = 0; i < variables.length; ++i)
		{
			String value = initialCommand.getVariableValue(variables[i]);
			if (value == null)
				value = ""; //$NON-NLS-1$
			form.addVariable(new Variable(variables[i], "'" + value + "'"));
		}
		Block block = new Block("InitialBlock"); //$NON-NLS-1$
		for (String key : varMap.keySet())
		{
			block.addAction(new Assignment(key, varMap.get(key)));
		}
		ILink nextLink = links.createNextLink();
		String[] parameterNames = initialCommand.getParameterNames();
		for (int i = 0; i < parameterNames.length; ++i)
			nextLink.setParameters(parameterNames[i], initialCommand
					.getParameterValues(parameterNames[i]));
		nextLink.setParameter(initialCommand.getResultName(), initialCommand
				.getResultValue());
		String[] fields = new String[varMap.size() + variables.length];
		int j = 0;
		for (String key : varMap.keySet())
		{
			fields[j] = key;
			++j;
		}
		System.arraycopy(variables, 0, fields, varMap.size(), variables.length);
		Submit submit = new Submit(nextLink.toString(), fields);
		submit.setMethod("post");
		block.addAction(submit);
		form.addFormElement(block);
		ILink hangupLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
			hangupLink.setParameters(parameterNames[i], initialCommand
					.getParameterValues(parameterNames[i]));
		hangupLink.setParameter(initialCommand.getResultName(),
				initialCommand.getHangupResultValue());
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		form.addEventHandler(disconnectCatch);
		VXMLDocument document = createVXMLDocument(links, form);
		document.setProperty("documentmaxage", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("documentmaxstale", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		List<String> events = ExtendedActionEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			ILink eventLink = links.createNextLink();
			for (int i = 0; i < parameterNames.length; ++i)
				eventLink.setParameters(parameterNames[i], initialCommand
						.getParameterValues(parameterNames[i]));
			eventLink.setParameter(initialCommand.getResultName(), event);
			Catch eventCatch = new Catch(event);
			eventCatch.addAction(new Goto(eventLink.toString()));
			form.addEventHandler(eventCatch);
		}
		return document;
	}

	public void generateInitialVariableRequests(Map<String, String> variables)
	{
		super.generateInitialVariableRequests(variables);
		variables.put("PLATFORM_ANI", //$NON-NLS-1$
				"session.connection.remote.uri"); //$NON-NLS-1$
		variables.put("PLATFORM_DNIS", //$NON-NLS-1$
				"session.connection.local.uri"); //$NON-NLS-1$
	}
	
	public List<String> getPlatformVariableNames()
	{
		List<String> vars = super.getPlatformVariableNames();
		return vars;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      renderInputRequest(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.commands.
	 *      InputRequestCommand)
	 */
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
		TimeValue timeout = 
				resolveTimeValue("initial-timeout", inputRequestCommand
				.getPropertyValue("initial-timeout")); //$NON-NLS-1$
		String inputMode = inputRequestCommand.getPropertyValue("input-mode"); //$NON-NLS-1$
		if (inputMode == null || inputMode.length() == 0)
			inputMode = "dtmf only"; //$NON-NLS-1$
		String confidenceLevel = inputRequestCommand
				.getPropertyValue("confidence-level"); //$NON-NLS-1$
		String sensitivity = inputRequestCommand
				.getPropertyValue("sensitivity-level"); //$NON-NLS-1$
		String speedVsAccuracy = inputRequestCommand
				.getPropertyValue("speed-vs-accuracy"); //$NON-NLS-1$
		TimeValue speechCompletionTimeout =
				resolveTimeValue("speech-complete-timeout", inputRequestCommand
				.getPropertyValue("speech-complete-timeout")); //$NON-NLS-1$
		TimeValue speechIncompleteTimeout =
				resolveTimeValue("speech-incomplete-timeout", inputRequestCommand
				.getPropertyValue("speech-incomplete-timeout")); //$NON-NLS-1$
		TimeValue maxSpeechLength =
				resolveTimeValue("max-speech-timeout", inputRequestCommand
				.getPropertyValue("max-speech-timeout")); //$NON-NLS-1$
		String maxNBest = inputRequestCommand.getPropertyValue("max-n-best"); //$NON-NLS-1$
		TimeValue interDigitTimeout =
				resolveTimeValue("interdigit-timeout", inputRequestCommand
				.getPropertyValue("interdigit-timeout")); //$NON-NLS-1$
		TimeValue terminationTimeout =
				resolveTimeValue("termination-timeout", inputRequestCommand
				.getPropertyValue("termination-timeout")); //$NON-NLS-1$
		String terminationCharacter = inputRequestCommand
				.getPropertyValue("termination-character"); //$NON-NLS-1$
		Field field = new Field(inputRequestCommand.getDataName());
		if (bargeIn != null)
			field.setProperty(NAME_BARGEIN, bargeIn);
		if (timeout != null)
			field.setProperty(NAME_TIMEOUT, timeout.toTimeString(TimeValue.SECONDS)); //$NON-NLS-1$
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
			if (speechCompletionTimeout != null)
				field.setProperty(NAME_COMPLETETIMEOUT, speechCompletionTimeout.toTimeString(TimeValue.SECONDS)); //$NON-NLS-1$
			if (speechIncompleteTimeout != null)
				field.setProperty(NAME_INCOMPLETETIMEOUT,
					speechIncompleteTimeout.toTimeString(TimeValue.SECONDS)); //$NON-NLS-1$
			if (maxSpeechLength != null)
				field.setProperty(NAME_MAXSPEECHTIMEOUT, maxSpeechLength.toTimeString(TimeValue.SECONDS)); //$NON-NLS-1$
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
			if (interDigitTimeout != null)
				field.setProperty(NAME_INTERDIGITTIMEOUT, interDigitTimeout.toTimeString(TimeValue.SECONDS)); //$NON-NLS-1$
			if (terminationTimeout != null)
				field.setProperty(NAME_TERMTIMEOUT, terminationTimeout.toTimeString(TimeValue.SECONDS)); //$NON-NLS-1$
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
				outputs.addOutput(generateAudioChain(links, outputValue));
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
					field.setType(customData.substring(VXML_BUILTIN_PREFIX.length()));
				break;
			case InputRequestCommand.INPUT_TYPE_INLINE:
				field.addGrammar(new RawInlineGrammar(inputRequestCommand.getInputValue()));
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
					field.setType(customData.substring(VXML_BUILTIN_PREFIX.length()));
				break;
			case InputRequestCommand.INPUT_TYPE_INLINE:
				field.addGrammar(new RawInlineGrammar(inputRequestCommand.getInput2Value()));
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
		If ifTag2 = new If("typeof(application.lastresult$.markname) == 'string'");
		Script markScript2 = new Script();
		markScript2.setText( "		lastresult = lastresult + '<mark name=\"' + application.lastresult$.markname + '\" offset=\"' + application.lastresult$.marktime + '\"/>';\r\n" +
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
		ifTag2.addScript(markScript2);
		Else elseTag2 = new Else();
		Script noMarkScript2 = new Script();
		noMarkScript2.setText( "		lastresult = lastresult + '<mark name=\"NONE\" offset=\"0\"/>';\r\n" +
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
		elseTag2.addScript(noMarkScript2);
		ifTag2.setElse(elseTag2);
		filled.addIfClause(ifTag2);
		Submit filledSubmit = new Submit(filledLink.toString(),
				new String[] { inputRequestCommand.getDataName(), "lastresult" });
		filledSubmit.setMethod(METHOD_POST);
		filled.addAction(filledSubmit);
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
		Submit noMatchSubmit = new Submit(noMatchLink.toString(),
				new String[] { inputRequestCommand.getDataName(), "lastresult" });
		noMatchSubmit.setMethod(METHOD_POST);
		noMatch.addAction(noMatchSubmit);
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
		List<String> events = ExtendedActionEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			ILink eventLink = links.createNextLink();
			for (int i = 0; i < parameterNames.length; ++i)
				eventLink.setParameters(parameterNames[i], inputRequestCommand
						.getParameterValues(parameterNames[i]));
			eventLink.setParameter(inputRequestCommand.getResultName(), event);
			Catch eventCatch = new Catch(event);
			eventCatch.addAction(new Goto(eventLink.toString()));
			form.addEventHandler(eventCatch);
		}
		return createVXMLDocument(links, form);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      renderSelectionRequest(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.commands.
	 *      SelectionRequestCommand)
	 */
	protected IDocument renderSelectionRequest(ILinkFactory links,
			SelectionRequestCommand selectionRequestCommand)
	{
		String bargeIn = getNormalizedBoolean(selectionRequestCommand
				.getPropertyValue("barge-in")); //$NON-NLS-1$
		TimeValue timeout = 
				resolveTimeValue("initial-timeout", selectionRequestCommand
				.getPropertyValue("initial-timeout")); //$NON-NLS-1$
		String inputMode = selectionRequestCommand.getPropertyValue("input-mode"); //$NON-NLS-1$
		if (inputMode == null || inputMode.length() == 0)
			inputMode = "dtmf only"; //$NON-NLS-1$
		String confidenceLevel = selectionRequestCommand
				.getPropertyValue("confidence-level"); //$NON-NLS-1$
		String sensitivity = selectionRequestCommand
				.getPropertyValue("sensitivity-level"); //$NON-NLS-1$
		String speedVsAccuracy = selectionRequestCommand
				.getPropertyValue("speed-vs-accuracy"); //$NON-NLS-1$
		TimeValue speechCompletionTimeout =
				resolveTimeValue("speech-complete-timeout", selectionRequestCommand
				.getPropertyValue("speech-complete-timeout")); //$NON-NLS-1$
		TimeValue speechIncompleteTimeout =
				resolveTimeValue("speech-incomplete-timeout", selectionRequestCommand
				.getPropertyValue("speech-incomplete-timeout")); //$NON-NLS-1$
		TimeValue maxSpeechLength =
				resolveTimeValue("max-speech-timeout", selectionRequestCommand
				.getPropertyValue("max-speech-timeout")); //$NON-NLS-1$
		String maxNBest = selectionRequestCommand.getPropertyValue("max-n-best"); //$NON-NLS-1$
		TimeValue interDigitTimeout =
				resolveTimeValue("interdigit-timeout", selectionRequestCommand
				.getPropertyValue("interdigit-timeout")); //$NON-NLS-1$
		TimeValue terminationTimeout =
				resolveTimeValue("termination-timeout", selectionRequestCommand
				.getPropertyValue("termination-timeout")); //$NON-NLS-1$
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
				outputs.addOutput(generateAudioChain(links, outputValue));
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
		Menu menu = new Menu(selectionRequestCommand.getSelectionName(), prompt);
		if (bargeIn != null)
			menu.setProperty(NAME_BARGEIN, bargeIn);
		if (timeout != null)
			menu.setProperty(NAME_TIMEOUT, timeout.toTimeString(TimeValue.SECONDS)); //$NON-NLS-1$
		if ("hybrid".equalsIgnoreCase(inputMode))
		{
			menu.setProperty(NAME_INPUTMODES, "dtmf voice"); //$NON-NLS-1$
			menu.setProperty("com.telera.speechenabled", Boolean.TRUE.toString()); //$NON-NLS-1$
		}
		if ("dtmf only".equalsIgnoreCase(inputMode)) //$NON-NLS-1$
		{
			grammarMode = GRAMMAR_MODE_DTMF;
			menu.setProperty(NAME_INPUTMODES, "dtmf"); //$NON-NLS-1$
			menu.setProperty("com.telera.speechenabled", Boolean.FALSE.toString()); //$NON-NLS-1$
		}
		else
		{
			try
			{
				if (confidenceLevel != null && confidenceLevel.length() > 0)
					menu.setProperty(NAME_CONFIDENCELEVEL,
							new BigDecimal(confidenceLevel).divide(ONE_HUNDRED).toString());
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			try
			{
				if (sensitivity != null && sensitivity.length() > 0)
					menu.setProperty(NAME_SENSITIVITY, new BigDecimal(sensitivity)
							.divide(ONE_HUNDRED).toString());
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			try
			{
				if (speedVsAccuracy != null && speedVsAccuracy.length() > 0)
					menu.setProperty(NAME_SPEEDVSACCURACY,
							new BigDecimal(speedVsAccuracy).divide(ONE_HUNDRED).toString());
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			if (speechCompletionTimeout != null)
				menu.setProperty(NAME_COMPLETETIMEOUT, speechCompletionTimeout.toTimeString(TimeValue.SECONDS)); //$NON-NLS-1$
			if (speechIncompleteTimeout != null)
				menu.setProperty(NAME_INCOMPLETETIMEOUT, speechIncompleteTimeout.toTimeString(TimeValue.SECONDS)); //$NON-NLS-1$
			if (maxSpeechLength != null)
				menu.setProperty(NAME_MAXSPEECHTIMEOUT, maxSpeechLength.toTimeString(TimeValue.SECONDS)); //$NON-NLS-1$
			else
				menu.setProperty(NAME_MAXSPEECHTIMEOUT, "10s"); //$NON-NLS-1$
			if (maxNBest != null && maxNBest.length() > 0)
				menu.setProperty(NAME_MAXNBEST, maxNBest);
		}
		if ("voice only".equalsIgnoreCase(inputMode)) //$NON-NLS-1$
		{
			grammarMode = GRAMMAR_MODE_VOICE;
			menu.setProperty(NAME_INPUTMODES, "voice"); //$NON-NLS-1$
			menu.setProperty("com.telera.speechenabled", Boolean.TRUE.toString()); //$NON-NLS-1$
		}
		else
		{
			if (interDigitTimeout != null)
				menu.setProperty(NAME_INTERDIGITTIMEOUT, interDigitTimeout.toTimeString(TimeValue.SECONDS)); //$NON-NLS-1$
			if (terminationTimeout != null)
				menu.setProperty(NAME_TERMTIMEOUT, terminationTimeout.toTimeString(TimeValue.SECONDS)); //$NON-NLS-1$
			if (terminationCharacter != null && terminationCharacter.length() > 0
					&& !"none".equalsIgnoreCase(terminationCharacter)) //$NON-NLS-1$
				menu.setProperty(NAME_TERMCHAR, terminationCharacter);
			else
				menu.setProperty(NAME_TERMCHAR, ""); //$NON-NLS-1$
		}
		ILink nextLink = links.createNextLink();
		String[] parameterNames = selectionRequestCommand.getParameterNames();
		for (int i = 0; i < parameterNames.length; ++i)
			nextLink.setParameters(parameterNames[i], selectionRequestCommand
					.getParameterValues(parameterNames[i]));
		nextLink.setParameter(selectionRequestCommand.getResultName(),
				selectionRequestCommand.getFilledResultValue());
		for (int i = 0; i < selectionRequestCommand.getOptionCount(); ++i)
		{
			String silent = selectionRequestCommand.getOptionProperty(i, "silent"); //$NON-NLS-1$
			if (Boolean.TRUE.toString().equalsIgnoreCase(silent))
				silent = Boolean.TRUE.toString();
			else if (Boolean.FALSE.toString().equalsIgnoreCase(silent))
				silent = Boolean.FALSE.toString();
			else
				silent = null;
			String dtmf = selectionRequestCommand.getOptionProperty(i, "dtmf"); //$NON-NLS-1$
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
								.addOutput(generateAudioChain(links, optionOutputValue));
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
			nextLink.setParameter(selectionRequestCommand.getSelectionName(),
					selectionRequestCommand.getOption(i));
			Choice choice = new Choice(nextLink.toString());
			if (!GRAMMAR_MODE_DTMF.equals(grammarMode))
			{
				switch (selectionRequestCommand.getOptionInputType(i))
				{
				case SelectionRequestCommand.INPUT_TYPE_FILE:
					choice.setGrammar(new ExternalGrammar(GRAMMAR_MODE_VOICE, links
							.createResourceLink(
									selectionRequestCommand.getOptionInputValue(i)).toString()));
					break;
				case InputRequestCommand.INPUT_TYPE_INLINE:
					choice.setGrammar(new RawInlineGrammar(selectionRequestCommand.getOptionInputValue(i)));
					break;
				}
			}
			if (!GRAMMAR_MODE_VOICE.equals(grammarMode))
				choice.setDTMF(dtmf == null ? "0" : dtmf);
			menu.addChoice(choice);
		}
		NoInput noInput = new NoInput();
		nextLink.setParameter(selectionRequestCommand.getResultName(),
				selectionRequestCommand.getNoInputResultValue());
		nextLink.setParameter(selectionRequestCommand.getSelectionName(), null);
		noInput.addAction(new Goto(nextLink.toString()));
		menu.addEventHandler(noInput);
		NoMatch noMatch = new NoMatch();
		nextLink.setParameter(selectionRequestCommand.getResultName(),
				selectionRequestCommand.getNoMatchResultValue());
		nextLink.setParameter(selectionRequestCommand.getSelectionName(), null);
		noMatch.addAction(new Goto(nextLink.toString()));
		menu.addEventHandler(noMatch);
		ILink hangupLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
			hangupLink.setParameters(parameterNames[i], selectionRequestCommand
					.getParameterValues(parameterNames[i]));
		hangupLink.setParameter(selectionRequestCommand.getResultName(),
				selectionRequestCommand.getHangupResultValue());
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		menu.addEventHandler(disconnectCatch);
		List<String> events = ExtendedActionEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			ILink eventLink = links.createNextLink();
			for (int i = 0; i < parameterNames.length; ++i)
				eventLink.setParameters(parameterNames[i], selectionRequestCommand
						.getParameterValues(parameterNames[i]));
			eventLink.setParameter(selectionRequestCommand.getResultName(), event);
			Catch eventCatch = new Catch(event);
			eventCatch.addAction(new Goto(eventLink.toString()));
			menu.addEventHandler(eventCatch);
		}
		return createVXMLDocument(links, menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      renderDataRequest(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.commands.
	 *      DataRequestCommand)
	 */
	protected IDocument renderDataRequest(ILinkFactory links,
			DataRequestCommand dataRequestCommand)
	{
		Form form = new Form("DataRequestForm"); //$NON-NLS-1$
		String bargeIn = getNormalizedBoolean(dataRequestCommand
				.getPropertyValue("barge-in")); //$NON-NLS-1$
		String playBeep = getNormalizedBoolean(dataRequestCommand
				.getPropertyValue("play-beep")); //$NON-NLS-1$
		String dtmfTerm = getNormalizedBoolean(dataRequestCommand
				.getPropertyValue("dtmf-termination")); //$NON-NLS-1$
		String initialTimeout = dataRequestCommand
				.getPropertyValue("initial-timeout"); //$NON-NLS-1$
		String finalSilence = dataRequestCommand
				.getPropertyValue("final-silence-timeout"); //$NON-NLS-1$
		String maxRecordTime = dataRequestCommand
				.getPropertyValue("max-record-time"); //$NON-NLS-1$
		Recording recording = new Recording(dataRequestCommand.getDataName());
		recording.setFileType("audio/x-wav"); //$NON-NLS-1$
		recording.setProperty(NAME_INPUTMODES, "dtmf"); //$NON-NLS-1$
		if (playBeep != null)
			recording.setBeepEnabled(Boolean.valueOf(playBeep).booleanValue());
		if (dtmfTerm != null)
			recording.setDtmfTermEnabled(Boolean.valueOf(dtmfTerm).booleanValue());
		if (initialTimeout != null && initialTimeout.length() > 0)
			recording.setTimeout(initialTimeout + "s"); //$NON-NLS-1$
		if (finalSilence != null && finalSilence.length() > 0)
			recording.setFinalSilence(finalSilence + "s"); //$NON-NLS-1$
		if (maxRecordTime != null && maxRecordTime.length() > 0)
			recording.setMaxtime(maxRecordTime + "s"); //$NON-NLS-1$
		else
			recording.setMaxtime("10s"); //$NON-NLS-1$
		OutputSet outputs = new OutputSet();
		for (int i = 0; i < dataRequestCommand.getOutputCount(); ++i)
		{
			String outputValue = dataRequestCommand.getOutputValue(i);
			switch (dataRequestCommand.getOutputType(i))
			{
			case DataRequestCommand.OUTPUT_TYPE_FILE:
				outputs.addOutput(generateAudioChain(links, outputValue));
				break;
			case DataRequestCommand.OUTPUT_TYPE_TEXT:
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
		recording.setPrompt(prompt);
		String[] parameterNames = dataRequestCommand.getParameterNames();
		String[] submitVars = new String[parameterNames.length + 4];
		submitVars[0] = dataRequestCommand.getDataName();
		submitVars[1] = dataRequestCommand.getResultName();
		submitVars[2] = dataRequestCommand.getDataName() + "_termchar";
		submitVars[3] = "lastresult";
		Filled filled = new Filled();
		filled.addVariable(new Variable(dataRequestCommand.getResultName(), "'"
				+ dataRequestCommand.getFilledResultValue() + "'"));
		filled.addVariable(new Variable(dataRequestCommand.getDataName() + "_termchar", dataRequestCommand.getDataName() + "$.termchar"));
		for (int i = 0; i < parameterNames.length; ++i)
		{
			submitVars[i + 4] = parameterNames[i];
			String[] values = dataRequestCommand
					.getParameterValues(parameterNames[i]);
			StringBuffer buf = new StringBuffer();
			for (int v = 0; v < values.length; v++)
			{
				buf.append(values[v]);
				if (v < values.length - 1)
					buf.append(',');
			}
			Variable paramVar = new Variable(parameterNames[i], "'" + buf.toString()
					+ "'");
			filled.addVariable(paramVar);
		}
		filled.addVariable(new Variable("lastresult", "''"));
/*		filled.addVariable(new Variable("lastresult", "'<lastresult>'"));
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
*/		ILink filledLink = links.createNextLink();
		Submit submit = new Submit(filledLink.toString(), submitVars);
		submit.setMethod(VXMLConstants.METHOD_POST);
		submit.setEncodingType("multipart/form-data");
		filled.addAction(submit);
		recording.addFilledHandler(filled);
		ILink noInputLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
		{
			noInputLink.setParameters(parameterNames[i], dataRequestCommand
					.getParameterValues(parameterNames[i]));
		}
		noInputLink.setParameter(dataRequestCommand.getResultName(),
				dataRequestCommand.getNoInputResultValue());
		NoInput noInput = new NoInput();
		noInput.addAction(new Submit(noInputLink.toString(),
				new String[] { dataRequestCommand.getDataName() }));
		recording.addEventHandler(noInput);
		form.addFormElement(recording);
		ILink hangupLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
			hangupLink.setParameters(parameterNames[i], dataRequestCommand
					.getParameterValues(parameterNames[i]));
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		String[] disconnectVars = new String[parameterNames.length + 2];
		disconnectVars[0] = dataRequestCommand.getDataName();
		disconnectVars[1] = dataRequestCommand.getResultName();
		disconnectCatch.addVariable(new Variable(dataRequestCommand.getResultName(), "'"
				+ dataRequestCommand.getHangupResultValue() + "'"));
		for (int i = 0; i < parameterNames.length; ++i)
		{
			disconnectVars[i + 2] = parameterNames[i];
			String[] values = dataRequestCommand
					.getParameterValues(parameterNames[i]);
			StringBuffer buf = new StringBuffer();
			for (int v = 0; v < values.length; v++)
			{
				buf.append(values[v]);
				if (v < values.length - 1)
					buf.append(',');
			}
			Variable paramVar = new Variable(parameterNames[i], "'" + buf.toString()
					+ "'");
			disconnectCatch.addVariable(paramVar);
		}
		Submit disconnectSubmit = new Submit(hangupLink.toString(), disconnectVars);
		disconnectSubmit.setMethod(VXMLConstants.METHOD_POST);
		disconnectSubmit.setEncodingType("multipart/form-data");
		disconnectCatch.addAction(disconnectSubmit);
		recording.addEventHandler(disconnectCatch);
		List<String> events = ExtendedActionEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			ILink eventLink = links.createNextLink();
			for (int i = 0; i < parameterNames.length; ++i)
				eventLink.setParameters(parameterNames[i], dataRequestCommand
						.getParameterValues(parameterNames[i]));
			eventLink.setParameter(dataRequestCommand.getResultName(), event);
			Catch eventCatch = new Catch(event);
			eventCatch.addAction(new Goto(eventLink.toString()));
			form.addEventHandler(eventCatch);
		}
		return createVXMLDocument(links, form);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.
	 *      AbstractPlatform#renderExternalReference(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.commands.
	 *      ExternalReferenceCommand)
	 */
	protected IDocument renderExternalReference(ILinkFactory links,
			ExternalReferenceCommand externalReferenceCommand)
	{
		Form form = new Form("ExternalReferenceForm"); //$NON-NLS-1$
		Subdialog subdialog = new Subdialog(externalReferenceCommand
				.getReferenceName());
		subdialog.setSourceURI(externalReferenceCommand.getReferenceURI());
		StringBuffer nameListBuffer = new StringBuffer();
		String[] sourceParameters = externalReferenceCommand.getURLParameterNames();
		for(int i = 0; i < sourceParameters.length; i++)
		{
			String sourceParameterValue = externalReferenceCommand.getURLParameterValue(sourceParameters[i]);
			form.addVariable(new Variable(sourceParameters[i], sourceParameterValue));
			nameListBuffer.append(sourceParameters[i]);
			if(i != sourceParameters.length - 1)
				nameListBuffer.append(' ');
		}
		subdialog.setNameList(nameListBuffer.toString());
		String[] inputArgNames = externalReferenceCommand.getInputArgumentNames();
		for (int i = 0; i < inputArgNames.length; ++i)
			subdialog.addParameter(new Parameter(inputArgNames[i],
					externalReferenceCommand.getInputArgumentValue(inputArgNames[i])));
		ILink nextLink = links.createNextLink();
		Filled filled = new Filled();
		List<String> submitNames = new ArrayList<String>();
		String[] parameterNames = externalReferenceCommand.getParameterNames();
		for (int i = 0; i < parameterNames.length; ++i)
		{
			submitNames.add(parameterNames[i]);
			String[] parameterValues = externalReferenceCommand.getParameterValues(parameterNames[i]);
			StringBuffer paramBuffer = new StringBuffer();
			for(int p = 0; p < parameterValues.length; p++)
			{
				String val = parameterValues[p];
				paramBuffer.append(val);
				if(p < parameterValues.length - 1)
				{
					paramBuffer.append(',');
				}
			}
			filled.addVariable(new Variable(parameterNames[i], "'" + paramBuffer.toString() + "'"));
		}
		submitNames.add(externalReferenceCommand.getResultName());
		filled.addVariable(new Variable(externalReferenceCommand.getResultName(), "'" + externalReferenceCommand.getFilledResultValue() + "'"));
		String[] outputArgNames = externalReferenceCommand.getOutputArgumentNames();
		for (int i = 0; i < outputArgNames.length; ++i)
		{
			submitNames.add(outputArgNames[i]);
			filled.addVariable(new Variable(outputArgNames[i],
					externalReferenceCommand.getReferenceName()
							+ "." + externalReferenceCommand //$NON-NLS-1$
									.getOutputArgumentValue(outputArgNames[i])));
		}
		Submit submit = new Submit(nextLink.toString(), submitNames.toArray(new String[submitNames.size()]));
		submit.setMethod(VXMLConstants.METHOD_POST);
		submit.setEncodingType("multipart/form-data");
		filled.addAction(submit);
		subdialog.addFilledHandler(filled);
		form.addFormElement(subdialog);
		ILink hangupLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
			hangupLink.setParameters(parameterNames[i], externalReferenceCommand
					.getParameterValues(parameterNames[i]));
		hangupLink.setParameter(externalReferenceCommand.getResultName(),
				externalReferenceCommand.getHangupResultValue());
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		form.addEventHandler(disconnectCatch);
		List<String> events = ExtendedActionEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			ILink eventLink = links.createNextLink();
			for (int i = 0; i < parameterNames.length; ++i)
				eventLink.setParameters(parameterNames[i], externalReferenceCommand
						.getParameterValues(parameterNames[i]));
			eventLink.setParameter(externalReferenceCommand.getResultName(), event);
			Catch eventCatch = new Catch(event);
			eventCatch.addAction(new Goto(eventLink.toString()));
			form.addEventHandler(eventCatch);
		}
		return createVXMLDocument(links, form);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      renderTransferMessage(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.commands.
	 *      TransferMessageCommand)
	 */
	protected IDocument renderTransferMessage(ILinkFactory links,
			TransferMessageCommand transferMessageCommand)
	{
		VXMLDocument document = new VXMLDocument();
		Form form = new Form("TransferMessageForm"); //$NON-NLS-1$
		form.addFormElement(new Transfer("TransferMessageElement", //$NON-NLS-1$
				transferMessageCommand.getDestination()));
		document.addDialog(form);
		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      renderBridgeMessage(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.commands.
	 *      BridgeMessageCommand)
	 */
	protected IDocument renderBridgeMessage(ILinkFactory links,
			BridgeMessageCommand bridgeMessageCommand)
	{
		Form form = new Form("BridgeMessageForm"); //$NON-NLS-1$
		Transfer tx = new Transfer("BridgeMessageElement", //$NON-NLS-1$
				bridgeMessageCommand.getDestination());
		tx.setTransferType(bridgeMessageCommand.getTransferType());
//		tx.setMaxTime("0s");
		Filled filled = new Filled();
		If ifBusy = new If("BridgeMessageElement == 'busy'");
		ILink link = links.createNextLink();
		link.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getBusyResultValue());
		ifBusy.addAction(new Goto(link.toString()));
		ElseIf ifNetworkBusy = new ElseIf("BridgeMessageElement == 'network_busy'");
		link.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getBusyResultValue());
		ifNetworkBusy.addAction(new Goto(link.toString()));
		ifBusy.addElseIf(ifNetworkBusy);
		ElseIf ifNoAnswer = new ElseIf("BridgeMessageElement == 'noanswer'");
		link.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getUnavailableResultValue());
		ifNoAnswer.addAction(new Goto(link.toString()));
		ifBusy.addElseIf(ifNoAnswer);
		ElseIf ifUnknown = new ElseIf("BridgeMessageElement == 'unknown'");
		link.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getUnavailableResultValue());
		ifUnknown.addAction(new Goto(link.toString()));
		ifBusy.addElseIf(ifUnknown);
		filled.addIfClause(ifBusy);
		tx.addFilledHandler(filled);
		
		//catch handlers
		Catch noAuthCatch = new Catch("error.connection.noauthorization");
		ILink noAuthLink = links.createNextLink();
		noAuthLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getNoAuthResultValue());
		noAuthCatch.addAction(new Goto(noAuthLink.toString()));
		tx.addEventHandler(noAuthCatch);
		
		Catch badDestCatch = new Catch("error.connection.baddestination");
		ILink badDestLink = links.createNextLink();
		badDestLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getBadDestResultValue());
		badDestCatch.addAction(new Goto(badDestLink.toString()));
		tx.addEventHandler(badDestCatch);
		
		Catch noRouteCatch = new Catch("error.connection.noroute");
		ILink noRouteLink = links.createNextLink();
		noRouteLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getNoRouteResultValue());
		noRouteCatch.addAction(new Goto(noRouteLink.toString()));
		tx.addEventHandler(noRouteCatch);
		
		Catch noResourceCatch = new Catch("error.connection.noresource");
		ILink noResourceLink = links.createNextLink();
		noResourceLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getNoResourceResultValue());
		noResourceCatch.addAction(new Goto(noResourceLink.toString()));
		tx.addEventHandler(noResourceCatch);
		
		Catch badProtocolCatch = new Catch("error.connection.protocol");
		ILink badProtocolLink = links.createNextLink();
		badProtocolLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getProtocolResultValue());
		badProtocolCatch.addAction(new Goto(badProtocolLink.toString()));
		tx.addEventHandler(badProtocolCatch);
		
		Catch bridgeUnsupportedCatch = new Catch("error.unsupported.transfer.bridge");
		ILink bridgeUnsupportedLink = links.createNextLink();
		bridgeUnsupportedLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getBadBridgeResultValue());
		bridgeUnsupportedCatch.addAction(new Goto(bridgeUnsupportedLink.toString()));
		tx.addEventHandler(bridgeUnsupportedCatch);
		
		Catch uriUnsupportedCatch = new Catch("error.unsupported.uri");
		ILink uriUnsupportedLink = links.createNextLink();
		uriUnsupportedLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getBadUriResultValue());
		uriUnsupportedCatch.addAction(new Goto(uriUnsupportedLink.toString()));
		tx.addEventHandler(uriUnsupportedCatch);
		
		Catch transferCatch = new Catch("connection.disconnect.transfer");
		ILink transferLink = links.createNextLink();
		transferLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getTransferredResultValue());
		transferCatch.addAction(new Goto(transferLink.toString()));
		tx.addEventHandler(transferCatch);

		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		ILink hangupLink = links.createNextLink();
		hangupLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getHangupResultValue());
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		tx.addEventHandler(disconnectCatch);
		
		form.addFormElement(tx);
		List<String> events = ExtendedActionEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			ILink eventLink = links.createNextLink();
			eventLink.setParameter(bridgeMessageCommand.getResultName(), event);
			Catch eventCatch = new Catch(event);
			eventCatch.addAction(new Goto(eventLink.toString()));
			form.addEventHandler(eventCatch);
		}
		return createVXMLDocument(links, form);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      renderEndMessage(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.commands.
	 *      EndMessageCommand)
	 */
	protected IDocument renderEndMessage(ILinkFactory links,
			EndMessageCommand endMessageCommand)
	{
		Form form = new Form("EndMessageForm"); //$NON-NLS-1$
		String[] variables = endMessageCommand.getVariableNames();
		for (int i = 0; i < variables.length; ++i)
			form.addVariable(new Variable(variables[i], "'"
					+ endMessageCommand.getVariable(variables[i]) + "'"));
		Block block = new Block("EndMessageBlock"); //$NON-NLS-1$
		block.addAction(new Exit(variables));
		form.addFormElement(block);
		VXMLDocument document = createVXMLDocument(links, form);
		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      renderFinalDocument(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.commands.FinalCommand)
	 */
	protected IDocument renderFinalDocument(ILinkFactory links,
			FinalCommand finalCommand)
	{
		Form form = new Form("FinalForm"); //$NON-NLS-1$
		String[] variables = finalCommand.getVariableNames();
		for (int i = 0; i < variables.length; ++i)
			form.addVariable(new Variable(variables[i], "'"
					+ finalCommand.getVariable(variables[i]) + "'"));
		Block block = new Block("FinalBlock"); //$NON-NLS-1$
		block.addAction(new Return(variables));
		form.addFormElement(block);
		VXMLDocument document = createVXMLDocument(links, form);
		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.IPlatform#
	 *      getInteractionTypeID()
	 */
	public String getInteractionTypeID()
	{
		return "org.eclipse.vtp.framework.interactions.voice.interaction"; //$NON-NLS-1$
	}

	public String getNormalizedBoolean(String parameter)
	{
		String ret = null;
		if (Boolean.TRUE.toString().equalsIgnoreCase(parameter))
		{
			ret = Boolean.TRUE.toString();
		}
		else if (Boolean.FALSE.toString().equalsIgnoreCase(parameter))
		{
			ret = Boolean.FALSE.toString();
		}
		return ret;
	}
}
