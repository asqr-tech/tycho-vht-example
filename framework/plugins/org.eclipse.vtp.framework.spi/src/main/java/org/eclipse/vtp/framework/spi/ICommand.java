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
 * A command that can be enqueued with the external process controller.
 * 
 * @author Lonnie Pryor
 */
public interface ICommand
{
	/**
	 * Attempts to invoke an implementation-dependent visit method on the supplied
	 * visitor, calling {@link ICommandVisitor#visitUnknown(ICommand)} if such a
	 * method is not available.
	 * 
	 * @param visitor The visitor to accept.
	 * @return The value returned by the visitor's visit method.
	 * @throws NullPointerException If the supplied visitor is <code>null</code>.
	 */
	Object accept(ICommandVisitor visitor) throws NullPointerException;

	/**
	 * Exports the contents of this command to a simple structure of arrays and
	 * serializable values from <code>java.lang</code>.
	 * 
	 * @return A serializable structure this command can be re-constituted from.
	 */
	Object exportContents();

	/**
	 * Configures the contents of this command with a structure previously
	 * returned from {@link #exportContents()}.
	 * 
	 * @param contents The exported contents structure to load from.
	 */
	void importContents(Object contents);
}
