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
package org.eclipse.vtp.framework.engine.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Base class for components that make up a process.
 * 
 * @author Lonnie Pryor
 */
public abstract class Component
{
	/** A comparator that sorts constructors by number of arguments. */
	private static final Comparator CONSTRUCTOR_SORT = new Comparator()
	{
		public int compare(Object left, Object right)
		{
			return ((Constructor)right).getParameterTypes().length
					- ((Constructor)left).getParameterTypes().length;
		}
	};

	/** The blueprint of the process. */
	protected final Blueprint blueprint;
	/** The constructors for the component. */
	protected final Constructor[] constructors;
	/** The mutator methods of the component. */
	protected final Method[] mutators;

	/**
	 * Creates a new Component.
	 * 
	 * @param blueprint The blueprint of the process.
	 * @param type The type of the component.
	 * @throws NullPointerException If the supplied blueprint is <code>null</code>.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 */
	public Component(Blueprint blueprint, Class type) throws NullPointerException
	{
		if (blueprint == null)
			throw new NullPointerException("blueprint"); //$NON-NLS-1$
		if (type == null)
			throw new NullPointerException("type"); //$NON-NLS-1$
		this.blueprint = blueprint;
		this.constructors = type.getConstructors();
		Arrays.sort(this.constructors, CONSTRUCTOR_SORT);
		Method[] methods = type.getMethods();
		List mutators = new ArrayList(methods.length);
		for (int i = 0; i < methods.length; ++i)
		{
			if (!Void.TYPE.equals(methods[i].getReturnType()))
				continue;
			if (Modifier.isStatic(methods[i].getModifiers()))
				continue;
			String name = methods[i].getName();
			if (!name.startsWith("set")) //$NON-NLS-1$
				continue;
			if (name.length() < 4)
				continue;
			char c = name.charAt(3);
			if (!Character.isJavaIdentifierStart(c))
				continue;
			if (!Character.isUpperCase(c))
				continue;
			Class[] parameterTypes = methods[i].getParameterTypes();
			if (parameterTypes.length != 1)
				continue;
			mutators.add(methods[i]);
		}
		this.mutators = (Method[])mutators.toArray(new Method[mutators.size()]);
	}
}
