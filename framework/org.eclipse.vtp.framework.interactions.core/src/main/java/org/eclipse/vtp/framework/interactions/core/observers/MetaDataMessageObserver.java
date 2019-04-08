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
package org.eclipse.vtp.framework.interactions.core.observers;

import org.eclipse.vtp.framework.core.ILogger;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;

/**
 * An action that enqueues a meta-data message.
 * 
 * @author Lonnie Pryor
 */
public class MetaDataMessageObserver implements Runnable
{
	/** The context to use. */
	private final ILogger logger;
	/** The conversation to use. */
	private final IConversation conversation;
	/** The configuration to use. */
	private final MetaDataConfiguration configuration;

	/**
	 * Creates a new MetaDataMessageAction.
	 * 
	 * @param logger The logger to use.
	 * @param conversation The conversation to use.
	 * @param configuration The configuration to use.
	 */
	public MetaDataMessageObserver(ILogger logger, IConversation conversation,
			MetaDataConfiguration configuration)
	{
		this.logger = logger;
		this.conversation = conversation;
		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		logger.debug(getClass().getName().substring(
				getClass().getName().lastIndexOf('.') + 1));
		configuration.setIgnoreErrors(true);
		conversation.createMetaDataMessage(configuration, "MetaDataMessageObserver").enqueue();
	}
}
