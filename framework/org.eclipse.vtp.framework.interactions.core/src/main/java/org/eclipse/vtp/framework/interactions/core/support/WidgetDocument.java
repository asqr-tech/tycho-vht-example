/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods),
 *    Vincent Pruitt (OpenMethods)
 *    
 *    T.D. Barnes (OpenMethods) - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.core.support;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * Base document type for component-style structures.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public abstract class WidgetDocument extends Widget implements IDocument
{
	private boolean secured = false;
	/** Determines if the receiving browser should cache this document */
	private boolean cachable = true;

	/**
	 * Creates a new WidgetDocument object.
	 */
	protected WidgetDocument()
	{
	}

	public boolean isCachable()
	{
		return cachable;
	}
	
	public void setCachable(boolean bool)
	{
		this.cachable = bool;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.core.output.IDocument#getContentType()
	 */
	public String getContentType()
	{
		return DEFAULT_CONTENT_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.core.output.IOutputDocument#
	 *      toXMLSource()
	 */
	public Source toXMLSource() throws IllegalStateException
	{
		return new SAXSource(new WidgetReader(), new InputSource());
	}

	/**
	 * An implementation of {@link XMLReader} that turns a widget document into
	 * SAX events.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class WidgetReader implements XMLReader
	{
		/** The configured features. */
		private final Map features = new HashMap();
		/** The configured properties. */
		private final Map properties = new HashMap();
		/** The configured content handler. */
		private ContentHandler contentHandler = null;
		/** The configured DTD handler. */
		private DTDHandler dtdHandler = null;
		/** The configured entity resolver. */
		private EntityResolver entityResolver = null;
		/** The configured error handler. */
		private ErrorHandler errorHandler = null;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#getFeature(java.lang.String)
		 */
		public boolean getFeature(String name) throws SAXNotRecognizedException,
				SAXNotSupportedException
		{
			return Boolean.TRUE.equals(features.get(name));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#getProperty(java.lang.String)
		 */
		public Object getProperty(String name) throws SAXNotRecognizedException,
				SAXNotSupportedException
		{
			return properties.get(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#getContentHandler()
		 */
		public ContentHandler getContentHandler()
		{
			return contentHandler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#getDTDHandler()
		 */
		public DTDHandler getDTDHandler()
		{
			return dtdHandler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#getEntityResolver()
		 */
		public EntityResolver getEntityResolver()
		{
			return entityResolver;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#getErrorHandler()
		 */
		public ErrorHandler getErrorHandler()
		{
			return errorHandler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#setFeature(java.lang.String, boolean)
		 */
		public void setFeature(String name, boolean value)
				throws SAXNotRecognizedException, SAXNotSupportedException
		{
			if (value)
				features.put(name, Boolean.TRUE);
			else
				features.remove(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#setProperty(java.lang.String,
		 *      java.lang.Object)
		 */
		public void setProperty(String name, Object value)
				throws SAXNotRecognizedException, SAXNotSupportedException
		{
			if (value == null)
				properties.remove(name);
			else
				properties.put(name, value);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#setContentHandler(org.xml.sax.ContentHandler)
		 */
		public void setContentHandler(ContentHandler handler)
		{
			contentHandler = handler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#setDTDHandler(org.xml.sax.DTDHandler)
		 */
		public void setDTDHandler(DTDHandler handler)
		{
			dtdHandler = handler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#setEntityResolver(org.xml.sax.EntityResolver)
		 */
		public void setEntityResolver(EntityResolver resolver)
		{
			entityResolver = resolver;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#setErrorHandler(org.xml.sax.ErrorHandler)
		 */
		public void setErrorHandler(ErrorHandler handler)
		{
			errorHandler = handler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#parse(java.lang.String)
		 */
		public void parse(String systemId) throws IOException, SAXException
		{
			writeWidget(contentHandler);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
		 */
		public void parse(InputSource input) throws IOException, SAXException
		{
			writeWidget(contentHandler);
		}
	}

	public boolean isSecured()
    {
	    return secured;
    }
	
	public void setSecured(boolean secured)
	{
		this.secured = secured;
	}
}
