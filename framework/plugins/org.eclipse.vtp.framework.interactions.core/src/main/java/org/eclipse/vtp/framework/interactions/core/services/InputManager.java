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
package org.eclipse.vtp.framework.interactions.core.services;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.framework.interactions.core.media.IInputGrammarFactory;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;

/**
 * Implementation of {@link IInputGrammarFactory}.
 * 
 * @author Lonnie Pryor
 */
public class InputManager implements IInputGrammarFactory
{
	/** The input types. */
	// private final Map inputTypes;
	/** The input type index. */
	private final Map inputTypeIndex;

	/**
	 * Creates a new InputManager.
	 * 
	 * @param registry The extension registry to load from.
	 */
	public InputManager(IExtensionRegistry registry)
	{
		IExtensionPoint point = registry.getExtensionPoint(//
				"org.eclipse.vtp.framework.interactions.core.inputtypes"); //$NON-NLS-1$
		IExtension[] extensions = point.getExtensions();
		// Map inputTypes = new HashMap(extensions.length);
		Map inputTypeIndex = new HashMap(extensions.length);
		for (int i = 0; i < extensions.length; ++i)
		{
			Bundle bundle = Platform.getBundle(extensions[i].getContributor()
					.getName());
			IConfigurationElement[] elements = extensions[i]
					.getConfigurationElements();
			for (int j = 0; j < elements.length; ++j)
			{
				try
				{
					InputType inputType = new InputType(elements[j].getAttribute("id"), //$NON-NLS-1$
							elements[j].getAttribute("class"), //$NON-NLS-1$
							bundle.loadClass(elements[j].getAttribute("class"))); //$NON-NLS-1$
					// inputTypes.put(inputType.getId(), inputType);
					inputTypeIndex.put(elements[j].getAttribute("element-name") + //$NON-NLS-1$
							elements[j].getAttribute("element-uri"), inputType); //$NON-NLS-1$
				}
				catch (Exception e)
				{
					e.printStackTrace();
					continue;
				}
			}
		}
		// this.inputTypes = Collections.unmodifiableMap(inputTypes);
		this.inputTypeIndex = Collections.unmodifiableMap(inputTypeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.
	 *      IInputGrammarFactory#loadInput(org.w3c.dom.Element)
	 */
	public InputGrammar loadInput(Element configuration)
	{
		if (configuration == null)
			return null;
		InputType inputType = (InputType)inputTypeIndex.get(configuration
				.getLocalName()
				+ configuration.getNamespaceURI());
		if (inputType == null)
			return null;
		return inputType.newInstance(configuration);
	}

	/**
	 * Implementation of {@link IInputType}.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class InputType
	{
		/** The ID of this input type. */
		private final String id;
		/** The name of this input type. */
		private final String name;
		/** The factory constructor to use. */
		private final Constructor constructor;

		/**
		 * Creates a new InputType.
		 * 
		 * @param id The ID of this input type.
		 * @param name The name of this input type.
		 * @param contentClass The implementation type.
		 */
		InputType(String id, String name, Class contentClass)
		{
			this.id = id;
			Constructor constructor = null;
			this.name = name;
			try
			{
				constructor = contentClass.getConstructor(new Class[] {
						IInputGrammarFactory.class, Element.class });
			}
			catch (NoSuchMethodException e)
			{
				try
				{
					constructor = contentClass
							.getConstructor(new Class[] { Element.class });
				}
				catch (NoSuchMethodException ex)
				{
					throw new IllegalStateException(ex);
				}
			}
			this.constructor = constructor;
		}

		/**
		 * Creates a new instance of this content type.
		 * 
		 * @param configuration The configuration to read.
		 * @return A new instance of this content type.
		 */
		InputGrammar newInstance(Element configuration)
		{
			try
			{
				if (constructor.getParameterTypes().length == 1)
					return (InputGrammar)constructor
							.newInstance(new Object[] { configuration });
				else
					return (InputGrammar)constructor.newInstance(new Object[] {
							InputManager.this, configuration });
			}
			catch (Exception e)
			{
				throw new IllegalStateException(e);
			}
		}

		public String getId()
		{
			return id;
		}

		public String getName()
		{
			return name;
		}
	}
}
