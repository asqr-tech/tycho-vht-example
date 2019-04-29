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
package com.openmethods.openvxml.idriver.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.ISecurableElement;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.w3c.dom.NodeList;

public class IDriverInitInformationProvider extends PrimitiveInformationProvider implements ISecurableElement
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	private String callIdVariable = "";
	private String connIdVariable = "";
	private String portVariable = "";
	boolean secured = false;

	public IDriverInitInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		connectorRecords.add(new ConnectorRecord(element, "error.nodriver", IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
	}

	public String getCallIdVariable()
	{
		return callIdVariable;
	}

	public void setCallIdVariable(String callIdVariable)
	{
		this.callIdVariable = callIdVariable;
	}

	public String getConnIdVariable()
	{
		return connIdVariable;
	}

	public void setConnIdVariable(String connIdVariable)
	{
		this.connIdVariable = connIdVariable;
	}

	public String getPortVariable()
	{
		return portVariable;
	}

	public void setPortVariable(String portVariable)
	{
		this.portVariable = portVariable;
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
		NodeList nl = configuration.getElementsByTagName("idriver-init");
		if(nl.getLength() > 0)
		{
			org.w3c.dom.Element scriptElement = (org.w3c.dom.Element)nl.item(0);
			secured = Boolean.parseBoolean(scriptElement.getAttribute("secured"));
			callIdVariable = scriptElement.getAttribute("call-id-variable");
			connIdVariable = scriptElement.getAttribute("conn-id-variable");
			portVariable = scriptElement.getAttribute("port-variable");
		}
	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		org.w3c.dom.Element scriptElement = configuration.getOwnerDocument().createElement("idriver-init");
		configuration.appendChild(scriptElement);
		scriptElement.setAttribute("secured", Boolean.toString(secured));
		scriptElement.setAttribute("call-id-variable", callIdVariable);
		scriptElement.setAttribute("conn-id-variable", connIdVariable);
		scriptElement.setAttribute("port-variable", portVariable);
	}

	public boolean hasConnectors()
    {
	    return true;
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
