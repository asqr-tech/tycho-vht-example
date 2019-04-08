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
 * A service provided by the process controller that accepts commands issued
 * from components of the process.
 * 
 * @author Lonnie Pryor
 */
public interface ICommandProcessor
{
	/**
	 * Attempts to enqueue the specified command for execution by the process
	 * controller.
	 * 
	 * @param command The command to enqueue.
	 * @return True if the command was accepted by this queue.
	 */
	boolean enqueue(ICommand command);

	/**
	 * Attempts to enqueue the specified command for execution by the process
	 * controller and then waits for all commands in the queue to be processed
	 * before returning.
	 * 
	 * @param command The command to enqueue.
	 * @return True if the command was accepted and processed by this queue.
	 * @throws IllegalStateException If the process controller is not in the
	 *           blocking state.
	 */
	boolean process(ICommand command) throws IllegalStateException;
}
