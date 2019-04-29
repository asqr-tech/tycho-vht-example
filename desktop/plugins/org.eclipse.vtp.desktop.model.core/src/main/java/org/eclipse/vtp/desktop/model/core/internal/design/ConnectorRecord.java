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
package org.eclipse.vtp.desktop.model.core.internal.design;

import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;

/**
 * @author Trip
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public class ConnectorRecord implements Comparable, IDesignElementConnectionPoint
{
	private DesignElement origin;
	private String name;
	private ConnectionPointType type;
	private DesignConnector connector;

	/**
	 * @param origin
	 * @param name
	 * @param type
	 */
	public ConnectorRecord(DesignElement origin, String name, ConnectionPointType type)
	{
		super();
		this.origin = origin;
		this.name = name;
		this.type = type;
	}

	/**
	 * @return Returns the type.
	 */
	public ConnectionPointType getType()
	{
		return type;
	}
	
	/**
	 * @param type
	 */
	public void setType(ConnectionPointType type)
	{
		this.type = type;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return Returns the origin.
	 */
	public IDesignElement getSourceElement()
	{
		return origin;
	}

	/**
	 * @return
	 */
	public IDesignConnector getDesignConnector()
	{
		return this.connector;
	}

	/**
	 * @param connector The connector to set.
	 */
	public void setConnector(DesignConnector connector)
	{
		this.connector = connector;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object obj)
	{
		if (!(obj instanceof ConnectorRecord))
		{
			throw new IllegalArgumentException();
		}

		return name.compareTo(((ConnectorRecord) obj).getName());
	}
}
