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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.ISecurableElement;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.NodeList;

public class ScriptInformationProvider extends PrimitiveInformationProvider implements ISecurableElement
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	String scriptText = "";
	boolean secured = false;

	public ScriptInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		connectorRecords.add(new ConnectorRecord(element, "error.script", IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
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
		NodeList nl = configuration.getElementsByTagName("script");
		if(nl.getLength() > 0)
		{
			org.w3c.dom.Element scriptElement = (org.w3c.dom.Element)nl.item(0);
			secured = Boolean.parseBoolean(scriptElement.getAttribute("secured"));
            scriptText = XMLUtilities.getElementTextDataNoEx(scriptElement, true);
            if(scriptText == null)
            	scriptText = "";
		}
	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		org.w3c.dom.Element scriptElement = configuration.getOwnerDocument().createElement("script");
		configuration.appendChild(scriptElement);
		scriptElement.setAttribute("secured", Boolean.toString(secured));
		scriptElement.setTextContent(scriptText);
	}

//	public List getPropertiesPanels()
//	{
//		List ret = new ArrayList();
//		ret.add(new ScriptPropertiesPanel("Properties", getElement()));
//		return ret;
//	}
	
	public String getScriptText()
	{
		return scriptText;
	}

	public void setScriptText(String text)
	{
		this.scriptText = text;
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
