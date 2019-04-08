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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.eclipse.vtp.framework.common.IArrayObject;
import org.eclipse.vtp.framework.common.IBooleanObject;
import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IDateObject;
import org.eclipse.vtp.framework.common.IDecimalObject;
import org.eclipse.vtp.framework.common.INumberObject;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.engine.ResourceGroup;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.spi.IProcessDefinition;
import org.eclipse.vtp.framework.spi.IProcessEngine;
import org.eclipse.vtp.framework.util.Guid;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;

/**
 * The HTTP connection strategy.
 * 
 * @author Lonnie Pryor
 */
public class HttpConnector
{
	/** The path prefix for reserved commands. */
	public static final String PATH_PREFIX = "/-/"; //$NON-NLS-1$
	/** The path info for aborting a session. */
	public static final String ABORT_PATH = PATH_PREFIX + "abort"; //$NON-NLS-1$
	/** The path info prefix for the deployment index. */
	public static final String INDEX_PATH = PATH_PREFIX + "index"; //$NON-NLS-1$
	/** The path info for the next step in a session. */
	public static final String NEXT_PATH = PATH_PREFIX + "next"; //$NON-NLS-1$
	/** The path info for binary resources. */
	public static final String RESOURCES_PATH = PATH_PREFIX + "resources"; //$NON-NLS-1$
	/** The path info for the session examiner. */
	public static final String EXAMINE_PATH = PATH_PREFIX + "examine"; //$NON-NLS-1$
	/** The path info for the log level setter. */
	public static final String LOG_PATH = PATH_PREFIX + "logging"; //$NON-NLS-1$
	/** The property prefix for mime types. */
	private static final String MIME_TYPE_PREFIX = "mime.type."; //$NON-NLS-1$
	/** The name of the session attribute the deployment ID is stored in. */
	private static final String DEPLOYMENT_ID = "deployment.id";
	/** The name of the session attribute the deployment ID is stored in. */
	private static final String ENTRY_POINT_NAME = "entry.point.name";
	/** Ordering of paths by longest-first. */
	private static final Comparator<String> PATH_SORT = new Comparator<String>()
	{
		public int compare(String left, String right)
		{
			int difference = right.length() - left.length();
			if (difference == 0)
				difference = left.compareTo(right);
			return difference;
		}
	};
	/** The process engine to use. */

	/** The log to use. */
	// private final LogService log;
	/** The process engine to use. */
	private final IProcessEngine engine;
	/** The HTTP service to use. */
	private final HttpService httpService;
	/** Comment for reporter. */
	private final IReporter reporter;
	/** The available process definitions. */
	private final Map<String, IProcessDefinition> definitionsByID = new HashMap<String, IProcessDefinition>();
	/** The contributors of the available process definitions. */
	private final Map<String, Bundle> definitionContributors = new HashMap<String, Bundle>();
	/** The available resources definitions. */
	private final Map<String, ResourceGroup> resourcesByID = new HashMap<String, ResourceGroup>();
	/** The currently configured properties. */
	@SuppressWarnings("rawtypes")
	private Dictionary properties = null;
	/** The currently configured registration path. */
	private String servletPath = null;
	/** The currently configured registration path. */
	private String resourcesPath = null;
	/** True if this connector is serving requests. */
	private boolean open = false;
	/** The currently deployed processes. */
	private final Map<String, Deployment> deploymentsByKey = new HashMap<String, Deployment>();
	/** The currently deployed processes. */
	private final Map<String, Deployment> deploymentsByID = new HashMap<String, Deployment>();
	/** The currently deployed processes. */
	private final Map<String, Deployment> deploymentsByPath = new TreeMap<String, Deployment>(PATH_SORT);

	/**
	 * Creates a new HttpConnector.
	 * 
	 * @param engine The process engine to use.
	 * @param httpService The HTTP service to use.
	 */
	public HttpConnector(LogService log, IProcessEngine engine,
			HttpService httpService, IReporter reporter)
	{
		// this.log = log;
		this.engine = engine;
		this.httpService = httpService;
		this.reporter = reporter;
	}

	/**
	 * Registers a process definition with this connector.
	 * 
	 * @param definitionID The ID of the definition to register.
	 * @param definition The definition to register.
	 */
	public void registerDefinition(String definitionID,
			IProcessDefinition definition, Bundle contributor)
	{
		synchronized (definitionsByID)
		{
			definitionsByID.put(definitionID, definition);
			definitionContributors.put(definitionID, contributor);
		}
	}

	/**
	 * Releases a process definition in this connector.
	 * 
	 * @param definitionID The ID of the definition to release.
	 */
	public void releaseDefinition(String definitionID)
	{
		synchronized (definitionsByID)
		{
			definitionContributors.remove(definitionID);
			definitionsByID.remove(definitionID);
		}
	}

	/**
	 * Registers a resource group with this connector.
	 * 
	 * @param resourcesID The ID of the resource group to register.
	 * @param resources The resource group to register.
	 */
	public void registerResouces(String resourcesID, ResourceGroup resources)
	{
		synchronized (resourcesByID)
		{
			resourcesByID.put(resourcesID, resources);
		}
	}

	/**
	 * Releases a resource group in this connector.
	 * 
	 * @param resourcesID The ID of the resource group to release.
	 */
	public void releaseResouces(String resourcesID)
	{
		synchronized (resourcesByID)
		{
			resourcesByID.remove(resourcesID);
		}
	}

	/**
	 * Configures the basic properties of this connector.
	 * 
	 * @param properties The basic configuration properties.
	 */
	public synchronized void configure(@SuppressWarnings("rawtypes") Dictionary properties)
	{
		close();
		this.properties = properties;
		open();
	}

	/**
	 * Deploys a process into the engine.
	 * 
	 * @param key The key for the deployment.
	 * @param properties The properties of the deployment.
	 */
	public synchronized void deploy(String key, @SuppressWarnings("rawtypes") Dictionary properties)
	{
		Deployment deployment = deploymentsByKey.remove(key);
		if (deployment != null)
			deploymentsByPath.remove(deployment.getPath());
		String definitionID = (String)properties.get("definition.id"); //$NON-NLS-1$
		String deploymentID = (String)properties.get("deployment.id");
		String path = (String)properties.get("path");
		IProcessDefinition definition = null;
		Bundle contributor = null;
		synchronized (definitionsByID)
		{
			definition = definitionsByID.get(definitionID);
			contributor = definitionContributors.get(definitionID);
		}
		if(definition == null) //race condition possible
		{
			try
			{
				Thread.sleep(15000);
			}
			catch(Exception ex)
			{
				
			}
			synchronized (definitionsByID)
			{
				definition = definitionsByID.get(definitionID);
				contributor = definitionContributors.get(definitionID);
			}
		}
		if (definition == null || contributor == null)
			return;
		try
		{
			deployment = new Deployment(engine, definition, properties, contributor,
					reporter);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		synchronized (resourcesByID)
		{
			for (Map.Entry<String, ResourceGroup> entry : resourcesByID.entrySet())
			{
				deployment.setResourceManager(entry.getKey(), entry.getValue());
			}
		}
		if (deploymentID == null)
			return;
		if (deploymentsByID.containsKey(deploymentID))
			return;
		deploymentsByKey.put(key, deployment);
		deploymentsByID.put(deploymentID, deployment);
		if (path == null)
			return;
//		if (deploymentsByPath.containsKey(path))
//			return;
		deploymentsByPath.put(path, deployment);
	}

	/**
	 * Undeploys a process from the engine.
	 * 
	 * @param key The key for the deployment.
	 */
	public synchronized void undeploy(String key)
	{
		Deployment deployment = deploymentsByKey.remove(key);
		if (deployment == null)
			return;
		deploymentsByPath.remove(deployment.getPath());
		deployment.dispose();
	}
	
	/**
	 * Serves up a site that can be used to examine and modify active sessions.
	 * 
	 * @param req The HTTP request.
	 * @param res The HTTP response.
	 * @throws ServletException If the processing fails.
	 * @throws IOException If the connection fails.
	 */
	public synchronized void examine(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		String cmd = req.getParameter("cmd");
		String process = req.getParameter("process");
		String session = req.getParameter("session");
		String variable = req.getParameter("variable");
		String action = req.getParameter("action");
		String value = req.getParameter("value");
		Deployment deployment = null;
		if (process != null)
			deployment = deploymentsByPath.get(process);
		DeploymentSession deploymentSession = null;
		if (session != null)
			deploymentSession = deployment.getActiveSession(session);
		IDataObject parent = null, object = null;
		String fieldName = null;
		if (variable != null) {
			for (String token : variable.split("\\.")) {
				if (object == null)
					object = deploymentSession.getVariableRegistry().getVariable(token);
				else {
					parent = object;
					object = parent.getField(token);
				}
				fieldName = token;
			}
		}
		if ("unlock".equals(cmd)) {
			if (action.startsWith("Save")) {
				IVariableRegistry variables = deploymentSession.getVariableRegistry();
				if (object == null) {
					object = variables.createVariable(parent.getField(fieldName).getType());
					parent.setField(fieldName, object);
				}
				if (object instanceof IBooleanObject)
					((IBooleanObject)object).setValue(value);
				else if (object instanceof IDateObject)
					((IDateObject)object).setValue(value);
				else if (object instanceof IDecimalObject)
					((IDecimalObject)object).setValue(value);
				else if (object instanceof INumberObject)
					((INumberObject)object).setValue(value);
				else if (object instanceof IStringObject)
					((IStringObject)object).setValue(value);
			}
			deploymentSession.unlock();
			res.sendRedirect(res.encodeRedirectURL("examine?cmd=variables&process=" + process + "&session=" + session));
		} else {
			String base = res.encodeURL("examine?cmd=");
			res.setContentType("text/html");
			ServletOutputStream out = res.getOutputStream();
			out.println("<html><head><title>Runtime Examination</title></head><body>");
			if ("edit".equals(cmd)) {
				out.println("<p>Application: " + process + "</p>");
				out.println("<p>Session: " + deploymentSession.getSessionID() + " : " + deploymentSession.getCurrentPosition() + "</p>");
				deploymentSession.lock();
				out.println("<form action=\"examine\" method=\"post\">");
				out.println("<input type=\"hidden\" name=\"cmd\" value=\"unlock\" />");
				out.println("<input type=\"hidden\" name=\"process\" value=\"" + process + "\" />");
				out.println("<input type=\"hidden\" name=\"session\" value=\"" + session + "\" />");
				out.println("<input type=\"hidden\" name=\"variable\" value=\"" + variable + "\" />");
				out.println("<p>" + variable + " = ");
				out.println("<input type=\"text\" name=\"value\" value=\"");
				if (object != null)
					out.println(object.toString());
				out.println("\" /></p>");
				out.println("<p><input type=\"submit\" name=\"action\" value=\"Save & Unlock\" />&nbsp;");
				out.println("<input type=\"submit\" name=\"action\" value=\"Cancel & Unlock\" /></p>");
				out.println("</form>");
			} else if ("variables".equals(cmd)) {
				out.println("<p>Application: " + process + "</p>");
				out.println("<p>Session: " + deploymentSession.getSessionID() + " : " + deploymentSession.getCurrentPosition() + "</p>");
				out.println("<p>Select a variable to edit:</p><ul>");
				IVariableRegistry variables = deploymentSession.getVariableRegistry();
				String prefix = base + "edit&process=" + process + "&session=" + session + "&variable=";
				String[] variableNames = variables.getVariableNames();
				Arrays.sort(variableNames);
				for (String name : variableNames)
					examineVariable(out, prefix, name, variables.getVariable(name));
				out.println("</ul>");
			} else if ("sessions".equals(cmd)) {
				out.println("<p>Application: " + process + "</p>");
				out.println("<p>Select the session to examine:</p><ul>");
				for (DeploymentSession s : deployment.getActiveSessions()) {
					out.print("<li><a href=\"");
					out.print(base);
					out.print("variables&process=");
					out.print(process);
					out.print("&session=");
					out.print(s.getSessionID());
					out.print("\">");
					out.print(s.getSessionID());
					out.print(" : ");
					out.print(s.getCurrentPosition());
					out.println("</a></li>");
				}
				out.println("</ul>");
			} else {
				out.println("<p>Select the application to examine:</p><ul>");
				for (Object o : deploymentsByPath.keySet()) {
					out.print("<li><a href=\"");
					out.print(base);
					out.print("sessions&process=");
					out.print(o.toString());
					out.print("\">");
					out.print(o.toString());
					out.println("</a></li>");
				}
				out.println("</ul>");
			}
			out.println("</body></html>");
		}
	}

	/**
	 * @param out
	 * @param prefix
	 * @param name
	 * @param var
	 * @throws IOException
	 */
	private void examineVariable(ServletOutputStream out, String prefix,
			String name, IDataObject var) throws IOException {
		if (var instanceof IBooleanObject ||
				 var instanceof IDateObject ||
				 var instanceof IDecimalObject ||
				 var instanceof INumberObject ||
				 var instanceof IStringObject) {
			out.print("<li>");
			out.print(name);
			out.print(" = ");
			out.print(var.toString());
			out.print("&nbsp;&nbsp;<a href=\"");
			out.print(prefix);
			out.print(name);
			out.print("\">Lock & Edit</a></li>");
		} else if (!(var instanceof IArrayObject)) {
			String[] fieldNames = var.getType().getFieldNames();
			Arrays.sort(fieldNames);
			for (String fieldName : fieldNames)
				examineVariable(out, prefix, name + "." + fieldName, var.getField(fieldName));
		}
	}

	/**
	 * Processes an HTTP request.
	 * 
	 * @param req The HTTP request.
	 * @param res The HTTP response.
	 * @throws ServletException If the processing fails.
	 * @throws IOException If the connection fails.
	 */
	public void process(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		try
		{
			invokeProcessEngine(req, res, req.getSession(), HttpUtils
					.normalizePath(req.getPathInfo()), Collections.emptyMap(),
					new HashMap<String, String[]>(), false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * invokeProcessEngine.
	 * 
	 * @param req
	 * @param res
	 * @param httpSession
	 * @param pathInfo
	 * @param embeddedInvocation TODO
	 * @throws IOException
	 * @throws ServletException
	 */
	private void invokeProcessEngine(HttpServletRequest req,
			HttpServletResponse res, HttpSession httpSession, String pathInfo,
			Map<Object, Object> variableValues,
			@SuppressWarnings("rawtypes") Map<String, String[]> parameterValues,
			boolean embeddedInvocation)
			throws IOException, ServletException
	{
		boolean newSession = false;
		Integer depth = (Integer)httpSession.getAttribute("connector.depth");
		if (depth == null)
		{
			depth = new Integer(0);
		}
		String prefix = "connector.attributes.";
		String fullPrefix = prefix + depth.intValue() + ".";
		if (embeddedInvocation)
			httpSession.setAttribute(fullPrefix + "fragment", "true");
		Deployment deployment = null;
		String brand = null;
		String entryName = null;
		boolean subdialog = false;
		if (!pathInfo.startsWith(PATH_PREFIX))
		{
			System.out.println("invoking process engine for new session: " + pathInfo);
			newSession = true;
			synchronized (this)
			{
				for (String path : deploymentsByPath.keySet())
				{
					System.out.println("Comparing to deployment: " + path);
					if (pathInfo.equals(path) || pathInfo.startsWith(path)
							&& pathInfo.length() > path.length()
							&& pathInfo.charAt(path.length()) == '/')
					{
						deployment = deploymentsByPath.get(path);
						System.out.println("Matching deployment found: " + deployment);
						brand = req.getParameter("BRAND");
						if(req.getParameter("SUBDIALOG") != null)
							subdialog = Boolean.parseBoolean(req.getParameter("SUBDIALOG"));
						if (pathInfo.length() > path.length() + 1)
						{
							entryName = pathInfo.substring(path.length() + 1);
							System.out.println("Entry point name: " + entryName);
						}
						break;
					}
				}
			}
			if (deployment == null)
			{
				res.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			if (entryName == null) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			pathInfo = NEXT_PATH;
			httpSession.setAttribute(fullPrefix + DEPLOYMENT_ID, deployment
					.getProcessID());
		}
		else if(pathInfo.equals(LOG_PATH))
		{
			if(req.getParameter("cmd") != null && req.getParameter("cmd").equals("set"))
			{
				String level = req.getParameter("level");
				if(level == null || (!level.equalsIgnoreCase("ERROR") && !level.equalsIgnoreCase("WARN") && !level.equalsIgnoreCase("INFO") && !level.equalsIgnoreCase("DEBUG")))
					level = "INFO";
				System.setProperty("org.eclipse.vtp.loglevel", level);
			}
			writeLogging(req, res);
			return;
		}
		else
		{
			String deploymentID = (String)httpSession.getAttribute(fullPrefix
					+ DEPLOYMENT_ID);
			synchronized (this)
			{
				deployment = deploymentsByID.get(deploymentID);
			}
			if (deployment == null)
			{
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
		}
		if (subdialog)
			httpSession.setAttribute(fullPrefix + "subdialog", "true");
		if (pathInfo.equals(INDEX_PATH))
		{
			writeIndex(res, deployment);
			return;
		}
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		if (ServletFileUpload.isMultipartContent(new ServletRequestContext(
				req)))
		{
			System.out.println("ServletFileUpload.isMultipartContent(new ServletRequestContext(httpRequest)) is true");
			try
			{
				List items = upload.parseRequest(req);
				for (int i = 0; i < items.size(); i++)
				{
					FileItem fui = (FileItem)items.get(i);
					if (fui.isFormField() || "text/plain".equals(fui.getContentType()))
					{
						System.out.println("Form Field: " + fui.getFieldName() + " | " + fui.getString());
						parameterValues
								.put(fui.getFieldName(), new String[] { fui.getString() });
					}
					else
					{
						File temp = File.createTempFile(Guid.createGUID(), ".tmp");
						fui.write(temp);
						parameterValues.put(fui.getFieldName(), new String[] { temp
								.getAbsolutePath() });
						fui.delete();
						System.out.println("File Upload: " + fui.getFieldName());
						System.out.println("\tTemp file name: " + temp.getAbsolutePath());
						System.out.println("\tContent Type: " + fui.getContentType());
						System.out.println("\tSize: " + fui.getSize());
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		for (Enumeration e = req.getParameterNames(); e.hasMoreElements();)
		{
			String key = (String)e.nextElement();
			parameterValues.put(key, req.getParameterValues(key));
		}
		for(String key : parameterValues.keySet())
		{
			String[] values = parameterValues.get(key);
			if(values == null || values.length == 0)
				System.out.println(key + " empty");
			else
			{
				System.out.println(key + " " + values[0]);
				for(int i = 1; i < values.length; i++)
					System.out.println("\t" + values[i]);
			}
		}
		IDocument document = null;
		if (pathInfo.equals(ABORT_PATH))
			document = deployment.abort(httpSession, req, res, prefix, depth.intValue(),
					variableValues, parameterValues);
		else if (pathInfo.equals(NEXT_PATH))
		{
			if (brand == null && !newSession)
				document = deployment.next(httpSession, req, res, prefix, depth.intValue(),
						variableValues, parameterValues);
			else
				document = deployment.start(httpSession, req, res, prefix, depth.intValue(),
						variableValues, parameterValues, entryName, brand, subdialog);
		}
		else
		{
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if (document == null)
		{
			res.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;
		}
		else if (document instanceof ControllerDocument)
		{
			ControllerDocument cd = (ControllerDocument)document;
			if (cd.getTarget() == null)
			{
				@SuppressWarnings("unchecked")
				Map<String, Map<String, Object>> outgoing = (Map<String, Map<String, Object>>)httpSession.getAttribute(fullPrefix + "outgoing-data");
				int newDepth = depth.intValue() - 1;
				if (newDepth == 0)
					httpSession.removeAttribute("connector.depth");
				else
					httpSession.setAttribute("connector.depth", new Integer(newDepth));
				String oldFullPrefix = fullPrefix;
				fullPrefix = prefix + newDepth + ".";
				Object[] params = (Object[])httpSession.getAttribute(fullPrefix
						+ "exitparams");
				if (params != null)
					for (int i = 0; i < params.length; i += 2)
						parameterValues.put((String)params[i], (String[])params[i + 1]);
				String[] paramNames = cd.getParameterNames();
				for (int i = 0; i < paramNames.length; ++i)
					parameterValues.put(paramNames[i], cd.getParameterValues(paramNames[i]));
				String[] variableNames = cd.getVariableNames();
				Map<Object, Object> variables = new HashMap<Object, Object>(variableNames.length);
				if (outgoing != null)
				{
					Map<String, Object> map = outgoing.get(cd.getParameterValues("exit")[0]);
					if (map != null)
					{
						for (int i = 0; i < variableNames.length; ++i)
						{
							Object mapping = map.get(variableNames[i]);
							if (mapping != null)
								variables.put(mapping, cd.getVariableValue(variableNames[i]));
						}
					}
				}
				deployment.end(httpSession, prefix, depth.intValue());
				for (@SuppressWarnings("rawtypes")
					Enumeration e = httpSession.getAttributeNames(); e
						.hasMoreElements();)
				{
					String name = (String)e.nextElement();
					if (name.startsWith(oldFullPrefix))
						httpSession.removeAttribute(name);
				}
				invokeProcessEngine(req, res, httpSession, NEXT_PATH, variables,
						parameterValues, newDepth > 0);
				return;
			}
			else
			{
				String[] paramNames = cd.getParameterNames();
				Object[] params = new Object[paramNames.length * 2];
				for (int i = 0; i < params.length; i += 2)
				{
					params[i] = paramNames[i / 2];
					params[i + 1] = cd.getParameterValues(paramNames[i / 2]);
				}
				httpSession.setAttribute(fullPrefix + "exitparams", params);
				String[] variableNames = cd.getVariableNames();
				Map<Object, Object> variables = new HashMap<Object, Object>(variableNames.length);
				for (int i = 0; i < variableNames.length; ++i)
					variables
							.put(variableNames[i], cd.getVariableValue(variableNames[i]));
				httpSession.setAttribute("connector.depth", new Integer(depth
						.intValue() + 1));
				fullPrefix = prefix + (depth.intValue() + 1) + ".";
				String deploymentId = cd.getTarget().substring(0, cd.getTarget().lastIndexOf('(') - 1);
				String entryPointName = cd.getTarget().substring(cd.getTarget().lastIndexOf('(') + 1, cd.getTarget().length() - 1);
				httpSession.setAttribute(fullPrefix + DEPLOYMENT_ID, deploymentId);
				httpSession.setAttribute(fullPrefix + ENTRY_POINT_NAME, entryPointName);
				Map<String, Map<String, Object>> outgoing = new HashMap<String, Map<String, Object>>();
				String[] outPaths = cd.getOutgoingPaths();
				for (int i = 0; i < outPaths.length; ++i)
				{
					Map<String, Object> map = new HashMap<String, Object>();
					String[] names = cd.getOutgoingDataNames(outPaths[i]);
					for (int j = 0; j < names.length; ++j)
						map.put(names[j], cd.getOutgoingDataValue(outPaths[i], names[j]));
					outgoing.put(outPaths[i], map);
				}
				httpSession.setAttribute(fullPrefix + "outgoing-data", outgoing);
				invokeProcessEngine(req, res, httpSession, "/" + deploymentId + "/" + entryPointName, variables,
						parameterValues, true);
				return;
			}
		}
		res.setStatus(HttpServletResponse.SC_OK);
		if(!document.isCachable())
			res.setHeader("Cache-Control", "max-age=0, no-cache");
		res.setContentType(document.getContentType());
		OutputStream writer = res.getOutputStream();
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			XMLWriter xmlWriter = new XMLWriter(writer);
			xmlWriter.setCompactElements(true);
			transformer.transform(document.toXMLSource(), xmlWriter
					.toXMLResult());
			if(reporter.isSeverityEnabled(IReporter.SEVERITY_INFO) && !document.isSecured())
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				xmlWriter = new XMLWriter(baos);
				xmlWriter.setCompactElements(true);
				transformer.transform(document.toXMLSource(), xmlWriter.toXMLResult());
				System.out.println(new String(baos.toByteArray(), "UTF-8"));
			}
		}
		catch (TransformerException e)
		{
			throw new ServletException(e);
		}
		writer.flush();
		writer.close();
	}

	/**
	 * The MIME type for the specified resource.
	 * 
	 * @param resourcePath The resource to determine the MIME type of.
	 * @return The MIME type for the specified resource.
	 */
	public String getMimeType(String resourcePath)
	{
		@SuppressWarnings("rawtypes")
		Dictionary properties = this.properties;
		if (properties == null)
			return null;
		if (resourcePath == null || properties.isEmpty())
			return null;
		int lastSlash = resourcePath.lastIndexOf('/');
		if (lastSlash >= 0)
			resourcePath = resourcePath.substring(lastSlash + 1);
		if (resourcePath.length() == 0)
			return null;
		Object mimeType = properties.get(MIME_TYPE_PREFIX + resourcePath);
		while (!(mimeType instanceof String))
		{
			int firstDot = resourcePath.indexOf('.');
			if (firstDot < 0)
				return null;
			resourcePath = resourcePath.substring(firstDot + 1);
			mimeType = properties.get(MIME_TYPE_PREFIX + resourcePath);
		}
		return (String)mimeType;
	}

	/**
	 * Returns the resource at the specified path.
	 * 
	 * @param resourcePath The path of the resource to return.
	 * @return The resource at the specified path.
	 */
	public URL getResource(String resourcePath)
	{
		String normal = HttpUtils.normalizePath(resourcePath).substring(1);
		int firstSlash = normal.indexOf('/');
		if (firstSlash < 0)
			return null;
		String resourcesID = normal.substring(0, firstSlash);
		ResourceGroup resources = null;
		synchronized (resourcesByID)
		{
			resources = resourcesByID.get(resourcesID);
		}
		if (resources == null)
			return null;
		return resources.getResource(normal.substring(firstSlash + 1));
	}

	/**
	 * Opens this connector.
	 */
	private void open()
	{
		if (open)
			return;
		try
		{
			String servletPath = null, resourcesPath = null;
			Object path = properties == null ? null : properties.get("path");
			if (path instanceof String)
				servletPath = HttpUtils.normalizePath((String)path);
			else
				servletPath = "/"; //$NON-NLS-1$
			if ("/".equals(servletPath)) //$NON-NLS-1$
				resourcesPath = RESOURCES_PATH;
			else
				resourcesPath = servletPath + RESOURCES_PATH;
			HttpConnectorContext context = new HttpConnectorContext(httpService
					.createDefaultHttpContext(), this);
			httpService.registerResources(resourcesPath, "/", context); //$NON-NLS-1$
			this.resourcesPath = resourcesPath;
			httpService.registerServlet(servletPath, new HttpConnectorServlet(this),
					null, context);
			this.servletPath = servletPath;
			open = true;
		}
		catch (NamespaceException e)
		{
			throw new IllegalArgumentException(e);
		}
		catch (ServletException e)
		{
			throw new IllegalArgumentException(e);
		}
		finally
		{
			if (!open && resourcesPath != null)
			{
				String resourcesPath = this.resourcesPath;
				this.resourcesPath = null;
				httpService.unregister(resourcesPath);
			}
		}
	}

	/**
	 * Closes this connector.
	 */
	private void close()
	{
		if (!open)
			return;
		open = false;
		try
		{
			if (servletPath != null)
				httpService.unregister(servletPath);
		}
		finally
		{
			servletPath = null;
			try
			{
				if (resourcesPath != null)
					httpService.unregister(resourcesPath);
			}
			finally
			{
				resourcesPath = null;
			}
		}
	}

	/**
	 * Writes an index page for the current deployments.
	 * 
	 * @param res The HTTP response.
	 * @param deployment The current deployment.
	 * @throws IOException If the connection fails.
	 */
	private void writeIndex(HttpServletResponse res, Deployment deployment)
			throws IOException
	{
		String deploymentID = deployment.getID();
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		PrintWriter writer = res.getWriter();
		writer.println("<html>");
		writer.println("<head><title>Deployments</title></head>");
		writer.println("<body>");
		if (deploymentID != null)
		{
			writer.print("<p>CURRENT: ");
			writer.print(deploymentID);
			writer.println("</p>");
		}
		synchronized (this)
		{
			for (Map.Entry<String, Deployment> entry : deploymentsByPath.entrySet())
			{
				writer.print("<p><a href=\"");
				writer.print(res.encodeURL(entry.getKey()));
				writer.print("\">");
				writer.print(entry.getValue().getProcessID());
				writer.println("</a></p>");
			}
		}
		writer.println("</body>");
		writer.println("</html>");
		writer.flush();
		writer.close();
	}

	/**
	 * Writes an logging page for the current deployments.
	 * 
	 * @param res The HTTP response.
	 * @param deployment The current deployment.
	 * @throws IOException If the connection fails.
	 */
	private void writeLogging(HttpServletRequest req, HttpServletResponse res)
			throws IOException
	{
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		PrintWriter writer = res.getWriter();
		writer.println("<html>");
		writer.println("<head><title>Logging Level</title></head>");
		writer.println("<body>");
		writer.print("<p>Current Log Level: " + System.getProperty("org.eclipse.vtp.loglevel", "INFO"));
		writer.println("</p>");
		StringBuffer buffer = new StringBuffer();
		String contextPath = HttpUtils.normalizePath(req.getContextPath());
		if (!contextPath.equals("/")) //$NON-NLS-1$
			buffer.append(contextPath);
		String servletPath = HttpUtils.normalizePath(req.getServletPath());
		if (!servletPath.equals("/")) //$NON-NLS-1$
			buffer.append(servletPath);
		buffer.append(LOG_PATH);
		writer.println("<form action=\"" + buffer.toString() + "\">");
		writer.println("<input type=\"hidden\" name=\"cmd\" value=\"set\"/>");
		writer.println("New Level <select name=\"level\">");
		writer.println("<option>ERROR</option>");
		writer.println("<option>WARN</option>");
		writer.println("<option selected>INFO</option>");
		writer.println("<option>DEBUG</option>");
		writer.println("</select>");
		writer.println("<input type=\"submit\" value=\"Set\"/>");
		writer.println("</form>");
		writer.println("</body>");
		writer.println("</html>");
		writer.flush();
		writer.close();
	}
}
