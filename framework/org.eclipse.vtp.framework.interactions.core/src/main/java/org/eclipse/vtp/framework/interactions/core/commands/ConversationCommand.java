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
package org.eclipse.vtp.framework.interactions.core.commands;


import org.eclipse.vtp.framework.spi.ICommand;
import org.eclipse.vtp.framework.spi.ICommandVisitor;

/**
 * Base class for the conversational commands.
 * 
 * @author Lonnie Pryor
 */
public abstract class ConversationCommand implements ICommand
{
	/** The type of output that contains a resource path. */
	public static final int OUTPUT_TYPE_FILE = 1;
	/** The type of output that contains raw text. */
	public static final int OUTPUT_TYPE_TEXT = 2;
	/** The type of input that contains a resource path. */
	public static final int INPUT_TYPE_FILE = 1;
	/** The type of input that contains an inline grammar. */
	public static final int INPUT_TYPE_INLINE = 2;
	/** Any custom type of input. */
	public static final int INPUT_TYPE_CUSTOM = Integer.MAX_VALUE;
	
	private boolean secured = false;
	
	public boolean isSecured()
	{
		return secured;
	}
	
	public void setSecured(boolean secured)
	{
		this.secured = secured;
	}
	
	/**
	 * Invokes the implementation-specific method on the specified visitor.
	 * 
	 * @param visitor The visitor to invoke.
	 * @return The value returned by the implementation-specific method.
	 */
	abstract Object accept(IConversationCommandVisitor visitor);

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
		if (visitor instanceof IConversationCommandVisitor)
			return accept((IConversationCommandVisitor)visitor);
		return visitor.visitUnknown(this);
	}
	
}
