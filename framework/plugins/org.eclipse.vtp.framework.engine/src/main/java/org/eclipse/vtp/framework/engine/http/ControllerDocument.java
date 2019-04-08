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
package org.eclipse.vtp.framework.engine.http;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;

import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;

/**
 * ControllerDocument.
 * 
 * @author Lonnie Pryor
 */
public class ControllerDocument implements IDocument
{
	/** The URI identifying the process to transfer control to. */
	private String target = null;
	/** The variables that will be passed to the target process. */
	private final Map<String, Object> variables = new HashMap<String, Object>();
	/** The variables that will be passed back from the target process. */
	private final Map<String, Map<String, Object>> outgoingData = new HashMap<String, Map<String, Object>>();
	/** The parameters to set when the current process resumes. */
	private final Map<String, List<String>> parameters = new HashMap<String, List<String>>();

	/**
	 * Creates a new ControllerDocument.
	 */
	public ControllerDocument()
	{
	}

	/**
	 * Returns the URI identifying the process to transfer control to.
	 * 
	 * @return The URI identifying the process to transfer control to.
	 */
	public String getTarget()
	{
		return target;
	}

	/**
	 * Sets the URI identifying the process to transfer control to.
	 * 
	 * @param targetProcessURI The URI identifying the process to transfer control
	 *          to.
	 */
	public void setTarget(String targetProcessURI)
	{
		this.target = targetProcessURI;
	}

	/**
	 * Returns the names of the variables that will be passed to the target
	 * process.
	 * 
	 * @return The names of the variables that will be passed to the target
	 *         process.
	 */
	public String[] getVariableNames()
	{
		return variables.keySet().toArray(new String[variables.size()]);
	}

	/**
	 * Returns the name of the variable in the current process that will be set as
	 * the specified variable in the target process.
	 * 
	 * @param targetVariableName The name of the variable in the target process.
	 * @return The name of the variable in the current process that will be set as
	 *         the specified variable in the target process.
	 */
	public Object getVariableValue(String targetVariableName)
	{
		if (targetVariableName == null)
			return null;
		return variables.get(targetVariableName);
	}

	/**
	 * Sets the name of the variable in the current process that will be set as
	 * the specified variable in the target process.
	 * 
	 * @param targetVariableName The name of the variable in the target process.
	 * @param localVariableName The name of the variable in the current process to
	 *          pass to the target process.
	 */
	public void setVariableValue(String targetVariableName,
			Object localVariableName)
	{
		if (targetVariableName == null)
			return;
		if (localVariableName == null)
			variables.remove(targetVariableName);
		else
			variables.put(targetVariableName, localVariableName);
	}

	public String[] getOutgoingPaths()
	{
		return outgoingData.keySet().toArray(new String[outgoingData.size()]);
	}

	public String[] getOutgoingDataNames(String path)
	{
		Map<String, Object> map = outgoingData.get(path);
		if (map == null)
			return new String[0];
		return map.keySet().toArray(new String[map.size()]);
	}

	public Object getOutgoingDataValue(String path, String name)
	{
		Map<String, Object> map = outgoingData.get(path);
		if (map == null)
			return null;
		return map.get(name);
	}

	public void setOutgoingDataValue(String path, String name, Object value)
	{
		if (path == null || name == null)
			return;
		Map<String, Object> map = outgoingData.get(path);
		if (map == null)
		{
			if (value == null)
				return;
			outgoingData.put(path, map = new HashMap<String, Object>());
		}
		if (value == null)
		{
			map.remove(name);
			if (map.isEmpty())
				outgoingData.remove(path);
		}
		else
			map.put(name, value);
	}

	/**
	 * Returns the names of the parameters that will be returned from the target
	 * process.
	 * 
	 * @return The names of the parameters that will be returned from the target
	 *         process.
	 */
	public String[] getParameterNames()
	{
		return parameters.keySet().toArray(new String[parameters.size()]);
	}

	/**
	 * Returns the values of a parameter to be set when the current process
	 * resumes.
	 * 
	 * @param name The name of the parameter to be set.
	 * @return The values that specified parameter will be set to.
	 */
	public String[] getParameterValues(String name)
	{
		if (name == null)
			return null;
		List<String> list = parameters.get(name);
		if (list == null)
			return null;
		return list.toArray(new String[list.size()]);
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
			List<String> list = parameters.get(name);
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
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.IDocument#
	 *      getContentType()
	 */
	public String getContentType()
	{
		return DEFAULT_CONTENT_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.IDocument#
	 *      getDocumentType()
	 */
	public String getDocumentType()
	{
		return "CONTROLLER"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.IDocument#
	 *      toXMLSource()
	 */
	public Source toXMLSource() throws IllegalStateException
	{
		throw new IllegalStateException();
	}
	
	public boolean isSecured()
	{
		return false;
	}
	
	public void setSecured(boolean secured)
	{
	}

	@Override
	public boolean isCachable()
	{
		return true;
	}
}
