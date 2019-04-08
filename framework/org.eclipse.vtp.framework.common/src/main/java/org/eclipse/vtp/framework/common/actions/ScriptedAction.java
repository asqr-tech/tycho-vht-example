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

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.vtp.framework.common.IScriptingEngine;
import org.eclipse.vtp.framework.common.IScriptingService;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.common.ScriptingException;
import org.eclipse.vtp.framework.common.configurations.ScriptConfiguration;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;

/**
 * An action that runs a configured script.
 * 
 * @author Lonnie Pryor
 */
public class ScriptedAction implements IAction
{
	/** The context that contains this action. */
	private final IActionContext context;
	/** The scripting engine to execute the script with. */
	private final IScriptingEngine engine;
	/** The configuration for this scripted action. */
	private final String script;
	/** The variable registry. */
	private final IVariableRegistry variableRegistry;

	/**
	 * Creates a new ScriptedAction.
	 * 
	 * @param context The context that contains this action.
	 * @param scriptingService The scripting service to create an engine with.
	 * @param configuration The configuration for this scripted action.
	 */
	public ScriptedAction(IActionContext context,
			IScriptingService scriptingService,
			ScriptConfiguration configuration, IVariableRegistry variableRegistry)
	{
		this.context = context;
		this.engine = scriptingService.createScriptingEngine(configuration
				.getScriptingLanguage());
		this.script = configuration.getScript();
		this.variableRegistry = variableRegistry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IAction#execute()
	 */
	public IActionResult execute()
	{
		if(context.isReportingEnabled())
		{
			Dictionary<String, Object> props = new Hashtable<String, Object>();
			props.put("event", "script.before");
			context.report(IReporter.SEVERITY_INFO, "Executing script.", props);
		}
		if(context.isInfoEnabled()) context.info("Executing script...");
		try
		{
			 engine.execute(script);
		}
		catch (ScriptingException e)
		{
			e.printStackTrace();
			IStringObject titleVar = (IStringObject)variableRegistry.createVariable(IStringObject.TYPE_NAME);
			titleVar.setValue(e.getTitle());
			variableRegistry.setVariable("scriptErrorName", titleVar);
			IStringObject descVar = (IStringObject)variableRegistry.createVariable(IStringObject.TYPE_NAME);
			descVar.setValue(e.getDescription());
			variableRegistry.setVariable("scriptErrorMessage", descVar);
			return context.createResult("error.script", e); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return context.createResult("error.script", e); //$NON-NLS-1$
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return context.createResult("error.script", t); //$NON-NLS-1$
		}
		if(context.isReportingEnabled())
		{
			Dictionary<String, Object> props = new Hashtable<String, Object>();
			props.put("event", "script.after");
			context.report(IReporter.SEVERITY_INFO, "Script completed.", props);
		}
		return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
	}
}
