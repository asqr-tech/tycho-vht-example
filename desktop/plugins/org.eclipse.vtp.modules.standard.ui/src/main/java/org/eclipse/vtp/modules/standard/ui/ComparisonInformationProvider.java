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
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.w3c.dom.NodeList;

public class ComparisonInformationProvider extends PrimitiveInformationProvider
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	private int lType = 0;
	private String lValue = "";
	private int compType = 0;
	private int rType = 0;
	private String rValue = "";
	private boolean lsecured = false;
	private boolean rsecured = false;
	
	public ComparisonInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "True", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		connectorRecords.add(new ConnectorRecord(element, "False", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
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
		NodeList nl = configuration.getElementsByTagName("comparison");
		org.w3c.dom.Element comparisonElement = null;
		if(nl.getLength() == 0)
		{
			comparisonElement = configuration.getOwnerDocument()
			.createElement("comparison");
		}
		else
			comparisonElement = (org.w3c.dom.Element)nl.item(0);

		String compTypeStr = comparisonElement.getAttribute("type");
		if ("equal".equalsIgnoreCase(compTypeStr))
			compType = 0;
		if ("less-than".equalsIgnoreCase(compTypeStr))
			compType = 1;
		if ("less-than-or-equal".equalsIgnoreCase(compTypeStr))
			compType = 2;
		if ("greater-than".equalsIgnoreCase(compTypeStr))
			compType = 3;
		if ("greater-than-or-equal".equalsIgnoreCase(compTypeStr))
			compType = 4;
		if ("not-equal".equalsIgnoreCase(compTypeStr))
			compType = 5;
		if ("expression".equalsIgnoreCase(comparisonElement.getAttribute("left-type")))
		{
			lType = 1;
			lsecured = Boolean.parseBoolean(comparisonElement.getAttribute("left-secured"));
		}
		else
			lType = 0;
		lValue = comparisonElement.getAttribute("left-value");
		if ("expression".equalsIgnoreCase(comparisonElement.getAttribute("right-type")))
		{
			rType = 1;
			rsecured = Boolean.parseBoolean(comparisonElement.getAttribute("right-secured"));
		}
		else
			rType = 0;
		rValue = comparisonElement.getAttribute("right-value");
	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		org.w3c.dom.Element comparisonElement = configuration.getOwnerDocument()
				.createElement("comparison");
		String compTypeStr = null;
		switch (compType)
		{
		case 0:
			compTypeStr = "equal";
			break;
		case 1:
			compTypeStr = "less-than";
			break;
		case 2:
			compTypeStr = "less-than-or-equal";
			break;
		case 3:
			compTypeStr = "greater-than";
			break;
		case 4:
			compTypeStr = "greater-than-or-equal";
			break;
		case 5:
			compTypeStr = "not-equal";
			break;
		}
		comparisonElement.setAttribute("type", compTypeStr);
		comparisonElement.setAttribute("left-type", lType == 0 ? "variable"
				: "expression");
		comparisonElement.setAttribute("left-secured", Boolean.toString(lsecured));
		comparisonElement.setAttribute("left-value", lValue);
		comparisonElement.setAttribute("right-type", rType == 0 ? "variable"
				: "expression");
		comparisonElement.setAttribute("right-secured", Boolean.toString(rsecured));
		comparisonElement.setAttribute("right-value", rValue);
		configuration.appendChild(comparisonElement);
	}

//	public List getPropertiesPanels()
//	{
//		List ret = new ArrayList();
//		ret.add(new PrimitiveGeneralPropertiesPanel(getElement()));
//		ret.add(new ComparisonPropertiesPanel("Comparison", getElement()));
//		return ret;
//	}
	
	public int getCompType()
	{
		return compType;
	}

	public void setCompType(int compType)
	{
		this.compType = compType;
	}

	public int getLType()
	{
		return lType;
	}

	public void setLType(int type)
	{
		lType = type;
	}

	public String getLValue()
	{
		return lValue;
	}

	public void setLValue(String value)
	{
		lValue = value;
	}

	public int getRType()
	{
		return rType;
	}

	public void setRType(int type)
	{
		rType = type;
	}

	public String getRValue()
	{
		return rValue;
	}

	public void setRValue(String value)
	{
		rValue = value;
	}
	
	public boolean isLeftSecured()
	{
		return lsecured;
	}
	
	public void setLeftSecured(boolean secured)
	{
		this.lsecured = secured;
	}
	
	public boolean isRightSecured()
	{
		return rsecured;
	}
	
	public void setRightSecured(boolean secured)
	{
		this.rsecured = secured;
	}
	
	public boolean hasConnectors()
    {
	    return true;
    }
}
