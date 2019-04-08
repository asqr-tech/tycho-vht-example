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

public abstract class FileContent extends Content
{
	public static final int STATIC_PATH = 1;
	public static final int VARIABLE_PATH = 2;
	
	private int dataType = STATIC_PATH;
	private String data = ""; //$NON-NLS-1$

	public FileContent()
	{
	}

	public FileContent(Element element)
	{
		String dataTypeString = element.getAttribute("dataType"); //$NON-NLS-1$
		if (dataTypeString.equals("")) //$NON-NLS-1$
			dataType = STATIC_PATH;
		else if ("static".equalsIgnoreCase(dataTypeString)) //$NON-NLS-1$
			dataType = STATIC_PATH;
		else if ("variable".equalsIgnoreCase(dataTypeString)) //$NON-NLS-1$
			dataType = VARIABLE_PATH;
		else
			dataType = Integer.parseInt(dataTypeString);
		data = XMLUtilities.getElementTextDataNoEx(element, true);
		if(data == null)
			data = "";
	}

	public void setStaticPath(String path)
	{
		dataType = STATIC_PATH;
		this.data = path;
	}

	public void setVariablePath(String variable)
	{
		dataType = VARIABLE_PATH;
		data = variable;
	}

	public String getPath()
	{
		return data;
	}

	public int getPathType()
	{
		return dataType;
	}

	public String getFileTypeName()
	{
		return "FILE"; //$NON-NLS-1$
	}

	public boolean isDataAware()
	{
		return dataType == VARIABLE_PATH;
	}

	public Content captureData(IDataSet dataSet)
	{
		if (dataType == STATIC_PATH)
			return this;
		FileContent clone = (FileContent)createCopy();
		clone.setStaticPath(dataSet.getData(data).toString());
		return clone;
	}

	protected void storeBaseInfo(Element thisElement)
	{
		thisElement.setAttribute("dataType", //$NON-NLS-1$
				dataType == STATIC_PATH ? "static" : "variable"); //$NON-NLS-1$ //$NON-NLS-2$
		Text dataNode = thisElement.getOwnerDocument().createTextNode(data);
		thisElement.appendChild(dataNode);
	}
}
