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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A command that sends an external reference to the user.
 * 
 * @author Lonnie Pryor
 */
public final class ExternalReferenceCommand extends ConversationCommand {
	/** The name of the external reference being called. */
	private String referenceName = null;
	/** The name of the parameter to pass the result of the request as. */
	private String resultName = null;
	/** The value of the result parameter to pass if the input is valid. */
	private String filledResultValue = null;
	/** The value of the result parameter to pass if the caller hungup. */
	private String hangupResultValue = null;
	/** The URL of the external reference. */
	private String referenceURI = null;
	/** The arguments to pass to the external entity. */
	private final Map<String, String> inputArguments = new HashMap<String, String>();
	/** The arguments to accept from the external entity. */
	private final Map<String, String> outputArguments = new HashMap<String, String>();
	/** The parameters to set when the process resumes. */
	private final Map<String, Object> parameters = new HashMap<String, Object>();
	private final Map<String, String> urlParameters = new HashMap<String, String>();

	/**
	 * Creates a new ExternalReferenceCommand.
	 */
	public ExternalReferenceCommand() {
	}

	/**
	 * Returns the name of the external reference being called.
	 * 
	 * @return The name of the external reference being called.
	 */
	public String getReferenceName() {
		return referenceName;
	}

	/**
	 * Sets the name of the external reference being called.
	 * 
	 * @param inputName The name of the external reference being called.
	 */
	public void setReferenceName(String inputName) {
		this.referenceName = inputName;
	}

	/**
	 * Returns the name of the parameter to pass the result of the request as.
	 * 
	 * @return The name of the parameter to pass the result of the request as.
	 */
	public String getResultName() {
		return resultName;
	}

	/**
	 * Sets the name of the parameter to pass the result of the request as.
	 * 
	 * @param resultName The name of the parameter to pass the result of the request
	 *                   as.
	 */
	public void setResultName(String resultName) {
		this.resultName = resultName;
	}

	/**
	 * Returns the value of the result parameter to pass if the input is valid.
	 * 
	 * @return The value of the result parameter to pass if the input is valid.
	 */
	public String getFilledResultValue() {
		return filledResultValue;
	}

	/**
	 * Sets the value of the result parameter to pass if the input is valid.
	 * 
	 * @param filledResultValue The value of the result parameter to pass if the
	 *                          input is valid.
	 */
	public void setFilledResultValue(String filledResultValue) {
		this.filledResultValue = filledResultValue;
	}

	/**
	 * Returns the value of the result parameter to pass if the caller hungup.
	 * 
	 * @return The value of the result parameter to pass if the caller hungup.
	 */
	public String getHangupResultValue() {
		return hangupResultValue;
	}

	/**
	 * Sets the value of the result parameter to pass if the caller hungup.
	 * 
	 * @param noInputResultValue The value of the result parameter to pass if the
	 *                           caller hungup.
	 */
	public void setHangupResultValue(String hangupResultValue) {
		this.hangupResultValue = hangupResultValue;
	}

	/**
	 * Returns the URI of the external reference.
	 * 
	 * @return The URI of the external reference.
	 */
	public String getReferenceURI() {
		return referenceURI;
	}

	/**
	 * Sets the URI of the external reference.
	 * 
	 * @param referenceURI The URI of the external reference.
	 */
	public void setReferenceURI(String referenceURI) {
		this.referenceURI = referenceURI;
	}

	/**
	 * Returns the names of the arguments the resource takes as input.
	 * 
	 * @return The names of the arguments the resource takes as input.
	 */
	public String[] getInputArgumentNames() {
		return (String[]) inputArguments.keySet().toArray(new String[inputArguments.size()]);
	}

	/**
	 * Returns the value of an argument the resource takes as input.
	 * 
	 * @param name The name of the argument to be passed.
	 * @return The value of an argument the resource takes as input.
	 */
	public String getInputArgumentValue(String name) {
		if (name == null)
			return null;
		return (String) inputArguments.get(name);
	}

	/**
	 * Configures the value of an argument the resource takes as input.
	 * 
	 * @param name  The name of the argument to pass.
	 * @param value The value of the argument.
	 */
	public void setInputArgumentValue(String name, String value) {
		if (name == null)
			return;
		if (value == null)
			inputArguments.remove(name);
		else
			inputArguments.put(name, value);
	}

	/**
	 * Returns the names of the arguments the resource returns as output.
	 * 
	 * @return The names of the arguments the resource returns as output.
	 */
	public String[] getOutputArgumentNames() {
		return (String[]) outputArguments.keySet().toArray(new String[outputArguments.size()]);
	}

	/**
	 * Returns the value of an argument the resource returns as output.
	 * 
	 * @param name The name of the argument to be returned.
	 * @return The value of an argument the resource returns as output.
	 */
	public String getOutputArgumentValue(String name) {
		if (name == null)
			return null;
		return (String) outputArguments.get(name);
	}

	/**
	 * Configures the value of an argument the resource returns as output.
	 * 
	 * @param name  The name of the argument to return.
	 * @param value The value of the argument.
	 */
	public void setOutputArgumentValue(String name, String value) {
		if (name == null)
			return;
		if (value == null)
			outputArguments.remove(name);
		else
			outputArguments.put(name, value);
	}

	public String[] getURLParameterNames() {
		return (String[]) urlParameters.keySet().toArray(new String[urlParameters.size()]);
	}

	public String getURLParameterValue(String name) {
		if (name == null)
			return null;
		return (String) urlParameters.get(name);
	}

	public void setURLParameterValue(String name, String value) {
		if (name == null)
			return;
		if (value == null)
			urlParameters.remove(name);
		else
			urlParameters.put(name, value);
	}

	/**
	 * Returns the names of the parameters that will be returned from the
	 * interaction.
	 * 
	 * @return The names of the parameters that will be returned from the
	 *         interaction.
	 */
	public String[] getParameterNames() {
		return (String[]) parameters.keySet().toArray(new String[parameters.size()]);
	}

	/**
	 * Returns the values of a parameter to be set when the process resumes.
	 * 
	 * @param name The name of the parameter to be set.
	 * @return The values that specified parameter will be set to.
	 */
	public String[] getParameterValues(String name) {
		if (name == null)
			return null;
		List<?> list = (List<?>) parameters.get(name);
		if (list == null)
			return null;
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * Configures a parameter set when the current process resumes.
	 * 
	 * @param name   The name of the parameter to set.
	 * @param values The values to set the parameter to.
	 */
	@SuppressWarnings("unchecked")
	public void setParameterValues(String name, String[] values) {
		if (name == null)
			return;
		if (values == null)
			parameters.remove(name);
		else {
			List<String> list = (List<String>) parameters.get(name);
			if (list == null)
				parameters.put(name, list = new LinkedList<String>());
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
	 * ConversationCommand#accept(
	 * org.eclipse.vtp.framework.interactions.core.commands.
	 * IConversationCommandVisitor)
	 */
	Object accept(IConversationCommandVisitor visitor) {
		return visitor.visitExternalReference(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#exportContents()
	 */
	public Object exportContents() {
		List<String> inputArguments = new ArrayList<String>(this.inputArguments.size() * 2);
		for (Entry<String, String> entry : this.inputArguments.entrySet()) {
			inputArguments.add((String) entry.getKey());
			inputArguments.add((String) entry.getValue());
		}
		List<String> urlArguments = new ArrayList<String>(this.urlParameters.size() * 2);
		for (Entry<String, String> entry : this.urlParameters.entrySet()) {
			urlArguments.add((String) entry.getKey());
			urlArguments.add((String) entry.getValue());
		}
		List<String> outputArguments = new ArrayList<String>(this.outputArguments.size() * 2);
		for (Entry<String, String> entry : this.outputArguments.entrySet()) {
			outputArguments.add((String) entry.getKey());
			outputArguments.add((String) entry.getValue());
		}
		List<String> parameters = new ArrayList<String>(this.parameters.size() * 2);
		for (Entry<String, Object> entry : this.parameters.entrySet()) {
			parameters.add((String) entry.getKey());
			parameters.add((String) entry.getValue());
		}
		return new Object[] { referenceName, referenceURI, inputArguments.toArray(new String[inputArguments.size()]),
				outputArguments.toArray(new String[outputArguments.size()]),
				parameters.toArray(new String[parameters.size()]) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#importContents( java.lang.Object)
	 */
	public void importContents(Object contents) {
		Object[] array = (Object[]) contents;
		referenceName = (String) array[0];
		referenceURI = (String) array[1];
		this.inputArguments.clear();
		String[] inputArguments = (String[]) array[2];
		for (int i = 0; i < inputArguments.length; i += 2)
			this.inputArguments.put(inputArguments[i], inputArguments[i + 1]);
		this.urlParameters.clear();
		String[] urlArguments = (String[]) array[2];
		for (int i = 0; i < urlArguments.length; i += 2)
			this.urlParameters.put(urlArguments[i], urlArguments[i + 1]);
		this.outputArguments.clear();
		String[] outputArguments = (String[]) array[3];
		for (int i = 0; i < outputArguments.length; i += 2)
			this.outputArguments.put(outputArguments[i], outputArguments[i + 1]);
		this.parameters.clear();
		String[] parameters = (String[]) array[4];
		for (int i = 0; i < parameters.length; i += 2)
			this.parameters.put(parameters[i], parameters[i + 1]);
	}
}
