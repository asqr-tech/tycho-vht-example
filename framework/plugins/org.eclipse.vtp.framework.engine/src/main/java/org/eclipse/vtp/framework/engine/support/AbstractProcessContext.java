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

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.eclipse.vtp.framework.core.IContext;
import org.eclipse.vtp.framework.core.ILogger;
import org.eclipse.vtp.framework.core.IProcessContext;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.spi.IProcessDescriptor;

/**
 * A support implementation of the {@link IProcessContext} interface.
 * 
 * @author Lonnie Pryor
 */
public abstract class AbstractProcessContext extends AbstractContext implements
		IProcessContext
{
	/** The service identifiers that will return this context. */
	protected static final Set RESERVED_SERVICE_IDENTIFIERS;

	static
	{
		Set identifiers = new HashSet(4);
		identifiers.add(IReporter.class.getName());
		identifiers.add(ILogger.class.getName());
		identifiers.add(IContext.class.getName());
		identifiers.add(IProcessContext.class.getName());
		RESERVED_SERVICE_IDENTIFIERS = Collections.unmodifiableSet(identifiers);
	}

	/** The process descriptor. */
	protected final IProcessDescriptor descriptor;
	/** The service identifiers provided by the descriptor. */
	protected final Set providedServiceIdentifiers;

	/**
	 * Creates a new AbstractProcessContext.
	 * 
	 * @param descriptor The process descriptor.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	protected AbstractProcessContext(IProcessDescriptor descriptor)
			throws NullPointerException
	{
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		this.descriptor = descriptor;
		this.providedServiceIdentifiers = Collections.unmodifiableSet(new HashSet(
				Arrays.asList(descriptor.getServiceIdentifiers())));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.support.AbstractReporter#doReport( int,
	 *      java.lang.String[], java.lang.String, java.util.Dictionary)
	 */
	protected void doReport(int severity, String[] categories, String message,
			Dictionary properties)
	{
		if (properties == null)
			properties = new Hashtable();
		if (properties.get("scope") == null) //$NON-NLS-1$
			properties.put("scope", "process"); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("process.id", getProcessID()); //$NON-NLS-1$
		descriptor.report(severity, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#isSeverityEnabled(int)
	 */
	public boolean isSeverityEnabled(int severity)
	{
		return descriptor.isSeverityEnabled(severity);
	}
	
	public boolean isReportingEnabled()
	{
		return descriptor.isReportingEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.support.AbstractServiceRegistry#
	 *      lookupReservedService(java.lang.String)
	 */
	protected Object lookupReservedService(String identifier)
	{
		if (RESERVED_SERVICE_IDENTIFIERS.contains(identifier))
			return this;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.support.AbstractServiceRegistry#
	 *      lookupLocalService(java.lang.String)
	 */
	protected Object lookupLocalService(String identifier)
	{
		if (providedServiceIdentifiers.contains(identifier))
			return descriptor.getService(identifier);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.support.AbstractServiceRegistry#
	 *      lookupAllLocalServices(java.lang.String)
	 */
	protected Object[] lookupAllLocalServices(String identifier)
	{
		if (providedServiceIdentifiers.contains(identifier))
			return new Object[] { descriptor.getService(identifier) };
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProcessID()
	 */
	public final String getProcessID()
	{
		return descriptor.getProcessID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProperty(
	 *      java.lang.String)
	 */
	public final Object getProperty(String propertyName)
			throws NullPointerException
	{
		return descriptor.getProperty(propertyName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#loadClass(
	 *      java.lang.String)
	 */
	public final Class loadClass(String className) throws ClassNotFoundException,
			NullPointerException
	{
		return descriptor.loadClass(className);
	}
}
