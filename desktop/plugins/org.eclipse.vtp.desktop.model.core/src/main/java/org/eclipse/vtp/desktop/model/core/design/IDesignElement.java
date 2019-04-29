/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.core.design;

import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManager;

public interface IDesignElement extends IDesignComponent, IAdaptable
{
	public String getName();
	
	public void setName(String name);
	
	public String getType();
	
	public String getTypeName();
	
	public String getTitle();
	
	public Image getIcon();
	
	public boolean canDelete();

	public Properties getProperties();
	
	public boolean acceptsConnector(IDesignElement sourceElement);
	
	public List<IDesignConnector> getIncomingConnectors();
	
	public boolean hasConnectors();
	
	public IDesignElementConnectionPoint getConnectorRecord(String recordName);
	
	public List<IDesignElementConnectionPoint> getConnectorRecords();
	
	public List<IDesignElementConnectionPoint> getConnectorRecords(IDesignElementConnectionPoint.ConnectionPointType... types);
	
	public List<IExitBroadcastReceiver> getExitBroadcastReceivers();
	
	public ConfigurationManager getConfigurationManager(String type);

	public void commitConfigurationChanges(ConfigurationManager manager);
	
	public void rollbackConfigurationChanges(ConfigurationManager manager);

	public Point getCenterPoint();
	
	public void setCenterPoint(Point centerPoint);
	
	public void setCenterPoint(int x, int y);

	public List<Variable> getOutgoingVariables(String exitPoint);
	
	public List<Variable> getOutgoingVariables(String exitPoint, boolean localOnly);

	/**
	 * @return
	 */
	public boolean hasErrors();
	
	/**
	 * @return
	 */
	public boolean hasWarnings();
	
	/**
	 * @return
	 */
	public boolean hasTodo();
	
	public boolean canBeContainedBy(IDesign design);
}
