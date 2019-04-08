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
package org.eclipse.vtp.framework.javascript;

import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.common.IScriptingContext;
import org.eclipse.vtp.framework.common.IScriptingEngine;
import org.eclipse.vtp.framework.common.ScriptingException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Implementation of the JavaScript context.
 * 
 * @author Lonnie Pryor
 */
public class JavaScriptContext implements IScriptingContext
{
	/** The application class loader. */
	private final ClassLoader applicationClassLoader;
	/** The scope of this context. */
	private final ScriptableObject scope;

	/**
	 * Creates a new JavaScriptContext.
	 * 
	 * @param applicationClassLoader The application class loader.
	 * @param scriptingLanguage The scripting language that was requested.
	 * @param scriptables The scriptables to initialize.
	 */
	public JavaScriptContext(ClassLoader applicationClassLoader,
			String scriptingLanguage, IScriptable[] scriptables)
	{
		this.applicationClassLoader = applicationClassLoader;
		Context ctx = Context.enter();
		try
		{
			scope = new Scope(ctx.initStandardObjects(), scriptables);
		}
		finally
		{
			Context.exit();
		}
	}

	/**
	 * Creates a new JavaScriptContext.
	 * 
	 * @param scriptables The scriptables to initialize.
	 * @param owner The owner of this context.
	 */
	public JavaScriptContext(IScriptable[] scriptables, JavaScriptContext owner)
	{
		applicationClassLoader = owner.applicationClassLoader;
		Context.enter();
		try
		{
			scope = new Scope(owner.scope, scriptables);
		}
		finally
		{
			Context.exit();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptingContext#
	 *      createScriptingContext(
	 *      org.eclipse.vtp.framework.spi.scripting.IScriptable[])
	 */
	public IScriptingContext createScriptingContext(IScriptable[] content)
	{
		return new JavaScriptContext(content, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptingContext#createScriptingEngine()
	 */
	public IScriptingEngine createScriptingEngine()
	{
		return new ScriptingEngine();
	}

	/**
	 * Implementation of {@link IScriptingEngine}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ScriptingEngine implements IScriptingEngine
	{
		/** The scriptable instance for this engine. */
		private Scriptable instance = null;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptingEngine#execute(
		 *      java.lang.String)
		 */
		public Object execute(String script)
		{
			Context ctx = Context.enter();
			ctx.setOptimizationLevel(-1);
			try
			{
				ctx.setApplicationClassLoader(applicationClassLoader);
				if (instance == null)
				{
					instance = ctx.newObject(scope);
					instance.setPrototype(scope);
					instance.setParentScope(null);
				}
				return JavaScriptObject.jsToJava(ctx.evaluateString(instance, script,
						"<script>", 0, null)); //$NON-NLS-1$
			}
			catch(JavaScriptException jse)
			{
				if(jse.getValue() instanceof ScriptableObject)
				{
					ScriptableObject error = (ScriptableObject)jse.getValue();
					Object titleObj = ScriptableObject.getProperty(error, "name");
					Object descriptionObj = ScriptableObject.getProperty(error, "message");
					throw new ScriptingException(JavaScriptObject.jsToJava(titleObj).toString(), JavaScriptObject.jsToJava(descriptionObj).toString(), jse);
				}
				throw jse;
			}
			finally
			{
				Context.exit();
			}
		}
	}

	/**
	 * Scope implementation.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class Scope extends ScriptableObject
	{
		/** Comment for serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Creates a new Scope.
		 * 
		 * @param prototype The prototype of this scope.
		 * @param scriptables The set of scriptables within the new scope
		 */
		Scope(Scriptable prototype, IScriptable[] scriptables)
		{
			setPrototype(prototype);
			setParentScope(null);
			for (int i = 0; i < scriptables.length; ++i)
				defineProperty(scriptables[i].getName(), new JavaScriptObject(
						scriptables[i]), ScriptableObject.PERMANENT
						| ScriptableObject.READONLY);
			sealObject();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.mozilla.javascript.ScriptableObject#getClassName()
		 */
		public String getClassName()
		{
			return getClass().getName();
		}
	}
}
