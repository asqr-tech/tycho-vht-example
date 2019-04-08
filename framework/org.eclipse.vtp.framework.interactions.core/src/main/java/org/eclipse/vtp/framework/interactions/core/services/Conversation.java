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
package org.eclipse.vtp.framework.interactions.core.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.framework.common.IBrand;
import org.eclipse.vtp.framework.common.IBrandSelection;
import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.ILastResult;
import org.eclipse.vtp.framework.common.ILastResultData;
import org.eclipse.vtp.framework.common.IMapObject;
import org.eclipse.vtp.framework.common.IScriptingEngine;
import org.eclipse.vtp.framework.common.IScriptingService;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.interactions.core.IInteractionTypeSelection;
import org.eclipse.vtp.framework.interactions.core.ILanguageSelection;
import org.eclipse.vtp.framework.interactions.core.commands.BridgeMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.ConversationCommand;
import org.eclipse.vtp.framework.interactions.core.commands.DataRequestCommand;
import org.eclipse.vtp.framework.interactions.core.commands.EndMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.ExternalReferenceCommand;
import org.eclipse.vtp.framework.interactions.core.commands.FinalCommand;
import org.eclipse.vtp.framework.interactions.core.commands.InitialCommand;
import org.eclipse.vtp.framework.interactions.core.commands.Input;
import org.eclipse.vtp.framework.interactions.core.commands.InputRequestCommand;
import org.eclipse.vtp.framework.interactions.core.commands.MetaDataMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.MetaDataRequestCommand;
import org.eclipse.vtp.framework.interactions.core.commands.Output;
import org.eclipse.vtp.framework.interactions.core.commands.OutputMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.SelectionRequestCommand;
import org.eclipse.vtp.framework.interactions.core.commands.TransferMessageCommand;
import org.eclipse.vtp.framework.interactions.core.configurations.BridgeMessageConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.DataRequestConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.ExternalReferenceConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.InputConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.InputRequestConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataItemConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputCase;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputContent;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputMessageConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputNode;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputSwitch;
import org.eclipse.vtp.framework.interactions.core.configurations.PropertyConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.SelectionChoiceConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.SelectionRequestConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.TransferMessageConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IBridgeMessage;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;
import org.eclipse.vtp.framework.interactions.core.conversation.IDataRequest;
import org.eclipse.vtp.framework.interactions.core.conversation.IEndMessage;
import org.eclipse.vtp.framework.interactions.core.conversation.IExternalReference;
import org.eclipse.vtp.framework.interactions.core.conversation.IFinal;
import org.eclipse.vtp.framework.interactions.core.conversation.IInitial;
import org.eclipse.vtp.framework.interactions.core.conversation.IInputRequest;
import org.eclipse.vtp.framework.interactions.core.conversation.IInteraction;
import org.eclipse.vtp.framework.interactions.core.conversation.IMetaDataMessage;
import org.eclipse.vtp.framework.interactions.core.conversation.IMetaDataRequest;
import org.eclipse.vtp.framework.interactions.core.conversation.IOutputMessage;
import org.eclipse.vtp.framework.interactions.core.conversation.ISelectionRequest;
import org.eclipse.vtp.framework.interactions.core.conversation.ITransferMessage;
import org.eclipse.vtp.framework.interactions.core.media.BuiltInInputGrammar;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.FileContent;
import org.eclipse.vtp.framework.interactions.core.media.FileInputGrammar;
import org.eclipse.vtp.framework.interactions.core.media.IDataSet;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProviderRegistry;
import org.eclipse.vtp.framework.interactions.core.media.IResourceManager;
import org.eclipse.vtp.framework.interactions.core.media.InlineInputGrammar;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;
import org.eclipse.vtp.framework.interactions.core.media.MediaRenderingManager;
import org.eclipse.vtp.framework.interactions.core.media.TextContent;
import org.eclipse.vtp.framework.spi.ICommandProcessor;

/**
 * A support implementation of the {@link IConversation} interface.
 * 
 * @author Lonnie Pryor
 */
public class Conversation implements IConversation {
	/** The queue to add commands to. */
	private final ICommandProcessor commandProcessor;
	/** The currently selected brand. */
	private final IBrandSelection brandSelection;
	/** The currently selected interaction type. */
	private final IInteractionTypeSelection interactionTypeSelection;
	/** The currently selected language. */
	private final ILanguageSelection languageSelection;
	/** The media provider registry. */
	private final IMediaProviderRegistry mediaProviderRegistry;
	/** Comment for variableRegistry. */
	private final IVariableRegistry variableRegistry;
	/** The variable data set. */
	private final IDataSet dataSet;
	/** The scripting service. */
	private final IScriptingService scriptingService;
	private final ILastResult lastResult;
	private final IExecutionContext context;

	/**
	 * Creates a new Conversation.
	 * 
	 * @param commandProcessor
	 *            The queue to add commands to.
	 * @param brandSelection
	 *            The currently selected brand.
	 * @param interactionTypeSelection
	 *            The currently selected interaction type.
	 * @param languageSelection
	 *            The currently selected language.
	 * @param mediaProviderRegistry
	 *            The media provider registry.
	 * @param variableRegistry
	 *            The variable registry.
	 * @param scriptingService
	 *            The scripting service.
	 */
	public Conversation(ICommandProcessor commandProcessor,
			IBrandSelection brandSelection,
			IInteractionTypeSelection interactionTypeSelection,
			ILanguageSelection languageSelection,
			IMediaProviderRegistry mediaProviderRegistry,
			IVariableRegistry variableRegistry,
			IScriptingService scriptingService, ILastResult lastResult,
			IExecutionContext context) {
		this.commandProcessor = commandProcessor;
		this.brandSelection = brandSelection;
		this.interactionTypeSelection = interactionTypeSelection;
		this.languageSelection = languageSelection;
		this.mediaProviderRegistry = mediaProviderRegistry;
		this.variableRegistry = variableRegistry;
		this.lastResult = lastResult;
		this.dataSet = new DataSet(variableRegistry);
		this.scriptingService = scriptingService;
		this.context = context;
	}

	/**
	 * Resolves the value of an output configuration using the selected brand,
	 * interaction type, and language.
	 * 
	 * @param configuration
	 *            The configuration to resolve.
	 * @return The resolved value of an output configuration.
	 */
	public List<Content> resolveOutput(OutputConfiguration configuration)
	{
		if (configuration == null)
			return Collections.emptyList();
		String interactionTypeID = interactionTypeSelection
				.getSelectedInteractionType().getId();
		String languageID = languageSelection.getSelectedLanguage();
		OutputNode[] nodes = null;
		IBrand brand = brandSelection.getSelectedBrand();
		while (brand != null && nodes == null) {
			nodes = configuration.getItem(brand.getName(), interactionTypeID,
					languageID);
			if (nodes == null)
				brand = brand.getParentBrand();
		}
		if (nodes == null)
			return Collections.emptyList();
		String mediaProviderID = null;
		while (brand != null && mediaProviderID == null) {
			mediaProviderID = mediaProviderRegistry.lookupMediaProviderID(
					brand.getId(), interactionTypeID, languageID);
			if (mediaProviderID == null)
				brand = brand.getParentBrand();
		}
		if (mediaProviderID == null)
			return Collections.emptyList();
		List<Content> results = new LinkedList<Content>();
		for (OutputNode node : nodes)
			resolveContentNode(node, results);
		List<Content> content = new MediaRenderingManager(
				mediaProviderRegistry.getMediaProvider(mediaProviderID),
				dataSet).renderContent(MediaRenderingManager.COMPLETE,
				results);
		return content;
	}

	private void resolveContentNode(OutputNode node,
			List<Content> results) {
		if (node instanceof OutputContent)
			for (Content content : ((OutputContent) node)
					.getContent())
				results.add(content);
		else if (node instanceof OutputSwitch) {
			for (OutputCase c : ((OutputSwitch) node)
					.getCases()) {
				Object result = scriptingService.createScriptingEngine(
						c.getScriptingLanguage()).execute(c.getScript());
				context.info("Evaluating prompt guard condition");
				context.info(c.getScript());
				context.info("Result: " + result.toString());
				if (result != null
						&& "true".equalsIgnoreCase(result.toString())) {
					for (OutputNode child : c.getNodes())
						resolveContentNode(child, results);
					break;
				}
			}
		}
	}

	public Output resolveFilePath(OutputConfiguration configuration,
			String relativePath)
	{
		Output fileOutput = new Output(Output.TYPE_FILE);
		if (relativePath.startsWith("http://") || relativePath.startsWith("dtmf:"))
		{
			fileOutput.setProperty("value", relativePath);
			return fileOutput;
		}
		String interactionTypeID = interactionTypeSelection
				.getSelectedInteractionType().getId();
		String languageID = languageSelection.getSelectedLanguage();
		Content[] result = null;
		IBrand brand = brandSelection.getSelectedBrand();
		// while (brand != null && result == null)
		// {
		// result = configuration.getItem(brand.getName(), interactionTypeID,
		// languageID);
		// if (result == null)
		// brand = brand.getParentBrand();
		// }
		// if (result == null)
		// return relativePath;
		String mediaProviderID = null;
		while (brand != null && mediaProviderID == null) {
			mediaProviderID = mediaProviderRegistry.lookupMediaProviderID(
					brand.getId(), interactionTypeID, languageID);
			if (mediaProviderID != null) {
				IMediaProvider provider = mediaProviderRegistry
						.getMediaProvider(mediaProviderID);
				if (provider != null) {
					IResourceManager resourceManager = provider
							.getResourceManager();
					if (resourceManager != null) {
						if (resourceManager.isFileResource(relativePath))
							break;
					}
				}
			}
			mediaProviderID = null;
			brand = brand.getParentBrand();
		}
		if (mediaProviderID == null)
		{
			fileOutput.setProperty("value", relativePath);
			return fileOutput;
		}
		fileOutput.setProperty("media-provider", mediaProviderID);
		fileOutput.setProperty("value", mediaProviderID + "/" + relativePath);
		fileOutput.setProperty("original-path", relativePath);
		return fileOutput;
	}

	/**
	 * Resolves the value of an input configuration using the selected brand,
	 * interaction type, and language.
	 * 
	 * @param configuration
	 *            The configuration to resolve.
	 * @return The resolved value of an input configuration.
	 */
	public InputGrammar resolveInput(InputConfiguration configuration) {
		if (configuration == null)
			return null;
		String interactionTypeID = interactionTypeSelection
				.getSelectedInteractionType().getId();
		String languageID = languageSelection.getSelectedLanguage();
		InputGrammar result = null;
		IBrand brand = brandSelection.getSelectedBrand();
		while (brand != null && result == null) {
			result = configuration.getItem(brand.getName(), interactionTypeID,
					languageID);
			if (result == null)
				brand = brand.getParentBrand();
		}
		if (result == null)
			return null;
		String mediaProviderID = null;
		while (brand != null && mediaProviderID == null) {
			mediaProviderID = mediaProviderRegistry.lookupMediaProviderID(
					brand.getId(), interactionTypeID, languageID);
			if (mediaProviderID == null)
				brand = brand.getParentBrand();
		}
		if (mediaProviderID == null)
			return null;
		if (result.isDataAware())
			result = result.captureData(scriptingService, dataSet);
		// if (result instanceof FileInputGrammar &&
		// !((FileInputGrammar)result).getPath().startsWith("http://"))
		//			((FileInputGrammar)result).setStaticPath(mediaProviderID + "/" //$NON-NLS-1$ 
		// + ((FileInputGrammar)result).getPath());
		return result;
	}

	public Input resolveFilePath(InputConfiguration configuration,
			String relativePath)
	{
		Input input = new Input(ConversationCommand.INPUT_TYPE_FILE);
		if (relativePath.startsWith("http://") || relativePath.startsWith("dtmf:"))
		{
			input.setProperty("value", relativePath);
			return input;
		}
		String interactionTypeID = interactionTypeSelection
				.getSelectedInteractionType().getId();
		String languageID = languageSelection.getSelectedLanguage();
		InputGrammar result = null;
		IBrand brand = brandSelection.getSelectedBrand();
		while (brand != null && result == null) {
			result = configuration.getItem(brand.getName(), interactionTypeID,
					languageID);
			if (result == null)
				brand = brand.getParentBrand();
		}
		if (result == null)
		{
			input.setProperty("value", relativePath);
			return input;
		}
		String mediaProviderID = null;
		while (brand != null && mediaProviderID == null) {
			mediaProviderID = mediaProviderRegistry.lookupMediaProviderID(
					brand.getId(), interactionTypeID, languageID);
			if (mediaProviderID == null)
				brand = brand.getParentBrand();
		}
		if (mediaProviderID == null)
		{
			input.setProperty("value", relativePath);
			return input;
		}
		input.setProperty("media-provider", mediaProviderID);
		input.setProperty("value", mediaProviderID + "/" + relativePath);
		input.setProperty("original-path", relativePath);
		return input;
	}

	/**
	 * Resolves the value of a property configuration using the selected brand
	 * and interaction type.
	 * 
	 * @param configuration
	 *            The configuration to resolve.
	 * @param useInteractionType
	 *            If true search by interaction type.
	 * @return The resolved value of a property configuration.
	 */
	public String resolveProperty(PropertyConfiguration configuration,
			boolean useInteractionType) {
		return resolveProperty(configuration, useInteractionType, false);
	}

	public String resolveProperty(PropertyConfiguration configuration,
			boolean useInteractionType, boolean useLanguage) {
		if (configuration == null) {
			return null;
		}
		String interactionTypeID = interactionTypeSelection
				.getSelectedInteractionType().getId();
		String languageID = languageSelection.getSelectedLanguage();
		String result = null;
		PropertyConfiguration.Value value = null;
		IBrand brand = brandSelection.getSelectedBrand();
		while (brand != null) {
			value = configuration
					.getItem(
							brand.getName(),
							useInteractionType ? interactionTypeID : "", useLanguage ? languageID : ""); //$NON-NLS-1$
			if (value != null)
				break;
			brand = brand.getParentBrand();
		}
		if (value == null)
			return null;
		if (PropertyConfiguration.VARIABLE.equals(value.getType())) {
			result = variableRegistry.getVariable(value.getValue()).toString();
		} else if (PropertyConfiguration.EXPRESSION.equals(value.getType())) {
			result = String.valueOf(scriptingService.createScriptingEngine(
					"JavaScript").execute(value.getValue()));
		} else {
			result = value.getValue();
		}
		return result;
	}

	/**
	 * Resolves the value of a meta-data configuration using the selected brand,
	 * interaction type, and language.
	 * 
	 * @param configuration
	 *            The configuration to resolve.
	 * @return The resolved values of a meta-data configuration.
	 */
	private List resolveMetaData(MetaDataConfiguration configuration) {
		String interactionTypeID = interactionTypeSelection
				.getSelectedInteractionType().getId();
		String languageID = languageSelection.getSelectedLanguage();
		MetaDataItemConfiguration[] items = null;
		IBrand brand = brandSelection.getSelectedBrand();
		for (; brand != null && items == null; brand = brand.getParentBrand())
			items = configuration.getItem(brand.getName() + interactionTypeID
					+ languageID);
		if (items == null) {
			brand = brandSelection.getSelectedBrand();
			for (; brand != null && items == null; brand = brand
					.getParentBrand())
				items = configuration.getItem(brand.getName());
			if (items == null)
				return Collections.EMPTY_LIST;
		}
		List results = new ArrayList(items.length);
		for (int i = 0; i < items.length; ++i) {
			MetaDataItemConfiguration item = items[i];
			switch (items[i].getValueType()) {
			case MetaDataItemConfiguration.TYPE_STATIC:
				break;
			case MetaDataItemConfiguration.TYPE_EXPRESSION:
				item = new MetaDataItemConfiguration();
				item.setName(items[i].getName());
				item.setStaticValue(String.valueOf(scriptingService
						.createScriptingEngine(items[i].getScriptingLanguage())
						.execute(items[i].getValue())));
				break;
			case MetaDataItemConfiguration.TYPE_VARIABLE:
				item = new MetaDataItemConfiguration();
				item.setName(items[i].getName());
				item.setStaticValue(dataSet.getData(items[i].getValue())
						.toString());
				break;
			case MetaDataItemConfiguration.TYPE_MAP:
				IMapObject map = (IMapObject)variableRegistry.getVariable(items[i].getValue());
				for(Map.Entry<String, IDataObject> entry : map.getValues().entrySet())
				{
					MetaDataItemConfiguration mdic = new MetaDataItemConfiguration();
					mdic.setName(entry.getKey());
					mdic.setStaticValue(entry.getValue().toString());
					results.add(mdic);
				}
				continue;
			default:
				continue;
			}
			results.add(item);
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.conversation.
	 * IConversation#createInitial(java.lang.String, java.util.Map)
	 */
	public IInitial createInitial(String resultParameterName, Map variables) {
		return new Initial(resultParameterName, variables);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.conversation.
	 * IConversation#createOutputMessage(
	 * org.eclipse.vtp.framework.interactions.core.configurations.
	 * OutputMessageConfiguration)
	 */
	public IOutputMessage createOutputMessage(
			OutputMessageConfiguration configuration, String resultParameterName) {
		return new OutputMessage(configuration, resultParameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.conversation.
	 * IConversation#createMetaDataMessage(
	 * org.eclipse.vtp.framework.interactions.core.configurations.
	 * MetaDataConfiguration)
	 */
	public IMetaDataMessage createMetaDataMessage(
			MetaDataConfiguration configuration, String resultParameterName) {
		return new MetaDataMessage(configuration, resultParameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.framework.interactions.core.conversation.IConversation
	 * #createMetaDataRequest(
	 * org.eclipse.vtp.framework.interactions.core.configurations.
	 * MetaDataConfiguration)
	 */
	public IMetaDataRequest createMetaDataRequest(
			MetaDataConfiguration configuration, String resultParameterName) {
		return new MetaDataRequest(configuration, resultParameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.conversation.
	 * IConversation#createInputRequest(
	 * org.eclipse.vtp.framework.interactions.core.configurations.
	 * InputRequestConfiguration, java.lang.String)
	 */
	public IInputRequest createInputRequest(
			InputRequestConfiguration configuration, String resultParameterName) {
		return new InputRequest(configuration, resultParameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.conversation.
	 * IConversation#createSelectionRequest(
	 * org.eclipse.vtp.framework.interactions.core.configurations.
	 * SelectionRequestConfiguration, java.lang.String)
	 */
	public ISelectionRequest createSelectionRequest(
			SelectionRequestConfiguration configuration,
			String resultParameterName) {
		return new SelectionRequest(configuration, resultParameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.conversation.
	 * IConversation#createDataRequest(
	 * org.eclipse.vtp.framework.interactions.core.configurations.
	 * DataRequestConfiguration, java.lang.String)
	 */
	public IDataRequest createDataRequest(
			DataRequestConfiguration configuration, String resultParameterName) {
		return new DataRequest(configuration, resultParameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.conversation.
	 * IConversation#createExternalReference(
	 * org.eclipse.vtp.framework.interactions.core.configurations.
	 * ExternalReferenceConfiguration)
	 */
	public IExternalReference createExternalReference(
			ExternalReferenceConfiguration configuration,
			String resultParameterName) {
		return new ExternalReference(configuration, resultParameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.conversation.
	 * IConversation#createTransferMessage(
	 * org.eclipse.vtp.framework.interactions.core.configurations.
	 * TransferMessageConfiguration)
	 */
	public ITransferMessage createTransferMessage(
			TransferMessageConfiguration configuration) {
		return new TransferMessage(configuration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.conversation.
	 * IConversation#createBridgeMessage(
	 * org.eclipse.vtp.framework.interactions.core.configurations.
	 * BridgeMessageConfiguration, java.lang.String)
	 */
	public IBridgeMessage createBridgeMessage(
			BridgeMessageConfiguration configuration, String resultParameterName) {
		return new BridgeMessage(configuration, resultParameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.conversation.
	 * IConversation#createEndMessage()
	 */
	public IEndMessage createEndMessage(AssignmentConfiguration[] configurations)
	{
		IEndMessage message = new EndMessage();
		for (int i = 0; i < configurations.length; ++i)
			message.setVariableValue(configurations[i].getName(), variableRegistry
					.getVariable(configurations[i].getName()).toString());
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.conversation.
	 * IConversation#createFinal()
	 */
	public IFinal createFinal() {
		return new Final();
	}

	/**
	 * Abstract implementation of {@link IInteraction}.
	 * 
	 * @author Lonnie Pryor
	 */
	private abstract class AbstractInteraction implements IInteraction {
		/**
		 * Creates a command to pass to the command processor.
		 * 
		 * @return A command to pass to the command processor.
		 */
		abstract ConversationCommand createCommand();

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.controller.IDispatcher#enqueue()
		 */
		public final boolean enqueue() {
			ConversationCommand command = createCommand();
			if (command == null)
				return false;
			return commandProcessor.enqueue(command);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.controller.IDispatcher#process()
		 */
		public final boolean process() throws IllegalStateException {
			ConversationCommand command = createCommand();
			if (command == null)
				return false;
			return commandProcessor.process(command);
		}
	}

	/**
	 * Implementation of {@link IOutputMessage}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class Initial extends AbstractInteraction implements IInitial {
		/** The name of the parameter to set the result value to. */
		private final String resultParameterName;
		/** Comment for variables. */
		private final Map variables;
		/** The parameters to set when the process resumes. */
		private final Map parameters = new HashMap();

		/**
		 * Creates a new Initial.
		 * 
		 * @param resultParameterName
		 *            The name of the parameter to set the result value to.
		 */
		Initial(String resultParameterName, Map variables) {
			this.resultParameterName = resultParameterName;
			this.variables = variables;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.services.Conversation.
		 * AbstractInteraction#createCommand()
		 */
		ConversationCommand createCommand() {
			InitialCommand command = new InitialCommand();
			command.setResultName(resultParameterName);
			command.setResultValue(RESULT_NAME_FILLED);
			command.setHangupResultValue(RESULT_NAME_HANGUP);
			for (Iterator i = variables.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				command.setVariable((String) entry.getKey(),
						(String) entry.getValue());
			}
			for (Iterator i = parameters.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				command.setParameterValues((String) entry.getKey(),
						(String[]) entry.getValue());
			}
			return command;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.conversation.IInitial#
		 * setParameterValue(java.lang.String, java.lang.String)
		 */
		public void setParameterValue(String name, String value) {
			if (name == null)
				return;
			setParameterValues(name, value == null ? null
					: new String[] { value });
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.conversation.IInitial#
		 * setParameterValues(java.lang.String, java.lang.String[])
		 */
		public void setParameterValues(String name, String[] values) {
			if (name == null)
				return;
			if (values == null)
				parameters.remove(name);
			else
				parameters.put(name, values);
		}
	}

	/**
	 * Implementation of {@link IOutputMessage}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class OutputMessage extends AbstractInteraction implements
			IOutputMessage {
		/** The configuration for this interaction. */
		private final OutputMessageConfiguration configuration;
		/** The name of the parameter to set the result value to. */
		private final String resultParameterName;
		/** The parameters to set when the process resumes. */
		private final Map parameters = new HashMap();

		/**
		 * Creates a new OutputMessage.
		 * 
		 * @param configuration
		 *            The configuration for this interaction.
		 */
		OutputMessage(OutputMessageConfiguration configuration,
				String resultParameterName) {
			this.configuration = configuration;
			this.resultParameterName = resultParameterName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.services.Conversation.
		 * AbstractInteraction#createCommand()
		 */
		ConversationCommand createCommand() {
			OutputMessageCommand command = new OutputMessageCommand();
			command.setResultName(resultParameterName);
			command.setFilledResultValue(RESULT_NAME_FILLED);
			command.setHangupResultValue(RESULT_NAME_HANGUP);
			command.setSecured(configuration.isSecured());
			MediaConfiguration mediaConfig = configuration
					.getMediaConfiguration();
			if (mediaConfig != null) {
				String[] propertyNames = mediaConfig
						.getPropertyConfigurationNames();
				for (int i = 0; i < propertyNames.length; ++i) {
					String value = resolveProperty(
							mediaConfig
									.getPropertyConfiguration(propertyNames[i]),
							true);
					if (value != null)
						command.setPropertyValue(propertyNames[i], value);
				}
				OutputConfiguration outputConfiguration = mediaConfig
						.getOutputConfiguration(configuration.getOutputName());
				List content = resolveOutput(outputConfiguration);
				for (Iterator i = content.iterator(); i.hasNext();) {
					Content item = (Content) i.next();
					if (item instanceof TextContent)
					{
						Output textOutput = new Output(Output.TYPE_TEXT);
						textOutput.setProperty("value", ((TextContent)item).getText());
						command.addOutput(textOutput);
					}
					else if (item instanceof FileContent)
						command.addOutput(resolveFilePath(
								outputConfiguration,
								((FileContent) item).getPath()));
				}
			}
			for (Iterator i = parameters.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				command.setParameterValues((String) entry.getKey(),
						(String[]) entry.getValue());
			}
			return command;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IOutputMessage#setParameterValue(java.lang.String, java.lang.String)
		 */
		public void setParameterValue(String name, String value) {
			if (name == null)
				return;
			setParameterValues(name, value == null ? null
					: new String[] { value });
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IOutputMessage#setParameterValues(java.lang.String,
		 * java.lang.String[])
		 */
		public void setParameterValues(String name, String[] values) {
			if (name == null)
				return;
			if (values == null)
				parameters.remove(name);
			else
				parameters.put(name, values);
		}
	}

	/**
	 * Implementation of {@link IMetaDataMessage}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class MetaDataMessage extends AbstractInteraction implements
			IMetaDataMessage {
		/** The configuration for this interaction. */
		private final MetaDataConfiguration configuration;
		/** The name of the parameter to set the result value to. */
		private final String resultParameterName;
		/** The parameters to set when the process resumes. */
		private final Map parameters = new HashMap();

		/**
		 * Creates a new MedatDataMessage.
		 * 
		 * @param configuration
		 *            The configuration for this interaction.
		 */
		MetaDataMessage(MetaDataConfiguration configuration,
				String resultParameterName) {
			this.configuration = configuration;
			this.resultParameterName = resultParameterName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.services.Conversation.
		 * AbstractInteraction#createCommand()
		 */
		ConversationCommand createCommand() {
			MetaDataMessageCommand command = new MetaDataMessageCommand();
			command.setResultName(resultParameterName);
			command.setFilledResultValue(RESULT_NAME_FILLED);
			List metaData = resolveMetaData(configuration);
			for (Iterator i = metaData.iterator(); i.hasNext();) {
				MetaDataItemConfiguration item = (MetaDataItemConfiguration) i
						.next();
				command.setMetaDataValue(item.getName(), item.getValue());
			}
			for (Iterator i = parameters.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				command.setParameterValues((String) entry.getKey(),
						(String[]) entry.getValue());
			}
			return command;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IMetaDataMessage#setParameterValue(java.lang.String,
		 * java.lang.String)
		 */
		public void setParameterValue(String name, String value) {
			if (name == null)
				return;
			setParameterValues(name, value == null ? null
					: new String[] { value });
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IMetaDataMessage#setParameterValues(java.lang.String,
		 * java.lang.String[])
		 */
		public void setParameterValues(String name, String[] values) {
			if (name == null)
				return;
			if (values == null)
				parameters.remove(name);
			else
				parameters.put(name, values);
		}
	}

	/**
	 * Implementation of {@link IMetaDataRequest}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class MetaDataRequest extends AbstractInteraction implements
			IMetaDataRequest {
		/** The configuration for this interaction. */
		private final MetaDataConfiguration configuration;
		/** The name of the parameter to set the result value to. */
		private final String resultParameterName;
		/** The parameters to set when the process resumes. */
		private final Map parameters = new HashMap();

		/**
		 * Creates a new MedatDataRequest.
		 * 
		 * @param configuration
		 *            The configuration for this interaction.
		 */
		MetaDataRequest(MetaDataConfiguration configuration,
				String resultParameterName) {
			this.configuration = configuration;
			this.resultParameterName = resultParameterName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.services.Conversation.
		 * AbstractInteraction#createCommand()
		 */
		ConversationCommand createCommand() {
			MetaDataRequestCommand command = new MetaDataRequestCommand();
			command.setResultName(resultParameterName);
			command.setFilledResultValue(RESULT_NAME_FILLED);
			command.setHangupResultValue(RESULT_NAME_HANGUP);
			command.setDataName("GetAttachedData");
			List metaData = resolveMetaData(configuration);
			for (Iterator i = metaData.iterator(); i.hasNext();) {
				MetaDataItemConfiguration item = (MetaDataItemConfiguration) i
						.next();
				command.addMetaDataName(item.getName());
			}
			for (Iterator i = parameters.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				command.setParameterValues((String) entry.getKey(),
						(String[]) entry.getValue());
			}
			return command;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IMetaDataMessage#setParameterValue(java.lang.String,
		 * java.lang.String)
		 */
		public void setParameterValue(String name, String value) {
			if (name == null)
				return;
			setParameterValues(name, value == null ? null
					: new String[] { value });
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IMetaDataMessage#setParameterValues(java.lang.String,
		 * java.lang.String[])
		 */
		public void setParameterValues(String name, String[] values) {
			if (name == null)
				return;
			if (values == null)
				parameters.remove(name);
			else
				parameters.put(name, values);
		}
	}

	/**
	 * Implementation of {@link IInputRequest}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class InputRequest extends AbstractInteraction implements
			IInputRequest {
		/** The configuration of the interaction. */
		private final InputRequestConfiguration configuration;
		/** The name of the parameter to set the result value to. */
		private final String resultParameterName;
		/** The parameters to set when the process resumes. */
		private final Map parameters = new HashMap();

		/**
		 * Creates a new InputRequest.
		 * 
		 * @param configuration
		 *            The configuration of the interaction.
		 * @param resultParameterName
		 *            The name of the parameter to set the result value to.
		 */
		InputRequest(InputRequestConfiguration configuration,
				String resultParameterName) {
			this.configuration = configuration;
			this.resultParameterName = resultParameterName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.services.Conversation.
		 * AbstractInteraction#createCommand()
		 */
		ConversationCommand createCommand() {
			InputRequestCommand command = new InputRequestCommand();
			command.setSecured(configuration.isSecured());
			command.setResultName(resultParameterName);
			command.setFilledResultValue(RESULT_NAME_FILLED);
			command.setNoInputResultValue(RESULT_NAME_NO_INPUT);
			command.setNoMatchResultValue(RESULT_NAME_NO_MATCH);
			command.setHangupResultValue(RESULT_NAME_HANGUP);
			command.setDataName(configuration.getDataName());
			MediaConfiguration mediaConfig = configuration
					.getMediaConfiguration();
			if (mediaConfig != null) {
				String[] propertyNames = mediaConfig
						.getPropertyConfigurationNames();
				for (int i = 0; i < propertyNames.length; ++i) {
					String value = resolveProperty(
							mediaConfig
									.getPropertyConfiguration(propertyNames[i]),
							true);
					if (value != null)
						command.setPropertyValue(propertyNames[i], value);
				}
				OutputConfiguration outputConfiguration = mediaConfig
						.getOutputConfiguration(configuration.getOutputName());
				List content = resolveOutput(outputConfiguration);
				for (Iterator i = content.iterator(); i.hasNext();) {
					Content item = (Content) i.next();
					if (item instanceof TextContent)
					{
						Output textOutput = new Output(Output.TYPE_TEXT);
						textOutput.setProperty("value", ((TextContent) item).getText());
						command.addOutput(textOutput);
					}
					else if (item instanceof FileContent)
						command.addOutput(resolveFilePath(
								outputConfiguration,
								((FileContent) item).getPath()));
				}
				InputConfiguration inputConfiguration = mediaConfig
						.getInputConfiguration(configuration.getInputName());
				InputGrammar grammar = resolveInput(inputConfiguration);
				if (grammar instanceof FileInputGrammar)
					command.setInput(resolveFilePath(inputConfiguration,
							((FileInputGrammar) grammar).getPath()));
				else if (grammar instanceof BuiltInInputGrammar)
				{
					Input customInput = new Input(ConversationCommand.INPUT_TYPE_CUSTOM);
					customInput.setProperty("value", ((BuiltInInputGrammar) grammar)
							.getBuiltInInputURI());
					command.setInput(customInput);
				}
				else if (grammar instanceof InlineInputGrammar)
				{
					Input inlineInput = new Input(ConversationCommand.INPUT_TYPE_INLINE);
					inlineInput.setProperty("value", ((InlineInputGrammar)grammar).getGrammarText());
					command.setInput(inlineInput);
				}
				InputConfiguration inputConfiguration2 = mediaConfig
						.getInputConfiguration(configuration.getInputName2());
				InputGrammar grammar2 = resolveInput(inputConfiguration2);
				if (grammar2 instanceof FileInputGrammar)
					command.setInput2(resolveFilePath(inputConfiguration2,
							((FileInputGrammar) grammar2).getPath()));
				else if (grammar2 instanceof BuiltInInputGrammar)
				{
					Input customInput = new Input(ConversationCommand.INPUT_TYPE_CUSTOM);
					customInput.setProperty("value", ((BuiltInInputGrammar) grammar2)
							.getBuiltInInputURI());
					command.setInput2(customInput);
				}
				else if (grammar2 instanceof InlineInputGrammar)
				{
					Input inlineInput = new Input(ConversationCommand.INPUT_TYPE_INLINE);
					inlineInput.setProperty("value", ((InlineInputGrammar)grammar2).getGrammarText());
					command.setInput2(inlineInput);
				}
			}
			for (Iterator i = parameters.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				command.setParameterValues((String) entry.getKey(),
						(String[]) entry.getValue());
			}
			return command;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IInputRequest#setParameterValue(java.lang.String, java.lang.String)
		 */
		public void setParameterValue(String name, String value) {
			if (name == null)
				return;
			setParameterValues(name, value == null ? null
					: new String[] { value });
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IInputRequest#setParameterValues(java.lang.String,
		 * java.lang.String[])
		 */
		public void setParameterValues(String name, String[] values) {
			if (name == null)
				return;
			if (values == null)
				parameters.remove(name);
			else
				parameters.put(name, values);
		}
	}

	/**
	 * Implementation of {@link ISelectionRequest}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class SelectionRequest extends AbstractInteraction implements
			ISelectionRequest {
		/** The configuration of the interaction. */
		private final SelectionRequestConfiguration configuration;
		/** The name of the parameter to set the result value to. */
		private final String resultParameterName;
		/** The parameters to set when the process resumes. */
		private final Map parameters = new HashMap();

		/**
		 * Creates a new InputRequest.
		 * 
		 * @param configuration
		 *            The configuration of the interaction.
		 * @param resultParameterName
		 *            The name of the parameter to set the result value to.
		 */
		SelectionRequest(SelectionRequestConfiguration configuration,
				String resultParameterName) {
			this.configuration = configuration;
			this.resultParameterName = resultParameterName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.services.Conversation.
		 * AbstractInteraction#createCommand()
		 */
		ConversationCommand createCommand() {
			SelectionRequestCommand command = new SelectionRequestCommand();
			command.setSecured(configuration.isSecured());
			command.setResultName(resultParameterName);
			command.setFilledResultValue(RESULT_NAME_FILLED);
			command.setNoInputResultValue(RESULT_NAME_NO_INPUT);
			command.setNoMatchResultValue(RESULT_NAME_NO_MATCH);
			command.setHangupResultValue(RESULT_NAME_HANGUP);
			command.setSelectionName(configuration.getDataName());
			MediaConfiguration mediaConfig = configuration
					.getMediaConfiguration();
			if (mediaConfig != null) {
				String[] propertyNames = mediaConfig
						.getPropertyConfigurationNames();
				for (int i = 0; i < propertyNames.length; ++i) {
					String value = resolveProperty(
							mediaConfig
									.getPropertyConfiguration(propertyNames[i]),
							true);
					if (value != null)
						command.setPropertyValue(propertyNames[i], value);
				}
				OutputConfiguration outputConfiguration = mediaConfig
						.getOutputConfiguration(configuration.getOutputName());
				List content = resolveOutput(outputConfiguration);
				for (Iterator i = content.iterator(); i.hasNext();) {
					Content item = (Content) i.next();
					if (item instanceof TextContent)
					{
						Output textOutput = new Output(Output.TYPE_TEXT);
						textOutput.setProperty("value", ((TextContent) item).getText());
						command.addOutput(textOutput);
					}
					else if (item instanceof FileContent)
						command.addOutput(resolveFilePath(outputConfiguration,
								((FileContent) item).getPath()));
				}
			}
			SelectionChoiceConfiguration[] choices = configuration.getChoices();
			Map choiceIndex = new HashMap(choices.length);
			for (int i = 0; i < choices.length; ++i)
				choiceIndex.put(choices[i].getName(), choices[i]);
			String[] choiceNames = null;
			for (IBrand brand = brandSelection.getSelectedBrand(); choiceNames == null
					&& brand != null; brand = brand.getParentBrand())
				choiceNames = configuration.getBrandedChoices(brand.getName());
			if (choiceNames != null) {
				for (int i = 0, index = 0; i < choiceNames.length; ++i) {
					SelectionChoiceConfiguration choice = (SelectionChoiceConfiguration) choiceIndex
							.get(choiceNames[i]);
					if (choice == null)
						continue;
					final String script = choice.getScript();
					final String scriptingLanguage = choice
							.getScriptingLanguage();
					if (scriptingLanguage != null && script != null) {
						IScriptingEngine engine = scriptingService
								.createScriptingEngine(scriptingLanguage);
						if (engine != null) {
							Object scriptResult = engine.execute(script);
							if (Boolean.FALSE.toString().equalsIgnoreCase(
									String.valueOf(scriptResult)))
								continue;
						}
					}
					command.addOption(choice.getName());
					mediaConfig = choice.getMediaConfiguration();
					if (mediaConfig != null) {
						String[] propertyNames = mediaConfig
								.getPropertyConfigurationNames();
						for (int j = 0; j < propertyNames.length; ++j) {
							String value = resolveProperty(
									mediaConfig
											.getPropertyConfiguration(propertyNames[j]),
									true);
							if (value != null)
								command.setOptionProperty(index,
										propertyNames[j], value);
						}
						OutputConfiguration outputConfiguration = mediaConfig
								.getOutputConfiguration(choice.getOutputName());
						List content = resolveOutput(outputConfiguration);
						for (Iterator j = content.iterator(); j.hasNext();) {
							Content item = (Content) j.next();
							if (item instanceof TextContent)
							{
								Output textOutput = new Output(Output.TYPE_TEXT);
								textOutput.setProperty("value", ((TextContent) item).getText());
								command.addOptionOutput(index, textOutput);
							}
							else if (item instanceof FileContent)
								command.addOptionOutput(index,
										resolveFilePath(outputConfiguration,
												((FileContent) item).getPath()));
						}
						InputConfiguration inputConfiguration = mediaConfig
								.getInputConfiguration(choice.getInputName());
						InputGrammar grammar = resolveInput(inputConfiguration);
						if (grammar instanceof FileInputGrammar)
							command.setOptionInput(index,
									resolveFilePath(inputConfiguration,
											((FileInputGrammar) grammar)
													.getPath()));
						else if (grammar instanceof BuiltInInputGrammar)
						{
							Input customInput = new Input(ConversationCommand.INPUT_TYPE_CUSTOM);
							customInput.setProperty("value", ((BuiltInInputGrammar) grammar)
									.getBuiltInInputURI());
							command.setOptionInput(index, customInput);
						}
						else if (grammar instanceof InlineInputGrammar)
						{
							Input inlineInput = new Input(ConversationCommand.INPUT_TYPE_INLINE);
							inlineInput.setProperty("value", ((InlineInputGrammar)grammar).getGrammarText());
							command.setOptionInput(index, inlineInput);
						}
					}
					++index;
				}
			}
			for (Iterator i = parameters.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				command.setParameterValues((String) entry.getKey(),
						(String[]) entry.getValue());
			}
			return command;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IInputRequest#setParameterValue(java.lang.String, java.lang.String)
		 */
		public void setParameterValue(String name, String value) {
			if (name == null)
				return;
			setParameterValues(name, value == null ? null
					: new String[] { value });
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IInputRequest#setParameterValues(java.lang.String,
		 * java.lang.String[])
		 */
		public void setParameterValues(String name, String[] values) {
			if (name == null)
				return;
			if (values == null)
				parameters.remove(name);
			else
				parameters.put(name, values);
		}
	}

	/**
	 * Implementation of {@link IDataRequest}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class DataRequest extends AbstractInteraction implements
			IDataRequest {
		/** The configuration of the interaction. */
		private final DataRequestConfiguration configuration;
		/** The name of the parameter to set the result value to. */
		private final String resultParameterName;
		/** The parameters to set when the process resumes. */
		private final Map parameters = new HashMap();

		/**
		 * Creates a new DataRequest.
		 * 
		 * @param configuration
		 *            The configuration of the interaction.
		 * @param resultParameterName
		 *            The name of the parameter to set the result value to.
		 */
		DataRequest(DataRequestConfiguration configuration,
				String resultParameterName) {
			this.configuration = configuration;
			this.resultParameterName = resultParameterName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.services.Conversation.
		 * AbstractInteraction#createCommand()
		 */
		ConversationCommand createCommand() {
			DataRequestCommand command = new DataRequestCommand();
			command.setSecured(configuration.isSecured());
			command.setResultName(resultParameterName);
			command.setFilledResultValue(RESULT_NAME_FILLED);
			command.setNoInputResultValue(RESULT_NAME_NO_INPUT);
			command.setHangupResultValue(RESULT_NAME_HANGUP);
			// command.setNoMatchResultValue(RESULT_NAME_NO_MATCH);
			command.setDataName(configuration.getDataName());
			MediaConfiguration mediaConfig = configuration
					.getMediaConfiguration();
			if (mediaConfig != null) {
				String[] propertyNames = mediaConfig
						.getPropertyConfigurationNames();
				for (int i = 0; i < propertyNames.length; ++i) {
					String value = resolveProperty(
							mediaConfig
									.getPropertyConfiguration(propertyNames[i]),
							true);
					if (value != null)
						command.setPropertyValue(propertyNames[i], value);
				}
				OutputConfiguration outputConfiguration = mediaConfig
						.getOutputConfiguration(configuration.getOutputName());
				List content = resolveOutput(outputConfiguration);
				for (Iterator i = content.iterator(); i.hasNext();) {
					Content item = (Content) i.next();
					if (item instanceof TextContent)
					{
						Output textOutput = new Output(Output.TYPE_TEXT);
						textOutput.setProperty("value", ((TextContent) item).getText());
						command.addOutput(textOutput);
					}
					else if (item instanceof FileContent)
						command.addOutput(resolveFilePath(
								outputConfiguration,
								((FileContent) item).getPath()));
				}
				InputGrammar grammar = resolveInput(mediaConfig
						.getInputConfiguration(configuration.getInputName()));
				if (grammar instanceof FileInputGrammar)
					command.setInput(resolveFilePath(
							mediaConfig.getInputConfiguration(configuration
									.getInputName()),
							((FileInputGrammar) grammar).getPath()));
				else if (grammar instanceof BuiltInInputGrammar)
				{
					Input customInput = new Input(ConversationCommand.INPUT_TYPE_CUSTOM);
					customInput.setProperty("value", ((BuiltInInputGrammar) grammar)
							.getBuiltInInputURI());
					command.setInput(customInput);
				}
				else if (grammar instanceof InlineInputGrammar)
				{
					Input inlineInput = new Input(ConversationCommand.INPUT_TYPE_INLINE);
					inlineInput.setProperty("value", ((InlineInputGrammar)grammar).getGrammarText());
					command.setInput(inlineInput);
				}
			}
			for (Iterator i = parameters.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				command.setParameterValues((String) entry.getKey(),
						(String[]) entry.getValue());
			}
			return command;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IInputRequest#setParameterValue(java.lang.String, java.lang.String)
		 */
		public void setParameterValue(String name, String value) {
			if (name == null)
				return;
			setParameterValues(name, value == null ? null
					: new String[] { value });
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IInputRequest#setParameterValues(java.lang.String,
		 * java.lang.String[])
		 */
		public void setParameterValues(String name, String[] values) {
			if (name == null)
				return;
			if (values == null)
				parameters.remove(name);
			else
				parameters.put(name, values);
		}
	}

	/**
	 * Implementation of {@link IExternalReference}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ExternalReference extends AbstractInteraction implements
			IExternalReference {
		/** The configuration for this interaction. */
		private final ExternalReferenceConfiguration configuration;
		private final String resultParameterName;
		/** The parameters to set when the process resumes. */
		private final Map parameters = new HashMap();

		/**
		 * Creates a new ExternalReference.
		 * 
		 * @param configuration
		 *            The configuration for this interaction.
		 */
		ExternalReference(ExternalReferenceConfiguration configuration,
				String resultParameterName) {
			this.configuration = configuration;
			this.resultParameterName = resultParameterName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.services.Conversation.
		 * AbstractInteraction#createCommand()
		 */
		ConversationCommand createCommand() {
			ExternalReferenceCommand command = new ExternalReferenceCommand();
			command.setReferenceName(configuration.getName());
			command.setResultName(resultParameterName);
			command.setFilledResultValue(RESULT_NAME_FILLED);
			command.setHangupResultValue(RESULT_NAME_HANGUP);
			command.setReferenceURI(configuration.getUrl());
			String[] keys = configuration.getInputNames();
			for (int i = 0; i < keys.length; ++i) {
				if (!configuration.isInputVariable(keys[i])) // this determines
																// whether the
																// input is a
																// variable or a
																// constant
					command.setInputArgumentValue(keys[i],
							configuration.getInputValue(keys[i]));
				else {
					IDataObject obj = variableRegistry
							.getVariable(configuration.getInputValue(keys[i]));
					if (obj != null)
						command.setInputArgumentValue(keys[i],
								"'" + obj.toString() + "'");

				}
			}
			keys = configuration.getOutputNames();
			for (int i = 0; i < keys.length; ++i)
				command.setOutputArgumentValue(keys[i],
						configuration.getOutputValue(keys[i]));
			keys = configuration.getURLParameterNames();
			for (int i = 0; i < keys.length; ++i) {
				if (!configuration.isURLParameterVariable(keys[i])) // this
																	// determines
																	// whether
																	// the input
																	// is a
																	// variable
																	// or a
																	// constant
					command.setURLParameterValue(keys[i],
							configuration.getURLParameterValue(keys[i]));
				else {
					IDataObject obj = variableRegistry
							.getVariable(configuration
									.getURLParameterValue(keys[i]));
					if (obj != null)
						command.setURLParameterValue(keys[i],
								"'" + obj.toString() + "'");

				}
			}
			for (Iterator i = parameters.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				command.setParameterValues((String) entry.getKey(),
						(String[]) entry.getValue());
			}
			return command;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IExternalReference#setParameterValue(java.lang.String,
		 * java.lang.String)
		 */
		public void setParameterValue(String name, String value) {
			if (name == null)
				return;
			setParameterValues(name, value == null ? null
					: new String[] { value });
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.
		 * IExternalReference#setParameterValues(java.lang.String,
		 * java.lang.String[])
		 */
		public void setParameterValues(String name, String[] values) {
			if (name == null)
				return;
			if (values == null)
				parameters.remove(name);
			else
				parameters.put(name, values);
		}
	}

	/**
	 * Implementation of {@link ITransferMessage}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class TransferMessage extends AbstractInteraction implements
			ITransferMessage {
		/** The configuration for this interaction. */
		private final TransferMessageConfiguration configuration;

		/**
		 * Creates a new TransferMessage.
		 * 
		 * @param configuration
		 *            The configuration for this interaction.
		 */
		TransferMessage(TransferMessageConfiguration configuration) {
			this.configuration = configuration;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.services.Conversation.
		 * AbstractInteraction#createCommand()
		 */
		ConversationCommand createCommand() {
			String value = resolveProperty(configuration.getDestination(), true);
			if (value == null)
				return null;
			String type = resolveProperty(configuration.getType(), true);
			if ("variable".equalsIgnoreCase(type))
				value = String.valueOf(variableRegistry.getVariable(value));
			else if ("expression".equalsIgnoreCase(type))
				value = String.valueOf(scriptingService.createScriptingEngine(
						"JavaScript").execute(value));
			TransferMessageCommand command = new TransferMessageCommand();
			command.setDestination(value);
			return command;
		}
	}

	/**
	 * Implementation of {@link ITBridgeMessage}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class BridgeMessage extends AbstractInteraction implements
			IBridgeMessage {
		/** The configuration for this interaction. */
		private final BridgeMessageConfiguration configuration;
		/** The name of the parameter to set the result value to. */
		private final String resultParameterName;

		/**
		 * Creates a new BridgeMessage.
		 * 
		 * @param configuration
		 *            The configuration for this interaction.
		 */
		BridgeMessage(BridgeMessageConfiguration configuration,
				String resultParameterName) {
			this.configuration = configuration;
			this.resultParameterName = resultParameterName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.services.Conversation.
		 * AbstractInteraction#createCommand()
		 */
		ConversationCommand createCommand() {
			MediaConfiguration mediaConfiguration = configuration
					.getMediaConfiguration();

			PropertyConfiguration transferTypePropertyConfig = mediaConfiguration
					.getPropertyConfiguration("transferType");
			PropertyConfiguration typePropertyConfig = mediaConfiguration
					.getPropertyConfiguration("type");
			PropertyConfiguration destinationPropertyConfig = mediaConfiguration
					.getPropertyConfiguration("destination");

			String value = resolveProperty(destinationPropertyConfig, true,
					true);
			if (value == null)
				value = resolveProperty(destinationPropertyConfig, true, false);
			if (value == null)
				return null;

			String type = resolveProperty(typePropertyConfig, true, true);
			if (type == null)
				type = resolveProperty(typePropertyConfig, true, false);
			if ("variable".equalsIgnoreCase(type))
				value = String.valueOf(variableRegistry.getVariable(value));
			else if ("expression".equalsIgnoreCase(type))
				value = String.valueOf(scriptingService.createScriptingEngine(
						"JavaScript").execute(value));

			String bridgedValue = resolveProperty(transferTypePropertyConfig,
					true, true);
			if (bridgedValue == null)
				bridgedValue = resolveProperty(transferTypePropertyConfig,
						true, false);

			BridgeMessageCommand command = new BridgeMessageCommand();
			command.setTransferType(bridgedValue);
			command.setDestination(value);
			command.setResultName(resultParameterName);
			command.setTransferredResultValue(TRANSFERRED);
			command.setBusyResultValue(BUSY);
			command.setUnavailableResultValue(UNAVAILABLE);
			command.setNoAuthResultValue(NOAUTH);
			command.setBadDestResultValue(BADDEST);
			command.setNoRouteResultValue(NOROUTE);
			command.setNoResourceResultValue(NORESOURCE);
			command.setProtocolResultValue(PROTOCOL);
			command.setBadBridgeResultValue(BADBRIDGE);
			command.setBadUriResultValue(BADURI);
			command.setHangupResultValue(RESULT_NAME_HANGUP);
			return command;
		}
	}

	/**
	 * Implementation of {@link IEndMessage}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class EndMessage extends AbstractInteraction implements
			IEndMessage
	{
		private final Map<String, String> variables = new HashMap<String, String>();

		public void setVariableValue(String variableName, String variableValue)
		{
			variables.put(variableName, variableValue);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.services.Conversation.
		 * AbstractInteraction#createCommand()
		 */
		ConversationCommand createCommand() {
			EndMessageCommand command = new EndMessageCommand();
			for (Map.Entry<String, String> entry : variables.entrySet())
			{
				command.addVariable(entry.getKey(), entry.getValue());
			}
			return command;
		}

	}

	/**
	 * Implementation of {@link IOutputMessage}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class Final extends AbstractInteraction implements IFinal {
		/** Comment for variableNames. */
		private final Map<String, String> variables = new HashMap<String, String>();

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.conversation.IFinal#
		 * setVariableValue(java.lang.String, java.lang.String)
		 */
		public void setVariableValue(String variableName, String variableValue) {
			variables.put(variableName, variableValue);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.services.Conversation.
		 * AbstractInteraction#createCommand()
		 */
		ConversationCommand createCommand() {
			FinalCommand command = new FinalCommand();
			for (Map.Entry<String, String> entry : variables.entrySet())
			{
				command.addVariable(entry.getKey(), entry.getValue());
			}
			return command;
		}
	}

	/**
	 * Implementation of {@link IDataSet}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class DataSet implements IDataSet {
		/** The variables to use. */
		private final IVariableRegistry variables;

		/**
		 * Creates a new DataSet.
		 * 
		 * @param variables
		 *            The variables to use.
		 */
		DataSet(IVariableRegistry variables) {
			this.variables = variables;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.interactions.core.media.IDataSet#getData(
		 * java.lang.String)
		 */
		public Object getData(String name) {
			if (name.startsWith("LastResult")) {
				if (name.length() > "LastResult.".length()) {
					String prop = name.substring(11);
					ILastResultData data = (ILastResultData) lastResult
							.getResults().get(0);
					if (data == null)
						return null;
					if ("confidence".equals(prop)) {
						return Integer.toString(data.getConfidence());
					}
					if ("utterance".equals(prop)) {
						return data.getUtterence();
					}
					if ("inputmode".equals(prop)) {
						return data.getInputMode();
					}
					if ("interpretation".equals(prop)) {
						return data.getInterpretation();
					}
				}
			}
			IDataObject result = variables.getVariable(name);
			if (result == null)
				return ""; //$NON-NLS-1$
			return result.toString();
		}
	}
}
