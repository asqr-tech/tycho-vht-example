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
package org.eclipse.vtp.modules.interactive.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.ISecurableElement;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;

public class PlayPromptInformationProvider extends PrimitiveInformationProvider implements ISecurableElement
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	boolean secured = false;
	
	public PlayPromptInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		connectorRecords.add(new ConnectorRecord(element, "error.disconnect.hangup", IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		List<String> events = ExtendedInteractiveEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			connectorRecords.add(new ConnectorRecord(element, event, IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		}
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
		return Collections.unmodifiableList(connectorRecords);
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
		secured = Boolean.parseBoolean(configuration.getAttribute("secured"));
	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		configuration.setAttribute("secured", Boolean.toString(secured));
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
