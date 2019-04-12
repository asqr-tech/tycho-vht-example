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
package org.eclipse.vtp.desktop.model.interactive.core.input;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.framework.interactions.core.media.IInputGrammarFactory;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;

public class InputLoadingManager implements IInputGrammarFactory
{
	public static final String inputTypeExtensionId = "org.eclipse.vtp.framework.interactions.core.inputtypes";
	private static final InputLoadingManager instance = new InputLoadingManager();

	public static InputLoadingManager getInstance()
	{
		return instance;
	}

	private Map<String, InputRegistration> inputTypes = new HashMap<String, InputRegistration>();
	private Map<String, InputRegistration> inputTypesById = new HashMap<String, InputRegistration>();

	public InputLoadingManager()
	{
		super();
		IConfigurationElement[] primitiveExtensions = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						inputTypeExtensionId);
		for (int i = 0; i < primitiveExtensions.length; i++)
		{
			String inputElementURI = primitiveExtensions[i]
					.getAttribute("element-uri");
			String inputElementName = primitiveExtensions[i]
					.getAttribute("element-name");
			String inputClassName = primitiveExtensions[i].getAttribute("class");
			if (inputElementURI == null || inputElementName == null
					|| inputClassName == null)
				continue;
			Bundle contributor = Platform.getBundle(primitiveExtensions[i]
					.getContributor().getName());
			try
			{
				@SuppressWarnings("unchecked")
				Class<InputGrammar> providerClass = (Class<InputGrammar>) contributor.loadClass(inputClassName);
				InputRegistration reg = new InputRegistration();
				reg.id = primitiveExtensions[i].getAttribute("id");
				reg.inputElementURI = inputElementURI;
				reg.inputElementName = inputElementName;
				reg.inputClass = providerClass;
				inputTypes.put(inputElementURI + inputElementName, reg);
				inputTypesById.put(reg.id, reg);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}

	public InputGrammar loadInput(Element inputElement)
	{
		try
		{
			String uri = inputElement.getNamespaceURI();
			String name = inputElement.getTagName();
			InputRegistration reg = inputTypes.get(uri + name);
			if (reg == null)
				return null;
			try
			{
				return reg.inputClass.getConstructor(
						new Class[] { IInputGrammarFactory.class, Element.class })
						.newInstance(new Object[] { this, inputElement });
			}
			catch (NoSuchMethodException e)
			{
				return reg.inputClass.getConstructor(
						new Class[] { Element.class }).newInstance(
						new Object[] { inputElement });
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private class InputRegistration
	{
		String id;
		@SuppressWarnings("unused")
		//TODO review these for removal
		String inputElementURI;
		@SuppressWarnings("unused")
		String inputElementName;
		Class<InputGrammar> inputClass;
	}
}
