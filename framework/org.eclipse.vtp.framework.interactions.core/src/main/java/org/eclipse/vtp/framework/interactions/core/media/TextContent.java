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

public class TextContent extends Content
{
	public static final int STATIC_TEXT = 1;
	public static final int VARIABLE_TEXT = 2;
	public static final String ELEMENT_NAME = "text-content"; //$NON-NLS-1$

	private int dataType = STATIC_TEXT;
	private String data = ""; //$NON-NLS-1$

	public TextContent()
	{
	}

	public TextContent(Element element)
	{
		String dataTypeString = element.getAttribute("dataType"); //$NON-NLS-1$
		if (dataTypeString.equals("")) //$NON-NLS-1$
			dataType = STATIC_TEXT;
		else if ("static".equalsIgnoreCase(dataTypeString)) //$NON-NLS-1$
			dataType = STATIC_TEXT;
		else if ("variable".equalsIgnoreCase(dataTypeString)) //$NON-NLS-1$
			dataType = VARIABLE_TEXT;
		else
			dataType = Integer.parseInt(dataTypeString);
		data = XMLUtilities.getElementTextDataNoEx(element, true);
		if(data == null)
			data = "";
	}

	public void setStaticText(String text)
	{
		this.dataType = STATIC_TEXT;
		this.data = text;
	}

	public void setVariableText(String variableName)
	{
		this.dataType = VARIABLE_TEXT;
		this.data = variableName;
	}

	public int getTextType()
	{
		return dataType;
	}

	public String getText()
	{
		return data;
	}

	public Element store(Element element)
	{
		Element thisElement = element.getOwnerDocument().createElementNS(
				ELEMENT_NAMESPACE, ELEMENT_NAME);
		element.appendChild(thisElement);
		thisElement.setAttribute("dataType", //$NON-NLS-1$
				dataType == STATIC_TEXT ? "static" : "variable"); //$NON-NLS-1$ //$NON-NLS-2$
		Text dataNode = thisElement.getOwnerDocument().createTextNode(data);
		thisElement.appendChild(dataNode);
		return thisElement;
	}

	public String getContentType()
	{
		return "org.eclipse.vtp.framework.interactions.core.media.content.text"; //$NON-NLS-1$
	}

	public boolean isDataAware()
	{
		return dataType == VARIABLE_TEXT;
	}

	public Content captureData(IDataSet dataSet)
	{
		if (dataType == STATIC_TEXT)
			return this;
		TextContent clone = new TextContent();
		clone.setStaticText(dataSet.getData(data).toString());
		return clone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.Content#createCopy()
	 */
	public Content createCopy()
	{
		TextContent copy = new TextContent();
		copy.dataType = dataType;
		copy.data = data;
		return copy;
	}
}
