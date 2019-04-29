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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignFolder;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IDesignRootFolder;
import org.eclipse.vtp.desktop.model.core.design.IDesignConstants;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignEntryPoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignExitPoint;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;

public class PortalExitInformationProvider extends PrimitiveInformationProvider implements PropertyChangeListener, IDesignEntryPoint
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	PrimitiveElement exitElement = null;
	
	public PortalExitInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		this.exitElement = this.getElement();
	}

	public boolean acceptsConnector(IDesignElement origin)
	{
		return false;
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

//	public List getPropertiesPanels()
//	{
//		List ret = new ArrayList();
//		ret.add(new PortalExitGeneralPropertiesPanel("General", getElement()));
//		return ret;
//	}
	
	public void resolve()
	{
	}

	public void propertyChange(PropertyChangeEvent evt)
    {
		if(evt.getPropertyName().equals(IDesignConstants.PROP_NAME))
		{
			getElement().setName((String)evt.getNewValue());
		}
    }

//	public List getActions(CommandListener commandListener)
//    {
//		List ret = new ArrayList();
//		List ee = getEntryElements();
//		List uiCanvases = ((BasicController)commandListener).getRenderedCanvas().getUICanvas().getUIModel().listUICanvases();
//		Map <String, Integer> map = new HashMap<String, Integer>();
//		
//		for(int b = 0; b < ee.size(); b++)
//		{
//			String currentId = ((PrimitiveElement)ee.get(b)).getId();
//			for(int c = 0; c < uiCanvases.size(); c++)
//			{
//				String currentCanvasId = ((UICanvas)uiCanvases.get(c)).getId();
//				List canvasElements = ((UICanvas)uiCanvases.get(c)).listUIElements();
//				for(int d = 0; d < canvasElements.size(); d++)
//				{
//					if(((UIElement)canvasElements.get(d)).getElement().getId().equals(currentId))
//					{
//						if(!map.containsKey(currentCanvasId))
//						{
//							map.put(currentCanvasId, 1);
//						}
//						else
//						{
//							map.put(currentCanvasId, map.get(currentCanvasId) + 1);
//						}
//					}
//				}
//			}
//		}
//
//		Iterator <Map.Entry<String, Integer>> i = map.entrySet().iterator();
//		while (i.hasNext())
//		{
//			Map.Entry <String, Integer> entry = i.next();
//			ret.add(new JumpToPortalEntryAction(exitElement, commandListener, entry.getKey() , entry.getValue()));
//		}
//		
//		return ret;
//    }

	public boolean hasConnectors()
    {
	    return true;
    }

	public String getId()
	{
		return getElement().getId();
	}
	
	public String getName()
	{
		return getElement().getName();
	}
	
	public List<IDesignExitPoint> getConnectingPoints()
	{
		List<IDesignExitPoint> exitPoints = new LinkedList<IDesignExitPoint>();
		List<IDesignExitPoint> designExitPoints = getElement().getDesign().getDocument().getDesignExitPoints();
		for(IDesignExitPoint exitPoint : designExitPoints)
		{
			if(exitPoint.getTargetId().equals(getElement().getId()))
				exitPoints.add(exitPoint);
		}
		IDesignItemContainer container = getElement().getDesign().getDocument().getParentDesignContainer();
		while(!(container instanceof IDesignRootFolder))
			container = (IDesignItemContainer)container.getParent();
		traverseDesigns(container, exitPoints);
		return exitPoints;
	}
	
	private void traverseDesigns(IDesignItemContainer container, List<IDesignExitPoint> exitPoints)
	{
		List<IDesignDocument> documents = container.getDesignDocuments();
		for(IDesignDocument document : documents)
		{
			if(!document.equals(getElement().getDesign().getDocument()))
			{
				List<IDesignExitPoint> designExitPoints = document.getDesignExitPoints();
				for(IDesignExitPoint exitPoint : designExitPoints)
				{
					if(exitPoint.getTargetId().equals(getElement().getId()))
						exitPoints.add(exitPoint);
				}
			}
		}
		List<IDesignFolder> folders = container.getDesignFolders();
		for(IDesignFolder folder : folders)
		{
			traverseDesigns(folder, exitPoints);
		}
	}

}
