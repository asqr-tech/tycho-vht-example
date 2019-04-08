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
package org.eclipse.vtp.framework.common.services;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IScriptingEngine;
import org.eclipse.vtp.framework.common.IScriptingService;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.common.commands.ControllerCommand;
import org.eclipse.vtp.framework.common.commands.ExitCommand;
import org.eclipse.vtp.framework.common.commands.ForwardCommand;
import org.eclipse.vtp.framework.common.commands.IncludeCommand;
import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.eclipse.vtp.framework.common.configurations.DispatchConfiguration;
import org.eclipse.vtp.framework.common.configurations.ExitConfiguration;
import org.eclipse.vtp.framework.common.configurations.VariableMappingConfiguration;
import org.eclipse.vtp.framework.common.controller.IController;
import org.eclipse.vtp.framework.common.controller.IDispatcher;
import org.eclipse.vtp.framework.common.controller.IExitDispatcher;
import org.eclipse.vtp.framework.common.controller.IForwardDispatcher;
import org.eclipse.vtp.framework.common.controller.IIncludeDispatcher;
import org.eclipse.vtp.framework.spi.ICommandProcessor;
import org.eclipse.vtp.framework.util.Guid;

/**
 * An implementation of controller that enqueues commands with a command queue.
 * 
 * @author Lonnie Pryor
 */
public class Controller implements IController
{
	/** The queue to add commands to. */
	private final ICommandProcessor commandProcessor;
	/** The variable registry. */
	private final IVariableRegistry variableRegistry;
	/** The scripting service. */
	private final IScriptingService scriptingService;

	/**
	 * Creates a new Controller.
	 * 
	 * @param commandProcessor The queue to add commands to.
	 * @param brandSelection The currently selected brand.
	 * @param variableRegistry The variable registry.
	 * @param scriptingService The scripting service
	 */
	public Controller(ICommandProcessor commandProcessor,
			IVariableRegistry variableRegistry, IScriptingService scriptingService)
	{
		this.commandProcessor = commandProcessor;
		this.variableRegistry = variableRegistry;
		this.scriptingService = scriptingService;
	}

	/**
	 * Resolves the specified variable mapping to the name of an existing
	 * variable.
	 * 
	 * @param configuration The configuration to resolve.
	 * @return The name of the variable the configuration resolved to.
	 */
	private String resolveVariableMapping(
			VariableMappingConfiguration configuration)
	{
		switch (configuration.getType())
		{
		case VariableMappingConfiguration.TYPE_STATIC:
			String staticName = Guid.createGUID();
			IStringObject staticVariable = (IStringObject)variableRegistry
					.createVariable(IStringObject.TYPE_NAME);
			staticVariable.setValue(configuration.getValue());
			variableRegistry.setVariable(staticName, staticVariable);
			return staticName;
		case VariableMappingConfiguration.TYPE_EXPRESSION:
			IScriptingEngine engine = scriptingService
					.createScriptingEngine(configuration.getScriptingLangugage());
			Object result = engine.execute(configuration.getValue());
			if (result == null)
				return null;
			String expressionName = Guid.createGUID();
			if (result instanceof IDataObject)
				variableRegistry.setVariable(expressionName, (IDataObject)result);
			else
			{
				IStringObject expressionVariable = (IStringObject)variableRegistry
						.createVariable(IStringObject.TYPE_NAME);
				expressionVariable.setValue(result.toString());
				variableRegistry.setVariable(expressionName, expressionVariable);
			}
			return expressionName;
		case VariableMappingConfiguration.TYPE_VARIABLE:
			return configuration.getValue();
		default:
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.controller.IController#createForward(
	 *      org.eclipse.vtp.framework.spi.config.DispatchConfiguration)
	 */
	public IForwardDispatcher createForward(DispatchConfiguration configuration)
	{
		return new ForwardDispatcher(configuration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.controller.IController#createExit(
	 *      org.eclipse.vtp.framework.spi.configurations.ExitConfiguration)
	 */
	public IExitDispatcher createExit(ExitConfiguration configuration,
			AssignmentConfiguration[] assignments)
	{
		return new ExitDispatcher(configuration, assignments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.controller.IController#createInclude(
	 *      org.eclipse.vtp.framework.spi.config.DispatchConfiguration)
	 */
	public IIncludeDispatcher createInclude(DispatchConfiguration configuration)
	{
		return new IncludeDispatcher(configuration);
	}

	/**
	 * Abstract implementation of {@link IDispatcher}.
	 * 
	 * @author Lonnie Pryor
	 */
	private abstract class AbstractDispatcher implements IDispatcher
	{
		/**
		 * Creates a command to pass to the command processor.
		 * 
		 * @return A command to pass to the command processor.
		 */
		abstract ControllerCommand createCommand();

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.controller.IDispatcher#enqueue()
		 */
		public final boolean enqueue()
		{
			ControllerCommand command = createCommand();
			if (command == null)
				return false;
			return commandProcessor.enqueue(command);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.controller.IDispatcher#process()
		 */
		public final boolean process() throws IllegalStateException
		{
			ControllerCommand command = createCommand();
			if (command == null)
				return false;
			return commandProcessor.process(command);
		}
	}

	/**
	 * Implementation of {@link IForwardDispatcher}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ForwardDispatcher extends AbstractDispatcher implements
			IForwardDispatcher
	{
		/** The configuration to use. */
		private final DispatchConfiguration configuration;

		/**
		 * Creates a new ForwardDispatcher.
		 * 
		 * @param configuration The configuration to use.
		 */
		ForwardDispatcher(DispatchConfiguration configuration)
		{
			this.configuration = configuration;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.services.Controller.
		 *      AbstractDispatcher#createCommand()
		 */
		ControllerCommand createCommand()
		{
			ForwardCommand command = new ForwardCommand();
			command.setTargetProcessURI(configuration.getTargetProcessURI());
			String[] variableNames = configuration.getVariableNames();
			for (int i = 0; i < variableNames.length; ++i)
			{
				String localVariableName = resolveVariableMapping(configuration
						.getVariableMapping(variableNames[i]));
				if (localVariableName != null)
					command.setVariableValue(variableNames[i], localVariableName);
			}
			return command;
		}
	}

	/**
	 * Implementation of {@link IIncludeDispatcher}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class IncludeDispatcher extends AbstractDispatcher implements
			IIncludeDispatcher
	{
		/** The configuration to use. */
		private final DispatchConfiguration configuration;
		/** The parameters to set when the current process resumes. */
		private final Map<String, String[]> parameters = new HashMap<String, String[]>();

		/**
		 * Creates a new IncludeDispatcher.
		 * 
		 * @param configuration The configuration to use.
		 */
		IncludeDispatcher(DispatchConfiguration configuration)
		{
			this.configuration = configuration;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.services.Controller.
		 *      AbstractDispatcher#createCommand()
		 */
		ControllerCommand createCommand()
		{
			IncludeCommand command = new IncludeCommand();
			command.setTargetProcessURI(configuration.getTargetProcessURI());
			String[] variableNames = configuration.getVariableNames();
			for (int i = 0; i < variableNames.length; ++i)
			{
				String localVariableName = resolveVariableMapping(configuration
						.getVariableMapping(variableNames[i]));
				if (localVariableName != null)
					command.setVariableValue(variableNames[i], localVariableName);
			}
			String[] outgoing = configuration.getOutgoingPaths();
			for (int i = 0; i < outgoing.length; ++i)
			{
				String[] names = configuration.getOutgoingDataNames(outgoing[i]);
				for (int j = 0; j < names.length; ++j)
					command.setOutgoingDataValue(outgoing[i], names[j], configuration
							.getOutgoingDataValue(outgoing[i], names[j]));
			}
			for (Map.Entry<String, String[]> entry : parameters.entrySet())
			{
				command.setParameterValues(entry.getKey(), entry.getValue());
			}
			return command;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.controller.IInclude#setParameter(
		 *      java.lang.String, java.lang.String)
		 */
		public void setParameterValue(String name, String value)
		{
			if (name == null)
				return;
			setParameterValues(name, value == null ? null : new String[] { value });
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.controller.IInclude#setParameter(
		 *      java.lang.String, java.lang.String[])
		 */
		public void setParameterValues(String name, String[] values)
		{
			if (name == null)
				return;
			if (values == null)
				parameters.remove(name);
			else
				parameters.put(name, values);
		}
	}

	/**
	 * Implementation of {@link IExitDispatcher}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ExitDispatcher extends AbstractDispatcher implements
			IExitDispatcher
	{
		/** The configuration to use. */
		private final ExitConfiguration configuration;
		/** The configuration to use. */
		private final AssignmentConfiguration[] assignments;

		/**
		 * Creates a new ExitDispatcher.
		 * 
		 * @param configuration The configuration to use.
		 */
		ExitDispatcher(ExitConfiguration configuration,
				AssignmentConfiguration[] assignments)
		{
			this.configuration = configuration;
			this.assignments = assignments;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.services.Controller.
		 *      AbstractDispatcher#createCommand()
		 */
		ControllerCommand createCommand()
		{
			ExitCommand command = new ExitCommand();
			command.setExitValue(configuration.getValue());
			if (assignments != null)
			{
				for (int i = 0; i < assignments.length; ++i)
					command.setVariableValues(assignments[i].getName(), "");
			}
			return command;
		}
	}
}
