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

public class MediaRenderingManager
{
	public static final int CAPTURE = 1;
	public static final int RESOLVE = 2;
	public static final int FORMAT = 4;
	public static final int COMPLETE = CAPTURE | RESOLVE | FORMAT;
	
	private IMediaProvider mediaProvider;
	private IDataSet dataSet;
	
	public MediaRenderingManager(IMediaProvider mediaProvider, IDataSet dataSet)
	{
		super();
		this.mediaProvider = mediaProvider;
		this.dataSet = dataSet;
	}
	
	public List<Content> renderContent(int renderingFlags, List<Content> contentSet)
	{
		List<Content> inputList = contentSet;
		List<Content> renderedList = contentSet;
		if((renderingFlags & CAPTURE) > 0)
		{
			renderedList = new LinkedList<Content>();
			for(Content c : inputList)
			{
				if(c.isDataAware())
					renderedList.add(c.captureData(dataSet));
				else
					renderedList.add(c);
			}
			inputList = renderedList;
		}
		if((renderingFlags & RESOLVE) > 0)
		{
			renderedList = new LinkedList<Content>();
			for(Content c : inputList)
			{
				if(c.isResolvable())
					renderedList.addAll(c.resolve(mediaProvider));
				else
					renderedList.add(c);
			}
			inputList = renderedList;
		}
		if((renderingFlags & FORMAT) > 0)
		{
			renderedList = new LinkedList<Content>();
			for(Content c : inputList)
			{
				if(c instanceof FormattableContent)
					renderedList.addAll(((FormattableContent)c).format(mediaProvider.getFormatter(), mediaProvider));
				else
					renderedList.add(c);
			}
		}
		return renderedList;
	}
}
