package org.eclipse.vtp.desktop.model.core.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.swt.graphics.Point;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationException;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManager;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManagerRegistry;
import org.eclipse.vtp.desktop.model.core.design.IDesignConstants;
import org.eclipse.vtp.desktop.model.core.design.PaperSize;
import org.eclipse.vtp.desktop.model.core.design.PaperSizeManager;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignConnector;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignElement;
import org.eclipse.vtp.desktop.model.core.internal.design.ElementManager;
import org.eclipse.vtp.desktop.model.core.internal.design.ElementResolutionVisitor;
import org.w3c.dom.NodeList;

public class DesignParser
{

	public DesignParser()
	{
	}

	public Design parseDesign(IDesignDocument designDocument, org.w3c.dom.Element designElement, ElementResolutionVisitor resolutionVisitor, boolean followExternalReferences)
	{
		return parseDesign(designDocument, null, designElement, resolutionVisitor, followExternalReferences);
	}
	
	public synchronized Design parseDesign(IDesignDocument designDocument, Design baseDesign, org.w3c.dom.Element designElement, ElementResolutionVisitor resolutionVisitor, boolean followExternalReferences)
	{
		List<DesignElement> elements = new LinkedList<DesignElement>();
		Map<String, DesignElement> elementsById = new HashMap<String, DesignElement>();
		List<DesignConnector> connectors = new LinkedList<DesignConnector>();
		Map<String, DesignConnector> connectorsById = new HashMap<String, DesignConnector>();
		try
		{
			String flowId = designElement.getAttribute("id");
			String flowName = designElement.getAttribute("name");
			NodeList modelList = designElement.getElementsByTagName("model");
			org.w3c.dom.Element modelElement = (org.w3c.dom.Element)modelList.item(0);
			Design design = new Design(flowId, flowName);
			design.setDocument(designDocument);
			NodeList elementsElementList = modelElement.getElementsByTagName("elements");
			if(elementsElementList.getLength() > 0)
			{
				org.w3c.dom.Element elementsElement = (org.w3c.dom.Element)elementsElementList.item(0);
				NodeList elementList = elementsElement.getElementsByTagName("element");
				for(int i = 0; i < elementList.getLength(); i++)
				{
					org.w3c.dom.Element elementElement = (org.w3c.dom.Element)elementList.item(i);
					String elementId = elementElement.getAttribute("id");
					String elementName = elementElement.getAttribute("name");
					String elementType = elementElement.getAttribute("type");
					Properties properties = new Properties();
					properties.setProperty("followExternalReferences", Boolean.toString(followExternalReferences));
					NodeList propertiesList = elementElement.getElementsByTagName("properties");
					if(propertiesList.getLength() > 0)
					{
						org.w3c.dom.Element propertiesElement = (org.w3c.dom.Element)propertiesList.item(0);
						NodeList propertyList = propertiesElement.getElementsByTagName("property");
						for(int p = 0; p < propertyList.getLength(); p++)
						{
							org.w3c.dom.Element propertyElement = (org.w3c.dom.Element)propertyList.item(p);
							properties.setProperty(propertyElement.getAttribute("name"), propertyElement.getAttribute("value"));
						}
					}
					DesignElement element = null; 
					element = ElementManager.getInstance().loadElement(elementType, elementId, elementName, properties);
					if(element != null)
					{
						design.addDesignElement(element, false);
						elements.add(element);
						elementsById.put(elementId, element);
						NodeList configurationList = elementElement.getElementsByTagName("configuration");
						if(configurationList.getLength() > 0)
						{
							org.w3c.dom.Element configurationElement = (org.w3c.dom.Element)configurationList.item(0);
							NodeList managedConfigSectionList = configurationElement.getElementsByTagName("managed-config");
							for(int c = 0; c < managedConfigSectionList.getLength(); c++)
							{
								org.w3c.dom.Element configSectionElement = (org.w3c.dom.Element)managedConfigSectionList.item(c);
								ConfigurationManager manager = ConfigurationManagerRegistry.getInstance().getConfigurationManager(design, configSectionElement.getAttribute("type"));
								if(manager != null)
								{
									try
				                    {
					                    manager.readConfiguration(configSectionElement);
				                    }
				                    catch(ConfigurationException e)
				                    {
					                    e.printStackTrace();
				                    }
									element.addConfigurationManager(manager);
								}
							}
							NodeList customElementList = configurationElement.getElementsByTagNameNS("http://www.eclipse.org/vtp/namespaces/config", "custom-config");
							if(customElementList.getLength() > 0)
							{
								element.readCustomConfiguration((org.w3c.dom.Element)customElementList.item(0));
							}
						}
					}
				}
			}
			for(DesignElement element : elements)
	        {
				element.resolve();
		        if(resolutionVisitor != null)
		        	resolutionVisitor.resolveElement(element);
	        }
			NodeList connectorElementList = modelElement.getElementsByTagName("connectors");
			if(connectorElementList.getLength() > 0)
			{
				NodeList connectorList = ((org.w3c.dom.Element)connectorElementList.item(0)).getElementsByTagName("connector");
				for(int i = 0; i < connectorList.getLength(); i++)
				{
					org.w3c.dom.Element connectorElement = (org.w3c.dom.Element)connectorList.item(i);
					String id = connectorElement.getAttribute("id");
					String originId = connectorElement.getAttribute("origin");
					String destinationId = connectorElement.getAttribute("destination");
					DesignElement origin = elementsById.get(originId);
					if(origin == null && baseDesign != null)
						origin = (DesignElement)baseDesign.getDesignElement(originId);
					DesignElement destination = elementsById.get(destinationId);
					if(destination == null && baseDesign != null)
						destination = (DesignElement)baseDesign.getDesignElement(destinationId);
					if(origin == null || destination == null)
						throw new RuntimeException("orphaned connector: id=" + id + " origin=" + originId + " destination=" + destinationId);
					DesignConnector connector = new DesignConnector(id, origin, destination);
					NodeList recordList = connectorElement.getElementsByTagName("record");
					for(int r = 0; r < recordList.getLength(); r++)
					{
						org.w3c.dom.Element recordElement = (org.w3c.dom.Element)recordList.item(r);
						String sourceName = recordElement.getAttribute("sourcename");
//						String destinationName = recordElement.getAttribute("destinationname");
						connector.addConnectionPoint(origin.getConnectorRecord(sourceName));
					}
					design.addDesignConnector(connector);
					connectors.add(connector);
					connectorsById.put(id, connector);
				}
			}
			//design UI
			NodeList canvasList = designElement.getElementsByTagName("design");
			org.w3c.dom.Element canvasElement = (org.w3c.dom.Element)canvasList.item(0);
			int orientation = IDesignConstants.LANDSCAPE;
			if(!canvasElement.getAttribute("orientation").equals(""))
				orientation = Integer.parseInt(canvasElement.getAttribute("orientation"));
			String paperSizeId = canvasElement.getAttribute("paper-size");
			PaperSize paperSize = null;
			if(paperSizeId != null && !paperSizeId.equals(""))
				paperSize = PaperSizeManager.getDefault().getPaperSize(paperSizeId);
			if(paperSize == null)
				paperSize = PaperSizeManager.getDefault().getPaperSize("org.eclipse.vtp.desktop.model.core.Letter");
			design.setPaperSize(paperSize);
			design.setOrientation(orientation);
			NodeList uiElementList = canvasElement.getElementsByTagName("ui-element");
			for(int ue = 0; ue < uiElementList.getLength(); ue++)
			{
				org.w3c.dom.Element uiElementElement = (org.w3c.dom.Element)uiElementList.item(ue);
				String elementId = uiElementElement.getAttribute("id");
				String xs = uiElementElement.getAttribute("x");
				String ys = uiElementElement.getAttribute("y");
				Point centerPoint = new Point(Integer.parseInt(xs), Integer.parseInt(ys));
				DesignElement element = elementsById.get(elementId);
				if(element != null)
					element.setCenterPoint(centerPoint);
			}
			NodeList uiConnectorList = canvasElement.getElementsByTagName("ui-connector");
			for(int ue = 0; ue < uiConnectorList.getLength(); ue++)
			{
				org.w3c.dom.Element uiConnectorElement = (org.w3c.dom.Element)uiConnectorList.item(ue);
				String connectorId = uiConnectorElement.getAttribute("id");
				int segment = 0;
				int lx = 0;
				int ly = 0;
				try
				{
					segment = Integer.parseInt(uiConnectorElement.getAttribute("label-segment"));
					lx = Integer.parseInt(uiConnectorElement.getAttribute("label-x"));
					ly = Integer.parseInt(uiConnectorElement.getAttribute("label-y"));
				}
				catch(Exception ex)
				{
				}
				DesignConnector connector = connectorsById.get(connectorId);
				if(connector != null)
				{
					connector.getConnectorLabel().setPosition(segment, lx, ly);
					NodeList midPointList = uiConnectorElement.getElementsByTagName("mid-point");
					for(int i = 0; i < midPointList.getLength(); i++)
					{
						org.w3c.dom.Element midPointElement = (org.w3c.dom.Element)midPointList.item(i);
						int x = Integer.parseInt(midPointElement.getAttribute("x"));
						int y = Integer.parseInt(midPointElement.getAttribute("y"));
						connector.addMidpoint(x, y);
					}
				}
			}
			return design;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
