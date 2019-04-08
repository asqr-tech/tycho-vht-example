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
import java.util.Set;

import org.w3c.dom.Element;

/**
 * Utilities used by the various runtime components.
 * 
 * @author Lonnie Pryor
 */
public final class RuntimeUtils
{
	/**
	 * Generates a unique string from the supplied qualifier and identifier.
	 * 
	 * @param identifier The identifier to be qualified.
	 * @param qualifier The qualifier of the identifier.
	 * @return A unique string from the supplied name space and identifier.
	 * @throws NullPointerException If supplied identifier is <code>null</code>.
	 * @throws NullPointerException If supplied qualifier is <code>null</code>.
	 */
	public static String getQualifiedIdentifier(String identifier,
			String qualifier) throws NullPointerException
	{
		if (qualifier == null)
			throw new NullPointerException("qualifier"); //$NON-NLS-1$
		if (identifier == null)
			throw new NullPointerException("identifier"); //$NON-NLS-1$
		return new StringBuffer(identifier.length() + 1 + qualifier.length())
				.append(identifier).append(':').append(qualifier).toString();
	}

	/**
	 * Generates a unique string from the supplied element's name space URI and
	 * tag name.
	 * 
	 * @param element The element to generate the identifier for.
	 * @return A unique string from the supplied element's name space URI and tag
	 *         name.
	 * @throws NullPointerException If supplied element is <code>null</code>.
	 */
	public static String getXMLIdentifier(Element element)
			throws NullPointerException
	{
		if (element == null)
			throw new NullPointerException("element"); //$NON-NLS-1$
		return getQualifiedIdentifier(element.getLocalName(), element
				.getNamespaceURI());
	}

	/**
	 * Adds the specified type and all its super-types to the supplied set.
	 * 
	 * @param type The type to list the hierarchy of.
	 * @param types The set to populate with classes.
	 * @throws NullPointerException If the supplied set is <code>null</code>.
	 */
	public static void listTypeHierarchy(Class type, Set types)
			throws NullPointerException
	{
		if (type == null)
			return;
		if (types == null)
			throw new NullPointerException("types"); //$NON-NLS-1$
		listTypeHierarchy(type.getSuperclass(), types);
		Class[] interfaces = type.getInterfaces();
		if (interfaces != null)
			for (int i = 0; i < interfaces.length; ++i)
				listTypeHierarchy(interfaces[i], types);
		types.add(type);
	}

	/**
	 * Creates a new instance of the specified class via
	 * {@link Class#newInstance()}.
	 * 
	 * @param type The type of object to create.
	 * @return The new object instance.
	 * @throws IllegalStateException If the object creation fails.
	 * @throws NullPointerException If the supplied type is <code>null</code>.
	 */
	public static Object createInstance(Class type) throws IllegalStateException,
			NullPointerException
	{
		if (type == null)
			throw new NullPointerException("type"); //$NON-NLS-1$
		try
		{
			return type.newInstance();
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * Creates a new instance of the specified class via the supplied constructor.
	 * 
	 * @param constructor The constructor to create the object with.
	 * @param arguments The arguments to satisfy the constructor with.
	 * @return The new object instance.
	 * @throws IllegalArgumentException If the supplied constructor argument array
	 *           has more or less elements than the specified constructor does
	 *           arguments.
	 * @throws IllegalStateException If the object creation fails.
	 * @throws NullPointerException If the supplied constructor is
	 *           <code>null</code>.
	 * @throws NullPointerException If the supplied constructor argument array or
	 *           any of its elements are <code>null</code>.
	 */
	public static Object createInstance(Constructor constructor,
			Object[] arguments) throws IllegalStateException, NullPointerException
	{
		if (constructor == null)
			throw new NullPointerException("constructor"); //$NON-NLS-1$
		if (arguments == null)
			throw new NullPointerException("arguments"); //$NON-NLS-1$
		Class[] types = constructor.getParameterTypes();
		if (types.length != arguments.length)
			throw new IllegalArgumentException("arguments"); //$NON-NLS-1$
		for (int i = 0; i < arguments.length; ++i)
			if (arguments[i] == null)
				throw new NullPointerException("arguments[" //$NON-NLS-1$
						+ i + "]"); //$NON-NLS-1$
		try
		{
			return constructor.newInstance(arguments);
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * Sets a property on an object instance.
	 * 
	 * @param instance The instance to set the property on.
	 * @param mutator The property mutator method to invoke.
	 * @param value The value to set the property to.
	 * @throws IllegalArgumentException If the supplied mutator does not take
	 *           exactly one argument.
	 * @throws IllegalStateException If the object creation fails.
	 * @throws NullPointerException If the supplied instance is <code>null</code>.
	 * @throws NullPointerException If the supplied mutator is <code>null</code>.
	 */
	public static void setProperty(Object instance, Method mutator, Object value)
			throws IllegalStateException, NullPointerException
	{
		if (instance == null)
			throw new NullPointerException("instance"); //$NON-NLS-1$
		if (mutator == null)
			throw new NullPointerException("mutator"); //$NON-NLS-1$
		if (mutator.getParameterTypes().length != 1)
			throw new IllegalArgumentException("mutator"); //$NON-NLS-1$
		try
		{
			mutator.invoke(instance, new Object[] { value });
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
