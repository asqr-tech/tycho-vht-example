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
import java.util.Date;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.core.ISessionContext;
import org.eclipse.vtp.framework.spi.IExecutionDescriptor;

/**
 * A support implementation of the {@link IExecutionContext} interface.
 * 
 * @author Lonnie Pryor
 */
public abstract class AbstractExecutionContext extends AbstractContext
		implements IExecutionContext
{
	/** The service identifiers that will return this context. */
	protected static final Set RESERVED_SERVICE_IDENTIFIERS;

	static
	{
		Set identifiers = new HashSet(
				AbstractSessionContext.RESERVED_SERVICE_IDENTIFIERS.size() + 1);
		identifiers.addAll(AbstractSessionContext.RESERVED_SERVICE_IDENTIFIERS);
		identifiers.add(IExecutionContext.class.getName());
		RESERVED_SERVICE_IDENTIFIERS = Collections.unmodifiableSet(identifiers);
	}

	/** The context of the session that created this execution. */
	protected final ISessionContext sessionContext;
	/** The execution descriptor. */
	protected final IExecutionDescriptor descriptor;
	/** The service identifiers provided by the descriptor. */
	protected final Set providedServiceIdentifiers;

	/**
	 * Creates a new AbstractExecutionContext.
	 * 
	 * @param sessionContext The context of the session that created this
	 *          execution.
	 * @param descriptor The execution descriptor.
	 * @throws NullPointerException If the supplied session context is
	 *           <code>null</code>.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	protected AbstractExecutionContext(ISessionContext sessionContext,
			IExecutionDescriptor descriptor) throws NullPointerException
	{
		if (sessionContext == null)
			throw new NullPointerException("sessionContext"); //$NON-NLS-1$
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		this.sessionContext = sessionContext;
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
			properties.put("scope", "execution"); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("execution.id", getExecutionID()); //$NON-NLS-1$
		sessionContext.report(severity, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#isSeverityEnabled(int)
	 */
	public boolean isSeverityEnabled(int severity)
	{
		return sessionContext.isSeverityEnabled(severity);
	}
	
	public boolean isReportingEnabled()
	{
		return sessionContext.isReportingEnabled();
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
	 * @see org.eclipse.vtp.framework.spi.support.AbstractServiceRegistry#
	 *      lookupAllLocalServices(java.lang.String)
	 */
	protected Object lookupInheritedService(String identifier)
	{
		return sessionContext.lookup(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.support.AbstractServiceRegistry#
	 *      lookupAllInheritedServices(java.lang.String)
	 */
	protected Object[] lookupAllInheritedServices(String identifier)
	{
		return sessionContext.lookupAll(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProcessID()
	 */
	public final String getProcessID()
	{
		return sessionContext.getProcessID();
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
		return sessionContext.getProperty(propertyName);
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
		return sessionContext.loadClass(className);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getSessionID()
	 */
	public final String getSessionID()
	{
		return sessionContext.getSessionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getSessionStartTime()
	 */
	public final Date getSessionStartTime()
	{
		return sessionContext.getSessionStartTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttributeNames()
	 */
	public final String[] getAttributeNames()
	{
		return sessionContext.getAttributeNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttribute(
	 *      java.lang.String)
	 */
	public final Object getAttribute(String attributeName)
			throws NullPointerException
	{
		return sessionContext.getAttribute(attributeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#setAttribute(
	 *      java.lang.String, java.lang.Object)
	 */
	public final void setAttribute(String attributeName, Object attributeValue)
			throws NullPointerException
	{
		sessionContext.setAttribute(attributeName, attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#clearAttribute(
	 *      java.lang.String)
	 */
	public final void clearAttribute(String attributeName)
			throws NullPointerException
	{
		sessionContext.clearAttribute(attributeName);
	}
	
	public final Object getInheritedAttribute(String attributeName)
		throws NullPointerException
	{
		return sessionContext.getInheritedAttribute(attributeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getExecutionID()
	 */
	public final String getExecutionID()
	{
		return descriptor.getExecutionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getParameterNames()
	 */
	public final String[] getParameterNames()
	{
		return descriptor.getParameterNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getParameter(
	 *      java.lang.String)
	 */
	public final String getParameter(String parameterName)
			throws NullPointerException
	{
		return descriptor.getParameter(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getParameters(
	 *      java.lang.String)
	 */
	public final String[] getParameters(String parameterName)
			throws NullPointerException
	{
		return descriptor.getParameters(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#setParameter(
	 *      java.lang.String, java.lang.String)
	 */
	public void setParameter(String parameterName, String value)
			throws NullPointerException
	{
		descriptor.setParameter(parameterName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#setParameters(
	 *      java.lang.String, java.lang.String[])
	 */
	public void setParameters(String parameterName, String[] values)
			throws NullPointerException
	{
		descriptor.setParameters(parameterName, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#clearParameter(
	 *      java.lang.String)
	 */
	public void clearParameter(String parameterName) throws NullPointerException
	{
		descriptor.clearParameter(parameterName);
	}

	public String[] getRootAttributeNames()
	{
		return sessionContext.getRootAttributeNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttribute(
	 *      java.lang.String)
	 */
	public Object getRootAttribute(String attributeName) throws NullPointerException
	{
		return sessionContext.getRootAttribute(attributeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#setAttribute(
	 *      java.lang.String, java.lang.Object)
	 */
	public void setRootAttribute(String attributeName, Object attributeValue)
			throws NullPointerException
	{
		sessionContext.setRootAttribute(attributeName, attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#clearAttribute(
	 *      java.lang.String)
	 */
	public void clearRootAttribute(String attributeName) throws NullPointerException
	{
		sessionContext.clearRootAttribute(attributeName);
	}
}
