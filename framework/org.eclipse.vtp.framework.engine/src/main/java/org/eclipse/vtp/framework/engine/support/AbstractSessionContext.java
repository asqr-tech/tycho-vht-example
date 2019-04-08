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

import org.eclipse.vtp.framework.core.IProcessContext;
import org.eclipse.vtp.framework.core.ISessionContext;
import org.eclipse.vtp.framework.spi.ISessionDescriptor;

/**
 * A support implementation of the {@link ISessionContext} interface.
 * 
 * @author Lonnie Pryor
 */
public abstract class AbstractSessionContext extends AbstractContext implements
		ISessionContext
{
	/** The service identifiers that will return this context. */
	protected static final Set RESERVED_SERVICE_IDENTIFIERS;

	static
	{
		Set identifiers = new HashSet(
				AbstractProcessContext.RESERVED_SERVICE_IDENTIFIERS.size() + 1);
		identifiers.addAll(AbstractProcessContext.RESERVED_SERVICE_IDENTIFIERS);
		identifiers.add(ISessionContext.class.getName());
		RESERVED_SERVICE_IDENTIFIERS = Collections.unmodifiableSet(identifiers);
	}

	/** The context of the process that created this session. */
	protected final IProcessContext processContext;
	/** The session descriptor. */
	protected final ISessionDescriptor descriptor;
	/** The service identifiers provided by the descriptor. */
	protected final Set providedServiceIdentifiers;

	/**
	 * Creates a new AbstractSessionContext.
	 * 
	 * @param processContext The context of the process that created this session.
	 * @param descriptor The session descriptor.
	 * @throws NullPointerException If the supplied process context is
	 *           <code>null</code>.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 */
	protected AbstractSessionContext(IProcessContext processContext,
			ISessionDescriptor descriptor) throws NullPointerException
	{
		if (processContext == null)
			throw new NullPointerException("processContext"); //$NON-NLS-1$
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		this.processContext = processContext;
		this.descriptor = descriptor;
		this.providedServiceIdentifiers = Collections.unmodifiableSet(new HashSet(
				Arrays.asList(descriptor.getServiceIdentifiers())));
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.support.AbstractReporter#doReport(
	 *      int, java.lang.String[], java.lang.String, java.util.Dictionary)
	 */
	protected void doReport(int severity, String[] categories, String message,
			Dictionary properties)
	{
		if (properties == null)
			properties = new Hashtable();
		if (properties.get("scope") == null) //$NON-NLS-1$
			properties.put("scope", "session"); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("session.id", getSessionID()); //$NON-NLS-1$
		Object value = getAttribute("span.dialog");
		if (value != null)
			properties.put("span.dialog", value);
		value = getAttribute("span.dialog.name");
		if (value != null)
			properties.put("span.dialog.name", value);
		value = getAttribute("span.primitive");
		if (value != null)
			properties.put("span.primitive", value);
		value = getAttribute("span.primitive.name");
		if (value != null)
			properties.put("span.primitive.name", value);
		processContext.report(severity, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#isSeverityEnabled(int)
	 */
	public boolean isSeverityEnabled(int severity)
	{
		return processContext.isSeverityEnabled(severity);
	}
	
	public boolean isReportingEnabled()
	{
		return processContext.isReportingEnabled();
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
		return processContext.lookup(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.support.AbstractServiceRegistry#
	 *      lookupAllInheritedServices(java.lang.String)
	 */
	protected Object[] lookupAllInheritedServices(String identifier)
	{
		return processContext.lookupAll(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProcessID()
	 */
	public final String getProcessID()
	{
		return processContext.getProcessID();
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
		return processContext.getProperty(propertyName);
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
		return processContext.loadClass(className);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getSessionID()
	 */
	public final String getSessionID()
	{
		return descriptor.getSessionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getSessionStartTime()
	 */
	public final Date getSessionStartTime()
	{
		return descriptor.getSessionStartTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttributeNames()
	 */
	public final String[] getAttributeNames()
	{
		return descriptor.getAttributeNames();
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
		return descriptor.getAttribute(attributeName);
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
		descriptor.setAttribute(attributeName, attributeValue);
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
		descriptor.clearAttribute(attributeName);
	}
	
	public final Object getInheritedAttribute(String attributeName) throws NullPointerException
	{
		return descriptor.getInheritedAttribute(attributeName);
	}

	public final String[] getRootAttributeNames()
	{
		return descriptor.getRootAttributeNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttribute(
	 *      java.lang.String)
	 */
	public final Object getRootAttribute(String attributeName)
			throws NullPointerException
	{
		return descriptor.getRootAttribute(attributeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#setAttribute(
	 *      java.lang.String, java.lang.Object)
	 */
	public final void setRootAttribute(String attributeName, Object attributeValue)
			throws NullPointerException
	{
		descriptor.setRootAttribute(attributeName, attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#clearAttribute(
	 *      java.lang.String)
	 */
	public final void clearRootAttribute(String attributeName)
			throws NullPointerException
	{
		descriptor.clearRootAttribute(attributeName);
	}
}
