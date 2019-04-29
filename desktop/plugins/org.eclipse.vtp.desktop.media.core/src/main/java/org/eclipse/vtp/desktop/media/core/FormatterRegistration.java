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
package org.eclipse.vtp.desktop.media.core;

import org.eclipse.vtp.framework.interactions.core.media.IFormatter;

public class FormatterRegistration
{
	String id;
	String name;
	String vendor;
	String interactionType;
	Class<IFormatter> formatterClass;
	
	public FormatterRegistration()
	{
		super();
	}

	public String getId()
	{
		return id;
	}

	public String getInteractionType()
	{
		return interactionType;
	}

	public String getName()
	{
		return name;
	}

	public String getVendor()
	{
		return vendor;
	}
	
	public IFormatter getFormatter()
	{
		try
		{
			IFormatter formatter = formatterClass.newInstance();
			return formatter;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}