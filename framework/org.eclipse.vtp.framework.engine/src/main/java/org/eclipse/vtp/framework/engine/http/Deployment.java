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

import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.interactions.core.media.IResourceManager;
import org.eclipse.vtp.framework.interactions.core.media.IResourceManagerRegistry;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.spi.IProcess;
import org.eclipse.vtp.framework.spi.IProcessDefinition;
import org.eclipse.vtp.framework.spi.IProcessDescriptor;
import org.eclipse.vtp.framework.spi.IProcessEngine;
import org.osgi.framework.Bundle;

/**
 * A process deployed on the HTTP connector.
 * 
 * @author Lonnie Pryor
 */
public class Deployment implements IProcessDescriptor, IResourceManagerRegistry, HttpSessionListener
{
	/** The process instance. */
	private final IProcess process;
	/** The process properties. */
	private final Dictionary properties;
	/** The process contributor. */
	private final Bundle contributor;
	/** Comment for reporter. */
	private final IReporter reporter;
	/** The resource managers. */
	private final Map<String, IResourceManager> resources = new HashMap<String, IResourceManager>();
	/** The active sessions. */
	private final Map<String, DeploymentSession> sessions = new HashMap<String, DeploymentSession>();
	/** Method to use when unregistering as a session listener. */
	private volatile Method unregisterSessionListener = null;

	/**
	 * Creates a new HttpDeployment.
	 * 
	 * @param engine The process engine to use.
	 * @param definition The process definition to build from.
	 * @param properties The process properties.
	 * @param contributor The process contributor.
	 */
	public Deployment(IProcessEngine engine, IProcessDefinition definition,
			Dictionary properties, Bundle contributor, IReporter reporter)
	{
		this.properties = properties;
		this.contributor = contributor;
		this.reporter = reporter;
		process = engine.createProcess(definition, this);
		String[] resourceManagerIDs = (String[])properties.get("resources"); //$NON-NLS-1$
		if (resourceManagerIDs != null)
			for (int i = 0; i < resourceManagerIDs.length; ++i)
				resources.put(resourceManagerIDs[i], null);
		if(reporter.isReportingEnabled())
		{
			Dictionary report = new Hashtable();
			report.put("event", "process.started");
			reporter.report(
					IReporter.SEVERITY_INFO, "Process \"" + getID() + "\" Started", report);
		}
	}
	
	public DeploymentSession getActiveSession(String sessionId) {
		synchronized (sessions)
		{
			return sessions.get(sessionId);
		}
	}
	
	public DeploymentSession[] getActiveSessions() {
		LinkedList<DeploymentSession> list = new LinkedList<DeploymentSession>();
		synchronized (sessions)
		{
			list.addAll(sessions.values());
		}
		return list.toArray(new DeploymentSession[list.size()]);
	}

	/**
	 * ReTurns the deployment ID.
	 * 
	 * @return The deployment ID.
	 */
	public String getID()
	{
		String id = (String)properties.get("process.id"); //$NON-NLS-1$
		if (id == null)
			id = (String)properties.get("deployment.id"); //$NON-NLS-1$
		return id;
	}

	/**
	 * ReTurns the deployment path.
	 * 
	 * @return The deployment path.
	 */
	public String getPath()
	{
		return HttpUtils.normalizePath((String)properties.get("path")); //$NON-NLS-1$
	}

	/**
	 * Configures an available resource manager.
	 * 
	 * @param resourceManagerID The ID of the resource manager.
	 * @param resourceManager The manager to register.
	 */
	public void setResourceManager(String resourceManagerID,
			IResourceManager resourceManager)
	{
		synchronized (resources)
		{
			if (!resources.containsKey(resourceManagerID))
				resources.put(resourceManagerID, resourceManager);
		}
	}

	/**
	 * Preforms the first step in the supplied session.
	 * 
	 * @param httpSession The HTTP session.
	 * @param httpRequest The HTTP request.
	 * @param httpResponse The HTTP response.
	 * @return The next document to render.
	 */
	public IDocument start(HttpSession httpSession,
			HttpServletRequest httpRequest, HttpServletResponse httpReesponse,
			String prefix, int depth, Map<Object, Object> variableValues, Map parameterValues, String entryName, String brand, boolean subdialog)
	{
		if (unregisterSessionListener == null) {
			synchronized (this) {
				if (unregisterSessionListener == null) {
					try {
						Class<?> manager = Thread.currentThread().getContextClassLoader().loadClass(
								"org.eclipse.vtp.framework.webapp.HttpSessionListenerManager");
						manager.getMethod("addHttpSessionListener", HttpSessionListener.class).invoke(null, this);
						unregisterSessionListener = manager.getMethod("removeHttpSessionListener", HttpSessionListener.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		String sessionID = httpSession.getId();
		DeploymentSession session = new DeploymentSession(sessionID, process);
		synchronized (sessions) {
			sessions.put(sessionID, session);
		}
		ResultDocument result = null;
		session.lock();
		try {
			result = session.start(httpSession, httpRequest, httpReesponse,
					prefix, depth, variableValues, parameterValues, entryName, brand, subdialog);
		} finally {
			session.unlock();
		}
		IDocument document = null;
		if (result == null || result.isTerminated())
			document = abort(httpSession, httpRequest, httpReesponse, prefix, depth,
					variableValues, parameterValues);
		if (result != null && result.getDocument() != null)
			document = result.getDocument();
		return document;
	}

	/**
	 * Preforms the next step in the supplied session.
	 * 
	 * @param httpSession The HTTP session.
	 * @param httpRequest The HTTP request.
	 * @param httpResponse The HTTP response.
	 * @return The next document to render.
	 */
	public IDocument next(HttpSession httpSession,
			HttpServletRequest httpRequest, HttpServletResponse httpReesponse,
			String prefix, int depth, Map<Object, Object> variableValues, Map parameterValues)
	{
		String sessionID = httpSession.getId();
		DeploymentSession session = null; 
		synchronized (sessions) {
			session = sessions.get(sessionID);
			if (session == null) {
				session = new DeploymentSession(sessionID, process);
				sessions.put(sessionID, session);
			}
		}
		ResultDocument result = null;
		session.lock();
		try {
			result = session.next(httpSession, httpRequest, httpReesponse,
				prefix, depth, variableValues, parameterValues);
		} finally {
			session.unlock();
		}
		IDocument document = null;
		System.out.println("Result: " + result);
		if(result != null)
		{
			System.out.println("Is Finished: " + result.isTerminated());
			System.out.println("Document: " + result.getDocument());
		}
		if (result == null)
			document = abort(httpSession, httpRequest, httpReesponse, prefix, depth,
					variableValues, parameterValues);
		if (result != null && result.getDocument() != null)
			document = result.getDocument();
		return document;
	}
	
	public void end(HttpSession httpSession, String prefix, int depth)
	{
		String sessionID = httpSession.getId();
		synchronized (sessions)
		{
			DeploymentSession session = sessions.remove(sessionID);
			System.out.println("Ending Session: " + session);
			if(session != null)
				session.end(httpSession, prefix, depth);
		}
	}

	/**
	 * Aborts the supplied session.
	 * 
	 * @param httpSession The HTTP session.
	 * @param httpRequest The HTTP request.
	 * @param httpResponse The HTTP response.
	 * @return The last document to render.
	 */
	public IDocument abort(HttpSession httpSession,
			HttpServletRequest httpRequest, HttpServletResponse httpReesponse,
			String prefix, int depth, Map<Object, Object> variableValues, Map parameterValues)
	{
		String sessionID = httpSession.getId();
		DeploymentSession session = null; 
		synchronized (sessions) {
			session = sessions.get(sessionID);
			if (session == null) {
				session = new DeploymentSession(sessionID, process);
				sessions.put(sessionID, session);
			}
		}
		session.lock();
		try {
			return session.abort(httpSession, httpRequest, httpReesponse, prefix, depth,
					variableValues, parameterValues);
		} finally {
			session.unlock();
			synchronized (sessions)
			{
				sessions.remove(sessionID);
			}
		}
	}

	/**
	 * dispose.
	 * 
	 */
	public void dispose()
	{
		synchronized (this) {
			if (unregisterSessionListener != null) {
				try {
					unregisterSessionListener.invoke(null, this);
					unregisterSessionListener = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(reporter.isReportingEnabled())
		{
			Dictionary report = new Hashtable();
			report.put("event", "process.stopped");
			((IReporter)process.lookupService(IReporter.class.getName())).report(
					IReporter.SEVERITY_INFO, "Process \"" + getID() + "\" Stopped", report);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDescriptor#getProcessID()
	 */
	public String getProcessID()
	{
		return getID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDescriptor#getProperty(
	 *      java.lang.String)
	 */
	public Object getProperty(String propertyName) throws NullPointerException
	{
		return properties.get(propertyName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDescriptor#
	 *      getServiceIdentifiers()
	 */
	public String[] getServiceIdentifiers()
	{
		return new String[] { IResourceManagerRegistry.class.getName() };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDescriptor#getService(
	 *      java.lang.String)
	 */
	public Object getService(String identifier) throws NullPointerException
	{
		return IResourceManagerRegistry.class.getName().equals(identifier) ? this
				: null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDescriptor#loadClass(
	 *      java.lang.String)
	 */
	public Class<?> loadClass(String className) throws ClassNotFoundException,
			NullPointerException
	{
		return contributor.loadClass(className);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDescriptor#isSeverityEnabled(
	 *      int)
	 */
	public boolean isSeverityEnabled(int severity)
	{
		return reporter.isSeverityEnabled(severity);
	}

	
	public boolean isReportingEnabled()
	{
		return reporter.isReportingEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IProcessDescriptor#report(int,
	 *      java.lang.String[], java.lang.String, java.util.Dictionary)
	 */
	public void report(int severity, String[] categories, String message,
			Dictionary properties)
	{
		reporter.report(severity, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.
	 *      IResourceManagerRegistry#getResourceManagerIDs()
	 */
	public String[] getResourceManagerIDs()
	{
		synchronized (resources)
		{
			return resources.keySet().toArray(new String[resources.size()]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.
	 *      IResourceManagerRegistry#getResourceManager(java.lang.String)
	 */
	public IResourceManager getResourceManager(String resourceManagerID)
	{
		synchronized (resources)
		{
			return resources.get(resourceManagerID);
		}
	}

	public void sessionCreated(HttpSessionEvent se) {}

	public void sessionDestroyed(HttpSessionEvent se) {
		synchronized (sessions)
		{
			DeploymentSession session = sessions.remove(se.getSession().getId());
			if (session != null)
				session.fireDisposedEvent(se.getSession());
		}
	}
}
