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

public class ReferencedContent extends Content
{
	public static final int STATIC_REF = 1;
	public static final int VARIABLE_REF = 2;
	public static final String ELEMENT_NAME = "referenced-content"; //$NON-NLS-1$

	private int referenceType = STATIC_REF;
	private String referencedName = ""; //$NON-NLS-1$

	public ReferencedContent()
	{
	}

	public ReferencedContent(Element element)
	{
		String referenceTypeString = element.getAttribute("referenceType"); //$NON-NLS-1$
		if (referenceTypeString.equals("")) //$NON-NLS-1$
			referenceType = STATIC_REF;
		else if ("static".equalsIgnoreCase(referenceTypeString)) //$NON-NLS-1$
			referenceType = STATIC_REF;
		else if ("variable".equalsIgnoreCase(referenceTypeString)) //$NON-NLS-1$
			referenceType = VARIABLE_REF;
		else
			referenceType = Integer.parseInt(referenceTypeString);
		referencedName = element.getAttribute("referencedName"); //$NON-NLS-1$
	}

	public String getReferencedName()
	{
		return this.referencedName;
	}

	public int getReferenceType()
	{
		return referenceType;
	}

	public void setStaticReferencedName(String name)
	{
		referenceType = STATIC_REF;
		this.referencedName = name;
	}

	public void setVariableReferencedName(String name)
	{
		referenceType = VARIABLE_REF;
		this.referencedName = name;
	}

	public Element store(Element element)
	{
		Element thisElement = element.getOwnerDocument().createElementNS(
				ELEMENT_NAMESPACE, ELEMENT_NAME);
		element.appendChild(thisElement);
		thisElement.setAttribute("referenceType", //$NON-NLS-1$
				referenceType == STATIC_REF ? "static" : "variable"); //$NON-NLS-1$ //$NON-NLS-2$
		thisElement.setAttribute("referencedName", referencedName); //$NON-NLS-1$
		return thisElement;
	}

	public String getContentType()
	{
		return "org.eclipse.vtp.framework.interactions.core.media.content.referenced"; //$NON-NLS-1$
	}

	public boolean isDataAware()
	{
		return referenceType == VARIABLE_REF;
	}

	public Content captureData(IDataSet dataSet)
	{
		if (referenceType == STATIC_REF)
			return this;
		ReferencedContent clone = new ReferencedContent();
		clone.setStaticReferencedName(dataSet.getData(referencedName).toString());
		return clone;
	}

	public boolean isResolvable()
	{
		return true;
	}

	public List resolve(IMediaProvider mediaProvider)
	{
		List ret = new LinkedList();
		if (referenceType == STATIC_REF && mediaProvider.hasSharedContent())
		{
			ISharedContentProvider scp = mediaProvider.getSharedContentProvider();
			Content refContent = scp.getSharedContent(referencedName);
			if (refContent != null)
			{
				if (refContent.isResolvable())
					ret.addAll(refContent.resolve(mediaProvider));
				else
					ret.add(refContent);
			}
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
		ReferencedContent copy = new ReferencedContent();
		copy.referenceType = referenceType;
		copy.referencedName = referencedName;
		return copy;
	}
}
