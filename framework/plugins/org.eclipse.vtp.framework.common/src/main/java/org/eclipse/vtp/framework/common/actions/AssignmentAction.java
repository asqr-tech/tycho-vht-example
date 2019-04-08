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
package org.eclipse.vtp.framework.common.actions;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.vtp.framework.common.IBooleanObject;
import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IDateObject;
import org.eclipse.vtp.framework.common.IDecimalObject;
import org.eclipse.vtp.framework.common.INumberObject;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;

/**
 * An action that modifies the variable registry.
 * 
 * @author Lonnie Pryor
 */
public class AssignmentAction implements IAction
{
	/** The context to use. */
	protected final IActionContext context;
	/** The variable registry to use. */
	protected final IVariableRegistry variableRegistry;
	/** The configurations to use. */
	protected final AssignmentConfiguration[] configurations;

	/**
	 * Creates a new AssignmentAction.
	 * 
	 * @param context The context to use.
	 * @param variableRegistry The variable registry to use.
	 * @param configurations The configurations to use.
	 */
	public AssignmentAction(IActionContext context,
			IVariableRegistry variableRegistry,
			AssignmentConfiguration[] configurations)
	{
		this.context = context;
		this.variableRegistry = variableRegistry;
		this.configurations = configurations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IAction#execute()
	 */
	public IActionResult execute()
	{
		return execute(Collections.<String, String>emptyMap(), true);
	}

	/**
	 * execute.
	 * 
	 * @param values
	 * @return
	 */
	protected IActionResult execute(Map<String, String> values, boolean overwrite)
	{
		try
		{
			if(context.isInfoEnabled()) context.info("Performing variable assignments");
			for (int i = 0; i < configurations.length; ++i)
			{
				if(context.isInfoEnabled()) context.info("Assigning variable \"" + configurations[i].getName() + "\"");
				IDataObject object = variableRegistry.getVariable(configurations[i].getName());
				if(object == null || (object != null && overwrite))
				{
					object = variableRegistry.createVariable(configurations[i]
							.getType(), configurations[i].isSecured());
					String value = configurations[i].getValue();
					if (values.containsKey(configurations[i].getName()))
						value = values.get(configurations[i].getName());
					if (value != null) {
						IDataObject toSet = object;
						String primaryField = object.getType().getPrimaryFieldName();
						if (primaryField != null)
							toSet = object.getField(primaryField);
						if (toSet instanceof IBooleanObject)
							((IBooleanObject)toSet).setValue(value);
						else if (toSet instanceof IDateObject)
							((IDateObject)toSet).setValue(value);
						else if (toSet instanceof IDecimalObject)
							((IDecimalObject)toSet).setValue(value);
						else if (toSet instanceof INumberObject)
							((INumberObject)toSet).setValue(value);
						else if (toSet instanceof IStringObject)
							((IStringObject)toSet).setValue(value);
					}
					variableRegistry.setVariable(configurations[i].getName(), object);
					if(context.isReportingEnabled())
					{
						Dictionary<String, Object> props = new Hashtable<String, Object>();
						props.put("event", "assignment");
						props.put("event.key", configurations[i].getName());
						props.put("event.value", configurations[i].isSecured() ? "**Secured**" : String.valueOf(object));
						context.report(IReporter.SEVERITY_INFO, "Assigned variable \""
							+ configurations[i].getName() + "\" to \"" + (configurations[i].isSecured() ? "**Secured**" : String.valueOf(object)) + "\"", props);
					}
				}
			}
			if(context.isInfoEnabled()) context.info("Variable assignments complete");
			return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
		}
		catch (RuntimeException e)
		{
			return context.createResult("error.assign", e); //$NON-NLS-1$
		}
	}
}
