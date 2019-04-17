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

import org.eclipse.swt.graphics.Point;

public interface IDesignConnector extends IDesignComponent
{
	public void addConnectionPoint(IDesignElementConnectionPoint connectionPoint);
	
	public void removeConnectionPoint(IDesignElementConnectionPoint connectionPoint);
	
	public List<IDesignElementConnectionPoint> getConnectionPoints();
	
	public void clearConnectionPoints();
	
	public IDesignElement getOrigin();
	
	public IDesignElement getDestination();

	public List<IDesignConnectorMidpoint> getMidpoints();
	
	public IDesignConnectorMidpoint addMidpoint(int x, int y);
	
	public IDesignConnectorMidpoint insertMidpoint(int index, int x, int y);
	
	public void removeMidpoint(int index);
	
	public void removeMidpoint(IDesignConnectorMidpoint midpoint);
	
	public IDesignConnectorLabel getConnectorLabel();
	
	public Point[] getPoints();

	//	public void adjustConnectorPosition(int xoff, int yoff);
}
