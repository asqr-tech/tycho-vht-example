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
package org.eclipse.vtp.framework.common.controller;

import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.eclipse.vtp.framework.common.configurations.DispatchConfiguration;
import org.eclipse.vtp.framework.common.configurations.ExitConfiguration;

/**
 * An service that can be used by components in the process engine to influence
 * the actions of the external process controller.
 * 
 * @author Lonnie Pryor
 */
public interface IController
{
	/**
	 * Creates a forward dispatcher for the specified configuration.
	 * 
	 * @param configuration The configuration to apply to the dispatcher.
	 * @return A new dispatcher for the specified configuration.
	 */
	IForwardDispatcher createForward(DispatchConfiguration configuration);

	/**
	 * Creates an include dispatcher for the specified configuration.
	 * 
	 * @param configuration The configuration to apply to the dispatcher.
	 * @return A new dispatcher for the specified configuration.
	 */
	IIncludeDispatcher createInclude(DispatchConfiguration configuration);

	/**
	 * Creates an exit dispatcher for the specified configuration.
	 * 
	 * @param configuration The configuration to apply to the dispatcher.
	 * @param assignments TODO
	 * @return A new dispatcher for the specified configuration.
	 */
	IExitDispatcher createExit(ExitConfiguration configuration, AssignmentConfiguration[] assignments);
}
