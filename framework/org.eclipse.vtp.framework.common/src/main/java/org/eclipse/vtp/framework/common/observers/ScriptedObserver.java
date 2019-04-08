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
package org.eclipse.vtp.framework.common.observers;

import org.eclipse.vtp.framework.common.IScriptingEngine;
import org.eclipse.vtp.framework.common.IScriptingService;
import org.eclipse.vtp.framework.common.configurations.ScriptConfiguration;

/**
 * An observer that runs a configured script.
 * 
 * @author Lonnie Pryor
 */
public class ScriptedObserver implements Runnable
{
	/** The scripting engine to execute the script with. */
	private final IScriptingEngine engine;
	/** The configuration for this scripted action. */
	private final String script;

	/**
	 * Creates a new ScriptedAction.
	 * 
	 * @param scriptingService The scripting service to create an engine with.
	 * @param configuration The configuration for this scripted action.
	 */
	public ScriptedObserver(IScriptingService scriptingService,
			ScriptConfiguration configuration)
	{
		this.engine = scriptingService.createScriptingEngine(configuration
				.getScriptingLanguage());
		this.script = configuration.getScript();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		engine.execute(script);
	}
}
