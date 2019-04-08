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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.vtp.framework.util.DateHelper;
import org.w3c.dom.Element;

public class DateContent extends FormattableContent
{
	public static final String ELEMENT_NAME = "date-content"; //$NON-NLS-1$

	public DateContent()
	{
	}

	public DateContent(Element element)
	{
		super(element);
	}

	public String getContentTypeName()
	{
		return "DATE"; //$NON-NLS-1$
	}

	public List format(IFormatter formatter, IMediaProvider mediaProvider)
	{
		List ret = new LinkedList();
		if (getValueType() != VARIABLE_VALUE)
        {
	        Calendar date = DateHelper.parseDate(getValue());
            if(date != null)
		        ret.addAll(formatter.formatDate(date,
						mediaProvider.getFormatManager().getFormat(this, getFormatName()),
						getFormatOptions(), mediaProvider.getResourceManager()));
            else
            {
            	TextContent textContent = new TextContent();
            	if(this.getValueType() == FormattableContent.STATIC_VALUE)
            		textContent.setStaticText(this.getValue());
            	else
            		textContent.setVariableText(this.getValue());
				ret.add(textContent);
            }
        }
		return ret;
	}

	public Element store(Element element)
	{
		Element thisElement = element.getOwnerDocument().createElementNS(
				ELEMENT_NAMESPACE, ELEMENT_NAME);
		element.appendChild(thisElement);
		super.storeBaseInfo(thisElement);
		return thisElement;
	}

	public String getContentType()
	{
		return "org.eclipse.vtp.framework.interactions.core.media.content.date"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.Content#createCopy()
	 */
	public Content createCopy()
	{
		return configureCopy(new DateContent());
	}
	
	public static void main(String[] args)
	{
		String[] dates = new String[] {"3/4/1977 3:45:34 pm EST",
									   "3/4/1977 3:45:34 pm",
									   "3/4/1977 3:45:34 EST",
									   "3/4/1977 3:45 pm EST",
									   "3/4/1977 3:45 pm",
									   "3/4/1977 3:45 EST",
									   "3:45:34 pm EST",
									   "3:45:34 pm",
									   "3:45:34 EST",
									   "3/31/2010 3:45:34 pm EST",
									   "1-4-1977",
									   "6/4/1977"};
		for(String date : dates)
		{
			printDate(date);
		}
		Calendar cal = Calendar.getInstance();
		System.out.println(DateHelper.toDateString(cal));
		System.out.println(DateFormat.getDateTimeInstance().format(cal.getTime()));
	}
		
	public static void printDate(String date)
	{
		Calendar cal = DateHelper.parseDate(date);
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG);
		System.out.println(cal.getTimeZone());
		System.out.println(df.format(cal.getTime()));
		df.setTimeZone(cal.getTimeZone());
		System.out.println(df.format(cal.getTime()));
		System.out.println(DateHelper.toDateString(cal));
	}
}
