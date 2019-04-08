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

/**
 * An object capable of re-directing the flow of the process engine.
 * 
 * @author Lonnie Pryor
 */
public interface IDispatcher
{
	/**
	 * Enqueues this dispatcher for evaluation by the process controller.
	 * 
	 * @return True if this dispatcher was accepted into the queue.
	 */
	boolean enqueue();

	/**
	 * Enqueues this dispatcher and waits for it to be processed.
	 * 
	 * @return True if this dispatcher was accepted into the queue and processed.
	 * @throws IllegalStateException If the process controller is not in the
	 *           blocking state.
	 */
	boolean process() throws IllegalStateException;
}
