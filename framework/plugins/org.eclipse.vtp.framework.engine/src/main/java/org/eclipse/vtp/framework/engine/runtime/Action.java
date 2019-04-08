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
package org.eclipse.vtp.framework.engine.runtime;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.engine.ActionDescriptor;
import org.w3c.dom.Element;

/**
 * Represents an action that can be run by the engine.
 * 
 * @author Lonnie Pryor
 */
public class Action extends Executable
{
	/** The name of the action. */
	private final String name;
	/** The descriptor of the action. */
	private final ActionDescriptor descriptor;
	/** The index of results by ID. */
	private final Map<String, Executable> resultPaths = new HashMap<String, Executable>();

	/**
	 * Creates a new Action.
	 * 
	 * @param blueprint The blueprint of the process.
	 * @param name The name of the action.
	 * @param elements The configuration data or <code>null</code> for no
	 *          configuration data.
	 * @param instanceID The ID of this instance of the action.
	 * @param descriptor The descriptor of the action.
	 * @throws NullPointerException If the supplied blueprint is <code>null</code>.
	 * @throws NullPointerException If the supplied instance ID is
	 *           <code>null</code>.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	public Action(Blueprint blueprint, String name, Element[] elements, String instanceID,
			ActionDescriptor descriptor) throws NullPointerException
	{
		super(blueprint, descriptor == null ? null : descriptor.getType(), elements, instanceID);
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		this.name = name;
		this.descriptor = descriptor;
		this.resultPaths.put(IActionResult.RESULT_NAME_REPEAT, this);
	}

	/**
	 * Configures an available result of this action.
	 * 
	 * @param resultID The ID of the result path to configure.
	 * @param next The next executable in the path.
	 */
	public void configure(String resultID, Executable next)
	{
		resultPaths.put(resultID, next);
	}
	
	/**
	 * Returns the name of the action.
	 *
	 * @return The name of the action.
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Executable#
	 *      getActionInstance()
	 */
	public Action getActionInstance()
	{
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Executable#getActionState()
	 */
	public int getActionState()
	{
		return IActionContext.STATE_DURING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Executable#isBlocking()
	 */
	public boolean isBlocking()
	{
		return descriptor.isBlocking();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Executable#execute(
	 *      org.eclipse.vtp.framework.engine.runtime.Sequence)
	 */
	public Executable execute(Sequence sequence)
	{
		Dictionary report = new Hashtable();
		report.put("event", "action.starting");
		sequence.context.report(IReporter.SEVERITY_INFO, "Action \""
				+ getName() + "\" Starting", report);
		IActionResult actionResult = null;
		try
		{
			actionResult = ((IAction)createInstance(sequence)).execute();
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
			actionResult = sequence.context.createResult(null, e);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			actionResult = sequence.context.createResult(null, t);
		}
		report = new Hashtable();
		report.put("event", "action.ended");
		sequence.context.report(IReporter.SEVERITY_INFO, "Action \""
				+ getName() + "\" Ended", report);
		if (actionResult == null)
			actionResult = sequence.context.createResult(null);
		Throwable cause = actionResult.getFailureCause();
		if (cause != null && sequence.context.isErrorEnabled())
		{
			Hashtable properties = new Hashtable();
			properties.put("cause", cause); //$NON-NLS-1$
			sequence.context.error(cause.getMessage(), properties);
		}
		String resultName = actionResult.getName();
		sequence.context.info("Leaving Action \"" + getName() + "\" through \"" + resultName + "\".");
		Executable next = resultPaths.get(resultName);
		while (next == null)
		{
			if (IActionResult.RESULT_NAME_ABORT.equals(resultName))
				return null;
			else if (IActionResult.RESULT_NAME_ERROR.equals(resultName))
				resultName = IActionResult.RESULT_NAME_ABORT;
			else if (IActionResult.RESULT_NAME_DEFAULT.equals(resultName))
				resultName = IActionResult.RESULT_NAME_ERROR;
			else
			{
				int lastDot = resultName.lastIndexOf('.');
				if (lastDot < 0)
					resultName = IActionResult.RESULT_NAME_DEFAULT;
				else
					resultName = resultName.substring(0, lastDot);
			}
			next = resultPaths.get(resultName);
		}
		return next;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.runtime.Configurable#
	 *      getComponentType()
	 */
	protected Class getComponentType()
	{
		return descriptor.getType();
	}
}
