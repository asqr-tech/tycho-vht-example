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
package org.eclipse.vtp.modules.standard.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.design.IDesignConstants;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignEntryPoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignExitPoint;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;


public class PortalEntryInformationProvider extends PrimitiveInformationProvider implements PropertyChangeListener, IDesignExitPoint
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	String exitId = "";
	String exitName = "";
	IDesignElement exitElement = null;
	
	public PortalEntryInformationProvider(PrimitiveElement element)
	{
		super(element);
	}

	public boolean acceptsConnector(IDesignElement origin)
	{
		return true;
	}

	public ConnectorRecord getConnectorRecord(String recordName)
	{
		for(int i = 0; i < connectorRecords.size(); i++)
		{
			ConnectorRecord cr = connectorRecords.get(i);
			if(cr.getName().equals(recordName))
				return cr;
		}
		return null;
	}

	public List<ConnectorRecord> getConnectorRecords()
	{
		return connectorRecords;
	}

	public List<ConnectorRecord> getConnectorRecords(IDesignElementConnectionPoint.ConnectionPointType... types)
	{
		List<ConnectorRecord> ret = new ArrayList<ConnectorRecord>();
		for(int i = 0; i < connectorRecords.size(); i++)
		{
			ConnectorRecord cr = connectorRecords.get(i);
			if(cr.getType().isSet(IDesignElementConnectionPoint.ConnectionPointType.getFlagSet(types)))
				ret.add(cr);
		}
		return ret;
	}

	public void readConfiguration(org.w3c.dom.Element configuration)
	{
		exitId = configuration.getAttribute("exit-id");
		exitName = configuration.getAttribute("exit-name");
	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		configuration.setAttribute("exit-id", exitId);
		configuration.setAttribute("exit-name", exitName);
	}

//	public List getPropertiesPanels()
//	{
//		List ret = new ArrayList();
//		ret.add(new PortalEntryPropertiesPanel("General", getElement()));
//		return ret;
//	}
	
	public String getExitId()
	{
		return exitId;
	}

	public void setExitId(String text)
	{
		this.exitId = text;
		resolve();
	}
	
	public void resolve()
	{
		if(exitElement != null)
		{
			exitElement.removePropertyListener(this);
		}
		exitElement = null;
		List<IDesignElement> elements = getElement().getDesign().getDesignElements();
		for(IDesignElement element : elements)
        {
    		if(element.getId().equals(exitId))
    		{
    			this.exitElement = element;
    		}
        }
		if(exitElement == null)
		{
			for(IDesignElement element : elements)
	        {
	    		if(element.getAdapter(IDesignEntryPoint.class) != null && element.getName().equals(exitName))
	    		{
	    			this.exitElement = element;
	    			this.exitId = element.getId();
	    		}
	        }
		}
		if(exitElement != null)
		{
			exitElement.addPropertyListener(this);
		}
	}

	public void propertyChange(PropertyChangeEvent evt)
    {
		if(evt.getPropertyName().equals(IDesignConstants.PROP_NAME))
		{
			getElement().setName((String)evt.getNewValue());
			exitName = (String)evt.getNewValue();
		}
		if(evt.getPropertyName().equals(IDesignConstants.PROP_ID))
		{
			this.exitId = exitElement.getId();
		}
    }

	public boolean hasConnectors()
    {
	    return false;
    }

	public String getId()
	{
		return getElement().getId();
	}
	
	public String getTargetId()
	{
		return exitId;
	}
	
	public String getTargetName()
	{
		return exitName;
	}
	
	public List<Variable> getExportedDesignVariables()
	{
		return getElement().getDesign().getVariablesFor(getElement(), true);
	}
	
}
