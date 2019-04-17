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

import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectField;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;

/**
 * This is a concrete implementation of <code>IBusinessObjectField</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class BusinessObjectField extends WorkflowResource
	implements IBusinessObjectField
{
	/**
	 * The business object defintion this field is a member of.
	 */
	private BusinessObject businessObject;

	/**
	 * The name of this business object field.
	 */
	private String name;

	/**
	 * The data type definition of this business object field.
	 */
	private FieldType dataType;

	/**
	 * The initial value this field will have when a new instance
	 * of the containing business object type is created.
	 */
	private String initialValue;
	
	private boolean secured = false;

	/**
	 * Creates a new <code>BusinessObjectField</code> with the given name,
	 * data type, initial value.  The parent business object is also provided
	 * by the businessObject parameter.
	 *
	 * @param businessObject The parent business object
	 * @param name The name of this business object field
	 * @param dataType The data type of this business object field
	 * @param initialValue The initial value for this business object field
	 */
	public BusinessObjectField(BusinessObject businessObject, String name,
		FieldType dataType, String initialValue, boolean secured)
	{
		super();
		this.businessObject = businessObject;
		this.name = name;
		this.dataType = dataType;
		this.initialValue = initialValue;
		this.secured = secured;
		activateEvents();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.VoiceResource#getObjectId()
	 */
	protected String getObjectId()
	{
		return businessObject.getObjectId() + "#" + name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IBusinessObjectField#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IBusinessObjectField#getDataType()
	 */
	public FieldType getDataType()
	{
		return dataType;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IBusinessObjectField#getInitialValue()
	 */
	public String getInitialValue()
	{
		return initialValue;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getParent()
	 */
	public IWorkflowResource getParent()
	{
		return businessObject;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.projects.core.IBusinessObjectField#isSecured()
	 */
	public boolean isSecured()
    {
	    return secured;
    }
}
