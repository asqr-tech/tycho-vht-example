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
package org.eclipse.vtp.desktop.model.core.internal;

import java.io.InputStream;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IWebserviceDescriptor;
import org.eclipse.vtp.desktop.model.core.wsdl.WSDL;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * WebserviceDescriptor.
 *
 * @author Trip Gilman
 */
public class WebserviceDescriptor extends WorkflowResource implements IWebserviceDescriptor
{
	private static final String HASHPREFIX = "Webservice";
	/** Comment for parent. */
	private final WebserviceSet parent;
	/** Comment for file. */
	private final IFile file;
	private WSDL wsdl = null;

	/**
	 * Creates a new Dependency.
	 *
	 * @param parent
	 * @param file
	 */
	public WebserviceDescriptor(WebserviceSet parent, IFile file)
	{
		this.parent = parent;
		this.file = file;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.VoiceResource#
	 * getObjectId()
	 */
	protected String getObjectId()
	{
		return file.getFullPath().toPortableString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getName()
	 */
	public String getName()
	{
		return file.getName();
	}
	
	public WSDL getWSDL() throws Exception
	{
		if(!file.exists())
			return null;
		if(wsdl == null)
		{
			try
			{
//				DocumentBuilderFactory factory =
//					DocumentBuilderFactory.newInstance();
//				factory.setNamespaceAware(true);
//				DocumentBuilder builder = factory.newDocumentBuilder();
//				Document document = builder.parse(file.getContents());
				WebserviceDescriptorXMLParser parser = new WebserviceDescriptorXMLParser(file.getContents());
				Document document = parser.getDocument();
				org.w3c.dom.Element rootElement = document.getDocumentElement();
				wsdl = new WSDL(rootElement);
			}
			catch(Exception ex)
			{
				throw new Exception("Invalid WSDL", ex);
			}
		}
		return wsdl;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getParent()
	 */
	public WebserviceSet getParent()
	{
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.projects.core.IDependency#delete()
	 */
	public void delete() throws CoreException
	{
		parent.deleteWebserviceDescriptor(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.projects.core.IDependency#exists()
	 */
	public boolean exists()
	{
		return file.exists();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass)
	{
		if(IResource.class.isAssignableFrom(adapterClass) && adapterClass.isAssignableFrom(file.getClass()))
		{
			return file;
		}
		if(WebserviceDescriptor.class.isAssignableFrom(adapterClass))
			return this;
		return super.getAdapter(adapterClass);
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof WebserviceDescriptor)
		{
			return file.equals(((WebserviceDescriptor)obj).file);
		}
		return false;
	}

	public int hashCode()
	{
		return (HASHPREFIX + file.toString()).hashCode();
	}

	public class WebserviceDescriptorXMLParser extends DOMParser
	{
		private XMLLocator locator;
		
		public WebserviceDescriptorXMLParser(InputStream input)
		{
			try
			{
				this.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
				this.parse(new InputSource(input));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		public void startElement(QName elementQName, XMLAttributes attrList, Augmentations augs) throws XNIException
		{
			super.startElement(elementQName, attrList, augs);
			Node node = null;
			try
			{
				node = (Node)getProperty("http://apache.org/xml/properties/dom/current-element-node");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			if(node != null)
				node.setUserData("line_Number", String.valueOf(locator.getLineNumber()), null);
		}
		
		public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException
		{
			super.startDocument(locator, encoding, namespaceContext, augs);
			this.locator = locator;
			Node node = null;
			try
			{
				node = (Node)getProperty("http://apache.org/xml/properties/dom/current-element-node");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			if(node != null)
				node.setUserData("line_Number", String.valueOf(locator.getLineNumber()), null);
		}
		
		public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException
		{
//			if(! NotIncludeIgnorableWhiteSpaces )
				super.ignorableWhitespace( text, augs);
		}

	}
	
}
