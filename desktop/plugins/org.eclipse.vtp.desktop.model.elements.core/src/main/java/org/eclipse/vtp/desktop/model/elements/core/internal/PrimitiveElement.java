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
package org.eclipse.vtp.desktop.model.elements.core.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.IExitBroadcastReceiver;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignElement;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveBroadcastReceiverProvider;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElementManager.PrimitiveElementTemplate;
import org.eclipse.vtp.desktop.model.elements.core.internal.views.PalletItemFilter;

public class PrimitiveElement extends DesignElement
{
	public static final String ELEMENT_TYPE = "org.eclipse.vtp.desktop.model.elements.core.basic";
	private String subTypeId;
	private String subTypeName;
	private PrimitiveInformationProvider info;
	private PalletItemFilter filter = null;

	public PrimitiveElement(String subTypeId, String name)
	{
		super(name);
		getProperties().setProperty("type", subTypeId);
		this.subTypeId = subTypeId;
		PrimitiveElementTemplate template = PrimitiveElementManager.getDefault().getElementTemplate(subTypeId);
		this.subTypeName = template.getName();
		this.info = template.getInformationProviderInstance(this);
		this.filter = template.getFilter();
	}

	public PrimitiveElement(String id, String name, Properties properties)
	{
		super(id, name, properties);
		this.subTypeId = properties.getProperty("type");
		PrimitiveElementTemplate template = PrimitiveElementManager.getDefault().getElementTemplate(subTypeId);
		this.subTypeName = template.getName();
		this.info = template.getInformationProviderInstance(this);
		this.filter = template.getFilter();
	}
	
	public String getTitle()
	{
		return super.getTitle() + " (" + getTypeName() + ")";
	}
	
	public void markDirty()
	{
		fireChange();
	}
	
	public boolean canDelete()
	{
		return info.canDelete();
	}
	
	public IDesignElementConnectionPoint getConnectorRecord(String recordName)
	{
		return info.getConnectorRecord(recordName);
	}

	public List<IDesignElementConnectionPoint> getConnectorRecords()
	{
		return new LinkedList<IDesignElementConnectionPoint>(info.getConnectorRecords());
	}
	
	public List<IDesignElementConnectionPoint> getConnectorRecords(IDesignElementConnectionPoint.ConnectionPointType... types)
	{
		return new LinkedList<IDesignElementConnectionPoint>(info.getConnectorRecords(types));
	}

	public void readCustomConfiguration(org.w3c.dom.Element configuration)
	{
		info.readConfiguration(configuration);
	}

	public void writeCustomConfiguration(org.w3c.dom.Element customElement)
	{
		info.writeConfiguration(customElement);
	}
	
	public boolean acceptsConnector(IDesignElement origin)
	{
		return info.acceptsConnector(origin);
	}

	public void showProperties()
	{
	}
	
	public String getType()
	{
		return ELEMENT_TYPE;
	}

	public String getTypeName()
	{
		return subTypeName;
	}

	public String getSubTypeId()
	{
		return subTypeId;
	}

	public PrimitiveInformationProvider getInformationProvider()
	{
		return info;
	}

	public Image getIcon()
	{
		ImageRegistry imageRegistry = org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry();
		return imageRegistry.get(subTypeId);
	}

	public List<Variable> getOutgoingVariables(String exitPoint, boolean localOnly)
    {
		return info.getOutgoingVariables(exitPoint, localOnly);
    }

	public void resolve()
	{
	    info.resolve();
	    declareBusinessObjects();
    }
	
	public void declareBusinessObjects()
	{
		info.declareBusinessObjects();
	}

	public boolean hasPathToStart(Map<String, IDesignElement> originPath)
    {
		if(originPath == null)
			originPath = new HashMap<String, IDesignElement>();
	    return super.hasPathToStart(originPath) || info.hasPathToStart(originPath);
    }
	
	public boolean hasConnectors()
    {
	    return info.hasConnectors();
    }
	
	public List<IExitBroadcastReceiver> getExitBroadcastReceivers()
	{
		if(info instanceof PrimitiveBroadcastReceiverProvider)
		{
			return ((PrimitiveBroadcastReceiverProvider) info).getExitBroadcastReceivers();
		}
		return super.getExitBroadcastReceivers();
	}

	public boolean canBeContainedBy(IDesign design)
	{
		if(filter == null)
			return true;
		return filter.canBeContainedBy(design);
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		return info.getAdapter(adapter);
	}
}
