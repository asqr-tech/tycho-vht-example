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
package org.eclipse.vtp.framework.engine.http;

import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.vtp.framework.common.IArrayObject;
import org.eclipse.vtp.framework.common.IBooleanObject;
import org.eclipse.vtp.framework.common.IBrand;
import org.eclipse.vtp.framework.common.IBrandRegistry;
import org.eclipse.vtp.framework.common.IBrandSelection;
import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IDateObject;
import org.eclipse.vtp.framework.common.IDecimalObject;
import org.eclipse.vtp.framework.common.IMapObject;
import org.eclipse.vtp.framework.common.INumberObject;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.engine.runtime.Executable;
import org.eclipse.vtp.framework.engine.runtime.Session;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.spi.IProcess;
import org.eclipse.vtp.framework.spi.ISession;
import org.eclipse.vtp.framework.spi.ISessionDescriptor;

/**
 * An active session on the HTTP connector.
 * 
 * @author Lonnie Pryor
 */
public class DeploymentSession implements ISessionDescriptor
{
	/** The name of the session attribute the execution ID is stored in. */
	private static final String EXECUTION_ID = "execution.id";
	private static final String ROOT_CONTEXT = "root.session.";


	private static void assignVariables(IVariableRegistry variableRegistry, Map variables, boolean copy)
	{
//		System.out.println("Assigning Variables");
		for (Iterator i = variables.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
//			System.out.println("Variable: " + entry.getKey());
			IDataObject variable = importVariable(variableRegistry, (Map)entry
					.getValue(), copy);
			if (variable != null)
				variableRegistry.setVariable((String)entry.getKey(), variable);
		}
	}

	private static IDataObject importVariable(IVariableRegistry variableRegistry,
			Map<String, Object> data, boolean copy)
	{
//		System.out.println("Importing Variable");
		if (data == null)
			return null;
//		for(Map.Entry<String, Object> entry : data.entrySet())
//		{
//			System.out.println("Key: " + entry.getKey() + " Value: [" + entry.getValue().getClass().getName() + "] " + entry.getValue().toString());
//		}
		String typeName = (String)data.get(null);
		if (typeName == null)
			return null;
		String objectId = (String)data.get("OBJECT_ID");
		IDataObject variable = variableRegistry.createVariable(typeName, objectId);
		if (variable == null)
			return null;
		if(copy)
		{
			if (variable instanceof IArrayObject)
			{
				IArrayObject array = (IArrayObject)variable;
				Object[] elements = (Object[])data.get("elements");
				for (int i = 0; i < elements.length; ++i)
					array.addElement(importVariable(variableRegistry, (Map)elements[i], true));
			}
			else if (variable instanceof IMapObject)
			{
				IMapObject map = (IMapObject)variable;
				Iterator i = data.keySet().iterator();
				while(i.hasNext())
				{
					String key = (String)i.next();
					if(key != null && !key.equals("OBJECT_ID"))
					{
						map.setField(key, importVariable(variableRegistry, (Map)data.get(key), true));
					}
				}
			}
			else if (variable instanceof IBooleanObject)
				((IBooleanObject)variable).setValue(data.get("value"));
			else if (variable instanceof IDateObject)
				((IDateObject)variable).setValue(data.get("value"));
			else if (variable instanceof IDecimalObject)
				((IDecimalObject)variable).setValue(data.get("value"));
			else if (variable instanceof INumberObject)
				((INumberObject)variable).setValue(data.get("value"));
			else if (variable instanceof IStringObject)
				((IStringObject)variable).setValue(data.get("value"));
			else
			{
				String[] fields = variable.getType().getFieldNames();
				for (int i = 0; i < fields.length; ++i)
					variable.setField(fields[i], importVariable(variableRegistry, (Map)data
							.get(fields[i]), copy));
			}
		}
		return variable;
	}
	
	/** The session ID. */
	private final String id;
	/** The process session. */
	private final ISession session;
	/** The HTTP session. */
	private HttpSession httpSession = null;
	/** Comment for qualifier. */
	private String[] qualifier = null;
	/** The depth of this deployment session */
	private int depth = 0;
	/** The start time of the session */
	private Date startTime;
	private long timeout = Long.MAX_VALUE;
	private long lastAccessed = System.currentTimeMillis();
	private final boolean[] editLock = { false };

	/**
	 * Creates a new HttpDeploymentSession.
	 * 
	 * @param id The session ID.
	 * @param process The process to create a session of.
	 */
	DeploymentSession(String id, IProcess process)
	{
		this.id = id;
		this.session = process.createSession(this);
	}
	
	public String getCurrentPosition() {
		Executable executable = ((Session)session).process.blueprint.getExecutable((String)getAttribute("engine.sequence.position"));
		if (executable == null)
			return null;
		return executable.getActionInstance().getName();
	}
	
	public IVariableRegistry getVariableRegistry() {
		return (IVariableRegistry)session.lookupService(IVariableRegistry.class.getName());
	}
	
	public void lock () {
		while (true) {
			try {
				synchronized (editLock) {
					if (editLock[0])
						editLock.wait(1000 * 60);
					if (editLock[0])
						throw new RuntimeException("Timed out trying to lock session.");
					editLock[0] = true;
					return;
				}
			} catch (InterruptedException e) {
				
			}
		}
	}
	
	public void unlock () {
		synchronized (editLock) {
			editLock[0] = false;
			editLock.notifyAll();
		}
	}

	/**
	 * Returns the session.
	 * 
	 * @return The session.
	 */
	public ISession getSession()
	{
		return session;
	}

	/**
	 * Preforms the first step in this session.
	 * 
	 * @param httpSession The HTTP session.
	 * @param httpRequest The HTTP request.
	 * @param httpResponse The HTTP response.
	 * @return The next document to render.
	 */
	public ResultDocument start(HttpSession httpSession,
			HttpServletRequest httpRequest, HttpServletResponse httpReesponse,
			String prefix, int depth, Map variableValues, Map parameterValues, String entryName, String brand, boolean subdialog)
	{
		this.httpSession = httpSession;
		this.timeout = (long)httpSession.getMaxInactiveInterval() * 1000L;
		this.lastAccessed = httpSession.getLastAccessedTime();
		this.depth = depth;
		this.qualifier = new String[depth + 1];
		for(int i = 0; i <= depth; i++)
		{
			qualifier[i] = prefix + i + ".";
		}
		assignVariables((IVariableRegistry)session
			.lookupService(IVariableRegistry.class.getName()), variableValues, false);
		IBrandRegistry brands = (IBrandRegistry)session
			.lookupService(IBrandRegistry.class.getName());
		IBrand selectedBrand = brands.getBrand(brand);
		if (selectedBrand != null)
			((IBrandSelection)session
				.lookupService(IBrandSelection.class.getName()))
				.setSelectedBrand(selectedBrand);
		else
			selectedBrand = brands.getDefaultBrand();
		IVariableRegistry variables = (IVariableRegistry)session
			.lookupService(IVariableRegistry.class.getName());
		IDataObject platform = variables.createVariable("Platform");
		((IStringObject)platform.getField("Brand")).setValue(selectedBrand
			.getName());
		if (httpRequest.getParameter("PLATFORM_ANI") != null)
		{
			((IStringObject)platform.getField("PLATFORM_ANI")).setValue(httpRequest
				.getParameter("PLATFORM_ANI"));
			((IStringObject)platform.getField("ANI")).setValue(httpRequest
				.getParameter("PLATFORM_ANI"));
		}
		if (httpRequest.getParameter("PLATFORM_DNIS") != null)
		{
			((IStringObject)platform.getField("PLATFORM_DNIS")).setValue(httpRequest
				.getParameter("PLATFORM_DNIS"));
			((IStringObject)platform.getField("DNIS")).setValue(httpRequest
				.getParameter("PLATFORM_DNIS"));
		}
		variables.setVariable("Platform", platform);
		IReporter reporter = (IReporter)session.lookupService(IReporter.class.getName());
		if(reporter.isReportingEnabled())
		{
			Dictionary report = new Hashtable();
			report.put("event", "session.created");
			reporter.report(IReporter.SEVERITY_INFO,
							"Session \"" + id + "\" created.",
							report);
		}
		startTime = (Date)getAttribute("session.starttime");
		if(startTime == null)
		{
			startTime = new Date();
			setAttribute("session.starttime", startTime);
		}
		setAttribute("engine.sequence.entry", entryName);
		setAttribute("subdialog", subdialog ? "true" : "false");
		return new DeploymentExecution(getNextExecutionID(), this, httpRequest,
			httpReesponse, parameterValues).doNext();
	}

	/**
	 * Preforms the next step in this session.
	 * 
	 * @param httpSession The HTTP session.
	 * @param httpRequest The HTTP request.
	 * @param httpResponse The HTTP response.
	 * @return The next document to render.
	 */
	public ResultDocument next(HttpSession httpSession,
			HttpServletRequest httpRequest, HttpServletResponse httpReesponse,
			String prefix, int depth, Map variableValues, Map parameterValues)
	{
		this.httpSession = httpSession;
		this.timeout = httpSession.getMaxInactiveInterval() * 1000L;
		this.lastAccessed = httpSession.getLastAccessedTime();
		this.depth = depth;
		this.qualifier = new String[depth + 1];
		for(int i = 0; i <= depth; i++)
		{
			qualifier[i] = prefix + i + ".";
		}
		assignVariables((IVariableRegistry)session
				.lookupService(IVariableRegistry.class.getName()), variableValues, true);
		startTime = (Date)getAttribute("session.starttime");
		if(startTime == null)
		{
			startTime = new Date();
			setAttribute("session.starttime", startTime);
		}
		return new DeploymentExecution(getNextExecutionID(), this, httpRequest,
				httpReesponse, parameterValues).doNext();
	}

	/**
	 * Aborts this session.
	 * 
	 * @param httpSession The HTTP session.
	 * @param httpRequest The HTTP request.
	 * @param httpResponse The HTTP response.
	 * @return The last document to render.
	 */
	public IDocument abort(HttpSession httpSession,
			HttpServletRequest httpRequest, HttpServletResponse httpReesponse,
			String prefix, int depth, Map variableValues, Map parameterValues)
	{
		this.httpSession = httpSession;
		this.depth = depth;
		this.qualifier = new String[depth + 1];
		for(int i = 0; i <= depth; i++)
		{
			qualifier[i] = prefix + i + ".";
		}
		try
		{
			assignVariables((IVariableRegistry)session
					.lookupService(IVariableRegistry.class.getName()), variableValues, true);
			return new DeploymentExecution(getNextExecutionID(), this, httpRequest,
					httpReesponse, parameterValues).doAbort();
		}
		finally
		{
			fireDisposedEvent(httpSession);
			httpSession.setAttribute("vtp.supressSessionDisposedEvent", Boolean.TRUE);
			if (!"true".equals(getAttribute("fragment")))
				httpSession.invalidate();
		}
	}

	public void end(HttpSession httpSession, String prefix, int depth)
	{
		this.httpSession = httpSession;
		this.depth = depth;
		this.qualifier = new String[depth + 1];
		for(int i = 0; i <= depth; i++)
		{
			qualifier[i] = prefix + i + ".";
		}
		fireDisposedEvent(httpSession);
		httpSession.setAttribute("vtp.supressSessionDisposedEvent", Boolean.TRUE);
		if (!"true".equals(getAttribute("fragment")))
			httpSession.invalidate();
	}

	public void fireDisposedEvent(HttpSession httpSession) {
		if (httpSession.getAttribute("vtp.supressSessionDisposedEvent") == Boolean.TRUE)
			return;
		IReporter reporter = (IReporter)session.lookupService(IReporter.class.getName());
		if(reporter.isReportingEnabled())
		{
			Dictionary report = new Hashtable();
			report.put("event", "session.disposed");
			reporter.report(
					IReporter.SEVERITY_INFO, "Session \"" + id + "\" disposed.", report);
		}
	}

	/**
	 * Returns the next execution ID.
	 * 
	 * @return The next execution ID.
	 */
	private String getNextExecutionID()
	{
		Object executionID = httpSession.getAttribute(qualifier + EXECUTION_ID);
		if (!(executionID instanceof Integer))
			executionID = new Integer(0);
		executionID = new Integer(((Integer)executionID).intValue() + 1);
		httpSession.setAttribute(qualifier + EXECUTION_ID, executionID);
		return executionID.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ISessionDescriptor#getSessionID()
	 */
	public String getSessionID()
	{
		return id;
	}
	
	public Date getSessionStartTime()
	{
		return startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ISessionDescriptor#
	 *      getServiceIdentifiers()
	 */
	public String[] getServiceIdentifiers()
	{
		return new String[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ISessionDescriptor#getService(
	 *      java.lang.String)
	 */
	public Object getService(String identifier) throws NullPointerException
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ISessionDescriptor#getAttributeNames()
	 */
	public String[] getAttributeNames()
	{
		List<String> list = new LinkedList<String>();
		if (httpSession != null)
		{
			for (Enumeration<String> e = httpSession.getAttributeNames(); e.hasMoreElements();)
			{
				String name = e.nextElement();
				if (name.startsWith(qualifier[depth]))
					list.add(name.substring(qualifier[depth].length()));
			}
		}
		return list.toArray(new String[list.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ISessionDescriptor#getAttribute(
	 *      java.lang.String)
	 */
	public Object getAttribute(String attributeName) throws NullPointerException
	{
		if(httpSession == null || attributeName == null)
			return null;
		Object obj = null;
		if(attributeName.startsWith("variable.records"))
		{
			for(int i = depth; i > -1; i--)
			{
				obj = httpSession.getAttribute(qualifier[i] + attributeName);
				if(obj != null)
					break;
			}
		}
		else
			obj = httpSession.getAttribute(qualifier[depth] + attributeName);
		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ISessionDescriptor#setAttribute(
	 *      java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String attributeName, Object attributeValue)
			throws NullPointerException
	{
		if (httpSession != null)
			httpSession.setAttribute(qualifier[depth] + attributeName, attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ISessionDescriptor#clearAttribute(
	 *      java.lang.String)
	 */
	public void clearAttribute(String attributeName) throws NullPointerException
	{
		if (httpSession != null)
			httpSession.removeAttribute(qualifier[depth] + attributeName);
	}
	
	public Object getInheritedAttribute(String attributeName) throws NullPointerException
	{
		if(httpSession == null || attributeName == null)
			return null;
		Object obj = null;
		for(int i = depth - 1; i > -1; i--)
		{
			obj = httpSession.getAttribute(qualifier[i] + attributeName);
			if(obj != null)
				break;
		}
		return obj;
	}

	public boolean isValid()
	{
		if(System.currentTimeMillis() - lastAccessed >= timeout)
		{
			return false;
		}
		return true;
	}

	@Override
	public String[] getRootAttributeNames()
	{
		List<String> list = new LinkedList<String>();
		if (httpSession != null)
		{
			for (Enumeration<String> e = httpSession.getAttributeNames(); e.hasMoreElements();)
			{
				String name = e.nextElement();
				if (name.startsWith(ROOT_CONTEXT))
					list.add(name.substring(ROOT_CONTEXT.length()));
			}
		}
		return list.toArray(new String[list.size()]);
	}

	@Override
	public Object getRootAttribute(String attributeName)
		throws NullPointerException
	{
		return httpSession == null ? null : httpSession.getAttribute(ROOT_CONTEXT
			+ attributeName);
	}

	@Override
	public void setRootAttribute(String attributeName, Object attributeValue)
		throws NullPointerException
	{
		if (httpSession != null)
			httpSession.setAttribute(ROOT_CONTEXT + attributeName, attributeValue);
	}

	@Override
	public void clearRootAttribute(String attributeName)
		throws NullPointerException
	{
		if (httpSession != null)
			httpSession.removeAttribute(ROOT_CONTEXT + attributeName);
	}
}
