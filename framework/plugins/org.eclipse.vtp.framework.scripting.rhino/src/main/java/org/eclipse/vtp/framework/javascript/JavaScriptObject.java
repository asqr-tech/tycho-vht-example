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

import java.util.Calendar;

import org.eclipse.vtp.framework.common.IScriptable;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Wrapper;

/**
 * A Rhino host for implementations of {@link IScriptable}.
 * 
 * @author Lonnie Pryor
 */
public class JavaScriptObject extends ScriptableObject implements Wrapper
{
	/** Comment for serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Converts a Java object into a JavaScript object.
	 * 
	 * @param object Java The object to convert.
	 * @param start The top scope object used for conversion.
	 * @return The converted JavaScript object.
	 */
	protected static Object javaToJS(Object object, Scriptable start)
	{
		if (object == null)
			return Context.getUndefinedValue();
		if (object instanceof Object[])
		{
			Object[] input = (Object[])object;
			Scriptable output = Context.getCurrentContext().newArray(start,
					input.length);
			for (int i = 0; i < input.length; ++i)
				output.put(i, start, javaToJS(input[i], start));
			return output;
		}
		if (object instanceof IScriptable)
			return new JavaScriptObject((IScriptable)object);
		if(object instanceof Calendar)
			return object;
		return Context.javaToJS(object, start);
	}

	/**
	 * Converts a JavaScript object into a Java string.
	 * 
	 * @param object JavaScript The object to convert.
	 * @return The converted Java string.
	 */
	protected static Object jsToJava(Object object)
	{
		if (object == null)
			return null;
		if (object.equals(Context.getUndefinedValue()))
			return null;
		if(object instanceof Calendar)
			return object;
		if (object instanceof JavaScriptObject)
			return ((JavaScriptObject)object).scriptable;
		if(object instanceof NativeJavaObject)
		{
			return ((NativeJavaObject)object).unwrap();
		}
		if (object instanceof Scriptable)
		{
			Scriptable input = (Scriptable)object;
			int count = 0;
			while (input.has(count, input))
				++count;
			if (count > 1)
			{
				Object[] output = new Object[count];
				for (int i = 0; i < count; ++i)
					output[i] = jsToJava(input.get(i, input));
				return output;
			}
		}
		return Context.jsToJava(object, String.class);
	}

	/** The scriptable to manage. */
	private final IScriptable scriptable;
	/** The names of the functions. */
	private final String[] functionNames;

	/**
	 * Creates a new ScriptableHost.
	 * 
	 * @param scriptable The scriptable to manage.
	 */
	public JavaScriptObject(IScriptable scriptable)
	{
		this.scriptable = scriptable;
		functionNames = scriptable.getFunctionNames();
		if (functionNames != null)
			for (int i = 0; i < functionNames.length; ++i)
				if (functionNames[i] != null)
					defineProperty(functionNames[i], new Function(functionNames[i]),
							ScriptableObject.PERMANENT | ScriptableObject.READONLY | ScriptableObject.DONTENUM);
		if (scriptable.hasValue())
			defineProperty("valueOf", //$NON-NLS-1$
					new Function("valueOf"), //$NON-NLS-1$
					ScriptableObject.PERMANENT | ScriptableObject.READONLY | ScriptableObject.DONTENUM);
		if(!scriptable.isMutable())
			sealObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozilla.javascript.Wrapper#unwrap()
	 */
	public Object unwrap()
	{
		return scriptable;
	}
	
	@Override
	public Object[] getIds()
	{
		Object[] parentIds = super.getIds();
		Object[] ids = scriptable.getPropertyNames();
		Object[] allIds = new Object[parentIds.length + ids.length];
		System.arraycopy(parentIds, 0, allIds, 0, parentIds.length);
		System.arraycopy(ids, 0, allIds, parentIds.length, ids.length);
		return allIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozilla.javascript.ScriptableObject#has(int,
	 *      org.mozilla.javascript.Scriptable)
	 */
	public boolean has(int index, Scriptable start)
	{
		return scriptable.hasItem(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozilla.javascript.ScriptableObject#has(java.lang.String,
	 *      org.mozilla.javascript.Scriptable)
	 */
	public boolean has(String name, Scriptable start)
	{
		return super.has(name, start) || scriptable.hasEntry(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozilla.javascript.ScriptableObject#get(int,
	 *      org.mozilla.javascript.Scriptable)
	 */
	public Object get(int index, Scriptable start)
	{
		return javaToJS(scriptable.getItem(index), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozilla.javascript.ScriptableObject#get(java.lang.String,
	 *      org.mozilla.javascript.Scriptable)
	 */
	public Object get(String name, Scriptable start)
	{
		if (super.has(name, start))
			return super.get(name, start);
		return javaToJS(scriptable.getEntry(name), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozilla.javascript.ScriptableObject#put(int,
	 *      org.mozilla.javascript.Scriptable, java.lang.Object)
	 */
	public void put(int index, Scriptable start, Object value)
	{
		scriptable.setItem(index, jsToJava(value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozilla.javascript.ScriptableObject#put(java.lang.String,
	 *      org.mozilla.javascript.Scriptable, java.lang.Object)
	 */
	public void put(String name, Scriptable start, Object value)
	{
		if (super.has(name, start))
			return;
		if ("valueOf".equals(name)) //$NON-NLS-1$
		{
			super.put(name, start, value);
			return;
		}
		for (int i = 0; i < functionNames.length; ++i)
		{
			if (functionNames[i].equals(name))
			{
				super.put(name, start, value);
				return;
			}
		}
		scriptable.setEntry(name, jsToJava(value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozilla.javascript.ScriptableObject#delete(int)
	 */
	public void delete(int index)
	{
		scriptable.clearItem(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mozilla.javascript.ScriptableObject#delete(java.lang.String)
	 */
	public void delete(String name)
	{
		if (!super.has(name, this))
			scriptable.clearEntry(name);
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

	/**
	 * A function on a scriptable.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class Function extends BaseFunction
	{
		/** Comment for serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The name of the function. */
		private final String name;

		/**
		 * Creates a new FunctionInstance.
		 * 
		 * @param name The name of the function.
		 */
		Function(String name)
		{
			this.name = name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.mozilla.javascript.BaseFunction#getFunctionName()
		 */
		public String getFunctionName()
		{
			return name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.mozilla.javascript.BaseFunction#call(org.mozilla.javascript.Context,
		 *      org.mozilla.javascript.Scriptable,
		 *      org.mozilla.javascript.Scriptable, java.lang.Object[])
		 */
		public Object call(Context ctx, Scriptable scope, Scriptable thisObj,
				Object[] args)
		{
			if ("valueOf".equals(name)) //$NON-NLS-1$
			{
				Object value = scriptable;
				while (value != null && value instanceof IScriptable)
				{
					IScriptable scriptable = (IScriptable)value;
					if (scriptable.hasValue())
						value = scriptable.toValue();
					else
						value = null;
				}
				return javaToJS(value, this);
			}
			for (int i = 0; i < args.length; ++i)
				args[i] = jsToJava(args[i]);
			try
			{
				return javaToJS(scriptable.invokeFunction(name, args), this);
			}
			catch(Exception ex)
			{
				Context.throwAsScriptRuntimeEx(ex);
			}
			return null;
		}
	}
}
