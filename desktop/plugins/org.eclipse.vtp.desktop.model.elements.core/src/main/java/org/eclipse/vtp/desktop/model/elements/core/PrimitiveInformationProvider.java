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
package org.eclipse.vtp.desktop.model.elements.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.w3c.dom.Element;


public abstract class PrimitiveInformationProvider implements IAdaptable
{
	PrimitiveElement element;

	public PrimitiveInformationProvider(PrimitiveElement element)
	{
		super();
		this.element = element;
	}
	
	public PrimitiveElement getElement()
	{
		return element;
	}
	
	public abstract boolean hasConnectors();
	
	public abstract ConnectorRecord getConnectorRecord(String recordName);

	public abstract List<ConnectorRecord> getConnectorRecords();
	
	public abstract List<ConnectorRecord> getConnectorRecords(IDesignElementConnectionPoint.ConnectionPointType... types);

	public abstract void readConfiguration(Element configuration);

	public abstract void writeConfiguration(Element configuration);
	
	public abstract boolean acceptsConnector(IDesignElement origin);
	
	public List<Variable> getOutgoingVariables(String exitPoint, boolean localOnly)
	{
		return Collections.emptyList();
	}
	
	public boolean canDelete()
	{
		return true;
	}
	
	public void declareBusinessObjects()
	{
		
	}
	
	public void resolve()
	{
		
	}
	
	public boolean hasPathToStart(Map<String, IDesignElement> path)
	{
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		if(adapter.isAssignableFrom(getElement().getClass()))
			return getElement();
		else if(adapter.isAssignableFrom(getClass()))
			return this;
		return null;
	}
}
