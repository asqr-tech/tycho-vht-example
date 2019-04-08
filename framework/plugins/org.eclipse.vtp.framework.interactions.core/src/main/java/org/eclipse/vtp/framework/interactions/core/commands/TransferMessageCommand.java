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

/**
 * A command that sends a transfer ending to the user.
 * 
 * @author Lonnie Pryor
 */
public final class TransferMessageCommand extends ConversationCommand
{
	/** The destination to transfer to. */
	private String destination = null;

	/**
	 * Creates a new TransferMessageCommand.
	 */
	public TransferMessageCommand()
	{
	}

	/**
	 * Returns the destination to transfer to.
	 * 
	 * @return The destination to transfer to.
	 */
	public String getDestination()
	{
		return destination;
	}

	/**
	 * Sets the destination to transfer to.
	 * 
	 * @param destination The destination to transfer to.
	 */
	public void setDestination(String destination)
	{
		this.destination = destination;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.commands.
	 *      ConversationCommand#accept(
	 *      org.eclipse.vtp.framework.interactions.core.commands.
	 *      IConversationCommandVisitor)
	 */
	Object accept(IConversationCommandVisitor visitor)
	{
		return visitor.visitTransferMessage(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#exportContents()
	 */
	public Object exportContents()
	{
		return destination;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#importContents(
	 *      java.lang.Object)
	 */
	public void importContents(Object contents)
	{
		destination = (String)contents;
	}
}
