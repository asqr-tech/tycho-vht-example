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
 * Base interface for the command visitor hierarchy.
 * 
 * @author Lonnie Pryor
 */
public interface ICommandVisitor
{
	/**
	 * Called when an implementation-dependent visit method is not available.
	 * 
	 * @param unknownCommand The command to visit.
	 * @return An implementation-specific result.
	 * @throws NullPointerException If the supplied command is <code>null</code>.
	 */
	Object visitUnknown(ICommand unknownCommand) throws NullPointerException;
}
