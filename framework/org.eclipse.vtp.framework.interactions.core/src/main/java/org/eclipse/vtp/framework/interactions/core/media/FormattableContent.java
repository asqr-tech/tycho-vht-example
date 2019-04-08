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

import java.util.List;

import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public abstract class FormattableContent extends Content
{
	public static final int STATIC_VALUE = 1;
	public static final int VARIABLE_VALUE = 2;

	private int dataType = STATIC_VALUE;
	private String data = ""; //$NON-NLS-1$
	private String formatName = ""; //$NON-NLS-1$
	private String formatOptions = ""; //$NON-NLS-1$

	public FormattableContent()
	{
	}

	public FormattableContent(Element element)
	{
		super();
		String dataTypeString = element.getAttribute("dataType"); //$NON-NLS-1$
		if (dataTypeString.equals("")) //$NON-NLS-1$
			dataType = STATIC_VALUE;
		else if ("static".equalsIgnoreCase(dataTypeString)) //$NON-NLS-1$
			dataType = STATIC_VALUE;
		else if ("variable".equalsIgnoreCase(dataTypeString)) //$NON-NLS-1$
			dataType = VARIABLE_VALUE;
		else
			dataType = Integer.parseInt(dataTypeString);
		data = XMLUtilities.getElementTextDataNoEx(element, true);
		if(data == null)
			data = "";
		formatName = element.getAttribute("format"); //$NON-NLS-1$
		formatOptions = element.getAttribute("options"); //$NON-NLS-1$
	}

	public abstract String getContentTypeName();

	public abstract List<Content> format(IFormatter formatter, IMediaProvider mediaProvider);

	public boolean isDataAware()
	{
		return dataType == VARIABLE_VALUE;
	}

	public Content captureData(IDataSet dataSet)
	{
		if (dataType == STATIC_VALUE)
			return this;
		FormattableContent copy = (FormattableContent)createCopy();
		copy.setStaticValue(dataSet.getData(data).toString());
		return copy;
	}

	public void setStaticValue(String value)
	{
		this.dataType = STATIC_VALUE;
		this.data = value;
	}

	public void setVariableValue(String variableName)
	{
		this.dataType = VARIABLE_VALUE;
		this.data = variableName;
	}

	public int getValueType()
	{
		return dataType;
	}

	public String getValue()
	{
		return data;
	}

	public String getFormatName()
	{
		return formatName;
	}

	public void setFormatName(String formatName)
	{
		this.formatName = formatName == null ? "" : formatName;//$NON-NLS-1$
	}

	public String getFormatOptions()
	{
		return formatOptions;
	}

	public void setFormatOptions(String formatOptions)
	{
		this.formatOptions = formatOptions == null ? "" : formatOptions;//$NON-NLS-1$
	}

	protected void storeBaseInfo(Element thisElement)
	{
		thisElement.setAttribute("dataType", dataType == STATIC_VALUE ? //$NON-NLS-1$
		"static"
				: "variable"); //$NON-NLS-1$ //$NON-NLS-2$
		thisElement.setAttribute("format", formatName); //$NON-NLS-1$
		if (formatOptions.length() > 0)
			thisElement.setAttribute("options", formatOptions); //$NON-NLS-1$
		Text dataNode = thisElement.getOwnerDocument().createTextNode(data);
		thisElement.appendChild(dataNode);
	}

	protected FormattableContent configureCopy(FormattableContent copy)
	{
		copy.dataType = dataType;
		copy.data = data;
		copy.formatName = formatName;
		copy.formatOptions = formatOptions;
		return copy;
	}
}
