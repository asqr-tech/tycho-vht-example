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
package org.eclipse.vtp.desktop.editors.themes.core.commands;

/**
 * This interface represents an agent capable of performing the necessary work
 * associated with all <code>Command</code> objects.
 * 
 * @author trip
 */
public interface CommandListener
{
	/**
	 * Executed when a command is to be performed by this command listener.
	 * 
	 * @param command The command to perform
	 */
	public void executeCommand(Command command);
}
