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
package org.eclipse.vtp.framework.engine.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.engine.support.AbstractReporter;
import org.eclipse.vtp.framework.util.LogTracker;
import org.eclipse.vtp.framework.util.StaticConfigurationAdmin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.w3c.dom.Document;

/**
 * Activator for the framework, manages an instance of {@link LogTracker},
 * {@link StaticConfigurationAdmin} (if configuration data is available), and
 * {@link ProcessEngineManager}.
 * 
 * @author Lonnie Pryor
 * @version 1.0
 * @since 3.0
 */
public class Activator extends AbstractReporter implements BundleActivator,
		LogListener, ServiceTrackerCustomizer
{
	private static final String[] REPORT_LEVELS = { "ERROR", "WARN", "INFO",
			"DEBUG" };

	/** The service tracker that tracks the log service. */
	private BundleContext context = null;
	/** The service tracker that tracks the log service. */
	private LogTracker log = null;
	/** The static configuration admin instance. */
	private StaticConfigurationAdmin configurationAdmin = null;
	/** The service tracker that tracks the extension registry service. */
	private ProcessEngineManager processEngineManager = null;
	/** The service tracker that tracks the log reader service. */
	private ServiceTracker logReader = null;
	/** The service tracker that tracks the reporter services. */
	private ServiceTracker reporters = null;
	private int activeLogLevel = 3;

	/**
	 * Creates a new Activator.
	 */
	public Activator()
	{
	}

	/**
	 * Builds a report .
	 * 
	 * @param severity
	 * @param categories
	 * @param message
	 * @param properties
	 * @return
	 */
	private String buildReport(int severity, String[] categories, String message,
			Dictionary properties)
	{
		// Create the line prefix.
		StringBuffer buffer = new StringBuffer(REPORT_LEVELS[Math.max(0, Math.min(
				REPORT_LEVELS.length - 1, severity - 1))]);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		buffer.append('|').append(calendar.get(Calendar.YEAR));
		buffer.append('/').append(format(calendar.get(Calendar.MONTH) + 1, 2));
		buffer.append('/').append(format(calendar.get(Calendar.DATE), 2));
		buffer.append('|').append(format(calendar.get(Calendar.HOUR_OF_DAY), 2));
		buffer.append(':').append(format(calendar.get(Calendar.MINUTE), 2));
		buffer.append(':').append(format(calendar.get(Calendar.SECOND), 2));
		Object id = properties.remove("process.id"); //$NON-NLS-1$
		if (id != null)
			buffer.append('|').append("p=").append(id); //$NON-NLS-1$
		id = properties.remove("session.id"); //$NON-NLS-1$
		if (id != null)
			buffer.append('|').append("s=").append(id); //$NON-NLS-1$
		id = properties.remove("execution.id"); //$NON-NLS-1$
		if (id != null)
			buffer.append('|').append("e=").append(format(id.toString(), 4)); //$NON-NLS-1$
		id = properties.remove("action.id"); //$NON-NLS-1$
		// if (id != null)
		// buffer.append('|').append("a=").append(id); //$NON-NLS-1$
		buffer.append('>');
		String prefix = buffer.toString();
		// Write the report.
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		printWriter.print(prefix);
		printWriter.print(' ');
		printWriter.print(message);
		// Append a line listing the categories, if any.
//		if (categories != null)
//		{
//			printWriter.println();
//			printWriter.print(prefix);
//			printWriter.print("\tcategories:"); //$NON-NLS-1$
//			for (int i = 0; i < categories.length; ++i)
//			{
//				if (categories[i] != null)
//				{
//					printWriter.print(' ');
//					printWriter.print(categories[i]);
//				}
//			}
//		}
		if (properties != null)
		{
			Object t = properties.get("cause"); //$NON-NLS-1$
			if (t instanceof Throwable)
				properties.remove("cause"); //$NON-NLS-1$
			else
				t = null;
//			if (!properties.isEmpty())
//			{
//				printWriter.println();
//				printWriter.print(prefix);
//				printWriter.print("\tproperties:"); //$NON-NLS-1$
//				for (Enumeration e = properties.keys(); e.hasMoreElements();)
//				{
//					Object key = e.nextElement();
//					Object value = properties.get(key);
//					printWriter.println();
//					printWriter.print(prefix);
//					printWriter.print("\t\t"); //$NON-NLS-1$
//					printWriter.print(key);
//					printWriter.print('=');
//					printWriter.print(value);
//				}
//			}
			if (t != null)
			{
				StringWriter causeStringWriter = new StringWriter();
				PrintWriter causePrintWriter = new PrintWriter(causeStringWriter);
				((Throwable)t).printStackTrace(causePrintWriter);
				causePrintWriter.flush();
				for (StringTokenizer st = new StringTokenizer(causeStringWriter
						.toString(), "\r\n"); st.hasMoreTokens();) //$NON-NLS-1$
				{
					printWriter.println();
					printWriter.print(prefix);
					printWriter.print('\t');
					printWriter.print(st.nextToken());
				}
			}
		}
		return stringWriter.toString();
	}

	private String format(int number, int minLength)
	{
		return format(String.valueOf(number), minLength);
	}

	private String format(String number, int minLength)
	{
		StringBuffer buffer = new StringBuffer(minLength);
		for (int i = number.length(); i < minLength; ++i)
			buffer.append(0);
		return buffer.append(number).toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#isSeverityEnabled(int)
	 */
	public boolean isSeverityEnabled(int severity)
	{
		return severity <= activeLogLevel;
	}
	
	public boolean isReportingEnabled()
	{
		return reporters.size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.engine.support.AbstractReporter#doReport(
	 *      int, java.lang.String[], java.lang.String, java.util.Dictionary)
	 */
	protected void doReport(int severity, String[] categories, String message,
			Dictionary properties)
	{
		Object[] reporters = this.reporters.getServices();
		if (properties == null)
			properties = new Hashtable();
		if (properties.get("scope") == null) //$NON-NLS-1$
			properties.put("scope", "host"); //$NON-NLS-1$ //$NON-NLS-2$
		Object type = properties.get("type"); //$NON-NLS-1$
		if (type == null || "report".equalsIgnoreCase(type.toString())) { //$NON-NLS-1$
			properties.put("type", type = "report"); //$NON-NLS-1$ //$NON-NLS-2$
			if (properties.get("time") == null) //$NON-NLS-1$
				properties.put("time", //$NON-NLS-1$
						DateFormat.getDateTimeInstance().format(new Date()));
			if (reporters != null)
				for (int i = 0; i < reporters.length; ++i)
					((IReporter)reporters[i]).report(severity, categories, message,
							properties);
		}
		else if (!"log".equalsIgnoreCase(type.toString())) //$NON-NLS-1$
			return;
		System.out.println(buildReport(severity, categories, message, properties));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(
	 *      org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		Thread t = new Thread(new Runnable(){
			public void run()
			{
				try
				{
					while(true)
					{
						String level = System.getProperty("org.eclipse.vtp.loglevel", "INFO");
						boolean found = false;
						for(int i = 0; i <REPORT_LEVELS.length; i++)
						{
							String l = REPORT_LEVELS[i];
							if(l.equalsIgnoreCase(level))
							{
								activeLogLevel = i + 1;
								found = true;
								break;
							}
						}
						if(!found)
							activeLogLevel = 3;
						Thread.sleep(10000);
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		t.setDaemon(true);
		t.start();
		this.context = context;
		logReader = new ServiceTracker(context, LogReaderService.class.getName(),
				this);
		logReader.open();
		reporters = new ServiceTracker(context, IReporter.class.getName(), null);
		reporters.open();
		log = new LogTracker(context);
		log.open();
		URL staticConfig = context.getBundle().getResource("META-INF/services/" //$NON-NLS-1$
				+ StaticConfigurationAdmin.class.getName());
		if (staticConfig != null)
		{
			log.log(LogService.LOG_DEBUG, "Loading static configuration...");
			Document document = null;
			InputStream input = null;
			try
			{
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(false);
				dbf.setValidating(false);
				document = dbf.newDocumentBuilder().parse(
						input = staticConfig.openStream());
				log.log(LogService.LOG_INFO, "Loaded static configuration from \""
						+ staticConfig.toExternalForm() + "\".");
			}
			catch (Exception e)
			{
				log.log(LogService.LOG_WARNING, "Failed to load static configuration: "
						+ e.getMessage(), e);
			}
			finally
			{
				try
				{
					if (input != null)
						input.close();
				}
				catch (IOException e)
				{
				}
			}
			if (document != null)
			{
				log.log(LogService.LOG_DEBUG, "Creating configuration admin...");
				configurationAdmin = new StaticConfigurationAdmin(context, log,
						document.getDocumentElement());
				configurationAdmin.start();
				log.log(LogService.LOG_DEBUG, "Configuration admin created.");
			}
		}
		log.log(LogService.LOG_DEBUG, "Creating process engine manager...");
		processEngineManager = new ProcessEngineManager(context, log, this);
		processEngineManager.open();
		log.log(LogService.LOG_DEBUG, "Process engine manager created.");
		Dictionary report = new Hashtable();
		report.put("event", "host.started");
		report(IReporter.SEVERITY_INFO, "Host Started", report);
		context.registerService("org.eclipse.osgi.framework.console.CommandProvider", new LogLevelCommandListener(), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(
	 *      org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		Dictionary report = new Hashtable();
		report.put("event", "host.stopped");
		report(IReporter.SEVERITY_INFO, "Host Stopped", report);
		try
		{
			if (processEngineManager != null)
			{
				log.log(LogService.LOG_DEBUG, "Releasing process engine manager...");
				processEngineManager.close();
			}
		}
		finally
		{
			if (processEngineManager != null)
			{
				processEngineManager = null;
				log.log(LogService.LOG_DEBUG, "Process engine manager released.");
			}
			try
			{
				if (configurationAdmin != null)
				{
					log.log(LogService.LOG_DEBUG, "Releasing configuration admin...");
					configurationAdmin.stop();
				}
			}
			finally
			{
				if (configurationAdmin != null)
				{
					configurationAdmin = null;
					log.log(LogService.LOG_DEBUG, "Configuration admin released.");
				}
				try
				{
					if (log != null)
						log.close();
				}
				finally
				{
					if (log != null)
						log = null;
					reporters.close();
					reporters = null;
					logReader.close();
					logReader = null;
					this.context = null;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.log.LogListener#logged(org.osgi.service.log.LogEntry)
	 */
	public synchronized void logged(LogEntry entry)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(
	 *      org.osgi.framework.ServiceReference)
	 */
	public Object addingService(ServiceReference reference)
	{
		LogReaderService logReader = (LogReaderService)context
				.getService(reference);
		Enumeration log = logReader.getLog();
		if (log != null)
			while (log.hasMoreElements())
				logged((LogEntry)log.nextElement());
		logReader.addLogListener(this);
		return logReader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(
	 *      org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void modifiedService(ServiceReference reference, Object service)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(
	 *      org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void removedService(ServiceReference reference, Object service)
	{
		((LogReaderService)context.getService(reference)).removeLogListener(this);
		context.ungetService(reference);
	}

	public class LogLevelCommandListener implements CommandProvider
	{
		public void _vtplogging(CommandInterpreter ci)
		{
			String command = ci.nextArgument();
			if(command == null)
			{
				ci.println("Usage: vtplogging getLevel");
				ci.println("Usage: vtplogging setLevel level(ERROR | WARN | INFO | DEBUG)");
				return;
			}
			if("getLevel".equalsIgnoreCase(command))
			{
				ci.println("Current Log Level: " + REPORT_LEVELS[activeLogLevel - 1]);
				return;
			}
			String level = ci.nextArgument();
			if(level == null || (!level.equalsIgnoreCase("ERROR") && !level.equalsIgnoreCase("WARN") && !level.equalsIgnoreCase("INFO") && !level.equalsIgnoreCase("DEBUG")))
			{
				ci.println("Usage: vtplogging getLevel");
				ci.println("Usage: vtplogging setLevel level(ERROR | WARN | INFO | DEBUG)");
				return;
			}
			if("setLevel".equalsIgnoreCase(command))
			{
				System.setProperty("org.eclipse.vtp.loglevel", level);
				return;
			}
			ci.println("Usage: vtplogging getLevel");
			ci.println("Usage: vtplogging setLevel level(ERROR | WARN | INFO | DEBUG)");
			return;
		}

		public String getHelp()
        {
	        // TODO Auto-generated method stub
	        return null;
        }
		
	}
}
