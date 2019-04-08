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

import org.eclipse.vtp.framework.spi.ICommand;
import org.eclipse.vtp.framework.spi.ICommandVisitor;

/**
 * Base class for forward, include, and exit commands.
 * 
 * @author Lonnie Pryor
 */
public abstract class ControllerCommand implements ICommand
{
	/**
	 * Invokes the implementation-specific method on the specified visitor.
	 * 
	 * @param visitor The visitor to invoke.
	 * @return The value returned by the implementation-specific method.
	 */
	abstract Object accept(IControllerCommandVisitor visitor);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#accept(
	 *      org.eclipse.vtp.framework.spi.ICommandVisitor)
	 */
	public final Object accept(ICommandVisitor visitor) throws NullPointerException
	{
		if (visitor == null)
			throw new NullPointerException("visitor"); //$NON-NLS-1$
		if (visitor instanceof IControllerCommandVisitor)
			return accept((IControllerCommandVisitor)visitor);
		return visitor.visitUnknown(this);
	}
}
