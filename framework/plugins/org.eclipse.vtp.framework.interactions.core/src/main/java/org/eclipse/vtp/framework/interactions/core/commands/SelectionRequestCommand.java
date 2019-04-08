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
package org.eclipse.vtp.framework.interactions.core.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A command that accepts a selection from the user.
 * 
 * @author Lonnie Pryor
 */
public final class SelectionRequestCommand extends ConversationCommand
{
	/** The name of the parameter to pass the provided input as. */
	private String selectionName = null;
	/** The name of the parameter to pass the result of the request as. */
	private String resultName = null;
	/** The value of the result parameter to pass if the input is valid. */
	private String filledResultValue = null;
	/** The value of the result parameter to pass if the input is missing. */
	private String noInputResultValue = null;
	/** The value of the result parameter to pass if the input is invalid. */
	private String noMatchResultValue = null;
	/** The value of the result parameter to pass if the caller hungup. */
	private String hangupResultValue = null;
	/** The properties of the interaction. */
	private final Map properties = new HashMap();
	/** The output to be rendered. */
	private final List<Output> output = new ArrayList<Output>();
	/** The options to be rendered. */
	private final List options = new ArrayList();
	/** The parameters to set when the process resumes. */
	private final Map parameters = new HashMap();

	/**
	 * Creates a new InputRequestCommand.
	 */
	public SelectionRequestCommand()
	{
	}

	/**
	 * Returns the name of the parameter to pass the provided input as.
	 * 
	 * @return The name of the parameter to pass the provided input as.
	 */
	public String getSelectionName()
	{
		return selectionName;
	}

	/**
	 * Sets the name of the parameter to pass the provided input as.
	 * 
	 * @param inputName The name of the parameter to pass the provided input as.
	 */
	public void setSelectionName(String inputName)
	{
		this.selectionName = inputName;
	}

	/**
	 * Returns the name of the parameter to pass the result of the request as.
	 * 
	 * @return The name of the parameter to pass the result of the request as.
	 */
	public String getResultName()
	{
		return resultName;
	}

	/**
	 * Sets the name of the parameter to pass the result of the request as.
	 * 
	 * @param resultName The name of the parameter to pass the result of the
	 *          request as.
	 */
	public void setResultName(String resultName)
	{
		this.resultName = resultName;
	}

	/**
	 * Returns the value of the result parameter to pass if the input is valid.
	 * 
	 * @return The value of the result parameter to pass if the input is valid.
	 */
	public String getFilledResultValue()
	{
		return filledResultValue;
	}

	/**
	 * Sets the value of the result parameter to pass if the input is valid.
	 * 
	 * @param filledResultValue The value of the result parameter to pass if the
	 *          input is valid.
	 */
	public void setFilledResultValue(String filledResultValue)
	{
		this.filledResultValue = filledResultValue;
	}

	/**
	 * Returns the value of the result parameter to pass if the input is missing.
	 * 
	 * @return The value of the result parameter to pass if the input is missing.
	 */
	public String getNoInputResultValue()
	{
		return noInputResultValue;
	}

	/**
	 * Sets the value of the result parameter to pass if the input is missing.
	 * 
	 * @param noInputResultValue The value of the result parameter to pass if the
	 *          input is missing.
	 */
	public void setNoInputResultValue(String noInputResultValue)
	{
		this.noInputResultValue = noInputResultValue;
	}

	/**
	 * Returns the value of the result parameter to pass if the input is invalid.
	 * 
	 * @return The value of the result parameter to pass if the input is invalid.
	 */
	public String getNoMatchResultValue()
	{
		return noMatchResultValue;
	}

	/**
	 * Sets the value of the result parameter to pass if the input is invalid.
	 * 
	 * @param noMatchResultValue The value of the result parameter to pass if the
	 *          input is invalid.
	 */
	public void setNoMatchResultValue(String noMatchResultValue)
	{
		this.noMatchResultValue = noMatchResultValue;
	}

	/**
	 * Returns the value of the result parameter to pass if the caller hungup.
	 * 
	 * @return The value of the result parameter to pass if the caller hungup.
	 */
	public String getHangupResultValue()
	{
		return hangupResultValue;
	}

	/**
	 * Sets the value of the result parameter to pass if the caller hungup.
	 * 
	 * @param hangupResultValue The value of the result parameter to pass if the
	 *          caller hungup.
	 */
	public void setHangupResultValue(String hangupResultValue)
	{
		this.hangupResultValue = hangupResultValue;
	}

	/**
	 * Returns the names of the properties of the interaction.
	 * 
	 * @return The names of the properties of the interaction.
	 */
	public String[] getPropertyNames()
	{
		return (String[])properties.keySet().toArray(new String[properties.size()]);
	}

	/**
	 * Returns the value of a property of the interaction.
	 * 
	 * @param name The name of the property to be set.
	 * @return The value that the specified property will be set to.
	 */
	public String getPropertyValue(String name)
	{
		if (name == null)
			return null;
		return (String)properties.get(name);
	}

	/**
	 * Configures a property of the interaction.
	 * 
	 * @param name The name of the property to set.
	 * @param value The value to set the property to.
	 */
	public void setPropertyValue(String name, String value)
	{
		if (name == null)
			return;
		if (value == null)
			properties.remove(name);
		else
			properties.put(name, value);
	}

	/**
	 * Returns the number of output elements configured in this selection request.
	 * 
	 * @return The number of output elements configured in this selection request.
	 */
	public int getOutputCount()
	{
		return output.size();
	}

	/**
	 * Returns the type of the output at the specified index.
	 * 
	 * @param outputIndex The index to check.
	 * @return The type of the output at the specified index.
	 */
	public int getOutputType(int outputIndex)
	{
		if (outputIndex < 0 || outputIndex >= output.size())
			return 0;
		if(Output.TYPE_FILE.equals(output.get(outputIndex).getType()))
			return OUTPUT_TYPE_FILE;
		if(Output.TYPE_TEXT.equals(output.get(outputIndex).getType()))
			return OUTPUT_TYPE_TEXT;
		return 0;
	}

	/**
	 * Returns the value of the output at the specified index.
	 * 
	 * @param outputIndex The index to check.
	 * @return The value of the output at the specified index.
	 */
	public String getOutputValue(int outputIndex)
	{
		if (outputIndex < 0 || outputIndex >= output.size())
			return null;
		return output.get(outputIndex).getProperty("value");
	}

	/**
	 * Adds a file output item to this output message.
	 * 
	 * @param path The path of the file to render.
	 */
	public void addOutput(Output o)
	{
		output.add(o);
	}

	/**
	 * Adds a file output item to this output message at the specified index.
	 * 
	 * @param outputIndex The index to insert at.
	 * @param path The path of the file to render.
	 */
	public void insertOutput(int outputIndex, Output o)
	{
		if (outputIndex >= 0 && outputIndex <= output.size())
		{
			output.add(outputIndex, o);
		}
	}

	/**
	 * Sets the file output item in this output message at the specified index.
	 * 
	 * @param outputIndex The index to set at.
	 * @param path The path of the file to render.
	 */
	public void setOutput(int outputIndex, Output o)
	{
		if (outputIndex >= 0 && outputIndex < output.size())
			output.set(outputIndex, o);
	}

	/**
	 * Removes the output item in this selection request at the specified index.
	 * 
	 * @param outputIndex The index to remove at.
	 */
	public void removeOutput(int outputIndex)
	{
		if (outputIndex >= 0 && outputIndex < output.size())
			output.remove(outputIndex);
	}

	/**
	 * Returns the number of options configured in this selection request.
	 * 
	 * @return The number of options configured in this selection request.
	 */
	public int getOptionCount()
	{
		return options.size();
	}

	/**
	 * Returns the name of the option at the specified index.
	 * 
	 * @param optionIndex The index to check.
	 * @return The name of the option at the specified index.
	 */
	public String getOption(int optionIndex)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return null;
		return ((Option)options.get(optionIndex)).name;
	}

	/**
	 * Adds an option to this selection request.
	 * 
	 * @param optionName The name of the option to add.
	 */
	public void addOption(String optionName)
	{
		insertOption(options.size(), optionName);
	}

	/**
	 * Inserts an option into this selection request at the specified index..
	 * 
	 * @param optionIndex The index to insert at.
	 * @param optionName The name of the option to insert.
	 */
	public void insertOption(int optionIndex, String optionName)
	{
		if (optionName != null && optionIndex >= 0 && optionIndex <= options.size())
			options.add(optionIndex, new Option(optionName));
	}

	/**
	 * Sets an option in this selection request at the specified index..
	 * 
	 * @param optionIndex The index to set at.
	 * @param optionName The name of the option to set.
	 */
	public void setOption(int optionIndex, String optionName)
	{
		if (optionName != null && optionIndex >= 0 && optionIndex < options.size())
			options.set(optionIndex, new Option(optionName));
	}

	/**
	 * Removes the option in this selection request at the specified index.
	 * 
	 * @param optionIndex The index to remove at.
	 */
	public void removeOption(int optionIndex)
	{
		if (optionIndex >= 0 && optionIndex < options.size())
			options.remove(optionIndex);
	}

	/**
	 * Returns the number of output elements configured in this selection request
	 * option.
	 * 
	 * @param optionIndex The index to check.
	 * @return The number of output elements configured in this selection request
	 *         option.
	 */
	public int getOptionOutputCount(int optionIndex)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return 0;
		return ((Option)options.get(optionIndex)).output.size();
	}

	/**
	 * Returns the type of the output at the supplied output index for the option
	 * at the specified index.
	 * 
	 * @param optionIndex The option index to check.
	 * @param optionOutputIndex The output index to check.
	 * @return The type of the output at the supplied output index for the option
	 *         at the specified index.
	 */
	public int getOptionOutputType(int optionIndex, int optionOutputIndex)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return 0;
		Option option = (Option)options.get(optionIndex);
		if (optionOutputIndex < 0 || optionOutputIndex >= option.output.size())
			return 0;
		if(Output.TYPE_FILE.equals(option.output.get(optionOutputIndex).getType()))
			return OUTPUT_TYPE_FILE;
		if(Output.TYPE_TEXT.equals(option.output.get(optionOutputIndex).getType()))
			return OUTPUT_TYPE_TEXT;
		return 0;
	}

	/**
	 * Returns the value of the output at the supplied output index for the option
	 * at the specified index.
	 * 
	 * @param optionIndex The option index to check.
	 * @param optionOutputIndex The output index to check.
	 * @return The value of the output at the supplied output index for the option
	 *         at the specified index.
	 */
	public String getOptionOutputValue(int optionIndex, int optionOutputIndex)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return null;
		Option option = (Option)options.get(optionIndex);
		if (optionOutputIndex < 0 || optionOutputIndex >= option.output.size())
			return null;
		return option.output.get(optionOutputIndex).getProperty("value");
	}

	/**
	 * Adds a file output item to this selection request.
	 * 
	 * @param optionIndex The option index to modify.
	 * @param path The path of the file to render.
	 */
	public void addOptionOutput(int optionIndex, Output o)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return;
		Option option = (Option)options.get(optionIndex);
		option.output.add(o);
	}

	/**
	 * Adds a file output item to this selection request at the specified index.
	 * 
	 * @param optionIndex The option index to modify.
	 * @param optionOutputIndex The index to insert at.
	 * @param path The path of the file to render.
	 */
	public void insertOptionOutput(int optionIndex, int optionOutputIndex,
			Output o)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return;
		Option option = (Option)options.get(optionIndex);
		if (optionOutputIndex >= 0 && optionOutputIndex <= option.output.size())
			option.output.add(optionOutputIndex, o);
	}

	/**
	 * Sets the file output item in this selection request at the specified index.
	 * 
	 * @param optionIndex The option index to modify.
	 * @param optionOutputIndex The index to set at.
	 * @param path The path of the file to render.
	 */
	public void setOptionOutput(int optionIndex, int optionOutputIndex,
			Output o)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return;
		Option option = (Option)options.get(optionIndex);
		if (optionOutputIndex >= 0 && optionOutputIndex < option.output.size())
			option.output.set(optionOutputIndex, o);
	}

	/**
	 * Removes the output item in this selection request at the specified index.
	 * 
	 * @param optionIndex The option index to modify.
	 * @param optionOutputIndex The index to remove at.
	 */
	public void removeOptionOutput(int optionIndex, int optionOutputIndex)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return;
		Option option = (Option)options.get(optionIndex);
		if (optionOutputIndex >= 0 && optionOutputIndex < option.output.size())
			option.output.remove(optionOutputIndex);
	}

	/**
	 * Returns a property value of the option at the specified index.
	 * 
	 * @param optionIndex The index to check.
	 * @param key The property key.
	 * @return The key of the option at the specified index.
	 */
	public String getOptionProperty(int optionIndex, String key)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return null;
		return (String)((Option)options.get(optionIndex)).properties.get(key);
	}

	/**
	 * Sets a property value of the option at the specified index.
	 * 
	 * @param optionIndex The index to check.
	 * @param key The property key.
	 * @param value The property value.
	 */
	public void setOptionProperty(int optionIndex, String key, String value)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return;
		if (value == null)
			((Option)options.get(optionIndex)).properties.remove(key);
		else
			((Option)options.get(optionIndex)).properties.put(key, value);
	}

	/**
	 * Returns the type of the input descriptor for the option at the specified
	 * index.
	 * 
	 * @param optionIndex The index to check.
	 * @return The type of the input descriptor for the option at the specified
	 *         index.
	 */
	public int getOptionInputType(int optionIndex)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return 0;
		Option option = (Option)options.get(optionIndex);
		if (option.input == null)
			return 0;
		return option.input.getType();
	}

	/**
	 * Returns the value of the input descriptor for the option at the specified
	 * index.
	 * 
	 * @param optionIndex The index to check.
	 * @return The value of the input descriptor for the option at the specified
	 *         index.
	 */
	public String getOptionInputValue(int optionIndex)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return null;
		Option option = (Option)options.get(optionIndex);
		if (option.input == null)
			return null;
		return option.input.getProperty("value");
	}

	/**
	 * Sets the input descriptor to a resource at the specified path for the
	 * option at the specified index.
	 * 
	 * @param optionIndex The index to set.
	 * @param path The path of the resource describing the option input.
	 */
	public void setOptionInput(int optionIndex, Input i)
	{
		if (optionIndex < 0 || optionIndex >= options.size())
			return;
		((Option)options.get(optionIndex)).input = i;
	}

	/**
	 * Returns the names of the parameters that will be returned from the
	 * interaction.
	 * 
	 * @return The names of the parameters that will be returned from the
	 *         interaction.
	 */
	public String[] getParameterNames()
	{
		return (String[])parameters.keySet().toArray(new String[parameters.size()]);
	}

	/**
	 * Returns the values of a parameter to be set when the process resumes.
	 * 
	 * @param name The name of the parameter to be set.
	 * @return The values that specified parameter will be set to.
	 */
	public String[] getParameterValues(String name)
	{
		if (name == null)
			return null;
		List list = (List)parameters.get(name);
		if (list == null)
			return null;
		return (String[])list.toArray(new String[list.size()]);
	}

	/**
	 * Configures a parameter set when the current process resumes.
	 * 
	 * @param name The name of the parameter to set.
	 * @param values The values to set the parameter to.
	 */
	public void setParameterValues(String name, String[] values)
	{
		if (name == null)
			return;
		if (values == null)
			parameters.remove(name);
		else
		{
			List list = (List)parameters.get(name);
			if (list == null)
				parameters.put(name, list = new LinkedList());
			else
				list.clear();
			for (int i = 0; i < values.length; ++i)
				if (values[i] != null)
					list.add(values[i]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.commands.
	 *      ConversationCommand#accept(
	 *      org.eclipse.vtp.framework.interactions.core.commands.
	 *      IConversationCommandVisitor)
	 */
	Object accept(IConversationCommandVisitor visitor)
	{
		return visitor.visitSelectionRequest(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#exportContents()
	 */
	public Object exportContents()
	{
		List properties = new ArrayList(this.properties.size() * 2);
		for (Iterator i = this.properties.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			properties.add((String)entry.getKey());
			properties.add((String)entry.getValue());
		}
		List<Object> output = new ArrayList<Object>(this.output.size() * 2);
		for (Output o : this.output)
		{
			output.add(o.getType());
			Set<String> propNames = o.getPropertyNames();
			String[] props = new String[propNames.size() * 2];
			int i = 0;
			for(String pn : propNames)
			{
				props[i] = pn;
				props[i + 1] = o.getProperty(pn);
				i++;
			}
			output.add(props);
		}
		List options = new ArrayList(this.options.size());
		for (Iterator i = this.options.iterator(); i.hasNext();)
		{
			Option entry = (Option)i.next();
			options.add(entry.exportContents());
		}
		List parameters = new ArrayList(this.parameters.size() * 2);
		for (Iterator i = this.parameters.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			parameters.add((String)entry.getKey());
			parameters.add((String)entry.getValue());
		}
		return new Object[] { selectionName, resultName, filledResultValue,
				noInputResultValue, noMatchResultValue,
				properties.toArray(new String[properties.size()]),
				output.toArray(new Object[output.size()]), options.toArray(),
				parameters.toArray(new String[parameters.size()]) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#importContents(
	 *      java.lang.Object)
	 */
	public void importContents(Object contents)
	{
		Object[] array = (Object[])contents;
		this.selectionName = (String)array[0];
		this.resultName = (String)array[1];
		this.filledResultValue = (String)array[2];
		this.noInputResultValue = (String)array[3];
		this.noMatchResultValue = (String)array[4];
		this.properties.clear();
		String[] properties = (String[])array[5];
		for (int i = 0; i < properties.length; i += 2)
			this.properties.put(properties[i], properties[i + 1]);
		this.output.clear();
		Object[] output = (Object[])array[1];
		for (int i = 0; i < output.length; i += 2)
		{
			Output o = new Output((String)output[i]);
			String[] props = (String[])output[i + 1];
			for(int p = 0; p < props.length; p+=2)
			{
				o.setProperty(props[p], props[p + 1]);
			}
			this.output.add(o);
		}
		this.parameters.clear();
		Object[] options = (Object[])array[7];
		for (int i = 0; i < options.length; ++i)
		{
			Option option = new Option();
			option.importContents(options[i]);
			this.options.add(option);
		}
		String[] parameters = (String[])array[8];
		for (int i = 0; i < parameters.length; i += 2)
			this.parameters.put(parameters[i], parameters[i + 1]);
	}

	/**
	 * A single selectable option.
	 * 
	 * @author Lonnie Pryor
	 */
	private static final class Option
	{
		/** The option name. */
		String name;
		/** The output to be rendered. */
		final List<Output> output = new ArrayList<Output>();
		/** The option properties. */
		final Map properties = new HashMap();
		/** The option input. */
		Input input = null;

		/**
		 * Creates a new Option.
		 */
		Option()
		{
			this.name = null;
		}

		/**
		 * Creates a new Option.
		 * 
		 * @param name The option name.
		 */
		Option(String name)
		{
			this.name = name;
		}

		/**
		 * Exports the contents of this command to a simple structure of arrays and
		 * serializable values from <code>java.lang</code>.
		 * 
		 * @return A serializable structure this command can be re-constituted from.
		 */
		Object exportContents()
		{
			List<Object> output = new ArrayList<Object>(this.output.size() * 2);
			for (Output o : this.output)
			{
				output.add(o.getType());
				Set<String> propNames = o.getPropertyNames();
				String[] props = new String[propNames.size() * 2];
				int i = 0;
				for(String pn : propNames)
				{
					props[i] = pn;
					props[i + 1] = o.getProperty(pn);
					i++;
				}
				output.add(props);
			}
			List properties = new ArrayList(this.properties.size() * 2);
			for (Iterator i = this.properties.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry entry = (Map.Entry)i.next();
				properties.add((String)entry.getKey());
				properties.add((String)entry.getValue());
			}
			Object[] inputRef = new Object[2];
			if(input != null)
			{
				inputRef[0] = input.getType();
				Set<String> propNames = input.getPropertyNames();
				String[] props = new String[propNames.size() * 2];
				int i = 0;
				for(String pn : propNames)
				{
					props[i] = pn;
					props[i + 1] = input.getProperty(pn);
					i++;
				}
				inputRef[1] = props;
			}
			return new Object[] {
					name,
					output.toArray(new Object[output.size()]),
					properties.toArray(new String[properties.size()]),
					inputRef};
		}

		/**
		 * Configures the contents of this command with a structure previously
		 * returned from {@link #exportContents()}.
		 * 
		 * @param contents The exported contents structure to load from.
		 */
		void importContents(Object contents)
		{
			Object[] array = (Object[])contents;
			this.name = (String)array[0];
			this.output.clear();
			Object[] output = (Object[])array[1];
			for (int i = 0; i < output.length; i += 2)
			{
				Output o = new Output((String)output[i]);
				String[] props = (String[])output[i + 1];
				for(int p = 0; p < props.length; p+=2)
				{
					o.setProperty(props[p], props[p + 1]);
				}
				this.output.add(o);
			}
			String[] properties = (String[])array[2];
			for (int i = 0; i < properties.length; i += 2)
				this.properties.put(properties[i], properties[i + 1]);
			Object[] inputRef = (Object[])array[3];
			if(inputRef[0] != null)
			{
				input = new Input((Integer)inputRef[0]);
				String[] props = (String[])output[1];
				for(int p = 0; p < props.length; p+=2)
				{
					input.setProperty(props[p], props[p + 1]);
				}
			}
		}
	}
}
