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
package org.eclipse.vtp.modules.attacheddata.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;

public class AttachedDataRequestInformationProvider extends PrimitiveInformationProvider
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	
	public AttachedDataRequestInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		connectorRecords.add(new ConnectorRecord(element, "error.disconnect.hangup", IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
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
	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
	}

/*	public List getPropertiesPanels()
	{
		AttachedDataManager manager = (AttachedDataManager)getElement().getConfigurationManager(AttachedDataManager.CONFIGURATION_TYPE_ID);
		AttachedDataBinding binding = manager.getAttachedDataBinding("default");
		AttachedDataBindingItem adbi = binding.getAttachedDataItem("Default", "", "");
		adbi.clearEntries();
		AttachedDataItemEntry attachedDataItemEntry = new AttachedDataItemEntry();
		attachedDataItemEntry.setName("Residential");
		attachedDataItemEntry.setValue("Residential");
		adbi.addEntry(attachedDataItemEntry);
		attachedDataItemEntry = new AttachedDataItemEntry();
		attachedDataItemEntry.setName("Business");
		attachedDataItemEntry.setValue("Business");
		adbi.addEntry(attachedDataItemEntry);
		attachedDataItemEntry = new AttachedDataItemEntry();
		attachedDataItemEntry.setName("EasyMax");
		attachedDataItemEntry.setValue("EasyMax");
		adbi.addEntry(attachedDataItemEntry);
		attachedDataItemEntry = new AttachedDataItemEntry();
		attachedDataItemEntry.setName("PA");
		attachedDataItemEntry.setValue("PA");
		adbi.addEntry(attachedDataItemEntry);
		attachedDataItemEntry = new AttachedDataItemEntry();
		attachedDataItemEntry.setName("IsHoliday");
		attachedDataItemEntry.setValue("IsHoliday");
		adbi.addEntry(attachedDataItemEntry);
		attachedDataItemEntry = new AttachedDataItemEntry();
		attachedDataItemEntry.setName("IsClosure");
		attachedDataItemEntry.setValue("IsClosure");
		adbi.addEntry(attachedDataItemEntry);
		attachedDataItemEntry = new AttachedDataItemEntry();
		attachedDataItemEntry.setName("WorkingHours");
		attachedDataItemEntry.setValue("WorkingHours");
		adbi.addEntry(attachedDataItemEntry);
		binding.putAttachedDataItem("Default", "", "", adbi);
		getElement().commitConfigurationChanges(manager);
		List ret = new ArrayList();
		ret.add(new PrimitiveGeneralPropertiesPanel("General", getElement()));
		return ret;
	}
*/	
	public List<Variable> getOutgoingVariables(String exitPoint, boolean localOnly)
    {
/*		List ret = new ArrayList();
		if(exitPoint.equals("Continue"))
		{
			Variable var = new Variable("Residential", FieldType.STRING);
			ret.add(var);
			var = new Variable("Business", FieldType.STRING);
			ret.add(var);
			var = new Variable("EasyMax", FieldType.STRING);
			ret.add(var);
			var = new Variable("PA", FieldType.STRING);
			ret.add(var);
			var = new Variable("IsHoliday", FieldType.STRING);
			ret.add(var);
			var = new Variable("IsClosure", FieldType.STRING);
			ret.add(var);
			var = new Variable("WorkingHours", FieldType.STRING);
			ret.add(var);
		}
		return ret;
*/		return Collections.emptyList();
    }

	public boolean hasConnectors()
    {
	    return true;
    }
}
