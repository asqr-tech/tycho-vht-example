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
package org.eclipse.vtp.framework.spi;

/**
 * A visitor for the base runnable command.
 * 
 * @author Lonnie Pryor
 */
public interface IRunnableCommandVisitor extends ICommandVisitor
{
	/**
	 * Called when passed to a runnable command.
	 * 
	 * @param runnableCommand The command to visit.
	 * @return An implementation-specific result.
	 * @throws NullPointerException If the supplied command is <code>null</code>.
	 */
	Object visitRunnable(IRunnableCommand runnableCommand) throws NullPointerException;
}
