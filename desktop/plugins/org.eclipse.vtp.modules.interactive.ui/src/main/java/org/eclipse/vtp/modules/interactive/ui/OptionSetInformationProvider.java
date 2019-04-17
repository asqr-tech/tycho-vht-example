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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.ISecurableElement;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.desktop.model.interactive.core.internal.MenuChoice;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.eclipse.vtp.modules.interactive.ui.properties.MenuChoiceBindingManager;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class OptionSetInformationProvider extends PrimitiveInformationProvider implements ISecurableElement
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	String variableName = "";
	boolean secured = false;
	
	public OptionSetInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "error.input.nomatch", IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		connectorRecords.add(new ConnectorRecord(element, "error.input.noinput", IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		connectorRecords.add(new ConnectorRecord(element, "error.disconnect.hangup", IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		List<String> events = ExtendedInteractiveEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			connectorRecords.add(new ConnectorRecord(element, event, IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#acceptConnector(org.eclipse.vtp.desktop.model.core.Element)
	 */
	public boolean acceptsConnector(IDesignElement origin)
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#getConnectorRecord(java.lang.String)
	 */
	public ConnectorRecord getConnectorRecord(String recordName)
	{
		for(ConnectorRecord cr : connectorRecords)
		{
			if(cr.getName().equals(recordName))
				return cr;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#getConnectorRecords()
	 */
	public List<ConnectorRecord> getConnectorRecords()
	{
		return connectorRecords;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#getConnectorRecords(int)
	 */
	public List<ConnectorRecord> getConnectorRecords(IDesignElementConnectionPoint.ConnectionPointType... types)
	{
		List<ConnectorRecord> ret = new ArrayList<ConnectorRecord>();
		for(ConnectorRecord cr : connectorRecords)
		{
			if(cr.getType().isSet(IDesignElementConnectionPoint.ConnectionPointType.getFlagSet(types)))
				ret.add(cr);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#readConfiguration(org.w3c.dom.Element)
	 */
	public void readConfiguration(org.w3c.dom.Element configuration)
	{
		variableName = configuration.getAttribute("variable-name");
		secured = Boolean.parseBoolean(configuration.getAttribute("secured"));
		updateConfiguration(configuration);
		MenuChoiceBindingManager mcBindingManager = (MenuChoiceBindingManager) getElement().getConfigurationManager(MenuChoiceBindingManager.TYPE_ID);
		List<MenuChoice> choices = mcBindingManager.getAllChoices();
		System.err.println("# of binding choices: " + choices.size());
		for(MenuChoice mc : choices)
		{
			connectorRecords.add(new ConnectorRecord(getElement(), mc.getOptionName(),
				IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		}
		getElement().rollbackConfigurationChanges(mcBindingManager);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#writeConfiguration(org.w3c.dom.Element)
	 */
	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		configuration.setAttribute("variable-name", variableName);
		configuration.setAttribute("secured", Boolean.toString(secured));
	}

	public String getVariableName()
	{
		return variableName;
	}

	public void setVariableName(String text)
	{
		this.variableName = text;
	}

	/**
	 * @param brand
	 * @param choice
	 * @return
	 */
	public MenuChoice addChoice(String brand, MenuChoice choice)
	{
		connectorRecords.add(new ConnectorRecord(getElement(), choice.getOptionName(),
			IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		return choice;
	}

	/**
	 * @param brand
	 * @param choice
	 */
	public void removeChoice(String brand, MenuChoice choice, boolean found)
	{
		if(!found)
		{
			for(ConnectorRecord cr : connectorRecords)
			{
				if(cr.getName().equals(choice.getOptionName()))
				{
					if(cr.getDesignConnector() != null)
					{
						IDesignConnector connector = cr.getDesignConnector();
						connector.removeConnectionPoint(cr);
						if(connector.getConnectionPoints().size() == 0) //removed last connector record
						{
							getElement().getDesign().removeDesignConnector(connector);
						}
					}
					connectorRecords.remove(cr);
					break;
				}
			}
		}
	}

	/**
	 * @param choice
	 * @param oldName
	 */
	public void updateChoice(MenuChoice choice, String oldName)
	{
		for(ConnectorRecord cr : connectorRecords)
		{
			if(cr.getName().equals(oldName))
			{
				cr.setName(choice.getOptionName());
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#getOutgoingVariables(java.lang.String, java.util.Map)
	 */
	public List<Variable> getOutgoingVariables(String exitPoint, boolean localOnly)
    {
		List<Variable> ret = new ArrayList<Variable>();
		if(exitPoint.equals("Continue") && !variableName.equals(""))
		{
			Variable var =
				new Variable(variableName,
					FieldType.STRING);
			ret.add(var);
		}
		return ret;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#hasConnectors()
	 */
	public boolean hasConnectors()
    {
	    return true;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.ISecurableElement#isSecured()
	 */
	public boolean isSecured()
    {
	    return secured;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.ISecurableElement#setSecured(boolean)
	 */
	public void setSecured(boolean secured)
    {
		this.secured = secured;
    }
	
	public void updateConfiguration(Element configuration)//TODO add a reference to this in the readconfiguration method
	{
		MenuChoiceBindingManager mcBindingManager = (MenuChoiceBindingManager) getElement().getConfigurationManager(MenuChoiceBindingManager.TYPE_ID); //TODO 
		List<MenuChoice> menuChoices = new ArrayList<MenuChoice>();
		Map<String, List<MenuChoice>> brandOrders = new HashMap<String, List<MenuChoice>>();

		System.out.println("Converting the old xml"); //TODO remove this line
		List<Element> choicesElements = XMLUtilities.getElementsByTagName(configuration, "choices", true);
		if(choicesElements.size() == 0)
		{
			System.err.println("does not have old config");
			getElement().rollbackConfigurationChanges(mcBindingManager);
			return;
		}
		List<Element> choices = XMLUtilities.getElementsByTagName(choicesElements.get(0), "choice", true);
		System.err.println("# of choices: " + choices.size());
		if(choices.size() == 0)
		{
			getElement().rollbackConfigurationChanges(mcBindingManager);
			return;
		}
		for(Element choiceElement : choices)
		{
			MenuChoice mc =
				new MenuChoice(choiceElement.getAttribute("name"), choiceElement.getAttribute("script"));
			menuChoices.add(mc);
		}
		NodeList brandOrdersList = configuration.getElementsByTagName("brand-order");
		if(brandOrdersList.getLength() == 0) //backwards compatibility
		{
			List<MenuChoice> defaultList = new ArrayList<MenuChoice>();
			defaultList.addAll(menuChoices);
			brandOrders.put("Default", defaultList);
		}
		for(int i = 0; i < brandOrdersList.getLength(); i++)
		{
			List<MenuChoice> brandList = new ArrayList<MenuChoice>();
			org.w3c.dom.Element brandOrderElement = (org.w3c.dom.Element)brandOrdersList.item(i);
			String brandName = brandOrderElement.getAttribute("brand");
			NodeList entryList = brandOrderElement.getElementsByTagName("entry");
			for(int e = 0; e < entryList.getLength(); e++)
			{
				org.w3c.dom.Element entryElement = (org.w3c.dom.Element)entryList.item(e);
				for(MenuChoice mc : menuChoices)
				{
					if(mc.getOptionName().equals(entryElement.getAttribute("name")))
					{
						brandList.add(mc);
						break;
					}
				}
			}
			brandOrders.put(brandName, brandList);
		}
		if(brandOrders.get("Default") == null) //sanity pass
		{
			List<MenuChoice> defaultList = new ArrayList<MenuChoice>();
			defaultList.addAll(menuChoices);
			brandOrders.put("Default", defaultList);
		}

		//TODO loop on brands in brandOrders; keys are brand names and values are lists of MenuChoice items
		Iterator<String> i = brandOrders.keySet().iterator();
		while(i.hasNext())
		{
			String currentBrandName = i.next();
			List<MenuChoice> mcList = brandOrders.get(currentBrandName);
			
			//TODO loop on MenuChoice items in the returned list
			for(int b = 0; b < mcList.size(); b++)
			{
				MenuChoice option = mcList.get(b);
				//TODO send them mcBindingManager
				//TODO verify that this doesn't overwrite prompt/grammar/dtmf data
				mcBindingManager.addChoice(currentBrandName, option);
			}
		}
		//TODO commit the config changes in mcBindingManager
		getElement().commitConfigurationChanges(mcBindingManager);
	}
}
