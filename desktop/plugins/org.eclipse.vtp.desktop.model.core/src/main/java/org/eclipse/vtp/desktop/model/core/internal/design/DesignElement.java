/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.core.internal.design;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.swt.graphics.Point;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManager;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManagerRegistry;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignConstants;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.IExitBroadcastReceiver;
import org.eclipse.vtp.desktop.model.core.design.Variable;

public abstract class DesignElement extends DesignComponent implements IDesignElement
{
	private String name;
	private Properties properties = new Properties();
	private Point centerPoint = null;
	private boolean hasErrors = false;
	private boolean hasWarnings = false;
	private boolean hasTodo = false;
	private Map<String, ConfigurationManager> configurationManagersByType;
	private List<ConfigurationManager> configurationManagers;
	private List<DesignConnector> incomingConnectors = new ArrayList<DesignConnector>();
	private Map<String, ConfigRefCount> configRefs = new HashMap<String, ConfigRefCount>();

	/**
	 * @param name
	 */
	public DesignElement(String name)
	{
		super();
		this.name = name;
		configurationManagersByType = new HashMap<String, ConfigurationManager>();
		configurationManagers = new ArrayList<ConfigurationManager>();
	}

	/**
	 * @param id
	 * @param name
	 * @param properties
	 */
	public DesignElement(String id, String name, Properties properties)
	{
		super(id);
		this.name = name;
		this.properties = properties;
		configurationManagersByType = new HashMap<String, ConfigurationManager>();
		configurationManagers = new ArrayList<ConfigurationManager>();
	}
	
	public List<IExitBroadcastReceiver> getExitBroadcastReceivers()
	{
		return Collections.emptyList();
	}
	
	/**
	 * @return
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * @param name
	 */
	public void setName(String name)
	{
		String oldName = this.name;
		this.name = name;
		this.firePropertyChange(IDesignConstants.PROP_NAME, oldName, name);
		this.fireChange();
	}
	
	public String getTitle()
	{
		return getName() + " Properties";
	}
	
	public boolean canDelete()
	{
		return true;
	}
	
	/**
	 * @return
	 */
	public Properties getProperties()
	{
		return properties;
	}
	
	/**
	 * @param manager
	 */
	public void addConfigurationManager(ConfigurationManager manager)
	{
		configurationManagersByType.put(manager.getType(), manager);
		configurationManagers.add(manager);
	}
	
	/**
	 * @param type
	 * @return
	 */
	public ConfigurationManager getConfigurationManager(String type)
	{
		System.err.println("getting manager:" + type);
		ConfigurationManager cm = configurationManagersByType.get(type);
		if(cm == null)
		{
			System.err.println("creating new manager instance");
			cm = ConfigurationManagerRegistry.getInstance().getConfigurationManager(getDesign(), type);
			if(cm != null)
			{
				configurationManagers.add(cm);
				configurationManagersByType.put(type, cm);
			}
			else
				return null;
		}
		ConfigRefCount ref = configRefs.get(type);
		if(ref == null) //no current transactions in progress
		{
			ref = new ConfigRefCount();
			ref.configType = type;
			ref.copy = (ConfigurationManager)cm.clone();
			configRefs.put(type, ref);
		}
		ref.refs++;
		System.err.println("manager " + ref.copy + " ref count: " + ref.refs);
		return ref.copy;
	}
	
	/**
	 * @param manager
	 */
	public void commitConfigurationChanges(ConfigurationManager manager)
	{
		System.out.println("committing manager: " + manager);
		ConfigRefCount ref = configRefs.get(manager.getType());
		if(ref == null)
		{
			System.err.println("Big problems!  no ref counter for " + manager);
			return;
		}
		ref.refs--;
		System.err.println("Reference count: " + ref.refs);
		if(ref.refs == 0) //final commit
		{
			configRefs.remove(manager.getType());
			ConfigurationManager cm = configurationManagersByType.get(manager.getType());
			configurationManagers.remove(cm);
			configurationManagers.add(manager);
			configurationManagersByType.put(manager.getType(), manager);
		}
		this.fireChange();
	}
	
	/**
	 * @param manager
	 */
	public void rollbackConfigurationChanges(ConfigurationManager manager)
	{
		System.out.println("rolling back manager: " + manager);
		configRefs.remove(manager.getType());
	}
	
	/**
	 * @return
	 */
	public List<ConfigurationManager> listConfigurationManagers()
	{
		return Collections.unmodifiableList(configurationManagers);
	}
	
	/**
	 * @param connector
	 */
	public void addIncomingConnector(DesignConnector connector)
	{
		incomingConnectors.remove(connector);
		incomingConnectors.add(connector);
	}
	
	/**
	 * @param connector
	 */
	public void removeIncomingConnector(DesignConnector connector)
	{
		incomingConnectors.remove(connector);
	}
	
	/**
	 * @return
	 */
	public List<IDesignConnector> getIncomingConnectors()
	{
		List<IDesignConnector> ret = new LinkedList<IDesignConnector>();
		ret.addAll(incomingConnectors);
		return ret;
	}
	
	/**
	 * @param exitPoint
	 * @return
	 */
	public List<Variable> getOutgoingVariables(String exitPoint)
	{
		return getOutgoingVariables(exitPoint, false);
	}
	
	public List<Variable> getOutgoingVariables(String exitPoint, boolean localOnly)
	{
		return new LinkedList<Variable>();
	}

	/**
	 * @param originPath
	 * @return
	 */
	public boolean hasPathToStart(Map<String, IDesignElement> originPath)
	{
		if(originPath == null)
		{
			originPath = new HashMap<String, IDesignElement>();
		}

		originPath.put(getId(), this);

		boolean ret = false;

		for(int i = 0; i < incomingConnectors.size(); i++)
		{
			DesignConnector connector = incomingConnectors.get(i);

			if(originPath.get(connector.getOrigin().getId()) != null)
			{
				continue;
			}
			boolean has = connector.getOrigin().hasPathToStart(originPath);
			ret |= has;

			if(ret)
			{
				return ret;
			}
		}

		return ret;
	}
	
	public void resolve()
	{
		
	}
	
	/**
	 * @return
	 */
	public boolean hasErrors()
	{
		return hasErrors;
	}
	
	/**
	 * @return
	 */
	public boolean hasWarnings()
	{
		return hasWarnings;
	}
	
	/**
	 * @return
	 */
	public boolean hasTodo()
	{
		return hasTodo;
	}
	
	public void validateStatus()
	{
		hasErrors = !hasPathToStart(null);
		hasWarnings = false;
		hasTodo = false;
		for(IDesignElementConnectionPoint cr : getConnectorRecords(IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT))
        {
	        if(cr.getDesignConnector() == null)
	        {
	        	hasWarnings = true;
	        	break;
	        }
        }
		for(IDesignElementConnectionPoint cr : getConnectorRecords(IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT))
        {
	        if(cr.getDesignConnector() == null)
	        {
	        	hasTodo = true;
	        	break;
	        }
        }
	}
	
	/**
	 * @param configuration
	 */
	public abstract void readCustomConfiguration(org.w3c.dom.Element configuration);
	
	/**
	 * @param customElement
	 */
	public abstract void writeCustomConfiguration(org.w3c.dom.Element customElement);
	
	public void declareBusinessObjects()
	{
	}

	public Point getCenterPoint()
	{
		return centerPoint;
	}
	
	public void setCenterPoint(int x, int y)
	{
		centerPoint = new Point(x, y);
		fireChange();
	}

	public void setCenterPoint(Point centerPoint)
	{
		this.centerPoint = centerPoint;
		fireChange();
	}
	
	public boolean canBeContainedBy(IDesign design)
	{
		return true;
	}

	private class ConfigRefCount
	{
		@SuppressWarnings("unused")
		private String configType;
		private int refs = 0;
		private ConfigurationManager copy;
	}
}
