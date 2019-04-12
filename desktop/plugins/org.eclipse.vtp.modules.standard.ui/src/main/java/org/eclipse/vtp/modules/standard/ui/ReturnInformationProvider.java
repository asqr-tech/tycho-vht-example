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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.IWorkflowExit;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.w3c.dom.NodeList;

public class ReturnInformationProvider extends PrimitiveInformationProvider implements IWorkflowExit
{
	public static final String PROP_EXIT_TYPE = "PROP_EXIT_TYPE";
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	String exitType = IWorkflowExit.NORMAL;
	List<String> exportedVars = new ArrayList<String>();
	
	public ReturnInformationProvider(PrimitiveElement element)
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
		exitType = configuration.getAttribute("exit-type").toUpperCase();
		if(!exitType.equals(IWorkflowExit.NORMAL) && !exitType.equals(IWorkflowExit.ERROR))
			exitType = IWorkflowExit.NORMAL;
		NodeList exportList = configuration.getElementsByTagName("export");
		for(int i = 0; i < exportList.getLength(); i++)
		{
			exportedVars.add(((org.w3c.dom.Element)exportList.item(i)).getAttribute("name"));
		}
	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		configuration.setAttribute("exit-type", exitType);
		for(int i = 0; i < exportedVars.size(); i++)
		{
			String name = exportedVars.get(i);
			org.w3c.dom.Element el = configuration.getOwnerDocument().createElement("export");
			configuration.appendChild(el);
			el.setAttribute("name", name);
		}
	}
	
	public List<String> getExports()
	{
		return exportedVars;
	}
	
	public void setExports(List<String> exports)
	{
		this.exportedVars = exports;
	}

//	public List getPropertiesPanels()
//	{
//		List ret = new ArrayList();
//		ret.add(new ReturnGeneralPropertiesPanel(getElement()));
//		ret.add(new ReturnVariablesPropertyPanel("Exported Variables", getElement()));
//		return ret;
//	}
	
	public String getExitType()
	{
		return exitType;
	}

	public void setExitType(String text)
	{
		String oldType = exitType;
		this.exitType = text;
		getElement().firePropertyChange(PROP_EXIT_TYPE, oldType, text);
	}

	public boolean hasConnectors()
    {
	    return false;
    }

	public String getId()
	{
		return getElement().getId();
	}

	public String getName()
	{
		return getElement().getName();
	}

	public String getType()
	{
		return exitType;
	}

	public List<Variable> getExportedVariables()
	{
		List<Variable> ret = new LinkedList<Variable>();
		List<Variable> variables = getElement().getDesign().getVariablesFor(getElement());
		for(Variable v : variables)
		{
			if(exportedVars.contains(v.getName()))
				ret.add(v);
		}
		return ret;
	}
}
