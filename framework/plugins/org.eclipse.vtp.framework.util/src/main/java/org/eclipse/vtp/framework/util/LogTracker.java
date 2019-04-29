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
package org.eclipse.vtp.framework.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

/**
 * A class that forwards to the most desirable log service when one is
 * available.
 * 
 * @author Lonnie Pryor
 */
@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
public class LogTracker extends SingletonTracker implements LogService {
	/**
	 * Creates a new Log.
	 * 
	 * @param context The context to operate under.
	 */
	public LogTracker(BundleContext context) {
		super(context, LogService.class.getName(), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.log.LogService#log(int, java.lang.String)
	 */
	public void log(int level, String message) {
		LogService log = (LogService) getService();
		if (log != null)
			log.log(level, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.log.LogService#log(int, java.lang.String,
	 * java.lang.Throwable)
	 */
	public void log(int level, String message, Throwable thrown) {
		LogService log = (LogService) getService();
		if (log != null)
			log.log(level, message, thrown);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.log.LogService#log(
	 * org.osgi.framework.ServiceReference, int, java.lang.String)
	 */
	public void log(ServiceReference reference, int level, String message) {
		LogService log = (LogService) getService();
		if (log != null)
			log.log(reference, level, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.log.LogService#log(
	 * org.osgi.framework.ServiceReference, int, java.lang.String,
	 * java.lang.Throwable)
	 */
	public void log(ServiceReference reference, int level, String message, Throwable thrown) {
		LogService log = (LogService) getService();
		if (log != null)
			log.log(reference, level, message, thrown);
	}

	public Logger getLogger(String message) {
		return null;
	}

	public Logger getLogger(Class clazz) {
		return null;
	}

	public Logger getLogger(String message, Class clazz) {
		return null;
	}

	public Logger getLogger(Class clazz1, Class clazz2) {
		return null;
	}

	public Logger getLogger(Bundle bundle, String message, Class clazz) {
		return null;
	}

}
