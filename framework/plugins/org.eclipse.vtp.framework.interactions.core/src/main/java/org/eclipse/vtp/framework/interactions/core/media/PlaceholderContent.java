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

import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class PlaceholderContent extends Content
{
	public static final String ELEMENT_NAME = "placeholder-content"; //$NON-NLS-1$

	private String data = ""; //$NON-NLS-1$

	public PlaceholderContent()
	{
	}

	public PlaceholderContent(Element element)
	{
		data = XMLUtilities.getElementTextDataNoEx(element, true);
		if(data == null)
			data = "";
	}

	public void setPlaceholder(String name)
	{
		this.data = name;
	}

	public String getPlaceholder()
	{
		return data;
	}

	public Element store(Element element)
	{
		Element thisElement = element.getOwnerDocument().createElementNS(
				ELEMENT_NAMESPACE, ELEMENT_NAME);
		element.appendChild(thisElement);
		Text dataNode = thisElement.getOwnerDocument().createTextNode(data);
		thisElement.appendChild(dataNode);
		return thisElement;
	}

	public String getContentType()
	{
		return "org.eclipse.vtp.framework.interactions.core.media.content.placeholder"; //$NON-NLS-1$
	}

	public boolean isDataAware()
	{
		return false;
	}

	public Content captureData(IDataSet dataSet)
	{
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.Content#createCopy()
	 */
	public Content createCopy()
	{
		PlaceholderContent copy = new PlaceholderContent();
		copy.data = data;
		return copy;
	}
}
