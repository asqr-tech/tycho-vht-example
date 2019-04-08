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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ContentComposite extends Content
{
	public static final String ELEMENT_NAME = "composite-content"; //$NON-NLS-1$
	private List<Content> children = new ArrayList<Content>();

	public ContentComposite()
	{
	}
	
	public ContentComposite(List<Content> content)
	{
		children.addAll(content);
	}

	public ContentComposite(IContentFactory factory, Element element)
	{
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
		{
			if (nl.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				Content c = factory.loadContent((Element)nl.item(i));
				if (c != null)
					children.add(c);
			}
		}
	}

	public void addContent(Content content)
	{
		children.remove(content);
		children.add(content);
	}

	public void removeContent(Content content)
	{
		children.remove(content);
	}

	public void moveContentUp(Content content)
	{
		int idex = children.indexOf(content);
		if (idex < 1) // covers both first element and element not contained
			return;
		children.remove(content);
		children.add(idex - 1, content);
	}

	public void moveContentDown(Content content)
	{
		int idex = children.indexOf(content);
		if (idex < 0 || idex > children.size() - 2)
			return;
		children.remove(content);
		children.add(idex + 1, content);
	}

	public List<Content> listContent()
	{
		return Collections.unmodifiableList(children);
	}

	public Element store(Element element)
	{
		Element thisElement = element.getOwnerDocument().createElementNS(
				ELEMENT_NAMESPACE, ELEMENT_NAME);
		element.appendChild(thisElement);
		for(Content c : children)
		{
			c.store(thisElement);
		}
		return thisElement;
	}

	public String getContentType()
	{
		return "org.eclipse.vtp.framework.interactions.core.media.content.composite"; //$NON-NLS-1$
	}

	public boolean isResolvable()
	{
		return true;
	}

	public List<Content> resolve(IMediaProvider mediaProvider)
	{
		List<Content> ret = new LinkedList<Content>();
		for(Content c : children)
		{
			if (c.isResolvable())
				ret.addAll(c.resolve(mediaProvider));
			else
				ret.add(c);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.Content#createCopy()
	 */
	public Content createCopy()
	{
		ContentComposite copy = new ContentComposite();
		for(Content c : children)
			copy.addContent(c.createCopy());
		return copy;
	}
}
