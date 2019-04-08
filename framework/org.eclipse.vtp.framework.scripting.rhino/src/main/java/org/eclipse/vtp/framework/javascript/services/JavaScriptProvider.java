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
package org.eclipse.vtp.framework.javascript.services;

import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.common.IScriptingContext;
import org.eclipse.vtp.framework.common.IScriptingProvider;
import org.eclipse.vtp.framework.core.IProcessContext;
import org.eclipse.vtp.framework.javascript.JavaScriptContext;

/**
 * Implementation of the scripting provider for JavaScript.
 * 
 * @author Lonnie Pryor
 */
public class JavaScriptProvider implements IScriptingProvider
{
	/** The application class loader. */
	private final ClassLoader applicatioClassLoader;

	/**
	 * Creates a new JavaScriptProvider.
	 * 
	 * @param context The process context of this provider 
	 */
	public JavaScriptProvider(final IProcessContext context)
	{
		applicatioClassLoader = new ClassLoader()
		{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class loadClass(String name) throws ClassNotFoundException
			{
				return context.loadClass(name);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptingProvider#
	 *      createScriptingContext(java.lang.String,
	 *      org.eclipse.vtp.framework.spi.scripting.IScriptable[])
	 */
	public IScriptingContext createScriptingContext(String scriptingLanuage,
			IScriptable[] content)
	{
		return new JavaScriptContext(applicatioClassLoader, scriptingLanuage,
				content);
	}
}
