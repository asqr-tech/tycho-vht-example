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
package org.eclipse.vtp.framework.interactions.core.conversation;

/**
 * Base type for conversational interactions.
 *
 * @author Lonnie Pryor
 */
public interface IInteraction
{
	/**
	 * Enqueues this interaction for evaluation by the process controller.
	 * 
	 * @return True if this interaction was accepted into the queue.
	 */
	boolean enqueue();

	/**
	 * Enqueues this interaction and waits for it to be processed.
	 * 
	 * @return True if this interaction was accepted into the queue and processed.
	 * @throws IllegalStateException If the process controller is not in the
	 *           blocking state.
	 */
	boolean process() throws IllegalStateException;
}
