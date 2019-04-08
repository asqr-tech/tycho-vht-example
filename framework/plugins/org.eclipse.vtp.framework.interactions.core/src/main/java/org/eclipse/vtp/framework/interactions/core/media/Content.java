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
package org.eclipse.vtp.framework.interactions.core.media;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

public abstract class Content
{
	public static final String ELEMENT_NAMESPACE = "http://www.eclipse.org/vtp/media/content"; //$NON-NLS-1$

	public Content()
	{
	}

	public abstract Element store(Element element);

	public abstract String getContentType();

	public boolean isDataAware()
	{
		return false;
	}

	public Content captureData(IDataSet dataSet)
	{
		return this;
	}

	public boolean isResolvable()
	{
		return false;
	}

	public List<Content> resolve(IMediaProvider mediaProvider)
	{
		List<Content> ret = new LinkedList<Content>();
		ret.add(this);
		return ret;
	}
	
	public abstract Content createCopy();
}
