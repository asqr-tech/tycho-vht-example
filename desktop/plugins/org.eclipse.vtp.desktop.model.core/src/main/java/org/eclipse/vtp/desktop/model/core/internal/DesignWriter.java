package org.eclipse.vtp.desktop.model.core.internal;

import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManager;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnectorMidpoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignConnector;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DesignWriter
{
	private static final IDesignFilter NO_FILTER = new IDesignFilter()
	{
		public boolean matches(IDesignComponent component)
		{
			return true;
		}
	};
	
	public void writeDesign(Element parentElement, Design design)
	{
		writeDesign(parentElement, design, NO_FILTER);
	}
	
	public void writeDesign(Element parentElement, Design design, IDesignFilter filter)
	{
		List<IDesignElement> elements = design.getDesignElements();
		List<IDesignConnector> connectors = design.getDesignConnectors();
		Document document = parentElement.getOwnerDocument();
		Element workflowElement = document.createElement("workflow");
		workflowElement.setAttribute("id", design.getDesignId());
		workflowElement.setAttribute("name", design.getName());
		parentElement.appendChild(workflowElement);
		
		//model data
		Element modelElement = workflowElement.getOwnerDocument().createElement("model");
		workflowElement.appendChild(modelElement);
		Element elementsElement = modelElement.getOwnerDocument().createElement("elements");
		modelElement.appendChild(elementsElement);
		//ui data
		Element designElement = workflowElement.getOwnerDocument().createElement("design");
		workflowElement.appendChild(designElement);
		designElement.setAttribute("orientation", Integer.toString(design.getOrientation()));
		designElement.setAttribute("paper-size", design.getPaperSize().getId());
		for(int i = 0; i < elements.size(); i++)
		{
			DesignElement el = (DesignElement)elements.get(i);
			if(!filter.matches(el))
				continue;
			writeElementData(elementsElement, el);
			writeElementUIData(designElement, el);
		}
		org.w3c.dom.Element connectorsElement = modelElement.getOwnerDocument().createElement("connectors");
		modelElement.appendChild(connectorsElement);
		for(int i = 0; i < connectors.size(); i++)
		{
			DesignConnector cn = (DesignConnector)connectors.get(i);
			if(!filter.matches(cn))
				continue;
			writeConnectorData(connectorsElement, cn);
			writeConnectorUIData(designElement, cn);
		}
	}

	private void writeElementData(Element parentElement, DesignElement de)
	{
		org.w3c.dom.Element elementElement = parentElement.getOwnerDocument().createElement("element");
		parentElement.appendChild(elementElement);
		elementElement.setAttribute("id", de.getId());
		elementElement.setAttribute("name", de.getName());
		elementElement.setAttribute("type", de.getType());
		org.w3c.dom.Element propertiesElement = parentElement.getOwnerDocument().createElement("properties");
		elementElement.appendChild(propertiesElement);
		for(Map.Entry<Object, Object> entry : de.getProperties().entrySet())
		{
			if(entry.getKey().equals("followExternalReferences"))
				continue;
			org.w3c.dom.Element propertyElement = parentElement.getOwnerDocument().createElement("property");
			propertyElement.setAttribute("name", (String)entry.getKey());
			propertyElement.setAttribute("value", (String)entry.getValue());
			propertiesElement.appendChild(propertyElement);
		}
		org.w3c.dom.Element configurationElement = parentElement.getOwnerDocument().createElement("configuration");
		elementElement.appendChild(configurationElement);
		List<ConfigurationManager> configurationManagers = de.listConfigurationManagers();
		for(ConfigurationManager manager : configurationManagers)
		{
			org.w3c.dom.Element managerElement = parentElement.getOwnerDocument().createElement("managed-config");
			configurationElement.appendChild(managerElement);
			managerElement.setAttribute("type", manager.getType());
			managerElement.setAttribute("xml-version", manager.getXMLVersion());
			manager.writeConfiguration(managerElement);
		}
		org.w3c.dom.Element customElement = configurationElement.getOwnerDocument().createElementNS("http://www.eclipse.org/vtp/namespaces/config", "custom-config");
		configurationElement.appendChild(customElement);
		de.writeCustomConfiguration(customElement);
	}
	
	private void writeConnectorData(Element parentElement, DesignConnector dc)
	{
		org.w3c.dom.Element connectorElement = parentElement.getOwnerDocument().createElement("connector");
		parentElement.appendChild(connectorElement);
		connectorElement.setAttribute("id", dc.getId());
		connectorElement.setAttribute("origin", dc.getOrigin().getId());
		connectorElement.setAttribute("destination", dc.getDestination().getId());
		for(IDesignElementConnectionPoint cr : dc.getConnectionPoints())
		{
			org.w3c.dom.Element recordElement = connectorElement.getOwnerDocument().createElement("record");
			connectorElement.appendChild(recordElement);
			recordElement.setAttribute("sourcename", cr.getName());
			recordElement.setAttribute("destinationname", "");
		}
	}
		
	private void writeElementUIData(Element parentElement, DesignElement de)
	{
		org.w3c.dom.Element elementElement = parentElement.getOwnerDocument().createElement("ui-element");
		parentElement.appendChild(elementElement);
		elementElement.setAttribute("x", Integer.toString(de.getCenterPoint().x));
		elementElement.setAttribute("y", Integer.toString(de.getCenterPoint().y));
		elementElement.setAttribute("id", de.getId());
	}
	
	private void writeConnectorUIData(Element parentElement, DesignConnector dc)
	{
		org.w3c.dom.Element connectorElement = parentElement.getOwnerDocument().createElement("ui-connector");
		parentElement.appendChild(connectorElement);
		connectorElement.setAttribute("id", dc.getId());
		connectorElement.setAttribute("label-segment", Integer.toString(dc.getConnectorLabel().getAnchorSegment()));
		connectorElement.setAttribute("label-x", Integer.toString(dc.getConnectorLabel().getOffsetPosition().x));
		connectorElement.setAttribute("label-y", Integer.toString(dc.getConnectorLabel().getOffsetPosition().y));
		List<IDesignConnectorMidpoint> midpoints = dc.getMidpoints();
		for(IDesignConnectorMidpoint mp : midpoints)
		{
			org.w3c.dom.Element midPointElement = connectorElement.getOwnerDocument().createElement("mid-point");
			connectorElement.appendChild(midPointElement);
			midPointElement.setAttribute("x", Integer.toString(mp.getPosition().x));
			midPointElement.setAttribute("y", Integer.toString(mp.getPosition().y));
		}
	}
}
