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
package org.eclipse.vtp.desktop.model.interactive.core.content;

import org.eclipse.vtp.framework.interactions.core.media.IContentType;

public class ContentType implements IContentType
{
	private String id;
	private String name;

	public ContentType(String id, String name)
	{
		super();
		this.id = id;
		this.name = name;
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
