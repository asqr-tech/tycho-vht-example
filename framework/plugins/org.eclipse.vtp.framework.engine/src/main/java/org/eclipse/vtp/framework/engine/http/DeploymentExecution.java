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

import java.net.URLEncoder;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.vtp.framework.common.IArrayObject;
import org.eclipse.vtp.framework.common.IBooleanObject;
import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IDateObject;
import org.eclipse.vtp.framework.common.IDecimalObject;
import org.eclipse.vtp.framework.common.IMapObject;
import org.eclipse.vtp.framework.common.INumberObject;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.common.commands.ExitCommand;
import org.eclipse.vtp.framework.common.commands.ForwardCommand;
import org.eclipse.vtp.framework.common.commands.IControllerCommandVisitor;
import org.eclipse.vtp.framework.common.commands.IncludeCommand;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.interactions.core.IInteractionType;
import org.eclipse.vtp.framework.interactions.core.IInteractionTypeRegistry;
import org.eclipse.vtp.framework.interactions.core.IInteractionTypeSelection;
import org.eclipse.vtp.framework.interactions.core.commands.ConversationCommand;
import org.eclipse.vtp.framework.interactions.core.commands.EndMessageCommand;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.interactions.core.platforms.ILink;
import org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory;
import org.eclipse.vtp.framework.interactions.core.platforms.IPlatform;
import org.eclipse.vtp.framework.interactions.core.platforms.IPlatformSelector;
import org.eclipse.vtp.framework.interactions.core.platforms.IRenderingQueue;
import org.eclipse.vtp.framework.spi.ICommand;
import org.eclipse.vtp.framework.spi.IExecution;
import org.eclipse.vtp.framework.spi.IExecutionDescriptor;
import org.eclipse.vtp.framework.spi.IRunnableCommand;
import org.eclipse.vtp.framework.spi.IRunnableCommandVisitor;

/**
 * A single execution on the HTTP connector.
 * 
 * @author Lonnie Pryor
 */
public class DeploymentExecution implements IExecutionDescriptor, ILinkFactory,
		IRenderingQueue, IRunnableCommandVisitor, IControllerCommandVisitor,
		IInteractionTypeSelection
{
	/** The execution ID. */
	private final String id;
	/** The process session. */
	private final DeploymentSession session;
	/** The HTTP request. */
	private final HttpServletRequest httpRequest;
	/** The HTTP response. */
	private final HttpServletResponse httpResponse;
	/** The execution parameters. */
	private final Map parameters;
	/** The current interaction type registry. */
	private IInteractionTypeRegistry interactionTypeRegistry = null;
	/** The current interaction type registry. */
	private IVariableRegistry variableRegistry = null;
	/** The current platform. */
	private IPlatform platform = null;
	/** The current command. */
	private ICommand command = null;
	/** True if URL encoding is enabled. */
	private boolean urlEncoded = true;

	/**
	 * Creates a new DeploymentExecution.
	 * 
	 * @param id The execution ID.
	 * @param session The process session.
	 * @param httpRequest The HTTP request.
	 * @param httpResponse The HTTP response.
	 */
	public DeploymentExecution(String id, DeploymentSession session,
			HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			Map parameterValues)
	{
		this.id = id;
		this.session = session;
		this.httpRequest = httpRequest;
		this.httpResponse = httpResponse;
		this.parameters = new HashMap();
		for (Iterator i = parameterValues.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			parameters.put(entry.getKey(), entry.getValue());
		}
//		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
//		if (ServletFileUpload.isMultipartContent(new ServletRequestContext(
//				httpRequest)))
//		{
//			System.out.println("ServletFileUpload.isMultipartContent(new ServletRequestContext(httpRequest)) is true");
//			try
//			{
//				List items = upload.parseRequest(httpRequest);
//				for (int i = 0; i < items.size(); i++)
//				{
//					FileItem fui = (FileItem)items.get(i);
//					if (fui.isFormField() || "text/plain".equals(fui.getContentType()))
//					{
//						System.out.println("Form Field: " + fui.getFieldName() + " | " + fui.getString());
//						parameters
//								.put(fui.getFieldName(), new String[] { fui.getString() });
//					}
//					else
//					{
//						File temp = File.createTempFile(Guid.createGUID(), ".tmp");
//						fui.write(temp);
//						parameters.put(fui.getFieldName(), new String[] { temp
//								.getAbsolutePath() });
//						System.out.println("File Upload: " + fui.getFieldName());
//						System.out.println("\tTemp file name: " + temp.getAbsolutePath());
//						System.out.println("\tContent Type: " + fui.getContentType());
//						System.out.println("\tSize: " + fui.getSize());
//					}
//				}
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//		for (Enumeration e = httpRequest.getParameterNames(); e.hasMoreElements();)
//		{
//			String key = (String)e.nextElement();
//			parameters.put(key, httpRequest.getParameterValues(key));
//		}
	}

	/**
	 * Generates the next document in the interaction.
	 * 
	 * @return The next document in the interaction.
	 */
	public ResultDocument doNext()
	{
		IExecution execution = session.getSession().createExecution(this);
		IReporter reporter = (IReporter)execution.lookupService(IReporter.class.getName());
		if(reporter.isReportingEnabled())
		{
			Dictionary report = new Hashtable();
			report.put("event", "execution.started");
			reporter.report(
					IReporter.SEVERITY_INFO, "Execution \"" + id + "\" Started", report);
		}
		try
		{
			interactionTypeRegistry = (IInteractionTypeRegistry)execution
					.lookupService(IInteractionTypeRegistry.class.getName());
			variableRegistry = (IVariableRegistry)execution
					.lookupService(IVariableRegistry.class.getName());
			IPlatformSelector selector = (IPlatformSelector)execution
					.lookupService(IPlatformSelector.class.getName());
			if (selector == null)
				return null;
			platform = selector.getSelectedPlatform();
			if (platform == null)
				return null;
			while (true)
			{
				if (!execution.hasNextStep())
					return null;
				command = execution.nextStep();
				System.out.println("Processing execution step: " + command);
				IDocument document = null;
				if (command instanceof ConversationCommand)
				{
					urlEncoded = true;
					document = platform.createDocument(this, this);
				}
				else
					document = (IDocument)command.accept(this);
				if (document != null)
					return new ResultDocument(document, !execution.hasNextStep());
			}
		}
		finally
		{
			platform = null;
			variableRegistry = null;
			interactionTypeRegistry = null;
			if(reporter.isReportingEnabled())
			{
				Dictionary report = new Hashtable();
				report.put("event", "execution.ended");
				reporter.report(
						IReporter.SEVERITY_INFO, "Execution \"" + id + "\" Ended", report);
			}
			execution.dispose();
		}
	}

	/**
	 * Generates the abort document and ends the interaction.
	 * 
	 * @return The abort document and ends the interaction.
	 */
	public IDocument doAbort()
	{
		IExecution execution = session.getSession().createExecution(this);
		try
		{
			if (!"true".equals(session.getAttribute("vtp.ended")))
			{
				IReporter reporter = (IReporter)execution.lookupService(IReporter.class.getName());
				if(reporter.isReportingEnabled())
				{
					Dictionary report = new Hashtable();
					report.put("event", "session.terminated");
					reporter.report(
							IReporter.SEVERITY_INFO, "Session \"" + session.getSessionID()
									+ "\" Terminated", report);
				}
			}
			interactionTypeRegistry = (IInteractionTypeRegistry)execution
					.lookupService(IInteractionTypeRegistry.class.getName());
			IPlatformSelector selector = (IPlatformSelector)execution
					.lookupService(IPlatformSelector.class.getName());
			if (selector == null)
				return null;
			platform = selector.getSelectedPlatform();
			if (platform == null)
				return null;
			command = new EndMessageCommand();
			urlEncoded = true;
			return platform.createDocument(this, this);
		}
		finally
		{
			platform = null;
			interactionTypeRegistry = null;
			execution.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecutionDescriptor#getExecutionID()
	 */
	public String getExecutionID()
	{
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecutionDescriptor#
	 *      getParameterNames()
	 */
	public String[] getParameterNames()
	{
		return (String[])parameters.keySet().toArray(new String[parameters.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecutionDescriptor#getParameter(
	 *      java.lang.String)
	 */
	public String getParameter(String parameterName) throws NullPointerException
	{
		String[] values = getParameters(parameterName);
		return values == null || values.length == 0 ? null : values[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecutionDescriptor#getParameters(
	 *      java.lang.String)
	 */
	public String[] getParameters(String parameterName)
			throws NullPointerException
	{
		String[] values = (String[])parameters.get(parameterName);
		if (values == null || values.length == 0)
			return null;
		String[] copy = new String[values.length];
		System.arraycopy(values, 0, copy, 0, values.length);
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecutionDescriptor#setParameter(
	 *      java.lang.String, java.lang.String)
	 */
	public void setParameter(String parameterName, String value)
			throws NullPointerException
	{
		setParameters(parameterName, value == null ? null : new String[] { value });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecutionDescriptor#setParameters(
	 *      java.lang.String, java.lang.String[])
	 */
	public void setParameters(String parameterName, String[] values)
			throws NullPointerException
	{
		if (values == null || values.length == 0)
			clearParameter(parameterName);
		else
		{
			String[] copy = new String[values.length];
			System.arraycopy(values, 0, copy, 0, values.length);
			parameters.put(parameterName, copy);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecutionDescriptor#clearParameter(
	 *      java.lang.String)
	 */
	public void clearParameter(String parameterName) throws NullPointerException
	{
		parameters.remove(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecutionDescriptor#
	 *      getServiceIdentifiers()
	 */
	public String[] getServiceIdentifiers()
	{
		return new String[] { IInteractionTypeSelection.class.getName() };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IExecutionDescriptor#getService(
	 *      java.lang.String)
	 */
	public Object getService(String identifier) throws NullPointerException
	{
		return IInteractionTypeSelection.class.getName().equals(identifier) ? this
				: null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory#
	 *      setUrlEncoded(boolean)
	 */
	public void setUrlEncoded(boolean urlEncoded)
	{
		this.urlEncoded = urlEncoded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory#
	 *      createAbortLink()
	 */
	public ILink createAbortLink()
	{
		return new Link(HttpConnector.ABORT_PATH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory#
	 *      createNextLink()
	 */
	public ILink createNextLink()
	{
		return new Link(HttpConnector.NEXT_PATH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory#
	 *      createResourceLink(java.lang.String)
	 */
	public ILink createResourceLink(String path)
	{
		if (path == null)
			return null;
		else if(path.startsWith("http://"))
			return new Link(path, false);
		else if(path.startsWith("dtmf:"))
			return new Link(path, false);
		else if (path.startsWith("/")) //$NON-NLS-1$
			return new Link(HttpConnector.RESOURCES_PATH + path, false);
		else
			return new Link(HttpConnector.RESOURCES_PATH + "/" + path, false); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.IRenderingQueue#
	 *      isEmpty()
	 */
	public boolean isEmpty()
	{
		return command == null || !(command instanceof ConversationCommand);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.IRenderingQueue#
	 *      peek()
	 */
	public ConversationCommand peek()
	{
		if (isEmpty())
			return null;
		return (ConversationCommand)command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.IRenderingQueue#
	 *      next()
	 */
	public ConversationCommand next()
	{
		if (isEmpty())
			return null;
		ConversationCommand command = (ConversationCommand)this.command;
		this.command = null;
		return command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IRunnableCommandVisitor#
	 *      visitRunnable(org.eclipse.vtp.framework.spi.IRunnableCommand)
	 */
	public Object visitRunnable(IRunnableCommand runnableCommand)
			throws NullPointerException
	{
		runnableCommand.run();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.commands.IControllerCommandVisitor#
	 *      visitInclude(org.eclipse.vtp.framework.common.commands.IncludeCommand)
	 */
	public Object visitInclude(IncludeCommand includeCommand)
	{
		ControllerDocument doc = new ControllerDocument();
		doc.setTarget(includeCommand.getTargetProcessURI());
		String[] targetNames = includeCommand.getVariableNames();
		for (int i = 0; i < targetNames.length; ++i)
		{
			doc.setVariableValue(targetNames[i], exportVariable(variableRegistry
					.getVariable(includeCommand.getVariableValue(targetNames[i]))));
		}
		String[] outgoing = includeCommand.getOutgoingPaths();
		for (int i = 0; i < outgoing.length; ++i)
		{
			String[] names = includeCommand.getOutgoingDataNames(outgoing[i]);
			for (int j = 0; j < names.length; ++j)
				doc.setOutgoingDataValue(outgoing[i], names[j], includeCommand
						.getOutgoingDataValue(outgoing[i], names[j]));
		}
		String[] paramNames = includeCommand.getParameterNames();
		for (int i = 0; i < paramNames.length; ++i)
			doc.setParameterValues(paramNames[i], includeCommand
					.getParameterValues(paramNames[i]));
		return doc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.commands.IControllerCommandVisitor#
	 *      visitExit(org.eclipse.vtp.framework.common.commands.ExitCommand)
	 */
	public Object visitExit(ExitCommand exitCommand)
	{
		ControllerDocument doc = new ControllerDocument();
		doc.setParameterValues("exit", new String[] { exitCommand.getExitValue() });
		String[] vars = exitCommand.getVariableNames();
		for (int i = 0; i < vars.length; ++i)
			doc.setVariableValue(vars[i], exportVariable(variableRegistry
					.getVariable(vars[i])));
		return doc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.commands.IControllerCommandVisitor#
	 *      visitForward(org.eclipse.vtp.framework.common.commands.ForwardCommand)
	 */
	public Object visitForward(ForwardCommand forwardCommand)
	{
		// TODO
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommandVisitor#
	 *      visitUnknown(org.eclipse.vtp.framework.spi.ICommand)
	 */
	public Object visitUnknown(ICommand unknownCommand)
			throws NullPointerException
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.IInteractionTypeSelection#
	 *      getSelectedInteractionType()
	 */
	public IInteractionType getSelectedInteractionType()
	{
		if (interactionTypeRegistry == null)
			return null;
		if (platform == null)
			return null;
		return interactionTypeRegistry.getInteractionType(platform
				.getInteractionTypeID());
	}

	private Map exportVariable(IDataObject variable)
	{
		if (variable == null)
			return null;
		Map data = new HashMap();
		data.put(null, variable.getType().getName());
		data.put("OBJECT_ID", variable.getId());
		if (variable instanceof IArrayObject)
		{
			IArrayObject array = (IArrayObject)variable;
			Object[] elements = new Object[array.getLength().getValue().intValue()];
			for (int i = 0; i < elements.length; ++i)
				elements[i] = exportVariable(array.getElement(i));
			data.put("elements", elements);
		}
		else if (variable instanceof IMapObject)
		{
			IMapObject map = (IMapObject)variable;
			Map<String, IDataObject> values = map.getValues();
			for(Map.Entry<String, IDataObject> entry : values.entrySet())
			{
				data.put(entry.getKey(), exportVariable(entry.getValue()));
			}
		}
		else if (variable instanceof IBooleanObject)
			data.put("value", ((IBooleanObject)variable).getValue());
		else if (variable instanceof IDateObject)
			data.put("value", ((IDateObject)variable).getValue());
		else if (variable instanceof IDecimalObject)
			data.put("value", ((IDecimalObject)variable).getValue());
		else if (variable instanceof INumberObject)
			data.put("value", ((INumberObject)variable).getValue());
		else if (variable instanceof IStringObject)
			data.put("value", ((IStringObject)variable).getValue());
		else
		{
			String[] fields = variable.getType().getFieldNames();
			for (int i = 0; i < fields.length; ++i)
				data.put(fields[i], exportVariable(variable.getField(fields[i])));
		}
		return data;
	}

	/**
	 * Implementation of {@link ILink}
	 * 
	 * @author Lonnie Pryor
	 */
	private final class Link implements ILink
	{
		/** The link path. */
		private final String path;
		/** The link parameters. */
		private final Map parameters = new HashMap();

		/**
		 * Creates a new Link.
		 * 
		 * @param path The link path.
		 */
		Link(String path)
		{
			this(path, true);
		}
		
		Link(String path, boolean appendMode)
		{
			if(path.startsWith("http://") || path.startsWith("dtmf:"))
			{
				this.path = path;
				return;
			}
			StringBuffer buffer = new StringBuffer();
			String contextPath = HttpUtils
					.normalizePath(httpRequest.getContextPath());
			if (!contextPath.equals("/")) //$NON-NLS-1$
				buffer.append(contextPath);
			String servletPath = HttpUtils
					.normalizePath(httpRequest.getServletPath());
			if (!servletPath.equals("/")) //$NON-NLS-1$
				buffer.append(servletPath);
			for (StringTokenizer st = new StringTokenizer(HttpUtils
					.normalizePath(path), "/"); st.hasMoreTokens();) //$NON-NLS-1$
			{
				try
				{
					String token = URLEncoder.encode(st.nextToken(), "UTF-8"); //$NON-NLS-1$
					StringBuffer b = new StringBuffer();
					for (int i = 0; i < token.length(); ++i)
					{
						if (token.charAt(i) == '+')
							b.append("%20"); //$NON-NLS-1$
						else
							b.append(token.charAt(i));
					}
					buffer.append('/').append(b.toString());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			this.path = buffer.toString();
			if(appendMode)
			{
				String[] mode = getParameters("MODE"); //$NON-NLS-1$
				if (mode != null && mode.length > 0)
					setParameters("MODE", mode); //$NON-NLS-1$
			}
		}

		/**
		 * Encodes a URL.
		 * 
		 * @param The URL to encode;
		 * @return The encoded URL.
		 */
		private String encode(String url)
		{
			String encoded = null;
			if (urlEncoded)
				encoded = httpResponse.encodeURL(url);
			else if (url.startsWith("/")
					&& !url.startsWith(httpRequest.getContextPath() + "/"))
				encoded = httpRequest.getContextPath() + url;
			else
				encoded = url;
			return encoded;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.platforms.ILink#
		 *      setParameter(java.lang.String, java.lang.String)
		 */
		public void setParameter(String parameterName, String parameterValue)
		{
			setParameters(parameterName, parameterValue == null ? null
					: new String[] { parameterValue });
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.platforms.ILink#
		 *      setParameters(java.lang.String, java.lang.String[])
		 */
		public void setParameters(String parameterName, String[] parameterValue)
		{
			if (parameterName == null)
				return;
			if (parameterValue == null)
				parameters.remove(parameterName);
			else
				parameters.put(parameterName, parameterValue);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			if (parameters.isEmpty())
				return encode(path);
			StringBuffer buffer = new StringBuffer(path).append('?');
			for (Iterator i = parameters.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry entry = (Map.Entry)i.next();
				String name = (String)entry.getKey();
				String[] values = (String[])entry.getValue();
				if (values.length == 0)
					continue;
				try
				{
					buffer.append(URLEncoder.encode(name, "UTF-8")); //$NON-NLS-1$
					buffer.append('=');
					for (int j = 0; j < values.length; ++j)
					{
						if (j > 0)
							buffer.append(',');
						buffer.append(URLEncoder.encode(values[j], "UTF-8")); //$NON-NLS-1$
					}
					buffer.append('&');
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			buffer.setLength(buffer.length() - 1);
			return encode(buffer.toString());
		}
	}
}
