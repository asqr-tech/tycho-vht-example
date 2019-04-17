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
import java.util.List;

import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.NodeList;

/**
 * @author Trip
 * @version 1.0
 */
public class SubdialogInformationProvider extends PrimitiveInformationProvider
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	private List<SubdialogInput> inputs = new ArrayList<SubdialogInput>();
	private List<SubdialogOutput> outputs = new ArrayList<SubdialogOutput>();
	private List<SubdialogParameter> urlParameters = new ArrayList<SubdialogParameter>();
	private String url = "";

	/**
	 * @param name
	 */
	public SubdialogInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		connectorRecords.add(new ConnectorRecord(element, "error.subdialog", IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		connectorRecords.add(new ConnectorRecord(element, "error.disconnect.hangup", IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		List<String> events = ExtendedInteractiveEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			connectorRecords.add(new ConnectorRecord(element, event, IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		}
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

	public String getURL()
	{
		return url;
	}
	
	public void setURL(String url)
	{
		this.url = url;
	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		configuration.setAttribute("url", XMLUtilities.encodeAttribute(url));
		org.w3c.dom.Element paramsElement = configuration.getOwnerDocument().createElement("params");
		configuration.appendChild(paramsElement);
		for(int i = 0; i < urlParameters.size(); i++)
		{
			SubdialogParameter sp = urlParameters.get(i);
			org.w3c.dom.Element paramElement = configuration.getOwnerDocument().createElement("param");
			paramsElement.appendChild(paramElement);
			paramElement.setAttribute("name", sp.name);
			paramElement.setAttribute("type", Integer.toString(sp.type));
			paramElement.setAttribute("value", sp.value);
		}
		org.w3c.dom.Element inputsElement = configuration.getOwnerDocument().createElement("inputs");
		configuration.appendChild(inputsElement);
		for(int i = 0; i < inputs.size(); i++)
		{
			SubdialogInput si = inputs.get(i);
			org.w3c.dom.Element inputElement = configuration.getOwnerDocument().createElement("input");
			inputsElement.appendChild(inputElement);
			inputElement.setAttribute("name", si.name);
			inputElement.setAttribute("type", Integer.toString(si.type));
			inputElement.setAttribute("value", si.value);
		}
		org.w3c.dom.Element outputsElement = configuration.getOwnerDocument().createElement("outputs");
		configuration.appendChild(outputsElement);
		for(int i = 0; i < outputs.size(); i++)
		{
			SubdialogOutput so = outputs.get(i);
			org.w3c.dom.Element outputElement = configuration.getOwnerDocument().createElement("output");
			outputsElement.appendChild(outputElement);
			outputElement.setAttribute("name", so.varName);
			outputElement.setAttribute("value", so.valueName);
		}
	}

	public void readConfiguration(org.w3c.dom.Element configuration)
	{
		url = configuration.getAttribute("url");
		NodeList nl = configuration.getElementsByTagName("inputs");
		if(nl.getLength() > 0)
		{
			org.w3c.dom.Element inputsElement = (org.w3c.dom.Element)nl.item(0);
			nl = inputsElement.getElementsByTagName("input");
			for(int i = 0; i < nl.getLength(); i++)
			{
				org.w3c.dom.Element inputElement = (org.w3c.dom.Element)nl.item(i);
				String inputName = inputElement.getAttribute("name");
				int inputType = Integer.parseInt(inputElement.getAttribute("type"));
				String inputValue = inputElement.getAttribute("value");
				inputs.add(new SubdialogInput(inputName, inputType, inputValue));
			}
		}
		nl = configuration.getElementsByTagName("outputs");
		if(nl.getLength() > 0)
		{
			org.w3c.dom.Element outputsElement = (org.w3c.dom.Element)nl.item(0);
			nl = outputsElement.getElementsByTagName("output");
			for(int i = 0; i < nl.getLength(); i++)
			{
				org.w3c.dom.Element outputElement = (org.w3c.dom.Element)nl.item(i);
				String varName = outputElement.getAttribute("name");
				String valueName = outputElement.getAttribute("value");
				outputs.add(new SubdialogOutput(varName, valueName));
			}
		}
		nl = configuration.getElementsByTagName("params");
		if(nl.getLength() > 0)
		{
			org.w3c.dom.Element paramsElement = (org.w3c.dom.Element)nl.item(0);
			nl = paramsElement.getElementsByTagName("param");
			for(int i = 0; i < nl.getLength(); i++)
			{
				org.w3c.dom.Element paramElement = (org.w3c.dom.Element)nl.item(i);
				String varName = paramElement.getAttribute("name");
				int inputType = Integer.parseInt(paramElement.getAttribute("type"));
				String valueName = paramElement.getAttribute("value");
				urlParameters.add(new SubdialogParameter(varName, inputType, valueName));
			}
		}
	}
	
	public List<SubdialogInput> getInputs()
	{
		return inputs;
	}
	
	public void setInputs(List<SubdialogInput> inputs)
	{
		this.inputs = inputs;
	}
	
	public List<SubdialogOutput> getOutputs()
	{
		return outputs;
	}
	
	public void setOutputs(List<SubdialogOutput> outputs)
	{
		this.outputs = outputs;
	}
	
	public List<SubdialogParameter> getURLParameters()
	{
		return urlParameters;
	}
	
	public void setURLParameters(List<SubdialogParameter> urlParameters)
	{
		this.urlParameters = urlParameters;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.EditorComponent#getPropertiesPanel()
	 */
//	public List getPropertiesPanels()
//	{
//		List ret = new ArrayList();
//		ret.add(new SubdialogPropertiesPanel("General", getElement()));
//		return ret;
//	}
	
	public class SubdialogParameter
	{
		public String name = "";
		public int type = 0;
		public String value = "";
		
		public SubdialogParameter(String name, int type, String value)
		{
			super();
			this.name = name;
			this.type = type;
			this.value = value;
		}
	}

	public class SubdialogInput
	{
		public String name = "";
		public int type = 0;
		public String value = "";
		
		public SubdialogInput(String name, int type, String value)
		{
			super();
			this.name = name;
			this.type = type;
			this.value = value;
		}
	}
	
	public class SubdialogOutput
	{
		public String varName = "";
		public String valueName = "";
		
		public SubdialogOutput(String varName, String valueName)
		{
			super();
			this.varName = varName;
			this.valueName = valueName;
		}
	}

	public boolean acceptsConnector(IDesignElement origin)
    {
	    return true;
    }

	public boolean hasConnectors()
    {
	    return true;
    }
}
