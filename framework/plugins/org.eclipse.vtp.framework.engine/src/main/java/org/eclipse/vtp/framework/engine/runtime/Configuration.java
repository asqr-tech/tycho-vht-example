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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.eclipse.vtp.framework.core.IContext;
import org.eclipse.vtp.framework.engine.ConfigurationDescriptor;
import org.w3c.dom.Element;

/**
 * Represents a configuration on a service, action, or observer.
 * 
 * @author Lonnie Pryor
 */
public class Configuration extends Component
{
	/** The data this configuration is bound to. */
	private Element data;
	private IConfiguration instance;
	/** The identifiers of this configuration. */
	private final Set identifiers;

	/**
	 * Creates a new Configuration.
	 * 
	 * @param blueprint The blueprint of the process.
	 * @param descriptor The descriptor of this configuration.
	 * @param data The data this configuration is bound to.
	 * @throws NullPointerException If the supplied blueprint is <code>null</code>.
	 * @throws NullPointerException If the supplied descriptor is
	 *           <code>null</code>.
	 * @throws NullPointerException If the supplied data is <code>null</code>.
	 */
	public Configuration(Blueprint blueprint, ConfigurationDescriptor descriptor,
			Element data) throws NullPointerException
	{
		super(blueprint, descriptor.getType());
		if (descriptor == null)
			throw new NullPointerException("descriptor"); //$NON-NLS-1$
		if (data == null)
			throw new NullPointerException("data"); //$NON-NLS-1$
		this.data = data;
		Set typeHierarchy = new HashSet();
		RuntimeUtils.listTypeHierarchy(descriptor.getType(), typeHierarchy);
		typeHierarchy.remove(Object.class);
		Set identifiers = new HashSet(2 + typeHierarchy.size() * 2);
		identifiers.add(descriptor.getId());
		String xmlIdentifier = RuntimeUtils.getQualifiedIdentifier(descriptor
				.getXmlTag(), descriptor.getXmlNamespace());
		identifiers.add(xmlIdentifier);
		for (Iterator i = typeHierarchy.iterator(); i.hasNext();)
		{
			String typeName = ((Class)i.next()).getName();
			identifiers.add(typeName);
			identifiers.add(RuntimeUtils.getQualifiedIdentifier(typeName,
					xmlIdentifier));
		}
		this.identifiers = Collections.unmodifiableSet(identifiers);
	}

	/**
	 * Returns the identifiers of this configuration.
	 * 
	 * @return The identifiers of this configuration.
	 */
	public Set getIdentifiers()
	{
		return identifiers;
	}

	public void solidify(final IContext serviceRegistry)
	{
		if(instance != null)
			return;
		if (serviceRegistry == null)
			throw new NullPointerException("serviceRegistry"); //$NON-NLS-1$
		Builder builder = new Builder()
		{
			protected Constructor[] getConstructors()
			{
				return constructors;
			}

			protected Method[] getMutators()
			{
				return mutators;
			}

			protected IContext createServiceRegistry()
			{
				return serviceRegistry;
			}
		};
		instance = (IConfiguration)builder.create();
		if (instance != null)
		{
			builder.configure();
			instance.load(data);
		}
		data = null;
	}
	
	/**
	 * Creates a new instance of the configuration component.
	 * 
	 * @param serviceRegistry The inherited service registry to use.
	 * @return A new instance of the configuration component.
	 * @throws NullPointerException If the supplied service registry is
	 *           <code>null</code>.
	 */
	public Object createInstance(final IContext serviceRegistry)
			throws NullPointerException
	{
			return instance;
	}
}
