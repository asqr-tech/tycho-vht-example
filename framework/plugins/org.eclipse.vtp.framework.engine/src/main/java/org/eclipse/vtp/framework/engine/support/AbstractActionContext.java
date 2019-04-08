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

import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IExecutionContext;

/**
 * A support implementation of the {@link IActionContext} interface.
 * 
 * @author Lonnie Pryor
 */
public abstract class AbstractActionContext extends AbstractContext implements
		IActionContext
{
	/** The service identifiers that will return this context. */
	protected static final Set RESERVED_SERVICE_IDENTIFIERS;

	static
	{
		Set identifiers = new HashSet(
				AbstractExecutionContext.RESERVED_SERVICE_IDENTIFIERS.size() + 1);
		identifiers.addAll(AbstractExecutionContext.RESERVED_SERVICE_IDENTIFIERS);
		identifiers.add(IActionContext.class.getName());
		RESERVED_SERVICE_IDENTIFIERS = Collections.unmodifiableSet(identifiers);
	}

	/** The context of the execution that created this action. */
	protected final IExecutionContext executionContext;

	/**
	 * Creates a new AbstractActionContext.
	 * 
	 * @param executionContext The context of the execution that created this
	 *          action.
	 * @throws NullPointerException If the supplied session context is
	 *           <code>null</code>.
	 */
	protected AbstractActionContext(IExecutionContext executionContext)
			throws NullPointerException
	{
		if (executionContext == null)
			throw new NullPointerException("executionContext"); //$NON-NLS-1$
		this.executionContext = executionContext;
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
			properties.put("scope", "action"); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("action.id", getActionID()); //$NON-NLS-1$
		properties.put("action.name", getActionName()); //$NON-NLS-1$
		executionContext.report(severity, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#isSeverityEnabled(int)
	 */
	public boolean isSeverityEnabled(int severity)
	{
		return executionContext.isSeverityEnabled(severity);
	}
	
	public boolean isReportingEnabled()
	{
		return executionContext.isReportingEnabled();
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
	 *      lookupAllLocalServices(java.lang.String)
	 */
	protected Object lookupInheritedService(String identifier)
	{
		return executionContext.lookup(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.support.AbstractServiceRegistry#
	 *      lookupAllInheritedServices(java.lang.String)
	 */
	protected Object[] lookupAllInheritedServices(String identifier)
	{
		return executionContext.lookupAll(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProcessID()
	 */
	public final String getProcessID()
	{
		return executionContext.getProcessID();
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
		return executionContext.getProperty(propertyName);
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
		return executionContext.loadClass(className);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getSessionID()
	 */
	public final String getSessionID()
	{
		return executionContext.getSessionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getSessionStartTime()
	 */
	public final Date getSessionStartTime()
	{
		return executionContext.getSessionStartTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttributeNames()
	 */
	public final String[] getAttributeNames()
	{
		return executionContext.getAttributeNames();
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
		return executionContext.getAttribute(attributeName);
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
		executionContext.setAttribute(attributeName, attributeValue);
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
		executionContext.clearAttribute(attributeName);
	}

	public final Object getInheritedAttribute(String attributeName)
			throws NullPointerException
	{
		return executionContext.getInheritedAttribute(attributeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getExecutionID()
	 */
	public final String getExecutionID()
	{
		return executionContext.getExecutionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getParameterNames()
	 */
	public final String[] getParameterNames()
	{
		return executionContext.getParameterNames();
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
		return executionContext.getParameter(parameterName);
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
		return executionContext.getParameters(parameterName);
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
		executionContext.setParameter(parameterName, value);
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
		executionContext.setParameters(parameterName, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#clearParameter(
	 *      java.lang.String)
	 */
	public void clearParameter(String parameterName) throws NullPointerException
	{
		executionContext.clearParameter(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IActionContext#createResult(
	 *      java.lang.String)
	 */
	public final IActionResult createResult(String resultName)
	{
		return createResult(resultName, null);
	}

	public String[] getRootAttributeNames()
	{
		return executionContext.getRootAttributeNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttribute(
	 *      java.lang.String)
	 */
	public Object getRootAttribute(String attributeName) throws NullPointerException
	{
		return executionContext.getRootAttribute(attributeName);
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
		executionContext.setRootAttribute(attributeName, attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#clearAttribute(
	 *      java.lang.String)
	 */
	public void clearRootAttribute(String attributeName) throws NullPointerException
	{
		executionContext.clearRootAttribute(attributeName);
	}
}
