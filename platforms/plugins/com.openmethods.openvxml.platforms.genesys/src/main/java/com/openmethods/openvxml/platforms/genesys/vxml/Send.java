package com.openmethods.openvxml.platforms.genesys.vxml;

import java.io.IOException;

import org.eclipse.vtp.framework.interactions.voice.vxml.Action;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Send extends Action
{
	private String contentType = null;
	private String body = null;
	private String nameList = null;
	private boolean async = false;

	public Send()
	{
		super();
	}
	
	public boolean isAsync()
	{
		return async;
	}
	
	public void setAsync(boolean async)
	{
		this.async = async;
	}
	
	public String getContentType()
	{
		return contentType;
	}
	
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}
	
	public String getBody()
	{
		return body;
	}
	
	public void setBody(String body)
	{
		this.body = body;
	}
	
	public String getNameList()
	{
		return nameList;
	}
	
	public void setNameList(String nameList)
	{
		this.nameList = nameList;
	}

	@Override
	public void writeWidget(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		outputHandler.startElement(NAMESPACE_URI_VXML, "send", "send",
				attributes);
		outputHandler.endElement(NAMESPACE_URI_VXML, "send", "send");
	}

	/**
	 * @param out
	 * @throws IOException
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
//§		super.writeAttributes(attributes);
		writeAttribute(attributes, null, null, "async", TYPE_CDATA, Boolean.toString(async));
		if(contentType != null)
			writeAttribute(attributes, null, null, "contenttype", TYPE_CDATA, contentType);
		if(body != null)
		{
			writeAttribute(attributes, null, null, "body", TYPE_CDATA, body);
		}
		else
			writeAttribute(attributes, null, null, "nameList", TYPE_CDATA, nameList);
	}

}
