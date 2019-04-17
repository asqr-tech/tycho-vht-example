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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.IBusinessObject;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectField;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectSet;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.event.ReloadObjectDataEvent;
import org.eclipse.vtp.desktop.model.core.internal.event.ObjectEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This is a concrete implementation of <code>IBusinessObject</code> and
 * provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class BusinessObject extends WorkflowResource implements IBusinessObject
{
	/**
	 * The parent business object set.
	 */
	private BusinessObjectSet objectSet;

	/**
	 * The eclipse file resource that contains the business object definition.
	 */
	private IFile file;

	/**
	 * The unique identifier of this business object type.
	 */
	private String id;

	/**
	 * The name of this business object type.
	 */
	private String name;

	/**
	 * The list of <code>BusinessObjectField</code>s defined for this business object type.
	 */
	private List<BusinessObjectField> fields;

	/**
	 * Creates a new <code>BusinessObject</code> with the given parent
	 * business object set and eclipse file resource.
	 *
	 * @param objectSet The parent business object set
	 * @param file The eclipse file resource that contains this business object
	 * type's definition
	 */
	public BusinessObject(BusinessObjectSet objectSet, IFile file)
	{
		super();
		this.objectSet = objectSet;
		this.file = file;
		activateEvents();
		loadHeaders();
	}

	/**
	 * Initializes the member variables and creates the business object fields
	 * for this business object type.
	 */
	private void loadHeaders()
	{
		try
		{
			Document document =
				DocumentBuilderFactory.newInstance().newDocumentBuilder()
									  .parse(file.getContents());
			Element rootElement = document.getDocumentElement();
			id = rootElement.getAttribute("id");
			name = rootElement.getAttribute("name");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(FactoryConfigurationError e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the member variables and creates the business object fields
	 * for this business object type.
	 */
	void loadModel()
	{
		try
		{
			Document document =
				DocumentBuilderFactory.newInstance().newDocumentBuilder()
									  .parse(file.getContents());
			Element rootElement = document.getDocumentElement();
			fields = new ArrayList<BusinessObjectField>();
			System.err.println("Loading object:" + name);
			NodeList nl = rootElement.getElementsByTagName("field");

			for(int i = 0; i < nl.getLength(); i++)
			{
				Element fieldElement = (Element)nl.item(i);
				String fieldName = fieldElement.getAttribute("name");
				System.err.println("Loading field: " + fieldName);
				String fieldInitialValue =
					fieldElement.getAttribute("initialValue");
				NodeList dtnl = fieldElement.getElementsByTagName("data-type");

				if(dtnl.getLength() > 0)
				{
					FieldType fieldDataType =
						FieldType.load(objectSet, (Element)dtnl.item(0));
					BusinessObjectField bof =
						new BusinessObjectField(this, fieldName, fieldDataType,
							fieldInitialValue, Boolean.parseBoolean(fieldElement.getAttribute("secured")));
					fields.add(bof);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(FactoryConfigurationError e)
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.VoiceResource#getObjectId()
	 */
	protected String getObjectId()
	{
		return file.getFullPath().toPortableString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IBusinessObject#getId()
	 */
	public String getId()
	{
		return id;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IBusinessObject#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IBusinessObject#getFields()
	 */
	public List<IBusinessObjectField> getFields()
	{
		return new ArrayList<IBusinessObjectField>(fields);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getParent()
	 */
	public IWorkflowResource getParent()
	{
		return objectSet;
	}

	/**
	 * Opens the underlying eclipse file resource that contains the
	 * XML format definition of this business object.  The contents
	 * are return in an <code>InputStream</code>.
	 *
	 * @return An input stream containing the contents of the XML
	 * format definition of this business object tyep
	 * @throws CoreException If an error occured while opening the
	 * file resource
	 */
	public InputStream read() throws CoreException
	{
		return file.getContents();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.event.ObjectListener#processObjectEvent(org.eclipse.vtp.desktop.core.project.internals.event.ObjectEvent)
	 */
	public void processObjectEvent(ObjectEvent event)
	{
		if(event instanceof ReloadObjectDataEvent)
		{
			loadModel();
			refresh();
		}
		else
		{
			super.processObjectEvent(event);
		}
	}

	/**
	 * Requests the contents of the XML format definition of this
	 * business object definition be replaced with the data from
	 * the given input stream.
	 *
	 * @param source An input stream to the new file contents
	 * @throws CoreException If an error occured while writing the
	 * new contents of the file
	 */
	public void write(InputStream source) throws CoreException
	{
		file.setContents(source, true, true, null);
		WorkflowCore.getDefault()
						 .postObjectEvent(new ReloadObjectDataEvent(
				getObjectId()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IBusinessObject#getBusinessObjectSet()
	 */
	public IBusinessObjectSet getBusinessObjectSet()
	{
		return objectSet;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.projects.core.IBusinessObject#delete()
	 */
	public void delete() throws CoreException
	{
		file.delete(false, null);
		objectSet.refresh();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.projects.core.IBusinessObject#getUnderlyingFile()
	 */
	public IFile getUnderlyingFile()
	{
		return file;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass)
	{
		if(IResource.class.isAssignableFrom(adapterClass))
			return getUnderlyingFile();
		if(BusinessObject.class.isAssignableFrom(adapterClass))
			return this;
		return super.getAdapter(adapterClass);
	}

	
	public boolean equals(Object obj)
	{
		if(obj instanceof BusinessObject)
		{
			return file.equals(((BusinessObject)obj).getUnderlyingFile());
		}
		return false;
	}
	
	public int hashCode()
	{
		return file.toString().hashCode();
	}
}
