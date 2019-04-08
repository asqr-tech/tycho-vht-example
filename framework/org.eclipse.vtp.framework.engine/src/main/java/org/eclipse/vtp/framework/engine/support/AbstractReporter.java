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
package org.eclipse.vtp.framework.engine.support;

import java.util.Dictionary;

import org.eclipse.vtp.framework.core.IReporter;

/**
 * A support implementation of the {@link IReporter} interface.
 * 
 * @author Lonnie Pryor
 */
public abstract class AbstractReporter implements IReporter
{
	/**
	 * Creates a new AbstractReporter.
	 */
	protected AbstractReporter()
	{
	}

	/**
	 * Implementation of report creation and publication after checking the
	 * severity.
	 * 
	 * @param severity The severity of the report.
	 * @param categories The categories the report pertains to or
	 *          <code>null</code> if no catagories are related.
	 * @param message The message associated with the report or <code>null</code>
	 *          to not include a message.
	 * @param properties The properties of the report or <code>null</code> if no
	 *          properties are specified.
	 */
	protected abstract void doReport(int severity, String[] categories,
			String message, Dictionary properties);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IReporter#report(int, java.lang.String)
	 */
	public final void report(int severity, String message)
	{
		if (isSeverityEnabled(severity))
			doReport(severity, null, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IReporter#report(int, java.lang.String,
	 *      java.util.Dictionary)
	 */
	public final void report(int severity, String message, Dictionary properties)
	{
		if (isSeverityEnabled(severity))
			doReport(severity, null, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IReporter#report(int,
	 *      java.lang.String[], java.lang.String)
	 */
	public final void report(int severity, String[] categories, String message)
	{
		if (isSeverityEnabled(severity))
			doReport(severity, categories, message, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IReporter#report(int,
	 *      java.lang.String[], java.lang.String, java.util.Dictionary)
	 */
	public final void report(int severity, String[] categories, String message,
			Dictionary properties)
	{
		if (isSeverityEnabled(severity))
			doReport(severity, categories, message, properties);
	}
}
