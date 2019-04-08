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
package org.eclipse.vtp.framework.common.commands;

import org.eclipse.vtp.framework.spi.ICommandVisitor;

/**
 * A visitor capable of handling forward, include, and exit commands.
 * 
 * @author Lonnie Pryor
 */
public interface IControllerCommandVisitor extends ICommandVisitor
{
	/**
	 * Called when this visitor is passed to a forward command instance.
	 * 
	 * @param forwardCommand The forward command this visitor was passed to.
	 * @return An implementation-specific result.
	 */
	Object visitForward(ForwardCommand forwardCommand);

	/**
	 * Called when this visitor is passed to an include command instance.
	 * 
	 * @param includeCommand The include command this visitor was passed to.
	 * @return An implementation-specific result.
	 */
	Object visitInclude(IncludeCommand includeCommand);

	/**
	 * Called when this visitor is passed to an exit command instance.
	 * 
	 * @param exitCommand The exit command this visitor was passed to.
	 * @return An implementation-specific result.
	 */
	Object visitExit(ExitCommand exitCommand);
}
